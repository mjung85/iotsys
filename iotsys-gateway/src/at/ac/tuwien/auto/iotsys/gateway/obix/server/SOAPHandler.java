/*******************************************************************************
 * Copyright (c) 2013
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/

package at.ac.tuwien.auto.iotsys.gateway.obix.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import obix.io.ObixEncoder;

public class SOAPHandler {
	private ObixServer obixServer;

	private final String SOAP_RESPONSE_START = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" 					
			+ "<soapenv:Header/>" + "<soapenv:Body>"
			+ "<obixWS:response xmlns=\"http://obix.org/ns/schema/1.1\" xmlns:obixWS=\"http://obix.org/ns/wsdl/1.1\">";

	private final String SOAP_RESPONSE_END = "</obixWS:response></soapenv:Body>"
			+ "</soapenv:Envelope>";

	public SOAPHandler(ObixServer obixServer) {
		this.obixServer = obixServer;
		try {
			schemaFileContent = readFile("res/obix.xsd");
			wsdlFileContent = readFile("res/obix.wsdl");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private String schemaFileContent = "";
	private String wsdlFileContent = "";
	
	public String getSchemaFileContent(){
		return schemaFileContent;	
	}
	
	public String getWSDLFileContent(){
		return wsdlFileContent;
	}

	public String process(String soapPayload, String soapAction) {
		OPERATION op = null;

		// String soapPayload = parms.getProperty("data");

		String nameSpacePrefix = extractNSPrefix("http://obix.org/ns/wsdl/1.1",
				soapPayload);

		// String soapAction = header.getProperty("soapaction");

		if (soapAction != null) {
			if (soapAction.contains("http://obix.org/ns/wsdl/1.1/read")) {
				op = OPERATION.READ;
			}
			if (soapAction.contains("http://obix.org/ns/wsdl/1.1/invoke")) {
				op = OPERATION.INVOKE;
			}
			if (soapAction.contains("http://obix.org/ns/wsdl/1.1/write")) {
				op = OPERATION.WRITE;
			}
		} else {
			// no SOAP action found in header -> investigate payload

			if (soapPayload.indexOf(nameSpacePrefix + "read") >= 0) {
				op = OPERATION.READ;
			}

			if (soapPayload.indexOf(nameSpacePrefix + "invoke") >= 0) {
				op = OPERATION.INVOKE;
			}

			if (soapPayload.indexOf(nameSpacePrefix + "write") >= 0) {
				op = OPERATION.WRITE;
			}
		}


		String href = extractHref(soapPayload);

		URI hrefURI = null;
		
		try {
			hrefURI = new URI(href);
		} catch (URISyntaxException e) {
			// TODO send back as SOAP fault?
			e.printStackTrace();
			return e.getMessage();
		}

		if (op == OPERATION.READ) {
			// read on object, find href attribute

			StringBuffer obixObj = new StringBuffer(ObixEncoder.toString(obixServer.readObj(hrefURI,
					"guest")));
			return SOAP_RESPONSE_START + obixObj.toString() + SOAP_RESPONSE_END;

		} else if (op == OPERATION.INVOKE) {
			String obj = extractObject(soapPayload, nameSpacePrefix, false);
			return SOAP_RESPONSE_START + ObixEncoder.toString(obixServer.invokeOp(hrefURI, obj))
					+ SOAP_RESPONSE_END;
		} else if (op == OPERATION.WRITE) {
			String obj = extractObject(soapPayload, nameSpacePrefix, true);
			return SOAP_RESPONSE_START + ObixEncoder.toString(obixServer.writeObj(hrefURI, obj))
					+ SOAP_RESPONSE_END;
		}

		return "Not implemented yet.";

	}

	private String extractNSPrefix(String namespace, String soapPayload) {
		String nameSpacePrefix = "";
		int nsStart = soapPayload.indexOf(namespace);

		// go back to colon and extract ns prefix
		if (nsStart >= 0) {
			for (int i = nsStart - 3; i >= 0 && soapPayload.charAt(i) != ':'; i--) {
				nameSpacePrefix = soapPayload.charAt(i) + nameSpacePrefix;
			}
			nameSpacePrefix += ":";
		}
		return nameSpacePrefix;
	}

	private String extractObject(String soapPayload, String nameSpacePrefix,
			boolean writeOp) {
		if (soapPayload.indexOf("obj/>") >= 0) { // only simple obj closing tag
			// present - empty obj
			return "";
		}

		int hrefStart = soapPayload.indexOf("href=\"");
		int firstQuote = soapPayload.indexOf("\"", hrefStart) + 1;
		int secondQuote = soapPayload.indexOf("\"", firstQuote + 1);

		String href = "";
		if (firstQuote >= 0 && secondQuote > firstQuote) {
			href = soapPayload.substring(firstQuote, secondQuote);
		}

		// obj starts after second quote and closing tag
		int objStart = secondQuote + 2;

		// obj ends after first closing tag (either write or invoke)

		int objEnd = soapPayload.indexOf("</" + nameSpacePrefix
				+ ((writeOp) ? ("write") : ("invoke")) + ">");

		if (objStart >= 0 && objEnd > objStart) {
			String obj = soapPayload.substring(objStart, objEnd).trim();

			String obixNsPrefix = extractNSPrefix(
					"http://obix.org/ns/schema/1.1", soapPayload);

			// remove namepace
			obj = obj.replaceAll(obixNsPrefix, "");

			return obj;
		}

		return "";
	}

	private String extractHref(String soapPayload) {
		int hrefStart = soapPayload.indexOf("href=\"");
		int firstQuote = soapPayload.indexOf("\"", hrefStart) + 1;
		int secondQuote = soapPayload.indexOf("\"", firstQuote + 1);

		String href = "";
		if (firstQuote >= 0 && secondQuote > firstQuote) {
			href = soapPayload.substring(firstQuote, secondQuote);
		}

		return href;
	}

	private static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}

}

enum OPERATION {
	WRITE, READ, INVOKE
}

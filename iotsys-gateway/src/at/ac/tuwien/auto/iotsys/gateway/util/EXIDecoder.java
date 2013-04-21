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

package at.ac.tuwien.auto.iotsys.gateway.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.openexi.proc.common.EXIOptionsException;
import org.openexi.proc.common.GrammarOptions;
import org.openexi.proc.grammars.GrammarCache;
import org.openexi.sax.EXIReader;
import org.openexi.schema.EXISchema;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import obix.Bool;
import obix.Int;
import obix.Obj;
import obix.Real;

public class EXIDecoder {
	private static GrammarCache schemaGrammarCache;
	private static GrammarCache defaultGrammarCache;

	private static final EXIDecoder instance = new EXIDecoder();
	
	
	public static void main(String[] args) {
		File inputFile = new File("out.exi");
		try {
			FileInputStream in = new FileInputStream(inputFile);
			// Read the file into a byte array.
			byte fileContent[] = new byte[(int) inputFile.length()];
			in.read(fileContent);
			Obj obj = getInstance().fromBytes(fileContent, true);
			System.out.println("FileContent length: " + fileContent.length);
			System.out.println(obj);
		} catch (FileNotFoundException e) {	
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (EXIOptionsException e) {
			e.printStackTrace();
		}

	}

	private EXIDecoder() {
		short options = GrammarOptions.DEFAULT_OPTIONS;

		defaultGrammarCache = new GrammarCache(null, options);

		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setNamespaceAware(true);

		EXISchema schema = null;
		FileInputStream fis;
		try {
			fis = new FileInputStream("res/obix.esd");
			DataInputStream dis = new DataInputStream(fis);
			schema = (EXISchema) EXISchema.readIn(dis);
			schemaGrammarCache = new GrammarCache(schema, options);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// fall back to default grammarCache
			schemaGrammarCache = defaultGrammarCache;
		} catch (IOException e) {
			e.printStackTrace();
			// fall back to default grammarCache
			schemaGrammarCache = defaultGrammarCache;
		}
	}

	public Obj fromBytes(byte[] payload, boolean useEXISchema)
			throws IOException, SAXException,
			TransformerConfigurationException, EXIOptionsException {
		
		// EXIReader infers and reconstructs the XML file structure.
		EXIReader reader = new EXIReader();

		if (useEXISchema) {
			reader.setEXISchema(schemaGrammarCache);
		} else {
			reader.setEXISchema(defaultGrammarCache);
		}

		// Assign the transformer handler to interpret XML content.
		ObixHandler obixHandler = new ObixHandler();
		reader.setContentHandler(obixHandler);

		// Parse the file information.
		reader.parse(new InputSource(new ByteArrayInputStream(payload)));

		return obixHandler.getObj();
	}
	
	public static EXIDecoder getInstance(){
		return instance;
	}

}

class ObixHandler extends DefaultHandler {

	// Obj to be returned
	private Obj obj = new Obj();
	
	public ObixHandler(){
		
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		if("bool".equals(localName)){
			obj = new Bool();
			if("true".equals(attributes.getValue(0))){
				obj.setBool(true);
			}
			else{
				obj.setBool(false);
			}
		}
		else if("real".equals(localName)) {
			obj = new Real();
			obj.setReal(Double.parseDouble(attributes.getValue(0)));
		} 
		else if("int".equals(localName)){
			obj = new Int();
			obj.setInt(Integer.parseInt(attributes.getValue(0)));
		}
	}

	public Obj getObj() {
		return obj;
	}
	
	

}

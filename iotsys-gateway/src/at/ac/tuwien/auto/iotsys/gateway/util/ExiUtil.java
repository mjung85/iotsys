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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
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
import org.openexi.sax.Transmogrifier;
import org.openexi.sax.TransmogrifierException;
import org.openexi.schema.EXISchema;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Simple EXI util that uses Nagasena for EXI encoding and decoding.

 */
public class ExiUtil {
	private GrammarCache schemaGrammarCache;
	private GrammarCache defaultGrammarCache;
	private SAXTransformerFactory saxTransformerFactory;
	
	private static final ExiUtil instance = new ExiUtil();

	private ExiUtil() {
		short options = GrammarOptions.DEFAULT_OPTIONS;

		defaultGrammarCache = new GrammarCache(null, options);

		saxTransformerFactory = (SAXTransformerFactory) SAXTransformerFactory
				.newInstance();
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setNamespaceAware(true);

		EXISchema schema = null;
		FileInputStream fis;
		try {
			fis = new FileInputStream("res/obix.esd");
			DataInputStream dis = new DataInputStream(fis);
			schema = (EXISchema) EXISchema.readIn(dis);
			schemaGrammarCache = new GrammarCache(schema, options);
		} catch (IOException e) {
			e.printStackTrace();
			// fall back to default grammar cache
			schemaGrammarCache = defaultGrammarCache;
		}

	}

	public byte[] encodeEXI(String source)
			throws TransmogrifierException, EXIOptionsException, IOException {
		return encodeEXI(source, false);
	}

	public byte[] encodeEXI(String source, boolean useEXISchema)
			throws TransmogrifierException, EXIOptionsException, IOException {
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

		Transmogrifier transmogrifier = new Transmogrifier();

		if (useEXISchema) {
			// if a schema should be used, the default namespace need to be
			// added
			int firstSpace = source.indexOf(' '); // first space of the first
													// element
			StringBuffer buffer = new StringBuffer(source);
			buffer.insert(firstSpace + 1,
					"xmlns=\"http://obix.org/ns/schema/1.1\" ");
			source = buffer.toString();
			transmogrifier.setEXISchema(schemaGrammarCache);
		} else {
			transmogrifier.setEXISchema(defaultGrammarCache);
		}

		transmogrifier.setOutputStream(outBytes);

		transmogrifier.encode(new InputSource(new ByteArrayInputStream(source
				.getBytes())));

		return outBytes.toByteArray();

	}

	public String decodeEXI(byte[] source) throws FileNotFoundException,
			IOException, SAXException, EXIOptionsException,
			TransformerConfigurationException {
		return decodeEXI(source, false);
	}

	public String decodeEXI(byte[] source, boolean useEXISchema)
			throws FileNotFoundException, IOException, SAXException,
			EXIOptionsException, TransformerConfigurationException {

		StringWriter stringWriter = new StringWriter();

		EXIReader reader = new EXIReader();

		if (useEXISchema) {
			reader.setEXISchema(schemaGrammarCache);
		} else {
			reader.setEXISchema(defaultGrammarCache);
		}
		TransformerHandler transformerHandler = saxTransformerFactory.newTransformerHandler();
	
		transformerHandler.setResult(new StreamResult(stringWriter));

		reader.setContentHandler(transformerHandler);

		reader.parse(new InputSource(new ByteArrayInputStream(source)));

		final String reconstitutedString;
		reconstitutedString = stringWriter.getBuffer().toString();

		return reconstitutedString;
	}
	
	public static ExiUtil getInstance(){
		return instance;
	}
}

class StringBufferOutputStream extends OutputStream {
	private StringBuffer textBuffer = new StringBuffer();

	/**
     * 
     */
	public StringBufferOutputStream() {
		super();
	}

	/*
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		char a = (char) b;
		textBuffer.append(a);
	}

	public String toString() {
		return textBuffer.toString();
	}

	public void clear() {
		textBuffer.delete(0, textBuffer.length());
	}
}

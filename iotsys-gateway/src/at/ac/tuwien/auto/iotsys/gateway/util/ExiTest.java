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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import org.openexi.sax.Transmogrifier;
import org.openexi.sax.TransmogrifierException;
import org.openexi.schema.EXISchema;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ExiTest {
	public static void main(String[] args) {

		try {
			encodeEXI("in.xml", "out.exi", true);
			decodeEXI("out.exi", "out.xml", true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (TransmogrifierException e) {
			e.printStackTrace();
		} catch (EXIOptionsException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void decodeEXI(String sourceFile, String destinationFile)
			throws FileNotFoundException, IOException, SAXException,
			EXIOptionsException, TransformerConfigurationException {
		decodeEXI(sourceFile, destinationFile, false);
	}

	public static void decodeEXI(String sourceFile, String destinationFile,
			boolean useEXISchema) throws FileNotFoundException, IOException,
			SAXException, EXIOptionsException,
			TransformerConfigurationException {

		FileInputStream in = null;
		FileWriter out = null;
		StringWriter stringWriter = new StringWriter();

		// The Grammar Cache stores schema and EXI options information. The
		// settings nust match when encoding
		// and subsequently decoding a data set.
		GrammarCache grammarCache = null;

		// All EXI options can expressed in a single short integer.
		// DEFAULT_OPTIONS=2;
		short options = GrammarOptions.DEFAULT_OPTIONS;

		try {

			// Standard SAX methods parse content and lexical values.
			SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) SAXTransformerFactory
					.newInstance();
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setNamespaceAware(true);
			TransformerHandler transformerHandler = saxTransformerFactory
					.newTransformerHandler();

			// EXIReader infers and reconstructs the XML file structure.
			EXIReader reader = new EXIReader();

			File inputFile = new File(sourceFile);
			in = new FileInputStream(inputFile);
			out = new FileWriter(destinationFile);

			// Set the schema (null, in this case) and EXI Options (default) in
			// the Grammar Cache.
			if(useEXISchema){				
				EXISchema schema = null;
				FileInputStream fis = new FileInputStream("res/obix.esd");
                DataInputStream dis = new DataInputStream(fis);       
				schema = (EXISchema) EXISchema.readIn(dis);
				grammarCache = new GrammarCache(schema, options);
			}
			else{
				grammarCache = new GrammarCache(null, options);
			}
			


			// Set the schema and options for EXIReader.
			reader.setEXISchema(grammarCache);

			// Prepare to send the results from the transformer to a
			// StringWriter object.
			transformerHandler.setResult(new StreamResult(stringWriter));

			// Read the file into a byte array.
			byte fileContent[] = new byte[(int) inputFile.length()];
			in.read(fileContent);

			// Assign the transformer handler to interpret XML content.
			reader.setContentHandler(transformerHandler);

			// Parse the file information.
			reader.parse(new InputSource(new ByteArrayInputStream(fileContent)));

			// Get the resulting string, write it to the output file, and flush
			// the buffer contents.
			final String reconstitutedString;
			reconstitutedString = stringWriter.getBuffer().toString();
			out.write(reconstitutedString);
			out.flush();
		}
		// Verify that the input and output files are closed.
		finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}

	public static String decodeEXI(byte[] source) throws FileNotFoundException,
			IOException, SAXException, EXIOptionsException,
			TransformerConfigurationException {
		return decodeEXI(source, false);
	}

	public static String decodeEXI(byte[] source, boolean useEXISchema)
			throws FileNotFoundException, IOException, SAXException,
			EXIOptionsException, TransformerConfigurationException {

		StringWriter stringWriter = new StringWriter();

		// The Grammar Cache stores schema and EXI options information. The
		// settings nust match when encoding
		// and subsequently decoding a data set.
		GrammarCache grammarCache = null;

		// All EXI options can expressed in a single short integer.
		// DEFAULT_OPTIONS=2;
		short options = GrammarOptions.DEFAULT_OPTIONS;

		// Standard SAX methods parse content and lexical values.
		SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) SAXTransformerFactory
				.newInstance();
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setNamespaceAware(true);
		TransformerHandler transformerHandler = saxTransformerFactory
				.newTransformerHandler();

		// EXIReader infers and reconstructs the XML file structure.
		EXIReader reader = new EXIReader();

		// File inputFile = new File(sourceFile);
		// in = new FileInputStream(inputFile);
		// out = new FileWriter(destinationFile);

		// Set the schema (null, in this case) and EXI Options (default) in
		// the Grammar Cache.
		if(useEXISchema){				
			EXISchema schema = null;
			FileInputStream fis = new FileInputStream("res/obix.esd");
            DataInputStream dis = new DataInputStream(fis);       
			schema = (EXISchema) EXISchema.readIn(dis);
			grammarCache = new GrammarCache(schema, options);
		}
		else{
			grammarCache = new GrammarCache(null, options);
		}
		


		// Set the schema and options for EXIReader.
		reader.setEXISchema(grammarCache);

		// Prepare to send the results from the transformer to a
		// StringWriter object.
		transformerHandler.setResult(new StreamResult(stringWriter));

		// Assign the transformer handler to interpret XML content.
		reader.setContentHandler(transformerHandler);

		// Parse the file information.
		reader.parse(new InputSource(new ByteArrayInputStream(source)));

		// Get the resulting string, write it to the output file, and flush
		// the buffer contents.
		final String reconstitutedString;
		reconstitutedString = stringWriter.getBuffer().toString();

		return reconstitutedString;

	}

	public static void encodeEXI(String sourceFile, String destinationFile)
			throws FileNotFoundException, IOException, ClassNotFoundException,
			TransmogrifierException, EXIOptionsException {
		encodeEXI(sourceFile, destinationFile, false);
	}

	public static void encodeEXI(String sourceFile, String destinationFile, boolean useEXISchema)
			throws FileNotFoundException, IOException, ClassNotFoundException,
			TransmogrifierException, EXIOptionsException {
		FileInputStream in = null;
		FileOutputStream out = null;
		GrammarCache grammarCache = null;

		// All EXI options can be stored in a single short integer.
		// DEFAULT_OPTIONS=2.
		short options = GrammarOptions.DEFAULT_OPTIONS;
		try {

			// Encoding always requires the same steps.

			// 1. Instantiate a Transmogrifier
			Transmogrifier transmogrifier = new Transmogrifier();

			// 2. Initialize the input and output streams.
			in = new FileInputStream(sourceFile);
			out = new FileOutputStream(destinationFile);

			// 3. Set the schema and EXI options in the Grammar Cache. This
			// example uses default options and no schema.
			if(useEXISchema){				
				EXISchema schema = null;
				FileInputStream fis = new FileInputStream("res/obix.esd");
                DataInputStream dis = new DataInputStream(fis);       
				schema = (EXISchema) EXISchema.readIn(dis);
				grammarCache = new GrammarCache(schema, options);
			}
			else{
				grammarCache = new GrammarCache(null, options);
			}
			

			// 4. Set the configuration options in the Transmogrifier. Later
			// examples will show more possible settings.
			transmogrifier.setEXISchema(grammarCache);

			// 5. Set the output stream.
			transmogrifier.setOutputStream(out);

			// 6. Encode the input stream.
			transmogrifier.encode(new InputSource(in));
		}

		// 7. Verify that the streams are closed.
		finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}

	public static byte[] encodeEXI(String source)
			throws TransmogrifierException, EXIOptionsException, IOException {
		StringWriter stringWriter = new StringWriter();
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		FileInputStream in = null;
		FileOutputStream out = null;
		GrammarCache grammarCache;

		// All EXI options can be stored in a single short integer.
		// DEFAULT_OPTIONS=2.
		short options = GrammarOptions.DEFAULT_OPTIONS;
		try {

			// Encoding always requires the same steps.

			// 1. Instantiate a Transmogrifier
			Transmogrifier transmogrifier = new Transmogrifier();

			// 3. Set the schema and EXI options in the Grammar Cache. This
			// example uses default options and no schema.
			grammarCache = new GrammarCache(null, options);

			// 4. Set the configuration options in the Transmogrifier. Later
			// examples will show more possible settings.
			transmogrifier.setEXISchema(grammarCache);

			// 5. Set the output stream.
			transmogrifier.setOutputStream(outBytes);

			// 6. Encode the input stream.
			transmogrifier.encode(new InputSource(new ByteArrayInputStream(
					source.getBytes())));

			return outBytes.toByteArray();
		}

		// 7. Verify that the streams are closed.
		finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}
}

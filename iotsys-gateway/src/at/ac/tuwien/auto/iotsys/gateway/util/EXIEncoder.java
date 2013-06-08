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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;

import org.openexi.proc.common.EXIOptionsException;
import org.openexi.proc.common.GrammarOptions;
import org.openexi.proc.grammars.GrammarCache;
import org.openexi.sax.SAXTransmogrifier;
import org.openexi.sax.Transmogrifier;
import org.openexi.sax.TransmogrifierException;
import org.openexi.schema.EXISchema;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import obix.Bool;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Str;
import obix.io.ObixEncoder;

/**
 * The EXIEncoder directly creates EXI encoded data for oBIX object, without the intermediate transformation to XML.
 * This works only for a selected number of basic oBIX object types.
 */
public class EXIEncoder {
	private static GrammarCache schemaGrammarCache;
	private static GrammarCache defaultGrammarCache;

	private static final EXIEncoder instance = new EXIEncoder();
	
	private Transmogrifier schemaTransmogrifier;
	private Transmogrifier transmogrifier;



	public static void main(String[] args) {
		try {
			Bool bool = new Bool(false);
			byte[] encodeEXI = getInstance().toBytes(bool, true);
			String xml = ObixEncoder.toString(bool);
			System.out.println(xml);
			System.out.println("xml " + xml.getBytes().length);
			System.out.println("exi " + encodeEXI.length);
			String decodeEXI = ExiUtil.getInstance().decodeEXI(encodeEXI, true);
			System.out.println("decoded: " + decodeEXI);
			Obj obj = EXIDecoder.getInstance().fromBytes(encodeEXI, true);
			System.out.println("decoded obj: " + obj);
			
			Int i = new Int(58);
			encodeEXI = getInstance().toBytes(i, true);
			xml = "<int val=\"58\"/>";
			System.out.println(new String(xml));
			System.out.println("xml " + xml.getBytes().length);
			System.out.println("exi " + encodeEXI.length);
			decodeEXI = ExiUtil.getInstance().decodeEXI(encodeEXI, true);
			System.out.println("decoded: " + decodeEXI);
			obj = EXIDecoder.getInstance().fromBytes(encodeEXI, true);
			System.out.println("decoded obj: " + obj);
			
			Real real = new Real(58.12);
			encodeEXI = getInstance().toBytes(real, true);
			xml = "<real val=\"58.12\"/>";
			System.out.println(new String(xml));
			System.out.println("xml " + xml.getBytes().length);
			System.out.println("exi " + encodeEXI.length);
			decodeEXI = ExiUtil.getInstance().decodeEXI(encodeEXI, true);
			System.out.println("decoded: " + decodeEXI);
			obj = EXIDecoder.getInstance().fromBytes(encodeEXI, true);
			System.out.println("decoded obj: " + obj);
			
			xml = "<str val=\"hello world\"/>";
			Str str = new Str("hello world");
			encodeEXI = getInstance().toBytes(str, true);


			System.out.println(new String(xml));
			System.out.println("xml " + xml.getBytes().length);
			System.out.println("exi " + encodeEXI.length);
			decodeEXI = ExiUtil.getInstance().decodeEXI(encodeEXI, true);
			System.out.println("decoded: " + decodeEXI);
			obj = EXIDecoder.getInstance().fromBytes(encodeEXI, true);
			System.out.println("decoded obj: " + obj);
			
			
		} catch (TransmogrifierException e) {
			e.printStackTrace();
		} catch (EXIOptionsException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private EXIEncoder() {
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
		

		try {
			schemaTransmogrifier = new Transmogrifier();
			schemaTransmogrifier.setEXISchema(schemaGrammarCache);
			
			transmogrifier = new Transmogrifier();
			transmogrifier.setEXISchema(defaultGrammarCache);
		} catch (TransmogrifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EXIOptionsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	

	}

	public byte[] toBytes(Obj obj, boolean useEXISchema)
			throws EXIOptionsException, IOException, TransmogrifierException,
			SAXException {

		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

		Transmogrifier transmogrifier = new Transmogrifier();

		if (useEXISchema) {
			transmogrifier.setEXISchema(schemaGrammarCache);
		} else {
			transmogrifier.setEXISchema(defaultGrammarCache);
		}

		transmogrifier.setOutputStream(outBytes);

		SAXTransmogrifier saxTransmogrifier = transmogrifier
				.getSAXTransmogrifier();

		saxTransmogrifier.startDocument();
		saxTransmogrifier.startPrefixMapping("xml",
				"http://www.w3.org/XML/1998/namespace");
		saxTransmogrifier.startPrefixMapping("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		saxTransmogrifier.startPrefixMapping("xsd",
				"http://www.w3.org/2001/http://www.w3.org/2001/XMLSchema");
		saxTransmogrifier.startPrefixMapping("obix",
				"http://obix.org/ns/schema/1.1");

		if(obj instanceof Bool){
			Bool bool = (Bool) obj;
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", "val", "val", "", "" + bool.get());
			saxTransmogrifier.startElement("http://obix.org/ns/schema/1.1", "bool",
					"obix:bool", atts);
			saxTransmogrifier.endElement("http://obix.org/ns/schema/1.1", "bool",
					"obix:bool");
			saxTransmogrifier.endDocument();
		}
		else if(obj instanceof Int){
			Int i = (Int) obj;
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", "val", "val", "", "" + i.get());
			saxTransmogrifier.startElement("http://obix.org/ns/schema/1.1", "int",
					"obix:int", atts);
			saxTransmogrifier.endElement("http://obix.org/ns/schema/1.1", "int",
					"obix:int");
			saxTransmogrifier.endDocument();
		} else if(obj instanceof Real){
			Real real = (Real) obj;
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", "val", "val", "", "" + real.get());
			saxTransmogrifier.startElement("http://obix.org/ns/schema/1.1", "real",
					"obix:real", atts);
			saxTransmogrifier.endElement("http://obix.org/ns/schema/1.1", "real",
					"obix:real");
			saxTransmogrifier.endDocument();
		} else if(obj instanceof Str){
			Str str = (Str) obj;
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", "val", "val", "", "" + str.get());
			saxTransmogrifier.startElement("http://obix.org/ns/schema/1.1", "str",
					"obix:str", atts);
			saxTransmogrifier.endElement("http://obix.org/ns/schema/1.1", "str",
					"obix:str");
			saxTransmogrifier.endDocument();
		} else{ // Not supported
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", "display", "display", "", "EXI encoding not supported for this object.");
			saxTransmogrifier.startElement("http://obix.org/ns/schema/1.1", "err",
					"obix:err", atts);
			saxTransmogrifier.endElement("http://obix.org/ns/schema/1.1", "err",
					"obix:err");
			saxTransmogrifier.endDocument();
		}

		return outBytes.toByteArray();
	}
	
	public synchronized byte[] toBytes(Bool bool, boolean useEXISchema)
			throws EXIOptionsException, IOException, TransmogrifierException,
			SAXException {

		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		Transmogrifier transmogrifier; 
		if(useEXISchema){
			transmogrifier = schemaTransmogrifier;
		}
		else{
			transmogrifier = this.transmogrifier;
		}
		transmogrifier.setOutputStream(outBytes);

		SAXTransmogrifier saxTransmogrifier = transmogrifier
				.getSAXTransmogrifier();

		saxTransmogrifier.startDocument();
		saxTransmogrifier.startPrefixMapping("xml",
				"http://www.w3.org/XML/1998/namespace");
		saxTransmogrifier.startPrefixMapping("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		saxTransmogrifier.startPrefixMapping("xsd",
				"http://www.w3.org/2001/http://www.w3.org/2001/XMLSchema");
		saxTransmogrifier.startPrefixMapping("obix",
				"http://obix.org/ns/schema/1.1");

		
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "val", "val", "", "" + bool.get());
		saxTransmogrifier.startElement("http://obix.org/ns/schema/1.1", "bool",
					"obix:bool", atts);
		saxTransmogrifier.endElement("http://obix.org/ns/schema/1.1", "bool",
					"obix:bool");
		saxTransmogrifier.endDocument();

		return outBytes.toByteArray();
	}
	
	public synchronized byte[] toBytes(Int i, boolean useEXISchema)
			throws EXIOptionsException, IOException, TransmogrifierException,
			SAXException {

		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		Transmogrifier transmogrifier; 
		if(useEXISchema){
			transmogrifier = schemaTransmogrifier;
		}
		else{
			transmogrifier = this.transmogrifier;
		}
		transmogrifier.setOutputStream(outBytes);

		SAXTransmogrifier saxTransmogrifier = transmogrifier
				.getSAXTransmogrifier();

		saxTransmogrifier.startDocument();
		saxTransmogrifier.startPrefixMapping("xml",
				"http://www.w3.org/XML/1998/namespace");
		saxTransmogrifier.startPrefixMapping("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		saxTransmogrifier.startPrefixMapping("xsd",
				"http://www.w3.org/2001/http://www.w3.org/2001/XMLSchema");
		saxTransmogrifier.startPrefixMapping("obix",
				"http://obix.org/ns/schema/1.1");

		
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "val", "val", "", "" + i.get());
		saxTransmogrifier.startElement("http://obix.org/ns/schema/1.1", "int",
				"obix:int", atts);
		saxTransmogrifier.endElement("http://obix.org/ns/schema/1.1", "int",
				"obix:int");
		saxTransmogrifier.endDocument();

		return outBytes.toByteArray();
	}
	
	public synchronized byte[] toBytes(Real real, boolean useEXISchema)
			throws EXIOptionsException, IOException, TransmogrifierException,
			SAXException {

		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		Transmogrifier transmogrifier; 
		if(useEXISchema){
			transmogrifier = schemaTransmogrifier;
		}
		else{
			transmogrifier = this.transmogrifier;
		}
		transmogrifier.setOutputStream(outBytes);

		SAXTransmogrifier saxTransmogrifier = transmogrifier
				.getSAXTransmogrifier();

		saxTransmogrifier.startDocument();
		saxTransmogrifier.startPrefixMapping("xml",
				"http://www.w3.org/XML/1998/namespace");
		saxTransmogrifier.startPrefixMapping("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		saxTransmogrifier.startPrefixMapping("xsd",
				"http://www.w3.org/2001/http://www.w3.org/2001/XMLSchema");
		saxTransmogrifier.startPrefixMapping("obix",
				"http://obix.org/ns/schema/1.1");

		
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "val", "val", "", "" + real.get());
		saxTransmogrifier.startElement("http://obix.org/ns/schema/1.1", "real",
				"obix:real", atts);
		saxTransmogrifier.endElement("http://obix.org/ns/schema/1.1", "real",
				"obix:real");
		saxTransmogrifier.endDocument();

		return outBytes.toByteArray();
	}
	
	public synchronized byte[] toBytes(Str str, boolean useEXISchema)
			throws EXIOptionsException, IOException, TransmogrifierException,
			SAXException {

		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		Transmogrifier transmogrifier; 
		if(useEXISchema){
			transmogrifier = schemaTransmogrifier;
		}
		else{
			transmogrifier = this.transmogrifier;
		}
		transmogrifier.setOutputStream(outBytes);

		SAXTransmogrifier saxTransmogrifier = transmogrifier
				.getSAXTransmogrifier();

		saxTransmogrifier.startDocument();
		saxTransmogrifier.startPrefixMapping("xml",
				"http://www.w3.org/XML/1998/namespace");
		saxTransmogrifier.startPrefixMapping("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		saxTransmogrifier.startPrefixMapping("xsd",
				"http://www.w3.org/2001/http://www.w3.org/2001/XMLSchema");
		saxTransmogrifier.startPrefixMapping("obix",
				"http://obix.org/ns/schema/1.1");

		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "val", "val", "", "" + str.get());
		saxTransmogrifier.startElement("http://obix.org/ns/schema/1.1", "str",
				"obix:str", atts);
		saxTransmogrifier.endElement("http://obix.org/ns/schema/1.1", "str",
				"obix:str");
		saxTransmogrifier.endDocument();

		return outBytes.toByteArray();
	}
	public static EXIEncoder getInstance(){
		return instance;
	}
}

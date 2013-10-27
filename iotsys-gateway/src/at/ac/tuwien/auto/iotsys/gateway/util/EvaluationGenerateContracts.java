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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import obix.Obj;
import obix.Uri;
import obix.io.BinObixEncoder;
import obix.io.ObixEncoder;

import org.json.JSONException;
import org.openexi.proc.common.EXIOptionsException;
import org.openexi.proc.common.GrammarOptions;
import org.openexi.proc.grammars.GrammarCache;

import org.openexi.sax.Transmogrifier;
import org.openexi.sax.TransmogrifierException;
import org.openexi.schema.EXISchema;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;

/**
 * Export oBIX objects as XML documents for evaluation.
 */
public class EvaluationGenerateContracts {

	private static final String dataPointExiExtentionOut = "DP.exi";
	private static final String contractExiDirectory = "./Contracts/Exi";
	private static final String dataPointExiSchemaExtentionOut = "DP.exischema";
	private static final String contractExiSchemaDirectory = "./Contracts/ExiSchema";
	private static final String dataPointObixBinaryExtentionOut = "DP.obin";
	private static final String contractObixBinaryDirectory = "./Contracts/ObixBinary";
	private static final String dataPointJSONExtentionOut = "DP.json";
	private static final String contractJsonDirectory = "./Contracts/JSON";
	private static final String objectExiExtentionOut = "OBJECT.exi";
	private static final String objectExiSchemaExtentionOut = "OBJECT.exischema";
	private static final String objectObixBinaryExtentionOut = "OBJECT.obin";
	private static final String objectJSONExtentionOut = "OBJECT.json";
	private static final String objectXmlExtentionOut = "OBJECT.xml";
	private static final String dataPointXmlExtentionOut = "DP.xml";
	private static final String contractXmlDirectory = "./Contracts/Xml";
	private static final String obixRootName = "/obix";

	private ObjectBroker objectBroker;
	private boolean makeExi;
	private boolean makeXml;
	private boolean makeExiSchemaInformed;
	private boolean makeJSON;
	private boolean makeObixBinary;

	public EvaluationGenerateContracts(ObjectBroker objectBroker,
			boolean makeExi, boolean makeXml, boolean makeExiSchemaInformed,
			boolean makeJSON, boolean makeObixBinary) {
		this.objectBroker = objectBroker;
		this.makeExi = makeExi;
		this.makeXml = makeXml;
		this.makeJSON = makeJSON;
		this.makeObixBinary = makeObixBinary;
		this.makeExiSchemaInformed = makeExiSchemaInformed;

		try {
			encodeExiXmlfromObixRoot();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getXMLObject(String href, String fileName) {
		Obj o = new Obj();
		o = getContractObj(href);

		System.out.println("File name  XML Object: " + fileName + "_"
				+ objectXmlExtentionOut);
		System.out.println(ObixEncoder.toString(o));

		if (o.getParent() == null) {
			writeFile(ObixEncoder.toString(o), fileName + "_"
					+ objectXmlExtentionOut);
		}
		if (o.size() > 0) {
			String fileNameKid = fileName;

			Obj[] kids = o.list();
			for (int i = 0; i < o.size(); i++) {
				if (kids[i].getHref() != null) {
					System.out.println("File name  XML DP: " + fileNameKid
							+ "_" + kids[i].getHref() + "_"
							+ dataPointXmlExtentionOut);
					writeFile(ObixEncoder.toString(kids[i]), fileNameKid + "_"
							+ kids[i].getHref() + "_"
							+ dataPointXmlExtentionOut);
				}
			}
		}
	}
	
	public void encodeObixBinaryFromObject(String href, String fileName) {
		Obj o = new Obj();
		o = getContractObj(href);

		System.out.println("File name  oBIX Binary Object: " + fileName + "_"
				+ objectObixBinaryExtentionOut);
		System.out.println(ObixEncoder.toString(o));

		if (o.getParent() == null) {
			writeBinaryFile(BinObixEncoder.toBytes(o), fileName + "_"
					+ objectObixBinaryExtentionOut);
		}
		if (o.size() > 0) {
			String fileNameKid = fileName;

			Obj[] kids = o.list();
			for (int i = 0; i < o.size(); i++) {
				if (kids[i].getHref() != null) {
					System.out.println("File name  oBIX DP: " + fileNameKid
							+ "_" + kids[i].getHref() + "_"
							+ dataPointObixBinaryExtentionOut);
					writeBinaryFile(BinObixEncoder.toBytes(kids[i]), fileNameKid + "_"
							+ kids[i].getHref() + "_"
							+ dataPointObixBinaryExtentionOut);
				}
			}
		}
	}
	
	public void encodeJsonFromObject(String href, String fileName) {
		Obj o = new Obj();
		o = getContractObj(href);

		if (o.getParent() == null) {
			try {
				writeFile(JsonUtil.fromXMLtoJSON(ObixEncoder.toString(o)), fileName + "_"
						+ objectJSONExtentionOut);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (o.size() > 0) {
			String fileNameKid = fileName;

			Obj[] kids = o.list();
			for (int i = 0; i < o.size(); i++) {
				if (kids[i].getHref() != null) {
					System.out.println("File name  JSON DP: " + fileNameKid
							+ "_" + kids[i].getHref() + "_"
							+ dataPointJSONExtentionOut);
					try {
						writeFile(JsonUtil.fromXMLtoJSON(ObixEncoder.toString(kids[i])), fileNameKid + "_"
								+ kids[i].getHref() + "_"
								+ dataPointJSONExtentionOut);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void writeFile(String data, String destination) {

		File outputFile = new File(destination);
		try {
			FileWriter out = new FileWriter(outputFile);
			out.write(data);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void writeBinaryFile(byte[] data, String destination) {

		File outputFile = new File(destination);
		try {
			FileOutputStream out = new FileOutputStream(outputFile);
			out.write(data);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void encodeExiformObject(String href, String fileName) {
		Obj o = new Obj();
			
		o = getContractObj(href);

		if (o.getParent() == null) {			
			try {
				encodeEXI(ObixEncoder.toString(o), fileName + "_"
						+ objectExiExtentionOut);			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TransmogrifierException e) {
				e.printStackTrace();
			} catch (EXIOptionsException e) {
				e.printStackTrace();
			}

		}

		if (o.size() > 0) {
			String fileNameKid = fileName;

			Obj[] kids = o.list();
			for (int i = 0; i < o.size(); i++) {
				if (kids[i].getHref() != null) {
					System.out.println("Kids href: " + kids[i].getHref());
					// fileNameKid =
					// fileNameKid+"_"+kids[i].getHref()+"_"+dataPointExtentionOut;
					// System.out.println(fileNameKid+"_"+kids[i].getHref()+"_"+dataPointExiExtentionOut);
					try {
						encodeEXI(ObixEncoder.toString(kids[i]), fileNameKid
								+ "_" + kids[i].getHref() + "_"
								+ dataPointExiExtentionOut);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TransmogrifierException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (EXIOptionsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	private void encodeExiSchemaFromObject(String href, String fileName) {
		Obj o = new Obj();
		// String fileName = new String();

		// o = objectBroker.pullObj(new Uri(href));
		o = getContractObj(href);

		System.out.println("href of Object: " + o.getHref().toString());
		System.out.println("Element name: " + o.getElement());
		System.out.println("Obj Size: " + o.size());

		System.out.println(ObixEncoder.toString(o));

		// fileName=o.getIs().toString().substring(o.getIs().toString().indexOf(":")+1);

		if (o.getParent() == null) {
			// String fileNameObject = fileName+"_"+objectExtentionOut;

			System.out.println("Obj is Parent");
			System.out.println("Obj is: " + o.getIs());

			// System.out.println("File name  Object: "+fileNameObject);
			System.out.println("File name  EXI Object: " + fileName + "_"
					+ objectExiSchemaExtentionOut);

			try {
				encodeExiSchema(ObixEncoder.toString(o), fileName + "_"
						+ objectExiSchemaExtentionOut);
				// encodeEXI(ObixEncoder.toString(o),
				// contractDirectory+"/"+fileNameObject);
				// contractDirectory+"/"+destinationFile

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TransmogrifierException e) {
				e.printStackTrace();
			} catch (EXIOptionsException e) {
				e.printStackTrace();
			}

		}

		if (o.size() > 0) {
			String fileNameKid = fileName;

			Obj[] kids = o.list();
			for (int i = 0; i < o.size(); i++) {
				if (kids[i].getHref() != null) {
					System.out.println("Kids href: " + kids[i].getHref());
					// fileNameKid =
					// fileNameKid+"_"+kids[i].getHref()+"_"+dataPointExtentionOut;
					// System.out.println(fileNameKid+"_"+kids[i].getHref()+"_"+dataPointExiExtentionOut);
					try {
						encodeExiSchema(ObixEncoder.toString(kids[i]), fileNameKid
								+ "_" + kids[i].getHref() + "_"
								+ dataPointExiSchemaExtentionOut);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TransmogrifierException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (EXIOptionsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public Obj getContractObj(String href) {
		Obj o = new Obj();
		o = objectBroker.pullObj(new Uri(href), true);
		return o;
	}

	public String getContract(String href) {
		Obj o = new Obj();
		// o = objectBroker.pullObj(new Uri(href));
		o = getContractObj(href);
		return ObixEncoder.toString(o);
	}

	// public static void encodeExiDP(String source) throws SAXException,
	// IOException{
	public void encodeExiXmlfromObixRoot() throws SAXException, IOException {
		String fileName = new String();

		// StringBuffer obixContract = new StringBuffer("");
		// String obixContract = new String();
		// obixContract = getContract("/airdamper");
		// System.out.println(getContract("/obix"));

		Obj o = new Obj();
		o = getContractObj(obixRootName);

		System.out.println("Obix root Size: " + o.size());

		if (o.size() > 0) {
			Obj[] kids = o.list();
			for (int i = 0; i < o.size(); i++) {
				if (kids[i].getHref() != null) {
					System.out.println("Kids href: " + kids[i].getHref());

					fileName = kids[i]
							.getIs()
							.toString()
							.substring(
									kids[i].getIs().toString().indexOf(":") + 1);
					System.out
							.println(kids[i]
									.getIs()
									.toString()
									.substring(
											kids[i].getIs().toString()
													.indexOf(":") + 1));
					if (makeExi) {
						encodeExiformObject(kids[i].getHref().toString(),
								contractExiDirectory + "/" + fileName);
					}
					if (makeXml) {

						System.out.println("contract kids: "
								+ kids[i].getHref().toString());
						System.out.println("contract director+filename: "
								+ contractXmlDirectory + "/" + fileName);

						getXMLObject(kids[i].getHref().toString(),
								contractXmlDirectory + "/" + fileName);
					}

					if (makeExiSchemaInformed) {
						encodeExiSchemaFromObject(kids[i].getHref().toString(),
								contractExiSchemaDirectory + "/" + fileName);
					}
					
					if(makeObixBinary){
						encodeObixBinaryFromObject(kids[i].getHref().toString(),
								contractObixBinaryDirectory + "/" + fileName);
					}
					
					if(makeJSON){
						encodeJsonFromObject(kids[i].getHref().toString(),
								contractJsonDirectory + "/" + fileName);
					}
					// encodeExiformObject("/airdamper")
				}
			}
		}
	}

	public static void encodeEXI(String source, String destinationFile)
			throws FileNotFoundException, IOException, ClassNotFoundException,
			TransmogrifierException, EXIOptionsException {
		// StringWriter stringWriter = new StringWriter();
		// destinationFile = contractDirectory+"/"+destinationFile;

		System.out.println(destinationFile);

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

			// 2. Initialize the input and output streams.
			// in = new FileInputStream(sourceFile);
			out = new FileOutputStream(destinationFile);

			// 3. Set the schema and EXI options in the Grammar Cache. This
			// example uses default options and no schema.
			grammarCache = new GrammarCache(null, options);

			// 4. Set the configuration options in the Transmogrifier. Later
			// examples will show more possible settings.
			transmogrifier.setEXISchema(grammarCache);

			// 5. Set the output stream.
			transmogrifier.setOutputStream(out);

			// 6. Encode the input stream.
			transmogrifier.encode(new InputSource(new ByteArrayInputStream(
					source.getBytes())));
		}

		// 7. Verify that the streams are closed.
		finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}

	public static void encodeExiSchema(String source, String destinationFile)
			throws FileNotFoundException, IOException, ClassNotFoundException,
			TransmogrifierException, EXIOptionsException {
		System.out.println(destinationFile);

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

			// 2. Initialize the input and output streams.
			// in = new FileInputStream(sourceFile);
			out = new FileOutputStream(destinationFile);

			EXISchema schema = null;
			FileInputStream fis = new FileInputStream("res/obix.esd");
			DataInputStream dis = new DataInputStream(fis);
			schema = (EXISchema) EXISchema.readIn(dis);
			grammarCache = new GrammarCache(schema, options);

			// if a schema should be used, the default namespace need to be
			// added
			int firstSpace = source.indexOf(' '); // first space of the first
													// element
			StringBuffer buffer = new StringBuffer(source);
			buffer.insert(firstSpace + 1,
					"xmlns=\"http://obix.org/ns/schema/1.1\" ");
			source = buffer.toString();
			

			// 4. Set the configuration options in the Transmogrifier. Later
			// examples will show more possible settings.
			transmogrifier.setEXISchema(grammarCache);

			// 5. Set the output stream.
			transmogrifier.setOutputStream(out);

			// 6. Encode the input stream.
			transmogrifier.encode(new InputSource(new ByteArrayInputStream(
					source.getBytes())));
			fis.close();
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

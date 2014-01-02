package at.ac.tuwien.auto.iotsys.xacml.util;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractNode {
	public static final String URN_XACML_CONTEXT = "urn:oasis:names:tc:xacml:2.0:context:schema:os";
	
	public static final String XML_SCHEMA_TYPE_STRING = "http://www.w3.org/2001/XMLSchema#string";

	private String issuer = "IoTSyS";
	
	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public DocumentBuilder getBuilder() {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			dbFactory.setNamespaceAware(true);
			builder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return builder;
	}

	public abstract Element getElement(Document doc);

	public static String transformDocument(Document doc, boolean indent) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		String out = "";
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			if (indent) {
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			}
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			out = writer.getBuffer().toString();

		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
}

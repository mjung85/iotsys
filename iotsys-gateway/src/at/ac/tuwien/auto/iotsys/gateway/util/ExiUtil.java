package at.ac.tuwien.auto.iotsys.gateway.util;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
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
 * Simple EXI util that uses Nagasena for exi encoding and decoding.
 * 
 * @author Markus Jung
 * 
 */
public class ExiUtil {
	private GrammarCache grammarCache;
	private Transmogrifier transmogrifier;
	private EXIReader reader;
	private TransformerHandler transformerHandler;

	public ExiUtil() throws TransmogrifierException, EXIOptionsException,
			TransformerConfigurationException {
		short options = GrammarOptions.DEFAULT_OPTIONS;
		transmogrifier = new Transmogrifier();
		grammarCache = new GrammarCache(null, options);
		transmogrifier.setEXISchema(grammarCache);

		SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) SAXTransformerFactory
				.newInstance();
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setNamespaceAware(true);
		transformerHandler = saxTransformerFactory.newTransformerHandler();
		reader = new EXIReader();
		reader.setEXISchema(grammarCache);
	}

	public String exiEncode(String in) throws TransmogrifierException,
			IOException {
		StringBufferOutputStream out = new StringBufferOutputStream();
		transmogrifier.setOutputStream(out);

		transmogrifier.encode(new InputSource(new StringReader(in)));
		return out.toString();
	}

	public String exiDecode(String input) throws IOException, SAXException {
		StringBufferOutputStream out = new StringBufferOutputStream();
		transformerHandler.setResult(new StreamResult(out));
		reader.setContentHandler(transformerHandler);
		reader.parse(new InputSource(new ByteArrayInputStream(input.getBytes())));
		return out.toString();
	}

	public static byte[] encodeEXI(String source)
			throws TransmogrifierException, EXIOptionsException, IOException {
		return encodeEXI(source, false);
	}

	public static byte[] encodeEXI(String source, boolean useEXISchema)
			throws TransmogrifierException, EXIOptionsException, IOException {
		StringWriter stringWriter = new StringWriter();
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
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

			// 3. Set the schema and EXI options in the Grammar Cache. This
			// example uses default options and no schema.
			// Set the schema (null, in this case) and EXI Options (default) in
			// the Grammar Cache.
			if (useEXISchema) {
				EXISchema schema = null;
				FileInputStream fis = new FileInputStream("res/obix.esd");
				DataInputStream dis = new DataInputStream(fis);
				schema = (EXISchema) EXISchema.readIn(dis);
				grammarCache = new GrammarCache(schema, options);
				
				// if a schema should be used, the default namespace need to be added
				int firstSpace = source.indexOf(' '); // first space of the first element
				StringBuffer buffer = new StringBuffer(source);
				buffer.insert(firstSpace + 1 , "xmlns=\"http://obix.org/ns/schema/1.1\" ");
				source = buffer.toString();
			} else {
				grammarCache = new GrammarCache(null, options);
			}

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

		// Set the schema (null, in this case) and EXI Options (default) in
		// the Grammar Cache.
		if (useEXISchema) {
			EXISchema schema = null;
			FileInputStream fis = new FileInputStream("res/obix.esd");
			DataInputStream dis = new DataInputStream(fis);
			schema = (EXISchema) EXISchema.readIn(dis);
			grammarCache = new GrammarCache(schema, options);
		} else {
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

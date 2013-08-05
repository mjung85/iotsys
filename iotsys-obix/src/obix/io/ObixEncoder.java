/*
 * This code licensed to public domain
 */
package obix.io;

import java.io.*;
import obix.*;
import obix.Enum;
import obix.xml.*;

/**
 * ObixEncoder is used to serialize an Obj tree to an XML stream.
 * 
 * @author Brian Frank
 * @creation 27 Apr 05
 * @version $Revision$ $Date$
 */
public class ObixEncoder extends XWriter {

	// //////////////////////////////////////////////////////////////
	// Factory
	// //////////////////////////////////////////////////////////////

	/**
	 * Dump an obj tree to stdout using an ObixEncoder.
	 */
	public static void dump(Obj obj) {
		try {
			ObixEncoder encoder = new ObixEncoder(System.out);
			encoder.encodeDocument(obj, false);
			encoder.flush();
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
	}

	/**
	 * Encode the specified obj to an internal String.
	 */
	public static String toString(Obj obj, boolean useRelativePath) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObixEncoder encoder = new ObixEncoder(out);
			encoder.encode(obj, useRelativePath);
			encoder.flush();
			return new String(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
	}
	
	public static String toString(Obj obj){
		return toString(obj, true);
	}

	// //////////////////////////////////////////////////////////////
	// Constructors
	// //////////////////////////////////////////////////////////////

	/**
	 * Construct encoder for specified file.
	 */
	public ObixEncoder(File file) throws IOException {
		super(file);
	}

	/**
	 * Construct encoder for specified output stream.
	 */
	public ObixEncoder(OutputStream out) throws IOException {
		super(out);
	}

	// //////////////////////////////////////////////////////////////
	// Document
	// //////////////////////////////////////////////////////////////

	/**
	 * Encode a full obix XML document including prolog and header information.
	 */
	public void encodeDocument(Obj obj, boolean useRelativePath)
			throws IOException {
		prolog();
		encode(obj, useRelativePath);
		flush();
	}
	
	public void encodeDocument(Obj obj)
			throws IOException {
		
		encode(obj, true);
		
	}

	/**
	 * Encode an object and it's children.
	 */
	public void encode(Obj obj, boolean useRelativePath) throws IOException {
		// open start tag
		String elemName = obj.getElement();
		indent(indent * 2).w('<').w(elemName);

		// name attribute
		String name = obj.getName();
		if (name != null)
			attr(" name", name);

		Uri href = obj.getHref();
		Uri contextPath = new Uri(obj.getFullContextPath());

		// avoid to encode the absolute URI, provide only relative URI
		if (obj.getParent() != null) {
			if (useRelativePath) {
				contextPath = obj.getRelativePath();
			} else {
				contextPath = href;
			}
		}

		if (href != null)
			attr(" href", contextPath.encodeVal());

		// val attribute
		if (obj instanceof Val) {
			Val val = (Val) obj;
			attr(" val", val.encodeVal());
		}
		// of attribute
		else if (obj instanceof List) {
			List list = (List) obj;
			Contract of = list.getOf();
			if (!of.containsOnlyObj())
				attr(" of", of.toString());
		}
		// in/out attributes
		else if (obj instanceof Op) {
			Op op = (Op) obj;
			attr(" in", op.getIn().toString());
			attr(" out", op.getOut().toString());
		}
		// in/of attributes
		else if (obj instanceof Feed) {
			Feed feed = (Feed) obj;
			attr(" in", feed.getIn().toString());
			attr(" of", feed.getOf().toString());
		}

		// is attribute
		Contract is = obj.getIs();
		if (is != null)
			attr(" is", is.toString());

		// facets - this is some butt ugly code!
		if (obj.getDisplay() != null)
			attr(" display", obj.getDisplay().toString());
		if (obj.getDisplayName() != null)
			attr(" displayName", obj.getDisplayName().toString());
		if (obj.getIcon() != null)
			attr(" icon", obj.getIcon().toString());
		if (obj.getStatus() != Status.ok)
			attr(" status", obj.getStatus().toString());
		if (obj.isNull())
			attr(" null", "true");
		if (obj.isWritable())
			attr(" writable", "true");
		if (obj instanceof Bool) {
			Bool b = (Bool) obj;
			if (b.getRange() != null)
				attr(" range", b.getRange().toString());
		} else if (obj instanceof Int) {
			Int i = (Int) obj;
			if (i.getMin() != Int.MIN_DEFAULT)
				attr(" min", String.valueOf(i.getMin()));
			if (i.getMax() != Int.MAX_DEFAULT)
				attr(" max", String.valueOf(i.getMax()));
			if (i.getUnit() != null)
				attr(" unit", i.getUnit().toString());
		} else if (obj instanceof Str) {
			Str s = (Str) obj;
			if (s.getMin() != Str.MIN_DEFAULT)
				attr(" min", String.valueOf(s.getMin()));
			if (s.getMax() != Str.MAX_DEFAULT)
				attr(" max", String.valueOf(s.getMax()));
		} else if (obj instanceof Enum) {
			Enum e = (Enum) obj;
			if (e.getRange() != null)
				attr(" range", e.getRange().toString());
		} else if (obj instanceof Real) {
			Real r = (Real) obj;
			if (r.getMin() != Real.MIN_DEFAULT)
				attr(" min", String.valueOf(r.getMin()));
			if (r.getMax() != Real.MAX_DEFAULT)
				attr(" max", String.valueOf(r.getMax()));
			if (r.getUnit() != null)
				attr(" unit", r.getUnit().toString());
			if (r.getPrecision() != Real.PRECISION_DEFAULT)
				attr(" precision", String.valueOf(r.getPrecision()));
		} else if (obj instanceof Reltime) {
			Reltime r = (Reltime) obj;
			if (r.getMin() != null)
				attr(" min", r.getMin().encodeVal());
			if (r.getMax() != null)
				attr(" max", r.getMax().encodeVal());
		} else if (obj instanceof Abstime) {
			Abstime a = (Abstime) obj;
			if (a.getMin() != null)
				attr(" min", a.getMin().encodeVal());
			if (a.getMax() != null)
				attr(" max", a.getMax().encodeVal());
			if (a.getTz() != null)
				attr(" tz", a.getTz());
		} else if (obj instanceof Time) {
			Time t = (Time) obj;
			if (t.getMin() != null)
				attr(" min", t.getMin().encodeVal());
			if (t.getMax() != null)
				attr(" max", t.getMax().encodeVal());
			if (t.getTz() != null)
				attr(" tz", t.getTz());
		} else if (obj instanceof Date) {
			Date d = (Date) obj;
			if (d.getMin() != null)
				attr(" min", d.getMin().encodeVal());
			if (d.getMax() != null)
				attr(" max", d.getMax().encodeVal());
			if (d.getTz() != null)
				attr(" tz", d.getTz());
		} else if (obj instanceof List) {
			List l = (List) obj;
			if (l.getMin() != List.MIN_DEFAULT)
				attr(" min", String.valueOf(l.getMin()));
			if (l.getMax() != List.MAX_DEFAULT)
				attr(" max", String.valueOf(l.getMax()));
		}

		// if no children, close tag and be done
		if (obj.size() == 0) {
			w("/>\n");
			return;
		}

		// close start tag
		w(">\n");
		indent++;

		// write children
		Obj[] kids = obj.list();
		for (int i = 0; i < kids.length; ++i)
			encode(kids[i], useRelativePath);

		// end tag
		indent--;
		indent(indent * 2).w("</").w(elemName).w(">\n");
	}

	// //////////////////////////////////////////////////////////////
	// Fields
	// //////////////////////////////////////////////////////////////

	private int indent;
}

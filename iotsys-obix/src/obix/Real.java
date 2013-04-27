/*
 * This code licensed to public domain
 */
package obix;

/**
 * Real models a 64-bit floating point number
 * 
 * @author Brian Frank
 * @creation 27 Apr 05
 * @version $Revision$ $Date$
 */
public class Real extends Val {

	// //////////////////////////////////////////////////////////////
	// Constructor
	// //////////////////////////////////////////////////////////////

	/**
	 * Construct named Real with specified value.
	 */
	public Real(String name, double val) {
		super(name);
		this.val = val; // we don't want to notify observers here

		// set(val);
	}

	/**
	 * Construct named Real with default of 0.
	 */
	public Real(String name) {
		super(name);
		this.val = 0; // we don't want to notify observers here

		// set(0);
	}

	/**
	 * Construct unnamed Real with specified value.
	 */
	public Real(double val) {
		this.val = val; // we don't want to notify observers here

		// set(val);
	}

	/**
	 * Construct unnamed Real with value of 0.
	 */
	public Real() {
		this.val = 0; // we don't want to notify observers here
		// set(0);
	}

	// //////////////////////////////////////////////////////////////
	// Real
	// //////////////////////////////////////////////////////////////

	/**
	 * Get value as a double.
	 */
	public double get() {
		return val;
	}

	/**
	 * Set value.
	 */
	public void set(double val) {
		this.val = val;
		notifyObservers();
	}

	// //////////////////////////////////////////////////////////////
	// Val
	// //////////////////////////////////////////////////////////////

	/**
	 * Return "real".
	 */
	public String getElement() {
		return "real";
	}

	/**
	 * Return BinObix.REAL.
	 */
	public int getBinCode() {
		return obix.io.BinObix.REAL;
	}

	/**
	 * Return if specified Val has equivalent real value.
	 */
	public boolean valEquals(Val that) {
		if (that instanceof Real)
			return ((Real) that).val == val;
		return false;
	}

	/**
	 * Compares this object with the specified object for order. Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 */
	public int compareTo(Object that) {
		double a = val;
		double b = ((Real) that).val;
		if (a == b)
			return 0;
		if (a < b)
			return -1;
		else
			return 1;
	}

	/**
	 * Encode the value as a string
	 */
	public String encodeVal() {
		return String.valueOf(val);
	}

	/**
	 * Decode the value from a string.
	 */
	public void decodeVal(String val) throws Exception {
		this.val = Double.parseDouble(val);
	}

	// //////////////////////////////////////////////////////////////
	// Facets
	// //////////////////////////////////////////////////////////////

	/**
	 * Get the min facet or MIN_DEFAULT if unspecified.
	 */
	public double getMin() {
		return min;
	}

	/**
	 * Set the min facet.
	 */
	public void setMin(double min) {
		this.min = min;
	}

	/**
	 * Get the max facet or MAX_DEFAULT if unspecified.
	 */
	public double getMax() {
		return max;
	}

	/**
	 * Set the max facet.
	 */
	public void setMax(double max) {
		this.max = max;
	}

	/**
	 * Get the unit facet or null if unspecified.
	 */
	public Uri getUnit() {
		return unit;
	}

	/**
	 * Set the unit facet.
	 */
	public void setUnit(Uri unit) {
		this.unit = unit;
	}

	/**
	 * Get the precision facet or PRECISION_DEFAULT if unspecified.
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * Set the precision facet.
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	// //////////////////////////////////////////////////////////////
	// Fields
	// //////////////////////////////////////////////////////////////

	/** Default min facet is Double.NEGATIVE_INFINITY */
	public static final double MIN_DEFAULT = Double.NEGATIVE_INFINITY;

	/** Default max facet is Double.POSITIVE_INFINITY */
	public static final double MAX_DEFAULT = Double.POSITIVE_INFINITY;

	/** Default precision facet is 1 */
	public static final int PRECISION_DEFAULT = 1;

	private double val;
	private double min = MIN_DEFAULT;
	private double max = MAX_DEFAULT;
	private Uri unit = null;
	private int precision = PRECISION_DEFAULT;

}

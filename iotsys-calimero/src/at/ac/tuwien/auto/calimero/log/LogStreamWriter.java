/*
    Calimero - A library for KNX network access
    Copyright (C) 2006-2008 W. Kastner

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package at.ac.tuwien.auto.calimero.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;

/**
 * LogWriter using a {@link Writer} for output.
 * <p>
 * An existing output stream has to be supplied on creation of this LogWriter. All output
 * will be checked against the internal log level, on logging permitted the output is
 * formatted (optional) and handed to the underlying writer.<br>
 * Output is not automatically flushed after each write by default.<br>
 * Using <code>autoFlush = true</code> in the constructor ensures that no data buffering
 * will delay the output. Note that this may degrade performance.<br>
 * For occasional flushing use {@link #flush()} manually.
 * 
 * @author B. Malinowsky
 */
public class LogStreamWriter extends LogWriter
{
	/** Calendar used to generate date/time of logged output message. */
	protected static final Calendar c = Calendar.getInstance();

	/**
	 * Set the formatting behavior of <code>LogStreamWriter</code>.
	 * <p>
	 * Determines if <code>LogStreamWriter</code> should call
	 * {@link #formatOutput(String, LogLevel, String, Throwable)  formatOutput(LogLevel,
	 * String, Throwable)} before writing out the log message. Defaults to
	 * <code>true</code>, but might be set to <code>false</code> by subtypes if
	 * message given to <code>write</code> is already formatted.
	 */
	protected boolean formatOutput = true;

	/**
	 * Line separator, retrieved from the property "line.separator" and set in the
	 * constructor.
	 */
	protected String lineSep;

	boolean autoFlush;
	private Writer out;

	/**
	 * Sets line separator; also called by subtypes creating the output stream on their
	 * own.
	 */
	protected LogStreamWriter()
	{
		try {
			lineSep = System.getProperty("line.separator");
		}
		catch (final SecurityException e) {}
		if (lineSep == null)
			lineSep = "\n";
	}

	/**
	 * Creates a <code>LogStreamWriter</code> with specified output stream.
	 * <p>
	 * The output stream is wrapped by a BufferedWriter.
	 * 
	 * @param os an OutputStream used by this LogStreamWriter
	 */
	public LogStreamWriter(OutputStream os)
	{
		this();
		createWriter(os);
	}

	/**
	 * Creates a <code>LogStreamWriter</code> with specified log level and output
	 * stream.
	 * 
	 * @param level log level assigned with this <code>LogStreamWriter</code>
	 * @param os an OutputStream used by this <code>LogStreamWriter</code>
	 * @see #LogStreamWriter(OutputStream)
	 */
	public LogStreamWriter(LogLevel level, OutputStream os)
	{
		this(os);
		setLogLevel(level);
	}

	/**
	 * Creates a <code>LogStreamWriter</code> with specified log level and output
	 * stream. Parameter <code>autoFlush</code> sets flushing behavior on write() calls.
	 * 
	 * @param level log level assigned with this <code>LogStreamWriter</code>
	 * @param os an OutputStream used by this <code>LogStreamWriter</code>
	 * @param autoFlush flush output after every successful call to write()
	 * @see #LogStreamWriter(LogLevel, OutputStream)
	 */
	public LogStreamWriter(LogLevel level, OutputStream os, boolean autoFlush)
	{
		this(level, os);
		this.autoFlush = autoFlush;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.log.LogWriter#write
	 * (java.lang.String, tuwien.auto.calimero.log.LogLevel, java.lang.String)
	 */
	public void write(String logService, LogLevel level, String msg)
	{
		doWrite(logService, level, msg, null);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.log.LogWriter#write
	 * (java.lang.String, tuwien.auto.calimero.log.LogLevel, java.lang.String,
	 * java.lang.Throwable)
	 */
	public void write(String logService, LogLevel level, String msg, Throwable t)
	{
		doWrite(logService, level, msg, t);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.log.LogWriter#flush()
	 */
	public synchronized void flush()
	{
		if (out != null)
			try {
				out.flush();
			}
			catch (final IOException e) {
				getErrorHandler().error(this, "on flush", e);
			}
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.log.LogWriter#close()
	 */
	public synchronized void close()
	{
		if (out != null) {
			try {
				out.close();
			}
			catch (final IOException e) {}
			out = null;
		}
	}

	/**
	 * Sets the underlying writer to use for logging output.
	 * <p>
	 * The log stream writer obtains ownership of the writer object.
	 * 
	 * @param w the Writer
	 */
	protected final void setOutput(Writer w)
	{
		out = w;
	}

	/**
	 * Checks if logging output with log <code>level</code> is possible and would get
	 * accepted by this <code>LogStreamWriter</code>.
	 * <p>
	 * Therefore this method also checks that the underlying output stream is opened.
	 * 
	 * @param level log level to check against
	 * @return true if log is permitted, false otherwise
	 */
	protected boolean logAllowed(LogLevel level)
	{
		if (out == null || level == LogLevel.OFF || level.higher(logLevel))
			return false;
		return true;
	}

	/**
	 * Creates a formatted output string from the input parameters.
	 * <p>
	 * Override this method to provide a different output format.<br>
	 * The output returned by default follows the shown format. The date/time format is
	 * according ISO 8601 representation, extended format with decimal fraction of second
	 * (milliseconds):<br>
	 * "YYYY-MM-DD hh:mm:ss,sss level=<code>level.toString()</code>,
	 * <code>logService</code>: <code>msg</code> (<code>t.getMessage()</code>)"<br>
	 * or, if throwable is <code>null</code> or throwable-message is <code>null</code><br>
	 * "YYYY-MM-DD hh:mm:ss,sss logService, LogLevel=<code>level.toString()</code>:
	 * <code>msg</code>".<br>
	 * If <code>logService</code> contains '.' in the name, only the part after the last
	 * '.' will be used. This way names like "package.subpackage.name" are shortened to
	 * "name". Nevertheless, if the first character after '.' is numeric, no truncation
	 * will be done to allow e.g. IP addresses in the log service name.
	 * 
	 * @param svc name of the log service the message comes from
	 * @param l log level of message and throwable
	 * @param msg message to format
	 * @param t an optional throwable object to format, might be <code>null</code>
	 * @return the formatted output
	 */
	protected String formatOutput(String svc, LogLevel l, String msg, Throwable t)
	{
		// ??? for more severe output levels, a formatting routine which
		// uses (part of) the throwable stack trace would be of advantage
		final StringBuffer b = new StringBuffer(150);
		synchronized (c) {
			c.setTimeInMillis(System.currentTimeMillis());
			b.append(c.get(Calendar.YEAR));
			b.append('-').append(pad2Digits(c.get(Calendar.MONTH) + 1));
			b.append('-').append(pad2Digits(c.get(Calendar.DAY_OF_MONTH)));
			b.append(' ').append(pad2Digits(c.get(Calendar.HOUR_OF_DAY)));
			b.append(':').append(pad2Digits(c.get(Calendar.MINUTE)));
			b.append(':').append(pad2Digits(c.get(Calendar.SECOND)));
			final int ms = c.get(Calendar.MILLISECOND);
			b.append(',');
			if (ms < 99)
				b.append('0');
			b.append(pad2Digits(ms));
		}
		b.append(" level=").append(l.toString());
		b.append(", ");
		final int dot = svc.lastIndexOf('.') + 1;
		if (dot > 0 && dot < svc.length() && Character.isDigit(svc.charAt(dot)))
			b.append(svc);
		else
			b.append(svc.substring(dot));
		b.append(": ").append(msg);
		if (t != null && t.getMessage() != null)
			b.append(" (").append(t.getMessage()).append(")");
		return b.toString();
	}

	void createWriter(OutputStream os)
	{
		setOutput(new BufferedWriter(new OutputStreamWriter(os), 512));
	}

	private synchronized void doWrite(String logService, LogLevel level, String msg,
		Throwable t)
	{
		if (logAllowed(level))
			try {
				out.write(formatOutput ? formatOutput(logService, level, msg, t) : msg);
				out.write(lineSep);
				if (autoFlush)
					out.flush();
			}
			catch (final Exception e) {
				// IOException and RuntimeException
				getErrorHandler().error(this, "on write", e);
			}
	}

	private static String pad2Digits(int i)
	{
		return i > 9 ? Integer.toString(i) : "0" + Integer.toString(i);
	}
}

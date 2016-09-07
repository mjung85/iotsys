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

package at.ac.tuwien.auto.calimero.process;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.Priority;
import at.ac.tuwien.auto.calimero.datapoint.Datapoint;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.exception.KNXInvalidResponseException;
import at.ac.tuwien.auto.calimero.exception.KNXTimeoutException;
import at.ac.tuwien.auto.calimero.link.KNXLinkClosedException;
import at.ac.tuwien.auto.calimero.link.KNXNetworkLink;

/**
 * Provides process communication with a KNX network.
 * <p>
 * The process communicator uses application layer group services for communication. Its
 * interface uses high level interaction based on Java data types and blocking read/write
 * functionality.
 * 
 * @author B. Malinowsky
 */
public interface ProcessCommunicator
{
	// Implementation note: responding to read indications is done the same way as
	// all the writing stuff just with different service, so no magic here...
	
	/**
	 * Represents "on" of datapoint type <b>Switch</b> (DPT ID 1.001), value =
	 * {@value #BOOL_ON}.
	 * <p>
	 * 
	 * @see #write(GroupAddress, boolean)
	 * @see #write(GroupAddress, boolean, byte)
	 */
	boolean BOOL_ON = true;

	/**
	 * Represents "off" of datapoint type <b>Switch</b> (DPT ID 1.001), value =
	 * {@value #BOOL_OFF}.
	 * <p>
	 * 
	 * @see #write(GroupAddress, boolean)
	 * @see #write(GroupAddress, boolean, byte)
	 */
	boolean BOOL_OFF = false;

	/**
	 * Represents "up" of datapoint type <b>Up/Down</b> (DPT ID 1.008), value =
	 * {@value #BOOL_UP}.
	 * <p>
	 * 
	 * @see #write(GroupAddress, boolean)
	 * @see #write(GroupAddress, boolean, byte)
	 */
	boolean BOOL_UP = false;

	/**
	 * Represents "down" of datapoint type <b>Up/Down</b> (DPT ID 1.008), value =
	 * {@value #BOOL_DOWN}.
	 * <p>
	 * 
	 * @see #write(GroupAddress, boolean)
	 * @see #write(GroupAddress, boolean, byte)
	 */
	boolean BOOL_DOWN = true;

	/**
	 * Represents "start" of datapoint type <b>Start</b> (DPT ID 1.010), value =
	 * {@value #BOOL_START}.
	 * <p>
	 * 
	 * @see #write(GroupAddress, boolean)
	 * @see #write(GroupAddress, boolean, byte)
	 */
	boolean BOOL_START = true;

	/**
	 * Represents "stop" of datapoint type <b>Start</b> (DPT ID 1.010), value =
	 * {@value #BOOL_STOP}.
	 * <p>
	 * 
	 * @see #write(GroupAddress, boolean)
	 * @see #write(GroupAddress, boolean, byte)
	 */
	boolean BOOL_STOP = false;

	/**
	 * Represents "increase" of datapoint type <b>Step</b> (DPT ID 1.007), value =
	 * {@value #BOOL_INCREASE}.
	 * <p>
	 * 
	 * @see #write(GroupAddress, boolean)
	 * @see #write(GroupAddress, boolean, byte)
	 */
	boolean BOOL_INCREASE = true;

	/**
	 * Represents "decrease" of datapoint type <b>Step</b> (DPT ID 1.007), value =
	 * {@value #BOOL_DECREASE}.
	 * <p>
	 * 
	 * @see #write(GroupAddress, boolean)
	 * @see #write(GroupAddress, boolean, byte)
	 */
	boolean BOOL_DECREASE = false;

	/**
	 * Represents the scaling format of datapoint type <b>Scaling</b> (DPT ID 5.001).
	 * <p>
	 * This format scales the 8 Bit unsigned value range from 0 to 100.
	 * 
	 * @see #readUnsigned(GroupAddress, String)
	 * @see #write(GroupAddress, int, String)
	 */
	String SCALING = "5.001";

	/**
	 * Represents the unscaled format, no scaling is used (like in datapoint types
	 * <b>Unsigned count</b> (DPT ID 5.010) or <b>Decimal factor</b> (DPT ID 5.005) ).
	 * <p>
	 * 
	 * @see #readUnsigned(GroupAddress, String)
	 * @see #write(GroupAddress, int, String)
	 */
	String UNSCALED = "5.010";

	/**
	 * Represents the scaling format of datapoint type <b>Angle</b> (DPT ID 5.003).
	 * <p>
	 * This format scales the 8 Bit unsigned value range from 0 to 360.
	 * 
	 * @see #readUnsigned(GroupAddress, String)
	 * @see #write(GroupAddress, int, String)
	 */
	String ANGLE = "5.003";

	/**
	 * Sets the response timeout to wait for a KNX response message to arrive to complete
	 * a message exchange.
	 * <p>
	 * 
	 * @param timeout time in seconds
	 */
	void setResponseTimeout(int timeout);

	/**
	 * Returns the response timeout used when waiting for a KNX response message to
	 * arrive.
	 * <p>
	 * 
	 * @return timeout in seconds
	 */
	int getResponseTimeout();

	/**
	 * Sets the KNX message priority for KNX messages to send.
	 * <p>
	 * 
	 * @param p new priority to use
	 */
	void setPriority(Priority p);

	/**
	 * Returns the current used KNX message priority for KNX messages.
	 * <p>
	 * 
	 * @return message Priority
	 */
	Priority getPriority();

	/**
	 * Adds the specified event listener <code>l</code> to receive events from this
	 * process communicator.
	 * <p>
	 * If <code>l</code> was already added as listener, no action is performed.
	 * 
	 * @param l the listener to add
	 */
	void addProcessListener(ProcessListener l);

	/**
	 * Removes the specified event listener <code>l</code>, so it does no longer
	 * receive events from this process communicator.
	 * <p>
	 * If <code>l</code> was not added in the first place, no action is performed.
	 * 
	 * @param l the listener to remove
	 */
	void removeProcessListener(ProcessListener l);

	/**
	 * Reads a boolean datapoint value from a group destination.
	 * <p>
	 * 
	 * @param dst group destination to read from
	 * @return the read value of type boolean
	 * @throws KNXTimeoutException on a timeout during send or no read response was
	 *         received
	 * @throws KNXInvalidResponseException on invalid read response message
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXFormatException on translation problem of the response data
	 * @throws KNXException on other read problems
	 */
	boolean readBool(GroupAddress dst) throws KNXException;

	/**
	 * Writes a boolean datapoint value to a group destination.
	 * <p>
	 * 
	 * @param dst group destination to write to
	 * @param value boolean value to write, consider the predefined BOOL_* constants (e.g.
	 *        {@link #BOOL_ON})
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 */
	void write(GroupAddress dst, boolean value) throws KNXTimeoutException,
		KNXLinkClosedException;

	/**
	 * Reads an unsigned 8 bit datapoint value from a group destination.
	 * <p>
	 * The predefined scaling format constants are equal to DPT identifiers of the 8 Bit
	 * DPT translator, any other suiting IDs of that type might be specified as well.
	 * 
	 * @param dst group destination to read from
	 * @param scale scaling of the read value before return, one of {@link #SCALING},
	 *        {@link #UNSCALED}, {@link #ANGLE}
	 * @return the read value of type unsigned byte
	 * @throws KNXTimeoutException on a timeout during send or no read response was
	 *         received
	 * @throws KNXInvalidResponseException on invalid read response message
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXFormatException on translation problem of the response data
	 * @throws KNXException on other read problems
	 */
	short readUnsigned(GroupAddress dst, String scale) throws KNXException;

	/**
	 * Writes a 8 bit unsigned datapoint value to a group destination.
	 * <p>
	 * The predefined scaling format constants are equal to DPT identifiers of the 8 Bit
	 * DPT translator, any other suiting IDs of that type might be specified as well.
	 * 
	 * @param dst group destination to write to
	 * @param value unsigned scaled value to write, 0 &lt;= value &lt;= scale format
	 *        specific upper value
	 * @param scale scaling of the read value before return, one of {@link #SCALING},
	 *        {@link #UNSCALED}, {@link #ANGLE}
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXFormatException on translation problem of the supplied datapoint value
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other write problems
	 */
	void write(GroupAddress dst, int value, String scale) throws KNXException;

	/**
	 * Reads a 3 Bit controlled datapoint value from a group destination.
	 * <p>
	 * The returned value is either positive or negative according to the read control
	 * information. For control bit orientation, the DPT <b>Dimming</b> (DPT ID 3.007) is
	 * used (i.e. control bit type <b>Step</b>). A control value of "decrease" results in
	 * a negative value return, a control value of "increase" results in a positive value
	 * return. The possible value range is -7 (decrease 7) to +7 (increase 7).
	 * 
	 * @param dst group destination to read from
	 * @return the read value of type 3 Bit controlled
	 * @throws KNXTimeoutException on a timeout during send or no read response was
	 *         received
	 * @throws KNXInvalidResponseException on invalid read response message
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXFormatException on translation problem of the response data
	 * @throws KNXException on other read problems
	 */
	byte readControl(GroupAddress dst) throws KNXException;

	/**
	 * Writes a 3 bit controlled datapoint value to a group destination.
	 * <p>
	 * 
	 * @param dst group destination to write to
	 * @param control control information, one of the predefined BOOL_* constants of DPT
	 *        <b>Step</b> and DPT <b>Up/Down</b>
	 * @param stepcode stepcode value, 0 &lt;= value &lt;= 7
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXFormatException on translation problem of the supplied datapoint value
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other write problems
	 */
	void write(GroupAddress dst, boolean control, byte stepcode) throws KNXException;

	/**
	 * Reads a 2 byte KNX float datapoint value from a group destination.
	 * <p>
	 * 
	 * @param dst group destination to read from
	 * @return the read value of type float
	 * @throws KNXTimeoutException on a timeout during send or no read response was
	 *         received
	 * @throws KNXInvalidResponseException on invalid read response message
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXFormatException on translation problem of the response data
	 * @throws KNXException on other read problems
	 */
	float readFloat(GroupAddress dst) throws KNXException;

	/**
	 * Writes a 2 byte KNX float datapoint value to a group destination.
	 * <p>
	 * 
	 * @param dst group destination to write to
	 * @param value float value to write
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXFormatException on translation problem of the supplied datapoint value
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other write problems
	 */
	void write(GroupAddress dst, float value) throws KNXException;


	/**
	 * Reads a 4 byte KNX unsigned int datapoint value from a group destination.
	 * <p>
	 * 
	 * @param dst
	 *            group destination to read from
	 * @return the read value of type float
	 * @throws KNXTimeoutException
	 *             on a timeout during send or no read response was received
	 * @throws KNXInvalidResponseException
	 *             on invalid read response message
	 * @throws KNXLinkClosedException
	 *             if network link to KNX network is closed
	 * @throws KNXFormatException
	 *             on translation problem of the response data
	 * @throws KNXException
	 *             on other read problems
	 */
	long readUint(GroupAddress dst) throws KNXException;

	/**
	 * Writes a 4 byte KNX unsigned int datapoint value to a group destination.
	 * <p>
	 * 
	 * @param dst
	 *            group destination to write to
	 * @param value
	 *            float value to write
	 * @throws KNXTimeoutException
	 *             on a timeout during send
	 * @throws KNXFormatException
	 *             on translation problem of the supplied datapoint value
	 * @throws KNXLinkClosedException
	 *             if network link to KNX network is closed
	 * @throws KNXException
	 *             on other write problems
	 */
	void write(GroupAddress dst, long value) throws KNXException;

	
	/**
	 * Reads a string datapoint value from a group destination.
	 * <p>
	 * The supported character set covers at least ISO-8859-1 (Latin 1), with an allowed
	 * string length of 14 characters.
	 * 
	 * @param dst group destination to read from
	 * @return the read value of type string
	 * @throws KNXTimeoutException on a timeout during send or no read response was
	 *         received
	 * @throws KNXInvalidResponseException on invalid read response message
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXFormatException on translation problem of the response data
	 * @throws KNXException on other read problems
	 */
	String readString(GroupAddress dst) throws KNXException;

	/**
	 * Writes a string datapoint value to a group destination.
	 * <p>
	 * The supported character set covers at least ISO-8859-1 (Latin 1), with an allowed
	 * string length of 14 characters.
	 * 
	 * @param dst group destination to write to
	 * @param value string value to write
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXFormatException on translation problem of the supplied datapoint value
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other write problems
	 */
	void write(GroupAddress dst, String value) throws KNXException;

	/**
	 * Reads a datapoint value from a group destination.
	 * <p>
	 * The used KNX message priority is according the supplied datapoint priority.
	 * 
	 * @param dp the datapoint for read
	 * @return the read value in textual representation according the datapoint its type
	 * @throws KNXTimeoutException on a timeout during send or no read response was
	 *         received
	 * @throws KNXInvalidResponseException on invalid read response message
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXFormatException on translation problem of the response data
	 * @throws KNXException if no appropriate DPT translator for the datapoint type is
	 *         available
	 */
	String read(Datapoint dp) throws KNXException;

	/**
	 * Writes a datapoint value to a group destination.
	 * <p>
	 * The used KNX message priority is according the supplied datapoint priority.
	 * 
	 * @param dp the datapoint for write
	 * @param value datapoint value in textual representation according the datapoint its
	 *        type
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXFormatException on translation problem of the supplied datapoint value
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException if no appropriate DPT translator for the datapoint type is
	 *         available
	 */
	void write(Datapoint dp, String value) throws KNXException;

	/**
	 * Detaches the network link from this process communicator.
	 * <p>
	 * If no network link is attached, no action is performed.
	 * <p>
	 * Note that a detach does not trigger a close of the used network link.
	 * 
	 * @return the formerly attached KNX network link, or <code>null</code> if already
	 *         detached
	 */
	KNXNetworkLink detach();
}

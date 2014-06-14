/*
  	Copyright (c) 2013 - IotSyS KNX Connector
 	Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
  	All rights reserved.

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
package at.ac.tuwien.auto.iotsys.commons.persistent.models;

import org.ektorp.support.CouchDbDocument;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class Device extends CouchDbDocument {

	private String connectorId;
	private String type;
	private String ipv6;
	private String address;
	private String href;
	private String name;
	private String displayName;
	private int historyCount;
	private boolean historyEnabled;
	private boolean groupcommEnabled;
	private boolean refreshEnabled;
	
	public Device(){}
	
	public Device(String type2, String ipv62, String address2, String href2,
			String name2, String displayName2, Integer historyCount2,
			boolean historyEnabled2, boolean groupCommEnabled2,
			boolean refreshEnabled2) {
		this.type = type2;
		this.ipv6 = ipv62;
		this.address = address2;
		this.href = href2;
		this.name = name2;
		this.displayName = displayName2;
		this.historyCount = historyCount2;
		this.historyEnabled = historyEnabled2;
		this.groupcommEnabled = groupCommEnabled2;
		this.refreshEnabled = refreshEnabled2;
	}
	public Device(String type2, String ipv62, String addressString,
			String href2, String name2, Integer historyCount2,
			boolean historyEnabled2, boolean groupCommEnabled2) {
		this(type2, ipv62, addressString, href2, name2, "", historyCount2, historyEnabled2, groupCommEnabled2, false);
	}

	public String getConnectorId() {
		return connectorId;
	}
	public void setConnectorId(String connectorId) {
		this.connectorId = connectorId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIpv6() {
		return ipv6;
	}
	public void setIpv6(String ipv6) {
		this.ipv6 = ipv6;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public int getHistoryCount() {
		return historyCount;
	}
	public void setHistoryCount(int historyCount) {
		this.historyCount = historyCount;
	}
	public boolean isHistoryEnabled() {
		return historyEnabled;
	}
	public void setHistoryEnabled(boolean historyEnabled) {
		this.historyEnabled = historyEnabled;
	}
	public boolean isGroupcommEnabled() {
		return groupcommEnabled;
	}
	public void setGroupcommEnabled(boolean groupcommEnabled) {
		this.groupcommEnabled = groupcommEnabled;
	}
	public boolean isRefreshEnabled() {
		return refreshEnabled;
	}
	public void setRefreshEnabled(boolean refreshEnabled) {
		this.refreshEnabled = refreshEnabled;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}	
	
}

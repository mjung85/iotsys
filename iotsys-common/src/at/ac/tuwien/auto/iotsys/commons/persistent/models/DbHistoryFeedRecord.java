/*
  	Copyright (c) 2013 - IotSyS Gateway
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
public class DbHistoryFeedRecord extends CouchDbDocument{
	private String href;
	private Object val;
	private String type;
	private long time;
	
	public DbHistoryFeedRecord(){}
	
	public DbHistoryFeedRecord(String href, long time, String type, String val) {
		this.href = href;
		this.type = type;
		this.time = time;

		switch (type) {
		case "int":
			this.val = new Integer(val);
			break;
		case "real":
			this.val = new Double(val);
			break;
		case "str":
			this.val = new String(val);
			break;
		case "bool":
			this.val = new Boolean(val);
			break;
		default:
			break;
		}
	}
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public Object getVal() {
		return val;
	}
	public void setVal(Object val) {
		this.val = val;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}

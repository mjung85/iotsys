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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class WriteableObject extends CouchDbDocument {

	String href;
	String dataStream;
	
	public WriteableObject(){}
	public WriteableObject(String href){
		this.href = href;
		setId(href);
	}
	public WriteableObject(String href, String dataStream){
		this(href);
		this.dataStream = dataStream;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getDataStream() {
		return dataStream;
	}

	public void setDataStream(String dataStream) {
		this.dataStream = dataStream;
	}
}

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
public class User extends CouchDbDocument {
	
	@JsonProperty("_id")
	String name;
	String password;
	String role;
	
	public User(){}
	
	public User(String n, String p, String r){
		this.name = n;
		this.password = p;
		this.role = r;
	}
	
	@JsonProperty("_id")
	public String getName() {
		return name;
	}
	@JsonProperty("_id")
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
}

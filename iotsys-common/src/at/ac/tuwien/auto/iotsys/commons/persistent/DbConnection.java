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
package at.ac.tuwien.auto.iotsys.commons.persistent;

import java.net.MalformedURLException;

import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
public class DbConnection {

	private static DbConnection INSTANCE;
	private static StdCouchDbInstance couchInstance;

	private DbConnection() throws MalformedURLException {
		HttpClient couchHttpClient = new StdHttpClient.Builder().url(
				"http://127.0.0.1:5984").build();
		couchInstance = new StdCouchDbInstance(couchHttpClient);
	}

	static {
		try {
			INSTANCE = new DbConnection();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static StdCouchDbInstance getCouchInstance() {
		return couchInstance;
	}

}

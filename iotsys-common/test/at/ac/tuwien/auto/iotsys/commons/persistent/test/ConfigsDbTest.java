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
package at.ac.tuwien.auto.iotsys.commons.persistent.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.tuwien.auto.iotsys.commons.persistent.ConfigsDb;
import at.ac.tuwien.auto.iotsys.commons.persistent.ConfigsDbImpl;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Device;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class ConfigsDbTest {

	static ConfigsDb cd;
	
	@BeforeClass
	public static void setUp() {
		cd = ConfigsDbImpl.getInstance();
	}
	
	@Test
	public void testGetConnectorsByTechnology(){
		List<JsonNode> result = cd.getConnectors("virtual"); 
		for (JsonNode j : result)
			System.out.println(j.get("name"));
	}

	
	@Test
	public void testGetConnector(){
		JsonNode j = cd.getConnectorByName("BACnet A-Lab");
		Assert.assertNotNull(j);
	}
	
	@Test
	public void testCountConnectorsByTechnology(){
		int count = cd.countConnectorsByTechnology("virtual");
		System.out.println(count);
	}

	@Test
	public void testAddDevice(){
		Device d = new Device();
		d.setConnectorId("58f42eb853accab012314419c90b08e1");
		d.setName("CouchDB Test Device");
		d.setHref("CouchDbTestDeviceHref");
		cd.addDevice(d);
	}
	
	@Test
	public void testUpdateDevice(){
		Device d = new Device();
		d.setId("58f42eb853accab012314419c90f261c");
		d.setRevision("2-c8ad3c8399a43c0b6ce8bb55426430a0");
		d.setName("CouchDB Test Device 2");
		d.setHref("CouchDbTestDeviceHref2");
		cd.updateDevice(d);
	}

	@Test
	public void testDeleteDevice(){
		Device d = new Device();
		d.setId("58f42eb853accab012314419c90f261c");
		cd.deleteDevice(d);
	}

	@Test
	public void testAddDeviceLoader(){
		String dloader = "at.ac.tuwien.auto.iotsys.gateway.connector.ttttttttttt";
		cd.addDeviceLoader(dloader);
	}
	
	@Test
	public void testUpdateDeviceLoader(){
		cd.updateDeviceLoader("at.ac.tuwien.auto.iotsys.gateway.connector.ttttttttttt", "-------------");
	}

	@Test
	public void testDeleteDeviceLoader(){
		cd.deleteDeviceLoader("-------------");;
	}
	
}

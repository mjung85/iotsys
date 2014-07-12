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

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.tuwien.auto.iotsys.commons.persistent.ConfigsDb;
import at.ac.tuwien.auto.iotsys.commons.persistent.ConfigsDbImpl;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class ConfigsDbTest {

	static ConfigsDb cd;
	
	@BeforeClass
	public static void setUp() {
		cd = ConfigsDbImpl.getInstance();
		org.junit.Assume.assumeTrue(cd != null);
	}
	
	@Test
	public void testCrudDeviceLoader(){
		
		String dloader = "at.ac.tuwien.auto.iotsys.gateway.connector.ttttttttttt";
		String dloaderToUpdate = "at.ac.tuwien.auto.iotsys.gateway.connector.eeeeeeeeee";
		
		try {
			cd.addDeviceLoader(dloader);
		} catch (Exception e) {
			System.out.println("Exception in adding device loader, probably overwriting the old one with an ADD");
		}
		
		assertTrue(cd.getDeviceLoader(dloader) > -1);
		
		cd.updateDeviceLoader(dloader, dloaderToUpdate);
		
		assertTrue(cd.getDeviceLoader(dloaderToUpdate) > -1);
		
		cd.deleteDeviceLoader(dloaderToUpdate);
		
		assertTrue(cd.getDeviceLoader(dloaderToUpdate) == -1);
		assertTrue(cd.getDeviceLoader(dloader) == -1);
	}
}

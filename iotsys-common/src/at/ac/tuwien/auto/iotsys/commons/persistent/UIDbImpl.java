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
package at.ac.tuwien.auto.iotsys.commons.persistent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.User;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class UIDbImpl implements UIDb {

	private static UIDb INSTANCE;
	private static final Logger log = Logger.getLogger(UIDbImpl.class.getName());

	public static UIDb getInstance(){
		INSTANCE = UIDbRepo.getInstance(); 
		if (INSTANCE == null)
			INSTANCE = new UIDbImpl();
		return INSTANCE;
	}

	@Override
	public String getValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateBulkKeyValue(Map<String, String> uiKeyValues) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User getUser(String name) {
		// TODO Auto-generated method stub
		return new User(name, "password", "admin");
	}

	@Override
	public void addUser(User u) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteUser(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateUser(String name, User u) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getUiStorage() {
		Map<String, String> sample = new HashMap<String, String>();
		sample.put("_id", "uikeyval");
		sample.put("device_name", "awefjawei");
		return sample;
	}

	@Override
	public boolean authenticateUser(String name, String password) {
		// DB not found, proceed with bootstrap account iotsys/s3cret
		String bootstrapUser = PropertiesLoader.getInstance().getProperties().getProperty("iotsys.gateway.security.bootstrapUser", "iotsys");
		String bootstrapPassword = PropertiesLoader.getInstance().getProperties().getProperty("iotsys.gateway.security.bootstrapPassword", "s3cret");
		
		if (!name.equals(bootstrapUser))
			return false;
		if (!password.equals(bootstrapPassword))
			return false;
		return true;
	}
}

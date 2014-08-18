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

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.map.HashedMap;
import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.ektorp.support.Views;

import at.ac.tuwien.auto.iotsys.commons.persistent.models.User;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
@Views({
	@View(name = "allKeyValues", map = "function(doc) {if (doc.key) emit(doc.key, doc);}"),
	@View(name = "allUsers", map = "function(doc) {if (doc.name && doc.password) emit(doc.name, doc);}"),
})
public class UIDbRepo extends CouchDbRepositorySupport<User> implements UIDb {
	
	private static UIDbRepo INSTANCE;
	private static final Logger log = Logger.getLogger(UIDbRepo.class.getName());
	
	private Map<String, String> uiStorageMap = new HashMap<String, String>();
	
	protected UIDbRepo(CouchDbConnector db) {
		super(User.class, db);
		initStandardDesignDocument();
		
		loadAllKeyValues();
	}
	
	public static UIDb getInstance(){
		if (INSTANCE == null){ 
			CouchDbConnector db = new StdCouchDbConnector("uidb", DbConnection.getCouchInstance());
			try {
				INSTANCE = new UIDbRepo(db);
			} catch (Exception e) {
				log.severe("FATAL: UI DB not connected!");
			}
		}
		return INSTANCE;
	}

	private void loadAllKeyValues(){
		try {
			uiStorageMap = db.get(Map.class, "uistorage");
		} catch (Exception e){
			uiStorageMap.put("_id", "uistorage");
		}
	}

	@Override
	public String getValue(String key) {
		return uiStorageMap.get(key);
	}

	@Override
	public void updateBulkKeyValue(Map<String, String> uiKeyValues) {
		// Clean input
		uiKeyValues.remove("_id");
		uiKeyValues.remove("_rev");
		// Check existing
		loadAllKeyValues();
		if (uiStorageMap.get("_rev") != null){
			uiKeyValues.put("_id", uiStorageMap.get("_id"));
			uiKeyValues.put("_rev", uiStorageMap.get("_rev"));
			db.update(uiKeyValues);
			uiStorageMap = uiKeyValues;
		} else {
			// create new
			uiKeyValues.put("_id", "uistorage");
			db.update(uiKeyValues);
			uiStorageMap = uiKeyValues;
		}
	}

	@Override
	public User getUser(String name) {
		User u = null;
		try {
			u = db.get(User.class, name);
		} catch(Exception e){}
		return u;
	}

	@Override
	public void addUser(User u) {
		byte[] salt = new byte[16];
		(new Random()).nextBytes(salt);
		KeySpec spec = new PBEKeySpec(u.getPassword().toCharArray(), salt, 65536, 128);
		SecretKeyFactory f;
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = f.generateSecret(spec).getEncoded();
			String saltStr = new BigInteger(1, salt).toString(16);
			String hasedPassword = new BigInteger(1, hash).toString(16);
			u.setSalt(saltStr);
			u.setPassword(hasedPassword);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.create(u);
	}

	@Override
	public void deleteUser(String name) {
		User u = getUser(name);
		db.delete(u);
	}

	@Override
	public void updateUser(String name, User u) {
		User oldU = getUser(name);
		oldU.setPassword(u.getPassword());
		oldU.setRole(u.getRole());
		db.update(oldU);
	}

	@Override
	public Map<String, String> getUiStorage() {
		uiStorageMap.remove("_rev");
		uiStorageMap.remove("_id");
		return uiStorageMap;
	}

	@Override
	public boolean authenticateUser(String name, String plainPassword) {
		User u = getUser(name);
		
		byte[] salt = new BigInteger(u.getSalt(), 16).toByteArray();
		KeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, 65536, 128);
		SecretKeyFactory f;
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = f.generateSecret(spec).getEncoded();
			String hasedPassword = new BigInteger(1, hash).toString(16);
			return hasedPassword.equals(u.getPassword()) ? true : false;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return false;
	}
}

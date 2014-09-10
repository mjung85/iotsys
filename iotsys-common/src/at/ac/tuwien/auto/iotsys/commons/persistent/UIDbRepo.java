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

import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.ektorp.support.Views;

import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
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
		
		// Instantiate bootstrap user account?
		String bootstrapUser = PropertiesLoader.getInstance().getProperties().getProperty("iotsys.gateway.security.bootstrapUser", "iotsys");
		String bootstrapPassword = PropertiesLoader.getInstance().getProperties().getProperty("iotsys.gateway.security.bootstrapPassword", "s3cret");
		
		User u = getUser(bootstrapUser);
		if (u != null)
			return;
		
		u = new User(bootstrapUser, bootstrapPassword, "admin");
		addUser(u);
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
		} catch(Exception e){
			e.printStackTrace();
		}
		return u;
	}

	private class SaltHash{
		String salt;
		String hash;
		public SaltHash(String salt, String hash){
			this.salt = salt;
			this.hash = hash;
		}
		public String getSalt() {
			return salt;
		}
		public void setSalt(String salt) {
			this.salt = salt;
		}
		public String getHash() {
			return hash;
		}
		public void setHash(String hash) {
			this.hash = hash;
		}
		
	}
	
	private String hashGen(String rawPassword, String salt) {
		byte[] saltB = new BigInteger(salt, 16).toByteArray();
		// calculate the hash
		KeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), saltB, 65536,
				128);
		SecretKeyFactory f;
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = f.generateSecret(spec).getEncoded();
			String hasedPassword = new BigInteger(1, hash).toString(16);
			return hasedPassword;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private SaltHash saltHashGen(String rawPassword){
		byte[] salt = new byte[16];
		(new Random()).nextBytes(salt);
		KeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, 65536, 128);
		SecretKeyFactory f;
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = f.generateSecret(spec).getEncoded();
			String saltStr = new BigInteger(1, salt).toString(16);
			String hasedPassword = new BigInteger(1, hash).toString(16);
			return new SaltHash(saltStr, hasedPassword);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void addUser(User u) {
		SaltHash sh = saltHashGen(u.getPassword());
		u.setSalt(sh.getSalt());
		u.setPassword(sh.getHash());
		try {
			db.create(u);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void deleteUser(String name) {
		User u = getUser(name);
		db.delete(u);
	}

	@Override
	public void updateUser(String name, User u) {
		User oldU = getUser(name);
		System.out.println(oldU.toString());
		SaltHash sh = saltHashGen(u.getPassword());
		oldU.setPassword(sh.getHash());
		oldU.setSalt(sh.getSalt());
		oldU.setRole(u.getRole());
		try {
			System.out.println(oldU.toString());
			db.update(oldU);
		} catch (Exception e){
			e.printStackTrace();
		}
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
		
		if (u == null)
			return false;
		
		System.out.println(u.toString());
		// retrieve the stored salt
		byte[] salt = new BigInteger(u.getSalt(), 16).toByteArray();
		if (salt[0] == 0) {
		    byte[] tmp = new byte[salt.length - 1];
		    System.arraycopy(salt, 1, tmp, 0, tmp.length);
		    salt = tmp;
		}
		// calculate the hash
		KeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, 65536, 128);
		SecretKeyFactory f;
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = f.generateSecret(spec).getEncoded();
			String hasedPassword = new BigInteger(1, hash).toString(16);
			System.out.println(plainPassword + "\t" + hasedPassword);
			return hasedPassword.equals(u.getPassword()) ? true : false;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return false;
	}
}

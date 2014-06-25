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

import java.util.List;
import java.util.logging.Logger;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

import at.ac.tuwien.auto.iotsys.commons.persistent.models.WritableObject;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class WriteableObjectDbImpl extends CouchDbRepositorySupport<WritableObject> implements
		WritableObjectDb {

	private static WriteableObjectDbImpl INSTANCE;
	private List<WritableObject> writtenObjects;
	private static final Logger log = Logger.getLogger(WriteableObjectDbImpl.class.getName());

	protected WriteableObjectDbImpl(CouchDbConnector db) {
		super(WritableObject.class, db);
		initStandardDesignDocument();
		writtenObjects = getPersistedObjects();
	}
	
	public static WritableObjectDb getInstance(){
		if (INSTANCE == null){ 
			CouchDbConnector db = new StdCouchDbConnector("writableObjects", DbConnection.getCouchInstance());
			try {
				INSTANCE = new WriteableObjectDbImpl(db);
			} catch (Exception e) {
				log.severe("FATAL: Writable objects DB not connected!");
			}
		}
		return INSTANCE;
	}

	@Override
	public void persistWritingObject(String href, String dataStream) {
		WritableObject wo;
		try {
			wo = get(href);
		} catch (DocumentNotFoundException e){
			wo = new WritableObject(href);
		}
		wo.setDataStream(dataStream);
		update(wo);
	}

	@Override
	public String getObjectDataStream(String href) {
		WritableObject wo = get(href);
		return wo.getDataStream();
	}

	@Override
	public List<WritableObject> getPersistedObjects() {
		List<WritableObject> result = getAll();
		return result;
	}


	@Override
	public WritableObject getPersistedObject(String href) {
		for (WritableObject wo : writtenObjects){
			if (wo.getHref().equals(href))
				return wo;
		}
		return null;
	}

	@Override
	public void compactDb() {
		db.compact();
	}
}

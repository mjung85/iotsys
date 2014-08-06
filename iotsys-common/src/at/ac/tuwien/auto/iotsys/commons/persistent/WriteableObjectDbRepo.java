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

import at.ac.tuwien.auto.iotsys.commons.persistent.models.WriteableObject;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class WriteableObjectDbRepo extends CouchDbRepositorySupport<WriteableObject> implements
		WriteableObjectDb {

	private static WriteableObjectDbRepo INSTANCE;
	private List<WriteableObject> writtenObjects;
	private static final Logger log = Logger.getLogger(WriteableObjectDbRepo.class.getName());

	protected WriteableObjectDbRepo(CouchDbConnector db) {
		super(WriteableObject.class, db);
		initStandardDesignDocument();
		writtenObjects = getPersistedObjects();
	}
	
	public static WriteableObjectDb getInstance(){
		if (INSTANCE == null){ 
			CouchDbConnector db = new StdCouchDbConnector("writableObjects", DbConnection.getCouchInstance());
			try {
				INSTANCE = new WriteableObjectDbRepo(db);
			} catch (Exception e) {
				log.severe("FATAL: Writable objects DB not connected!");
			}
		}
		return INSTANCE;
	}

	@Override
	public void persistWritingObject(String href, String dataStream) {
		WriteableObject wo;
		try {
			wo = get(href);
		} catch (DocumentNotFoundException e){
			wo = new WriteableObject(href);
		}
		wo.setDataStream(dataStream);
		update(wo);
	}

	@Override
	public String getObjectDataStream(String href) {
		WriteableObject wo = get(href);
		return wo.getDataStream();
	}

	@Override
	public List<WriteableObject> getPersistedObjects() {
		List<WriteableObject> result = getAll();
		return result;
	}


	@Override
	public WriteableObject getPersistedObject(String href) {
		for (WriteableObject wo : writtenObjects){
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

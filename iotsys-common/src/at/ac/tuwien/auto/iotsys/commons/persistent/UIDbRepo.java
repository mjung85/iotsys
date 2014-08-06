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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.support.*;
import org.ektorp.util.Assert;

import at.ac.tuwien.auto.iotsys.commons.persistent.models.CanvasObject;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
@Views({
	@View(name = "allCanvasObjects", map = "function(doc) {if (doc.technology) emit(doc.name, doc);}"),
	@View(name = "allConnections", map = "function(doc) {if (doc.href) emit(doc.href, doc);}"),
})
public class UIDbRepo extends CouchDbRepositorySupport<CanvasObject> implements UIDb {
	
	private static UIDbRepo INSTANCE;
	private static final Logger log = Logger.getLogger(UIDbRepo.class.getName());
	
	private List<CanvasObject> allCanvasObjects = new ArrayList<CanvasObject>();
	
	protected UIDbRepo(CouchDbConnector db) {
		super(CanvasObject.class, db);
		initStandardDesignDocument();
		
		loadAllCanvasObjects();
	}

	private void loadAllCanvasObjects(){
		ViewQuery query = new ViewQuery().designDocId("_design/Connector")
				.viewName("allCanvasObjects");
		allCanvasObjects = db.queryView(query, CanvasObject.class);
	}
	
	public static UIDb getInstance(){
		if (INSTANCE == null){ 
			CouchDbConnector db = new StdCouchDbConnector("canvasobjects", DbConnection.getCouchInstance());
			try {
				INSTANCE = new UIDbRepo(db);
			} catch (Exception e) {
				log.severe("FATAL: Canvas Object DB not connected!");
			}
		}
		return INSTANCE;
	}

	@Override
	public List<CanvasObject> getCanvasObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteCanvasObject(String uri) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCanvasObject(String uri, CanvasObject co) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCanvasObject(CanvasObject co) {
		Assert.hasText(co.getObjUri(), "A canvas object must have a uri");
		add(co);
	}

	@Override
	public void addCanvasObjects(List<CanvasObject> cos) {
		for (CanvasObject co : cos)
			addCanvasObject(co);
	}
	
	@GenerateView @Override
	public List<CanvasObject> getAll() {
		ViewQuery q = createQuery("all")
						.descending(true)
						.includeDocs(true);
		return db.queryView(q, CanvasObject.class);
	}

}

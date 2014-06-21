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

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.Page;
import org.ektorp.PageRequest;
import org.ektorp.ViewQuery;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.ektorp.support.Views;
import org.ektorp.util.Assert;

import at.ac.tuwien.auto.iotsys.commons.persistent.models.DbHistoryFeed;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
@Views({
	@View(name = "by_href", map = "function(doc) {emit(doc.href, doc.val);}"),
	@View(name = "by_time_href", map = "function(doc) {emit([doc.href, doc.time], doc.val);}")
})
public class HistoryDbImpl extends CouchDbRepositorySupport<DbHistoryFeed> implements HistoryDb {

	private static HistoryDbImpl INSTANCE;
	private ObjectMapper om = new ObjectMapper();

	private static final Logger log = Logger.getLogger(HistoryDbImpl.class.getName());

	protected HistoryDbImpl(CouchDbConnector db) {
		super(DbHistoryFeed.class, db);
		initStandardDesignDocument();
	}
	
	public static HistoryDb getInstance(){
		if (INSTANCE == null){ 
			CouchDbConnector db = new StdCouchDbConnector("historyFeeds", DbConnection.getCouchInstance());
			try {
				INSTANCE = new HistoryDbImpl(db);
			} catch (Exception e) {
				log.severe("FATAL: Objects DB not connected!");
			}
		}
		return INSTANCE;
	}

	public List<DbHistoryFeed> findByHref(String href) {
		return queryView("by_href", href);
	}
	
	@Override
	public DbHistoryFeed getObject(String href) {
		return get(href);
	}

	@Override
	public void addObject(DbHistoryFeed hf) {
		Assert.hasText(hf.getHref(), "A datapoint must have a href");
		add(hf);
	}

	@Override
	public void deleteObject(String href) {
		DbHistoryFeed p = getObject(href);
		remove(p);
	}

//	@Override
//	public void updateObject(DbHistoryFeed newp) {
//		Assert.hasText(newp.getId(), "A datapoint must have a id");
//		update(newp);		
//	}

	@Override
	public Page<DbHistoryFeed> getLatestHistoryFeed(String href, long number) {
		ComplexKey startKey = ComplexKey.of(href);
		ComplexKey endKey = ComplexKey.of(href, ComplexKey.emptyObject());
		ViewQuery q = createQuery("by_time_href").includeDocs(true).startKey(startKey).endKey(endKey);
		System.out.println(q.buildQuery());
		PageRequest pr = org.ektorp.PageRequest.firstPage((int) number);
		return db.queryForPage(q, pr, DbHistoryFeed.class);
	}

	@Override
	public void addBulkObject(List<DbHistoryFeed> dhfs) {
		db.executeAllOrNothing(dhfs);
	}
}

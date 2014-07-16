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

import at.ac.tuwien.auto.iotsys.commons.persistent.models.DbHistoryFeedRecord;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class HistoryDbImpl implements HistoryDb {

	private static final Logger log = Logger.getLogger(HistoryDbImpl.class.getName());
	private static HistoryDb INSTANCE;
	
	public static HistoryDb getInstance(){
		INSTANCE = HistoryDbRepo.getInstance(); 
		if (INSTANCE == null)
			INSTANCE = new HistoryDbImpl();
		return INSTANCE;
	}
	
	@Override
	public DbHistoryFeedRecord getObject(String href) {
		return null;
	}

	@Override
	public List<DbHistoryFeedRecord> getLatestHistoryFeed(String href,
			int number) {
		return new ArrayList<DbHistoryFeedRecord>();
	}

	@Override
	public void addObject(DbHistoryFeedRecord dhf) {
		log.severe("HISTORY DB NOT CONNECTED");
	}

	@Override
	public void addBulkFeedRecords(List<DbHistoryFeedRecord> dhfs) {
		log.severe("HISTORY DB NOT CONNECTED");
	}

	@Override
	public void deleteObject(String href) {
		log.severe("HISTORY DB NOT CONNECTED");
	}

	@Override
	public List<DbHistoryFeedRecord> getHistoryFeed(String href, long start,
			long end, int limit) {
		return new ArrayList<DbHistoryFeedRecord>();
	}

	@Override
	public void compactDb() {
		log.severe("HISTORY DB NOT CONNECTED");
	}

}

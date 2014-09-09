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

import at.ac.tuwien.auto.iotsys.commons.persistent.models.GroupCommunication;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public interface GroupCommDb {

	public GroupCommunication getGroupComm(String href);
	public void addGroupComm(GroupCommunication gc);
	public void deleteGroupComm(String href);
	public void updateGroupComm(GroupCommunication gc);
	
	public List<String> getGroupCommAddress(String href);
	public void addGroupCommAddress(String href, String address);
	public void deleteGroupCommAddress(String href, String address);
	public void updateGroupCommAddress(String href, String oldAddress, String newAddress);
}

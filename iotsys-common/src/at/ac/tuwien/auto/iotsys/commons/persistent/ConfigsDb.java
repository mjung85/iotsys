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
package at.ac.tuwien.auto.iotsys.commons.persistent;

import java.util.List;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.Device;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public interface ConfigsDb {

	public List<JsonNode> getAllConnectors();
	public List<JsonNode> getConnectors(String technology);
	public JsonNode getConnector(String connectorName);
	public int countConnectors();
	public int countConnectorsByTechnology(String technology);
	public void addConnector(Connector c);
	public void addConnectors(List<Connector> cs);
	public void updateConnector(Connector c);
	public void deleteConnector(Connector c);
	public void deleteConnector(String connectorName);
	public void deleteAllConnectors(String technology);
	
	public List<Device> getAllDevices();
	public List<Device> getDevices(String technology);
	public Device getDevice(String id);
	public int countDevices();
	public int countDevicesByTechnology(String technology);
	public void addDevice(Device d);
	public void addDevices(List<Device> ds);
	public void updateDevice(Device d);
	public void deleteDevice(Device d);
	public void deleteDevice(String id);
	public void deleteAllDevices(String connectorName);
	
	public String getDeviceLoader(int no);
	public String[] getAllDeviceLoader();
	public void addDeviceLoader(String deviceLoader);
	public void addDeviceLoaders(List<String> ds);
	public void deleteDeviceLoader(int no);
	public void deleteDeviceLoader(String deviceLoader);
	public void deleteAllDeviceLoader();
	public void updateDeviceLoader(int no, String deviceLoader);
	public void updateDeviceLoader(String oldDeviceLoader, String newDeviceLoader);
	
	public void clear();
}

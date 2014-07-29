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

import org.ektorp.DocumentOperationResult;

import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Device;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class ConfigsDbImpl implements ConfigsDb {

	private static final Logger log = Logger.getLogger(ConfigsDbImpl.class.getName());
	private static ConfigsDb INSTANCE;
	
	public static ConfigsDb getInstance(){
		INSTANCE = ConfigsDbRepo.getInstance(); 
		if (INSTANCE == null)
			INSTANCE = new ConfigsDbImpl();
		return INSTANCE;
	}
	
	@Override
	public List<JsonNode> getAllConnectors() {
		log.severe("CONFIGS DB NOT CONNECTED");
		return new ArrayList<JsonNode>();
	}

	@Override
	public List<JsonNode> getConnectors(String technology) {
		log.severe("CONFIGS DB NOT CONNECTED");
		return new ArrayList<JsonNode>();
	}

	@Override
	public JsonNode getConnector(String connectorId) {
		log.severe("CONFIGS DB NOT CONNECTED");
		return null;
	}

	@Override
	public JsonNode getConnectorByName(String connectorName) {
		log.severe("CONFIGS DB NOT CONNECTED");
		return null;
	}

	@Override
	public int countConnectors() {
		log.severe("CONFIGS DB NOT CONNECTED");
		return 0;
	}

	@Override
	public int countConnectorsByTechnology(String technology) {
		log.severe("CONFIGS DB NOT CONNECTED");
		return 0;
	}

	@Override
	public void addConnector(Connector c) throws Exception {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public List<DocumentOperationResult> addBulkConnectors(List<Connector> cs)
			throws Exception {
		log.severe("CONFIGS DB NOT CONNECTED");
		return new ArrayList<DocumentOperationResult>();
	}

	@Override
	public void updateConnector(Connector c) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void deleteConnector(Connector c) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void deleteConnector(String connectorName) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void deleteAllConnectors(String technology) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public List<Device> getAllDevices() {
		log.severe("CONFIGS DB NOT CONNECTED");
		return new ArrayList<Device>();
	}

	@Override
	public List<Device> getDevices(String connectorId) {
		log.severe("CONFIGS DB NOT CONNECTED");
		return new ArrayList<Device>();
	}

	@Override
	public Device getDevice(String id) {
		log.severe("CONFIGS DB NOT CONNECTED");
		return null;
	}

	@Override
	public int countDevices() {
		log.severe("CONFIGS DB NOT CONNECTED");
		return 0;
	}

	@Override
	public int countDevicesByTechnology(String technology) {
		log.severe("CONFIGS DB NOT CONNECTED");
		return 0;
	}

	@Override
	public void addDevice(Device d) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void addBulkDevices(List<Device> ds) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void updateDevice(Device d) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void deleteDevice(Device d) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void deleteDevice(String id) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void deleteAllDevices(String connectorName) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public String getDeviceLoader(int no) {
		log.severe("CONFIGS DB NOT CONNECTED");
		return null;
	}

	@Override
	public int getDeviceLoader(String name) {
		log.severe("CONFIGS DB NOT CONNECTED");
		return 0;
	}

	@Override
	public String[] getAllDeviceLoader() {
		log.severe("CONFIGS DB NOT CONNECTED");
		return new String[0];
	}

	@Override
	public void addDeviceLoader(String deviceLoader) throws Exception {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void addBulkDeviceLoaders(List<String> ds) throws Exception {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void deleteDeviceLoader(int no) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void deleteDeviceLoader(String deviceLoader) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void deleteAllDeviceLoader() {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void updateDeviceLoader(int no, String deviceLoader) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void updateDeviceLoader(String oldDeviceLoader,
			String newDeviceLoader) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void clear() {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void prepareDevice(String connectorName, Device d) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void prepareDeviceLoader(String deviceLoaderName) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void prepareConnectors(List<Connector> connectors) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void migrate(List<Connector> connectors) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void migrate() {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public boolean isMigrating() {
		log.severe("CONFIGS DB NOT CONNECTED");
		return false;
	}

	@Override
	public void setMigrating(boolean migrating) {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

	@Override
	public void compactDb() {
		log.severe("CONFIGS DB NOT CONNECTED");

	}

}

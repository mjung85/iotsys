/*
  	Copyright (c) 2013 - IotSyS Device Config
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.DocumentOperationResult;
import org.ektorp.StreamingViewResult;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult.Row;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.ektorp.support.Views;
import org.ektorp.util.Assert;

import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Device;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.DeviceLoaders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
@Views({
	@View(name = "allConnectors", map = "function(doc) {if (doc.technology) emit(doc.name, doc);}"),
	@View(name = "allDevices", map = "function(doc) {if (doc.href) emit(doc.href, doc);}"),
	@View(name = "deviceLoaders", map = "function(doc) {if (doc.deviceLoaders) emit(null, doc);}"),
	@View(name = "devicesByConnectorId", map = "function(doc) {if (doc.href) emit(doc.connectorId, doc);}"),
})
public class ConfigsDbImpl extends CouchDbRepositorySupport<Connector> implements ConfigsDb{

	private static ConfigsDbImpl INSTANCE;
	private ObjectMapper om = new ObjectMapper();
	private List<JsonNode> allConnectors = new ArrayList<JsonNode>();
	private List<Device> allDevices = new ArrayList<Device>();
	private DeviceLoaders deviceLoaders = new DeviceLoaders();
	
	// Transition step
	private boolean migrating = false;
	private boolean connectorsMigrated = false;
	private List<String> allDeviceLoadersFromXML = new ArrayList<String>();
	private List<Connector> allConnectorsFromXML = new ArrayList<Connector>();
	private List<Device> allDevicesFromXML = new ArrayList<Device>();
	
	private static final Logger log = Logger.getLogger(ConfigsDbImpl.class.getName());
	
	private ConfigsDbImpl(CouchDbConnector db) throws Exception {
		super(Connector.class, db);
		if (!migrating){
			// Loading device configs from db
			loadAllConnectors();
			loadAllDevices();
			loadAllDeviceLoaders();
		} else {
			if (DbConnection.getCouchInstance().checkIfDbExists("deviceConfiguration")){
				DbConnection.getCouchInstance().deleteDatabase("deviceConfiguration");
				DbConnection.getCouchInstance().createDatabase("deviceConfiguration");
			}
			// Double check!
			if (!DbConnection.getCouchInstance().checkIfDbExists("deviceConfiguration")){
				throw new Exception();
			}
			initStandardDesignDocument();
		}
		
		//enableConnector("Virtual Devices 2");
//		allConnectors.clear();
//		allDevices.clear();
	}

	public static ConfigsDb getInstance(){
		if (INSTANCE == null){ 
			CouchDbConnector db = new StdCouchDbConnector("deviceConfiguration", DbConnection.getCouchInstance());
			try {
				INSTANCE = new ConfigsDbImpl(db);
			} catch (Exception e) {
				log.severe("FATAL: Config DB not connected!");
			}
		}
		return INSTANCE;
	}

	private void loadAllDeviceLoaders(){
		// There is just one DeviceLoaders document that contain an array of all device loader's names
		ViewQuery query = new ViewQuery().designDocId("_design/Connector")
				.viewName("deviceLoaders");
		List<DeviceLoaders> dls = db.queryView(query, DeviceLoaders.class);
		deviceLoaders = dls.size() != 0 ? dls.get(0) : deviceLoaders;
	}
	
	private void loadAllDevices(){
		ViewQuery query = new ViewQuery().designDocId("_design/Connector")
				.viewName("allDevices");
		allDevices = db.queryView(query, Device.class);
	}

	private void loadAllConnectors(){
		allConnectors.clear();
		
		ViewQuery query = new ViewQuery().designDocId("_design/Connector")
				.viewName("allConnectors");

		StreamingViewResult s;
		try {
			s= db.queryForStreamingView(query);
			if (s.getTotalRows() == 0)
				return;
		} catch (DocumentNotFoundException e){
			e.printStackTrace();
			return;
		}
		Iterator<Row> i = s.iterator();
		while (i.hasNext()){
			String row = ((Row)i.next()).getValue();
			JsonNode jn;
			try {
				jn = om.getFactory().createParser(row).readValueAsTree();
				allConnectors.add(jn);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		s.close();
	}
	
	@Override
	public void addDevice(Device d) {
		Assert.hasText(d.getHref(), "Device must have a href field");
		Assert.hasText(d.getConnectorId(), "Device must be associated with a connector");
		db.create(d);
	}
	
	@Override
	public void addBulkDevices(List<Device> ds){
		if (!migrating) return;
		if (!connectorsMigrated) return;
		
		for (Device d : ds){
			JsonNode thisConnector = getConnectorByName(d.getConnectorId());
			d.setConnectorId(thisConnector.get("_id").asText());
		}
		db.executeAllOrNothing(ds);
		loadAllDevices();
	}
	
	@Override
	public List<DocumentOperationResult> addBulkConnectors(List<Connector> cs){
		if (!migrating) return null;

		List<DocumentOperationResult> res = db.executeAllOrNothing(cs);
		loadAllConnectors();
		
		return res;
	}

	@Override
	public List<JsonNode> getAllConnectors() {
		return allConnectors;
	}

	@Override
	public List<JsonNode> getConnectors(String technology) {
		List<JsonNode> resultList = new ArrayList<JsonNode>();
		for (JsonNode j : allConnectors)
			if (j.get("technology").asText().equals(technology))
				resultList.add(j);
		return resultList;
	}

	@Override
	public JsonNode getConnector(String connectorId) {
		for (JsonNode j : allConnectors)
			if (j.get("_id").asText().equals(connectorId))
				return j;
		return null;
	}
	
	@Override
	public JsonNode getConnectorByName(String connectorName) {
		for (JsonNode j : allConnectors)
			if (j.get("name").asText().equals(connectorName))
				return j;
		return null;
	}

	@Override
	public int countConnectors() {
		return allConnectors.size();
	}

	@Override
	public int countConnectorsByTechnology(String technology) {
		int count = 0;
		for (JsonNode j : allConnectors)
			if (j.get("technology").asText().equals(technology))
				count++;
		return count;
	}

	@Override
	public void addConnector(Connector c) {
		add(c);
	}

	@Override
	public void updateConnector(Connector c) {
		Assert.hasText(c.getId(), "Updating connector must have an id field");
		update(c);
		JsonNode toUpdate = getConnectorByName(c.getName());
		allConnectors.remove(toUpdate);
		allConnectors.add(om.valueToTree(c));
	}
	
	@Override
	public void deleteConnector(Connector c) {
		Assert.hasText(c.getId(), "deleting connector must have an id field");
		remove(c);
	}

	@Override
	public void deleteConnector(String connectorName) {
		JsonNode connector = getConnectorByName(connectorName);
		String id = connector.get("_id").asText();
		String rev = connector.get("_rev").asText();
		db.delete(id, rev);		
		allConnectors.remove(connector);
	}

	@Override
	public void deleteAllConnectors(String technology) {
		List<JsonNode> connectors = getConnectors(technology);
		for (JsonNode connector : connectors)
			deleteConnector(connector.get("name").asText());
	}

	@Override
	public List<Device> getAllDevices() {
		return allDevices;
	}

	@Override
	public List<Device> getDevices(String connectorId) {
		List<Device> devices = new ArrayList<Device>();
		for (Device d : allDevices)
			if (d.getConnectorId().equals(connectorId))
				devices.add(d);
		return devices;
	}

	@Override
	public Device getDevice(String id) {
		for (Device d : allDevices)
			if (d.getId().equals(id))
				return d;
		return null;
	}

	@Override
	public int countDevices() {
		return allDevices.size();
	}

	@Override
	public int countDevicesByTechnology(String technology) {
		return getDevices(technology).size();
	}

	@Override
	public void updateDevice(Device d) {
		Assert.hasText(d.getId(), "Updating device must have an id field");
		Device toUpdate = getDevice(d.getId());
		db.update(d);
		allDevices.remove(toUpdate);
		allDevices.add(d);
	}

	@Override
	public void deleteDevice(Device d) {
		Assert.hasText(d.getId(), "Deleting device must have a id field");
		deleteDevice(d.getId());
	}

	@Override
	public void deleteDevice(String id) {
		Device d = getDevice(id);
		db.delete(d);
		allDevices.remove(d);
	}

	@Override
	public void deleteAllDevices(String connectorName) {
		JsonNode connector = getConnectorByName(connectorName);
		List<Device> toBeDeleted = getDevices(connector.get("technology").asText());
		for (Device d : toBeDeleted){
			deleteDevice(d.getId());
		}
	}

	@Override
	public String getDeviceLoader(int no) {
		return deviceLoaders.getDeviceLoaders().length > 0 ? deviceLoaders.getDeviceLoaders()[no] : null;
	}

	@Override
	public String[] getAllDeviceLoader() {
		return deviceLoaders.getDeviceLoaders();
	}

	@Override
	public void addDeviceLoader(String deviceLoader) {
		int noOfDeviceLoaders = deviceLoaders.getDeviceLoaders().length;
		String[] newList = new String[noOfDeviceLoaders + 1];
		System.arraycopy(deviceLoaders.getDeviceLoaders(), 0, newList, 0, noOfDeviceLoaders);
		newList[noOfDeviceLoaders] = deviceLoader;	
		
		deviceLoaders.setDeviceLoaders(newList);
		db.update(deviceLoaders);
	}
	
	@Override
	public void addBulkDeviceLoaders(List<String> ds){
		if (!migrating) return;
		
		int noOfDeviceLoaders = deviceLoaders.getDeviceLoaders().length;
		String[] newList = new String[noOfDeviceLoaders + ds.size()];
		System.arraycopy(deviceLoaders.getDeviceLoaders(), 0, newList, 0,
				noOfDeviceLoaders);
		for (int i = 0; i < ds.size(); i++)
			newList[noOfDeviceLoaders + i] = ds.get(i);

		deviceLoaders.setDeviceLoaders(newList);
		db.create(deviceLoaders);
	}

	@Override
	public void deleteDeviceLoader(int no) {
		deleteDeviceLoader(deviceLoaders.getDeviceLoaders()[no]);
	}

	@Override
	public void deleteDeviceLoader(String deviceLoader) {
		if (deviceLoaders.getDeviceLoaders().length <= 0) return;
		int noOfDeviceLoaders = deviceLoaders.getDeviceLoaders().length;
		String[] newList = new String[noOfDeviceLoaders - 1];
		for (int i = 0; i < noOfDeviceLoaders; i++){
			if (deviceLoaders.getDeviceLoaders()[i].equals(deviceLoader))
				continue;
			newList[i] = deviceLoaders.getDeviceLoaders()[i];
		}
		deviceLoaders.setDeviceLoaders(newList);
		db.update(deviceLoaders);
	}

	@Override
	public void deleteAllDeviceLoader() {
		deviceLoaders.setDeviceLoaders(new String[0]);
		db.update(deviceLoaders);
	}

	@Override
	public void updateDeviceLoader(int no, String deviceLoader) {
		updateDeviceLoader(deviceLoaders.getDeviceLoaders()[no], deviceLoader);
	}

	@Override
	public void updateDeviceLoader(String oldDeviceLoader,
			String newDeviceLoader) {
		int noOfDeviceLoaders = deviceLoaders.getDeviceLoaders().length;
		for (int i = 0; i < noOfDeviceLoaders; i++){
			if (deviceLoaders.getDeviceLoaders()[i].equals(oldDeviceLoader))
				deviceLoaders.getDeviceLoaders()[i] = newDeviceLoader;
		}
		db.update(deviceLoaders);
	}

	@Override
	public void clear() {
		allConnectors.clear();		
	}

	@Override
	public void prepareDevice(String connectorName, Device d) {
		if (!migrating) return;
		
		// FIXME: Assuming connector name is unique
		d.setConnectorId(connectorName);
		allDevicesFromXML.add(d);
	}

	@Override
	public void prepareDeviceLoader(String deviceLoaderName) {
		if (!migrating) return;
		
		allDeviceLoadersFromXML.add(deviceLoaderName);
	}

	public void prepareConnectors(List<Connector> connectors){
		if (!migrating) return;

		allConnectorsFromXML.addAll(connectors);
		
		// if all technology-specific connectors have been prepared, do migrate and lock migration process
		// by setting migrating = false in migrate()
		if (allDeviceLoadersFromXML.size() == 10){ // knxets skipped
			System.out.println(">>>>>>>>>>>>>>>>>>> start migrating");
			migrate();
		}
	}
	
	@Override
	public void migrate(List<Connector> connectors) {
		if (!migrating) return;
		
		System.out.println(">>>>>>>>>>>>>>>>>>>> migrating " + allDeviceLoadersFromXML.size() + " device loaders to DB");
		addBulkDeviceLoaders(allDeviceLoadersFromXML);
		// addBulkConnectors involves re-fetching the allConnectors list from database so that 
		// in addBulkDevices we can pair the generated connector ID with the device's connector ID
		List<DocumentOperationResult> res = addBulkConnectors(connectors);
		System.out.println(">>>>>>>>>>>>>>>>>>>> migrating " + connectors.size() + " connectors to DB");
		if ((res != null) && (res.size() == 0)){
			connectorsMigrated = true;
			addBulkDevices(allDevicesFromXML);
			System.out.println(">>>>>>>>>>>>>>>>>>>> migrating " + allDevicesFromXML.size() + " devices to DB");
		}
	}
	
	@Override
	public void migrate() {
		migrate(allConnectorsFromXML);
		migrating = false;
	}
	
	public void disableAllConnectors(){
		for(JsonNode j : allConnectors){
			((ObjectNode)j).put("enabled", false);
			db.update(j);
		}
	}
	
	public void enableConnector(String name){
		for(JsonNode j : allConnectors)
			if (j.get("name").asText().equals(name)){
				((ObjectNode)j).put("enabled", true);
				db.update(j);
				return;
			}
	}
	
	@Override
	public boolean isMigrating() {
		return migrating;
	}

	public void setMigrating(boolean migrating) {
		this.migrating = migrating;
	}

	@Override
	public void compactDb() {
		db.compact();
	}

}

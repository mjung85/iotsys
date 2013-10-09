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

package at.ac.tuwien.auto.iotsys.gateway.connectors.knx;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import obix.Contract;
import obix.List;
import obix.Obj;
import obix.Uri;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.impl.DatapointImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.entity.impl.EntityImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumConnector;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumStandard;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.impl.EnumsImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.language.impl.TranslationImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.network.Network;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.network.impl.NetworkImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.DomainImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.GroupImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.PartImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl.DataPointInit;

public class KNXDeviceLoaderETSImpl implements DeviceLoader {
	private static Logger log = Logger.getLogger(KNXDeviceLoaderImpl.class
			.getName());

	private XMLConfiguration devicesConfig;

	private ArrayList<String> myObjects = new ArrayList<String>();

	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		try {
			devicesConfig = new XMLConfiguration(
					"knx-config/Suitcase_2013-09-05.xml");
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		ArrayList<Connector> connectors = new ArrayList<Connector>();

		KNXConnector knxConnector = new KNXConnector("192.168.161.59", 3671,
				"auto");

		connect(knxConnector);

		initNetworks(knxConnector, objectBroker);

		connectors.add(knxConnector);

		return connectors;
	}

	private void connect(KNXConnector knxConnector) {
		try {
			knxConnector.connect();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KNXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initNetworks(KNXConnector knxConnector,
			ObjectBroker objectBroker) {
		// Networks
		List networks = new List();
		networks.setName("networks");
		networks.setOf(new Contract(Network.CONTRACT));
		networks.setHref(new Uri("/networks"));

		// ================================================================================

		// Phase I - Parse general information

		// parse manufacturer information

		Hashtable<String, String> manufacturerById = new Hashtable<String, String>();

		Object manufacturers = devicesConfig
				.getProperty("configurations.manufacturers.manufacturer.[@id]");

		int manufacturersSize = 0;

		if (manufacturers != null) {
			manufacturersSize = 1;
		}

		if (manufacturers instanceof Collection<?>) {
			manufacturersSize = ((Collection<?>) manufacturers).size();
		}

		for (int manufacturerIdx = 0; manufacturerIdx < manufacturersSize; manufacturerIdx++) {
			String manufacturerId = devicesConfig
					.getString("configurations.manufacturers.manufacturer("
							+ manufacturerIdx + ").[@id]");
			String manufacturerName = devicesConfig
					.getString("configurations.manufacturers.manufacturer("
							+ manufacturerIdx + ").[@name]");
			manufacturerById.put(manufacturerId, manufacturerName);
		}

		// parse group communication addresses for endpoints

		HierarchicalConfiguration functionalConfig = devicesConfig
				.configurationAt("views.functional");

		Hashtable<String, String> groupAddressByDatapointID = new Hashtable<String, String>();

		parseGroupAddressConfig(functionalConfig, groupAddressByDatapointID);

		// ================================================================================

		// Phase II

		// create entities

		// Hashtable to lookup entities by ID
		Hashtable<String, EntityImpl> entityById = new Hashtable<String, EntityImpl>();

		// Hashtable to lookup datapoints by ID
		Hashtable<String, DatapointImpl> datapointById = new Hashtable<String, DatapointImpl>();

		ArrayList<Connector> connectors = new ArrayList<Connector>();

		String networkName = (String) devicesConfig.getProperty("[@name]");
		String networkStandard = devicesConfig.getString("[@standard]");
		String networkId = devicesConfig.getString("[@id]");

		NetworkImpl n = new NetworkImpl(networkId, networkName, null, EnumsImpl
				.getInstance().getEnum(EnumStandard.HREF)
				.getKey(networkStandard));
		networks.add(n);
		networks.add(n.getReference(false));

		// Network
		objectBroker.addObj(n, true);

		Object entities = devicesConfig.getProperty("entities.entity[@id]");

		int entitiesSize = 0;

		if (entities != null) {
			entitiesSize = 1;
		}

		if (entities instanceof Collection<?>) {
			entitiesSize = ((Collection<?>) entities).size();
		}

		for (int entityIdx = 0; entityIdx < entitiesSize; entityIdx++) {
			HierarchicalConfiguration entityConfig = devicesConfig
					.configurationAt("entities.entity(" + entityIdx + ")");

			String entityId = entityConfig.getString("[@id]");
			String entityName = entityConfig.getString("[@name]");
			String entityDescription = entityConfig.getString("[@description]");
			String entityOrderNumber = entityConfig.getString("[@orderNumber]");
			String entityManufacturerId = entityConfig
					.getString("[@manufacturerId]");

			// Entities and Datapoints
			EntityImpl entity = new EntityImpl(entityId, entityName, null,
					manufacturerById.get(entityManufacturerId),
					entityOrderNumber);

			entityById.put(entityId, entity);

			Object translations = entityConfig
					.getProperty("translations.translation[@language]");

			int translationsSize = 0;

			if (translations != null) {
				translationsSize = 1;
			}

			if (translations instanceof Collection<?>) {
				translationsSize = ((Collection<?>) translations).size();
			}

			for (int transIdx = 0; transIdx < translationsSize; transIdx++) {

				HierarchicalConfiguration transConfig = entityConfig
						.configurationAt("translations.translation(" + transIdx
								+ ")");

				String language = transConfig.getString("[@language]");
				String attribute = transConfig.getString("[@attribute]");
				String value = transConfig.getString("[@value]");
//				entity.addTranslation(new TranslationImpl(language, attribute,
//						value));

			}

			n.getEntities().addEntity(entity);

			objectBroker.addObj(entity, true);

			// now add datapoints

			Object datapoints = entityConfig
					.getProperty("datapoints.datapoint[@id]");

			int datapointsSize = 0;

			if (datapoints != null) {
				datapointsSize = 1;
			}

			if (datapoints instanceof Collection<?>) {
				datapointsSize = ((Collection<?>) datapoints).size();
			}

			for (int datapointIdx = 0; datapointIdx < datapointsSize; datapointIdx++) {

				HierarchicalConfiguration datapointConfig = entityConfig
						.configurationAt("datapoints.datapoint(" + datapointIdx
								+ ")");
				String dataPointId = datapointConfig.getString("[@id]");
				String[] dataPointNameArray = datapointConfig
						.getStringArray("[@name]");
				StringBuilder dataPointNameBuilder = new StringBuilder();
				String dataPointName = "";
				for (int i = 0; i < dataPointNameArray.length; i++) {
					dataPointNameBuilder.append(dataPointNameArray[i]);
					if (i < dataPointNameArray.length - 1) {
						dataPointNameBuilder.append(" ");
					}
				}
				try {
					dataPointName = URLEncoder.encode(
							dataPointNameBuilder.toString(), "UTF-8");
				} catch (UnsupportedEncodingException e2) {

					e2.printStackTrace();
				}

				String dataPointDescription = datapointConfig
						.getString("[@description]");
				String dataPointTypeIds = datapointConfig
						.getString("[@datapointTypeIds]");
				String dataPointPriority = datapointConfig
						.getString("[@priority]");
				String dataPointWriteFlag = datapointConfig
						.getString("[@writeFlag]");
				String dataPointCommunicationFlag = datapointConfig
						.getString("[@communicationFlag]");
				String dataPointReadFlag = datapointConfig
						.getString("[@readFlag]");
				String dataPointReadOnInitFlag = datapointConfig
						.getString("[@readOnInitFlag]");
				String dataPointTransmitFlag = datapointConfig
						.getString("[@transmitFlag]");
				String updateFlag = datapointConfig.getString("[@updateFlag]");
				String clazzName = "at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl."
						+ dataPointTypeIds.replace('-', '_') + "_ImplKnx";
				Class clazz = null;

				try {
					log.info("Loading: " + clazzName);
					clazz = Class.forName(clazzName);
				} catch (ClassNotFoundException e) {
					log.warning(clazzName
							+ " not found. Cannot instantiate according sub data point type. Trying fallback to generic main type.");
					clazzName = "at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl."
							+ "DPT_" + clazzName.charAt(5) + "_ImplKnx"; //

					try {
						log.info("Loading: " + clazzName);
						clazz = Class.forName(clazzName);
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				try {
					if (clazz != null) {
						Constructor constructor = clazz.getConstructor(
								KNXConnector.class, DataPointInit.class);
						Object[] object = new Object[2];
						object[0] = knxConnector;
						DataPointInit dptInit = new DataPointInit();
//						dptInit.setDisplay(dataPointDescription);
						dptInit.setDisplayName(dataPointDescription);
						dptInit.setReadable("Enabled".equals(dataPointReadFlag));
						dptInit.setName(dataPointName);
						dptInit.setGroupAddress(new GroupAddress(Integer
								.parseInt(groupAddressByDatapointID
										.get(dataPointId))));
						dptInit.setWritable("Enabled"
								.equals(dataPointWriteFlag));

						object[1] = dptInit;
						DatapointImpl dataPoint = (DatapointImpl) constructor
								.newInstance(object);
						
						if(dataPoint.get("value") != null){
							dataPoint.get("value").setDisplayName(dataPointDescription);
//							dataPoint.get("value").setDisplay(dataPointDescription);
						}

						translations = datapointConfig
								.getProperty("translations.translation[@language]");

						translationsSize = 0;

						if (translations != null) {
							translationsSize = 1;
						}

						if (translations instanceof Collection<?>) {
							translationsSize = ((Collection<?>) translations)
									.size();
						}

						for (int transIdx = 0; transIdx < translationsSize; transIdx++) {

							HierarchicalConfiguration transConfig = datapointConfig
									.configurationAt("translations.translation("
											+ transIdx + ")");

							String language = transConfig
									.getString("[@language]");
							String attribute = transConfig
									.getString("[@attribute]");
							
							String value = arrayToString(transConfig.getStringArray("[@value]"));
							
							if(language.equals("en-US") && attribute.equals("name")){
								dataPoint.setDisplayName(value);
							}
														
//							dataPoint.addTranslation(new TranslationImpl(
//									language, attribute, value));

						}

						datapointById.put(dataPointId, dataPoint);
						entity.addDatapoint(dataPoint);

						objectBroker.addObj(dataPoint, true);
					}

				} catch (NoSuchMethodException e) {
					log.warning(clazzName
							+ " no such method. Cannot instantiate according datapoint.");
				} catch (SecurityException e) {
					log.warning(clazzName
							+ " security exception. Cannot instantiate according datapoint.");
				} catch (InstantiationException e) {
					log.warning(clazzName
							+ " instantiation exception. Cannot instantiate according datapoint.");
				} catch (IllegalAccessException e) {
					log.warning(clazzName
							+ " illegal access exception. Cannot instantiate according datapoint.");
				} catch (IllegalArgumentException e) {
					log.warning(clazzName
							+ " illegal argument exception. Cannot instantiate according datapoint.");
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					log.warning(clazzName
							+ " invocation target exception. Cannot instantiate according datapoint.");
					e.printStackTrace();
				}
			}
		}

		// Phase III create views
		HierarchicalConfiguration buildingConfig = devicesConfig
				.configurationAt("views.building");

		objectBroker.addObj(n.getBuilding(), true);

		parseBuildingView(buildingConfig, (Obj) n.getBuilding(), n,
				objectBroker, entityById);

		HierarchicalConfiguration funcionalView = devicesConfig
				.configurationAt("views.functional");

		objectBroker.addObj(n.getFunctional(), true);

		parseFunctionalView(funcionalView, (Obj) n.getFunctional(), n,
				objectBroker, entityById, datapointById);

		HierarchicalConfiguration domainView = devicesConfig
				.configurationAt("views.domains");

		objectBroker.addObj(n.getDomains(), true);

		parseDomainView(domainView, (Obj) n.getDomains(), n, objectBroker,
				entityById, datapointById);

		HierarchicalConfiguration topologyView = devicesConfig
				.configurationAt("views.topology");

		objectBroker.addObj(n.getTopology(), true);

		parseTopologyView(topologyView, (Obj) n.getTopology(), n, objectBroker,
				entityById, datapointById);

	}

	@Override
	public void removeDevices(ObjectBroker objectBroker) {
		synchronized (myObjects) {
			for (String href : myObjects) {
				objectBroker.removeObj(href);
			}
		}
	}

	@Override
	public void setConfiguration(XMLConfiguration devicesConfiguration) {
		this.devicesConfig = devicesConfiguration;
		if (devicesConfiguration == null) {

		}
	}

	private void parseGroupAddressConfig(HierarchicalConfiguration groupConfig,
			Hashtable<String, String> groupAddressByDatapointId) {
		Object groups = groupConfig.getProperty("group.[@id]");

		// identify number of group elements
		int groupsSize = 0;

		if (groups != null) {
			groupsSize = 1;
		}

		if (groups instanceof Collection<?>) {
			groupsSize = ((Collection<?>) groups).size();
		}

		// if there are no group elements return
		for (int groupsIdx = 0; groupsIdx < groupsSize; groupsIdx++) {
			String groupAddress = groupConfig.getString("group(" + groupsIdx
					+ ").[@address]");

			// find instance elements for this group

			HierarchicalConfiguration concreteGroup = groupConfig
					.configurationAt("group(" + groupsIdx + ")");

			Object instanceElements = concreteGroup
					.getProperty("instance.[@id]");

			int instanceSize = 0;

			if (instanceElements != null) {
				instanceSize = 1;
			}

			if (instanceElements instanceof Collection<?>) {
				instanceSize = ((Collection<?>) instanceElements).size();
			}

			for (int instanceIdx = 0; instanceIdx < instanceSize; instanceIdx++) {
				String instanceId = concreteGroup.getString("instance("
						+ instanceIdx + ").[@id]");
				log.info("Mapping instanceID: " + instanceId + " to "
						+ groupAddress);
				groupAddressByDatapointId.put(instanceId, groupAddress);
			}

			// recursively perform this method for nested groups
			parseGroupAddressConfig(concreteGroup, groupAddressByDatapointId);

		}

	}

	private void parseBuildingView(HierarchicalConfiguration partConfig,
			Obj parent, Network n, ObjectBroker objectBroker,
			Hashtable<String, EntityImpl> entityById) {

		Object parts = partConfig.getProperty("part.[@id]");

		// identify number of group elements
		int partsSize = 0;

		if (parts != null) {
			partsSize = 1;
		}

		if (parts instanceof Collection<?>) {
			partsSize = ((Collection<?>) parts).size();
		}

		// if there are no group elements return
		for (int partsIdx = 0; partsIdx < partsSize; partsIdx++) {
			String partId = partConfig
					.getString("part(" + partsIdx + ").[@id]");

			String partName = partConfig.getString("part(" + partsIdx
					+ ").[@name]");

			String partType = partConfig.getString("part(" + partsIdx
					+ ").[@type]");

			PartImpl part = new PartImpl(partId, partName, null, partType);

			// add instances to part
			HierarchicalConfiguration concretePart = partConfig
					.configurationAt("part(" + partsIdx + ")");
			Object instanceElements = concretePart
					.getProperty("instance.[@id]");

			int instanceSize = 0;

			if (instanceElements != null) {
				instanceSize = 1;
			}

			if (instanceElements instanceof Collection<?>) {
				instanceSize = ((Collection<?>) instanceElements).size();
			}

			// add part to parent part
			parent.add(part);

			objectBroker.addObj(part, true);

			// if this part has some instances set, add references to entities
			for (int instanceIdx = 0; instanceIdx < instanceSize; instanceIdx++) {
				String instanceId = concretePart.getString("instance("
						+ instanceIdx + ").[@id]");
				Obj instance = part.addInstance(entityById.get(instanceId));
				objectBroker.addObj(instance);
			}

			// recursively add more parts

			parseBuildingView(concretePart, part, n, objectBroker, entityById);

		}
	}

	private void parseFunctionalView(HierarchicalConfiguration groupConfig,
			Obj parent, Network n, ObjectBroker objectBroker,
			Hashtable<String, EntityImpl> entityById,
			Hashtable<String, DatapointImpl> datapointById) {

		Object groups = groupConfig.getProperty("group.[@id]");

		// identify number of group elements
		int groupsSize = 0;

		if (groups != null) {
			groupsSize = 1;
		}

		if (groups instanceof Collection<?>) {
			groupsSize = ((Collection<?>) groups).size();
		}

		// if there are no group elements return
		for (int groupsIdx = 0; groupsIdx < groupsSize; groupsIdx++) {

			String groupId = groupConfig.getString("group(" + groupsIdx
					+ ").[@id]");

			String groupName = groupConfig.getString("group(" + groupsIdx
					+ ").[@name]");

			String groupAddress = groupConfig.getString("group(" + groupsIdx
					+ ").[@type]");

			String groupDescription = groupConfig.getString("group("
					+ groupsIdx + ").[@type]");

			int address = 0;

			if (groupAddress != null) {
				address = Integer.parseInt(groupAddress);
			}

			GroupImpl group = new GroupImpl(groupId, groupName,
					groupDescription, address);

			// add instances to part
			HierarchicalConfiguration concreteGroup = groupConfig
					.configurationAt("group(" + groupsIdx + ")");
			Object instanceElements = concreteGroup
					.getProperty("instance.[@id]");

			int instanceSize = 0;

			if (instanceElements != null) {
				instanceSize = 1;
			}

			if (instanceElements instanceof Collection<?>) {
				instanceSize = ((Collection<?>) instanceElements).size();
			}
			// add part to parent part
			parent.add(group);

			objectBroker.addObj(group, true);

			// if this part has some instances set, add references to entities
			for (int instanceIdx = 0; instanceIdx < instanceSize; instanceIdx++) {
				String instanceId = concreteGroup.getString("instance("
						+ instanceIdx + ").[@id]");
				group.addInstance(datapointById.get(instanceId),
						EnumConnector.KEY_SEND);

				try {
					DatapointImpl datapointImpl = (DatapointImpl) datapointById
							.get(instanceId).clone();
					datapointImpl.setName("function");
					group.addFunction(datapointImpl);

					objectBroker.addObj(datapointImpl, true);

				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// recursively add more parts
			parseFunctionalView(concreteGroup, group, n, objectBroker,
					entityById, datapointById);

		}
	}

	private void parseDomainView(HierarchicalConfiguration domainConfig,
			Obj parent, Network n, ObjectBroker objectBroker,
			Hashtable<String, EntityImpl> entityById,
			Hashtable<String, DatapointImpl> datapointById) {

		Object domains = domainConfig.getProperty("domain.[@id]");

		// identify number of group elements
		int domainSize = 0;

		if (domains != null) {
			domainSize = 1;
		}

		if (domains instanceof Collection<?>) {
			domainSize = ((Collection<?>) domains).size();
		}

		// if there are no group elements return
		for (int domainIdx = 0; domainIdx < domainSize; domainIdx++) {

			String domainId = domainConfig.getString("domain(" + domainIdx
					+ ").[@id]");

			String domainName = domainConfig.getString("domain(" + domainIdx
					+ ").[@name]");

			DomainImpl domain = new DomainImpl(domainId, domainName, null);

			// add instances to part
			HierarchicalConfiguration concreteDomain = domainConfig
					.configurationAt("domain(" + domainIdx + ")");
			Object instanceElements = concreteDomain
					.getProperty("instance.[@id]");

			int instanceSize = 0;

			if (instanceElements != null) {
				instanceSize = 1;
			}

			if (instanceElements instanceof Collection<?>) {
				instanceSize = ((Collection<?>) instanceElements).size();
			}
			// add part to parent part
			parent.add(domain);

			objectBroker.addObj(domain, true);

			// if this part has some instances set, add references to entities
			for (int instanceIdx = 0; instanceIdx < instanceSize; instanceIdx++) {
				String instanceId = concreteDomain.getString("instance("
						+ instanceIdx + ").[@id]");
				Obj addInstance = domain
						.addInstance(entityById.get(instanceId));
				objectBroker.addObj(addInstance, true);

			}

			// recursively add more domains
			parseDomainView(concreteDomain, domain, n, objectBroker,
					entityById, datapointById);

		}
	}

	private void parseTopologyView(HierarchicalConfiguration areaConfig,
			Obj parent, Network n, ObjectBroker objectBroker,
			Hashtable<String, EntityImpl> entityById,
			Hashtable<String, DatapointImpl> datapointById) {

		Object areas = areaConfig.getProperty("area.[@id]");

		// identify number of group elements
		int areaSize = 0;

		if (areas != null) {
			areaSize = 1;
		}

		if (areas instanceof Collection<?>) {
			areaSize = ((Collection<?>) areas).size();
		}

		// if there are no group elements return
		for (int areaIdx = 0; areaIdx < areaSize; areaIdx++) {

			String areaId = areaConfig.getString("area(" + areaIdx + ").[@id]");

			String areaName = areaConfig.getString("area(" + areaIdx
					+ ").[@name]");

			DomainImpl area = new DomainImpl(areaId, areaName, null);

			// add instances to part
			HierarchicalConfiguration concreteArea = areaConfig
					.configurationAt("area(" + areaIdx + ")");
			Object instanceElements = concreteArea
					.getProperty("instance.[@id]");

			int instanceSize = 0;

			if (instanceElements != null) {
				instanceSize = 1;
			}

			if (instanceElements instanceof Collection<?>) {
				instanceSize = ((Collection<?>) instanceElements).size();
			}
			// add part to parent part
			parent.add(area);

			objectBroker.addObj(area, true);

			// if this part has some instances set, add references to entities
			for (int instanceIdx = 0; instanceIdx < instanceSize; instanceIdx++) {
				String instanceId = concreteArea.getString("instance("
						+ instanceIdx + ").[@id]");
				Obj addInstance = area.addInstance(entityById.get(instanceId));
				objectBroker.addObj(addInstance, true);

			}

			// recursively add more domains
			parseTopologyView(concreteArea, area, n, objectBroker, entityById,
					datapointById);

		}
	}

	private String arrayToString(String[] array) {
		StringBuilder dataPointNameBuilder = new StringBuilder();
		String dataPointName = "";
		for (int i = 0; i < array.length; i++) {
			dataPointNameBuilder.append(array[i]);
			if (i < array.length - 1) {
				dataPointNameBuilder.append(", ");
			}
		}

		return dataPointNameBuilder.toString();
	}
}

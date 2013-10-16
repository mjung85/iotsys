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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import obix.Contract;
import obix.List;
import obix.Obj;
import obix.Uri;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.impl.DatapointImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.entity.impl.EntityImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumConnector;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumEnabled;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.impl.EnumsImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.network.Network;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.network.impl.NetworkImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.DomainImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.GroupImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.PartImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl.DataPointInit;

public class KNXDeviceLoaderETSImpl implements DeviceLoader
{
	private static Logger log = Logger.getLogger(KNXDeviceLoaderETSImpl.class.getName());

	private XMLConfiguration devicesConfig;
	private XMLConfiguration connectorsConfig;

	private ArrayList<String> myObjects = new ArrayList<String>();

	public void unZip(String zipFile, String outputFolder)
	{
		byte[] buffer = new byte[1024];

		try
		{
			File folder = new File(outputFolder);
			if (!folder.exists())
			{
				folder.mkdir();
			}

			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));

			ZipEntry ze = zis.getNextEntry();

			while (ze != null)
			{

				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				log.info("file unzip : " + newFile.getAbsoluteFile());

				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0)
				{
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public ArrayList<Connector> initDevices(ObjectBroker objectBroker)
	{
		setConfiguration(connectorsConfig);

		ArrayList<Connector> connectors = new ArrayList<Connector>();

		Object knxConnectors = connectorsConfig.getProperty("knx-ets.connector.name");

		int connectorsSize = 0;

		if (knxConnectors != null)
		{
			connectorsSize = 1;
		}

		if (knxConnectors instanceof Collection<?>)
		{
			connectorsSize = ((Collection<?>) knxConnectors).size();
		}

		// Networks
		List networks = new List();
		networks.setName("networks");
		networks.setOf(new Contract(Network.CONTRACT));
		networks.setHref(new Uri("/networks"));
		objectBroker.addObj(networks, true);

		for (int connector = 0; connector < connectorsSize; connector++)
		{
			HierarchicalConfiguration subConfig = connectorsConfig.configurationAt("knx-ets.connector(" + connector + ")");

			// String connectorName = subConfig.getString("name");
			String routerIP = subConfig.getString("router.ip");
			int routerPort = subConfig.getInteger("router.port", 3671);
			String localIP = subConfig.getString("localIP");
			Boolean enabled = subConfig.getBoolean("enabled", false);
			Boolean forceRefresh = subConfig.getBoolean("forceRefresh", false);
			String knxProj = subConfig.getString("knx-proj");

			Boolean enableGroupComm = subConfig.getBoolean("enableGroupComm", false);

			Boolean enableHistories = subConfig.getBoolean("enableHistories", false);

			if (enabled)
			{
				File file = new File(knxProj);

				if (!file.exists() || file.isDirectory() || !file.getName().endsWith(".knxproj") || file.getName().length() < 8)
				{
					log.warning("KNX project file " + knxProj + " is not a valid KNX project file.");
					continue;
				}

				String projDirName = knxProj.substring(0, knxProj.length() - 8);

				File projDir = new File(projDirName);

				if (!projDir.exists() || forceRefresh)
				{
					log.info("Expanding " + knxProj + " into directory " + projDirName);
					unZip(knxProj, projDirName);
				}

				String directory = "file:///" + projDir.getAbsolutePath().replace('\\', '/');

				// now the unpacked ETS project should be available in the directory
				String transformFileName = directory + "/" + file.getName().replaceFirst(".knxproj", "") + ".xml";

				File transformFile = new File(transformFileName);

				if (!transformFile.exists() || forceRefresh)
				{
					log.info("Transforming ETS configuration.");
					System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
					// Create a transform factory instance.
					TransformerFactory tfactory = TransformerFactory.newInstance();

					// Create a transformer for the stylesheet.
					Transformer transformer;

					try
					{
						transformer = tfactory.newTransformer(new StreamSource("knx-config/stylesheet_knx.xsl"));
						Collection<File> listFiles = FileUtils.listFiles(projDir, FileFilterUtils.nameFileFilter("0.xml"), new IOFileFilter()
						{

							@Override
							public boolean accept(File file)
							{
								return true;
							}

							@Override
							public boolean accept(File dir, String name)
							{
								return true;
							}

						});
						if (listFiles.size() != 1)
						{
							log.severe("Found no or more than one 0.xml file in " + projDirName);
							continue;
						}

						log.info("Transforming with directory parameter set to " + directory);

						transformer.setParameter("directory", directory);
						transformer.transform(new StreamSource(listFiles.iterator().next().getAbsoluteFile()), new StreamResult(transformFileName));

						log.info("Transformation completed and result written to: " + transformFileName);
					}
					catch (TransformerConfigurationException e)
					{
						e.printStackTrace();
					}
					catch (TransformerException e)
					{
						e.printStackTrace();
					}

					try
					{
						devicesConfig = new XMLConfiguration(transformFileName);
					}
					catch (Exception e)
					{
						log.log(Level.SEVERE, e.getMessage(), e);
					}

					KNXConnector knxConnector = new KNXConnector(routerIP, routerPort, localIP);

					connect(knxConnector);

					initNetworks(knxConnector, objectBroker, networks, enableGroupComm, enableHistories);

					connectors.add(knxConnector);
				}
			}
		}

		return connectors;
	}

	private void connect(KNXConnector knxConnector)
	{
		try
		{
			knxConnector.connect();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (KNXException e)
		{
			e.printStackTrace();
		}
	}

	private void initNetworks(KNXConnector knxConnector, ObjectBroker objectBroker, Obj networks, boolean enableGroupComm, boolean enableHistories)
	{
		// Maps
		Hashtable<String, EntityImpl> entityById = new Hashtable<String, EntityImpl>();
		Hashtable<String, DatapointImpl> datapointById = new Hashtable<String, DatapointImpl>();
		Hashtable<String, String> referenceById = new Hashtable<String, String>();
		Hashtable<String, String> groupAddressByDatapointID = new Hashtable<String, String>();

		// Resources
		parseReferences(referenceById);

		// Group communication addresses for endpoints
		parseGroupAddressConfig(devicesConfig.configurationAt("views.functional"), groupAddressByDatapointID);

		// Network
		NetworkImpl n = new NetworkImpl(devicesConfig.getString("[@id]"), devicesConfig.getString("[@name]"), devicesConfig.getString("[@description]"), devicesConfig.getString("[@standard]"));
		networks.add(n);
		networks.add(n.getReference());
		objectBroker.addObj(n, true);

		// Entities and datapoints
		parseEntites(knxConnector, objectBroker, entityById, datapointById, n, referenceById, groupAddressByDatapointID);

		// Views
		objectBroker.addObj(n.getBuilding(), true);
		parseBuildingView(devicesConfig.configurationAt("views.building"), (Obj) n.getBuilding(), n, objectBroker, entityById);

		objectBroker.addObj(n.getFunctional(), true);
		parseFunctionalView(devicesConfig.configurationAt("views.functional"), n.getFunctional(), n, objectBroker, entityById, datapointById);

		objectBroker.addObj(n.getDomains(), true);
		parseDomainView(devicesConfig.configurationAt("views.domains"), (Obj) n.getDomains(), n, objectBroker, entityById, datapointById);

		objectBroker.addObj(n.getTopology(), true);
		parseTopologyView(devicesConfig.configurationAt("views.topology"), (Obj) n.getTopology(), n, objectBroker, entityById, datapointById);
	}

	private void parseReferences(Hashtable<String, String> referenceById)
	{
		Object references = devicesConfig.getProperty("references.reference.[@id]");

		int referencesSize = 0;

		if (references != null)
		{
			referencesSize = 1;
		}

		if (references instanceof Collection<?>)
		{
			referencesSize = ((Collection<?>) references).size();
		}

		for (int i = 0; i < referencesSize; i++)
		{
			String id = devicesConfig.getString("references.reference(" + i + ").[@id]");
			String name = devicesConfig.getString("references.reference(" + i + ").[@name]");
			referenceById.put(id, name);
		}
	}

	private void parseGroupAddressConfig(HierarchicalConfiguration groupConfig, Hashtable<String, String> groupAddressByDatapointId)
	{
		Object groups = groupConfig.getProperty("group.[@id]");

		// identify number of group elements
		int groupsSize = 0;

		if (groups != null)
		{
			groupsSize = 1;
		}

		if (groups instanceof Collection<?>)
		{
			groupsSize = ((Collection<?>) groups).size();
		}

		// if there are no group elements return
		for (int groupsIdx = 0; groupsIdx < groupsSize; groupsIdx++)
		{
			String groupAddress = groupConfig.getString("group(" + groupsIdx + ").[@address]");

			// find instance elements for this group

			HierarchicalConfiguration concreteGroup = groupConfig.configurationAt("group(" + groupsIdx + ")");

			Object instanceElements = concreteGroup.getProperty("instance.[@id]");

			int instanceSize = 0;

			if (instanceElements != null)
			{
				instanceSize = 1;
			}

			if (instanceElements instanceof Collection<?>)
			{
				instanceSize = ((Collection<?>) instanceElements).size();
			}

			for (int instanceIdx = 0; instanceIdx < instanceSize; instanceIdx++)
			{
				String instanceId = concreteGroup.getString("instance(" + instanceIdx + ").[@id]");
				log.info("Mapping instanceID: " + instanceId + " to " + groupAddress);

				if (!groupAddressByDatapointId.containsKey(instanceId))
					groupAddressByDatapointId.put(instanceId, groupAddress);
			}

			// recursively perform this method for nested groups
			parseGroupAddressConfig(concreteGroup, groupAddressByDatapointId);

		}

	}

	private void parseEntites(KNXConnector knxConnector, ObjectBroker objectBroker, Hashtable<String, EntityImpl> entityById, Hashtable<String, DatapointImpl> datapointById, NetworkImpl n, Hashtable<String, String> resourceById,
			Hashtable<String, String> groupAddressByDatapointID)
	{
		Object entities = devicesConfig.getProperty("entities.entity[@id]");

		int entitiesSize = 0;

		if (entities != null)
		{
			entitiesSize = 1;
		}

		if (entities instanceof Collection<?>)
		{
			entitiesSize = ((Collection<?>) entities).size();
		}

		for (int entityIdx = 0; entityIdx < entitiesSize; entityIdx++)
		{
			// Entity
			HierarchicalConfiguration entityConfig = devicesConfig.configurationAt("entities.entity(" + entityIdx + ")");

			String entityId = entityConfig.getString("[@id]");
			String entityName = arrayToString(entityConfig.getStringArray("[@name]"));
			String entityDescription = arrayToString(entityConfig.getStringArray("[@description]"));
			String entityOrderNumber = entityConfig.getString("[@orderNumber]");
			String entityManufacturerId = entityConfig.getString("[@manufacturerId]");

			EntityImpl entity = new EntityImpl(entityId, entityName, entityDescription, resourceById.get(entityManufacturerId), entityOrderNumber);

			entityById.put(entityId, entity);

			// Translations
			Object translations = entityConfig.getProperty("translations.translation[@language]");

			int translationsSize = 0;

			if (translations != null)
			{
				translationsSize = 1;
			}

			if (translations instanceof Collection<?>)
			{
				translationsSize = ((Collection<?>) translations).size();
			}

			for (int transIdx = 0; transIdx < translationsSize; transIdx++)
			{
				HierarchicalConfiguration transConfig = entityConfig.configurationAt("translations.translation(" + transIdx + ")");

				String language = transConfig.getString("[@language]");
				String attribute = transConfig.getString("[@attribute]");
				String value = arrayToString(transConfig.getStringArray("[@value]"));

				try
				{
					entity.addTranslation(language, attribute, value);
				}
				catch (Exception e)
				{
					log.warning(e.getMessage());
				}
			}

			n.getEntities().addEntity(entity);
			objectBroker.addObj(entity, true);

			// Datapoints
			Object datapoints = entityConfig.getProperty("datapoints.datapoint[@id]");

			int datapointsSize = 0;

			if (datapoints != null)
			{
				datapointsSize = 1;
			}

			if (datapoints instanceof Collection<?>)
			{
				datapointsSize = ((Collection<?>) datapoints).size();
			}

			for (int datapointIdx = 0; datapointIdx < datapointsSize; datapointIdx++)
			{
				HierarchicalConfiguration datapointConfig = entityConfig.configurationAt("datapoints.datapoint(" + datapointIdx + ")");

				String dataPointName = arrayToString(datapointConfig.getStringArray("[@name]"));
				String dataPointTypeIds = datapointConfig.getString("[@datapointTypeIds]");
				String dataPointId = datapointConfig.getString("[@id]");
				String dataPointDescription = arrayToString(datapointConfig.getStringArray("[@description]"));
				String dataPointWriteFlag = datapointConfig.getString("[@writeFlag]");
				String dataPointReadFlag = datapointConfig.getString("[@readFlag]");
				// String dataPointPriority = datapointConfig.getString("[@priority]");
				// String dataPointCommunicationFlag = datapointConfig.getString("[@communicationFlag]");
				// String dataPointReadOnInitFlag = datapointConfig.getString("[@readOnInitFlag]");
				// String dataPointTransmitFlag = datapointConfig.getString("[@transmitFlag]");
				// String updateFlag = datapointConfig.getString("[@updateFlag]");
				
				// use only the first DPTS
				if (dataPointTypeIds.indexOf(" ") >= 0)
				{
					dataPointTypeIds = dataPointTypeIds.substring(0, dataPointTypeIds.indexOf(" "));
				}

				String clazzName = "at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl." + dataPointTypeIds.replace('-', '_') + "_ImplKnx";
				Class<?> clazz = null;

				try
				{
					log.info("Loading: " + clazzName);
					clazz = Class.forName(clazzName);
				}
				catch (ClassNotFoundException e)
				{
					log.warning(clazzName + " not found. Cannot instantiate according sub data point type. Trying fallback to generic main type.");
					clazzName = "at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl." + "DPT_" + dataPointTypeIds.charAt(5) + "_ImplKnx"; //

					try
					{
						log.info("Loading: " + clazzName);
						clazz = Class.forName(clazzName);
					}
					catch (ClassNotFoundException e1)
					{
						e1.printStackTrace();
					}
				}

				try
				{
					if (clazz != null)
					{
						Constructor<?> constructor = clazz.getConstructor(KNXConnector.class, DataPointInit.class);
						Object[] object = new Object[2];
						object[0] = knxConnector;

						DataPointInit dptInit = new DataPointInit();
						dptInit.setDisplay(dataPointDescription);
						dptInit.setDisplayName(dataPointName);
						dptInit.setReadable(EnumsImpl.getInstance().getEnum(EnumEnabled.HREF).getBool(dataPointReadFlag));
						dptInit.setName(dataPointId);
						dptInit.setGroupAddress(new GroupAddress(Integer.parseInt(groupAddressByDatapointID.get(dataPointId))));
						dptInit.setWritable(EnumsImpl.getInstance().getEnum(EnumEnabled.HREF).getBool(dataPointWriteFlag));

						object[1] = dptInit;
						DatapointImpl dataPoint = (DatapointImpl) constructor.newInstance(object);

						Obj dpValue = dataPoint.get("value");

						if (dpValue != null)
						{
							dpValue.setDisplayName(dataPointDescription);
						}

						// Translations (Datapoint)
						translations = datapointConfig.getProperty("translations.translation[@language]");

						translationsSize = 0;

						if (translations != null)
						{
							translationsSize = 1;
						}

						if (translations instanceof Collection<?>)
						{
							translationsSize = ((Collection<?>) translations).size();
						}

						for (int transIdx = 0; transIdx < translationsSize; transIdx++)
						{

							HierarchicalConfiguration transConfig = datapointConfig.configurationAt("translations.translation(" + transIdx + ")");

							String language = transConfig.getString("[@language]");
							String attribute = transConfig.getString("[@attribute]");
							String value = arrayToString(transConfig.getStringArray("[@value]"));

							try
							{
								if (attribute.toLowerCase().trim().equals("description") && dpValue != null)
								{
									dpValue.addTranslation(language, attribute, value);
								}
								dataPoint.addTranslation(language, attribute, value);
							}
							catch (Exception e)
							{
								log.warning(e.getMessage());
							}
						}

						datapointById.put(dataPointId, dataPoint);
						entity.addDatapoint(dataPoint);

						objectBroker.enableGroupComm(dataPoint);

						objectBroker.addObj(dataPoint, true);
					}

				}
				catch (NoSuchMethodException e)
				{
					log.warning(clazzName + " no such method. Cannot instantiate according datapoint.");
				}
				catch (SecurityException e)
				{
					log.warning(clazzName + " security exception. Cannot instantiate according datapoint.");
				}
				catch (InstantiationException e)
				{
					log.warning(clazzName + " instantiation exception. Cannot instantiate according datapoint.");
				}
				catch (IllegalAccessException e)
				{
					log.warning(clazzName + " illegal access exception. Cannot instantiate according datapoint.");
				}
				catch (IllegalArgumentException e)
				{
					log.warning(clazzName + " illegal argument exception. Cannot instantiate according datapoint.");
					e.printStackTrace();
				}
				catch (InvocationTargetException e)
				{
					log.warning(clazzName + " invocation target exception. Cannot instantiate according datapoint.");
					e.printStackTrace();
				}
			}
		}

		// Phase III create views
		Object buildings = devicesConfig.getProperty("views.building");
		if (buildings != null)
		{
			HierarchicalConfiguration buildingConfig = devicesConfig.configurationAt("views.building");

			objectBroker.addObj(n.getBuilding(), true);

			parseBuildingView(buildingConfig, (Obj) n.getBuilding(), n, objectBroker, entityById);
		}

		Object functional = devicesConfig.getProperty("views.functional");
		if (functional != null)
		{
			HierarchicalConfiguration funcionalView = devicesConfig.configurationAt("views.functional");

			objectBroker.addObj(n.getFunctional(), true);

			parseFunctionalView(funcionalView, (Obj) n.getFunctional(), n, objectBroker, entityById, datapointById);
		}

		Object domains = devicesConfig.getProperty("views.domains");
		if (domains != null)
		{
			HierarchicalConfiguration domainView = devicesConfig.configurationAt("views.domains");

			objectBroker.addObj(n.getDomains(), true);
			parseDomainView(domainView, (Obj) n.getDomains(), n, objectBroker, entityById, datapointById);
		}

		Object topologies = devicesConfig.getProperty("views.topology");
		if (topologies != null)
		{
			HierarchicalConfiguration topologyView = devicesConfig.configurationAt("views.topology");

			objectBroker.addObj(n.getTopology(), true);

			parseTopologyView(topologyView, (Obj) n.getTopology(), n, objectBroker, entityById, datapointById);
		}

	}

	private void parseBuildingView(HierarchicalConfiguration partConfig, Obj parent, Network n, ObjectBroker objectBroker, Hashtable<String, EntityImpl> entityById)
	{

		Object parts = partConfig.getProperty("part.[@id]");

		// identify number of group elements
		int partsSize = 0;

		if (parts != null)
		{
			partsSize = 1;
		}

		if (parts instanceof Collection<?>)
		{
			partsSize = ((Collection<?>) parts).size();
		}

		// if there are no group elements return
		for (int partsIdx = 0; partsIdx < partsSize; partsIdx++)
		{
			String partId = partConfig.getString("part(" + partsIdx + ").[@id]");

			String partName = partConfig.getString("part(" + partsIdx + ").[@name]");

			String partType = partConfig.getString("part(" + partsIdx + ").[@type]");

			PartImpl part = new PartImpl(partId, partName, null, partType);

			// add instances to part
			HierarchicalConfiguration concretePart = partConfig.configurationAt("part(" + partsIdx + ")");
			Object instanceElements = concretePart.getProperty("instance.[@id]");

			int instanceSize = 0;

			if (instanceElements != null)
			{
				instanceSize = 1;
			}

			if (instanceElements instanceof Collection<?>)
			{
				instanceSize = ((Collection<?>) instanceElements).size();
			}

			// add part to parent part
			parent.add(part);

			objectBroker.addObj(part, true);

			// if this part has some instances set, add references to entities
			for (int instanceIdx = 0; instanceIdx < instanceSize; instanceIdx++)
			{
				String instanceId = concretePart.getString("instance(" + instanceIdx + ").[@id]");
				Obj instance = part.addInstance(entityById.get(instanceId));
				objectBroker.addObj(instance);
			}

			// recursively add more parts

			parseBuildingView(concretePart, part, n, objectBroker, entityById);

		}
	}

	private void parseFunctionalView(HierarchicalConfiguration groupConfig, Obj parent, Network n, ObjectBroker objectBroker, Hashtable<String, EntityImpl> entityById, Hashtable<String, DatapointImpl> datapointById)
	{

		Object groups = groupConfig.getProperty("group.[@id]");

		// identify number of group elements
		int groupsSize = 0;

		if (groups != null)
		{
			groupsSize = 1;
		}

		if (groups instanceof Collection<?>)
		{
			groupsSize = ((Collection<?>) groups).size();
		}

		// if there are no group elements return
		for (int groupsIdx = 0; groupsIdx < groupsSize; groupsIdx++)
		{

			String groupId = groupConfig.getString("group(" + groupsIdx + ").[@id]");

			String groupName = groupConfig.getString("group(" + groupsIdx + ").[@name]");

			String groupAddress = groupConfig.getString("group(" + groupsIdx + ").[@type]");

			String groupDescription = groupConfig.getString("group(" + groupsIdx + ").[@type]");

			int address = 0;

			if (groupAddress != null)
			{
				address = Integer.parseInt(groupAddress);
			}

			GroupImpl group = new GroupImpl(groupId, groupName, groupDescription, address);

			// add instances to part
			HierarchicalConfiguration concreteGroup = groupConfig.configurationAt("group(" + groupsIdx + ")");
			Object instanceElements = concreteGroup.getProperty("instance.[@id]");

			int instanceSize = 0;

			if (instanceElements != null)
			{
				instanceSize = 1;
			}

			if (instanceElements instanceof Collection<?>)
			{
				instanceSize = ((Collection<?>) instanceElements).size();
			}
			// add part to parent part
			parent.add(group);

			objectBroker.addObj(group, true);

			// if this part has some instances set, add references to entities
			for (int instanceIdx = 0; instanceIdx < instanceSize; instanceIdx++)
			{
				String instanceId = concreteGroup.getString("instance(" + instanceIdx + ").[@id]");
				group.addInstance(datapointById.get(instanceId), EnumConnector.KEY_SEND);

				try
				{
					DatapointImpl datapointImpl = (DatapointImpl) datapointById.get(instanceId).clone();
					datapointImpl.setName("function");
					group.addFunction(datapointImpl);

					objectBroker.addObj(datapointImpl, true);

				}
				catch (CloneNotSupportedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// recursively add more parts
			parseFunctionalView(concreteGroup, group, n, objectBroker, entityById, datapointById);

		}
	}

	private void parseDomainView(HierarchicalConfiguration domainConfig, Obj parent, Network n, ObjectBroker objectBroker, Hashtable<String, EntityImpl> entityById, Hashtable<String, DatapointImpl> datapointById)
	{

		Object domains = domainConfig.getProperty("domain.[@id]");

		// identify number of group elements
		int domainSize = 0;

		if (domains != null)
		{
			domainSize = 1;
		}

		if (domains instanceof Collection<?>)
		{
			domainSize = ((Collection<?>) domains).size();
		}

		// if there are no group elements return
		for (int domainIdx = 0; domainIdx < domainSize; domainIdx++)
		{

			String domainId = domainConfig.getString("domain(" + domainIdx + ").[@id]");

			String domainName = domainConfig.getString("domain(" + domainIdx + ").[@name]");

			DomainImpl domain = new DomainImpl(domainId, domainName, null);

			// add instances to part
			HierarchicalConfiguration concreteDomain = domainConfig.configurationAt("domain(" + domainIdx + ")");
			Object instanceElements = concreteDomain.getProperty("instance.[@id]");

			int instanceSize = 0;

			if (instanceElements != null)
			{
				instanceSize = 1;
			}

			if (instanceElements instanceof Collection<?>)
			{
				instanceSize = ((Collection<?>) instanceElements).size();
			}
			// add part to parent part
			parent.add(domain);

			objectBroker.addObj(domain, true);

			// if this part has some instances set, add references to entities
			for (int instanceIdx = 0; instanceIdx < instanceSize; instanceIdx++)
			{
				String instanceId = concreteDomain.getString("instance(" + instanceIdx + ").[@id]");
				Obj addInstance = domain.addInstance(entityById.get(instanceId));
				objectBroker.addObj(addInstance, true);

			}

			// recursively add more domains
			parseDomainView(concreteDomain, domain, n, objectBroker, entityById, datapointById);

		}
	}

	private void parseTopologyView(HierarchicalConfiguration areaConfig, Obj parent, Network n, ObjectBroker objectBroker, Hashtable<String, EntityImpl> entityById, Hashtable<String, DatapointImpl> datapointById)
	{

		Object areas = areaConfig.getProperty("area.[@id]");

		// identify number of group elements
		int areaSize = 0;

		if (areas != null)
		{
			areaSize = 1;
		}

		if (areas instanceof Collection<?>)
		{
			areaSize = ((Collection<?>) areas).size();
		}

		// if there are no group elements return
		for (int areaIdx = 0; areaIdx < areaSize; areaIdx++)
		{

			String areaId = areaConfig.getString("area(" + areaIdx + ").[@id]");

			String areaName = areaConfig.getString("area(" + areaIdx + ").[@name]");

			DomainImpl area = new DomainImpl(areaId, areaName, null);

			// add instances to part
			HierarchicalConfiguration concreteArea = areaConfig.configurationAt("area(" + areaIdx + ")");
			Object instanceElements = concreteArea.getProperty("instance.[@id]");

			int instanceSize = 0;

			if (instanceElements != null)
			{
				instanceSize = 1;
			}

			if (instanceElements instanceof Collection<?>)
			{
				instanceSize = ((Collection<?>) instanceElements).size();
			}
			// add part to parent part
			parent.add(area);

			objectBroker.addObj(area, true);

			// if this part has some instances set, add references to entities
			for (int instanceIdx = 0; instanceIdx < instanceSize; instanceIdx++)
			{
				String instanceId = concreteArea.getString("instance(" + instanceIdx + ").[@id]");
				Obj addInstance = area.addInstance(entityById.get(instanceId));
				objectBroker.addObj(addInstance, true);

			}

			// recursively add more domains
			parseTopologyView(concreteArea, area, n, objectBroker, entityById, datapointById);

		}
	}

	private String arrayToString(String[] array)
	{
		StringBuilder dataPointNameBuilder = new StringBuilder();
		for (int i = 0; i < array.length; i++)
		{
			dataPointNameBuilder.append(array[i]);
			if (i < array.length - 1)
			{
				dataPointNameBuilder.append(", ");
			}
		}

		return dataPointNameBuilder.toString();
	}

	@Override
	public void removeDevices(ObjectBroker objectBroker)
	{
		synchronized (myObjects)
		{
			for (String href : myObjects)
			{
				objectBroker.removeObj(href);
			}
		}
	}

	@Override
	public void setConfiguration(XMLConfiguration connectorsConfig)
	{
		this.connectorsConfig = connectorsConfig;
		if (connectorsConfig == null)
		{
			try
			{
				connectorsConfig = new XMLConfiguration(DEVICE_CONFIGURATION_LOCATION);
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}

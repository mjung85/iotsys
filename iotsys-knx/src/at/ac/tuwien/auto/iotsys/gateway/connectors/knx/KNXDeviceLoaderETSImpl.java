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
import obix.Obj.TranslationAttribute;
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
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumEnabled;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.impl.EnumsImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.network.Network;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.network.impl.NetworkImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.AreaImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.DomainImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.GroupImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.PartImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.ViewBuildingImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.ViewDomainsImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.ViewFunctionalImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.ViewTopologyImpl;
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
		log.info("KNX ETS device loader starting. - connectorsConfig: " + connectorsConfig);
		setConfiguration(connectorsConfig);
		

		ArrayList<Connector> connectors = new ArrayList<Connector>();

		log.info("connectors config now: " + connectorsConfig);
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

			Boolean enableGroupComm = subConfig.getBoolean("groupCommEnabled", false);

			Boolean enableHistories = subConfig.getBoolean("historyEnabled", false);

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

				String directory =  "./" + knxProj.substring(knxProj.indexOf("/")+ 1).replaceFirst(".knxproj", "");

				// now the unpacked ETS project should be available in the directory
				String transformFileName =  knxProj.replaceFirst(".knxproj", "") + "/" + file.getName().replaceFirst(".knxproj", "") + ".xml";
				
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

		// Group communication addresses
		parseGroupAddressConfig(devicesConfig.configurationAt("views.functional"), groupAddressByDatapointID);

		// Network
		NetworkImpl n = new NetworkImpl(devicesConfig.getString("[@id]"), arrayToString(devicesConfig.getStringArray("[@name]")), arrayToString(devicesConfig.getStringArray("[@description]")), devicesConfig.getString("[@standard]"));
		networks.add(n);
		networks.add(n.getReference());
		objectBroker.addObj(n, true);

		// Entities
		parseEntites(knxConnector, objectBroker, entityById, datapointById, n, referenceById, groupAddressByDatapointID, enableGroupComm, enableHistories);

		// Views
		Object building = devicesConfig.getProperty("views.building.part[@id]");
		if (building != null)
		{
			objectBroker.addObj(n.getBuilding(), true);
			parseBuildingView(devicesConfig.configurationAt("views.building"), n.getBuilding(), n, objectBroker, entityById);
		}

		Object functional = devicesConfig.getProperty("views.functional.group[@id]");
		if (functional != null)
		{
			objectBroker.addObj(n.getFunctional(), true);
			parseFunctionalView(devicesConfig.configurationAt("views.functional"), n.getFunctional(), n, objectBroker, entityById, datapointById, knxConnector, groupAddressByDatapointID);
		}

		Object domains = devicesConfig.getProperty("views.domains.domain[@id]");
		if (domains != null)
		{
			objectBroker.addObj(n.getDomains(), true);
			parseDomainView(devicesConfig.configurationAt("views.domains"), n.getDomains(), n, objectBroker, entityById, datapointById);
		}

		Object topologies = devicesConfig.getProperty("views.topology.area[@id]");
		if (topologies != null)
		{
			objectBroker.addObj(n.getTopology(), true);
			parseTopologyView(devicesConfig.configurationAt("views.topology"), n.getTopology(), n, objectBroker, entityById, datapointById, referenceById);
		}
	}

	private void parseReferences(Hashtable<String, String> referenceById)
	{
		for (int i = 0; i < sizeOfConfiguration(devicesConfig.getProperty("references.reference.[@id]")); i++)
		{
			String id = devicesConfig.getString("references.reference(" + i + ").[@id]");
			String name = arrayToString(devicesConfig.getStringArray("references.reference(" + i + ").[@name]"));
			referenceById.put(id, name);
		}
	}

	private void parseGroupAddressConfig(HierarchicalConfiguration groupConfig, Hashtable<String, String> groupAddressByDatapointId)
	{
		// if there are no group elements return
		for (int groupsIdx = 0; groupsIdx < sizeOfConfiguration(groupConfig.getProperty("group.[@id]")); groupsIdx++)
		{
			String groupAddress = groupConfig.getString("group(" + groupsIdx + ").[@address]");

			// find instance elements for this group
			HierarchicalConfiguration concreteGroup = groupConfig.configurationAt("group(" + groupsIdx + ")");

			for (int instanceIdx = 0; instanceIdx < sizeOfConfiguration(concreteGroup.getProperty("instance.[@id]")); instanceIdx++)
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

	private void parseEntites(KNXConnector knxConnector, ObjectBroker objectBroker, Hashtable<String, EntityImpl> entityById, Hashtable<String, DatapointImpl> datapointById, NetworkImpl n, Hashtable<String, String> resourceById, Hashtable<String, String> groupAddressByDatapointID, boolean enableGroupComm, boolean enableHistories)
	{
		for (int entityIdx = 0; entityIdx < sizeOfConfiguration(devicesConfig.getProperty("entities.entity[@id]")); entityIdx++)
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
			n.getEntities().addEntity(entity);
			objectBroker.addObj(entity, true);

			// Translations
			for (int transIdx = 0; transIdx < sizeOfConfiguration(entityConfig.getProperty("translations.translation[@language]")); transIdx++)
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

			// DPs
			for (int datapointIdx = 0; datapointIdx < sizeOfConfiguration(entityConfig.getProperty("datapoints.datapoint[@id]")); datapointIdx++)
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

						datapointById.put(dataPointId, dataPoint);
						entity.addDatapoint(dataPoint);

						if(enableGroupComm)
							objectBroker.enableGroupComm(dataPoint);
						if(enableHistories)
							objectBroker.addHistoryToDatapoints(dataPoint);
						objectBroker.addObj(dataPoint, true);

						// Search for child "value"
						Obj dpValue = dataPoint.get("value");
						if (dpValue != null)
						{
							dpValue.setDisplayName(dataPointDescription);
						}

						// Translations (DP)
						for (int transIdx = 0; transIdx < sizeOfConfiguration(datapointConfig.getProperty("translations.translation[@language]")); transIdx++)
						{
							HierarchicalConfiguration transConfig = datapointConfig.configurationAt("translations.translation(" + transIdx + ")");

							String language = transConfig.getString("[@language]");
							String attribute = transConfig.getString("[@attribute]");
							String value = arrayToString(transConfig.getStringArray("[@value]"));

							try
							{
								dataPoint.addTranslation(language, attribute, value);

								// translation for DisplayName of value
								if (attribute.toLowerCase().trim().equals("description") && dpValue != null)
								{
									dpValue.addTranslation(language, TranslationAttribute.displayName, value);
								}
							}
							catch (Exception e)
							{
								log.warning(e.getMessage());
							}
						}
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
	}

	private void parseBuildingView(HierarchicalConfiguration partConfig, Obj parent, Network n, ObjectBroker objectBroker, Hashtable<String, EntityImpl> entityById)
	{
		for (int partsIdx = 0; partsIdx < sizeOfConfiguration(partConfig.getProperty("part.[@id]")); partsIdx++)
		{
			String partId = partConfig.getString("part(" + partsIdx + ").[@id]");
			String partName = arrayToString(partConfig.getStringArray("part(" + partsIdx + ").[@name]"));
			String partDescription = arrayToString(partConfig.getStringArray("part(" + partsIdx + ").[@description]"));
			String partType = partConfig.getString("part(" + partsIdx + ").[@type]");

			PartImpl part = new PartImpl(partId, partName, partDescription, partType);

			// add part to parent part
			if (parent instanceof ViewBuildingImpl)
				((ViewBuildingImpl) parent).addPart(part);
			else if (parent instanceof PartImpl)
				((PartImpl) parent).addPart(part);
			else
				parent.add(part);

			objectBroker.addObj(part, true);

			// add instances to part
			HierarchicalConfiguration concretePart = partConfig.configurationAt("part(" + partsIdx + ")");

			for (int instanceIdx = 0; instanceIdx < sizeOfConfiguration(concretePart.getProperty("instance.[@id]")); instanceIdx++)
			{
				String instanceId = concretePart.getString("instance(" + instanceIdx + ").[@id]");
				Obj instance = part.addInstance(entityById.get(instanceId));
				objectBroker.addObj(instance);
			}

			// recursively add more parts
			parseBuildingView(concretePart, part, n, objectBroker, entityById);

		}
	}

	private void parseFunctionalView(HierarchicalConfiguration groupConfig, Obj parent, Network n, ObjectBroker objectBroker, Hashtable<String, EntityImpl> entityById, Hashtable<String, DatapointImpl> datapointById, KNXConnector knxConnector, Hashtable<String, String> groupAddressByDatapointId)
	{
		for (int groupsIdx = 0; groupsIdx < sizeOfConfiguration(groupConfig.getProperty("group.[@id]")); groupsIdx++)
		{
			String groupId = groupConfig.getString("group(" + groupsIdx + ").[@id]");
			String groupName = arrayToString(groupConfig.getStringArray("group(" + groupsIdx + ").[@name]"));
			String groupDescription = arrayToString(groupConfig.getStringArray("group(" + groupsIdx + ").[@description]"));
			long groupAddress = groupConfig.getLong("group(" + groupsIdx + ").[@address]");

			GroupImpl group = new GroupImpl(groupId, groupName, groupDescription, groupAddress);

			// add part to parent part
			if (parent instanceof ViewFunctionalImpl)
				((ViewFunctionalImpl) parent).addGroup(group);
			else if (parent instanceof GroupImpl)
				((GroupImpl) parent).addGroup(group);
			else
				parent.add(group);

			objectBroker.addObj(group, true);

			// add instances to part
			HierarchicalConfiguration concreteGroup = groupConfig.configurationAt("group(" + groupsIdx + ")");

			for (int instanceIdx = 0; instanceIdx < sizeOfConfiguration(concreteGroup.getProperty("instance.[@id]")); instanceIdx++)
			{
				String instanceId = concreteGroup.getString("instance(" + instanceIdx + ").[@id]");
				String connector = concreteGroup.getString("instance(" + instanceIdx + ").[@connector]");

				try
				{
					DatapointImpl dp = datapointById.get(instanceId);
					Class<?> clazz = dp.getClass();

					if (clazz != null)
					{
						Constructor<?> constructor = clazz.getConstructor(KNXConnector.class, DataPointInit.class);
						Object[] object = new Object[2];
						object[0] = knxConnector;

						DataPointInit dptInit = new DataPointInit();
						dptInit.setName("function");
						dptInit.setReadable(dp.isValueReadable());
						dptInit.setWritable(dp.isValueWritable());
						dptInit.setGroupAddress(new GroupAddress(Integer.parseInt(groupAddressByDatapointId.get(instanceId))));

						object[1] = dptInit;
						DatapointImpl dataPoint = (DatapointImpl) constructor.newInstance(object);

						group.addFunction(dataPoint);
						objectBroker.addObj(dataPoint, true);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				group.addInstance(datapointById.get(instanceId), connector);
			}

			// recursively add more parts
			parseFunctionalView(concreteGroup, group, n, objectBroker, entityById, datapointById, knxConnector, groupAddressByDatapointId);
		}
	}

	private void parseDomainView(HierarchicalConfiguration domainConfig, Obj parent, Network n, ObjectBroker objectBroker, Hashtable<String, EntityImpl> entityById, Hashtable<String, DatapointImpl> datapointById)
	{
		for (int domainIdx = 0; domainIdx < sizeOfConfiguration(domainConfig.getProperty("domain.[@id]")); domainIdx++)
		{
			String domainId = domainConfig.getString("domain(" + domainIdx + ").[@id]");
			String domainName = arrayToString(domainConfig.getStringArray("domain(" + domainIdx + ").[@name]"));
			String domainDescription = arrayToString(domainConfig.getStringArray("domain(" + domainIdx + ").[@description]"));

			DomainImpl domain = new DomainImpl(domainId, domainName, domainDescription);

			// add part to parent part
			if (parent instanceof ViewDomainsImpl)
				((ViewDomainsImpl) parent).addDomain(domain);
			else if (parent instanceof DomainImpl)
				((DomainImpl) parent).addDomain(domain);
			else
				parent.add(domain);

			objectBroker.addObj(domain, true);

			// add instances to part
			HierarchicalConfiguration concreteDomain = domainConfig.configurationAt("domain(" + domainIdx + ")");

			for (int instanceIdx = 0; instanceIdx < sizeOfConfiguration(concreteDomain.getProperty("instance.[@id]")); instanceIdx++)
			{
				String instanceId = concreteDomain.getString("instance(" + instanceIdx + ").[@id]");
				Obj addInstance = domain.addInstance(entityById.get(instanceId));
				objectBroker.addObj(addInstance, true);
			}

			// recursively add more domains
			parseDomainView(concreteDomain, domain, n, objectBroker, entityById, datapointById);
		}
	}

	private void parseTopologyView(HierarchicalConfiguration areaConfig, Obj parent, Network n, ObjectBroker objectBroker, Hashtable<String, EntityImpl> entityById, Hashtable<String, DatapointImpl> datapointById, Hashtable<String, String> resourceById)
	{
		for (int areaIdx = 0; areaIdx < sizeOfConfiguration(areaConfig.getProperty("area.[@id]")); areaIdx++)
		{
			String areaId = areaConfig.getString("area(" + areaIdx + ").[@id]");
			String areaName = arrayToString(areaConfig.getStringArray("area(" + areaIdx + ").[@name]"));
			String areaDescription = arrayToString(areaConfig.getStringArray("area(" + areaIdx + ").[@description]"));
			String areaMediaTypeId = areaConfig.getString("area(" + areaIdx + ").[@mediaTypeId]");
			long areaAddress = areaConfig.getLong("area(" + areaIdx + ").[@address]");

			String areaMediaType = null;
			if (areaMediaTypeId != null)
			{
				areaMediaType = resourceById.get(areaMediaTypeId);
			}

			AreaImpl area = new AreaImpl(areaId, areaName, areaDescription, areaAddress, areaMediaType);

			// add part to parent part
			if (parent instanceof ViewTopologyImpl)
				((ViewTopologyImpl) parent).addArea(area);
			else if (parent instanceof DomainImpl)
				((AreaImpl) parent).addArea(area);
			else
				parent.add(area);

			objectBroker.addObj(area, true);

			// add instances to part
			HierarchicalConfiguration concreteArea = areaConfig.configurationAt("area(" + areaIdx + ")");

			for (int instanceIdx = 0; instanceIdx < sizeOfConfiguration(concreteArea.getProperty("instance.[@id]")); instanceIdx++)
			{
				String instanceId = concreteArea.getString("instance(" + instanceIdx + ").[@id]");
				long address = concreteArea.getLong("instance(" + instanceIdx + ").[@address]");

				Obj addInstance = area.addInstance(entityById.get(instanceId), address);
				objectBroker.addObj(addInstance, true);

			}

			// recursively add more domains
			parseTopologyView(concreteArea, area, n, objectBroker, entityById, datapointById, resourceById);
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

	private int sizeOfConfiguration(Object configuration)
	{
		int size = 0;

		if (configuration != null)
		{
			size = 1;
		}

		if (configuration instanceof Collection<?>)
		{
			size = ((Collection<?>) configuration).size();
		}
		return size;
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
				log.info("Loading XML configuration from " + DEVICE_CONFIGURATION_LOCATION);
				this.connectorsConfig = new XMLConfiguration(DEVICE_CONFIGURATION_LOCATION);
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}

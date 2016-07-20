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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import obix.Contract;
import obix.List;
import obix.Obj.TranslationAttribute;
import obix.Uri;

import org.apache.commons.configuration.XMLConfiguration;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.entity.impl.EntityImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumConnector;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumPart;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.network.Network;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.network.impl.NetworkImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.AreaImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.DomainImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.GroupImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl.PartImpl;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl.DPST_1_1_ImplKnx;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl.DPST_3_7_ImplKnx;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl.DPST_5_1_ImplKnx;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl.DPST_9_1_ImplKnx;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl.DPST_9_8_ImplKnx;

public class KNXDeviceLoaderETSImplManual implements DeviceLoader
{
	private static Logger log = Logger.getLogger(KNXDeviceLoaderImpl.class.getName());

	private XMLConfiguration devicesConfig;

	private ArrayList<String> myObjects = new ArrayList<String>();

	public ArrayList<Connector> initDevices(ObjectBroker objectBroker)
	{
		setConfiguration(devicesConfig);

		ArrayList<Connector> connectors = new ArrayList<Connector>();

		KNXConnector knxConnector = new KNXConnector("192.168.161.59", 3671, "auto");

		connect(knxConnector);

		initNetworks(knxConnector, objectBroker);

		connectors.add(knxConnector);

		return connectors;
	}

	private void connect(KNXConnector knxConnector)
	{
		// try
		// {
		// knxConnector.connect();
		// }
		// catch (UnknownHostException e)
		// {
		// e.printStackTrace();
		// }
		// catch (KNXException e)
		// {
		// e.printStackTrace();
		// }
	}

	private void initNetworks(KNXConnector knxConnector, ObjectBroker objectBroker)
	{
		// Networks
		List networks = new List();
		networks.setName("networks");
		networks.setOf(new Contract(Network.CONTRACT));
		networks.setHref(new Uri("/networks"));

		// Network
		NetworkImpl n = new NetworkImpl("P-0341", "Suitcase", null, "KNX");
		networks.add(n);
		networks.add(n.getReference());

		objectBroker.addObj(n, true);

		// Entities and Datapoints
		EntityImpl entity = new EntityImpl("P-0341-0_DI-3", "Switching actuator N 567/01, (8 Amp)", null, "Siemens", "5WG1 567-1AB01");
		entity.addTranslation("de-DE", TranslationAttribute.displayName, "Schaltaktor N 567/01, (8 Amp)");
		n.getEntities().addEntity(entity);

		objectBroker.addObj(entity, true);

		DPST_1_1_ImplKnx datapoint_lightonoff = new DPST_1_1_ImplKnx(knxConnector, new GroupAddress(1, 0, 0), "P-0341-0_DI-3_M-0001_A-9803-03-3F77_O-3_R-4", "Switch, Channel A", "On / Off", true, false);
		datapoint_lightonoff.addTranslation("de-DE", TranslationAttribute.displayName, "Schalten, Kanal A");
		entity.addDatapoint(datapoint_lightonoff);

		objectBroker.addObj(datapoint_lightonoff, true);

		entity = new EntityImpl("P-0341-0_DI-2", "Universal Dimmer N 527", null, "Siemens", "5WG1 527-1AB01");
		entity.addTranslation("de-DE", TranslationAttribute.displayName, "Universal-Dimmer N 527");
		n.getEntities().addEntity(entity);

		objectBroker.addObj(entity, true);

		DPST_3_7_ImplKnx datapoint_dimming = new DPST_3_7_ImplKnx(knxConnector, new GroupAddress(1, 0, 1), "P-0341-0_DI-2_M-0001_A-6102-01-A218_O-1_R-1", "Dimming", "Brighter / Darker");
		entity.addDatapoint(datapoint_dimming);

		objectBroker.addObj(datapoint_dimming, true);

		DPST_5_1_ImplKnx datapoint_dimming_status = new DPST_5_1_ImplKnx(knxConnector, new GroupAddress(1, 0, 3), "P-0341-0_DI-2_M-0001_A-6102-01-A218_O-3_R-3", "Status", "8-bit Value", false, true);
		entity.addDatapoint(datapoint_dimming_status);

		objectBroker.addObj(datapoint_dimming_status, true);

		entity = new EntityImpl("P-0341-0_DI-11", "Temperature Sensor N 258/02", null, "Siemens", "5WG1 258-1AB02");
		entity.addTranslation("de-DE", TranslationAttribute.displayName, "Temperatursensor N 258/02");
		n.getEntities().addEntity(entity);

		objectBroker.addObj(entity, true);

		DPST_9_1_ImplKnx datapoint_temperature = new DPST_9_1_ImplKnx(knxConnector, new GroupAddress(1, 1, 0), "P-0341-0_DI-11_M-0001_A-9814-01-5F7E_O-0_R-2", "Temperature, Channel A", "°C-value (EIS5)", false, true);
		entity.addDatapoint(datapoint_temperature);

		objectBroker.addObj(datapoint_temperature, true);

		entity = new EntityImpl("P-0341-0_DI-7", "KNX CO², Humidity and Temperature Sensor", null, "Schneider Electric Industries SAS", "MTN6005-0001");
		entity.addTranslation("de-DE", TranslationAttribute.displayName, "KNX CO2-, Feuchte- und Temperatursensor");
		n.getEntities().addEntity(entity);

		objectBroker.addObj(entity, true);

		DPST_9_8_ImplKnx datapoint_co2 = new DPST_9_8_ImplKnx(knxConnector, new GroupAddress(1, 3, 0), "P-0341-0_DI-7_M-0064_A-FF21-11-DDFC-O0048_O-0_R-1", "CO2 Value", "Physical Value", false, true);
		entity.addDatapoint(datapoint_co2);

		objectBroker.addObj(datapoint_co2, true);

		// Views
		PartImpl building = new PartImpl("P-0341-0_BP-1", "Treitlstraße 1-3", null, EnumPart.KEY_BUILDING);
		PartImpl floor = new PartImpl("P-0341-0_BP-2", "4. Stock", null, EnumPart.KEY_FLOOR);
		PartImpl board = new PartImpl("P-0341-0_BP-4", "Suitcase", null, EnumPart.KEY_DISTRIBUTIONBOARD);

		building.addPart(floor);
		floor.addPart(board);
		board.addInstance(entity);
		n.getBuilding().addPart(building);

		GroupImpl all = new GroupImpl("P-0341-0_GR-1", "All component", null, 2048);
		GroupImpl light = new GroupImpl("P-0341-0_GR-2", "Light", "Contains groups for lighting", 2048);
		GroupImpl lightonoff = new GroupImpl("P-0341-0_GA-1", "Light on/off", null, 2048);

		all.addGroup(light);
		light.addGroup(lightonoff);
		lightonoff.addFunction(new DPST_1_1_ImplKnx(knxConnector, new GroupAddress(1, 0, 0), "function", null, null, true, false));
		lightonoff.addInstance(datapoint_lightonoff, EnumConnector.KEY_SEND);
		n.getFunctional().addGroup(all);

		AreaImpl area = new AreaImpl("P-0341-0_A-2", "All component", "Zone 8", 8, null);
		AreaImpl subarea = new AreaImpl("P-0341-0_L-2", "Main Line", "Line 0", 0, "Twisted Pair");

		area.addArea(subarea);
		subarea.addInstance(entity, 3);
		n.getTopology().addArea(area);

		DomainImpl domain = new DomainImpl("P-0341-0_T-1", "Suitcase", null);
		DomainImpl subdomain = new DomainImpl("P-0341-0_T-0", "Beleuchtung", null);

		domain.addDomain(subdomain);
		subdomain.addInstance(entity);
		n.getDomains().addDomain(domain);

		objectBroker.addObj(networks, true);
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
	public void setConfiguration(XMLConfiguration devicesConfiguration)
	{
		this.devicesConfig = devicesConfiguration;
		if (devicesConfiguration == null)
		{
			try
			{
				devicesConfig = new XMLConfiguration(DEVICE_CONFIGURATION_LOCATION);
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}

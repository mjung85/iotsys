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
import obix.Int;
import obix.List;
import obix.Obj;
import obix.Ref;
import obix.Str;
import obix.Uri;

import org.apache.commons.configuration.XMLConfiguration;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPST_1_1;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPST_3_7;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPST_9_1;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.entity.impl.EntityImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.impl.EnumStandardImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.network.impl.NetworkImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.impl.PartImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl.DPST_1_1_ImplKnx;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl.DPST_3_7_ImplKnx;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl.DPST_9_1_ImplKnx;

public class KNXDeviceLoaderETSImpl implements DeviceLoader
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
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// catch (KNXException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	private void initNetworks(KNXConnector knxConnector, ObjectBroker objectBroker)
	{
		// skip standard element since for KNX mapping only knx is relevant.

		// Networks
		Obj networks = new Obj();
		networks.setName("networks");
		networks.setHref(new Uri("/networks"));

		// Workaround, addObj registers currently only the obj and direct
		// children

		// Network
		Obj network = new Obj();
		network.setName("P-0341");
		network.setHref(new Uri(networks.getHref().getPath() + "/" + "suitcase"));
		network.setDisplayName("Suitcase");
		network.setDisplay("...");
		network.setIs(new Contract("knx:network"));

		obix.Enum standard = new obix.Enum();
		standard.setName("standard");
		standard.setHref(new Uri("standard"));
		standard.setRange(new Uri("/enums/enumStandard"));
		standard.set("knx");
		network.add(standard);
		objectBroker.addObj(standard, false);

		// Network Reference
		// Note: build references after building the real objects, so that href
		// is registered for real object
		Ref networkRef = new Ref();
		networkRef.setName("P-0341");
		networkRef.setHref(new Uri("suitcase"));
		networkRef.setDisplayName("Suitcase");
		networkRef.setIs(new Contract("knx:network"));
		networks.add(networkRef);
		objectBroker.addObj(networkRef, false);

		// Entities
		initEntities(knxConnector, objectBroker, network);

		// Views
		initViews(knxConnector, objectBroker, network);

		
		// Phase II
		
		// Enumerations
		EnumStandardImpl e = new EnumStandardImpl();
		objectBroker.addObj(e, true);
		
		// Units
		
		// Network
		NetworkImpl n = new NetworkImpl("P-0341/2", "Suitcase2", null, e.getKey("KNX"));
		networks.add(n);
		networks.add(n.getReference(false));
		
		EntityImpl entity = new EntityImpl("P-0944-0_DI-1","Temperature Sensor N 258/02", null);
		
		n.getEntities().addEntity(entity);
		
		PartImpl part = new PartImpl("P-01EE-0_BP-0", "Treitlstraße 1-3", null);

		n.getBuilding().addPart(part);
		
		part.addInstance(entity);
		
		objectBroker.addObj(networks, true);

	}

	private void initViews(KNXConnector knxConnector, ObjectBroker objectBroker, Obj network)
	{
		initFunctional(knxConnector, objectBroker, network);
		initTopology(knxConnector, objectBroker, network);
		initBuilding(knxConnector, objectBroker, network);
		initDomains(knxConnector, objectBroker, network);
	}

	private void initFunctional(KNXConnector knxConnector, ObjectBroker objectBroker, Obj network)
	{
		// Functional View
		Obj functional = new Obj();
		functional.setHidden(true);
		functional.setName("functional");
		functional.setDisplay("Funktionale Sicht des Netzwerks");
		functional.setHref(new Uri(network.getHref().getPath() + "/" + "views/functional"));
		functional.setIs(new Contract("knx:viewFunctional"));
		objectBroker.addObj(functional, false);

		// Reference
		Ref functionalRef = new Ref();
		functionalRef.setName("functional");
		functionalRef.setHref(new Uri("views/functional"));
		functionalRef.setIs(new Contract("knx:viewFunctional"));

		network.add(functionalRef);
		objectBroker.addObj(functionalRef, false);

		// List of entities
		List list = new List();
		list.setName("groups");
		list.setOf(new Contract("knx:group"));
		list.setHref(new Uri("groups"));
		functional.add(list);
		objectBroker.addObj(list, false);

		// All component
		Obj all = new Obj();
		all.setName("P-0341-0_GR-1");
		all.setDisplayName("All component");
		all.setIs(new Contract("knx:group"));

		// TODO change relative HREFS !!!!!!!
		all.setHref(new Uri("all_component"));

		list.add(all);
		objectBroker.addObj(all, false);

		Int address = new Int();
		address.setName("address");
		address.setHref(new Uri(all.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(2048);
		all.add(address);
		objectBroker.addObj(address, false);

		list = new List();
		list.setName("groups");
		list.setOf(new Contract("knx:group"));
		list.setHref(new Uri(all.getHref().getPath() + "/" + "groups"));
		all.add(list);
		objectBroker.addObj(list, false);

		// Light
		Obj middle = new Obj();
		middle.setName("P-0341-0_GR-2");
		middle.setDisplayName("Light");
		middle.setIs(new Contract("knx:group"));
		middle.setHref(new Uri(list.getHref().getPath() + "/" + "light"));
		list.add(middle);
		objectBroker.addObj(middle, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(middle.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(2048);
		middle.add(address);
		objectBroker.addObj(address, false);

		List middleList = new List();
		middleList.setName("groups");
		middleList.setOf(new Contract("knx:group"));
		middleList.setHref(new Uri(middle.getHref().getPath() + "/" + "groups"));
		middle.add(middleList);
		objectBroker.addObj(middleList, false);

		// Light on/off
		Obj group = new Obj();
		group.setName("P-0341-0_GA-1");
		group.setDisplayName("Light on/off");
		group.setIs(new Contract("knx:group"));
		group.setHref(new Uri(middleList.getHref().getPath() + "/" + "light_on_off"));
		middleList.add(group);
		objectBroker.addObj(group, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(group.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(2048);
		group.add(address);
		objectBroker.addObj(address, false);

		// List of instances
		List instances = new List();
		instances.setName("instances");
		instances.setOf(new Contract("knx:instanceFunctional"));
		instances.setHref(new Uri(group.getHref().getPath() + "/" + "instances"));
		group.add(instances);
		objectBroker.addObj(instances, false);

		Obj instance = new Obj();
		instance.setName("M-0001_A-9803-03-3F77_O-3_R-4");
		instance.setHref(new Uri(instances.getHref().getPath() + "/" + "1"));
		instance.setIs(new Contract("knx:instanceFunctional"));
		instances.add(instance);
		objectBroker.addObj(instance, false);

		obix.Enum connector = new obix.Enum();
		connector.setName("connector");
		connector.setHref(new Uri(instance.getHref().getPath() + "/" + "connector"));
		connector.setRange(new Uri("/enums/enumConnector"));
		connector.set("send");
		instance.add(connector);
		objectBroker.addObj(connector, false);

		Ref reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "switching_actuator_n_567_01_8_amp" + "/1/datapoints/switch_channel_a"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// Dimmer
		group = new Obj();
		group.setName("P-0341-0_GA-2");
		group.setDisplayName("Dimmer light");
		group.setIs(new Contract("knx:group"));
		group.setHref(new Uri(middleList.getHref().getPath() + "/" + "dimmer_light"));
		middleList.add(group);
		objectBroker.addObj(group, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(group.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(2049);
		group.add(address);
		objectBroker.addObj(address, false);

		// List of instances
		instances = new List();
		instances.setName("instances");
		instances.setOf(new Contract("knx:instanceFunctional"));
		instances.setHref(new Uri(group.getHref().getPath() + "/" + "instances"));
		group.add(instances);
		objectBroker.addObj(instances, false);

		instance = new Obj();
		instance.setName("M-0001_A-6102-01-A218_O-1_R-1");
		instance.setHref(new Uri(instances.getHref().getPath() + "/" + "1"));
		instance.setIs(new Contract("knx:instanceFunctional"));
		instances.add(instance);
		objectBroker.addObj(instance, false);

		connector = new obix.Enum();
		connector.setName("connector");
		connector.setHref(new Uri(instance.getHref().getPath() + "/" + "connector"));
		connector.setRange(new Uri("/enums/enumConnector"));
		connector.set("send");
		instance.add(connector);
		objectBroker.addObj(connector, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "universal_dimmer_n_527" + "/1/datapoints/dimming"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// Temperature
		middle = new Obj();
		middle.setName("P-0341-0_GR-3");
		middle.setDisplayName("Temperature");
		middle.setIs(new Contract("knx:group"));
		middle.setHref(new Uri(list.getHref().getPath() + "/" + "temperature"));
		list.add(middle);
		objectBroker.addObj(middle, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(middle.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(2304);
		middle.add(address);
		objectBroker.addObj(address, false);

		middleList = new List();
		middleList.setName("groups");
		middleList.setOf(new Contract("knx:group"));
		middleList.setHref(new Uri(middle.getHref().getPath() + "/" + "groups"));
		middle.add(middleList);
		objectBroker.addObj(middleList, false);

		// Temp 1
		group = new Obj();
		group.setName("P-0341-0_GA-3");
		group.setDisplayName("Temperature1");
		group.setIs(new Contract("knx:group"));
		group.setHref(new Uri(middleList.getHref().getPath() + "/" + "temperature1"));
		middleList.add(group);
		objectBroker.addObj(group, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(group.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(2304);
		group.add(address);
		objectBroker.addObj(address, false);

		// List of instances
		instances = new List();
		instances.setName("instances");
		instances.setOf(new Contract("knx:instanceFunctional"));
		instances.setHref(new Uri(group.getHref().getPath() + "/" + "instances"));
		group.add(instances);
		objectBroker.addObj(instances, false);

		instance = new Obj();
		instance.setName("M-0001_A-9814-01-5F7E_O-0_R-2");
		instance.setHref(new Uri(instances.getHref().getPath() + "/" + "1"));
		instance.setIs(new Contract("knx:instanceFunctional"));
		instances.add(instance);
		objectBroker.addObj(instance, false);

		connector = new obix.Enum();
		connector.setName("connector");
		connector.setHref(new Uri(instance.getHref().getPath() + "/" + "connector"));
		connector.setRange(new Uri("/enums/enumConnector"));
		connector.set("send");
		instance.add(connector);
		objectBroker.addObj(connector, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "temperature_sensor_n_258_02" + "/1/datapoints/temperatur_kanal_a"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// Temp 2
		group = new Obj();
		group.setName("P-0341-0_GA-4");
		group.setDisplayName("Temperature2");
		group.setIs(new Contract("knx:group"));
		group.setHref(new Uri(middleList.getHref().getPath() + "/" + "temperature2"));
		middleList.add(group);
		objectBroker.addObj(group, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(group.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(2305);
		group.add(address);
		objectBroker.addObj(address, false);

		// List of instances
		instances = new List();
		instances.setName("instances");
		instances.setOf(new Contract("knx:instanceFunctional"));
		instances.setHref(new Uri(group.getHref().getPath() + "/" + "instances"));
		group.add(instances);
		objectBroker.addObj(instances, false);

		instance = new Obj();
		instance.setName("M-0001_A-9814-01-5F7E_O-1_R-3");
		instance.setHref(new Uri(instances.getHref().getPath() + "/" + "1"));
		instance.setIs(new Contract("knx:instanceFunctional"));
		instances.add(instance);
		objectBroker.addObj(instance, false);

		connector = new obix.Enum();
		connector.setName("connector");
		connector.setHref(new Uri(instance.getHref().getPath() + "/" + "connector"));
		connector.setRange(new Uri("/enums/enumConnector"));
		connector.set("send");
		instance.add(connector);
		objectBroker.addObj(connector, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "temperature_sensor_n_258_02" + "/1/datapoints/temperatur_kanal_b"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// Temp 3
		group = new Obj();
		group.setName("P-0341-0_GA-5");
		group.setDisplayName("Temperature3");
		group.setIs(new Contract("knx:group"));
		group.setHref(new Uri(middleList.getHref().getPath() + "/" + "temperature3"));
		middleList.add(group);
		objectBroker.addObj(group, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(group.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(2306);
		group.add(address);
		objectBroker.addObj(address, false);

		// List of instances
		instances = new List();
		instances.setName("instances");
		instances.setOf(new Contract("knx:instanceFunctional"));
		instances.setHref(new Uri(group.getHref().getPath() + "/" + "instances"));
		group.add(instances);
		objectBroker.addObj(instances, false);

		instance = new Obj();
		instance.setName("M-0001_A-9814-01-5F7E_O-2_R-1");
		instance.setHref(new Uri(instances.getHref().getPath() + "/" + "1"));
		instance.setIs(new Contract("knx:instanceFunctional"));
		instances.add(instance);
		objectBroker.addObj(instance, false);

		connector = new obix.Enum();
		connector.setName("connector");
		connector.setHref(new Uri(instance.getHref().getPath() + "/" + "connector"));
		connector.setRange(new Uri("/enums/enumConnector"));
		connector.set("send");
		instance.add(connector);
		objectBroker.addObj(connector, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "temperature_sensor_n_258_02" + "/1/datapoints/temperatur_kanal_c"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// Temp 4
		group = new Obj();
		group.setName("P-0341-0_GA-6");
		group.setDisplayName("Temperature4");
		group.setIs(new Contract("knx:group"));
		group.setHref(new Uri(middleList.getHref().getPath() + "/" + "temperature4"));
		middleList.add(group);
		objectBroker.addObj(group, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(group.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(2307);
		group.add(address);
		objectBroker.addObj(address, false);

		// List of instances
		instances = new List();
		instances.setName("instances");
		instances.setOf(new Contract("knx:instanceFunctional"));
		instances.setHref(new Uri(group.getHref().getPath() + "/" + "instances"));
		group.add(instances);
		objectBroker.addObj(instances, false);

		instance = new Obj();
		instance.setName("M-0001_A-9814-01-5F7E_O-3_R-4");
		instance.setHref(new Uri(instances.getHref().getPath() + "/" + "1"));
		instance.setIs(new Contract("knx:instanceFunctional"));
		instances.add(instance);
		objectBroker.addObj(instance, false);

		connector = new obix.Enum();
		connector.setName("connector");
		connector.setHref(new Uri(instance.getHref().getPath() + "/" + "connector"));
		connector.setRange(new Uri("/enums/enumConnector"));
		connector.set("send");
		instance.add(connector);
		objectBroker.addObj(connector, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "temperature_sensor_n_258_02" + "/1/datapoints/temperatur_kanal_d"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// Sun Blind
		middle = new Obj();
		middle.setName("P-0341-0_GR-4");
		middle.setDisplayName("Sun Blind");
		middle.setIs(new Contract("knx:group"));
		middle.setHref(new Uri(list.getHref().getPath() + "/" + "sun_blind"));
		list.add(middle);
		objectBroker.addObj(middle, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(middle.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(2560);
		middle.add(address);
		objectBroker.addObj(address, false);

		middleList = new List();
		middleList.setName("groups");
		middleList.setOf(new Contract("knx:group"));
		middleList.setHref(new Uri(middle.getHref().getPath() + "/" + "groups"));
		middle.add(middleList);
		objectBroker.addObj(middleList, false);

		// Diverses
		middle = new Obj();
		middle.setName("P-0341-0_GR-5");
		middle.setDisplayName("Diverses");
		middle.setIs(new Contract("knx:group"));
		middle.setHref(new Uri(list.getHref().getPath() + "/" + "diverses"));
		list.add(middle);
		objectBroker.addObj(middle, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(middle.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(2816);
		middle.add(address);
		objectBroker.addObj(address, false);

		middleList = new List();
		middleList.setName("groups");
		middleList.setOf(new Contract("knx:group"));
		middleList.setHref(new Uri(middle.getHref().getPath() + "/" + "groups"));
		middle.add(middleList);
		objectBroker.addObj(middleList, false);
	}

	private void initTopology(KNXConnector knxConnector, ObjectBroker objectBroker, Obj network)
	{
		// Topology View
		Obj topology = new Obj();
		topology.setName("topology");
		topology.setDisplay("Topologie des Netzwerks");
		topology.setHref(new Uri(network.getHref().getPath() + "/" + "views/topology"));
		topology.setIs(new Contract("knx:viewTopology"));

		objectBroker.addObj(topology, false);

		// Reference
		Ref topologyRef = new Ref();
		topologyRef.setName("topology");
		topologyRef.setHref(new Uri("views/topology"));
		topologyRef.setIs(new Contract("knx:viewTopology"));

		network.add(topologyRef);
		objectBroker.addObj(topologyRef, false);

		// List of entities
		List list = new List();
		list.setName("areas");
		list.setOf(new Contract("knx:area"));
		list.setHref(new Uri("areas"));
		topology.add(list);
		objectBroker.addObj(list, false);

		// Area
		Obj all = new Obj();
		all.setName("P-0341-0_A-2");
		all.setDisplayName("All component");
		all.setIs(new Contract("knx:area"));
		all.setHref(new Uri(list.getHref().getPath() + "/" + "all_component"));
		list.add(all);
		objectBroker.addObj(all, false);

		Int address = new Int();
		address.setName("address");
		address.setHref(new Uri(all.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(8);
		all.add(address);
		objectBroker.addObj(address, false);

		// List of areas
		list = new List();
		list.setName("areas");
		list.setOf(new Contract("knx:area"));
		list.setHref(new Uri(all.getHref().getPath() + "/" + "areas"));
		all.add(list);
		objectBroker.addObj(list, false);

		// Line
		Obj line = new Obj();
		line.setName("P-0341-0_L-2");
		line.setDisplayName("Main Line");
		line.setIs(new Contract("knx:area"));
		line.setHref(new Uri(list.getHref().getPath() + "/" + "main_line"));
		list.add(line);
		objectBroker.addObj(line, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(line.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(0);
		line.add(address);
		objectBroker.addObj(address, false);

		Str mediaType = new Str();
		mediaType.setName("mediaType");
		mediaType.setHref(new Uri(line.getHref().getPath() + "/" + "mediaType"));
		mediaType.set("Twisted Pair");
		line.add(mediaType);
		objectBroker.addObj(mediaType, false);

		// List of instances
		list = new List();
		list.setName("instances");
		list.setOf(new Contract("knx:instanceTopology"));
		list.setHref(new Uri(line.getHref().getPath() + "/" + "instances"));
		line.add(list);
		objectBroker.addObj(list, false);

		// Instances

		// 1
		Obj instance = new Obj();
		instance.setName("P-0341-0_DI-1");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "1"));
		instance.setIs(new Contract("knx:instanceTopology"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(instance.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(1);
		instance.add(address);
		objectBroker.addObj(address, false);

		Ref reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "shutter_switch_n_522_02" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 2
		instance = new Obj();
		instance.setName("P-0341-0_DI-2");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "2"));
		instance.setIs(new Contract("knx:instanceTopology"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(instance.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(2);
		instance.add(address);
		objectBroker.addObj(address, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "universal_dimmer_n_527" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 3
		instance = new Obj();
		instance.setName("P-0341-0_DI-3");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "3"));
		instance.setIs(new Contract("knx:instanceTopology"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(instance.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(3);
		instance.add(address);
		objectBroker.addObj(address, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "switching_actuator_n_567_01_8_amp" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 4
		instance = new Obj();
		instance.setName("P-0944-0_DI-1");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "4"));
		instance.setIs(new Contract("knx:instanceTopology"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(instance.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(4);
		instance.add(address);
		objectBroker.addObj(address, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "temperature_sensor_n_258_02" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 5
		instance = new Obj();
		instance.setName("P-0341-0_DI-9");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "5"));
		instance.setIs(new Contract("knx:instanceTopology"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(instance.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(5);
		instance.add(address);
		objectBroker.addObj(address, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "push_button_2_fold_up_211_delta_studio_red_lens" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 6
		instance = new Obj();
		instance.setName("P-0341-0_DI-7");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "6"));
		instance.setIs(new Contract("knx:instanceTopology"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(instance.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(7);
		instance.add(address);
		objectBroker.addObj(address, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "knx_co2_humidity_and_temperature_sensor" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 7
		instance = new Obj();
		instance.setName("P-0341-0_DI-10");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "7"));
		instance.setIs(new Contract("knx:instanceTopology"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		address = new Int();
		address.setName("address");
		address.setHref(new Uri(instance.getHref().getPath() + "/" + "address"));
		address.setMin(0);
		address.set(8);
		instance.add(address);
		objectBroker.addObj(address, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "push_button_4_f_up_245_delta_profil_without_sym" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);
	}

	private void initBuilding(KNXConnector knxConnector, ObjectBroker objectBroker, Obj network)
	{
		// Building View
		Obj building = new Obj();
		building.setName("building");
		building.setDisplay("Gebï¿½ude-Sicht des Netzwerks");
		building.setHref(new Uri(network.getHref().getPath() + "/" + "views/building"));
		building.setIs(new Contract("knx:viewBuilding"));
		objectBroker.addObj(building, false);

		// Reference
		Ref buildingRef = new Ref();
		buildingRef.setName("building");
		buildingRef.setHref(new Uri("views/building"));
		buildingRef.setIs(new Contract("knx:viewBuilding"));
		network.add(buildingRef);
		objectBroker.addObj(buildingRef, false);

		// List of building parts
		List list = new List();
		list.setName("parts");
		list.setOf(new Contract("knx:part"));
		list.setHref(new Uri("parts"));
		building.add(list);
		objectBroker.addObj(list, false);

		// Part
		Obj part = new Obj();
		part.setName("P-01EE-0_BP-0");
		part.setDisplayName("Treitlstraï¿½e 1-3");
		part.setIs(new Contract("knx:part"));
		part.setHref(new Uri(list.getHref().getPath() + "/" + "treitlstrasse_1-3"));
		list.add(part);
		objectBroker.addObj(part, false);

		obix.Enum type = new obix.Enum();
		type.setName("type");
		type.setHref(new Uri(part.getHref().getPath() + "/" + "type"));
		type.setRange(new Uri("/enums/enumPart"));
		type.set("building");
		part.add(type);
		objectBroker.addObj(type, false);

		// List of building parts
		list = new List();
		list.setName("parts");
		list.setOf(new Contract("knx:part"));
		list.setHref(new Uri(part.getHref().getPath() + "/" + "parts"));
		part.add(list);
		objectBroker.addObj(list, false);

		// Part
		part = new Obj();
		part.setName("P-01EE-0_BP-1");
		part.setDisplayName("4. Stock");
		part.setIs(new Contract("knx:part"));
		part.setHref(new Uri(list.getHref().getPath() + "/" + "4_stock"));
		list.add(part);
		objectBroker.addObj(part, false);

		type = new obix.Enum();
		type.setName("type");
		type.setHref(new Uri(part.getHref().getPath() + "/" + "type"));
		type.setRange(new Uri("/enums/enumPart"));
		type.set("floor");
		part.add(type);
		objectBroker.addObj(type, false);

		// List of building parts
		list = new List();
		list.setName("parts");
		list.setOf(new Contract("knx:part"));
		list.setHref(new Uri(part.getHref().getPath() + "/" + "parts"));
		part.add(list);
		objectBroker.addObj(list, false);

		// Part
		part = new Obj();
		part.setName("P-01EE-0_BP-4");
		part.setDisplayName("A-Lab");
		part.setIs(new Contract("knx:part"));
		part.setHref(new Uri(list.getHref().getPath() + "/" + "a-lab"));
		list.add(part);
		objectBroker.addObj(part, false);

		type = new obix.Enum();
		type.setName("type");
		type.setHref(new Uri(part.getHref().getPath() + "/" + "type"));
		type.setRange(new Uri("/enums/enumPart"));
		type.set("room");
		part.add(type);
		objectBroker.addObj(type, false);

		// List of building parts
		list = new List();
		list.setName("parts");
		list.setOf(new Contract("knx:part"));
		list.setHref(new Uri(part.getHref().getPath() + "/" + "parts"));
		part.add(list);
		objectBroker.addObj(list, false);

		// Part
		part = new Obj();
		part.setName("P-01FF-0_BP-1");
		part.setDisplayName("Suitcase");
		part.setIs(new Contract("knx:part"));
		part.setHref(new Uri(list.getHref().getPath() + "/" + "suitcase"));
		list.add(part);
		objectBroker.addObj(part, false);

		type = new obix.Enum();
		type.setName("type");
		type.setHref(new Uri(part.getHref().getPath() + "/" + "type"));
		type.setRange(new Uri("/enums/enumPart"));
		type.set("distributedBoard");
		part.add(type);
		objectBroker.addObj(type, false);

		// List of instances
		list = new List();
		list.setName("instances");
		list.setOf(new Contract("knx:instanceBuilding"));
		list.setHref(new Uri(part.getHref().getPath() + "/" + "instances"));
		part.add(list);
		objectBroker.addObj(list, false);

		// 1
		Obj instance = new Obj();
		instance.setName("P-0341-0_DI-1");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "1"));
		instance.setIs(new Contract("knx:instanceBuilding"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		Ref reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "shutter_switch_n_522_02" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 2
		instance = new Obj();
		instance.setName("P-0341-0_DI-2");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "2"));
		instance.setIs(new Contract("knx:instanceBuilding"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "universal_dimmer_n_527" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 3
		instance = new Obj();
		instance.setName("P-0341-0_DI-3");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "3"));
		instance.setIs(new Contract("knx:instanceBuilding"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "switching_actuator_n_567_01_8_amp" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 4
		instance = new Obj();
		instance.setName("P-0944-0_DI-1");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "4"));
		instance.setIs(new Contract("knx:instanceBuilding"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "temperature_sensor_n_258_02" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 5
		instance = new Obj();
		instance.setName("P-0341-0_DI-9");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "5"));
		instance.setIs(new Contract("knx:instanceBuilding"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "push_button_2_fold_up_211_delta_studio_red_lens" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 6
		instance = new Obj();
		instance.setName("P-0341-0_DI-7");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "6"));
		instance.setIs(new Contract("knx:instanceBuilding"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "knx_co2_humidity_and_temperature_sensor" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 7
		instance = new Obj();
		instance.setName("P-0341-0_DI-10");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "7"));
		instance.setIs(new Contract("knx:instanceBuilding"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "push_button_4_f_up_245_delta_profil_without_sym" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);
	}

	private void initDomains(KNXConnector knxConnector, ObjectBroker objectBroker, Obj network)
	{
		// Building View
		Obj domains = new Obj();
		domains.setName("domains");
		domains.setDisplay("Domï¿½nen des Netzwerks");
		domains.setHref(new Uri(network.getHref().getPath() + "/" + "views/domains"));
		domains.setIs(new Contract("knx:viewDomains"));

		objectBroker.addObj(domains, false);

		// Reference
		Ref domainsRef = new Ref();
		domainsRef.setName("domains");
		domainsRef.setHref(new Uri("views/domains"));
		domainsRef.setIs(new Contract("knx:viewDomains"));

		network.add(domainsRef);
		objectBroker.addObj(domainsRef, false);

		// List of entities
		List list = new List();
		list.setName("domains");
		list.setOf(new Contract("knx:domain"));
		list.setHref(new Uri("domains"));
		domains.add(list);
		objectBroker.addObj(list, false);

		// Domain general
		Obj general = new Obj();
		general.setName("D-0");
		general.setDisplayName("General");
		general.setIs(new Contract("knx:domain"));
		general.setHref(new Uri(list.getHref().getPath() + "/" + "general"));
		list.add(general);
		objectBroker.addObj(general, false);

		obix.Enum type = new obix.Enum();
		type.setName("type");
		type.setHref(new Uri(general.getHref().getPath() + "/" + "type"));
		type.setRange(new Uri("/enums/enumDomain"));
		type.set("undef");
		general.add(type);
		objectBroker.addObj(type, false);

		// List of instances
		list = new List();
		list.setName("instances");
		list.setOf(new Contract("knx:instanceDomain"));
		list.setHref(new Uri(general.getHref().getPath() + "/" + "instances"));
		general.add(list);
		objectBroker.addObj(list, false);

		// Instances

		// 1
		Obj instance = new Obj();
		instance.setName("P-0341-0_DI-1");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "1"));
		instance.setIs(new Contract("knx:referenceDomain"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		Ref reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "shutter_switch_n_522_02" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 2
		instance = new Obj();
		instance.setName("P-0341-0_DI-2");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "2"));
		instance.setIs(new Contract("knx:referenceDomain"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "universal_dimmer_n_527" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 3
		instance = new Obj();
		instance.setName("P-0341-0_DI-3");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "3"));
		instance.setIs(new Contract("knx:referenceDomain"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "switching_actuator_n_567_01_8_amp" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 4
		instance = new Obj();
		instance.setName("P-0944-0_DI-1");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "4"));
		instance.setIs(new Contract("knx:referenceDomain"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "temperature_sensor_n_258_02" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 5
		instance = new Obj();
		instance.setName("P-0341-0_DI-9");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "5"));
		instance.setIs(new Contract("knx:referenceDomain"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "push_button_2_fold_up_211_delta_studio_red_lens" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 6
		instance = new Obj();
		instance.setName("P-0341-0_DI-7");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "6"));
		instance.setIs(new Contract("knx:referenceDomain"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "knx_co2_humidity_and_temperature_sensor" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);

		// 7
		instance = new Obj();
		instance.setName("P-0341-0_DI-10");
		instance.setHref(new Uri(list.getHref().getPath() + "/" + "7"));
		instance.setIs(new Contract("knx:referenceDomain"));
		list.add(instance);
		objectBroker.addObj(instance, false);

		reference = new Ref();
		reference.setName("reference");
		reference.setHref(new Uri(network.getHref().getPath() + "/entities/" + "push_button_4_f_up_245_delta_profil_without_sym" + "/" + "1"));
		instance.add(reference);
		objectBroker.addObj(reference, false);
	}

	private void initEntities(KNXConnector knxConnector, ObjectBroker objectBroker, Obj network)
	{
		// Entities
		Obj entities = new Obj();
		entities.setName("entities");
		entities.setHref(new Uri(network.getHref().getPath() + "/" + "entities"));
		entities.setIs(new Contract("knx:entities"));
		objectBroker.addObj(entities, false);

		// Reference
		Ref entitiesRef = new Ref();
		entitiesRef.setName("entities");
		entitiesRef.setHref(new Uri("entities"));
		entitiesRef.setIs(new Contract("knx:entities"));
		network.add(entitiesRef);
		objectBroker.addObj(entitiesRef, false);

		// List of entities
		List list = new List();
		list.setName("entities");
		list.setOf(new Contract("obix:ref"));
		list.setHref(new Uri("entities"));
		entities.add(list);
		objectBroker.addObj(list, false);

		// Datapoints and Entities
		List datapoints;

		// 1.
		datapoints = this.initEntity(knxConnector, objectBroker, entities, list, "P-0341-0_DI-1", "Shutter switch N 522/02", "shutter_switch_n_522_02" + "/" + "1", "Siemens", "5WG1 522-1AB02");

		// 2.
		datapoints = this.initEntity(knxConnector, objectBroker, entities, list, "P-0341-0_DI-2", "Universal dimmer N 527", "universal_dimmer_n_527" + "/" + "1", "Siemens", "5WG1 527-1AB01");

		try
		{
			DPST_3_7_ImplKnx dpst = new DPST_3_7_ImplKnx(knxConnector, new GroupAddress("1/0/1"));
			dpst.setName("M-0001_A-6102-01-A218_O-1_R-1");
			dpst.setDisplay("...");
			dpst.setHref(new Uri(datapoints.getHref().getPath() + "/" + "dimming"));
			dpst.setDisplayName("Dimming");

			dpst.function().setHref(new Uri(dpst.getHref().getPath() + "/" + DPST_3_7.FUNCTION_HREF));
			dpst.unit().setHref(new Uri(dpst.getHref().getPath() + "/" + DPST_3_7.UNIT_HREF));
			dpst.value().setHref(new Uri(dpst.getHref().getPath() + "/" + DPST_3_7.VALUE_HREF));

			datapoints.add(dpst);
			objectBroker.addObj(dpst, false);
		}
		catch (KNXFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 3.
		datapoints = this.initEntity(knxConnector, objectBroker, entities, list, "P-0341-0_DI-3", "Switching actuator N 567/01, (8 Amp)", "switching_actuator_n_567_01_8_amp" + "/" + "1", "Siemens", "5WG1 567-1AB01");

		try
		{
			DPST_1_1_ImplKnx dpst = new DPST_1_1_ImplKnx(knxConnector, new GroupAddress("1/0/0"));
			dpst.setName("M-0001_A-9803-03-3F77_O-3_R-4");
			dpst.setDisplay("...");
			dpst.setHref(new Uri(datapoints.getHref().getPath() + "/" + "switch_channel_a"));
			dpst.setDisplayName("Switch, Channel A");

			dpst.function().setHref(new Uri(dpst.getHref().getPath() + "/" + DPST_1_1.FUNCTION_HREF));
			dpst.unit().setHref(new Uri(dpst.getHref().getPath() + "/" + DPST_1_1.UNIT_HREF));
			dpst.value().setHref(new Uri(dpst.getHref().getPath() + "/" + DPST_1_1.VALUE_HREF));

			datapoints.add(dpst);
			objectBroker.addObj(dpst, false);
		}
		catch (KNXFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 4. Temperature sensor
		datapoints = this.initEntity(knxConnector, objectBroker, entities, list, "P-0944-0_DI-1", "Temperature Sensor N 258/02", "temperature_sensor_n_258_02" + "/" + "1", "Siemens", "5WG1 258-1AB02");

		try
		{
			DPST_9_1_ImplKnx tempKanalA = new DPST_9_1_ImplKnx(knxConnector, new GroupAddress("1/1/0"));
			tempKanalA.setName("M-0001_A-9814-01-5F7E_O-0_R-2");
			tempKanalA.setDisplay("...");
			tempKanalA.setHref(new Uri(datapoints.getHref().getPath() + "/" + "temperatur_kanal_a"));
			tempKanalA.setDisplayName("Temperatur, Kanal A");
			tempKanalA.function().setHref(new Uri(tempKanalA.getHref().getPath() + "/" + DPST_9_1.FUNCTION_HREF));
			tempKanalA.unit().setHref(new Uri(tempKanalA.getHref().getPath() + "/" + DPST_9_1.UNIT_HREF));
			tempKanalA.value().setHref(new Uri(tempKanalA.getHref().getPath() + "/" + DPST_9_1.VALUE_HREF));
			datapoints.add(tempKanalA);
			objectBroker.addObj(tempKanalA, false);

			DPST_9_1_ImplKnx tempKanalB = new DPST_9_1_ImplKnx(knxConnector, new GroupAddress("1/1/1"));
			tempKanalB.setName("M-0001_A-9814-01-5F7E_O-1_R-3");
			tempKanalB.setDisplay("...");
			tempKanalB.setHref(new Uri(datapoints.getHref().getPath() + "/" + "temperatur_kanal_b"));
			tempKanalB.setDisplayName("Temperatur, Kanal B");
			tempKanalB.function().setHref(new Uri(tempKanalB.getHref().getPath() + "/" + DPST_9_1.FUNCTION_HREF));
			tempKanalB.unit().setHref(new Uri(tempKanalB.getHref().getPath() + "/" + DPST_9_1.UNIT_HREF));
			tempKanalB.value().setHref(new Uri(tempKanalB.getHref().getPath() + "/" + DPST_9_1.VALUE_HREF));
			datapoints.add(tempKanalB);
			objectBroker.addObj(tempKanalB, false);

			DPST_9_1_ImplKnx tempKanalC = new DPST_9_1_ImplKnx(knxConnector, new GroupAddress("1/1/2"));
			tempKanalC.setName("M-0001_A-9814-01-5F7E_O-2_R-1");
			tempKanalC.setDisplay("...");
			tempKanalC.setHref(new Uri(datapoints.getHref().getPath() + "/" + "temperatur_kanal_c"));
			tempKanalC.setDisplayName("Temperatur, Kanal C");
			tempKanalC.function().setHref(new Uri(tempKanalC.getHref().getPath() + "/" + DPST_9_1.FUNCTION_HREF));
			tempKanalC.unit().setHref(new Uri(tempKanalC.getHref().getPath() + "/" + DPST_9_1.UNIT_HREF));
			tempKanalC.value().setHref(new Uri(tempKanalC.getHref().getPath() + "/" + DPST_9_1.VALUE_HREF));
			datapoints.add(tempKanalC);
			objectBroker.addObj(tempKanalC, false);

			DPST_9_1_ImplKnx tempKanalD = new DPST_9_1_ImplKnx(knxConnector, new GroupAddress("1/1/3"));
			tempKanalD.setName("M-0001_A-9814-01-5F7E_O-3_R-4");
			tempKanalD.setDisplay("...");
			tempKanalD.setHref(new Uri(datapoints.getHref().getPath() + "/" + "temperatur_kanal_d"));
			tempKanalD.setDisplayName("Temperatur, Kanal D");
			tempKanalD.function().setHref(new Uri(tempKanalD.getHref().getPath() + "/" + DPST_9_1.FUNCTION_HREF));
			tempKanalD.unit().setHref(new Uri(tempKanalD.getHref().getPath() + "/" + DPST_9_1.UNIT_HREF));
			tempKanalD.value().setHref(new Uri(tempKanalD.getHref().getPath() + "/" + DPST_9_1.VALUE_HREF));
			datapoints.add(tempKanalD);
			objectBroker.addObj(tempKanalD, false);
		}
		catch (KNXFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 5.
		datapoints = this.initEntity(knxConnector, objectBroker, entities, list, "P-0341-0_DI-9", "Push button 2-fold UP 211 DELTA studio (red lens)", "push_button_2_fold_up_211_delta_studio_red_lens" + "/" + "1", "Siemens", "5WG1 211-2AB_1");

		// no datapoints

		// 6.
		datapoints = this.initEntity(knxConnector, objectBroker, entities, list, "P-0341-0_DI-7", "KNX COï¿½, Humidity and Temperature Sensor", "knx_co2_humidity_and_temperature_sensor" + "/" + "1", "Schneider Electric Industries SAS", "MTN6005-0001");

		// datapoints omitted

		// 7.
		datapoints = this.initEntity(knxConnector, objectBroker, entities, list, "P-0341-0_DI-10", "Push button 4-f UP 245 DELTA profil (without sym)", "push_button_4_f_up_245_delta_profil_without_sym" + "/" + "1", "Siemens", "5WG1 245-2AB_1");

		// datapoints omitted

	}

	private List initEntity(KNXConnector knxConnector, ObjectBroker objectBroker, Obj entities, List list, String name, String displayName, String href, String manufact, String orderNo)
	{
		// Add entities
		Obj entity;
		Ref entityRef;
		Str manufacturer;
		Str orderNumber;
		List datapoints;

		// P-0944-0_DI-1
		entity = new Obj();
		entity.setName(name);
		entity.setDisplayName(displayName);
		entity.setDisplay("...");
		entity.setHref(new Uri(entities.getHref().getPath() + "/" + href));
		objectBroker.addObj(entity, false);

		entityRef = new Ref();
		entityRef.setName(name);
		entityRef.setDisplayName(displayName);
		entityRef.setHref(new Uri(entity.getHref().getPath()));
		entityRef.setIs(new Contract("knx:entity"));
		list.add(entityRef);
		objectBroker.addObj(entityRef, false);

		manufacturer = new Str("manufacturer", manufact);
		manufacturer.setHref(new Uri("manufacturer"));
		entity.add(manufacturer);
		objectBroker.addObj(manufacturer, false);

		orderNumber = new Str("orderNumber", orderNo);
		orderNumber.setHref(new Uri("orderNumber"));
		entity.add(orderNumber);
		objectBroker.addObj(orderNumber, false);

		datapoints = new List();
		datapoints.setName("datapoints");
		datapoints.setHref(new Uri("datapoints"));
		datapoints.setOf(new Contract("knx:datapoint"));
		entity.add(datapoints);
		objectBroker.addObj(datapoints, false);

		return datapoints;
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

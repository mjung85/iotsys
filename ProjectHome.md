# Intro #
IoTSyS is an integration middleware for the Internet of Things. It provides a communication stack for embedded devices based on IPv6, Web services and [oBIX](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=obix) to provide interoperable interfaces for smart objects. Using [6LoWPAN](http://tools.ietf.org/html/rfc4919) for constrained wireless networks and the [Constrained Application Protocol](http://datatracker.ietf.org/doc/draft-ietf-core-coap/) together with [Efficient XML Interchange](http://www.w3.org/XML/EXI/) an efficient stack is provided allowing using interoperable Web technologies in the field of sensor and actuator networks and systems while remaining nearly as efficient regarding transmission message sizes as existing automation systems. The IoTSyS middleware aims providing a gateway concept for existing sensor and actuator systems found in nowadays home and building automation systems, a stack which can be deployed directly on embedded 6LoWPAN devices and further addresses security, discovery and scalability issues.

IoTSyS was started within the frame of the FP7 [IoT6](http://www.iot6.eu/) European research project and is hosted and further maintained by the [Automation Systems Group](http://www.auto.tuwien.ac.at) at the [Vienna University of Technology](http://www.tuwien.ac.at). It is also supported through the Internet Foundation Austria within the [NetIdee](https://www.netidee.at/) open source grants and further developed within the project Secure and Semantic Web of Automation (SeWoA, FFG 840206).

# Demo - Videos #

## Intro ##
General project overview.

<a href='http://www.youtube.com/watch?feature=player_embedded&v=HcDewOwTzgM' target='_blank'><img src='http://img.youtube.com/vi/HcDewOwTzgM/0.jpg' width='425' height=344 /></a>

## Demo 1 ##
Heterogeneous device interaction between EnOcean, KNX, BACnet components and a RFID tag reader.

<a href='http://www.youtube.com/watch?feature=player_embedded&v=HXSmHkvhE8E' target='_blank'><img src='http://img.youtube.com/vi/HXSmHkvhE8E/0.jpg' width='425' height=344 /></a>

## Demo 2 ##
Combining 6LoWPAN based sensor (push button) with KNX light switch and sun blind actuator.
<a href='http://www.youtube.com/watch?feature=player_embedded&v=qKoOR0MrqKM' target='_blank'><img src='http://img.youtube.com/vi/qKoOR0MrqKM/0.jpg' width='425' height=344 /></a>

## Demo 3 ##
Complex HVAC process. The IoTSyS gateway uses logic blocks to realize a HVAC process that combines BACnet controlled devices (chiller, boiler, fans, valves) with an EnOcean window contact sensor a KNX room control unit and W-MBus smart meter.

<a href='http://www.youtube.com/watch?feature=player_embedded&v=jlPfXNv_pgo' target='_blank'><img src='http://img.youtube.com/vi/jlPfXNv_pgo/0.jpg' width='425' height=344 /></a>

## Demo 4 ##
Alarming scenarios - a 6LoWPAN sensor is used to detect a freefall and to raise an alarm on a KNX text display with accustic signal. Furthermore, a weather data connector is combined with an EnOcean window contact sensor to raise an alarm if a storm is approaching and a open window is detected.

<a href='http://www.youtube.com/watch?feature=player_embedded&v=1hCgPanTSNA' target='_blank'><img src='http://img.youtube.com/vi/1hCgPanTSNA/0.jpg' width='425' height=344 /></a>

## Demo 5 ##
Demonstration of 6LoWPAN peer to peer interaction based on CoAP/oBIX with IPv6 multicasting. 6LoWPAN LEDs are switched through a 6LoWPAN push button without the need of the gateway for process data exchange.

<a href='http://www.youtube.com/watch?feature=player_embedded&v=LpopaUDxALQ' target='_blank'><img src='http://img.youtube.com/vi/LpopaUDxALQ/0.jpg' width='425' height=344 /></a>

# Project overview and open source license #

IoTSyS consists of multiple projects that are currently evolving and act as proof of concept prototypes within several research projects of the [Automation Systems Group](http://www.auto.tuwien.ac.at) at the [Vienna University of Technology](http://www.tuwien.ac.at).

IoTSyS consists of multiple projects that are partly based on existing open source libraries with different licenses.

The oBIX gateway including the OSGI oBIX server, the protocol bindings (HTTP, CoAP, SOAP) and common oBIX objects are made open source based on the liberal [new BSD license](http://opensource.org/licenses/BSD-3-Clause). The gateway by itself is completely independet from technology specific connectors and runs as OSGI application within an OSGI container.

The connectors to home and building automation technologies are based on existing open source libraries and are wrapped in protocol bundles using OSGI. These bundles remain the open source license of the library, but can be deployed within an OSGI environment.

The gateway and the protocol bundle projects are separate applications. However, within an OSGI environment they can work together.

**Projects**:
  * **IoTSyS-Gateway**: OSGI based oBIX server, HTTP Server, CoAP Server, RESTful Web service endpoint, SOAP Web service endpoint
  * **IoTSyS-oBIX**: Extendend and modified oBIX toolkit
  * **IoTSyS-Common**: Interfaces and API to interact with the oBIX server, common oBIX object types
  * **IoTSyS-WMBus**: connector for Wireless M-Bus and technology specific oBIX object implementation, provided as OSGI bundle
  * **IoTSyS-Calimero**: OSGI library wrapper of Calimero
  * **IoTSyS-KNX**: OSGI bundle wrapper for Calimero, including IoTSyS specific bundle activator and technology specific oBIX object implementation
  * **IoTSyS-BACnet4J**: OSGI library wrapper of BACnet4J
  * **IoTSyS-BACnet**: OSGI bundle wrapper for BACnet4J, including IoTSyS specific bundle activator and technology specific oBIX object implementation
  * **IoTSyS-Encoding-JSON**: OSGI library wrapper


<img src='https://iotsys.googlecode.com/hg/misc/img/overview.png' />

## User and developer guide ##
Check the user [user and developer guide](userguide.md) to find out:
  * [How to setup the project environment](gettingstarted.md)?
  * [How to run the gateway](gettingstarted.md)?
  * How to access the gateway using different oBIX bindings ([HTTP](HTTPinteraction.md), [CoAP](COAPinteraction.md), [SOAP](SOAPinteraction.md))
  * [How to add your own technology adapter](connectorhowto.md)?


## IoTSyS Lead ##
  * [Markus Jung](https://www.auto.tuwien.ac.at/people/view/Markus_Jung/)


## IoTSyS Contributors ##
  * [Juergen Weidinger](https://www.auto.tuwien.ac.at/people/view/Juergen_Weidinger/): KNX protocol bundle, IoTSyS Gateway
  * Ralph Hoch: Wireless M-Bus protocol bundle
  * Isolde Carsasco de Cantú: Logo
  * Esad Hajdarevic: Obelix UI
  * Robert Horvath: IoTSyS Gateway, oBIX 1.1 implementation (GSoC 2013 student)
  * Thomas Hofer: XACML Module
  * Stefan Suzcsich: Weather Forecast module
  * Nam Giang: DNS, mDNS, DNS-SD Module (GSoC 2013 student)
  * Clemens Pühringer: Appscale CoAP/OBIX API
  * Jürgen Schober: Wired MBus connector

Parts of the project are used as proof of concept implementation within the PhD thesis [An integration middleware for the Internet of Things](https://www.dropbox.com/s/w2ayr0bv2diss1b/diss_cc_attribution_nc.pdf?dl=0).
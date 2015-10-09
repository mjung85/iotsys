


## Introduction ##
This page provides a user guide how to use the IoTSyS middleware (project, stack).

If you are looking to extend and modify the gateway please refer to the [developer guide](devguide.md).

## Project overview ##
The gateway provides an IPv6 based Web service interface based on oBIX for existing home and building automation technologies providing:
  * OSGI based oBIX server (partial implementation of oBIX 1.1 WD 06)
  * common set of oBIX contracts to represent standard device types
  * various new protocol bindings for oBIX using CoAP, SOAP or HTTP for message transport and offering XML, EXI, schema informed EXI, JSON and oBIX binary encodings.
  * an IPv6 address for each device
  * protocol bundles for KNX, BACnet, Wireless M-Bus

The gateway by itself is completely independent from technology specific connectors and runs as OSGI application within an OSGI container.

The core projects are:
  * iotsys-obix: Extended and modified oBIX toolkit
  * [iotsys-commons](iotsyscommons.md): Interfaces and API to interact with the oBIX server, common oBIX object types
  * iotsys-gateway: OSGI based oBIX server, HTTP Server, CoAP Server, RESTful Web service endpoint, SOAP Web service endpoint

IoTSyS also provides connectors to various technologies, which are continiously extended.
These connectors are available as additional OSGI Bundles:
  * [iotsys-wmbus](WMBusConnector.md): Connector for Wireless M-Bus and technology specific oBIX object implementation
  * [iotsys-knx](KNXConnector.md): OSGI bundle wrapper for Calimero, including IoTSyS specific bundle activator and technology specific oBIX object implementation
  * [iotsys-bacnet](BACnetConnector.md): OSGI bundle wrapper for BACnet4J, including IoTSyS specific bundle activator and technology specific oBIX object implementation
  * [iotsys-weather-forecast](WeatherForecastConnector.md): A simple connector for weather services that are based on web services, including weather data specific oBIX object implementation

If you want to create your custom connector, refer to the [How to create a connector guide](connectorhowto.md).

## Getting started ##
Please follow the getting started guide for using the gateway: [getting started](gettingstarted.md)

## IoTSyS Commons ##
IoTSyS uses a set of generic object representation for different device types, which are based on oBIX and used to provide a common view on devices of different technologies.

A set of primitive device types is provided within the IoTSyS commons project. New device types can be defined there.

Please refer to the [IoTSyS commons documentation](iotsyscommons.md).

## oBIX services ##
oBIX provides services such as Histories, Watches and Alarming.

You can learn how they are used in the [HTTP interaction guide](HTTPinteraction#Using_oBIX_services.md).

If you want to enable these services for your own devices, please refer to the [oBIX Services guide](obixServices.md).
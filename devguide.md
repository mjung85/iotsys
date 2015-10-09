


## Introduction ##
This page provides a developer guide on how the internal components of the IoTSyS middleware work together and how to extend and modify it.

Be sure to read the [user guide](userguide.md) first to learn how the gateway is used.


## Initialization of the Gateway ##
The class `IoTSySGateway` in the iotsys-gateway project is the main class of the gateway.
It initializes the major components discussed below.

After initializing the object broker, the main device loader is called.
It reads the devices config file (`iotsys-gateway/config/devices.xml`) and calls the configured device loaders.
Additional device loaders are made available by [technology connectors](devguide#Technology_Connectors.md).
After the devices are loaded, the HTTP & CoAP servers are started.


## oBIX object model ##
oBIX features a simple object model, based on a small, fixed set of object types.
The base object type is simply called "Obj".
Among the value object types are bool, int, real, str, enum, uri, abstime, reltime, date and time.
More specialized object types defined in oBIX are list, op (operation), feed, ref (reference) and err (error).
These object types are implemented in the oBIX Toolkit (in the iotsys-obix project).

The objects can be observed for changes.
This mechanism is used for the implementation of oBIX services (histories, watches, alarms) as well as for CoAP observing.

## Object Broker ##
The object broker is the central point where all objects are managed.
It maintains the root object, to which other objects can be added in order to become reachable through the server.
Objects can be retrieved again by their full URI.
They can be read, written to, and invoked.

The gateway allows mapping IPv6 addresses to objects.
These IPv6 mappings are also stored in the object broker.

Upon initialization of the broker some default objects are created, such as the oBIX Lobby, the about-object, the watch service object and a alarm subject object.


## Technology Connectors ##
Technology Connectors are used to connect devices from a variety of building automation systems to the gateway. Furthermore, data from any web services, e.g., weather forecasts, can be incorporated and provided by the gateway.
Custom device loaders map devices from these systems to oBIX objects and makes them available to the object broker.

The created objects implement reading and writing with the actual device when the oBIX object is being read or written to.
For more information on how to create your own connectors, please refer to [How to create a technology connector](connectorhowto.md).

## oBIX Server ##
The oBIX Server is used to read, write and invoke objects managed by the object broker.
It sits as a layer between the object broker and the front-end HTTP & CoAP servers.

The server takes the URI of the object to be read, written or invoked, and the payload data of the request.
The response of the server always is an oBIX object.

## oBIX Decoder & Encoder ##
Write and Invoke requests in oBIX take oBIX objects as input. These objects are transmitted as XML string.
The `ObixDecoder` deserializes XML into Java oBIX objects.

To map used contracts to their corresponding Java Interfaces, the `ContractRegistry` is used.
If you create a new contract, be sure to register it with the `ContractRegistry`.
The interface registration is currently done within the iotsys-gateway project in the `at.ac.tuwien.auto.iotsys.gateway.obix.object.ContractInit` class.

The `ObixEncoder` serializes Java oBIX objects to XML.
The HTTP and CoAP servers use a subclass of the `ObixEncoder` to relativize the hrefs in the XML output based on the request.

## Front-end ##
On the front-end, there are two ways to interact with the gateway, the HTTP and CoAP servers.
The server classes are located in the iotsys-gateway project in the `at.ac.tuwien.auto.iotsys.gateway.obix.server` package.

The servers listen on the ports set in the `iotsys.properties` file.

Both servers have the same tasks, only adjusted for their respective protocol:
  * Any payload data is decoded based on the Content-Type of the request.
  * The URI of the requested oBIX resource has to be determined. If the request was received on an IPv6-Interface, the oBIX-Server is asked for the object mapped to that address as a base to resolve the path, otherwise the request path can be used directly.
  * The read/write/invoke request is forwarded to the oBIX server
  * The response object is encoded based on the Accept-Header of the request. Supported encodings are XML, EXI, JSON and oBIX-Binary.

The HTTP server also offers an HTML5 interface.
The HTML, JavaScript and CSS files are statically served by `NanoHTTPD`.

CoAP provides a mechanism to observe objects.
Observing relationships are managed in the `ObixObservingManager` class.
Furthermore, requests to `/.well-known/core` are answered by listing all available resources in the [CoRE link format](http://tools.ietf.org/html/rfc6690).

## Request example ##
Here is an example of a typical HTTP PUT request to write a new value to an object.
The image shows the components involved in processing the request.

```
HTTP PUT http://localhost:8080/buildingA/roomA/fanspeedSetpoint

Payload:
<int val="1200"/>
```

<img src='http://cl.ly/image/0N0d470o3135/request_flow.png' />

The request first reaches the front-end of the server.
The HTTP server (`NanoHTTPD`) receives the request and after decoding the payload and determining the requested resource path, the request is passed to the oBIX server.
There the string of XML data is converted to an oBIX object by the `ObixDecoder`.
The object broker finds the object by its path and writes to it using the payload object.

The object transforms this request into a write request to the device it represents.
It updates itself with the new data, which notifies its observers (eg. watches or observers for alarm conditions).
The object and its new state are then returned.
This object is passed back up along the chain to the front-end, where it has to be encoded for transport over HTTP.
The `ObixEncoder` serializes the object to XML.
Based on the Accept-Header of the request it may be further encoded.


## oBIX services ##
Details on the implementation of oBIX services, such as the History, Watch and Alarm service, can be found in [the oBIX services developer guide](obixServicesDev.md).
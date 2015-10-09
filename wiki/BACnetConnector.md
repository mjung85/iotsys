# Introduction #

BACnet is a mature standard in the domain of building automation. BACnet uses a quite generic information model and BACnet controllers are interfaced using the BACnet/IP connectivity.

# Connection configuration #
One or many BACnet connections can be configured in the `devices.xml` configuration file.

A connector configuration consists of a connection configuration section and zero or more configured devices. For the configuration the broadcast IP address and the local UDP port and a local BACnet device identifier need need to be configured.

```
<name>BACnet E-Lab</name>
<enabled>false</enabled>
<localDeviceID>23345</localDeviceID>
<broadcastAddress>192.168.161.255</broadcastAddress>
<localPort>47808</localPort>
```

# Device configuration #

For the initialization of a device, one has to specify the type of the class implementing the according oBIX contract for BACnet. A BACnet device is initialized with one or multiple BACnet data point address information consisting of the device identifier, the object type and the property identifier used for the communication. The `href` field specifies the relative path that is used to uniquly identify the oBIX object representing the BACnet device. `historyEnabled` allows to enable or disable the oBIX history for all basic data points of an oBIX object. `historyCount` limits the number of history values that are collected for each data point.


```
<device>
 <type>at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.bacnet.FanSpeedActuatorImplBacnet
  </type>
  <address>2098177, 1, 4, 85, 2098177, 4, 4, 85</address>
  <href>fanAirOut</href>
  <historyEnabled>false</historyEnabled>
</device>
```

See the `devices.xml` for more examples.
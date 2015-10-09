# Introduction #

The KNX connector is based on the [Calimero Java Library](http://sourceforge.net/p/calimero/wiki/Home/). It can be used together with KNXnet/IP tunnel or router connection.

KNX is integrated using two bundles, one is a basic OSGI wrapper for

# Connection configuration #
One or many KNX connections can be configured in the `devices.xml` configuration file. A connector configuration consists of a connection configuration section and zero or more configured devices. For the configuration the IP address and the UDP port of the KNX IP router need to be configured. The local IP can be autodetected. It searches for a local interface on the same IPv4 network.

**Connector Configuration:**
```
<name>KNX E-Lab</name>
<enabled>false</enabled>
<router>
  <ip>192.168.161.53</ip>
  <port>3671</port>
</router>
<localIP>auto</localIP>
```

# Device configuration #

For the initialization of a device, one has to specify the type of the class implementing the according oBIX contract for KNX. A KNX device is initialized with one or multiple KNX group addresses used for the communication. The `href` field specifies the relative path that is used to uniquly identify the oBIX object representing the KNX device. `historyEnabled` allows to enable or disable the oBIX history for all basic data points of an oBIX object. `historyCount` limits the number of history values that are collected for each data point.

**Device configuration examples:**
```
<device>
 <type>at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.knx.LightSwitchActuatorImplKnx</type>
  <address>null, 2/0/0</address>
  <href>light1</href>
  <ipv6>2001:629:2500:570::102</ipv6>
  <historyEnabled>false</historyEnabled>
  <historyCount>0</historyCount>
</device>
```

```
<device>
  <type>at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.knx.TemperatureSensorImplKnx</type>
  <address>0/0/4</address>
  <href>temperature1</href>
  <ipv6>2001:629:2500:570::108</ipv6>
  <historyEnabled>true</historyEnabled>
  <historyCount>200</historyCount>
</device>
```

See the `devices.xml` for more examples.
# Introduction #

Wireless Metering Bus allows to read out your smart meter on your real time energy consumption and current power load.

# Hardware #
The W-MBus connector uses the [Amber AMB8465 USB adapter](http://amber-wireless.de/406-0-AMB8465-M.html), which is interfaced through a serial interface on the gateway. The connector is dedicated to the Amber API to interact with the USB dongle, but it should be possible to easily adjust the connector to any other W-MBus transceiver.

# Connection and device configuration #
For the connection you need to specify the serial port, to which the USB communication module is connected. Further, the serial number of the meter and the AES key need to be specified.

```
<connector>
  <name>W-MBus Smart Meter Linux</name>
  <enabled>false</enabled>
  <serialPort>/dev/ttyUSB0</serialPort>
  <device>
     <type>at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.wmbus.SmartMeterImplWMBus</type>
    <address>15004474, 66 77 66 77 66 77 66 77 66 77 66 77 66 77 66 77   </address>
    <href>smartmeter</href>
    <historyEnabled>true</historyEnabled>
    <historyCount>1000</historyCount>
  </device>
</connector>

<connector>
  <name>W-MBus Smart Meter Windows</name>
  <enabled>false</enabled>
  <serialPort>COM8</serialPort>
  <device>
        <type>at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.wmbus.SmartMeterImplWMBus
        </type>
      <address>15004474, 66 77 66 77 66 77 66 77 66 77 66 77 66 77 66 77</address>
      <href>smartmeter</href>
      <historyEnabled>true</historyEnabled>
      <historyCount>1000</historyCount>
    </device>
  </connector>
</wmbus>
```
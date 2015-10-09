# Introduction #

IoTSyS is an integration middleware for the Internet of Things. It provides a communication stack for embedded devices based on IPv6, Web services and oBIX to provide interoperable interfaces for smart objects. The IoTSyS middleware aims providing a gateway concept for existing sensor and actuator systems found in nowadays home and building automation systems (KNX, BACnet, ZigBee, M-Bus, Wireless M-Bus), a stack which can be deployed directly on embedded 6LoWPAN devices and further addresses security, discovery and scalability issues.

This connector extends IoTSyS by the Enocean standard. Therefore, the API [java-enocean-library](https://code.google.com/p/java-enocean-library/) was used to make the communication with Enocean devices as easy as possible. For developing the connector the USB gateway [USB 300](http://www.enocean.com/en/enocean_modules/usb-300-oem/) with the TCM 310 transceiver gateway module is used and for running on the Raspberry Pi the Enocean Pi piggy back board is used(also with the TCM 310 transceiver gateway module).

# Enocean Protocol Overview #

The Enocean radio protocol (ERP) is optimized to transmit information with the utmost reliability using extremely little power while ensuring that the products of costumers applying Enocean technology are compatible with each other. Only the very shortest transmission period (<1ms) for an Enocean telegram allows the design of, for example, a battery-free radio switch, which can produce a full radio command with just approximately 50 uWs of energy. At the same time, the reliability of the system increases, as the possibility of data collision is strongly reduced. Every data bit in the radio telegram is essential. For each '0' and '1' state, content descriptions are defined, which must be  followed by the sender and the receiver likewise. Depending on the telegram and the function of the device the user data (payload) is defined in Enocean Equipment Profiles (EEP). {1}

## Enocean Serial Protocol ##

The Enocean Serial Protocol 3.0 (ESP3) {2} defines the serial communication between a host and EnOcean modules. Hosts are external microcontrollers, e.g. the TCM 310, or PC’s incl. software tools.

The physical interface between a host and a EnOcean RF module (UART) is a 3-wire connection (Rx, Tx, GND / software handshake / full-duplex), modelled on RS-232 serial interface.

### Packet Description ###
A ESP3 packet contains the following information:

<img src='https://jschober88-enocean.googlecode.com/hg/iotsys-enocean/doc/EnOcean_PacketDescription.png' />

### Packet Types ###
Depending on the field _Packet Type_ a different kind of packet is transmitted as shown in the following table {2}:
| **Type No.**  | **Value hex**| **Name**  | **Description** |
|:--------------|:|:----------|:----------------|
| 0  | 0x00  | ---  | Reserved |
| 1  | 0x01  | RADIO  | Radio telegram |
| 2  | 0x02  | RESPONSE  | Response to any packet |
| 3  | 0x03  | RADIO\_SUB\_TEL  | Radio subtelegram |
| 4  | 0x04  | EVENT  Event|  message |
| 5  | 0x05  | COMMON\_COMMAND  | Common command |
| 6  | 0x06  | SMART\_ACK\_COMMAND  | Smart Ack command |
| 7  | 0x07  | REMOTE\_MAN\_COMMAND | Remote management command |
| 8  | 0x08  | ---  | Reserved for Enocean |
| 9  | 0x09  | RADIO\_MESSAGE  | Radio message |
| 10  | 0x0A  | RADIO\_ADVANCED  | Advanced protocol radio telegram |
| 11 ... 127 | 0x08 ... 7F | ---  | Reserved for Enocean |
| 128...255 | 0x80 ... FF | available | Manufacturer specific |

### Packet Type 1: Radio ###
The radio telegram (raw data) is embedded into the ESP3 packet and it is the most important one, because it is used to communicate with Enocean devices. Therefore, it is the only packet type, which is described here. The other types can be found in the ESP3 documentation {2}.
The actual user data (variable length) is a subset of the radio telegram.

The following structure is applicable to all types of radio telegrams:
<img src='https://jschober88-enocean.googlecode.com/hg/iotsys-enocean/doc/EnOcean_RadioPacket.png' />

When receiving a telegram, no RESPONSE has to be sent. When sending a telegram, a RESPOND has to be expected. In this case, the following RESPONSE message gives the return codes:
| 00 | RET\_OK |
|:---|:--------|
| 02 | RET\_NOT\_SUPPORTED |
|03 | RET\_WRONG\_PARAM |

There is no additional data included in the standard RESPONSE structure.

**Radio variants (examples)**

Out of the numerous variants of the RADIO packet, described in the Enocean standard, only a few examples are described here. These examples describe the structure of DATA on the ESP3 interface. On the radio link specifically the ADT telegram has a different structure (e.g. R-ORG\_EN).

**RADIO(VLD)**
<img src='https://jschober88-enocean.googlecode.com/hg/iotsys-enocean/doc/EnOcean_RadioVLD.png' />

**RADIO(ADT)**: Addressing Destination Telegram
<img src='https://jschober88-enocean.googlecode.com/hg/iotsys-enocean/doc/EnOcean_RadioADT.png' />

**RADIO(4BS)**: EEP profile 07:02:14
<img src='https://jschober88-enocean.googlecode.com/hg/iotsys-enocean/doc/EnOcean_Radio4BS.png' />

## Enocean Equipment Profiles (EEP) ##

The ERP specification defines the structure of the entire radio telegram. The user data embedded in this structure is the defined by the EEP.

The objective of interoperability is easier to reach with as less profiles as required. Therefore, it is Enocean Alliance's goal to configure each profile as universally as possible, to target a spectrum of devices in the building automation sector for all manufacturers.

It is of big interest to the Enocean Alliance that Alliance members verify new devices or newly joined companies verify their products against the existing EEP Profiles and adopt these during testing. Every newly defined EEP would increase diversity and therefore decrease interoperability.

The technical characteristics of a devices define three profile element, which make up the organizational descriptions of all profiles:
  1. The ERP radio telegram type (RORG)
  1. Basic functionality of the data content (FUNC)
  1. Type of device in its individual characteristics (TYPE)

# Add new Enocean devices #
### Create new EEP in the Enocean Library ###
  1. Package _org.opencean.core.common_: _EEPId.java_: Add EEPId here, e.g. for the contact window sensor
```
public static final EEPId EEP_D5_00_01 = new EEPId("D5:00:01");
```
  1. _Package org.opencean.core.eep_: _EEPParserFactory.java_: Connect EEP with function, which has to be created for the specific profile

### Create new Enocean Entity ###
In the package _at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.entity_ a new Enocean entity with the EEP of the new Enocean device has to be added.
It just contains the contract for the Enocean entity, like for example for the EEP D5:00:01:

```
package at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.entity;

public interface EntityEEP_D50001 extends EnoceanEntity
{
	public static final String CONTRACT = "enocean:EntityEEP_D50001";
}
```

### Create new Entity Implementation ###
To add a new Enocean device to this connector a new Entity implementation has to be added to the package _at.ac.tuwien.auto.iotsys.gateway.obix.objects.enocean.entity.impl_.
The entity implementation of the window contact implementing the EEP D5:00:01 is used to explain the process of adding a new device.
The EEP D5:00:01 has two datapoints: a contact and a learn button, both are boolean. Therefore, different types of oBIX data types are defined in the packages _at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.datapoint_ and _at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.datapoint.impl_. For the contact datapoint a _EnoceanDPTBoolOpenClosedImpl_ and for the learn mode a _EnoceanDPTBoolOnOffImpl_ will be used.

In the constructor of the EntityEEP\_D50001Impl the two datapoints are created and added to the entity. Additionally, a watchdog is added to the ESP3Host with the method _notifyWatchDog_ to notify the connector when the data has changed.

There are three important functions which have to be overwritten:
  * _initialize()_: Insert code here which should be executed after object creation.
  * _writeObject(Obj input)_: When the entity is written, this function should create a new Enocean telegram using the data of the data points of this entity implementation.
  * _refreshObject()_: When the entity is read, this function should request new information from the device. Since Enocean devices use mostly energy harvesting they just send new data after a state change.

# Java Enocean Library (API) #

The class _ESP3Host_ is used to handle all the Enocean communication.  The method _connect()_ creates a _ProtocolConnector_, which handles the serial communication over the USB gateway and then it has to be started with the following command:

```
new Thread(esp3Host).start()
```

To initialize the _EnoceanConnector_ correctly there are a some useful methods in the class _ESP3Host_:

  * _connect()_: Creates a new instance of _ProtocolConnector_ and opens a serial connection.
  * _disconnect()_: Closes the serial connection.
  * _setSenderId(String idString)_: Sets the EnoceanID of the USB gateway.
  * _getSenderId()_: Returns the EnoceanID of the USB gateway.
  * _setSerialPortName(String name)_: Sets the serial port of the USB gateway.
  * _getSerialPortName()_: Returns the serial port of the USB gateway.
  * _addWatchDog(EnoceanId id, EnoceanWatchdog enoceanWatchdog)_: Add a new watchdog for an Enocean devices.

The serial connection is handled by the class _EnoceanSerialConnector_ which implements the _ProtocolConnector_.

In the package _org.opencean.core.packets_ different types of packets of the Enocean protocol are included, which can be used to create new packages for sending data, e.g. the class RadioPacket4BS can be used to create a new 4BS Enocean telegram.


# Device Configuration #
The serial port of the Raspberry Pi (see UART configuration)and the the sender ID of the USB gatewayt have to specified for the connection. Furthermore, the following properties are required for a device:
  * _address_
  * _deviceName_
  * _displayName_
  * _display_
  * _manufacturer_
  * _href_
  * _historyEnabled_
  * _groupCommEnabled_

For Example a configuration for an Enocean connector may look like the following:
```
<enocean>    
   <connector>
   <name>EnOcean Linux</name>
   <enabled>false</enabled>
   <senderAddress>01:23:45:67</senderAddress>
   <serialPort>/dev/ttyS80</serialPort>
   <device>
     <type>at.ac.tuwien.auto.iotsys.gateway.obix.objects.enocean.entity.impl.EntityEEP_F60201Impl</type>
     <address>00:25:A2:DC</address>
     <deviceName>EasyClickWallTransmitter</deviceName>
     <displayName>Switching actuator</displayName>
     <display>null</display>
     <manufacturer>PEHA</manufacturer>
     <href>EasyClickWallTransmitter</href>
     <historyEnabled>false</historyEnabled>
     <groupCommEnabled>true</groupCommEnabled>
  </device>	  
  <device>        
     <type>at.ac.tuwien.auto.iotsys.gateway.obix.objects.enocean.entity.impl.EntityEEP_D50001Impl</type>
     <address>01:81:DC:DD</address>
     <deviceName>EasyclickWindowContact</deviceName>
     <displayName>Window contact</displayName>
     <display>null</display>
     <manufacturer>PEHA</manufacturer>
     <href>EasyclickWindowContact</href>
     <historyEnabled>false</historyEnabled>
     <groupCommEnabled>true</groupCommEnabled>
  </device> 	 
</connector>     
```

# Raspberry Pi #

## UART Initialization ##

In order to use the dedicated UART pins on the Raspberry Pi, first they have to be removed from their default application which is debugging.
To do this edit "/boot/cmdline.txt" and "/etc/inittab" {4}.
This files should be backuped to make it possible to return to the default configuration:
```
cp /boot/cmdline.txt /boot/cmdline.bak
cp /etc/inittab /etc/inittab.bak
```

Remove "console=ttyAMA0,115200" and "kgdboc=ttyAMA0,115200" configuration parameters from the "/boot/cmdline.txt" configuration file using nano editor.
```
vi /boot/cmdline.txt
```

Comment out the last line on the "/etc/inittab" file. Put a '#' before "T0:23:respawn:/sbin/getty -L ttyAMA0 115200 vt100.
```
vi /etc/inittab
```

Now the RXD (GPIO15) and TXD (GPIO14) pins are available for general UART use.

To use the Raspberry Pi UART interface with the Java RXTX library one more step is required. A symbolic link the serial port (UART) has to be created:
```
sudo ln -s /dev/ttyAMA0 /dev/ttyS80
```

## Felix Framework Execution ##

And finally to start the Felix framework, the following command should be used in the Felix framework directory _iotsys-dir/felix-framework-4.2.1/_:
```
sudo java -Djava.library.path=/usr/lib/jni -jar bin/felix.jar 
```

# Enocean Devices #

The following devices have been used for developing and testing this Enocean connector:

### [EasyClick Plus Receiver](http://www.peha.de/cms/front_content.php?client=1&lang=2&idcatart=1234&Page=1&ProductsPage=48&keyword=&catID=530&prodID=20641) ###

**ID**: configured for receiving packets from ID 00:25:A2:DC

**Packet Type**: 0x01 RadioRPS

**RORG**: F6

RLC is not used as security mechanism

### [Easyclick wall transmitter](http://www.peha.de/cms/front_content.php?client=1&lang=2&idcatart=1234&Page=1&ProductsPage=48&keyword=&catID=382&prodID=18294) ###
**ID**: 00:25:A2:DC

**Packet Type**: 0x01 RadioRPS

**RORG**: F6

**ON Packet**:

```
RadioPacketRPS[header=[Header: dataLength=07, optionalDataLength=07, packetType=01, crc8h=7A], payload=Payload: data=[F6, 50, 00, 25, A2, DC, 30], optionaldata=[01, FF, FF, FF, FF, 40, 00], crc8d=-95], raw=[55, 00, 07, 07, 01, 7A, F6, 50, 00, 25, A2, DC, 30, 01, FF, FF, FF, FF, 40, 00, A1], [sender=00:25:A2:DC, repeaterCount=0]
```
**OFF Packet**:
```
RadioPacketRPS[header=[Header: dataLength=07, optionalDataLength=07, packetType=01, crc8h=7A], payload=Payload: data=[F6, 70, 00, 25, A2, DC, 30], optionaldata=[01, FF, FF, FF, FF, 36, 00], crc8d=-125], raw=[55, 00, 07, 07, 01, 7A, F6, 70, 00, 25, A2, DC, 30, 01, FF, FF, FF, FF, 36, 00, 83], [sender=00:25:A2:DC, repeaterCount=0]
```

### [Easyclick window contact](http://www.peha.de/cms/front_content.php?client=1&lang=2&idcatart=1234&Page=1&ProductsPage=48&keyword=&catID=382&prodID=18672) ###
**ID**: 01:81:DC:DD

**Packet Type**: 0x01 Radio1BS

**RORG**: D5

**CLOSED**:
```
RadioPacket1BS[header=[Header: dataLength=07, optionalDataLength=07, packetType=01, crc8h=7A], payload=Payload: data=[D5, 09, 01, 81, DC, DD, 00], optionaldata=[01, FF, FF, FF, FF, 39, 00], crc8d=66], raw=[55, 00, 07, 07, 01, 7A, D5, 09, 01, 81, DC, DD, 00, 01, FF, FF, FF, FF, 39, 00, 42], [sender=01:81:DC:DD, repeaterCount=0], [dataByte=09
```
**OPEN**:
```
RadioPacket1BS[header=[Header: dataLength=07, optionalDataLength=07, packetType=01, crc8h=7A], payload=Payload: data=[D5, 08, 01, 81, DC, DD, 00], optionaldata=[01, FF, FF, FF, FF, 39, 00], crc8d=-42], raw=[55, 00, 07, 07, 01, 7A, D5, 08, 01, 81, DC, DD, 00, 01, FF, FF, FF, FF, 39, 00, D6], [sender=01:81:DC:DD, repeaterCount=0], [dataByte=08
```

### [EnOcean Wireless Valve Actuator](http://shop.loxone.com/enen/enocean-valve-actuator.html) ###
**ID**: 00:85:7A:08

**Packet Type**: 0x01 Radio4BS

**RORG**: A5 (A5-20-01)

**RECEIVE**:
```
RadioPacket4BS[header=[Header: dataLength=0A, optionalDataLength=07, packetType=01, crc8h=EB], payload=Payload: data=[A5, 80, 08, 0A, 80, 00, 85, 7A, 08, 00], optionaldata=[01, FF, FF, FF, FF, 34, 00], crc8d=46], raw=[55, 00, 0A, 07, 01, EB, A5, 80, 08, 0A, 80, 00, 85, 7A, 08, 00, 01, FF, FF, FF, FF, 34, 00, 2E], [sender=00:85:7A:08, repeaterCount=0], [db0=80, db1=0A, db2=08, db3=80, teachIn=true]
```


# References #
{1} [| Enocean Equipment Profiles EEP V2.6](http://www.enocean.com/en/enocean-software/)

{2} [| Enocean Serial Protocol 3](http://www.enocean.com/en/enocean-software/)
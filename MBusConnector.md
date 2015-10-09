# Introduction #

IoTSyS is an integration middleware for the Internet of Things. It provides a communication stack for embedded devices based on IPv6, Web services and oBIX to provide interoperable interfaces for smart objects.  The IoTSyS middleware aims providing a gateway concept for existing sensor and actuator systems found in nowadays home and building automation systems (KNX, BACnet, ZigBee, Wireless M-Bus), a stack which can be deployed directly on embedded 6LoWPAN devices and further addresses security, discovery and scalability issues.

This connector extends IoTSyS by the M-Bus (Meter Bus) standard. Therefore, the API from the Wireless M-Bus has been altered to make the communication with the meters as easy as possible. To use this API in IoTSyS a M-Bus connector was created. As Raspberry Pi is be the a target platform for IoTSyS a piggy back board M-Bus-Master for a Raspberry Pi was created.

# M-Bus Protocol Overview #

The M-Bus standard is defined in EN 13757-2 (physical and link layer) and
EN 13757-3 (application layer). A detailed description is available at the official M-Bus homepage {1}.

The M-Bus was developed to fill the need for a system for the networking and remote reading of utility meters, for example to measure the consumption of gas or water in the home. This bus fulfills the special requirements of remotely powered or battery driven systems, including consumer utility meters. When interrogated, the meters deliver the data they have collected to a common master, which can, for example, be a hand-held computer, connected at periodic intervals to read all utility meters of a building. {1}

## Physical Layer ##

The M-Bus is a hierarchical system, with communication controlled by a master. The M-Bus consists of the master, a number of slaves (end-equipment meters) and a two-wire connecting cable. The slaves are connected in parallel to the transmission medium.

In order to realize an extensive bus network with low cost for the transmission medium, a two-wire cable was used together with serial data transfer. In order to allow remote powering of the slaves, the bits on the bus are represented as follows:

The transfer of bits from master to slave is accomplished by means of voltage level shifts. A logical "1" (Mark) corresponds to a nominal voltage of +36V at the output of the bus driver (repeater), which is a part of the master; when a logical "0" (Space) is sent, the repeater reduces the bus voltage by 12V to a nominal +24V at its output.
Bits sent in the direction from slave to master are coded by modulating the current consumption of the slave. A logical "1" is represented by a constant (versus voltage, temperature and time) current of up to 1.5mA, and a logical "0" (Space) by an increased current drain requirement by the slave of additional 11-20mA. The mark state current can be used to power the interface and possibly the meter or sensor itself.

## Data Link Layer ##

The physical layer makes certain demands on the data link layer. Besides half-duplex asynchronous serial transmission with data rates between 300 and 9600 Baud, these include the requirement that at least every eleventh bit should be a logical 1, and also that there should be a Master-Slave structure, since the slaves can not communicate with each other.
The protocol of the data link layer is based on the international standard IEC 870-5, which defines the transmission protocols for telecontrol equipment and systems. The M-Bus protocol described below derives from the above standard, but doesn´t use all the IEC functions.

### Transmission Parameters ###

This protocol uses asynchronous serial bit transmission, in which the synchronization is implemented with start and stop bits for each character. There must be no pauses within a telegram, not even after a stop bit. Since quiescence on the line corresponds to a 1 (Mark), the start bit must be a Space, and the stop bit a Mark. In between the eight data bits and the even parity bit are transmitted, ensuring that at least every eleventh bit is a Mark. The bits of data are transmitted in ascending order, i.e. the bit with the lowest value (LSB = least significant bit) is the first one to be found on the line. The transmission takes place in half duplex and with a data rate of at least 300 Baud.

### Telegram Format ###
In the M-Bus specification there are three different telegram formats, which can be recognized by means of special start characters. In the table below the telegram formats used for the M-Bus will now be explained.

| **Single Character** | **Short Frame** | **Control Frame** | **Long Frame** |
|:---------------------|:----------------|:------------------|:---------------|
| E5h | Start 10h| Start 68h | Start 68h |
|  | C Field | L Field | L Field |
|  | A Field | L Field | L Field |
|  | Check Sum | Start 68h | Start 68h |
|  | Stop 16 h | C Field | C Field |
|  |  | A Field | A Field |
|  |  | CI Field | CI Field |
|  |  | Check Sum | User Data (0-252 Byte) |
|  |  | Stop16h | Check Sum |
|  |  |  | Stop16h |

**Single Character:**
This format consists of a single character, which the E5h (decimal 229), and serves to acknowledge receipt of transmissions.

**Short Frame:**
This frame with a fixed length begins with the start character 10h, and besides the C and A fields includes the check sum (this is made up from the two last mentioned characters), and the stop character 16h.

**Long Frame:** With the long frame, after the start character 68h, the length field (L field) is first transmitted twice, followed by the start character once again. After this, there follow the function field (C field), the address field (A field) and the control information field (CI
field). The L field gives the quantity of the user data inputs plus 3 (for C,A,CI). After the user data inputs, the check sum is transmitted, which is built up over the same area as the length field, and in conclusion the stop character 16h is transmitted.

**Control Frame:**
The control sentence conforms to the long sentence without user data, with an L field from the contents of 3. The check sum is calculated at this point from the fields C, A and CI.

### Meaning of the Fields ###

**C Field:**
The following table shows the function codes used in the calling and the replying directions:

| **Name** | **Hex Value** | **Telegram** | **Description** |
|:---------|:--------------|:-------------|:----------------|
| SND\_NKE | 40 | Short Frame | Initialization of Slave |
| SND\_UD |  53/73 | Long/Control Frame | Send User Data to Slave |
| REQ\_UD2 | 5B/7B | Short Frame | Request for Class 2 Data |
| REQ\_UD1 | 5A/7A | Short Frame | Request for Class1 Data |
| RSP\_UD | 08/18/28/38 | Long/Control Frame | Data Transfer from Slave to Master after Request |

**A Field:**
The address (A) field is used to address a slave in calling direction and to identify the sender of information in receiving direction. The addresses 1 to 250 can be allocated to the individual slaves, up to a maximum of 250. The address 0 is reserved for unconfigured slaves and the addresses 254 and 255 are used for broadcasts.

**CI Field:**
The control information (CI) field is already a part of the Application Layer, and is described in more detail in section [Application Layer](MBusConnector#Application_Layer.md). It is used to distinguish between the formats of the long and the control frames. The control information allows the implementation of a variety of actions in the master or the slaves.

**Check Sum:**
The Check Sum is used to recognize transmission and synchronization faults. The Check Sum is calculated from the arithmetical sum of
the data mentioned above without taking carry digits into account.

### Communication Process ###

The Data Link Layer uses the two kinds of transmission services Send/Confirm (SND/CON) and Request/Respond (REQ/RSP).

_Send/Confirm Procedures:_

**SND\_NKE → Single control character**
This procedure is used to start up after the interruption or beginning of communication. The slave responds to a correctly received SND\_NKE with an acknowledgment using of a single character (E5h).

**SND\_UD → Single control character**
With this procedure the master sends user data to the slave. The slave can either confirm the correct receipt of data with a single character acknowledge (E5h), or by omitting a confirmation signal that it did not receive the telegram correctly.

_Request/Respond Procedures:_

**REQ\_UD2 → RSP\_UD**
The master requests data from the slave according to Class 2. The slave can either transfer its data with RSP\_UD, or give no response indicating that the REQ\_UD2 telegram has not been received correctly or that the address contained in the REQ\_UD2 telegram does not match.

## Application Layer ##

The standardized application protocol is defined in the standard EN1434-3 for data exchange with heat meters. This standard is also suitable for other consumer utility meters, e.g. for gas and water. However, EN1434-3 only covers the data structure in the reply direction, the data structure generally used in the direction master to slave will be presented here.

The CI field encodes the mode of operation of the data transfer between the master and a slave. To send the requested data from a slave to the master there are two possible data structures, namely fixed data structure and variable data structure.

The configuration of slaves is also defined in this layer, but won't be described here, because M-Bus meters are only read with IoTSyS.

### Fixed Data Structure ###

In the reply direction with a long frame two different data structures are used. The fixed data structure, besides a fixed length, is limited to the transmission of only two counter states of a predetermined length, which have binary or BCD coding. In contrast the variable data structure allows the transmission of more counter states in various codes and further useful information about the data. The number of bytes of the transmitted counter states is also variable with this data structure. Contrary to the fixed structure, the variable structure can also be used in calling direction. For this reasons the fixed data structure is not recommended for future developments.

The frame of the fixed data structure is shown in the following table:

| Identification No. | Access No. | Status | Medium/Unit | Counter 1 | Counter 2|
|:-------------------|:-----------|:-------|:------------|:----------|:---------|
| 4 Byte | 1 Byte | 1 Byte | 2 Byte | 4 Byte | 4 Byte |

### Variable Data Structure ###

The frame of the variable data structure is shown in the table below:

| Fixed Data Header | Variable Data Blocks (Records) | MDH | Mfg.specific data |
|:------------------|:-------------------------------|:----|:------------------|
| 12 Byte | variable number | 1 Byte | variable number |

**Fixed Data Header:**

The first twelve bytes of the user data consist of a block with a fixed length and structure :

| Ident. Nr. | Manufr. | Version | Medium | Access No. | Status | Signature |
|:-----------|:--------|:--------|:-------|:-----------|:-------|:----------|
| 4 Byte | 2 Byte | 1 Byte | 1 Byte | 1 Byte | 1 Byte | 2 Byte |

**Variable Data Blocks:**

Each data record contains one value with its description as shown in the table below, a data record, which consists of a data record header (DRH) and the actual data. The DRH in turn consists of the DIB (data information block) to describe the length, type and coding of the data, and the VIB (value information block) to give the value of the unit and the multiplier.

| DIF | DIFE | VIF | VIFE | Data |
|:----|:-----|:----|:-----|:-----|
| 1 Byte | 0-10 (1 Byte each) | 1 Byte 0-10 | (1 Byte each) | 0-N Byte |

The DIB contains at least one byte (DIF, data information field), and can be extended by a maximum of ten DIFE's (data information field extensions). After a DIF or DIFE without a set extension bit there follows the VIB (value information
block). This consists at least of the VIF (value information field) and can be expanded with a maximum of 10 extensions (VIFE). The VIF and also the VIFE's show with a set MSB that a VIFE will follow. In the value information field VIF the other seven bits give the unit and the multiplier of the transmitted value. The meaning of the multiplier can be look up in the M-Bus standard {1}.

**Manufacturer Specific Data Block:**

The manufacturer specific data block consists of the manufacturer data
header (MDH) and manufacturer  specific data. This can't be encoded because it depends on the manufacturer how this part of frame structure looks like.

## Example: Slave Read Out ##

Master sends a request (REQ\_UD2) for reading data of slave with address 1:
```
10 7B 01 7C 16 
```

Encoded request send from master to slave with address 1:
```
10	Start: Short frame
7B	C-Feld: REQ_UD2: 01FV 1011 = 4B/5B/6B/7B: Request for Class 2 Data
01	A-Feld : primary address = 1
7C	Checksum
16	Stop 
```

Received data from slave with address 1:
```
68 6A 6A 68 08 01 72 43 53 93 07 65 32 10 04 CA 00 00 00 0C 05 14 00 00 00 0C 13 13 20 00 00 0B 22 01 24 03 04 6D 12 0B D3 12 32 6C 00 00 0C 78 43 53 93 07 06 FD 0C F2 03 01 00 F6 01 0D FD 0B 05 31 32 4D 46 57 01 FD 0E 00 4C 05 14 00 00 00 4C 13 13 20 00 00 42 6C BF 1C 0F 37 FD 17 00 00 00 00 00 00 00 00 02 7A 25 00 02 78 25 00 3A 16 
```

Encoded data from slave with address 1:
```
68	Start : STart of telegram, Long or control frame
6A	L-Feld : Length of user data plus 3 	
6A	L-Feld : Length of user data plus 3 
68 	Start : 2. Start
08 	C-Feld : RSP_UD
01 	A-Feld : primary address = 1
Begin user data:
72 	CI-Feld : variable data structure, Mode 1
Fixed data block:
43 53 93 07	Identification#= 07935343
65 32 	Manufacturer = 3265h equals LSE
10 04	Version = 10 Medium = Heat
CA 00	Access number = CA Status  = 00h
00 00	Signatur = 00 00h
1.Datenrecord:
0C	DIF: 8 digit BCD
05	VIF: E0000nnn = Energy 10^(nnn-3) Wh =  0.001Wh to 10000Wh: 10^(5-3) Wh = 100 Wh
14 00 00 00	date = 14 * 100 Wh = 1.4 kWh
2.Datenrecord:
0C	DIF: 8 digit BCD
13	VIF: E0010nnn = Volume 10(nnn-6) m3 =  0.001l to 10000l: 10^(3-6) m3 = 10^-3 m3 = 1l
13 20 00 00	date = 2013 * 1l = 2013 l
3.Datenrecord:
0B	DIF: 6 digit BCD
22	VIF: E010 00nn On Time: nn = 00 seconds, 01 minutes, 10 hours, 11 days
01 24 03	date = 032401 hours
4.Datenrecord:
04	DIF: 32 Bit Integer
6D	VIF: E110110n: Time Point n = 0 date = data type G, n = 1 time & date = data type F 
12 0B D3 12	date: Type F = Compound CP32: Date and Time: 
5.Datenrecord:
32	DIF: 16 Bit Integer, fehlerbehaftet
6C	VIF: E110110n Time Point n = 0 = data type G
00 00	date: Type F = Compound CP32: Date and Time: 
6.Datenrecord:
0C	DIF: 8 digit BCD
78	VIF: E110110n Time Point n = 0 = data type G
43 53 93 07	date: E111 1000 Fabrication No 07935343
7.Datenrecord:
06	DIF: 48 Bit Integer
FD	VIF: 1111 1101 Extension of VIF-codes true VIF is given in the first VIFE and is coded using table 8.4.4 a)
0C	VIFE: E000 1100 Model / Version
F2 03 01 00 F6 01	date: Model / Version 01F6000103F2h = 2156073649138
8.Datenrecord:
0D	DIF: variable Länge
FD	VIF: 1111 1101 Extension of VIF-codes true VIF is given in the first VIFE and is coded using table 8.4.4 a)
0B	VIFE: E000 1011 Parameter set identification
05	LVAR: ASCII string with LVAR characters, Länge 5
31 32 4D 46 57 	date: 57 46 4D 32 31 = WFM21
9.Datenrecord:
01	DIF: 8 bit Integer
FD	VIF: 1111 1101 Extension of VIF-codes true VIF is given in the first VIFE and is coded using table 8.4.4 a)
0E	VIFE: E000 1110 Firmware version #
00	date: 0
10.Datenrecord:
4C  	DIF: 8 digit BCD, LSB of storage number 1
05	VIF: E0000nnn = Energy 10^(nnn-3) Wh =  0.001Wh to 10000Wh: 10^(5-3) Wh = 100 Wh
14 00 00 00	date = 14 * 100 Wh = 1.4 kWh
11.Datenrecord:
4C	DIF: 8 digit BCD, LSB of storage number 1
13	VIF: E0010nnn = Volume 10(nnn-6) m3 =  0.001l to 10000l: 10^(3-6) m3 = 10^-3 m3 = 1l
13 20 00 00	date = 2013 * 1l = 2013 l
12.Datenrecord:
42	DIF: 16 Bit Integer, LSB of storage number 1
6C	VIF: E110110n Time Point n = 0 = data type G
BF 1C	date: Type F = Compound CP32: Date and Time: 
13.Datenrecord:
0F	DIF: special function
37 FD 17 00 00 00 00 00 00 00 00 02 7A 25 00 02 78 25 00	manufacturer specific data
3A	Checksum
16	Stop
```

# M-Bus Java API #

The M-Bus Java Api is shown as class diagram in the picture below:

**Class diagramm:**
<img src='https://jschober88-mbus.googlecode.com/hg/iotsys-mbus/doc/MBus.png' />

The class MBusConnector is used to create a new instance of the class ComPortReader, which handles the serial communication with the M-Bus master. To initialize the ComPortReader correctly there are a some useful methods in the class MBusConnector:
  * **connect():** Creates a new instance of ComPortReader and open a serial connection.
  * **disconnect():** Closes the serial connection.
  * **setInterval():** Sets the periodic reading interval of the meters.
  * **setAdress():** Sets the address of the meter.
  * **refresh():** Forces a start of the read out procedure of a meter.

If the ComPortReader receives a new telegram, it is added to a TelgramManager. The class Telegram represents the data structure of a M-Bus long frame. Therefore, Telegram is split into TelegramHeader (representing the fields start, L, C, A, CI, check sum and stop) and the and TelegramBody (representing the user data).

The TelegramBody can be split furthermore as seen in section [Data Link Layer](MBusConnector#Data_Link_Layer.md) into the TelegramBody header representing the fixed data header and the TelegramBodyPayload representing the variable data blocks, which can be again differentiated in the following fields:
  * DIFTelegramField
  * DIFETelegramField
  * VIFTelegramField
  * VIFETelegramField

The information of all data fields is encoded using the tables of the appendix of the M-Bus documentation {1}. For debugging the class Telegram and all the related classes have a method _debugOutput()_ to print the encoded data to the console.

# Connection and Device Configuration #

For the connection you need to specify the serial port of the Raspberry Pi (see [UART](MBusConnector#UART.md) configuration). Further, the serial number and the address of the meter and polling interval need to be specified.

```
 <mbus>
 <connector>
  <name>MBus Smart Meter Linux</name>
  <enabled>true</enabled>
  <serialPort>/dev/ttyS80</serialPort>
  <device>
     <type>at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.mbus.SmartMeterImplMBus</type>
    <address>01</address>
	<interval>60</interval>
	<serialnr>07935343</serialnr>
    <href>smartmeter</href>
    <historyEnabled>true</historyEnabled>
    <historyCount>1000</historyCount>
  </device>
</connector>

<connector>
  <name>MBus Smart Meter Windows</name>
  <enabled>false</enabled>
  <serialPort>COM17</serialPort>
  <device>
        <type>at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.mbus.SmartMeterImplMBus</type>
	  <interval>60</interval>
      <address>01</address>
	  <serialnr>07935343</serialnr>
      <href>smartmeter</href>
      <historyEnabled>true</historyEnabled>
      <historyCount>1000</historyCount>
    </device>
  </connector>
</mbus>
```

# M-Bus-Master for Raspberry Pi #

To create a master for the M-Bus a layout for simple M-Bus-Master for up to 3 slaves {2} has been used. This layout had to be extended, because the Raspberry Pi doesn't have a RS232 port, which is necessary for this M-Bus-Master. This master is on the right side of the picture below. The M-Bus-Master operates  with a supply voltage of +/- 15V. The Raspberry Pi only provides 3.3V and 5V as output voltages. Hence, a DC/DC converter IH0515S {3} produced by the company XP POWER was used to convert the 5V output of the Raspberry Pi to +15V and -15V with a maximum output current of 66mA for each output. The output currents are sufficient, because a M-Bus slave consumes between 12mA and 20mA and the Mini M-Bus-Master is designed to operate with up to 3 slaves.

The Raspberry Pi GPIO (TXD GPIO14 and RXD GPIO15) voltage levels are 3.3V and therefore, a MAX3232 has to be used to operate correctly with the M-Bus-Master input and output, which uses the RS232 voltage levels of +/-12V.

<img src='https://jschober88-mbus.googlecode.com/hg/iotsys-mbus/doc/MBus-Master_Circuit_BW.png' />

<img src='https://jschober88-mbus.googlecode.com/hg/iotsys-mbus/doc/MBus-Master_Board.png' />

# Raspberry Pi #

## UART ##

In order to use the dedicated UART pins on the Raspberry Pi, first they have to be removed from their default application which is debugging.
To do this edit "/boot/cmdline.txt" and "/etc/inittab" {4}.
This files should be backuped to make it possible to return to the default configuration:
```
cp /boot/cmdline.txt /boot/cmdline.bak
cp /etc/inittab /etc/inittab.bak
```

Remove "console=ttyAMA0,115200" and "kgdboc=ttyAMA0,115200" configuration parameters from the "/boot/cmdline.txt" configuration file using nano editor.
```
nano /boot/cmdline.txt
```

Comment the last line on the "/etc/inittab" file. Put a '#' before "T0:23:respawn:/sbin/getty -L ttyAMA0 115200 vt100.
```
nano /etc/inittab
```

Now the RXD (GPIO15) and TXD (GPIO14) pins are available for general UART use.

To use the Raspberry Pi UART interface with the Java RXTX library one more step is required. A symbolic link the serial port (UART) has to be created:
```
sudo ln -s /dev/ttyAMA0 /dev/ttyS80
```

# References #
{1} http://www.m-bus.com/

{2} [M-Bus Mini Master](http://www.m-bus.com/files/minimaster.tif)

{3} http://at.farnell.com/xp-power/ih0515s/wandler-dc-dc-2w-15v/dp/8727929

{4} https://sites.google.com/site/semilleroadt/raspberry-pi-tutorials/gpio
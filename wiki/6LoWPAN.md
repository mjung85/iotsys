# Introduction #

The goal is to have the IoTSyS interface directly on the end device. To show the feasibility below a simple example illustrates how to deploy the CoAP/oBIX stack on a constrained microcontroller.

The example is based on the Contiki operating system and currently mainly on the Erbium CoAP engine example. Providing a proper oBIX framework for Contiki is ongoing work.

# How to #

Download Instant Contiki (> 2.7) from www.contiki-os.org.

Clone the forked repository:
git clone https://github.com/mjung85/contiki

In the contiki base directory of the working copy init the submodule
```
contiki# git submodule init
contiki# git submodule update
```

For the simple example we use a Z1 sensor node from Zolertia. It can also be run in the simulator.

**Simulator (Cooja):**
```
contiki/examples/iotsys# make TARGET=cooja iotsys.csc
```
This starts a simple simulation with one RPL border router and a Z1 sensor node that runs a CoAP/oBIX server.

The host operating system can connect to the simulated WSN by creating a tunnel connection to the RPL border router.

```
contiki/examples/iotsys# make connect-router-cooja
```
Afterwards, the mote can be reached using the IPv6 address aaaa::c30c:0:0:2. Just test it by using the Firefox plugin Copper.

**Note**: For working properly you have to select CoAP 13, enable Debug options and set accept to application/xml.

**Z1 deployment**
```
contiki/examples/iotsys# make TARGET=z1 savetarget
contiki/examples/iotsys# make iotsys-server.upload && make z1-reset
```

# Z1 specific #

See [ZolertiaWiki](http://zolertia.sourceforge.net/wiki/index.php/Mainpage:Contiki_apps#Change_the_default_MAC_address_and_Node_ID.2C_and_burn_it_to_flash).

Burn node id (used for stateless IPv6 auto configuration):

```
/contiki/examples/z1$
make clean && make burn-nodeid.upload nodeid=158 nodemac=158 && make z1-reset && make login
```

List available motes:
```
make TARGET=z1 z1-motelist
```
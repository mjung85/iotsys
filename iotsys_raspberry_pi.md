#IoTSyS on Raspberry PI

# IoTSyS on Raspberry PI #

This guide assumes that you have a Raspberry Pi with a fresh installed Raspbian OS.

  * Install Java 7 Runtime

```
apt-get install open-jdk-7-jre
```

  * Create an OSGI build on your PC and copy the complete felix framework directory
  * Execute felix within the felix directory. Note: The working directory is important since the config files will be resolved relative to this directory.

```
felix-dir$ java -jar bin/felix.jar
```

## IoTSyS with direct KNX TP connectivity based on eibd ##
You can either use a TP UART USB or GPIO extension module to directly interface a KNX bus without any KNXnet/IP router or tunnel interface in between.

For example you can use the busware.de connectors:
  * http://www.busware.de/tiki-index.php?page=TUL
  * http://www.busware.de/tiki-index.php?page=ROT

**eibd setup**

```
wget  http://www.auto.tuwien.ac.at/~mkoegler/debian/pool/main/p/pthsem/pthsem_2.0.8.tar.gz
tar -xzvf pthsem_2.0.8.tar.gz
cd pthsem-2.0.8
sudo ./configure
sudo make install
sudo ldconfig -v

cd ..

wget http://www.auto.tuwien.ac.at/~mkoegler/debian/pool/main/b/bcusdk/bcusdk_0.0.5.tar.gz
tar -xzvf bcusdk_0.0.5.tar.gz
cd bcusdk_0.0.5
sudo ./configure --enable-onlyeibd --enable-tpuarts --enable-eibnetip --enable-eibnetipserver
make install
```

Depending on the TP UART interface you use, the eibd can now be started with the following commands:

**TP UART USB Light (see www.busware.de)**
```
eibd -i -D -T -S -e 1.1.251 tpuarts:/dev/ttyACM0
```

**Configure eibd connectivity in IoTSyS**

eibd provides a similar interface like a KNXnet/IP router or tunnel interface. Just use the local loopback device (127.0.0.1) in config/devices.xml when specifying the according network configuration.
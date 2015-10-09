

# Introduction #
## Multicast DNS ##
Multicast DNS or mDNS is the use of DNS protocol on the local network multicast address.

The multicast addresses is 224.0.0.251 for IPv4 and FF02:0:0:0:0:0:0:FB for IPv6 link-local network.

The service host will open the UDP port 5353 to listen for incoming DNS query. If a corresponding record is found in the host's record registry, it will reply back by sending another multicast DNS response.

## Service Discovery based on Multicast DNS ##
Multicast DNS - Service Discovery or mDNS-SD is a service discovery protocol based on DNS protocol. A mDNS-SD client broadcast DNS queries for its demanded services and a mDNS-SD server with the corresponding service response back with DNS responses.

There are two phases in mDNS-SD: Service Browsing and Service Resolving.

In Service Browsing, a mDNS-SD client browses for all advertised services. The multicast DNS query is as follow:

```
DNS Query: 
Name: _obix._coap.local: type PTR, class IN, "QM" question
```

And the mDNS-SD server responses with a PTR record that points to the actual service instance (in this example the sunblind1 device)
```
DNS Response:
Name: _obix._coap.iotsys.auto.tuwien.ac.at: type PTR, class IN, sunblind1._sunblindactuator._sub._obix._coap.iotsys.auto.tuwien.ac.at
Name: _sunblindactuator._sub._obix._coap.iotsys.auto.tuwien.ac.at: type PTR, class IN, sunblind1._sunblindactuator._sub._obix._coap.iotsys.auto.tuwien.ac.at
```

In this response, the sunblind1 device (service instance) is advertised under 2 pointer records, one under parent service type - _obix._coap.domain\_name and one under sub service type - _sunblindactuator._sub._obix._coap.domain\_name.

The server can send as many PTR records as the number of matched service types (_obix._coap.local) it has.

After receiving all the Pointer record PTR, the client starts the Service Resolving phase. In this phase, mDNS-SD client sends out a multicast DNS query asking for SRV and TXT record instead of PTR to get the service instance's details:

```
DNS Query:
Name: sunblind1._obix._coap.local: type SRV, class IN, "QM" question
Name: sunblind1._obix._coap.local: type TXT, class IN, "QM" question
```

And the response:

```
DNS Response:
Name: sunblind1._sunblindactuator._sub._obix._http.iotsys.auto.tuwien.ac.at: type SRV, class IN, cache flush, priority 0, weight 0, port 8080, target sunblind1.testdevices.iotsys.auto.tuwien.ac.at
Name: sunblind1._sunblindactuator._sub._obix._http.iotsys.auto.tuwien.ac.at: type AAAA, class IN, cache flush, addr 2001:629:2500:570::11b
```

After receiving these detail information, which are the port and the address of the queried service, the service is said to be resolved and can be accessed via the retrieved port and address information.

# mDNS-SD in IoTSyS gateway #
To interact with the IoTSyS gateway via multicast DNS protocol, we need a mDNS client or mDNS library. The mDNS library used in the project is the open sourced JmDNS project and the usage can be found below.

## Using JmDNS library ##
```
	public static void main(String[] args) throws UnknownHostException, IOException {
		final JmDNS jmdns = JmDNS.create(InetAddress.getByName("an IP address"));
		jmdns.addServiceListener("_obix._coap.domain_name.", new SampleListener());
	}
```

The IP address specified in the _getByName_ function indicates the IP address of the network interface on which mDNS packages should be broadcast and received

The given class "SampleListener" is as follow:

```
class SampleListener implements ServiceListener {

		@Override
		public void serviceAdded(ServiceEvent event) {
			/// When a PTR is responded
			System.out.println("Service added   : " + event.getName() + "." + event.getType());
		}

		@Override
		public void serviceRemoved(ServiceEvent event) {
			System.out.println("Service removed : " + event.getName() + "." + event.getType());
		}

		@Override
		public void serviceResolved(ServiceEvent event) {
			/// Only when DNS records other than PTR (SRV, AAAA, TXT) are found then serviceResolved is called.
			System.out.println("Service resolved: " + event.getInfo());
		}
	}

```

This class listens on service events which are derived from retrieved DNS responses and acts upon.

## Using Apple's Bonjour SDK ##
Apple's Bonjour SDK provide mDNS-SD functionality. After installing Apple's Bounjour service, the command to interact with Bonjour is 'dns-sd'.

Note about compatibility:
  * Apple's Bonjour always depends on the TXT record existence to determine if the service is resolved or not. Therefore, a dummy TXT record is added in the response for this service resolve issue.
  * Apple's Bonjour does not accept custom proto type such as _coap. It always looks for_tcp and _udp proto in query. Therefore, the devices in IoTSyS gateway is advertised both as_obix._coap.domain\_name and_obix._udp.domain\_name.
  * Apple's Bonjour cannot discover sub-typed services.
  * Apple's Bonjour does not work on IPv6 network stack_



Service browsing:
```
e:\>dns-sd -B _obix._udp iotsys.auto.tuwien.ac.at
Browsing for _obix._udp.iotsys.auto.tuwien.ac.at
Timestamp     A/R Flags if Domain                    Service Type              Instance Name
 0:16:56.407  Add     3 20 iotsys.auto.tuwien.ac.at.                    _obix._udp.               virtualindoorbrightnesssensor
 0:16:56.407  Add     3 20 iotsys.auto.tuwien.ac.at.                    _obix._udp.               virtualpresence
 0:16:56.408  Add     3 20 iotsys.auto.tuwien.ac.at.                    _obix._udp.               sunblindmiddlea
 0:16:56.409  Add     3 20 iotsys.auto.tuwien.ac.at.                    _obix._udp.               sunblindmiddleb
 0:16:56.410  Add     2 20 iotsys.auto.tuwien.ac.at.                    _obix._udp.               virtualfanspeed
```

Service resolving:

```
e:\>dns-sd -L virtualfanspeed _obix._udp iotsys.auto.tuwien.ac.at
Lookup virtualfanspeed._obix._udp.iotsys.auto.tuwien.ac.at
 0:17:04.710  virtualfanspeed._obix._udp.iotsys.auto.tuwien.ac.at. can be reached at virtualfanspeed.virtualdevices.iotsys.auto.tuwien.ac.a
t.:5683 (interface 20)
 text=text\ value
```


# JmDNS Library #

The mDNS-SD functionality of IoTSyS gateway is provided by JmDNS library with some adaptations. JmDNS library is adapted into IoTSyS gateway use case with regard the followings:
  * The original JmDNS is designed for single machine to advertise its own service, therefore, the service's target (host) is always the machine's IP address and HostName. In IoTSyS case, the gateway advertise many services on behalf of the devices it manages. Therefore, the service's target is the device's IP address and DNS name.
  * The original JmDNS does not fully implement sub service case.
  * The original JmDNS explicitly enforces the inclusion of a TxT record in responding to service resolve query (query for SRV records). Meanwhile, this record is optional. This leads to a bug in JmDNS that it depends on the order of the retrieved records in the DNS response to determine if the service is resolved.

The JmDNS is modified in the following locations:
  * DNSQuestion: 'addAnswersForServiceInfo'
  * DNSEntry: constructor
  * DNSRecord: 'getServiceEvent'
  * JmDNSImpl: 'getServiceInfoFromCache', 'updateRecord', 'handleResponse',
  * ListenerStatus: 'sameInfo'
  * ServiceInfoImpl: 'getQualifiedName', 'getTextByte', 'hasData', 'toString', 'answers', added: 'setServer', 'setIpv6Addr'


# mDNS-SD module #
## The Named service ##
The module consists of a Named service, which responses to DNS query for Name record. This acts as a simple authoritative DNS server for the IoTSyS gateway.

The Named service uses JmDNS library to provide necessary DNS objects and uses the Java Datagram Socket to provide UDP transportation

The Named service's DNS record base is built on a concurrent hash map, which is managed by class 'MdnsResolverImpl' who implements the 'MdnsResolver' interface.

## The Mdns Resolver service ##
The MdnsResolver service manages a concurrent hash map that is used by IoTSyS gateway to keep the dns name record and IPv6 of devices. When an IPv6-enabled device is connected by the corresponding connector, the 'MdnsResolver.addToRecordDict' and 'MdnsResolver.registerDevice' functions are called.
  * The first function adds the device's dns name and its IPv6 address to the concurrent hash map so that the device's name can be resolved to IPv6 by interesting DNS clients.
  * The second function registers the device as a service which is advertised over mDNS-SD protocol.

Note on the project dependency:

In OSGi environment, services and bundles dependency is important. When mDNS-SD module is not started, the IoTSyS gateway cannot find one to register its devices name and services. Thus, a service listener is implemented in IoTSyS gateway to listen for mDNS-SD status.

Named and MdnsResolver interfaces are stored in iotsys-common instead of residing in mDNS-SD, also because of the above dependency.
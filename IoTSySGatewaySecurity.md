# Introduction #

The iostys gateway should transfer messages in a secure way. In this case, a username/password authentication and a SSL protocol have been implemented.


# Details #

### Configuration ###

Users can turn on and off the security functions manually. The configurations are in the iotsys.properties :

```
iotsys.gateway.security.enable=true
iotsys.gateway.security.clientCert=true
iotsys.gateway.security.authentication=true
```

The 'enable' flag indicates whether the security functions are applied. When 'enable' is 'false', the values of 'clientCert' and 'authentication' won't have any influence on the system. Generally, it is only turned off for test purpose.

The 'clientCert' flag indicates whether a client certificate is requested.

The 'authentication' flag controls the username/password authentication.


## Username/Password ##

To protect the gateway from intruders, a username/password authentication has been implemented to keep outside visitors from interacting with the gateway. Without login, outside visitors can only view the welcome page of iotsys.

## Digital Certificates ##

In order that the incoming and out-coming messages are transferred in security under HTTP, a SSL protocol based on the tomcat server has been implemented. With a self-signed certificate, the tomcat server will make encryption and decryption for the requests using the keys in the digital certiifcates.

Users should import the root certificate which is a Certificate Authority(CA) in this case, the certificate of tomcatserver (and the client certificate if needed) in their local keystore.

The digital certifites locate in 'iotsys-gateway/ssl/certs', three certificates are needed:

**cacert.pem** : CA

**tomcatcert.pem** : Server certificate

**client.p12** : Client certificate
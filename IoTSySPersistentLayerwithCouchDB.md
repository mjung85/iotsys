# Introduction #

IoTSyS persistent layer brings a mean for storing objects state, configurations and UI commissionings. The layer uses CouchDB for storing data and Ektorp library to communicate with CouchDB. The layer consists of 4 main database connections: ConfigsDb, HistoryDb, UIDb and WriteableObjectDb, respectively for storing gateway’s configurations, objects’ history, user interface’s commissionings and actuator command objects.

These database connections all follow the singleton pattern and consist of 3 classes: an interface (Db.java), an implementation (DbImpl.java) and an Ektorp’s CouchDbRepositorySupport subclass (DbRepo.java). The Ektorp’s subclass is where the actual implementation takes place and the implementation class is used to cope with the situation when the CouchDb is not available.



# Details #
## Installation ##
To install CouchDb on the Raspberry Pi, simply issue an “apt-get install couchdb” from the Raspberry Pi shell.
## ConfigsDb ##
Configuration database maintains 3 collections of document: devices, connectors and device loaders.

The device includes address, name, display name, ipv6, type, href, group communication switch, history count, history switch and refresh switch.

The connector includes name, technology, enable switch and list of associated devices referenced by connector id.

The device loader is simply a document containing an array of device loader in the form of String.

The ConfigsDb supports transition step which migrates all configurations from device.xml configuration file to the database. The migration proceeds with dropping existing Configs database and starting to migrate all the connectors in device.xml. Once all connectors are stored and connector IDs are generated, all devices will be proceeded. ConfigsDb also supports CRUD operations so that devices, connectors and device loaders can be created, read, updated, or deleted.

### How to do a device.xml migration ###
To migrate configurations from device.xml to database, simply modify in iotsys.properties file:

iotsys.gateway.dbmigrating=true

## HistoryDb ##
History database is used to store object’s history records. A history record consists of object’s href, recorded time, object’s type and the value. Every history-enabled object will have access to HistoryDb so that object’s history records are flushed to the database after maximum number of cached events reaches.

The persisted history records will be fetched and replied to client when the in-memory records set is not enough for user request. For example, if users request for maximum 10 records but there is only 4 records found in memory, the rest 6 will be fetched from the database given that the information is available.

The history update is found at HistoryImpl class and the history fetch is found at HistoryFilterImpl class.

## UIDb ##
UI database stores user interface configuration. Basically UI database is simply a key-value store that mirrors web browser’s local storage.

Other use of UIDb is to store user’s authentication which is used by Tomcat’s security layer. The user’s authentication comes with password hashing so that it’s safe to store password in the gateway’s database.

## WriteableObjectDb ##
This database is used to persist the actuator objects so that when the gateway is restarted, previously set commands will be re-applied to the corresponding devices.

The database is hooked up inside the Obix Server whenever a write request is issued from clients.

The WriteableObject document simply contains the object’s href and it’s serialized content in String format.

## Database APIs ##
CouchDB originally come with a built-in web-based configuration client called Futon, which can be accessed from /_utils/index.html. However, CouchDB only accept connection from localhost interface by default and every clients connect from localhost will have admin role to the database (admin party). To change this, either one ca
n modify the /etc/couchdb/local.ini to set the bind\_address to 0.0.0.0 for every client endpoint or to a specific client’s ip address. In addition, admin party can be disabled by creating an admin user for the database:_

#curl -X PUT http://<CouchDB Host>:5984/_config/admins/<admin name> -d '"<admin pwd>"'_

And authenticate a request by putting http://<admin name>:<admin pwd>@ in front of <CouchDB Host>

Then the database content can be modified directly from Futon.

Additionally, the IoTSyS gateway comes with a RESTful API to communicate with UI database over the resource URI /uidb/uistorage, which is served by UIDbServlet class:
POST /uidb/uistorage
{“key1”:”value1”,”key2”:”value2,”key3”:”value3”,...}
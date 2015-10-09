# Introduction #

This page contains the description of a Groovy based domain-specific language for the iotsys gateway project.
The language allows to interact with the gateway's HTTP interface in an intuitive way. It is easily possible to set and get attributes, create virtual methods, or automatically act when a particular condition is fulfilled.

# Domain-specific language description #

Basically there are two keywords which indicate the start of an expression that belongs to the domain-specific language.

  * ` thing ` - Is used to get or set thing attributes, invoke virtual methods (See Configuration section) or listen to attribute changes.
  * ` every ` - Is used to run a command periodically

## ` thing ` keyword ##

The ` thing ` keyword indicates that either an action or a conditional action command on a particular thing is invoked.
The keyword is followed by the id of a thing. Aliases (see Configuration section) can be used as well.

Examples:

  * ` thing "myThing" ... ` - An expression without a registered alias
  * ` thing myThing ... `  - An expression where a registered alias is used
  * ` thing "myPathTo/myThing" ... ` - An expression where no additional prefix is used

### Retrieving attribute values ###

Attribute values can be retrieved by using the ` get <attribute> ` expression that succeeds a ` thing ` keyword.

Examples:

  * ` thing "myThing" get "myAttribute" ` - An expression without registered aliases
  * ` thing myThing get myAttribute ` - An expression that uses aliases

The received attribute value will be returned and printed to the console output.

### Setting attribute values ###

Attribute values can bet set by using the ` set <attribute> to <value> ` expression that succeeds a ` thing ` keyword.

Examples:

  * ` thing "myThing" set "myAttribute" to "myValue" ` - An expression that sets a string attribute without using aliases
  * ` thing myThing set myAttribute to 5 ` - An expression that sets a numerical attribute using aliases
  * ` thing myThing set myAttribute to {-> return 123 } ` - An expression that sets a numerical attribute by using a closure and aliases

### Invoking (virtual) methods ###

Things can be extended with virtual methods either by their id or type. A virtual method can contain multiple set/increase or decrease commands. A configured method can be executed on a thing.
Details on the configuration see Configuration section.

Examples:

  * ` thing myThing myMethod() ` - An expression where a method without arguments is invoked on a thing by using an alias
  * ` thing "myThing" myMethod(myArgument) ` - An expression where a method with one argument is invoked on a thing
  * ` thing myThing myMethod(myFirstArgument, mySecondArgument) `
  * ` thing myThing myMethod({ -> return 5 }) ` - An expression where a method with a closure as argument is invoked on a thing using an aliases

### Conditional actions ###

The ` thing ` keyword allows conditional actions. Listeners on attribute value changes and conditions can be defined along with an action to be executed in case of the fulfillment of the condition.
The syntax of of conditional actions is as follows:

```
thing <thing> on <attribute> [ greater | less | equal | greater or equal | less or equal ] <value> thing <thing> [ <method> | set <attribute> to <value> ]
```

Multiple ` and ` linked conditions are also possible:

```
CONDITION = <attribute> [ greater | less | equal | greater or equal | less or equal ] <value>
thing <thing> on CONDITION [and <thing> on CONDITION]* thing <thing> [ <method> | set <attribute> to <value> ]
```

Examples:

  * ` thing myThing on myAttribute greater 5 thing mySecondThing anyAction() `
  * ` thing myThing on myAttribute greater or equal 5 and mySecondThing on mySecondAttribute less 4 thing myThirdThing set myThirdAttribute to 9`

## ` every ` keyword ##

As mentioned previously, the ` every ` keyword is used to run a command periodically.
Therefore this keyword must be succeeded by the period.

The period can be expressed in three different ways:
  * ` <Number>.milliseconds `
  * ` <Number>.seconds `
  * ` <Number>.minutes `

Examples:
  * ` every 500.milliseconds ... `
  * ` every 5.seconds ... `
  * ` every 2.minutes ... `

The period is then succeeded by a ` thing ... ` expression.

Examples:
  * ` every 500.milliseconds thing "myThing" set "myAttribute" to "myValue" `
  * ` every 5.seconds thing myThing myMethod() `
  * ` every 2.minutes thing "myThing" myMethod("myArgument") `

# Configuration #

To run and use the domain-specific language it has to be configured. The configuration of of the dsl is done via xml and can be separated into three independent parts.
  * Connection & general information
  * Aliases
  * Virtual methods

The root element of the xml file is named ` iotDslConfig `

```
<?xml version="1.0"?>
<iotDslConfig xmlns="http://auto.tuwien.ac.at/iotsys-dsl"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://auto.tuwien.ac.at/iotsys-dsl dsl-config.xsd">
</iotDslConfig>
```

## Connection & general information ##
The connection & general information part is required and consists of three properties.
  * httpConnectorConfig
    * baseUrl: Host and port to the iotsys gateway server
    * prefix: Optional prefix for things (e.g., _VirtualDevices_)
  * attributeChangeCheckInterval: Poll interval for attribute checks

```
<httpConnectorConfig>
    <baseUrl>http://host[:port]</baseUrl>
    <prefix>prefix</prefix>
</httpConnectorConfig>
<attributeChangeCheckInterval>interval</attributeChangeCheckInterval>
```

## Aliases ##

In order to reduce the typing effort or make the expression more readable, aliases for both, things and attributes can be defined.
Besides more readable names of things or attributes, aliases do have the advantage that there are no quotation marks required. (see examples in previous section).

```
<aliases>
    <thingAliases>
        <thingAlias thingId="sophisticatedThingId" alias="thingId" />
    </thingAliases>
    <attributeAliases>
        <attributeAlias attributeId="encapsulated.sophisticatedAttributeId" alias="attributeId" />
    </attributeAliases>
</aliases>
```

## Virtual methods ##
The configuration allows to extends things by virtual methods. These methods can be defined to set, increase or decrease attribute values. Multiple set, increase or decrease commands are also allowed within one method.
The big advantage of this feature is, that is more intuitively and can reduce the code that has to be written considerably.
Virtual methods can applied on both, thing id's or types.
The set, increase or decrease commands can have fixed pre-configured values, or take arguments.

```
<virtualMethods>
    <thingMethods thingId="myThing">
        <method name="myMethod" arguments="0">
            <commands>
                <set attribute="myAttribute" value="myValue" />
            </commands>
            <return attribute="myReturnAttribute" />
        </method>
    </thingMethods>
    <thingMethods typeId="iot:MyType">
        <method name="myMethod" arguments="1">
            <commands>
                <set attribute="myAttribute" argumentRef="0" />
            </commands>
            <return attribute="myReturnAttribute" />
        </method>
    </thingMethods>
</virtualMethods>
```

## Specification & examples ##

A full specification of the configuration file is available in the form of an xsd [here](https://code.google.com/r/bernhardnickel-iotsys-dsl/source/browse/iotsys-dsl/src/main/resources/dsl-config.xsd)

Additionally there are some example configuration files [here](https://code.google.com/r/bernhardnickel-iotsys-dsl/source/browse/iotsys-dsl/example/config)

# Build & run #

To build the domain specific language project all that needs to be done is to run the [Gradle](http://www.gradle.org/) [jar](http://www.gradle.org/docs/current/dsl/org.gradle.api.tasks.bundling.Jar.html) task.

  * Change to iotsys-dsl directory
  * Run ` gradle jar `

The assembled jar can be found in the  ` build/libs ` directory. This jar file contains the required DSL classes and the groovy script that can be executed.

` groovy -cp iotsys-dsl.jar jar:file:iotsys-dsl.jar'!'/dsl.groovy <configuration-file> [<script-file>]`

Examples:
  * `groovy -cp iotsys-dsl.jar jar:file:iotsys-dsl.jar'!'/dsl.groovy myconfig.xml`
  * `groovy -cp iotsys-dsl.jar jar:file:iotsys-dsl.jar'!'/dsl.groovy myconfig.xml myscript.groovy`

**Important note:**

As some DSL classes use [Grape](http://groovy.codehaus.org/Grape) there is a chance that further configurations (such as proxy or ivy setup) might be required.

See: http://groovy.codehaus.org/Grape
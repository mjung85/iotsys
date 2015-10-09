# IoTSyS Gateway Getting Started #

## Requirements ##
  * Java JDK >= 1.7
  * http://www.gradle.org Gradle >= 1.10
  * Mercurial SCM client

## Install ##
  * Clone the project with your mercurial client to your local _iotsys-dir_
  * Check your environment variables for Java and Gradle and verify the version

## Build the project ##
  * _iotsys-dir_# gradle clean
  * _iotsys-dir_# gradle compileJava

## Run the gateway in eclipse ##
  * _iotsys-dir_# gradle eclipse
  * optional: modify iotsys-gateway/config/devices.xml (Standard config has only virtual devices)
  * Run _at.ac.tuwien.auto.iotsys.gateway.IoTSySGateway_ in iotsys-gateway

## Run the gateway using OSGI ##
  * _iotsys-dir_# gradle setupFelix
  * optional: _iotsys-dir_# gradle clearOsgiCache
  * _iotsys-dir_# gradle deployOsgi
  * _iotsys-dir_/felix-framework-4.2.0/# java -jar bin/felix.jar

## Interact with the gateway ##
  * http://localhost:8080/obix see [HTTP interaction](HTTPinteraction.md)
  * http://localhost:8080/ see [oBeliX HTML5 client](oBeliX.md)
  * coap://localhost/.well-known/core see [CoAP interaction](COAPinteraction.md)
  * http://localhost:8080/soap?wsdl






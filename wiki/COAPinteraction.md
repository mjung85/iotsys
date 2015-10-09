

# Introduction #

For the CoAP interaction you browser needs to provide a user agent for the Constrained Application Protocol (CoAP). The [Copper](https://addons.mozilla.org/en-us/firefox/addon/copper-270430/) add-on provides such a user agent for the Firefox browser.

**NOTE: CoAP 13-** The gateway currently supports the CoAP 13 draft.

# Discover available resources #
<img src='https://iotsys.googlecode.com/hg/misc/img/coap/discover.png' />
According to the CoRE link format you can query the gateway using the `/.well-known/core` URL.

**Request:**
```
COAP GET coap://localhost/.well-known/core
```

**Response (Excerpt):**
```
</watchService>;rt="obix:WatchService";if="obix"
</alarms>;rt="obix:AlarmSubject";if="obix"
</VirtualDevices/virtualPresence>;rt="iot:PresenceDetectorSensor";if="obix"
</VirtualDevices/virtualPresence/presenceStatus>;rt="obix:bool";if="obix"
</VirtualDevices/virtualBrightnessActuator>;rt="iot:BrightnessActuator";if="obix"
</VirtualDevices/virtualBrightnessActuator/value>;rt="obix:obj";if="obix"
</VirtualDevices/virtualBrightnessActuator/value/history>;rt="obix:int";if="obix"
</VirtualDevices/virtualBrightnessActuator/value/history/count>;rt="obix:int";if="obix"
</VirtualDevices/virtualBrightnessActuator/value/history/start>;rt="obix:abstime";if="obix"
</VirtualDevices/virtualBrightnessActuator/value/history/end>;rt="obix:abstime";if="obix"
```

# Query or modify a sensor or actuator #
Use the same oBIX interaction like with [HTTP](HTTPinteraction.md).

**Be sure to select coap13 - selection button in upper right of Copper**

# Observe a resource using CoAP #
One big advantage of CoAP is the support for asynchronous communication. Use the `OBSERVE` protocol verb to subscribe to a resource, like a temperature sensor or brightness actuator.

**Request:**
```
COAP OBSERVE coap://localhost/VirtualDevices/virtualBrightnessActuator/value
```

Issue some `PUT` requests in parallel, e.g. using a [HTTP client](HTTPinteraction.md).

**Be sure to select coap13 - selection button in upper right of Copper**
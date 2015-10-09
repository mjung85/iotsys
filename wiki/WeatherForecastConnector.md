


# Introduction #
Predictive behavior can increase the efficiency and accommodation of smart homes and buildings. The knowledge of future weather data is a key factor enabling predictive behavior and adding new features like severe weather warning.

# Using the weather forecast connector #
One or many weather forecast connectors can be configured within the `devices.xml` configuration file.

A weather forecast connector consists of a configuration section and zero or more configured devices.

## Configuration section ##

The configuration section describes the connector's basic properties, i.e., its name as well as a flag indicating whether or not the connector's devices should be loaded by the corresponding device loader on startup.

```
<weather-forecast>
   <connector>
      <name>myWeatherForecastConnector</name>
      <enabled>true</enabled>
      <device>
        ...
      </device>
   </connector>
</weather-forecast>
```

## Device configuration ##

For the initialization of an oBIX weather forecast crawler object, the type of the class implementing the according weather service has to be specified. The specified class is responsible for generating the service URL and parsing the service response. Since weather services differ in the way how data is requested and represented, the weather forecast connector simply takes a service URL as input and returns an XML document.

The `name` field contains the long name of the oBIX weather forecast crawler object whereas the `href` field specifies the relative path that is used to uniquely identify the oBIX object. `location` specifies the location weather data is requested for. It contains a `description`, `latitude` and `longitude` in decimal degrees as well as the `height` in meters above sea level. Finally, `refreshEnable` allows automatic, periodic refreshing of the oBIX weather forecast crawler object.

```
<device>
   <type>at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.devices.WeatherForecastCrawlerYR_NO</type>
   <name>yr.no weather service</name>
   <href>yr.no</href>
   <location>
      <description>Vienna</description>
      <latitude>48.21</latitude>
      <longitude>16.37</longitude>
      <height>171</height>
   </location>
   <refreshEnabled>true</refreshEnabled>
</device>
```

See `devices.xml` for more examples.

# Creating a weather forecast crawler #

Independent from the specific weather service the following structure should be observed when creating a new weather forecast crawler.

```
import org.w3c.dom.Document;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl.WeatherForecastCrawlerImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl.WeatherForecastLocationImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl.WeatherForecastUpdateInImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast.WeatherForecastConnector;

public class WeatherForecastCrawlerXY extends WeatherForecastCrawlerImpl
{

   private static final String SERVICE_URL = "http://www.xy.com/weatherdata/?";

   public WeatherForecastCrawlerXY( String name, 
                                    WeatherForecastLocationImpl location, 
                                    WeatherForecastConnector connector) 
          throws FactoryConfigurationError, ParserConfigurationException
   {
      super(location, connector);
		
      setName(name);
   }
	
   @Override
   public void initialize()
   {
      super.initialize();
   }
	
   @Override
   public void refreshObject()
   {
      // Refresh weather data (periodically called if refreshEnabled == true)

      try 
      {
         Document doc;
         ArrayList<Obj> newData = new ArrayList<Obj>();
		
         // request data from weather service
         doc = connector.getWeatherForecastAsXML(this.getServiceURL());
		
         // parse response
         // ...			
			
         // update the forecast array with new data
         this.forecasts.update(new WeatherForecastUpdateInImpl(newData));
      }
      catch (Exception e)
      {
      }
   }
		
   @Override
   public String getServiceURL()
   {
      // generate service URL using the crawler's location
      return SERVICE_URL + 
                "lat=" + this.location.getLatitude() + 
                ";lon=" + this.location.getLongitude() + 
                ";msl=" + this.location.getHeight();
   }
}

```

Note that the weather forecast crawler's `href` has to be set after object creation, i.e., within the weather forecast connector's device loader (see [connectorhowto](connectorhowto.md) for more information about device loaders).


# Using WeatherForecast services #

## Query the history of an WeatherForecast object ##


### Filter the history ###
By using the query operation a filter can be performed.

**Request:**
```
HTTP POST http://localhost:8080/weather-forecast/yr.no/forecasts/query
```

```
<obj is="iot:WeatherForecastFilter"> 
  <int name="limit" val="2"/>
  <abstime name="start" href="start" val="2013-11-25T12:00:00.000Z" tz="UTC"/>
  <abstime name="end" href="end" val="2013-12-05T00:00:00.000Z" tz="UTC"/>>
</obj>
```

**Response:**
```
<obj is="iot:WeatherForecastQueryResult">
  <int name="count" val="2"/>
  <abstime name="start" val="2013-11-25T12:00:00.000Z" tz="UTC"/>
  <abstime name="end" val="2013-11-25T15:00:00.000Z" tz="UTC"/>
  <list name="data" of="iot:WeatherForecastRecord">
    <obj is="iot:WeatherForecastRecord">
      <abstime name="timestamp" val="2013-11-25T12:00:00.000Z" tz="UTC"/>
      <enum name="probabilityCode" val="unknown" range="iot:probabilityCode"/>
      <real name="temperature" val="3.0" unit="obix:units/celsius"/>
      <real name="humidity" val="49.3" unit="obix:units/percent"/>
      <real name="pressure" val="1019.4" unit="obix:units/hectopascal"/>
      <real name="precipitation" val="0.2" unit="obix:units/millimeter"/>
      <real name="cloudiness" val="78.1" unit="obix:units/percent"/>
      <real name="fog" val="0.0" unit="obix:units/percent"/>
      <int name="windSpeed" val="4" unit="obix:units/beaufort"/>
      <enum name="symbol" val="partly cloudy" range="iot:weatherSymbol"/>
    </obj>
    <obj is="iot:WeatherForecastRecord">
      <abstime name="timestamp" val="2013-11-25T15:00:00.000Z" tz="UTC"/>
      <enum name="probabilityCode" val="unknown" range="iot:probabilityCode"/>
      <real name="temperature" val="2.0" unit="obix:units/celsius"/>
      <real name="humidity" val="49.2" unit="obix:units/percent"/>
      <real name="pressure" val="1020.6" unit="obix:units/hectopascal"/>
      <real name="precipitation" val="0.0" unit="obix:units/millimeter"/>
      <real name="cloudiness" val="3.9" unit="obix:units/percent"/>
      <real name="fog" val="0.0" unit="obix:units/percent"/>
      <int name="windSpeed" val="4" unit="obix:units/beaufort"/>
      <enum name="symbol" val="sun" range="iot:weatherSymbol"/>
    </obj>
  </list>
</obj>
```

# Useful #
http://api.yr.no/#english
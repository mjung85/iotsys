
import at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast.WeatherForecastConnector;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;
import java.io.IOException;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;


public class TestApp {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try
		{
			WeatherForecastConnector con = new WeatherForecastConnector();
			Document doc = con.getWeatherForecastAsXML("http://api.yr.no/weatherapi/locationforecast/1.8/?lat=60.10;lon=9.58");

			// Hashtable<String, WeatherForecastRecordImpl>
			Hashtable<String, String> dataTable = new Hashtable<String, String>();
			// WeatherForecastRecordImpl
			String forecast;
			
			if (doc != null)
			{
				NodeList elements = doc.getElementsByTagName("location");
				
				for (int i=0; i < elements.getLength(); i++)
				{
					Element location = (Element) elements.item(i);
					
					if (location != null)
					{
						NodeList tmp;
						Element time = (Element) location.getParentNode();

						String from = time.getAttribute("from");
						String to = time.getAttribute("to");

						try
						{
							DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
							Date dFrom = f.parse(from.replaceAll("Z", "+02:00"));

							// get timezone
							String s = to.replaceAll("Z", "+02:00"); 
							// strip date
							s = s.substring(time.getAttribute("to").indexOf('T'));

							// strip time
							s = s.substring(s.lastIndexOf('+') + s.lastIndexOf('-') + 1);
							
							System.out.println(TimeZone.getTimeZone("GMT" + s));
							
							System.out.println(dFrom.toString());
						}
						catch (ParseException e)
						{
							System.out.println(e.getMessage());
						}
						
						if ((forecast = dataTable.get(to)) == null)
						{
							// WeatherForecastRecordImpl
							forecast = new String("");
							
							
						}

						if (from.equals(to))
						{
							//
							
							// temperature
							tmp = location.getElementsByTagName("temperature");
							
							if (tmp.getLength() >= 1)
							{
								System.out.println("temperature: " + ((Element) tmp.item(0)).getAttribute("value"));
							}
							else
							{
								// TODO
							}
							
							// wind speed
							tmp = location.getElementsByTagName("windSpeed");
							
							if (tmp.getLength() >= 1)
							{
								System.out.println("wind speed: " + ((Element) tmp.item(0)).getAttribute("beaufort"));
							}
							else
							{
								// TODO
							}
							
							// humidity
							tmp = location.getElementsByTagName("humidity");
							
							if (tmp.getLength() >= 1)
							{
								System.out.println("humidity: " + ((Element) tmp.item(0)).getAttribute("value"));
							}
							else
							{
								// TODO
							}
							
							// pressure
							tmp = location.getElementsByTagName("pressure");
							
							if (tmp.getLength() >= 1)
							{
								System.out.println("pressure: " + ((Element) tmp.item(0)).getAttribute("value"));
							}
							else
							{
								// TODO
							}
							
							// cloudiness
							tmp = location.getElementsByTagName("cloudiness");
							
							if (tmp.getLength() >= 1)
							{
								System.out.println("cloudiness: " + ((Element) tmp.item(0)).getAttribute("percent"));
							}
							else
							{
								// TODO
							}
							
							// fog
							tmp = location.getElementsByTagName("fog");
							
							if (tmp.getLength() >= 1)
							{
								System.out.println("fog: " + ((Element) tmp.item(0)).getAttribute("percent"));
							}
							else
							{
								// TODO
							}
						}
						else
						{
							//
							
							// precipitation
							tmp = location.getElementsByTagName("precipitation");
							
							if (tmp.getLength() >= 1)
							{
								System.out.println("precipitation: " + ((Element) tmp.item(0)).getAttribute("value"));
							}
							else
							{
								// TODO
							}
							
							// humidity
							tmp = location.getElementsByTagName("symbol");
							
							if (tmp.getLength() >= 1)
							{
								System.out.println("symbol: " + ((Element) tmp.item(0)).getAttribute("number"));
							}
							else
							{
								// TODO
							}
						}
					}
				}
			}				
		}
		catch(MalformedURLException murle)
		{
			System.out.println("Malformed URL!");
		}
		catch(IOException ioe)
		{
			System.out.println("Could not connect to weather service!");
		}
		catch (ParserConfigurationException parcone)
		{
			System.out.println("Could not create Parser!");
		}
		catch (SAXException saxe)
		{
			System.out.println("Could not parse response!");
		}
	}

}

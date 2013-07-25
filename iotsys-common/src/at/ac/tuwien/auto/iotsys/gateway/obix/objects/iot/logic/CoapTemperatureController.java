package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.logic;

import obix.Str;

public interface CoapTemperatureController extends TemperatureController {
	public static final String CONTRACT = "iot:coapTemperatureController";
	
	public static final String tempHref = "<str name='tempIPv6' href='tempIPv6' val=''/>";
	public Str tempHref();
}

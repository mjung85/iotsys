/*******************************************************************************
 * Copyright (c) 2013
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/

package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.impl;

import java.util.logging.Logger;

import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.ObjectBrokerHelper;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.WeatherController;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.UpcomingWeather;
import at.ac.tuwien.auto.iotsys.obix.observer.Observer;
import at.ac.tuwien.auto.iotsys.obix.observer.Subject;

public class WeatherControllerImpl extends Obj implements WeatherController {
	private static final Logger log = Logger.getLogger(WeatherControllerImpl.class.getName());

	private ObjectBroker objectBroker;
	protected Bool enabled = new Bool(false);
	protected Real roomTempSetPoint = new Real();
	protected Real roomCurrentTemp = new Real();
	protected Real tolerance = new Real();
	private UpcomingWeather upcomingWeather;
	private boolean observersAttached = false;
	private long curWindSpeed = 0;
	private boolean windowOpen = false;

	private final String UPCOMING_LINK = "weather-forecast/vienna/upcoming";
	
	private final String WARNING_LINK = "networks/siemens_koffer_iotsys/entities/text_display_up_587_01/1/datapoints/function_2_warning_message/value";
	private final String ALARM_LINK = "networks/siemens_koffer_iotsys/entities/text_display_up_587_01/1/datapoints/function_3_alarm_message/value";
	private final String LINK_WINDOW_OPEN = "/EnOcean/window/value";
	
	public WeatherControllerImpl() {
		objectBroker = ObjectBrokerHelper.getInstance();

		setIs(new Contract(RoomTemperatureControlSimulationImpl.CONTRACT));

		enabled.setName("enabled");
		enabled.setDisplayName("Enabled");
		enabled.setHref(new Uri("enabled"));
		enabled.setWritable(true);

		this.add(enabled);
	}

	@Override
	public void writeObject(Obj input) {
		
		String resourceUriPath = "";
		if (input.getHref() == null) {
			resourceUriPath = input.getInvokedHref().substring(
					input.getInvokedHref().lastIndexOf('/') + 1);
		} else {
			resourceUriPath = input.getHref().get();

		}
		
		if (input instanceof Bool) {

			if ("enabled".equals(resourceUriPath)) {
				enabled.set(((Bool) input).get());
				
				if(enabled.get() && !observersAttached){
					attachObservers();
				}
			}
		}
		doControl();
	}

	private void attachObservers() {
		Obj obj = objectBroker.pullObj(new Uri(UPCOMING_LINK), false);

		if (obj instanceof UpcomingWeather) {
			upcomingWeather = (UpcomingWeather) obj;

			upcomingWeather.windspeed().attach(new Observer() {

				@Override
				public void update(Object state) {

					System.out.println("windSpeed Update occur");
					// TODO Auto-generated method stub
					if (state instanceof Int) {
						curWindSpeed = ((Int) state).get();
						doControl();
					}

				}

				@Override
				public void setSubject(Subject object) {
					// TODO Auto-generated method stub

				}

				@Override
				public Subject getSubject() {
					// TODO Auto-generated method stub
					return null;
				}

			});

		}
		
		objectBroker.pullObj(new Uri(LINK_WINDOW_OPEN), false).attach(new Observer() {

				@Override
				public void update(Object state) {
					// TODO Auto-generated method stub
					if (state instanceof Bool) {
						windowOpen = ((Bool) state).get();
						doControl();
					}

				}

				@Override
				public void setSubject(Subject object) {
					// TODO Auto-generated method stub

				}

				@Override
				public Subject getSubject() {
					// TODO Auto-generated method stub
					return null;
				}

			});
		
	}

	private void doControl() {
	
		if (enabled.get()) {
			log.info("Weather controller checking!");
			if(curWindSpeed > 7 && !windowOpen){
				// make warning
				ObjectBrokerHelper.getInstance().pullObj(new Uri(WARNING_LINK), false).writeObject(new Bool(true));
			}
			if(curWindSpeed > 7 && windowOpen){
				// make alarm
				ObjectBrokerHelper.getInstance().pullObj(new Uri(ALARM_LINK), false).writeObject(new Bool(true));
			}
		} 
	}

	@Override
	public Bool enabled() {
		return enabled;
	}
}

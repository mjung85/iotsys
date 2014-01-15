package at.ac.tuwien.auto.iotsys.gateway.connectors.virtual.simulation;

import java.util.logging.Logger;

import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Str;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.ObjectBrokerHelper;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sim.HVACSimulationSuitcase;
import at.ac.tuwien.auto.iotsys.obix.observer.Observer;
import at.ac.tuwien.auto.iotsys.obix.observer.Subject;

public class HVACSimulationSuitcaseImpl extends Obj implements
		HVACSimulationSuitcase {

	private final String LINK_OUTSIDE_TEMP = "BACnetIoTSuitcase/2098177/AnalogInput2/value";
	private final String LINK_WINDOW_CLOSED = "/EnOcean/window/value";
	private final String LINK_STANDBY_MODE_ACTIVE = "/networks/siemens_koffer_iotsys/entities/fcu_operator_panel_office_up_237e_delta_i_system/1/datapoints/standby_mode/value";
	private final String LINK_COMFORT_MODE_ACTIVE = "/networks/siemens_koffer_iotsys/entities/fcu_operator_panel_office_up_237e_delta_i_system/1/datapoints/status_of_comfort_mode/value";
	private final String LINK_DOOR_OPENER_ACTIVE = "/networks/siemens_koffer_iotsys/entities/fcu_operator_panel_office_up_237e_delta_i_system/1/datapoints/status_of_comfort_mode/value";
	private final String LINK_TEMP_OUTSIDE_OFFSET = "/networks/siemens_koffer_iotsys/views/functional/groups/koffer/groups/sensor/groups/sollwertverschiebung/value";
	protected Str season = new Str("winter");
	
	public final static String HEATER_LINK = "BACnetIoTSuitcase/2098177/AnalogOutput12/value";
	public final static String AIR_IN_LINK = "BACnetIoTSuitcase/2098177/AnalogOutput11/value";
	public final static String COOLER_LINK = "BACnetIoTSuitcase/2098177/AnalogOutput13/value";
	public final static String AIR_OUT_LINK = "BACnetIoTSuitcase/2098177/AnalogOutput10/value";
	public final static String AIR_IN_VALVE_LINK = "BACnetIoTSuitcase/2098177/AnalogOutput9/value";
	public final static String AIR_OUT_VALVE_LINK = "BACnetIoTSuitcase/2098177/AnalogOutput8/value";
	public final static String HEATING_STATUS_LINK = "networks/siemens_koffer_iotsys/entities/fcu_operator_panel_office_up_237e_delta_i_system/1/datapoints/status_heating___cooling_mode/value";
	
	public static HVACSimulationSuitcaseImpl instance = null;
	private static final Logger log = Logger
			.getLogger(HVACSimulationSuitcaseImpl.class.getName());

	private ObjectBroker objectBroker;
	
	protected Bool enabled = new Bool(false);
	private Real temp = new Real(18);
	// // if no cooling or heating happens the season
	// // depicts the impact on the temperature
	// private SEASON season = SEASON.SUMMER;
	// change of degree celsius per TIME_INTERVALL_MS depending on season
	protected Real summerImpact = new Real(0.1); // raise 0.05 degree
	protected Real springImpact = new Real(summerImpact.get() / 2);
	protected Real winterImpact = new Real(-summerImpact.get());
	protected Real fallImpact = new Real(winterImpact.get() / 2);
	protected Real heatingImpact = new Real(summerImpact.get() * 2);
	protected Real coolingImpact = new Real(winterImpact.get() * 2);
	protected Bool coolerActive = new Bool(false);
	protected Bool boilerActive = new Bool(false);
	protected Bool fanInActive = new Bool(false);
	protected Bool fanOutActive = new Bool(false);
	protected Int valveInPosition = new Int(0);
	protected Int valveOutPosition = new Int(0);
	protected Bool windowClosed = new Bool(false);
	protected Real tempOutside = new Real();
	protected Real tempOutsideOffset = new Real();
	protected Bool comfortModeActive = new Bool(false);
	protected Bool standbyModeActive = new Bool(false);
	protected Bool doorOpenerActive = new Bool(false);
	private boolean observersRegistered = false;
	public static final int TIME_INTERVALL_MS = 2000;
	private volatile boolean threadStarted = false;
	private SimSuitcaseThread simThread;

	public HVACSimulationSuitcaseImpl() {

		objectBroker = ObjectBrokerHelper.getInstance();

		setIs(new Contract(HVACSimulationSuitcaseImpl.CONTRACT));
		instance = this;

		this.enabled.setName("enabled");
		this.enabled.setHref(new Uri("enabled"));
		this.enabled.setWritable(true);
		this.add(enabled);

		this.boilerActive.setName("boilerActive");
		this.boilerActive.setHref(new Uri("boilerActive"));
		this.add(boilerActive);

		this.coolerActive.setName("coolerActive");
		this.coolerActive.setHref(new Uri("coolerActive"));
		this.add(coolerActive);

		this.doorOpenerActive.setName("doorOpenerActive");
		this.doorOpenerActive.setHref(new Uri("doorOpenerActive"));
		this.add(doorOpenerActive);

		this.fanInActive.setName("fanInActive");
		this.fanInActive.setHref(new Uri("fanInActive"));
		this.add(fanInActive);

		this.fanOutActive.setName("fanOutActive");
		this.fanOutActive.setHref(new Uri("fanOutActive"));
		this.add(fanOutActive);

		this.coolingImpact.setName("coolingImpact");
		this.coolingImpact.setHref(new Uri("coolingImpact"));
		this.add(coolingImpact);

		this.fallImpact.setName("fallImpact");
		this.fallImpact.setHref(new Uri("fallImpact"));
		this.add(fallImpact);

		this.heatingImpact.setName("heatingImpact");
		this.heatingImpact.setHref(new Uri("heatingImpact"));
		this.add(heatingImpact);

		this.season.setName("season");
		this.season.setHref(new Uri("season"));
		this.add(season);

		this.springImpact.setName("springImpact");
		this.springImpact.setHref(new Uri("springImpact"));
		this.add(springImpact);

		this.summerImpact.setName("summerImpact");
		this.summerImpact.setHref(new Uri("summerImpact"));
		this.add(summerImpact);

		this.temp.setName("temp");
		this.temp.setHref(new Uri("temp"));
		this.add(temp);

		this.winterImpact.setName("winterImpact");
		this.winterImpact.setHref(new Uri("winterImpact"));
		this.add(winterImpact);

		this.valveInPosition.setName("valveInPosition");
		this.valveInPosition.setHref(new Uri("valveInPosition"));
		this.add(valveInPosition);

		this.valveOutPosition.setName("valveOutPosition");
		this.valveOutPosition.setHref(new Uri("valveOutPosition"));
		this.add(valveOutPosition);

		this.windowClosed.setName("windowClosed");
		this.windowClosed.setHref(new Uri("windowClosed"));
		this.add(windowClosed);

		this.tempOutside.setName("tempOutside");
		this.tempOutside.setHref(new Uri("tempOutside"));
		this.add(tempOutside);

		this.tempOutsideOffset.setName("tempOutsideOffset");
		this.tempOutsideOffset.setHref(new Uri("tempOutsideOffset"));
		this.add(tempOutsideOffset);
		
		this.comfortModeActive.setName("comfortModeActive");
		this.comfortModeActive.setHref(new Uri("comfortModeActive"));
		this.add(comfortModeActive);

		this.standbyModeActive.setName("standbyModeActive");
		this.standbyModeActive.setHref(new Uri("standbyModeActive"));
		this.add(standbyModeActive);

	}

	public double getHeatingImpact() {
		return heatingImpact.get();
	}

	public void setHeatingImpact(double heatingImpact) {
		this.heatingImpact.set(heatingImpact);
	}

	public double getCoolingImpact() {
		return coolingImpact.get();
	}

	public void setCoolingImpact(double coolingImpact) {
		this.coolingImpact.set(coolingImpact);
	}

	public double getTemp() {
		return temp.get();
	}

	public void setTemp(double temp) {
		this.temp.set(temp);
	}

	public String getSeason() {
		return season.get();
	}

	public void setSeason(String season) {
		this.season.set(season);
	}

	public double getSummerImpact() {
		return summerImpact.get();
	}

	public void setSummerImpact(double summerImpact) {
		this.summerImpact.set(summerImpact);
	}

	public double getSpringImpact() {
		return springImpact.get();
	}

	public void setSpringImpact(double springImpact) {
		this.springImpact.set(springImpact);
	}

	public double getWinterImpact() {
		return winterImpact.get();
	}

	public void setWinterImpact(double winterImpact) {
		this.winterImpact.set(winterImpact);
	}

	public double getFallImpact() {
		return fallImpact.get();
	}

	public void setFallImpact(double fallImpact) {
		this.fallImpact.set(fallImpact);
	}

	public void increaseTemp(double d) {
		temp.set(temp.get() + d);
	}

	public void decreaseTemp(double d) {
		System.out.println("Temp vorher: " + temp.get());
		System.out.println("d: " + d);
		temp.set(temp.get() - Math.abs(d));
		System.out.println("Temp danach: " + temp.get());
	}

	public boolean isBoilerActive() {
		return boilerActive.get();
	}

	public void setBoilerActive(boolean boilerActive) {
		this.boilerActive.set(boilerActive);
	}

	public boolean isFanInActive() {
		return fanInActive.get();
	}

	public void setFanInActive(boolean fanInActive) {
		this.fanInActive.set(fanInActive);
	}

	public boolean isFanOutActive() {
		return fanOutActive.get();
	}

	public void setFanOutActive(boolean faOutActive) {
		this.fanOutActive.set(fanOutActive);
	}

	public boolean isCoolerActive() {
		return coolerActive.get();
	}

	public void setCoolerActive(boolean coolerActive) {
		this.coolerActive.set(coolerActive);
	}

	public void setcomfortModeActive(boolean comfortModeActive) {
		this.comfortModeActive.set(comfortModeActive);
	}

	public void setstandbyModeActive(boolean standbyModeActive) {
		this.standbyModeActive.set(standbyModeActive);
	}

	@Override
	public Bool enabled() {
		return enabled;
	}
	@Override
	public Str season() {
		return season;
	}
	@Override
	public Real springImpact() {
		return springImpact;
	}
	@Override
	public Real winterImpact() {
		return winterImpact;
	}
	@Override
	public Real fallImpact() {
		return fallImpact;
	}
	@Override
	public Real summerImpact() {
		return summerImpact;
	}
	@Override
	public Real heatingImpact() {
		return heatingImpact;
	}
	@Override
	public Real coolingImpact() {
		return coolingImpact;
	}
	@Override
	public Bool boilerActive() {
		return boilerActive;
	}
	@Override
	public Bool coolerActive() {
		return coolerActive;
	}
	@Override
	public Bool fanInActive() {
		return fanInActive;
	}
	@Override
	public Bool fanOutActive() {
		return fanOutActive;
	}
	@Override
	public Int valveInPosition() {
		return valveInPosition;
	}
	@Override
	public Int valveOutPosition() {
		return valveOutPosition;
	}
	@Override
	public Real temp() {
		return temp;
	}
	@Override
	public Real tempOutside() {
		return tempOutside;
	}
	@Override
	public Bool windowClosed() {
		return windowClosed;
	}
	@Override
	public Bool comfortModeActive() {
		return comfortModeActive;
	}
	@Override
	public Bool standbyModeActive() {
		return standbyModeActive;
	}
	@Override
	public Bool doorOpenerActive() {
		return doorOpenerActive;
	}
	@Override
	public Real tempOutsideOffset() {
		return tempOutsideOffset;
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

		if (input instanceof HVACSimulationSuitcase) {
			HVACSimulationSuitcase in = (HVACSimulationSuitcase) input;
			this.enabled.set(in.enabled().get());
			this.temp.set(in.temp().get());
			this.season.set(in.season().get());
			this.enabled.set(in.enabled().get());
			this.boilerActive.set(in.boilerActive().get());
			this.coolerActive.set(in.coolerActive().get());
			this.coolingImpact.set(in.coolingImpact().get());

			this.fallImpact.set(in.fallImpact().get());
			this.fanInActive.set(in.fanInActive().get());
			this.fanOutActive.set(in.fanOutActive().get());

			this.heatingImpact.set(in.heatingImpact().get());

			this.valveInPosition.set(in.valveInPosition().get());
			this.valveOutPosition.set(in.valveOutPosition().get());

			this.springImpact.set(in.springImpact().get());
			this.summerImpact.set(in.summerImpact().get());
			this.winterImpact.set(in.winterImpact().get());

		} else if (input instanceof Bool) {

			if ("enabled".equals(resourceUriPath)) {
				enabled.set(((Bool) input).get());
			} else if ("boilerActive".equals(resourceUriPath)) {
				boilerActive.set(((Bool) input).get());
			} else if ("coolerActive".equals(resourceUriPath)) {
				coolerActive.set(((Bool) input).get());
			} else if ("fanInActive".equals(resourceUriPath)) {
				fanInActive.set(((Bool) input).get());
			} else if ("fanOutActive".equals(resourceUriPath)) {
				fanOutActive.set(((Bool) input).get());
			}
		} else if (input instanceof Real) {
			if ("temp".equals(resourceUriPath)) {
				temp.set(((Real) input).get());
			} else if ("heatingImpact".equals(resourceUriPath)) {
				heatingImpact.set(((Real) input).get());
			} else if ("springImpact".equals(resourceUriPath)) {
				springImpact.set(((Real) input).get());
			} else if ("winterImpact".equals(resourceUriPath)) {
				winterImpact.set(((Real) input).get());
			} else if ("coolingImpact".equals(resourceUriPath)) {
				coolingImpact.set(((Real) input).get());
			} else if ("fallImpact".equals(resourceUriPath)) {
				fallImpact.set(((Real) input).get());
			}
		} else if (input instanceof Str) {
			this.season.set(((Str) input).get());
		} else if (input instanceof Int) {
			if ("valveInPosition".equals(resourceUriPath)) {
				valveInPosition.set(((Int) input).get());
			} else if ("valveOutPosition".equals(resourceUriPath)) {
				valveOutPosition.set(((Int) input).get());
			}
		}

		if (enabled().get() && !threadStarted) {
			synchronized (this) {
				simThread = new SimSuitcaseThread(this);

				if (!observersRegistered) {
					observersRegistered = true;
					registerObservers();
				}

				simThread.setDaemon(true);
				simThread.start();
				threadStarted = true;
			}
		} else if (!enabled().get() && threadStarted) {
			synchronized (this) {
				simThread.stopSim();
				threadStarted = false;
			}
		}
	}

	private void registerObservers() {
		ObjectBroker objectBroker = ObjectBrokerHelper.getInstance();
		Obj objOutsideTemp = objectBroker.pullObj(new Uri(LINK_OUTSIDE_TEMP),
				true);

		if (objOutsideTemp instanceof Real) {
			objOutsideTemp.attach(new Observer() {
				@Override
				public void update(Object state) {
					if (state instanceof Obj) {
						tempOutside.set(((Obj) state).getReal());
					}
				}
				@Override
				public void setSubject(Subject object) {
				}
				@Override
				public Subject getSubject() {
					return null;
				}

			});
		}

		Obj objwindowClosed = objectBroker.pullObj(new Uri(LINK_WINDOW_CLOSED),
				false);

		if (objwindowClosed instanceof Bool) {
			objwindowClosed.attach(new Observer() {
				@Override
				public void update(Object state) {
					if (state instanceof Obj) {
					//	System.out.println("Windows change ############");
						if(((Obj) state).getBool()){
							windowClosed.set(false);
						}else{
							windowClosed.set(true);
						}
					//	windowClosed.set(((Obj) state).getBool());
					}
				}

				@Override
				public void setSubject(Subject object) {
				}

				@Override
				public Subject getSubject() {
					return null;
				}
			});
		}

		Obj objStandbyModeActive = objectBroker.pullObj(new Uri(
				LINK_STANDBY_MODE_ACTIVE), false);
		if (objStandbyModeActive instanceof Bool) {
			objStandbyModeActive.attach(new Observer() {
				@Override
				public void update(Object state) {
					if (state instanceof Obj) {
						standbyModeActive.set(((Obj) state).getBool());
					}
				}

				@Override
				public void setSubject(Subject object) {
				}

				@Override
				public Subject getSubject() {
					return null;
				}
			});
		}

		Obj objComfortModeActive = objectBroker.pullObj(new Uri(
				LINK_COMFORT_MODE_ACTIVE), false);
		if (objComfortModeActive instanceof Bool) {
			objComfortModeActive.attach(new Observer() {
				@Override
				public void update(Object state) {
					if (state instanceof Obj) {
						comfortModeActive.set(((Obj) state).getBool());
					}
				}

				@Override
				public void setSubject(Subject object) {
				}

				@Override
				public Subject getSubject() {
					return null;
				}
			});
		}

		Obj objDoorOpenerActive = objectBroker.pullObj(new Uri(
				LINK_DOOR_OPENER_ACTIVE), false);
		if (objDoorOpenerActive instanceof Bool) {
			objDoorOpenerActive.attach(new Observer() {
				@Override
				public void update(Object state) {
					if (state instanceof Obj) {
						doorOpenerActive.set(((Obj) state).getBool());
					}
				}

				@Override
				public void setSubject(Subject object) {
				}

				@Override
				public Subject getSubject() {
					return null;
				}
			});
		}
		
		Obj objTempOutsideOffset = objectBroker.pullObj(new Uri(
				LINK_TEMP_OUTSIDE_OFFSET), false);
		if (objTempOutsideOffset instanceof Real) {
			objTempOutsideOffset.attach(new Observer() {
				@Override
				public void update(Object state) {
					if (state instanceof Obj) {
						tempOutsideOffset.set(((Obj) state).getReal());
					}
				}
				@Override
				public void setSubject(Subject object) {
				}
				@Override
				public Subject getSubject() {
					return null;
				}
			});
		}
		

	}

	
}

class SimSuitcaseThread extends Thread {
	private HVACSimulationSuitcaseImpl hvacSimulation = null;
	private static final Logger log = Logger.getLogger(SimSuitcaseThread.class
			.getName());

	private volatile boolean stopped = false;

	public SimSuitcaseThread(HVACSimulationSuitcaseImpl hvacSimulation) {
		this.hvacSimulation = hvacSimulation;
	}

	@Override
	public void run() {
		while (!stopped) {
			try {
				double impact = 0;
				if (hvacSimulation.getSeason().equals("summer")) {
					if (hvacSimulation.getTemp() < 40)
						impact = hvacSimulation.getSummerImpact();
				} else if (hvacSimulation.getSeason().equals("winter")) {
					if (hvacSimulation.getTemp() > -10)
						impact = hvacSimulation.getWinterImpact();
				} else if (hvacSimulation.getSeason().equals("spring")) {
					if (hvacSimulation.getTemp() < 30)
						impact = hvacSimulation.getSpringImpact();
				} else if (hvacSimulation.getSeason().equals("fall")) {
					if (hvacSimulation.getTemp() > 10)
						impact = hvacSimulation.getFallImpact();
				}

				hvacSimulation.increaseTemp(impact);
				log.finest("HVAC simulation temp is now: "
						+ hvacSimulation.getTemp());
				Thread.sleep(HVACSimulationImpl.TIME_INTERVALL_MS * 2);

				// System.out.println("isBoilerActive"+hvacSimulation.isBoilerActive());
				// System.out.println("isFanInActive"+hvacSimulation.isFanInActive());

				if (hvacSimulation.isBoilerActive()
						&& hvacSimulation.isFanInActive()) {
					System.out.println("increaseTemp");
					hvacSimulation.increaseTemp(hvacSimulation
							.getHeatingImpact());
					log.finest("HVAC simulation temp is now (after heating): "
							+ hvacSimulation.getTemp());
				}

				// System.out.println("isCoolerActive"+hvacSimulation.isCoolerActive());
				// System.out.println("isFanOutActive"+hvacSimulation.isFanOutActive());

				if (hvacSimulation.isCoolerActive()
						&& hvacSimulation.isFanOutActive()) {
					System.out.println("decreaseTemp");
					log.finest("HVAC simulation temp is now (after cooling): "
							+ hvacSimulation.getTemp());
					hvacSimulation.decreaseTemp(hvacSimulation
							.getCoolingImpact());
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopSim() {
		stopped = true;
		ObjectBrokerHelper.getInstance().pullObj(new Uri(HVACSimulationSuitcaseImpl.HEATER_LINK), false).writeObject(new Real(0));
		ObjectBrokerHelper.getInstance().pullObj(new Uri(HVACSimulationSuitcaseImpl.COOLER_LINK), false).writeObject(new Real(0));
		ObjectBrokerHelper.getInstance().pullObj(new Uri(HVACSimulationSuitcaseImpl.AIR_IN_LINK), false).writeObject(new Real(0));
		ObjectBrokerHelper.getInstance().pullObj(new Uri(HVACSimulationSuitcaseImpl.AIR_OUT_VALVE_LINK), false).writeObject(new Real(0));
		ObjectBrokerHelper.getInstance().pullObj(new Uri(HVACSimulationSuitcaseImpl.AIR_IN_VALVE_LINK), false).writeObject(new Real(0));
		ObjectBrokerHelper.getInstance().pullObj(new Uri(HVACSimulationSuitcaseImpl.AIR_OUT_VALVE_LINK), false).writeObject(new Real(0));
		ObjectBrokerHelper.getInstance().pullObj(new Uri(HVACSimulationSuitcaseImpl.HEATING_STATUS_LINK), false).writeObject(new Real(0));
	}

}

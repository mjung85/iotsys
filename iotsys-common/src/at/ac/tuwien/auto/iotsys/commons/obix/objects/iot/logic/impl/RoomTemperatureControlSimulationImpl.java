package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.impl;

import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.ObjectBrokerHelper;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.TemperatureControlActuator;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl.HVACvalveActuatorImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.RoomTemperatureControlSimulation;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sim.HVACSimulationSuitcase;
import at.ac.tuwien.auto.iotsys.obix.observer.Observer;
import at.ac.tuwien.auto.iotsys.obix.observer.Subject;
import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Uri;

public class RoomTemperatureControlSimulationImpl extends Obj implements RoomTemperatureControlSimulation{
	
	private ObjectBroker objectBroker;
	private ControllerState controllerState = ControllerState.INACTIVE;
	protected Bool enabled = new Bool(false);
	protected Real roomTempSetPoint = new Real();
	protected Real roomCurrentTemp = new Real();
	protected Real tolerance = new Real();
	protected Real tempOutsideOffset = new Real();
	protected Bool windowClosed = new Bool(false);
	protected Bool comfortModeActive = new Bool(true);
	protected Bool standbyModeActive = new Bool(false);
	private HVACSimulationSuitcase hvacSim;
	
	private final String HEATER_LINK = "BACnetIoTSuitcase/2098177/AnalogOutput12/value";
	private final String AIR_IN_LINK = "BACnetIoTSuitcase/2098177/AnalogOutput11/value";
	private final String COOLER_LINK = "BACnetIoTSuitcase/2098177/AnalogOutput13/value";
	private final String AIR_OUT_LINK = "BACnetIoTSuitcase/2098177/AnalogOutput10/value";
	private final String AIR_IN_VALVE_LINK = "BACnetIoTSuitcase/2098177/AnalogOutput9/value";
	private final String AIR_OUT_VALVE_LINK = "BACnetIoTSuitcase/2098177/AnalogOutput8/value";
	
	private final String LINK_WINDOW_CLOSED = "/EnOcean/window/value";
	private final String HEATING_STATUS_LINK = "networks/siemens_koffer_iotsys/entities/fcu_operator_panel_office_up_237e_delta_i_system/1/datapoints/status_heating___cooling_mode/value";
	private final String LINK_STANDBY_MODE_ACTIVE = "/networks/siemens_koffer_iotsys/entities/fcu_operator_panel_office_up_237e_delta_i_system/1/datapoints/standby_mode/value";
	private final String LINK_COMFORT_MODE_ACTIVE = "/networks/siemens_koffer_iotsys/entities/fcu_operator_panel_office_up_237e_delta_i_system/1/datapoints/status_of_comfort_mode/value";
	private final String LINK_TEMP_OUTSIDE_OFFSET = "/networks/siemens_koffer_iotsys/views/functional/groups/koffer/groups/sensor/groups/sollwertverschiebung/value";
	
	public RoomTemperatureControlSimulationImpl(){
		
		objectBroker= ObjectBrokerHelper.getInstance();

		setIs(new Contract(RoomTemperatureControlSimulationImpl.CONTRACT));
	
		enabled.setName("enabled");
		enabled.setDisplayName("Enabled");
		enabled.setHref(new Uri("enabled"));
		enabled.setWritable(true);

		roomTempSetPoint.setName("roomTempSetPoint");
		roomTempSetPoint.setDisplayName("Set point");
		roomTempSetPoint.setHref(new Uri("roomTempSetPoint"));
		roomTempSetPoint.setUnit(new Uri("obix:units/celsius"));
		roomTempSetPoint.setWritable(true);
		
		roomCurrentTemp.setName("currentTemp");
		roomCurrentTemp.setDisplayName("currentTemp");
		roomCurrentTemp.setHref(new Uri("currentTemp"));
		roomCurrentTemp.setUnit(new Uri("obix:units/celsius"));
		roomCurrentTemp.setWritable(false);
		
		tolerance.setName("tolerance");
		tolerance.setDisplayName("Tolerance");
		tolerance.setHref(new Uri("tolerance"));
		tolerance.setUnit(new Uri("obix:units/celsius"));
		tolerance.setWritable(true);
		
		tempOutsideOffset.setName("tempOutsideOffset");
		tempOutsideOffset.setDisplayName("tempOutsideOffset");
		tempOutsideOffset.setHref(new Uri("tempOutsideOffset"));
		tempOutsideOffset.setUnit(new Uri("obix:units/celsius"));
		tempOutsideOffset.setWritable(false);
		
		windowClosed.setName("windowClosed");
		windowClosed.setDisplayName("windowClosed");
		windowClosed.setHref(new Uri("windowClosed"));
		windowClosed.setWritable(false);
		
		comfortModeActive.setName("comfortModeActive");
		comfortModeActive.setDisplayName("comfortModeActive");
		comfortModeActive.setHref(new Uri("comfortModeActive"));
		comfortModeActive.setWritable(false);
		
		standbyModeActive.setName("standbyModeActive");
		standbyModeActive.setDisplayName("standbyModeActive");
		standbyModeActive.setHref(new Uri("standbyModeActive"));
		standbyModeActive.setWritable(false);
		
		this.add(enabled);
		this.add(roomCurrentTemp);
		this.add(roomTempSetPoint);
		this.add(tolerance);
		this.add(tempOutsideOffset);
		this.add(windowClosed);
		this.add(comfortModeActive);
		this.add(standbyModeActive);
		
		Obj obj = objectBroker.pullObj(new Uri("/HVAC+Simulation/hvacSimSuitcase"), false);
		
		//System.out.println("havc Temperature+++++++++++++++++++++++");
		
	if (obj instanceof HVACSimulationSuitcase)
	{
			hvacSim = (HVACSimulationSuitcase) obj;

			//System.out.println("Current Temp: " + hvacSim.temp());
			roomCurrentTemp.set(hvacSim.temp());
			
			hvacSim.temp().attach(new Observer(){

				@Override
				public void update(Object state) {
					// TODO Auto-generated method stub
					if(state instanceof Obj){
						doControl();
						roomCurrentTemp.set(((Obj) state).getReal());
						System.out.println("update occur");
					
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
		if (input instanceof RoomTemperatureControlSimulation) {
			RoomTemperatureControlSimulation in = (RoomTemperatureControlSimulation) input;
			this.enabled.set(in.enabled().get());
			this.roomTempSetPoint.set(in.roomTempSetPoint().get());

		} else if (input instanceof Real) {
			if ("roomTempSetPoint".equals(resourceUriPath)) {
				System.out.println("set roomTempSetPoint");
				this.roomTempSetPoint.set(((Real) input).get());
			} else if ("tolerance".equals(resourceUriPath)) {
					tolerance.set(((Real) input).get());
			}
//			} else if ("roomTempSetPoint".equals(resourceUriPath)) {
//				roomTempSetPoint.set(((Real) input).get());
 

		} else if (input instanceof Bool) {

			if ("enabled".equals(resourceUriPath)) {
				enabled.set(((Bool) input).get());
			} 
//			else if ("saveEnergyFactor".equals(resourceUriPath)) {
//				saveEnergyFactor.set(((Bool) input).get());
//			}

		} //else if (input instanceof Int) {

//			if ("setpoint".equals(resourceUriPath)) {
//				setpoint.set(((Int) input).get());
//			} else if ("temperature".equals(resourceUriPath)) {
//				temperature.set(((Bool) input).get());
//			} 

		//}
		registerObservers();
		doControl();
	}
	
	private void registerObservers() {
		ObjectBroker objectBroker = ObjectBrokerHelper.getInstance();

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
		
		Obj objWindowClosed = objectBroker.pullObj(new Uri(
				LINK_WINDOW_CLOSED), false);
		if (objWindowClosed instanceof Bool) {
			objWindowClosed.attach(new Observer() {
				@Override
				public void update(Object state) {
					if (state instanceof Obj) {
						if(((Obj) state).getBool()){
							windowClosed.set(false);
						}else{
							windowClosed.set(true);
						}
						//windowClosed.set(((Obj) state).getBool());
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
	
	private void doControl() {
		if (enabled.get()) {
			System.out.println("enabled");
			System.out.println(roomTempSetPoint);
			System.out.println(tolerance);
			
		
			
			if (roomCurrentTemp.get() < roomTempSetPoint.get() - tolerance.get() + tempOutsideOffset.get()
					&& controllerState != ControllerState.HEATING && windowClosed.get()==false) {
					// we need to heat!
					controllerState = ControllerState.HEATING;
					hvacSim.coolerActive().setBool(false);
				
					hvacSim.boilerActive().setBool(true);
					hvacSim.fanOutActive().setBool(false);
					hvacSim.fanInActive().setBool(true);
					hvacSim.valveInPosition().setInt(100);
					hvacSim.valveOutPosition().setInt(50);
					//hvacSim.heatPumpActive().setBool(true);
					ObjectBrokerHelper.getInstance().pullObj(new Uri(LINK_STANDBY_MODE_ACTIVE), false).writeObject(new Bool(false));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(LINK_COMFORT_MODE_ACTIVE), false).writeObject(new Bool(true));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(HEATER_LINK), false).writeObject(new Real(100));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(COOLER_LINK), false).writeObject(new Real(0));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_IN_LINK), false).writeObject(new Real(100));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_OUT_LINK), false).writeObject(new Real(100));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_IN_VALVE_LINK), false).writeObject(new Real(100));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_OUT_VALVE_LINK), false).writeObject(new Real(100));				
					ObjectBrokerHelper.getInstance().pullObj(new Uri(HEATING_STATUS_LINK), false).writeObject(new Real(100));					
					
				//controlValue.set(100);
			} else if (roomCurrentTemp.get() > roomTempSetPoint.get() + tolerance.get() + tempOutsideOffset.get()
					&& controllerState != ControllerState.COOLING && windowClosed.get()==false) {
					//controlValue.set(-100);
					hvacSim.coolerActive().setBool(true);
					hvacSim.boilerActive().setBool(false);
					hvacSim.fanOutActive().setBool(true);
					hvacSim.fanInActive().setBool(false);
					hvacSim.valveInPosition().setInt(50);
					hvacSim.valveOutPosition().setInt(100);
					controllerState = ControllerState.COOLING;
					ObjectBrokerHelper.getInstance().pullObj(new Uri(LINK_STANDBY_MODE_ACTIVE), false).writeObject(new Bool(false));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(LINK_COMFORT_MODE_ACTIVE), false).writeObject(new Bool(true));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(HEATER_LINK), false).writeObject(new Real(0));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(COOLER_LINK), false).writeObject(new Real(100));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_IN_LINK), false).writeObject(new Real(100));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_OUT_LINK), false).writeObject(new Real(100));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_IN_VALVE_LINK), false).writeObject(new Real(100));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_OUT_VALVE_LINK), false).writeObject(new Real(100));
					ObjectBrokerHelper.getInstance().pullObj(new Uri(HEATING_STATUS_LINK), false).writeObject(new Real(-100));	
			} else if (roomCurrentTemp.get() > roomTempSetPoint.get()
					&& controllerState == ControllerState.HEATING && windowClosed.get()==true) {
				// we have reached the target heating temp
				hvacSim.coolerActive().setBool(false);
				hvacSim.boilerActive().setBool(false);
				hvacSim.fanOutActive().setBool(false);
				hvacSim.fanInActive().setBool(false);
				hvacSim.valveInPosition().setInt(0);
				hvacSim.valveOutPosition().setInt(0);
				//controlValue.set(0);
				controllerState = ControllerState.INACTIVE;
				ObjectBrokerHelper.getInstance().pullObj(new Uri(LINK_STANDBY_MODE_ACTIVE), false).writeObject(new Bool(true));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(LINK_COMFORT_MODE_ACTIVE), false).writeObject(new Bool(false));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(HEATER_LINK), false).writeObject(new Real(0));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(COOLER_LINK), false).writeObject(new Real(0));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_IN_LINK), false).writeObject(new Real(0));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_OUT_LINK), false).writeObject(new Real(0));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_IN_VALVE_LINK), false).writeObject(new Real(0));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_OUT_VALVE_LINK), false).writeObject(new Real(0));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(HEATING_STATUS_LINK), false).writeObject(new Real(0));	
			} else if (roomCurrentTemp.get() < roomTempSetPoint.get()
					&& controllerState == ControllerState.COOLING && windowClosed.get()==true) {
				// we have reached the target cooling temp
				hvacSim.coolerActive().setBool(false);
				hvacSim.boilerActive().setBool(false);
				hvacSim.fanOutActive().setBool(false);
				hvacSim.fanInActive().setBool(false);
				hvacSim.valveInPosition().setInt(0);
				hvacSim.valveOutPosition().setInt(0);
				//controlValue.set(0);
				controllerState = ControllerState.INACTIVE;
				ObjectBrokerHelper.getInstance().pullObj(new Uri(LINK_STANDBY_MODE_ACTIVE), false).writeObject(new Bool(true));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(LINK_COMFORT_MODE_ACTIVE), false).writeObject(new Bool(false));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(HEATER_LINK), false).writeObject(new Real(0));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(COOLER_LINK), false).writeObject(new Real(0));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_IN_LINK), false).writeObject(new Real(0));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_OUT_LINK), false).writeObject(new Real(0));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_IN_VALVE_LINK), false).writeObject(new Real(0));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(AIR_OUT_VALVE_LINK), false).writeObject(new Real(0));
				ObjectBrokerHelper.getInstance().pullObj(new Uri(HEATING_STATUS_LINK), false).writeObject(new Real(0));	
			}
			
		}
		else{
			// System.out.println("not enabled");
		}	
	}

	@Override
	public Bool enabled() {
		return enabled;
	}
	@Override
	public Real roomTempSetPoint() {
		return roomTempSetPoint;
	}
	@Override
	public Real roomCurrentTemp() {
		return roomCurrentTemp;
	}
	@Override
	public Real tolerance() {
		return tolerance;
	}
	@Override
	public Real tempOutsideOffset() {
		return tempOutsideOffset;
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
		return comfortModeActive;
	}
}



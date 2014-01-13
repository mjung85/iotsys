package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.impl;

import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.ObjectBrokerHelper;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.TemperatureControlActuator;
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
	private HVACSimulationSuitcase hvacSim ;
	
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
		
		this.add(enabled);
		this.add(roomCurrentTemp);
		this.add(roomTempSetPoint);
		this.add(tolerance);
		
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
		System.out.println("in writeObject");
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

			System.out.println("is a Real");
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
		doControl();
	}

	
	
	
	private void doControl() {
		if (enabled.get()) {
			System.out.println("enabede");
			System.out.println(roomTempSetPoint);
			System.out.println(tolerance);
			
			if (roomCurrentTemp.get() < roomTempSetPoint.get() - tolerance.get()
					&& controllerState != ControllerState.HEATING) {
					// we need to heat!
					controllerState = ControllerState.HEATING;
					hvacSim.coolerActive().setBool(false);
					hvacSim.boilerActive().setBool(true);
					hvacSim.fanOutActive().setBool(false);
					hvacSim.fanInActive().setBool(true);
					hvacSim.valveInPosition().setInt(100);
					hvacSim.valveOutPosition().setInt(50);
					//hvacSim.heatPumpActive().setBool(true);
				//controlValue.set(100);
			} else if (roomCurrentTemp.get() > roomTempSetPoint.get() + tolerance.get()
					&& controllerState != ControllerState.COOLING) {
					//controlValue.set(-100);
					hvacSim.coolerActive().setBool(true);
					hvacSim.boilerActive().setBool(false);
					hvacSim.fanOutActive().setBool(true);
					hvacSim.fanInActive().setBool(false);
					hvacSim.valveInPosition().setInt(50);
					hvacSim.valveOutPosition().setInt(100);
					controllerState = ControllerState.COOLING;
			} else if (roomCurrentTemp.get() > roomTempSetPoint.get()
					&& controllerState == ControllerState.HEATING) {
				// we have reached the target heating temp
				hvacSim.coolerActive().setBool(false);
				hvacSim.boilerActive().setBool(false);
				hvacSim.fanOutActive().setBool(false);
				hvacSim.fanInActive().setBool(false);
				hvacSim.valveInPosition().setInt(0);
				hvacSim.valveOutPosition().setInt(0);
				//controlValue.set(0);
				controllerState = ControllerState.INACTIVE;
			} else if (roomCurrentTemp.get() < roomTempSetPoint.get()
					&& controllerState == ControllerState.COOLING) {
				// we have reached the target cooling temp
				hvacSim.coolerActive().setBool(false);
				hvacSim.boilerActive().setBool(false);
				hvacSim.fanOutActive().setBool(false);
				hvacSim.fanInActive().setBool(false);
				hvacSim.valveInPosition().setInt(0);
				hvacSim.valveOutPosition().setInt(0);
				//controlValue.set(0);
				controllerState = ControllerState.INACTIVE;
			}
		}
		else{
			System.out.println("not enabled");
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



	
}



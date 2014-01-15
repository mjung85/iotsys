package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.impl;

import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.ObjectBrokerHelper;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.WindowInfluenceSimulation;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sim.HVACSimulationSuitcase;
import at.ac.tuwien.auto.iotsys.obix.observer.Observer;
import at.ac.tuwien.auto.iotsys.obix.observer.Subject;
import obix.Bool;
import obix.Contract;
import obix.Obj;
import obix.Uri;

public class WindowInfluenceSimulationImpl extends Obj implements WindowInfluenceSimulation{

	private ObjectBroker objectBroker;
	private ControllerState controllerState = ControllerState.INACTIVE;
	protected Bool enabled = new Bool(false);
	protected Bool windowClosed = new Bool(false);
	protected Bool comfortModeActive = new Bool(false);
	protected Bool standbyModeActive = new Bool(false);
	
	private HVACSimulationSuitcase hvacSim;
	
	public WindowInfluenceSimulationImpl(){
		objectBroker= ObjectBrokerHelper.getInstance();
		
		setIs(new Contract(WindowInfluenceSimulationImpl.CONTRACT));
		
		enabled.setName("enabled");
		enabled.setDisplayName("Enabled");
		enabled.setHref(new Uri("enabled"));
		enabled.setWritable(true);
		
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
		this.add(windowClosed);
		this.add(comfortModeActive);
		this.add(standbyModeActive);
		
		
		Obj obj = objectBroker.pullObj(new Uri("/HVAC+Simulation/hvacSimSuitcase"), false);
			
		if (obj instanceof HVACSimulationSuitcase)
		{
			hvacSim = (HVACSimulationSuitcase) obj;

			//System.out.println("Current Temp: " + hvacSim.temp());
			windowClosed.set(hvacSim.temp());
			
			hvacSim.temp().attach(new Observer(){
				@Override
				public void update(Object state) {
					// TODO Auto-generated method stub
					if(state instanceof Obj){
						doControl();
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
	
	@Override
	public Bool enabled() {
		return enabled;
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
	public Bool windowClosed() {
		return windowClosed;
	}

	private void doControl() {
		if (enabled.get()) {
			
			System.out.println("enabled");
			System.out.println("Windwos Staus: "+windowClosed.get());
			System.out.println("standbyModeActive Status: "+standbyModeActive.get());
			System.out.println("comfortModeActive Status: "+comfortModeActive.get());
			
			
//			if (windowClosed.get() < roomTempSetPoint.get() - tolerance.get()
//					&& controllerState != ControllerState.HEATING) {
//					// we need to heat!
//					controllerState = ControllerState.HEATING;
//					hvacSim.coolerActive().setBool(false);
//					hvacSim.boilerActive().setBool(true);
//					hvacSim.fanOutActive().setBool(false);
//					hvacSim.fanInActive().setBool(true);
//					hvacSim.valveInPosition().setInt(100);
//					hvacSim.valveOutPosition().setInt(50);
//					//hvacSim.heatPumpActive().setBool(true);
//				//controlValue.set(100);
//			} else if (roomCurrentTemp.get() > roomTempSetPoint.get() + tolerance.get()
//					&& controllerState != ControllerState.COOLING) {
//					//controlValue.set(-100);
//					hvacSim.coolerActive().setBool(true);
//					hvacSim.boilerActive().setBool(false);
//					hvacSim.fanOutActive().setBool(true);
//					hvacSim.fanInActive().setBool(false);
//					hvacSim.valveInPosition().setInt(50);
//					hvacSim.valveOutPosition().setInt(100);
//					controllerState = ControllerState.COOLING;
//			} else if (roomCurrentTemp.get() > roomTempSetPoint.get()
//					&& controllerState == ControllerState.HEATING) {
//				// we have reached the target heating temp
//				hvacSim.coolerActive().setBool(false);
//				hvacSim.boilerActive().setBool(false);
//				hvacSim.fanOutActive().setBool(false);
//				hvacSim.fanInActive().setBool(false);
//				hvacSim.valveInPosition().setInt(0);
//				hvacSim.valveOutPosition().setInt(0);
//				//controlValue.set(0);
//				controllerState = ControllerState.INACTIVE;
//			} else if (roomCurrentTemp.get() < roomTempSetPoint.get()
//					&& controllerState == ControllerState.COOLING) {
//				// we have reached the target cooling temp
//				hvacSim.coolerActive().setBool(false);
//				hvacSim.boilerActive().setBool(false);
//				hvacSim.fanOutActive().setBool(false);
//				hvacSim.fanInActive().setBool(false);
//				hvacSim.valveInPosition().setInt(0);
//				hvacSim.valveOutPosition().setInt(0);
//				//controlValue.set(0);
//				controllerState = ControllerState.INACTIVE;
//			}
			
		}
		else{
			System.out.println("not enabled");
		}
	}
}

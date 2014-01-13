package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.impl;

import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.ObjectBrokerHelper;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.WindowInfluenceSimulation;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sim.HVACSimulationSuitcase;
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
	}
	
	@Override
	public Bool enabled() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Bool comfortModeActive() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bool standbyModeActive() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bool windowClosed() {
		// TODO Auto-generated method stub
		return null;
	}

}

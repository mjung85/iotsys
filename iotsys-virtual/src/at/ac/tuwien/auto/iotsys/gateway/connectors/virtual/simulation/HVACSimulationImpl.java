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

package at.ac.tuwien.auto.iotsys.gateway.connectors.virtual.simulation;

import java.util.logging.Logger;

import obix.Bool;
import obix.Contract;
import obix.Obj;
import obix.Real;
import obix.Str;
import obix.Uri;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sim.HVACSimulation;

/**
 * Singleton used for simulating a HVAC system.
 */
public class HVACSimulationImpl extends Obj implements HVACSimulation {
	
	public static HVACSimulationImpl instance = null;
	
	private static final Logger log = Logger.getLogger(HVACSimulationImpl.class.getName());

	private Real temp = new Real(18);

//	// if no cooling or heating happens the season
//	// depicts the impact on the temperature
//	private SEASON season = SEASON.SUMMER;

	protected Str season = new Str("summer");
	// change of degree celsius per TIME_INTERVALL_MS depending on season
	protected Real summerImpact = new Real(0.1); // raise 0.05 degree 

	protected Real springImpact = new Real(summerImpact.get() / 2);
	protected Real winterImpact = new Real(-summerImpact.get());
	protected Real fallImpact = new Real(winterImpact.get() / 2);
	
	protected Real heatingImpact = new Real(summerImpact.get() * 2);
	protected Real coolingImpact = new Real(winterImpact.get() * 2);
	
	protected Bool boilerActive = new Bool(false);
	
	protected Bool heatPumpActive = new Bool(false);
	
	protected Bool  fanInActive = new Bool(false);
	
	protected Bool coolerActive = new Bool(false);
	
	protected Bool coolPumpActive = new Bool(false);
	
	protected Bool enabled = new Bool(false);
	
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

	public static final int TIME_INTERVALL_MS = 2000;
	
	private volatile boolean threadStarted = false;
	
	private SimThread simThread;

	public HVACSimulationImpl() {
		setIs(new Contract(HVACSimulation.CONTRACT));
		instance = this;
		
		this.boilerActive.setName("boilerActive");
		this.boilerActive.setHref(new Uri("boilerActive"));
		this.add(boilerActive);
		
		this.coolerActive.setName("coolerActive");
		this.coolerActive.setHref(new Uri("coolerActive"));
		this.add(coolerActive);
		
		this.coolingImpact.setName("coolingImpact");
		this.coolingImpact.setHref(new Uri("coolingImpact"));
		this.add(coolingImpact);
		
		this.coolPumpActive.setName("coolPumpActive");
		this.coolPumpActive.setHref(new Uri("coolPumpActive"));
		this.add(coolPumpActive);
		
		this.enabled.setName("enabled");
		this.enabled.setHref(new Uri("enabled"));
		this.add(enabled);
		
		this.fallImpact.setName("fallImpact");
		this.fallImpact.setHref(new Uri("fallImpact"));
		this.add(fallImpact);
		
		this.fanInActive.setName("fanInActive");
		this.fanInActive.setHref(new Uri("fanInActive"));
		this.add(fanInActive);
		
		this.heatingImpact.setName("heatingImpact");
		this.heatingImpact.setHref(new Uri("heatingImpact"));
		this.add(heatingImpact);
		
		this.heatPumpActive.setName("heatPumpActive");
		this.heatPumpActive.setHref(new Uri("heatPumpActive"));
		this.add(heatPumpActive);
		
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
		temp.set(temp.get() - d);
	}

	public boolean isBoilerActive() {
		return boilerActive.get();
	}

	public void setBoilerActive(boolean boilerActive) {
		this.boilerActive.set(boilerActive);
	}

	public boolean isHeatPumpActive() {
		return heatPumpActive.get();
	}

	public void setHeatPumpActive(boolean heatPumpActive) {
		this.heatPumpActive.set(heatPumpActive);
	}

	public boolean isFanInActive() {
		return fanInActive.get();
	}

	public void setFanInActive(boolean fanInActive) {
		this.fanInActive.set(fanInActive);
	}

	public boolean isCoolerActive() {
		return coolerActive.get();
	}

	public void setCoolerActive(boolean coolerActive) {
		this.coolerActive.set(coolerActive);
	}

	public boolean isCoolPumpActive() {
		return coolPumpActive.get();
	}

	public void setCoolPumpActive(boolean coolPumpActive) {
		this.coolPumpActive.set(coolPumpActive);
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
	public Bool heatPumpActive() {
		return heatPumpActive;
	}

	@Override
	public Bool fanInActive() {
		return fanInActive;
	}

	@Override
	public Bool coolerActive() {
		return coolerActive;
	}

	@Override
	public Bool coolPumpActive() {
		return coolPumpActive;
	}

	@Override
	public Bool enabled() {
		return enabled;
	}
	
	@Override
	public void writeObject(Obj input){
		String resourceUriPath = "";
		
		if (input.getHref() == null) {
			resourceUriPath = input.getInvokedHref().substring(
					input.getInvokedHref().lastIndexOf('/') + 1);
		} else {
			resourceUriPath = input.getHref().get();
		}

		if (input instanceof HVACSimulation) {
			HVACSimulation in = (HVACSimulation) input;
			this.enabled.set(in.enabled().get());
			this.temp.set(in.temp().get());
			this.season.set(in.season().get());
			this.enabled.set(in.enabled().get());
			this.boilerActive.set(in.boilerActive().get());
			this.coolerActive.set(in.coolerActive().get());
			this.coolingImpact.set(in.coolingImpact().get());
			this.coolPumpActive.set(in.coolPumpActive().get());
			this.fallImpact.set(in.fallImpact().get());
			this.fanInActive.set(in.fanInActive().get());
			this.heatingImpact.set(in.heatingImpact().get());
			this.heatPumpActive.set(in.heatPumpActive().get());
			this.springImpact.set(in.springImpact().get());
			this.summerImpact.set(in.summerImpact().get());
			this.winterImpact.set(in.winterImpact().get());
		} 
		
		if(enabled().get() && !threadStarted){
			synchronized(this){
				simThread = new SimThread(this);
				simThread.setDaemon(true);
				simThread.start();
				threadStarted = true;
			}
		}
		else if(!enabled().get() && threadStarted){
			synchronized(this){
				simThread.stopSim();
				threadStarted = false;
			}
		}	
	}

	@Override
	public Real temp() {
		return temp;
	}
}

class SimThread extends Thread{
	private HVACSimulationImpl hvacSimulation = null;
	private static final Logger log = Logger.getLogger(SimThread.class.getName());

	private volatile boolean stopped = false;
	public SimThread(HVACSimulationImpl hvacSimulation) {
		this.hvacSimulation = hvacSimulation;
	}

	@Override
	public void run() {
		while (!stopped) {
			try {
				double impact = 0;
				if (hvacSimulation.getSeason().equals("summer")) {
					if(hvacSimulation.getTemp() < 40)
						impact = hvacSimulation.getSummerImpact();
				} else if (hvacSimulation.getSeason().equals("winter")) {
					if(hvacSimulation.getTemp() > -10)
						impact = hvacSimulation.getWinterImpact();
				} else if (hvacSimulation.getSeason().equals("spring")) {
					if(hvacSimulation.getTemp() < 30)
						impact = hvacSimulation.getSpringImpact();
				} else if (hvacSimulation.getSeason().equals("fall")) {
					if(hvacSimulation.getTemp() > 10)
						impact = hvacSimulation.getFallImpact();
				}
				hvacSimulation.increaseTemp(impact);
				log.finest("HVAC simulation temp is now: " + hvacSimulation.getTemp());
				Thread.sleep(HVACSimulationImpl.TIME_INTERVALL_MS);
				
				if(hvacSimulation.isBoilerActive() && hvacSimulation.isHeatPumpActive() && hvacSimulation.isFanInActive()){
					hvacSimulation.increaseTemp(hvacSimulation.getHeatingImpact());
					log.finest("HVAC simulation temp is now (after heating): " + hvacSimulation.getTemp());
				}
				
				if(hvacSimulation.isCoolerActive() && hvacSimulation.isCoolPumpActive() && hvacSimulation.isFanInActive()){
					log.finest("HVAC simulation temp is now (after cooling): " + hvacSimulation.getTemp());
					hvacSimulation.increaseTemp(hvacSimulation.getCoolingImpact());
				}
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	public void stopSim(){
		stopped = true;
	}
	
}

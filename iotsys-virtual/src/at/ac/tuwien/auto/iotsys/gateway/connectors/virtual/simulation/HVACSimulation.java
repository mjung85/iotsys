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

/**
 * Singleton used for simulating a HVAC system.
 * 
 */
public class HVACSimulation {
	public static final HVACSimulation instance = new HVACSimulation();
	
	private static final Logger log = Logger.getLogger(HVACSimulation.class.getName());

	private double temp = 18;

	// if no cooling or heating happens the season
	// depicts the impact on the temperature
	private SEASON season = SEASON.SUMMER;

	// change of degree celsius per TIME_INTERVALL_MS depending on season
	private double summerImpact = 0.1; // raise 0.05 degree 

	private double springImpact = summerImpact / 2;
	private double winterImpact = -summerImpact;
	private double fallImpact = winterImpact / 2;
	
	private double heatingImpact = summerImpact * 2;
	private double coolingImpact = winterImpact * 2;
	
	private boolean boilerActive = false;
	
	private boolean heatPumpActive = false;
	
	private boolean fanInActive = false;
	
	private boolean coolerActive = false;
	
	private boolean coolPumpActive = false;
	
	

	public double getHeatingImpact() {
		return heatingImpact;
	}

	public void setHeatingImpact(double heatingImpact) {
		this.heatingImpact = heatingImpact;
	}

	public double getCoolingImpact() {
		return coolingImpact;
	}

	public void setCoolingImpact(double coolingImpact) {
		this.coolingImpact = coolingImpact;
	}

	public static final int TIME_INTERVALL_MS = 2000;

	private HVACSimulation() {
		SimThread simThread = new SimThread(this);
		simThread.setDaemon(true);
		simThread.start();
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public SEASON getSeason() {
		return season;
	}

	public void setSeason(SEASON season) {
		this.season = season;
	}

	public double getSummerImpact() {
		return summerImpact;
	}

	public void setSummerImpact(double summerImpact) {
		this.summerImpact = summerImpact;
	}

	public double getSpringImpact() {
		return springImpact;
	}

	public void setSpringImpact(double springImpact) {
		this.springImpact = springImpact;
	}

	public double getWinterImpact() {
		return winterImpact;
	}

	public void setWinterImpact(double winterImpact) {
		this.winterImpact = winterImpact;
	}

	public double getFallImpact() {
		return fallImpact;
	}

	public void setFallImpact(double fallImpact) {
		this.fallImpact = fallImpact;
	}

	public void increaseTemp(double d) {
		temp += d;
	}
	
	public void decreaseTemp(double d) {
		temp -= d;
		
	}

	public boolean isBoilerActive() {
		return boilerActive;
	}

	public void setBoilerActive(boolean boilerActive) {
		this.boilerActive = boilerActive;
	}

	public boolean isHeatPumpActive() {
		return heatPumpActive;
	}

	public void setHeatPumpActive(boolean heatPumpActive) {
		this.heatPumpActive = heatPumpActive;
	}

	public boolean isFanInActive() {
		return fanInActive;
	}

	public void setFanInActive(boolean fanInActive) {
		this.fanInActive = fanInActive;
	}

	public boolean isCoolerActive() {
		return coolerActive;
	}

	public void setCoolerActive(boolean coolerActive) {
		this.coolerActive = coolerActive;
	}

	public boolean isCoolPumpActive() {
		return coolPumpActive;
	}

	public void setCoolPumpActive(boolean coolPumpActive) {
		this.coolPumpActive = coolPumpActive;
	}
}

class SimThread extends Thread{
	private HVACSimulation hvacSimulation = null;
	private static final Logger log = Logger.getLogger(SimThread.class.getName());

	public SimThread(HVACSimulation hvacSimulation) {
		this.hvacSimulation = hvacSimulation;
	}

	@Override
	public void run() {
		while (true) {
			try {
				double impact = 0;
				if (hvacSimulation.getSeason().equals(SEASON.SUMMER)) {
					if(hvacSimulation.getTemp() < 40)
						impact = hvacSimulation.getSummerImpact();
				} else if (hvacSimulation.getSeason().equals(SEASON.WINTER)) {
					if(hvacSimulation.getTemp() > -10)
						impact = hvacSimulation.getWinterImpact();
				} else if (hvacSimulation.getSeason().equals(SEASON.SPRING)) {
					if(hvacSimulation.getTemp() < 30)
						impact = hvacSimulation.getSpringImpact();
				} else if (hvacSimulation.getSeason().equals(SEASON.FALL)) {
					if(hvacSimulation.getTemp() > 10)
						impact = hvacSimulation.getFallImpact();
				}
				hvacSimulation.increaseTemp(impact);
				log.finest("HVAC simulation temp is now: " + hvacSimulation.getTemp());
				Thread.sleep(HVACSimulation.TIME_INTERVALL_MS);
				
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
}

enum SEASON {
	SUMMER, WINTER, FALL, SPRING
}
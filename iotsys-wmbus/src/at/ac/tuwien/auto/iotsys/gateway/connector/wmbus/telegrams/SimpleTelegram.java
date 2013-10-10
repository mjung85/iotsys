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

package at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams;

import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.util.Measure_Unit;

public class SimpleTelegram {
	
	private long timeStamp;
	private double power;
	private Measure_Unit powerUnit;
	private double energy;
	private Measure_Unit energyUnit;
	
	public SimpleTelegram() {
		timeStamp = new java.util.Date().getTime();
	}
	
	public SimpleTelegram(long _timeStamp) {
		timeStamp = _timeStamp;
	}
	
	public SimpleTelegram(double power, Measure_Unit powerUnit, double energy,
			Measure_Unit energyUnit) {
		super();
		timeStamp = new java.util.Date().getTime();
		this.power = power;
		this.powerUnit = powerUnit;
		this.energy = energy;
		this.energyUnit = energyUnit;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public double getPower() {
		return power;
	}
	public void setPower(double power) {
		this.power = power;
	}
	public Measure_Unit getPowerUnit() {
		return powerUnit;
	}
	public void setPowerUnit(Measure_Unit powerUnit) {
		this.powerUnit = powerUnit;
	}
	public double getEnergy() {
		return energy;
	}
	public void setEnergy(double energy) {
		this.energy = energy;
	}
	public Measure_Unit getEnergyUnit() {
		return energyUnit;
	}
	public void setEnergyUnit(Measure_Unit energyUnit) {
		this.energyUnit = energyUnit;
	}
	
	public void debugOutput() {
		System.out.println("-----------SimpleTelegram----------");
		System.out.println("Power: " + this.power + " " + this.powerUnit);
		System.out.println("Energy: " + this.energy + " " + this.energyUnit);
	}
}

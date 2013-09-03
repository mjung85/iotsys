/*******************************************************************************
 * Copyright (c) 2013, Automation Systems Group, TU Wien.
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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.unit.impl;

import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.contracts.Dimension;

public class DimensionImpl extends Obj implements Dimension
{
	private Int kg, m, sec, K, A, mol, cd;

	public DimensionImpl(int kg, int m, int sec, int K, int A, int mol, int cd)
	{
		this.setName("dimension");
		this.setIs(new Contract("obix:Dimension"));
		
		if (kg != 0)
			this.add(this.kg = new Int("kg",kg));

		if (m != 0)
			this.add(this.m = new Int("m",m));

		if (sec != 0)
			this.add(this.sec = new Int("sec",sec));

		if (K != 0)
			this.add(this.K = new Int("K",K));

		if (A != 0)
			this.add(this.A = new Int("A",A));

		if (mol != 0)
			this.add(this.mol = new Int("mol",mol));

		if (cd != 0)
			this.add(this.cd = new Int("cd",cd));
	}

	@Override
	public Int kg()
	{
		return kg;
	}

	@Override
	public Int m()
	{
		return m;
	}

	@Override
	public Int sec()
	{
		return sec;
	}

	@Override
	public Int K()
	{

		return K;
	}

	@Override
	public Int A()
	{

		return A;
	}

	@Override
	public Int mol()
	{

		return mol;
	}

	@Override
	public Int cd()
	{

		return cd;
	}
}

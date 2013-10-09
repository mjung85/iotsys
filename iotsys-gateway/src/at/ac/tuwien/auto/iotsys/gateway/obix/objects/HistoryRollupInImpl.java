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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects;

import obix.Abstime;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Reltime;
import obix.Status;
import obix.Uri;
import obix.contracts.HistoryRollupIn;

public class HistoryRollupInImpl extends Obj implements HistoryRollupIn{

	public static final String HISTORY_ROLLUPIN_CONTRACT = "obix:HistoryRollupIn";

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Obj getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Obj getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri getHref() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri getNormalizedHref() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHref(Uri href) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Contract getIs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIs(Contract is) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isVal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBool() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnum() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStr() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAbstime() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReltime() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUri() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isList() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRef() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFeed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isErr() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getBool() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getInt() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getReal() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getStr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBool(boolean val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInt(long val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setReal(double val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStr(String val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDisplay(String display) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDisplayName(String displayName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Uri getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIcon(Uri icon) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStatus(Status status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isNull() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setNull(boolean isNull) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWritable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWritable(boolean writable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWritable(boolean writable, boolean recursive) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Obj get(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Obj[] list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Obj add(Obj kid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Obj addAll(Obj[] kid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Obj kid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replace(Obj oldObj, Obj newObj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeThis() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Int limit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Abstime start() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Abstime end() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reltime interval() {
		// TODO Auto-generated method stub
		return null;
	}

}

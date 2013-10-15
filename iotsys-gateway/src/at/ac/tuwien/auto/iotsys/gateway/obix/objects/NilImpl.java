package at.ac.tuwien.auto.iotsys.gateway.obix.objects;

import obix.Contract;
import obix.Obj;
import obix.contracts.Nil;

public class NilImpl extends Obj implements Nil {
	public NilImpl(){
		this.setNull(true);
		this.setIs(new Contract("obix:Nil"));
	}
}

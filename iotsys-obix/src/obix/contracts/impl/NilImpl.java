package obix.contracts.impl;

import obix.Contract;
import obix.Obj;
import obix.contracts.Nil;

public class NilImpl extends Obj implements Nil {

	public NilImpl() {
		setIs(new Contract(Nil.CONTRACT));
		
		setNull(true);
	}
}

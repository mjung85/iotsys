package at.ac.tuwien.auto.iotsys.commons.obix.objects;

import obix.Contract;
import obix.Obj;
import obix.contracts.Nil;

public class NillImpl extends Obj implements Nil {

	public NillImpl() {
		setIs(new Contract(WatchImpl.OBIX_NIL));
	}

}

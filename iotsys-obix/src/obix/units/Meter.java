package obix.units;

import obix.contracts.impl.DimensionImpl;
import obix.contracts.impl.UnitImpl;

public class Meter extends UnitImpl {
	
	public Meter() {
		super("m", 1, 0, new DimensionImpl(0, 1, 0, 0, 0, 0, 0));
	}
}

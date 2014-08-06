package obix.units;

import obix.contracts.impl.DimensionImpl;
import obix.contracts.impl.UnitImpl;

public class Millimeter extends UnitImpl {
	
	public Millimeter() {
		super("mm", 0.001, 0, new DimensionImpl(0, 1, 0, 0, 0, 0, 0));
	}
}

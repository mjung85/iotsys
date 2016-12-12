package obix.units;

import obix.contracts.impl.DimensionImpl;
import obix.contracts.impl.UnitImpl;

public class Celsius extends UnitImpl{
		
	public Celsius() {
		super("°C", 1, -273.15, new DimensionImpl(0, 0, 0, 1, 0, 0, 0));
	}
}

package obix.units;

import obix.contracts.impl.DimensionImpl;
import obix.contracts.impl.UnitImpl;

public class Hectopascal extends UnitImpl {

	public Hectopascal() {
		super("hPa", 100, 0, new DimensionImpl(1, -1, -2, 0, 0, 0, 0));
	}
}

package obix.contracts.impl;

import obix.Contract;
import obix.Obj;
import obix.Real;
import obix.Str;
import obix.Uri;
import obix.contracts.Dimension;
import obix.contracts.Unit;

public class UnitImpl extends Obj implements Unit {

	protected Str symbol;
	protected DimensionImpl dimension;
	protected Real scale;
	protected Real offset;
	
	public UnitImpl(String symbol, double scale, double offset, Dimension dimension) {
		setIs(new Contract(Unit.CONTRACT));
		
		this.symbol = new Str("symbol", symbol);
		this.scale = new Real("scale", scale);
		this.offset = new Real("offset", offset);
		if (dimension != null)
			this.dimension = (DimensionImpl) dimension;
		else
			this.dimension = new DimensionImpl(0, 0, 0, 0, 0, 0, 0);
		
		this.symbol.setHref(new Uri("symbol"));
		this.scale.setHref(new Uri("scale"));
		this.offset.setHref(new Uri("offset"));
		this.dimension.setHref(new Uri("dimension"));
		
		add(this.symbol);
		add(this.scale);
		add(this.offset);
		add(this.dimension);
	}
	
	public UnitImpl() {
		this("", 0, 0, null);
	}

	@Override
	public Str symbol() {
		return symbol;
	}

	@Override
	public Dimension dimension() {
		return dimension;
	}

	@Override
	public Real scale() {
		return scale;
	}

	@Override
	public Real offset() {
		return offset;
	}

}

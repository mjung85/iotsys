package obix.contracts.impl;

import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Uri;
import obix.contracts.Dimension;

public class DimensionImpl extends Obj implements Dimension {

	private Int kg = new Int("kg");
	private Int m = new Int("m");
	private Int sec = new Int("sec");
	private Int K = new Int("K");
	private Int A = new Int("A");
	private Int mol = new Int("mol");	
	private Int cd = new Int("cd");
	
	public DimensionImpl(int kg, int m, int sec, int K, int A, int mol, int cd) {
		setIs(new Contract(Dimension.CONTRACT));
		
		this.kg.setSilent(kg);
		this.m.setSilent(m);
		this.sec.setSilent(sec);
		this.K.setSilent(K);
		this.A.setSilent(A);
		this.mol.setSilent(mol);
		this.cd.setSilent(cd);
		
		this.kg.setHref(new Uri("kg"));
		this.m.setHref(new Uri("m"));
		this.sec.setHref(new Uri("sec"));
		this.K.setHref(new Uri("K"));
		this.A.setHref(new Uri("A"));
		this.mol.setHref(new Uri("mol"));
		this.cd.setHref(new Uri("cd"));
		
		add(this.kg);
		add(this.m);
		add(this.sec);
		add(this.K);
		add(this.A);
		add(this.mol);
		add(this.cd);
	}
	
	public DimensionImpl() {
		this(0, 0, 0, 0, 0, 0, 0);
	}

	@Override
	public Int kg() {
		return kg;
	}

	@Override
	public Int m() {
		return m;
	}

	@Override
	public Int sec() {
		return sec;
	}

	@Override
	public Int K() {
		return K;
	}

	@Override
	public Int A() {
		return A;
	}

	@Override
	public Int mol() {
		return mol;
	}

	@Override
	public Int cd() {
		return cd;
	}

}

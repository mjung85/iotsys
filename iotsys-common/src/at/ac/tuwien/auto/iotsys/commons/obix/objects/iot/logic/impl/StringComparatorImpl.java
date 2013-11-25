package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.impl;

import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Str;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.StringComparator;

public class StringComparatorImpl extends Obj implements StringComparator {
	protected Str input1 = new Str();
	protected Str input2 = new Str();
	protected Bool enabled = new Bool();
	protected Bool caseSensitive = new Bool();
	protected Bool result = new Bool();
	protected obix.Enum compareType = new obix.Enum();

	public StringComparatorImpl() {
		setIs(new Contract(StringComparator.CONTRACT));

		input1.setName("input1");
		input1.setDisplayName("Input 1");
		input1.setHref(new Uri("input1"));

		input2.setName("input2");
		input2.setDisplayName("Input 2");
		input2.setHref(new Uri("input2"));

		enabled.setName("enabled");
		enabled.setDisplayName("Enabled");
		enabled.setHref(new Uri("enabled"));

		input1.setWritable(true);
		input2.setWritable(true);
		enabled.setWritable(true);

		result.setName("result");
		result.setDisplayName("Result");
		result.setHref(new Uri("result"));

		compareType.setName("compareType");
		compareType.setDisplayName("Compare Type");
		compareType.setRange(new Uri("/enums/stringCompareTypes"));
		compareType.set("eq");
		
		compareType.setHref(new Uri("compareType"));

		this.add(input1);
		this.add(input2);
		this.add(enabled);
		this.add(result);
		this.add(compareType);

	}

	@Override
	public Str input1() {
		return input1;
	}

	@Override
	public Str input2() {
		return input2;
	}

	@Override
	public Bool enabled() {
		return enabled;
	}

	@Override
	public obix.Enum compareType() {
		return compareType;
	}

	@Override
	public void writeObject(Obj input) {
		String resourceUriPath = "";
		if (input.getHref() == null) {
			resourceUriPath = input.getInvokedHref().substring(
					input.getInvokedHref().lastIndexOf('/') + 1);
		} else {
			resourceUriPath = input.getHref().get();
		}
		if (input instanceof StringComparator) {
			StringComparator in = (StringComparator) input;
			this.input1.set(in.input1().get());
			this.input2.set(in.input2().get());
			this.compareType.set(in.compareType().get());
			this.enabled.set(in.enabled().get());
		} else if (input instanceof Real) {
			
				
				if ("input1".equals(resourceUriPath)) {
					input1.set(((Real) input).get());
				} else if ("input2".equals(resourceUriPath)) {
					input2.set(((Real) input).get());
				} else if("enabled".equals(resourceUriPath)){
					enabled.set(((Real) input).get());
				} else if("caseSensitive".equals(resourceUriPath)){
					caseSensitive.set(((Real) input).get());
				}
			
		} else if (input instanceof Bool) {
			
				if ("input1".equals(resourceUriPath)) {
					input1.set(((Bool) input).get());
				} else if ("input2".equals(resourceUriPath)) {
					input2.set(((Bool) input).get());
				} else if("enabled".equals(resourceUriPath)){
					enabled.set(((Bool) input).get());
				} else if("caseSensitive".equals(resourceUriPath)){
					caseSensitive.set(((Bool) input).get());
				}
			
		}
		else if (input instanceof Int) {
			
				if ("input1".equals(resourceUriPath)) {
					input1.set(((Int) input).get());
				} else if ("input2".equals(resourceUriPath)) {
					input2.set(((Int) input).get());
				} else if("enabled".equals(resourceUriPath)){
					enabled.set(((Int) input).get());
				} else if("caseSensitive".equals(resourceUriPath)){
					caseSensitive.set(((Int) input).get());
				}
			
		}
		else if (input instanceof obix.Enum){
			this.compareType.set( ((obix.Enum) input).get() );
		}

		// perform control logic
		if (enabled.get()) {
			if (StringComparator.COMPARE_TYPE_EQ.equals(compareType.get())) {

				if (input1.get().equals(input2.get())) {
					result.set(true);
				} else {
					result.set(false);
				}
			} else if (StringComparator.COMPARE_TYPE_STARTS_WITH.equals(compareType.get())) {

				if (input1.get().startsWith(input2.get())) {
					result.set(true);
				} else {
					result.set(false);
				}
			} else if (StringComparator.COMPARE_TYPE_ENDS_WITH.equals(compareType.get())) {

				if (input1.get().endsWith(input2.get())) {
					result.set(true);
				} else {
					result.set(false);
				}
			} else if (StringComparator.COMPARE_TYPE_CONTAINS.equals(compareType.get())) {

				if (input1.get().contains(input2.get())) {
					result.set(true);
				} else {
					result.set(false);
				}
			} 
		}
	}

	@Override
	public void refreshObject() {
		// Nothing to do for logic objects
	}

	@Override
	public Bool result() {

		return result;
	}

	@Override
	public Bool caseSensitive() {
		
		return caseSensitive;
	}

}

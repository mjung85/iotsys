package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.impl;

import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.BinaryOperation;

public class BinaryOperationImpl extends Obj implements BinaryOperation {
	protected Real input1 = new Real();
	protected Real input2 = new Real();
	protected Bool enabled = new Bool();
	protected Real result = new Real();
	protected obix.Enum operationType = new obix.Enum();

	public BinaryOperationImpl() {
		setIs(new Contract(BinaryOperation.CONTRACT));

		input1.setName("input1");
		input1.setHref(new Uri("input1"));

		input2.setName("input2");
		input2.setHref(new Uri("input2"));

		enabled.setName("enabled");
		enabled.setHref(new Uri("enabled"));

		input1.setWritable(true);
		input2.setWritable(true);
		enabled.setWritable(true);

		result.setName("result");
		result.setHref(new Uri("result"));

		operationType.setName("operationType");
		operationType.setRange(new Uri("/enums/operationTypes"));
		operationType.set(BinaryOperation.BIN_OP_ADD);
		operationType.setHref(new Uri("operationType"));

		this.add(input1);
		this.add(input2);
		this.add(enabled);
		this.add(result);
		this.add(operationType);

	}

	@Override
	public Real input1() {
		return input1;
	}

	@Override
	public Real input2() {
		return input2;
	}

	@Override
	public Bool enabled() {
		return enabled;
	}

	@Override
	public obix.Enum operationType() {
		return operationType;
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

		if (input instanceof BinaryOperation) {
			BinaryOperation in = (BinaryOperation) input;
			this.input1.set(in.input1().get());
			this.input2.set(in.input2().get());
			this.operationType.set(in.operationType().get());
			this.enabled.set(in.enabled().get());
		} else if (input instanceof Real) {

			if ("input1".equals(resourceUriPath)) {
				input1.set(((Real) input).get());
			} else if ("input2".equals(resourceUriPath)) {
				input2.set(((Real) input).get());
			} else if ("enabled".equals(resourceUriPath)) {
				enabled.set(((Real) input).get());
			}

		} else if (input instanceof Bool) {

			if ("input1".equals(resourceUriPath)) {
				input1.set(((Bool) input).get());
			} else if ("input2".equals(resourceUriPath)) {
				input2.set(((Bool) input).get());
			} else if ("enabled".equals(resourceUriPath)) {
				enabled.set(((Bool) input).get());
			}

		} else if (input instanceof Int) {

			if ("input1".equals(resourceUriPath)) {
				input1.set(((Int) input).get());
			} else if ("input2".equals(resourceUriPath)) {
				input2.set(((Int) input).get());
			} else if ("enabled".equals(resourceUriPath)) {
				enabled.set(((Int) input).get());
			}

		}
		else if (input instanceof obix.Enum){
			this.operationType.set( ((obix.Enum) input).get() );
		}

		// perform control logic
		if (enabled.get()) {
			if (BinaryOperation.BIN_OP_ADD.equals(operationType.get())) {
				result.set(input1.get() + input2.get());
			} else if (BinaryOperation.BIN_OP_SUB.equals(operationType.get())) {
				result.set(input1.get() - input2.get());
			} else if (BinaryOperation.BIN_OP_MUL.equals(operationType.get())) {
				result.set(input1.get() * input2.get());
			} else if (BinaryOperation.BIN_OP_DIV.equals(operationType.get())) {
				if(input2.get() != 0){
					result.set(input1.get() / input2.get());
				}
				else{
					result.set(0);
				}
			}  else if (BinaryOperation.BIN_OP_MOD.equals(operationType.get())) {
				if(input2.get() != 0){
					result.set(input1.get() % input2.get());
				}
				else{
					result.set(0);
				}
			}
		}
	}

	@Override
	public void refreshObject() {
		// Nothing to do for logic objects
	}

	@Override
	public Real result() {

		return result;
	}
}

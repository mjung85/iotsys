package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.knx;

import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.SunblindActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.SunblindActuatorImpl;
import obix.Obj;


public class ComplexSunblindActuatorImplKnx extends SunblindActuatorImpl {

	private enum STATE_TYPE {
		STATE_STOP, STATE_MOVE_DOWN, STATE_MOVE_UP
	}

	private STATE_TYPE myState;
	private static final Logger log = Logger
			.getLogger(SunblindActuatorImplKnx.class.getName());

	private Timer timer;
	private GroupAddress status;
	private GroupAddress moveDown;
	private GroupAddress moveUp;
	private KNXConnector knxConnector;

	public static final Logger knxBus = KNXConnector.knxBus;

	public ComplexSunblindActuatorImplKnx(KNXConnector knxConnector,
			GroupAddress status, final GroupAddress moveDown,
			final GroupAddress moveUp) {
		super();
		this.status = status;
		this.moveDown = moveDown;
		this.moveUp = moveUp;
		this.knxConnector = knxConnector;
		myState = STATE_TYPE.STATE_STOP;

		if (status == null) {
			// add watchdog on switching group address
			knxConnector.addWatchDog(moveDown, new KNXWatchDog() {
				@Override
				public void notifyWatchDog(byte[] apdu) {
					try {
						DPTXlatorBoolean x = new DPTXlatorBoolean(
								DPTXlatorBoolean.DPT_OCCUPANCY);

						x.setData(apdu);

						if (x.getValueBoolean() != ComplexSunblindActuatorImplKnx.this.moveDownValue
								.get()) {
							ComplexSunblindActuatorImplKnx.this.moveDownValue.set(x
									.getValueBoolean());
						}

					} catch (KNXException e) {
						e.printStackTrace();
					}
				}
			});
			

		}
	}

	class ChangeStateTask extends TimerTask {

		private GroupAddress address;
		private boolean value;

		public ChangeStateTask(GroupAddress address, boolean value) {
			this.address = address;
			this.value = value;
		}

		@Override
		public void run() {
			// knxConnector.write(moveDown, false);
			knxConnector.write(this.address, this.value);
			// timer.cancel();
		}

	}
	
	class ChangeMoveStateTask extends TimerTask {

		private GroupAddress address;

		public ChangeMoveStateTask(GroupAddress address) {
			this.address = address;
		}

		@Override
		public void run() {
			knxConnector.write(this.address, true);
			timer = new Timer();
			timer.schedule(new ChangeStateTask(this.address, false), 2500);
		}

	}
	
	public void changeDirection(GroupAddress stopAddress, GroupAddress newAddressContraryDirektion){
		sunblindStop(stopAddress);
		timer = new Timer();
		timer.schedule(new ChangeMoveStateTask(newAddressContraryDirektion), 1000);
	}
	
	public void sunblindDown() {
		knxConnector.write(moveDown, true);
		timer = new Timer();
		timer.schedule(new ChangeStateTask(moveDown, false), 2500);
	}

	public void sunblindUp() {
		knxConnector.write(moveUp, true);
		timer = new Timer();
		timer.schedule(new ChangeStateTask(moveUp, false), 2500);
	}

	public void sunblindStop(GroupAddress address) {
		knxConnector.write(address, true);
		timer = new Timer();
		timer.schedule(new ChangeStateTask(address, false), 1000);
	}

	public void writeObject(Obj input) {
		// A write on this object was received, update the according data point.
	
		// boolean direction = false; // false == Up ; true = Down
	//	boolean newMoveDownValue = false;
		
		if (input instanceof SunblindActuator) {
			// boolean upDownValue = false;
			SunblindActuator in = (SunblindActuator) input;

			// vorzugsweise Up
			
			if (in.moveDownValue().get() && in.moveUpValue().get()) {
					in.moveDownValue().set(false);
				}
		
		}

		super.writeObject(input);

		log.info("myState:" + myState);
		switch (myState) {
		case STATE_STOP: // STOP
			log.info("State Number:" + myState);
			if (this.moveDownValue().get()) {
				myState = STATE_TYPE.STATE_MOVE_DOWN;
				sunblindDown();
			} else if (this.moveUpValue().get()) {
				myState = STATE_TYPE.STATE_MOVE_UP;
				sunblindUp();
			} 
			break;

		case STATE_MOVE_DOWN: // DOWN
			log.info("State Number:" + myState);

			if (this.moveUpValue().get()) {				
				myState = STATE_TYPE.STATE_MOVE_UP;
				changeDirection(moveDown,moveUp);

			} else if (this.moveDownValue().get()) {
//				myState = STATE_TYPE.STATE_MOVE_DOWN;
//				sunblindDown();
			} else {
				myState = STATE_TYPE.STATE_STOP;
				sunblindStop(moveDown);
			}
			break;

		case STATE_MOVE_UP: // UP
			log.info("State Number:" + myState);
			if (this.moveUpValue().get()) {
//				myState = STATE_TYPE.STATE_STOP;
//				sunblindUp();
			} else if (this.moveDownValue().get()) {
				myState = STATE_TYPE.STATE_MOVE_DOWN;
				changeDirection(moveUp,moveDown);
				//sunblindStop(moveUp);
				//sunblindDown();
			} else {
				myState = STATE_TYPE.STATE_STOP;
				sunblindStop(moveUp);
			}
			break;
		default:
			break;
		}

	}

	public void refreshObject() {
		if (status != null) {
			boolean value = knxConnector.readBool(status);
			this.moveDownValue().set(value);
		}
	}

}
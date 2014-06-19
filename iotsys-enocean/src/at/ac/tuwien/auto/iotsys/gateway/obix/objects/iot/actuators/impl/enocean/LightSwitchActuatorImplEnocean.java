package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.enocean;

import java.util.logging.Logger;

import obix.Bool;
import obix.Contract;
import obix.Obj;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.EnoceanConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.CRC8Hash;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.DeviceID;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3PacketHeader;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3PacketHeader.PacketType;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3Response;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3TelegramInterface.RORG;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3TelegramOneBS;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.enocean.PushButtonImplEnocean;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.actuators.LightSwitchActuatorEnocean;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl.ActuatorImpl;


public class LightSwitchActuatorImplEnocean extends ActuatorImpl implements LightSwitchActuatorEnocean {
	private static final Logger log = Logger.getLogger(PushButtonImplEnocean.class.getName());

	private EnoceanConnector connector;
	private String hexAddress;

	protected Bool value = new Bool(false);
	protected Bool lrn = new Bool(false);

	ESP3Response response = new ESP3Response();
	
	public LightSwitchActuatorImplEnocean(EnoceanConnector connector, final String hexAddress) {
		this.connector = connector;
//		this.hexAddress = hexAddress;
		this.hexAddress = DeviceID.toString(connector.getBaseID().getDeviceID() + Long.decode(hexAddress));
		
		setIs(new Contract(LightSwitchActuatorEnocean.CONTRACT));
		value.setWritable(true);
		Uri valueUri = new Uri(LightSwitchActuatorEnocean.VALUE_CONTRACT_HERF);
		value.setHref(valueUri);
		value.setName(LightSwitchActuatorEnocean.VALUE_CONTRACT_NAME);			

		lrn.setWritable(true);
		Uri lrnUri = new Uri(LightSwitchActuatorEnocean.LRN_CONTRACT_HERF);
		lrn.setHref(lrnUri);
		lrn.setName(LightSwitchActuatorEnocean.LRN_CONTRACT_NAME);

		add(value);
		add(lrn);

		log.info("EnOcean Address: " + hexAddress);
	}

	@Override
	public void initialize(){
		super.initialize();
		// But stuff here that should be executed after object creation
	}

	@Override
	public void writeObject(Obj input){
		super.writeObject(input);
		String resourceUriPath = "";
		if (input.getHref() == null) {
			resourceUriPath = input.getInvokedHref().substring(
					input.getInvokedHref().lastIndexOf('/') + 1);
		} else {
			resourceUriPath = input.getHref().get();
		}

		ESP3TelegramOneBS telegram = new ESP3TelegramOneBS((byte)0x00);

		telegram.setSubTelNum(0x03);
		telegram.setDestinationID(DeviceID.BROADCAST_ID);
		telegram.setdBm(0xFF);
		telegram.setSecurityLevel(0x00);

		telegram.setRORG(RORG.OneBS);
		telegram.setSenderID(DeviceID.fromString(hexAddress));
		telegram.setStatus((byte)0x00);

		// A write on this object was received, update the according data point.	
		// The base class knows how to update the internal variable and to trigger
		// all the oBIX specific processing.
		// super.writeObject(input);

		if (input instanceof LightSwitchActuatorEnocean) {
			LightSwitchActuatorEnocean in = (LightSwitchActuatorEnocean) input;
			log.finer("Writing on LightSwitchActuatorEnocean: "
					+ in.value().get() + ","
					+ in.lrn().get());

			this.value.set(in.value().get());
			this.lrn.set(in.lrn().get());

		} else if (input instanceof Bool) {

			if (LightSwitchActuatorEnocean.LRN_CONTRACT_HERF
					.equals(resourceUriPath)) {
				this.lrn.set(((Bool) input).get());

			} else if (LightSwitchActuatorEnocean.VALUE_CONTRACT_HERF
					.equals(resourceUriPath)) {
				this.value.set(((Bool) input).get());
			}

			if(this.value().get() == true)
			{
				
				if (this.lrn().get() == true)
				{
					telegram.setTelegramData((byte)0x00);
				} 
				else
				{
					telegram.setTelegramData((byte)0x08);
				}
				
				//telegram.setTelegramData((byte)0x08);
				byte[] telegramByte = telegram.toByteArray();
				ESP3PacketHeader header = new ESP3PacketHeader(telegram.getDataLen(), telegram.getOptDataLen(), PacketType.RADIO);
				byte[] headerByte = header.toByteArray();
				byte[] frame = new byte[1 + headerByte.length + 1 + telegramByte.length + 1];
				byte CRC8H = CRC8Hash.calculate(headerByte);
				byte CRC8D = CRC8Hash.calculate(telegramByte);

				frame[0] = (byte)0x55;
				System.arraycopy(headerByte, 0, frame, 1, headerByte.length);
				frame[headerByte.length + 1] = CRC8H;
				System.arraycopy(telegramByte, 0, frame, headerByte.length + 2, telegramByte.length);
				frame[frame.length - 1] = CRC8D;
				// connector.serialSend(frame);
				
				response = connector.send(frame);
				if (response == null)
				{
					log.info("Response = NULL!");
				} else {
					log.info("Response: " + response.getPayloadAsString());
				}
		//		log.info("LightSwitchActuator Response: " + connector.getResponse().getPayloadAsString());
			}
			else
			{
				
				if (this.lrn().get() == true)
				{
					telegram.setTelegramData((byte)0x00);
				} 
				else
				{
					telegram.setTelegramData((byte)0x09);
				}
				
				//telegram.setTelegramData((byte)0x09);
				byte[] telegramByte = telegram.toByteArray();
				ESP3PacketHeader header = new ESP3PacketHeader(telegram.getDataLen(), telegram.getOptDataLen(), PacketType.RADIO);
				byte[] headerByte = header.toByteArray();
				byte[] frame = new byte[1 + headerByte.length + 1 + telegramByte.length + 1];
				byte CRC8H = CRC8Hash.calculate(headerByte);
				byte CRC8D = CRC8Hash.calculate(telegramByte);

				frame[0] = (byte)0x55;
				System.arraycopy(headerByte, 0, frame, 1, headerByte.length);
				frame[headerByte.length + 1] = CRC8H;
				System.arraycopy(telegramByte, 0, frame, headerByte.length + 2, telegramByte.length);
				frame[frame.length - 1] = CRC8D;

				//connector.serialSend(frame);
		//		log.info("LightSwitchActuator Response: " + connector.getResponse().getPayloadAsString());
				response = connector.send(frame);
				if (response == null)
				{
					log.info("Response = NULL!");
				} else {
					log.info("Response: " + response.getPayloadAsString());
				}
			}

		}
	}

	@Override
	public void refreshObject(){
		// value is the protected instance variable of the base class (TemperatureSensorImpl)
		if(value != null){
			// Boolean value = virtualConnector.readBoolean(busAddress);	

			// this calls the implementation of the base class, which triggers als
			// oBIX services (e.g. watches, history) and CoAP observe!

			// this.value().set(value);
			// this.value().set(value.get());
			log.info("value is: " + value + " lrn is: " + lrn);
		}	
	}

	@Override
	public Bool lrn() {
		return this.lrn;
	}

	@Override
	public Bool value() {
		return this.value;
	}	
}

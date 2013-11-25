package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.rfid;

import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import obix.Abstime;
import obix.Bool;
import obix.Contract;
import obix.Obj;
import obix.Str;
import obix.Real;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sensors.impl.SensorImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.rfid.RfidConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.rfid.RfidWatchdog;
import at.ac.tuwien.auto.iotsys.gateway.connectors.rfid.util.RfidFrame;

public class EventImplRFID extends SensorImpl implements EventRFID  {
		private static final Logger log = Logger.getLogger(EventImplRFID.class.getName());
	
		protected Str rfidTag = new Str();
		protected Abstime rfidTime = new Abstime();	
		
		Date milis = new Date();
		TimeZone tz = TimeZone.getTimeZone("Europe/Vienna");
		
		public EventImplRFID(RfidConnector connector){
									
			setIs(new Contract(new String[]{EventRFID.CONTRACT}));
						
			rfidTag.setWritable(false);
			Uri rfidTagUri = new Uri(EventRFID.RFID_TAG_HREF);
			rfidTag.setHref(rfidTagUri);
			rfidTag.setName(EventRFID.RFID_TAG_NAME);
			rfidTag.set("");
			rfidTag.setWritable(true);
			add(rfidTag);
			
			log.info("String VALUE: " + rfidTag.get());
			
			rfidTime.setWritable(false);
			Uri rfidTimeUri = new Uri(EventRFID.RFID_TIME_HREF);
			rfidTime.setHref(rfidTimeUri);
			rfidTime.setName(EventRFID.RFID_TIME_NAME);
			rfidTime.set(milis.getTime(), tz);
			
			add(rfidTime);
			
			connector.addWatchDog(new RfidWatchdog() {
				
				@Override
				public void notifyWatchDog(String tag) {		
					rfidTag.set(tag);					
				}
			});
		}
		public void initialize(){
			super.initialize();
			// But stuff here that should be executed after object creation
		}
		
		

		public void refreshObject(){		
//			if(RfidFrame.instance != null){
//				
//				this.rfidTag().set(RfidFrame.instance.dataToString());				
//				log.info("RFID TAG is: " + rfidTag );
//				log.info("INSTANCE: " + RfidFrame.instance.dataToString());
//			}
		}

		@Override
		public Str rfidTag() {
			return this.rfidTag;
		}

		@Override
		public Abstime rfidTagTime() {
			return this.rfidTime;
		}
		
		
}

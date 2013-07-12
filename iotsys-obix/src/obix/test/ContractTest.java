/*
 * This code licensed to public domain
 */
package obix.test;

import java.io.*;
import obix.*;
import obix.Enum;
import obix.io.*;
import obix.contracts.*;

/**
 * ContractTest tests contracts and using them to map to specified types.
 *
 * @author    Brian Frank
 * @creation  21 Sept 05
 * @version   $Revision$ $Date$
 */
public class ContractTest
  extends Test
{                    

////////////////////////////////////////////////////////////////
// Main
////////////////////////////////////////////////////////////////  

  public void run()
    throws Exception
  {                          
    // verify !useContracts flag
    verify(!(make("<obj is='obix:Point'/>", false) instanceof Point));
    verify(!(make("<obj is='obix:History obix:Point'/>", false) instanceof Point));
    verify(!(make("<obj is='obix:History obix:Point'/>", false) instanceof History));
    verify(!(make("<real is='obix:History obix:Point'/>", false) instanceof History));
    verify((make("<real is='obix:History obix:Point'/>", false) instanceof Real));
    
    // verify using single marker contract                                
    verify(make("<obj is='obix:Point'/>") instanceof Point);
    verify(make("<obj is='obix:Point'/>") instanceof Obj);
    verify(make("<obj is='obix:Point'/>").getClass().getSuperclass() == Obj.class);

    // verify using single marker contract with base covariance
    verify(make("<real is='obix:Point'/>") instanceof Point);
    verify(make("<real is='obix:Point'/>") instanceof Obj);
    verify(make("<real is='obix:Point'/>") instanceof Real);
    verify(make("<int  is='obix:Point'/>").getClass().getSuperclass() == Int.class);
    
    // verify structure - Dimension should have 7 Int 
    // children added in it's constructor along with
    // their associated getter
    Dimension dim = (Dimension)make("<obj is='obix:Dimension'/>");
    verify(dim.getClass().getSuperclass() == Obj.class);
    verify(dim.size() == 7);  
    verify(dim.kg().get() == 0L);
    verify(dim.kg().getClass() == Int.class);
    verify(dim.m().get() == 0L);
    verify(dim.sec().get() == 0L);
    verify(dim.K().get() == 0L);
    verify(dim.A().get() == 0L);
    verify(dim.mol().get() == 0L);
    verify(dim.cd().get() == 0L);        
    dim.m().set(1);
    dim.sec().set(-2);
    dim = (Dimension)roundtrip((Obj)dim, false);
    verify(dim.m().get() == 1);
    verify(dim.sec().get() == -2);

    // verify contract (Unit) which contains child with
    // another contract (Dimension) 
    Unit unit = (Unit)make("<obj is='obix:Unit'/>");
    verify(unit.getClass().getSuperclass() == Obj.class);
    verify(unit.size() == 4);
    verify(unit.symbol().get().equals(""));
    verify(unit.offset().get() == 0d);
    verify(unit.scale().get() == 1d); // default val=1
    verify(unit.dimension() instanceof Dimension); // is='obix:Dimension'
    verify(unit.dimension() instanceof Obj);
    verify(unit.dimension().getIs().toString().equals("obix:Dimension")); 

    // now try parsing a unit with children filled in
    unit = (Unit)make("<obj is='obix:Unit'>" +
                        "<str name='symbol' val='\u00baC'/>" +
                        "<obj name='dimension'><int name='K' val='1'/></obj>" +
                        "<real name='offset' val='273'/>" +
                      "</obj>");
    verify(unit.symbol().get().equals("\u00baC"));
    verify(unit.offset().get() == 273);
    verify(unit.scale().get() == 1d); // default val=1
    verify(unit.dimension().K().get() == 1);
    verify(unit.dimension().kg().get() == 0);    
    
    // round IO trip the unit we just did
    unit.dimension().K().set(2);
    unit.dimension().kg().set(-8);
    unit.scale().set(77d);     
    unit = (Unit)roundtrip((Obj)unit, false);
    verify(unit.symbol().get().equals("\u00baC"));
    verify(unit.offset().get() == 273);
    verify(unit.scale().get() == 77d);
    verify(unit.dimension().K().get() == 2);
    verify(unit.dimension().kg().get() == -8);    


    // verify we can take a sophisticated nested contract
    // like Unit and apply it to a different base element type
    unit = (Unit)make("<str is='obix:Unit' val='just messing with you'/>");
    verify(unit.getClass().getSuperclass() == Str.class);
    verify(unit.getStr().equals("just messing with you"));  
    
    // verify some facets defined in a contract
    History history = (History)make("<obj is='obix:History'/>");
    verify(history.count().getMin() == 0); 

    // verify op in/out defined in a contract    
    WritablePoint wpt = (WritablePoint)make("<obj is='obix:WritablePoint'/>");
    verify(wpt.writePoint().getIn(), new Contract("obix:WritePointIn"));
    verify(wpt.writePoint().getOut(), new Contract("obix:Point"));
    
    // verify mixins
    Enum pt = (Enum)make("<enum is='obix:Point obix:History' val='off'/>");
    verify(pt.get(), "off");
    verify(pt instanceof Point);
    verify(pt instanceof History);
    verify(((History)pt).count().get(), 0L);  
            
    // verify list of
    List list = (List)make("<list of='obix:Unit'><obj><str name='symbol' val='xyz'/></obj></list>");
    Obj[] listVals = list.list();                 
    verify(listVals.length == 1);
    verify(listVals[0] instanceof Unit);
    verify(((Unit)listVals[0]).symbol().get(), "xyz");
    
    // verify list of mixed
    list = (List)make("<list of='obix:Point'><obj/><int is='obix:Unit obix:Point'/></list>");
    listVals = list.list();                 
    verify(listVals.length == 2);
    verify(listVals[0].getClass().getSuperclass(), Obj.class);
    verify(listVals[0] instanceof Point);
    verify(listVals[1].getClass().getSuperclass(), Int.class);
    verify(listVals[1] instanceof Int);
    verify(listVals[1] instanceof Point);
    verify(listVals[1] instanceof Unit);

    // verify an instance which covariantly changes it's 
    // element type from an inherited contract    
    PointAlarm ptAlarm = (PointAlarm)make("<obj is='obix:PointAlarm'><real name='alarmValue' val='-1'/></obj>");
    verify(ptAlarm instanceof Alarm);
    verify(ptAlarm.timestamp() instanceof Abstime);
    verify(ptAlarm.alarmValue() instanceof Real);
    verify(ptAlarm.alarmValue().getReal() == -1d);
    verify(ptAlarm.alarmValue().getName(), "alarmValue");
    verify(ptAlarm.alarmValue().getParent() == ptAlarm); 

    // verify a ref with an is attribute
    Ref ref = (Ref)make("<ref is='obix:Point'/>");
    verify(ref.getIs(), new Contract("obix:Point"));

    // verify a Lobby contract with ref to About
    Lobby lobby = (Lobby)make("<obj is='obix:Lobby'/>");
    verify(lobby.about() instanceof Ref);
    verify(lobby.about().getIs(), new Contract("obix:About"));
    verify(!(lobby.about() instanceof About));                
    
    // verify null is cleared implicitly with val
    AckAlarm alarm = (AckAlarm)make("<obj is='obix:AckAlarm'/>");
    verify(alarm.ackTimestamp().isNull());
    verify(alarm.ackTimestamp().getMillis() == 0);
    verify(alarm.ackUser().isNull());
    verify(alarm.ackUser().get().equals(""));
    alarm = (AckAlarm)make("<obj is='obix:AckAlarm'>" +
      "<abstime name='ackTimestamp' val='2005-09-21T13:14:02.12Z'/>" +
      "<str name='ackUser' val='Fred'/></obj>");
    verify(!alarm.ackTimestamp().isNull());
    verify(alarm.ackTimestamp().getMillis() > 0);
    verify(!alarm.ackUser().isNull());
    verify(alarm.ackUser().get().equals("Fred"));    
    
    // verify using primitive contracts
//    Real real = (Real)make("<obj is='obix:real'/>");
//    real = (Real)make("<real is='obix:real'/>");
    Exception ex = null;
    try { make("<int is='obix:real'/>"); } catch(Exception e) { ex = e; }
    verify(ex != null);  
  }

////////////////////////////////////////////////////////////////
// Decode
////////////////////////////////////////////////////////////////  

  public Obj make(String xml)
    throws Exception
  {
    return make(xml, true);
  }

  public Obj make(String xml, boolean useContracts)
    throws Exception
  {            
    ObixDecoder decoder = new ObixDecoder(new ByteArrayInputStream(xml.getBytes("UTF-8")));
    decoder.setUseContracts(useContracts);
    return decoder.decodeDocument();
  }
  
}

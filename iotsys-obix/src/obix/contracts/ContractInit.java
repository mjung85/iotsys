package obix.contracts;

import obix.*;

/**
 * ContractInit
 *
 * @author    obix.tools.Obixc
 * @creation  24 May 06
 * @version   $Revision$ $Date$
 */
public class ContractInit
{

  public static void init()
  {
    ContractRegistry.put("obix:BadUriErr", "obix.contracts.BadUriErr");
    ContractRegistry.put("obix:UnsupportedErr", "obix.contracts.UnsupportedErr");
    ContractRegistry.put("obix:PermissionErr", "obix.contracts.PermissionErr");
    ContractRegistry.put("obix:Lobby", "obix.contracts.Lobby");
    ContractRegistry.put("obix:About", "obix.contracts.About");
    ContractRegistry.put("obix:BatchIn", "obix.contracts.BatchIn");
    ContractRegistry.put("obix:BatchOut", "obix.contracts.BatchOut");
    ContractRegistry.put("obix:Read", "obix.contracts.Read");
    ContractRegistry.put("obix:Write", "obix.contracts.Write");
    ContractRegistry.put("obix:Invoke", "obix.contracts.Invoke");
    ContractRegistry.put("obix:Nil", "obix.contracts.Nil");
    ContractRegistry.put("obix:Range", "obix.contracts.Range");
    ContractRegistry.put("obix:Weekday", "obix.contracts.Weekday");
    ContractRegistry.put("obix:Month", "obix.contracts.Month");
    ContractRegistry.put("obix:Dimension", "obix.contracts.Dimension");
    ContractRegistry.put("obix:Unit", "obix.contracts.Unit");
    ContractRegistry.put("obix:WatchService", "obix.contracts.WatchService");
    ContractRegistry.put("obix:Watch", "obix.contracts.Watch");
    ContractRegistry.put("obix:WatchIn", "obix.contracts.WatchIn");
    ContractRegistry.put("obix:WatchInItem", "obix.contracts.WatchInItem");
    ContractRegistry.put("obix:WatchOut", "obix.contracts.WatchOut");
    ContractRegistry.put("obix:Point", "obix.contracts.Point");
    ContractRegistry.put("obix:WritablePoint", "obix.contracts.WritablePoint");
    ContractRegistry.put("obix:WritePointIn", "obix.contracts.WritePointIn");
    ContractRegistry.put("obix:History", "obix.contracts.History");
    ContractRegistry.put("obix:HistoryRecord", "obix.contracts.HistoryRecord");
    ContractRegistry.put("obix:HistoryFilter", "obix.contracts.HistoryFilter");
    ContractRegistry.put("obix:HistoryQueryOut", "obix.contracts.HistoryQueryOut");
    ContractRegistry.put("obix:HistoryRollupIn", "obix.contracts.HistoryRollupIn");
    ContractRegistry.put("obix:HistoryRollupOut", "obix.contracts.HistoryRollupOut");
    ContractRegistry.put("obix:HistoryRollupRecord", "obix.contracts.HistoryRollupRecord");
    ContractRegistry.put("obix:Alarm", "obix.contracts.Alarm");
    ContractRegistry.put("obix:StatefulAlarm", "obix.contracts.StatefulAlarm");
    ContractRegistry.put("obix:AckAlarm", "obix.contracts.AckAlarm");
    ContractRegistry.put("obix:AckAlarmIn", "obix.contracts.AckAlarmIn");
    ContractRegistry.put("obix:AckAlarmOut", "obix.contracts.AckAlarmOut");
    ContractRegistry.put("obix:PointAlarm", "obix.contracts.PointAlarm");
    ContractRegistry.put("obix:AlarmSubject", "obix.contracts.AlarmSubject");
    ContractRegistry.put("obix:AlarmFilter", "obix.contracts.AlarmFilter");
    ContractRegistry.put("obix:AlarmQueryOut", "obix.contracts.AlarmQueryOut");
  }

}

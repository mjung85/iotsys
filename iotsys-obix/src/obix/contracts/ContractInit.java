package obix.contracts;

import obix.*;

/**
 * ContractInit
 * 
 * @author obix.tools.Obixc
 * @creation 24 May 06
 * @version $Revision$ $Date$
 */
public class ContractInit
{

	public static void init()
	{
		ContractRegistry.put(BadUriErr.CONTRACT, "obix.contracts.BadUriErr");
		ContractRegistry.put(UnsupportedErr.CONTRACT, "obix.contracts.UnsupportedErr");
		ContractRegistry.put(PermissionErr.CONTRACT, "obix.contracts.PermissionErr");
		ContractRegistry.put(Lobby.CONTRACT, "obix.contracts.Lobby");
		ContractRegistry.put(About.CONTRACT, "obix.contracts.About");
		ContractRegistry.put(BatchIn.CONTRACT, "obix.contracts.BatchIn");
		ContractRegistry.put(BatchOut.CONTRACT, "obix.contracts.BatchOut");
		ContractRegistry.put(Read.CONTRACT, "obix.contracts.Read");
		ContractRegistry.put(Write.CONTRACT, "obix.contracts.Write");
		ContractRegistry.put(Invoke.CONTRACT, "obix.contracts.Invoke");
		ContractRegistry.put(Nil.CONTRACT, "obix.contracts.Nil");
		ContractRegistry.put(Range.CONTRACT, "obix.contracts.Range");
		ContractRegistry.put(Weekday.CONTRACT, "obix.contracts.Weekday");
		ContractRegistry.put(Month.CONTRACT, "obix.contracts.Month");
		ContractRegistry.put(Dimension.CONTRACT, "obix.contracts.Dimension");
		ContractRegistry.put(Unit.CONTRACT, "obix.contracts.Unit");
		ContractRegistry.put(WatchService.CONTRACT, "obix.contracts.WatchService");
		ContractRegistry.put(Watch.CONTRACT, "obix.contracts.Watch");
		ContractRegistry.put(WatchIn.CONTRACT, "obix.contracts.WatchIn");
		ContractRegistry.put(WatchInItem.CONTRACT, "obix.contracts.WatchInItem");
		ContractRegistry.put(WatchOut.CONTRACT, "obix.contracts.WatchOut");
		ContractRegistry.put(Point.CONTRACT, "obix.contracts.Point");
		ContractRegistry.put(WritablePoint.CONTRACT, "obix.contracts.WritablePoint");
		ContractRegistry.put(WritePointIn.CONTRACT, "obix.contracts.WritePointIn");
		ContractRegistry.put(History.CONTRACT, "obix.contracts.History");
		ContractRegistry.put(HistoryRecord.CONTRACT, "obix.contracts.HistoryRecord");
		ContractRegistry.put(HistoryFilter.CONTRACT, "obix.contracts.HistoryFilter");
		ContractRegistry.put(HistoryQueryOut.CONTRACT, "obix.contracts.HistoryQueryOut");
		ContractRegistry.put(HistoryRollupIn.CONTRACT, "obix.contracts.HistoryRollupIn");
		ContractRegistry.put(HistoryRollupOut.CONTRACT, "obix.contracts.HistoryRollupOut");
		ContractRegistry.put(HistoryRollupRecord.CONTRACT, "obix.contracts.HistoryRollupRecord");
		ContractRegistry.put(HistoryAppendIn.CONTRACT, "obix.contracts.HistoryAppendIn");
		ContractRegistry.put(HistoryAppendOut.CONTRACT, "obix.contracts.HistoryAppendOut");
		ContractRegistry.put(Alarm.CONTRACT, "obix.contracts.Alarm");
		ContractRegistry.put(StatefulAlarm.CONTRACT, "obix.contracts.StatefulAlarm");
		ContractRegistry.put(AckAlarm.CONTRACT, "obix.contracts.AckAlarm");
		ContractRegistry.put(AckAlarmIn.CONTRACT, "obix.contracts.AckAlarmIn");
		ContractRegistry.put(AckAlarmOut.CONTRACT, "obix.contracts.AckAlarmOut");
		ContractRegistry.put(PointAlarm.CONTRACT, "obix.contracts.PointAlarm");
		ContractRegistry.put(AlarmSubject.CONTRACT, "obix.contracts.AlarmSubject");
		ContractRegistry.put(AlarmFilter.CONTRACT, "obix.contracts.AlarmFilter");
		ContractRegistry.put(AlarmQueryOut.CONTRACT, "obix.contracts.AlarmQueryOut");
	}

}

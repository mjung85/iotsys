jQuery(document).ready(function($) {
	
	$("#rec-stop").click(function() {
		var stop = $("#stop");
		stop.val("true");
		$("#reqform").submit();
	});
	
	$("#rec-del").click(function() {
		var stop = $("#del");
		stop.val("true");
		$("#reqform").submit();
	});
	
	$("#start-obj").click(function() {
		var type = $("#type");
		type.val("obj");
		$("#reqform").submit();
	});
	
	$("#start-watch").click(function() {
		var type = $("#type");
		type.val("watch");
		$("#reqform").submit();
	});
	
	$("#start-history").click(function() {
		var type = $("#type");
		type.val("history");
		$("#reqform").submit();
	});
	
	var history_records = $(".history-record");
	history_records.each(function(index) {
		var numeric =$(this).find(".numeric");
		if(numeric) {
			var timestamp = $(this).find(".timestamp");
			numeric_entries.push([timestamp.text(), parseInt(numeric.text())]);
		}
	});
		
	if(numeric_entries.length > 0) {
		$("#history-plot").css("display", "block");
		$.jqplot("history-plot", [numeric_entries], {
			title:'Datapoint Values',
			axes:{
				xaxis:{
					renderer:$.jqplot.DateAxisRenderer,
					tickOptions: { formatString:'%Y-%m-%d %H:%M:%S' },
					tickInterval:'30 seconds' 
				}  
			}
		});
	}
 
});

var numeric_entries = new Array();





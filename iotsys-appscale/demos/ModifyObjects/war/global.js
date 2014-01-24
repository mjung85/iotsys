jQuery(document).ready(function($) {
	$("#form-send").click(function() {
		$("#muri").val("");
		$("#val").val("");
		$("#reqform").submit();		
	});
		
	$("#objectlist > li").each(function(index) {
		var href = $(this).find("button.href");
		href.click(function() {
			var duri = $("#duri");
			duri.val($(this).text());
			$("#muri").val("");
			$("#val").val(""); 
			$("#reqform").submit();		
		});
		
		var numpoint = $(this).find("input.numpoint");
		if(numpoint) {
			var pointsubmit = $(this).find("button.numpointsubmit");
			pointsubmit.click(function() {
				var uri = $(this).parent().parent().find("button.href").text();
				var val = $(this).parent().parent().find("input.numpoint").val();
				var muri = $("#muri");
				muri.val(uri);
				var value = $("#val");
				value.val(val); 
				$("#reqform").submit();
			});
		}
		
		var boolpoint = $(this).find("input.boolpoint");
		if(boolpoint) {
			var pointon = $(this).find("button.bool-on");
			pointon.click(function() {
				var uri = $(this).parent().parent().find("button.href").text();
				var val = "true";
				var muri = $("#muri");
				muri.val(uri);
				var value = $("#val");
				value.val(val);
				$("#reqform").submit();
			});
			var pointoff = $(this).find("button.bool-off");
			pointoff.click(function() {
				var uri = $(this).parent().parent().find("button.href").text();
				var val = "false";
				var muri = $("#muri");
				muri.val(uri);
				var value = $("#val");
				value.val(val);
				$("#reqform").submit();
			});
		}
		
		var operation = $(this).find("input.operation-in");
		if(operation) {
			var op_invoke = $(this).find("button.operation-invoke");
			op_invoke.click(function() {
				var uri = $(this).parent().parent().find("button.href").text();
				var val = $(this).parent().parent().find("input.operation-in").val();
				var muri = $("#muri");
				muri.val(uri);
				var value = $("#val");
				value.val(val);
				$("#reqform").submit();
			});
		}
	});
	
});
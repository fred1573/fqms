$(document).ready(function(e){
	$('#search_input').on('keydown', function(event){
		var e = event || window.event;
		if(e && e.keyCode==13){
			$("#input").val($("#search_input").val());
			jumpPage(1);
		}
	});
	$("#search_submit").bind("click", function(){
		$("#input").val($("#search_input").val());
		jumpPage(1);
	});
	
});


function searchWithDate(){
	$("#selectDate").val($("#funcReport-date").text());
	search();
}

function search() {
	$("#mainForm").submit();
}

function jumpPage(pageNo){
	var date = ($("#funcReport-date").text() != '')?$("#funcReport-date").text().trim():"";
	$("#selectDate").val(date);
	$("#pageNo").val(pageNo);
	search();
}
$(document).ready(function(e){
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
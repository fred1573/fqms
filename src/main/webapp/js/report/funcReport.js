$(document).ready(function(e){
});


function searchWithDate(){
	$("#selectDate").val($("#funcReport-date").text());
	search();
}

function search() {
	$("#mainForm").submit();
}
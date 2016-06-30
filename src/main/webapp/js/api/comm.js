$(document).ready(function(e){
	$("div.header button").bind("click", function(event){
		var $btn = $(event.currentTarget);
		url = $btn.attr("urls");
		window.location = url;
	});
	
});

function search() {
	$("#mainForm").submit();
}

function refresh(){
	location.reload();
}


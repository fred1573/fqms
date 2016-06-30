$(document).ready(function(e){
	$("table.kz-table td.oper a.del-button").bind("click", function(){
		$btn = $(this);
		var id = $btn.parent().parent().attr("newsId");
		showRemoveDiv(id);
	});
});

function showRemoveDiv(id){
	$("#remove_news_div a.audit-pass-button").attr("onclick", "removeNews("+id+")");
	showAlertDialog('remove_news_div');
}

function removeNews(id){
	var url = ctx+"/news/remove"+generateUrlEndStr();
	var data = {
			"id": id
	};
	$.post(url, data).done(function(json) {
		if(json.status == 200){
			closeAlertDialog('remove_news_div');
			$("table.kz-table tr[newsId="+id+"]").remove();
		}else{
			alert(json.result);
		}
	});
}
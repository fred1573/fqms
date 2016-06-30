$(document).ready(function(e){
	$("#edit_submit").bind("click", function(){
		var content = editor.text();
		saveNews();
	});
	
});

function saveNews(){
	var url = ctx+'/news/addOrEdit'+generateUrlEndStr();
	var title = $("#title").val();
	if(isEmpty(title)){
		alert("文章标题不能为空!");
		return;
	}
//	var content = editor.text();
	var content = $("#content").val();
	console.log(content);
	if(isEmpty(content)){
		alert("文章内容不能为空!");
		return;
	}
	var data = {
			"id": $("#news_id").val(),
			"title": title,
			"content": content
	};
	$.post(url, data).done(function(json) {
		if(json.status == 200){
			window.location.href = ENUM_URL.LIST;
		}else{
			alert(json.result);
		}
	});
}


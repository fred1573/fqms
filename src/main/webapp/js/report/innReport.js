$(document).ready(function(){
	$("input[name='hasBrand']").bind("click",function(){
		var brandVal=false;
		if($(this).is(":checked")){
			brandVal=true;
		}
		$.post($("#updateBrandUrl").val(),{innId:$(this).val(),brand:brandVal},function(retVal){
			if(retVal.status!=200){
				alert("服务繁忙，请稍后重试！");
			}
		});
	});
	$('#search_input').on('keydown', function(event){
		var e = event || window.event;
		if(e && e.keyCode==13){
			$("#innName").val($("#search_input").val());
			jumpPage(1);
		}
	});
	$("#search_submit").bind("click", function(){
		$("#innName").val($("#search_input").val());
		jumpPage(1);
	});
});

function search() {
	$("#mainForm").submit();
}

function jumpPage(pageNo){
	$("#pageNo").val(pageNo);
	search();
}
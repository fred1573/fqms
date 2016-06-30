//显示审核窗并传id值入
function audit(id) {
	showCoverBox();
	$("#audit").fadeIn();
	$("#id").val(id);
}


//关闭审核通过并刷新页面
function ens() {
	$("#tanchukuang2").fadeOut();
	location.reload();
}

//关闭审核拒绝并刷新页面
function ref() {
	$("#tanchukuang3").fadeOut();
	location.reload();
}

//取消审核客栈
function quit() {
	$("#audit").fadeOut();
	hideCoverBox();
}

//初始化
$(document).ready(function(e) {
	//回车响应
	$("#innName").keydown(function(event) { 
		if (event.keyCode == 13) {
			$("#sysUserCode_button").click();
		}
	});
	//安回车键触发提交表单
	$("#sysUserCode_button").bind("click",function(e) {
		searchWithName();
	});
});

//提交表单
function searchWithName() {
	search();
}

//确定重置密码
function ensure() {
	var id = $("#id").val();
	$.get("inn/updatePassword",{id:id},function(obj) {
		alert("您的重置密码是:"+obj.result);
		$("#tanchukuang").fadeOut();
		hideCoverBox();
	});
}

//取消重置密码
function cancel() {
	$("#tanchukuang").fadeOut();
}
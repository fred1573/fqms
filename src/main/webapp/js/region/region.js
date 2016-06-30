$(function() {
	jQuery("#addInnForm").validationEngine('attach', {promptPosition : "bottomRight"});
	$("#innName").focus();
	//选择使用状况
	$("#useStatu").change(function() {
		var s = $(this).val();
		$("#useStatus").val(s);
		$("#condition_button").click();
	});
	var status = $("#useStatus").val();
	//$("#useStatu").find("option[html='"+status+"']").attr("selected",true);
	$("#useStatu").val(status);
});

//提交删除信息
function ensure() {
	var id = $("#deleteId").val();
	var status = $("#status").val();
	if(status=="") { 
		status="周庄"
			}
	$.post(ctx+"/region/delete",{id:id,status:status},function(obj) {
		alert("成功删除");
		close(2);
		setTimeout(ref,500);
	});	
}

//删除客栈
function deleted(id) {
	$("#deleteId").val(id);
	$("#regionName").html($("#"+id).val());
	show(2);
}

//添加客栈
function add() {
	var phone = $("#phone").val();
	var status = $("#status").val();
	var status = $("#status").val();
	if(status==""){
		status="周庄";
	}
	if($("#addInnForm").validationEngine('validate')) {
		$.post(ctx+"/region/add",{phone:phone,status:status},function(obj) {
			if(obj.status==400) {
				var admin = obj.result;
				if(admin!=null) {
					if(admin.status==1) { 
						alert("该用户尚未通过审核");
					}else if(admin.status==3) {
						alert("该用户审核被拒绝");
					}else if(admin.status==4) {
						alert("该用户已被删除");
					}
				}else {
					alert("该账号所对应的客栈不存在");
				}
			}else if(obj.status==200) {
				alert("成功用户添加");
				close(1);
				 $("#phone").val("");
				setTimeout(ref,500);
			}else if(obj.status==401) {
				alert("该用户已经在该区域");
			}else{
				alert("数据插入有误");
			}		
		});
	}
}

//刷新页面
function ref() {
	location.reload();	
}


//显示框
function show(status) {
	if(status=='1'){
		$("#add").fadeIn();
		$("#phone").focus();
	}else {
		$("#delete").fadeIn();
	}
	showCoverBox();
}

//关闭显示框
function close(status) {
	if(status=='1'){
		$("#add").fadeOut();
		$(".formErrorContent").click();
	}else {
		$("#delete").fadeOut();	
	}
	hideCoverBox();
}

//初始化
$(document).ready(function(e) {	
	//按键查询
	$("#innName").keydown(function(event) { 
		if (event.keyCode == 13) {
			$("#condition_button").click();
		}
	});
	
	//点击查询
	$("#condition_button").bind("click",function(e) {
		var status = $("#status").val();
		if(status==""){
			$("#status").val("周庄");
		}
			searchWithName();
	});
	var status = $("#status").val();
	if(status=="")status="周庄";
	$.post(ctx+"/region/count",{status:status},function(obj) {		
		$("#content").html("已安装番茄来了客栈管理系统的客栈共有<font color='red'>"+obj.message+"家</font>"+"，已经连续3天未使用过系统的客栈共有<font color='red'>"+obj.status+"家</font>");
	});
});

//显示信息
$(function() {
	var $selectArea = $(".select-area");
	var $selectUlLi = $selectArea.find("ul li");
	$selectUlLi.each(function() {
		$(this).mousedown(function() {
			var status = $(this).html();
			$("#status").val(status);
			$.post(ctx+"/region/count",{status:status},function(obj) {		
				$("#content").html("已安装番茄来了客栈管理系统的客栈共有<font color='red'>"+obj.message+"家</font>"+"，已经连续3天未使用过系统的客栈共有<font color='red'>"+obj.status+"家</font>");
			});
		});
		
	});	
});

//获取状态值产讯
function status(status) {
	$("#status").val(status);
	$.post(ctx+"/region/count",{status:status},function(obj) {		
		$("#content").html("已安装番茄来了客栈管理系统的客栈共有<font color='red'>"+obj.message+"家</font>"+"，已经连续3天未使用过系统的客栈共有<font color='red'>"+obj.status+"家</font>");
	});
}

//提交条件查询
function searchWithName() {
	search();
}

//分页
function jumpPage(pageNo) {
	var status = $("#status").val();
	if(status==""){$("#status").val("周庄");}
	$("#pageNo").val(pageNo);
	$("#mainForm").submit();
}

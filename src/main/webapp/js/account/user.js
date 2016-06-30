$(document).ready(function(e){
	$("#roles li").bind("click",function(e) {
		searchWithRole(this.value,$(this).text());
	});
	
	$("#sysUserCode_button").bind("click",function(e) {
		searchWithName();
	});
	
	jQuery("#inputForm").validationEngine('attach', {promptPosition : "bottomRight"});
	
	//新增用户键盘监听
	$("#add_user_div").keydown(function(event){ 
		if (event.keyCode == 13)
			$("#add_user_button").click( );
	});
	
	$("#user_roles_ol li").bind("click",function(e) {
		$("#user_roleId_input").val(this.value);
	});
	
	$("#add_user_button").on("click",function(){
		if($("#inputForm").validationEngine('validate')){
			$.post("user/checkLoginName", $("#inputForm").serialize()).done(function(obj) {
				if(obj.result){
					$.post($("#inputForm").attr("action"), $("#inputForm").serialize()).done(function(obj) {
						if(obj.status == 200){
							closeByDivId("add_user_div");
							art.dialog.tips('新增/修改用户成功！',AUTO_RELOAD_TIME/1000);
							setInterval("reload()",AUTO_RELOAD_TIME);
					  	}else{
					  		showErrorTip(obj.result,"add_user_div");
					  	}
					});
			  	}else{
			  		$('#sysUserCode').validationEngine('showPrompt', '该用户账号已存在，请重新输入！', 'load');
	        		$("#sysUserCode").focus();
	        		return false;
			  	}
			});
		}
	});
	
	$("#bound_user_region_button").on("click",function(){
		var regionIds = getCheckedValue("innRegionIds");
		if(regionIds == ""){
			showErrorTip("至少选择一个区域！","region_div");
		}else{
			$.get("user/boundUserRegion/"+$("#user_id_input").val()+"/"+regionIds).done(function(obj) {
				if(obj.status == 200){
					closeByDivId("region_div");
					art.dialog.tips('新增/修改用户区域成功！',AUTO_RELOAD_TIME/1000);
			  	}else{
			  		showErrorTip(obj.result,"region_div");
			  	}
			});
		}
	});
	
});

function searchWithName(){
	search();
}

function searchWithRole(roleId,roleText){
	$("#roleId").val(roleId);
	$("#roleName").val(roleText);
	search();
}

function reload(){
	window.location.reload();
}

/**
 * 添加/修改用户
 * @param {Object} id
 */
function toAddOrModifyUser(id) {
	$.get("user/input?id="+id).done(function(obj) {
		if(obj.status == 200){
			showCoverBox();
			var user = obj.result;
			if(id != ""){
				$("#sysUserPwd_li").hide();
				$("#sysUserPwd").attr("name","");
				$("#mobile_li").show();
				$("#user_roleId_input").val(user.roleList[0].id);
	    		$("#roleName_span").text(user.roleList[0].sysRoleName);
			}else{
				$("#sysUserPwd_li").show();
				$("#sysUserPwd").attr("name","sysUserPwd");
				$("#mobile_li").hide();
				$("#user_roleId_input").val($("#user_roleId_init").val());
	    		$("#roleName_span").text($("#user_roleName_init").val());
			}
			$("#id").val(id);
			$("#oldLoginCode").val(user.sysUserCode);
			$("#sysUserCode").val(user.sysUserCode);
    		$("#sysUserName").val(user.sysUserName);
    		$("#sysUserPwd").val(user.sysUserPwd);
    		$("#mobile").val(user.mobile);
	  	}
	});
	$("#add_user_div").fadeIn();
	$("#sysUserCode").focus();
}

function toChangeUserStatusById(id,status,outerObj){
	$.get("user/changeUserStatus/"+id+"/"+status).done(function(obj) {
		if(obj.status == 200){
			if(status == 0){
				$(outerObj).text("启用");
				$(outerObj).attr("onclick","toChangeUserStatusById("+id+",1,this)");
			}else{
				$(outerObj).text("禁用");
				$(outerObj).attr("onclick","toChangeUserStatusById("+id+",0,this)");
			}
			art.dialog.tips(status==0?"禁用用户成功！":"启用用户成功！",AUTO_RELOAD_TIME/1000);
	  	}else{
	  		alert(obj.result);
	  	}
	});
}

function deleteUser(id){
	art.dialog.confirm('你确认删除此用户吗？', function(){
		$.get("user/delete/"+id).done(function(obj) {
			if(obj.status == 200){
				art.dialog.tips("删除用户成功！",AUTO_RELOAD_TIME/1000);
				$("#user_ul_"+id).fadeOut();
		  	}else{
		  		alert(obj.result);
		  	}
		});
	});
}

function showRegion(id){
	$.get("user/getRegion?id="+id).done(function(obj) {
		if(obj.status == 200){
			showCoverBox();
			$("#user_id_input").val(id);
			setAllNoChecked("innRegionIds");
			console.debug(1);
			$.each(obj.result, function(i,id){
				$("#region_"+id).prop("checked",true);
    		});
			if(obj.result.length == $("input[name=innRegionIds]").size()){
				$("#checkAll").prop("checked",true);
			}else{
				$("#checkAll").prop("checked",false);
			}
			$("#region_div").fadeIn();
	  	}else{
	  		alert(obj.result);
	  	}
	});
}
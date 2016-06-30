$(document).ready(function(e){
	jQuery("#inputForm").validationEngine('attach', {promptPosition : "bottomRight"});
	
	//新增用户键盘监听
	$("#add_role_div").keydown(function(event){ 
		if (event.keyCode == 13)
			$("#add_role_button").click( );
	});
	
	$("#add_role_button").on("click",function(){
		if($("#inputForm").validationEngine('validate')){
			if($("#parentId").val() == "")
				$('#parentName').validationEngine('showPrompt', '上级角色不能为空！', 'error');
			else{
				//首先判断角色账号是否唯一
				$.post("role/checkRoleName",$("#inputForm").serialize()).done(function(obj) {
					if(obj.result){
						$.post($("#inputForm").attr("action"), $("#inputForm").serialize()).done(function(obj) {
							if(obj.status == 200){
								closeByDivId("add_role_div");
								art.dialog.tips('新增/修改部门成功！',AUTO_RELOAD_TIME/1000);
								setInterval("reload()",AUTO_RELOAD_TIME);
						  	}else{
						  		showErrorTip(obj.result,"add_role_div");
						  	}
						});
				  	}else{
				  		$('#sysRoleName').validationEngine('showPrompt', '该角色名称已存在，请重新输入！', 'load' , '1');
		        		$("#sysRoleName").focus();
		        		return false;
				  	}
				});
			}
		}
	});
	
	$("#bound_role_auth_button").on("click",function(){
		var authorityIds = getCheckedValue("sysAuthorityIds");
		if(authorityIds == ""){
			showErrorTip("至少选择一个权限！","authority_div");
		}else{
			$.get("role/boundRoleAuth/"+$("#role_id_input").val()+"/"+authorityIds).done(function(obj) {
				if(obj.status == 200){
					closeByDivId("authority_div");
					art.dialog.tips('新增/修改部门权限成功！',AUTO_RELOAD_TIME/1000);
					setInterval("reload()",AUTO_RELOAD_TIME);
			  	}else{
			  		showErrorTip(obj.result,"authority_div");
			  	}
			});
		}
	});
	
});

window.onload = function(){
	var setting = {
		view: {
			showIcon: false
		},
		data: {
			simpleData: {
				enable: true
			}
		},
		callback: {
			onClick: onClick
		}
	};

	var zNodes = JSON.parse($("#roleTreeData").val());
	
	$.fn.zTree.init($("#roleTree"), setting, zNodes);

	function onClick(event, treeId, treeNode, clickFlag) {
		$("#parentId").val(treeNode.id);
		$("#parentName").val(treeNode.name);
		$("#inputForm").validationEngine('hide');
		if($("#id").val()!=""){
			recursionCheckChildRole();
		}
	}
	
	//修改角色时,不允许所选中的上级角色为当前所修改角色的下级角色
	function recursionCheckChildRole(){
		$.get("role/recursionCheckChildRole?checkedId="+$("#parentId").val()+"&id="+$("#id").val()).done(function(obj) {
			if(!obj.result){
				art.dialog.alert("当前角色的上级角色不能为自己而且不能为其下级角色的角色！");
        		$("#parentId").val("");
        		$("#parentName").val("");
        		return false;
		  	}
		});
	}
}

function reload(){
	window.location.reload();
}

/**
 * 添加/修改部门
 * @param {Object} id
 */
function toAddOrModifyRole(id) {
	$.get("role/input?id="+id).done(function(obj) {
		if(obj.status == 200){
			showCoverBox();
			var role = obj.result.role;
			if(id == "1"){
				$("#parent_role_li").hide();
			}else{
				$("#parent_role_li").show();
			}
			$("#id").val(id);
			$("#oldRoleName").val(role.sysRoleName);
			$("#parentId").val(role.parentId);
    		$("#parentName").val(obj.result.parentName);
    		$("#sysRoleName").val(role.sysRoleName);
    		$("#status").val(role.status);
    		$("#rmk").val(role.rmk);
    		$("#add_role_div").fadeIn();
    		$("#sysRoleName").focus();
	  	}
	});
}

function toChangeRoleStatusById(id,status,outerObj){
	$.get("role/changeRoleStatus/"+id+"/"+status).done(function(obj) {
		if(obj.status == 200){
			if(status == 0){
				$(outerObj).text("启用");
				$(outerObj).attr("onclick","toChangeRoleStatusById("+id+",1,this)");
			}else{
				$(outerObj).text("禁用");
				$(outerObj).attr("onclick","toChangeRoleStatusById("+id+",0,this)");
			}
			art.dialog.tips(status==0?"禁用部门成功！":"启用部门成功！",AUTO_RELOAD_TIME/1000);
	  	}else{
	  		alert(obj.result);
	  	}
	});
}

function deleteRole(id){
	art.dialog.confirm('你确认删除此部门吗？', function(){
		$.get("role/delete/"+id).done(function(obj) {
			if(obj.status == 200){
				art.dialog.tips("删除部门成功！",AUTO_RELOAD_TIME/1000);
				setInterval("reload()",AUTO_RELOAD_TIME);
		  	}else{
		  		alert(obj.result);
		  	}
		});
	});
}

function showAuthority(id){
	$.get("role/getAuthority?id="+id).done(function(obj) {
		if(obj.status == 200){
			showCoverBox();
			$("#role_id_input").val(id);
			setAllNoChecked("sysAuthorityIds");
			$.each(obj.result, function(i,id){
				$("#auth_"+id).prop("checked",true);
    		});
			if(obj.result.length == $("input[name=sysAuthorityIds]").size()){
				$("#checkAll").prop("checked",true);
			}else{
				$("#checkAll").prop("checked",false);
			}
			$("#authority_div").fadeIn();
	  	}else{
	  		alert(obj.result);
	  	}
	});
}

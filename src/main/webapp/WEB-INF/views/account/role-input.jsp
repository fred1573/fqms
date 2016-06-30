<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<title>角色新增/修改</title>
<link rel="stylesheet" href="${ctx}/css/zTreeStyle/zTreeStyle.css" type="text/css">
<link rel="stylesheet" href="${ctx}/css/formValidator/validationEngine.jquery.css" type="text/css"/>
<script src="${ctx}/js/ztree/jquery.ztree.core-3.2.min.js" type="text/javascript"></script>
<script src="${ctx}/js/formValidator/jquery.validationEngine-zh_CN.js" type="text/javascript"></script>
<script src="${ctx}/js/formValidator/jquery.validationEngine.js" type="text/javascript"></script>
<script type="text/javascript">
	jQuery(document).ready(function(){
		jQuery("#inputForm").validationEngine('attach', {promptPosition : "bottomRight"});
	});

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

	var zNodes = ${roleTreeData};

	function onClick(event, treeId, treeNode, clickFlag) {
		$("#parentId").val(treeNode.id);
		$("#parentName").val(treeNode.name);
		$("#inputForm").validationEngine('hide');
		if($("#id").val()!=""){
			recursionCheckChildRole();
		}
	}
	
	$(document).ready(function(){
		$.fn.zTree.init($("#roleTree"), setting, zNodes);
	});
	
	//修改角色时,不允许所选中的上级角色为当前所修改角色的下级角色
	function recursionCheckChildRole(){
		$.ajax({
	        type: "post",
	        dataType: "text",
	        url: "role!recursionCheckChildRole.action?checkedId="+$("#parentId").val()+"&id="+$("#id").val(),
	        success: function(data){
	        	if(data == 'false'){
	        		art.dialog.alert("当前角色的上级角色不能为自己而且不能为其下级角色的角色！");
	        		$("#parentId").val("");
	        		$("#parentName").val("");
	        		return false;
	        	}
	        }
	    });
	}
	
	function toSubmit() {
		if($("#inputForm").validationEngine('validate')){
			if($("#parentId").val() == "")
				$('#parentName').validationEngine('showPrompt', '上级角色不能为空！', 'error');
			else{
				//首先判断角色账号是否唯一
				$.ajax({
			        type: "post",
			        dataType: "text",
			        //url: "role!checkRoleCode.action?roleCode="+$("#sysRoleCode").val()+"&oldRoleCode="+$("#oldRoleCode").val(),
			        url: "role!checkRoleName.action?roleName="+$("#sysRoleName").val()+"&oldRoleName="+$("#oldRoleName").val(),
			        success: function(data){
			        	if(data == 'false'){
			        		//$('#sysRoleCode').validationEngine('showPrompt', '该角色CODE已存在，请重新输入！', 'load');
			        		//$("#sysRoleCode").focus();
			        		$('#sysRoleName').validationEngine('showPrompt', '该角色名称已存在，请重新输入！', 'load' , '1');
			        		$("#sysRoleName").focus();
			        		return false;
			        	}else{
			        		//账号唯一通过后提交
			        		$("#inputForm").ajaxSubmit({
			        	        type: "post",
			        	        dataType: "text",
			        	        success: function(data){
			        	        	if(data == 'true'){
			        	        		art.dialog.data("flag", data);
			        	        		closeWin();
			        	        	}
			        	        	else
			        	        		art.dialog.alert('保存失败！'+data);
			        	        }
			        	    });
			        	}
			        }
			    });
			}
		}
	}
	
	function closeWin(){
		art.dialog.close();
	}
</script>
</head>
<body>
	<div id="main">
		<form id="inputForm" action="role!save.action" method="post">
		<input type="hidden" id="id" name="id" value="${id}">
		<!-- <input type="hidden" id="oldRoleCode" name="oldRoleCode" value="${sysRoleCode}"> -->
		<input type="hidden" id="oldRoleName" name="oldRoleName" value="${sysRoleName}">
			<div class="eject">
				<div class="tablnr">
					<table width="100%" border="0" cellpadding="0" cellspacing="4" class="basictable">
						<!-- 
						<tr>
							<td width="130" align="right" class="txtright">角色CODE：</td>
							<td><input type="text" id="sysRoleCode" name="sysRoleCode" value="${sysRoleCode}" class="validate[required,custom[onlyLetterNumber],minSize[3],maxSize[25]] ipt"></td>
							<td valign="top" rowspan="6"><div id="roleTree" class="ztree"></div></td>
						</tr>
						 -->
						<tr>
							<td width="130" align="right" class="txtright">角色名称<font color="red">*</font>：</td>
							<td width="300" >
								<input type="text" id="sysRoleName" name="sysRoleName" value="${sysRoleName}" class="validate[required,maxSize[10]] ipt" maxlength="10">
							</td>
							<td valign="top" rowspan="8"><div id="roleTree" class="ztree"></div></td>
						</tr>
						<c:if test="${id != 1}">
						<tr>
							<td width="130" align="right" class="txtright">上级角色<font color="red">*</font>：</td>
							<td>
								<input type="hidden" id="parentId" name="parentId" value="${parentId}"/>
								<input type="text" id="parentName" name="parentName" value="${parentName}" readonly="readonly" class="ipt" title="请在右侧角色树中选择">
							</td>
						</tr>
						</c:if>
						<c:if test="${id == 1}">
							<input type="hidden" id="parentId" name="parentId" value="${parentId}"/>
						</c:if>
						<tr>
							<td align="right" class="txtright">是否启用<font color="red">*</font>：</td>
							<td><sw:select type="isuse_flag" id="status" name="status" value="${status}" showValue="false"></sw:select></td>
						</tr>
						<tr>
							<td align="right" class="txtright">角色描述：</td>
							<td><textarea id="rmk" name="rmk" class="validate[maxSize[200]]" style="width:300px;">${rmk}</textarea></td>
						</tr>
					</table>
				</div>
			</div>
			<div class="eject" align="center">
				<input type="button" id="button1" name="button1" value="提交" class="btn04" onclick="toSubmit()"/>
				<input type="button" id="button2" name="button2" value="关闭" class="btn04" onclick="closeWin()"/>
			</div>
		</form>
	</div>
</body>
</html>
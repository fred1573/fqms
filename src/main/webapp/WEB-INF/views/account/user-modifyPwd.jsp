<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<title>用户密码修改</title>
<link rel="stylesheet" href="${ctx}/css/formValidator/validationEngine.jquery.css" type="text/css"/>
<script src="${ctx}/js/formValidator/jquery.validationEngine-zh_CN.js" type="text/javascript"></script>
<script src="${ctx}/js/formValidator/jquery.validationEngine.js" type="text/javascript"></script>
<script type="text/javascript">
	jQuery(document).ready(function(){
		jQuery("#inputForm").validationEngine('attach', {promptPosition : "centerRight"});
	});

	function toSubmit() {
		if($("#inputForm").validationEngine('validate')){
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
	
	function closeWin(){
		art.dialog.close();
	}
</script>
</head>
<body>
	<div id="main">
		<form id="inputForm" action="user!saveNewPwd.action" method="post">
		<input type="hidden" id="id" name="id" value="${id}">
			<div class="eject">
				<div class="tablnr">
					<table width="100%" border="0" cellpadding="0" cellspacing="4" class="basictable">
						<tr height="65px">
							<td width="130" align="right" class="txtright">旧密码：</td>
							<td><input type="password" id="oldSysUserPwd" name="oldSysUserPwd" class="validate[required,custom[username],minSize[6],maxSize[18]] ipt" maxlength="18"></td>
						</tr>
						<tr height="65px">
							<td width="130" align="right" class="txtright">新密码：</td>
							<td><input type="password" id="sysUserPwd" name="sysUserPwd" class="validate[required,custom[username],minSize[6],maxSize[18]] ipt" maxlength="18"></td>
						</tr>
						<tr height="65px">
							<td width="130" align="right">确认新密码：</td>
							<td><input type="password" id="sysUserPwd_confirm" name="sysUserPwd_confirm" class="validate[required,equals[sysUserPwd]] ipt" maxlength="18"></td>
						</tr>
					</table>
				</div>
			</div>
			<div class="eject" align="center">
				<input type="button" id="button1" name="button1" value="提交" class="btn04" onclick="toSubmit()"/>
				<input type="button" id="button1" name="button1" value="关闭" class="btn04" onclick="closeWin()"/>
			</div>
		</form>
	</div>
</body>
</html>
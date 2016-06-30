<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<title>用户绑定角色</title>
<link rel="stylesheet" href="${ctx}/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script src="${ctx}/js/common/form.js" type="text/javascript"></script>
<script src="${ctx}/js/ztree/jquery.ztree.core-3.2.min.js" type="text/javascript"></script>
<script src="${ctx}/js/ztree/jquery.ztree.excheck-3.2.min.js" type="text/javascript"></script>
<script type="text/javascript">
	var setting = {
		view: {
			showIcon: false
		},
		check: {
			enable: true
		},
		data: {
			simpleData: {
				enable: true
			}
		}
	};

	var zNodes = ${roleTreeData};
	
	var zTree;

	$(document).ready(function(){
		$.fn.zTree.init($("#roleTree"), setting, zNodes);
		zTree = $.fn.zTree.getZTreeObj("roleTree");
		//是否关联父节点或者子节点
		zTree.setting.check.chkboxType = { "Y":"", "N":"" };
	});
	
	/**
	 * 获取选中的用户id
	 */
	function getCheckedUserIdsValue(type){
		var checkObj = document.getElementsByName("checkbox");
		var checkedValue = "";
		for(var i=0;i<checkObj.length;i++){
			if(checkObj[i].checked){
				checkedValue += checkObj[i].value+",";
			}
		}
		if (checkedValue == "") {
			if(type=="user")
				art.dialog.alert('请至少选择一个用户！');
			else if(type=="role")
				art.dialog.alert('请至少选择一个角色！');
			return false;
		}
		else {
			checkedValue = checkedValue.substr(0,checkedValue.length-1);
		}
		return checkedValue;
	}
	
	/**
	 * 通过用户id获取其相应角色树
	 */
	function toGetRoleByUserIds(){
		var checkedValue = getCheckedUserIdsValue();
		if(checkedValue){
			$("#roleTree").html("<img src=\"../images/loading.gif\"/>");
			$.ajax({
		        type: "post",
		        dataType: "text",
				url: "user-role!getRoleByIds.action?userIds="+checkedValue,
		        success: function(data){
					$.fn.zTree.init($("#roleTree"), setting, zNodes);
					zTree = $.fn.zTree.getZTreeObj("roleTree");
					//是否关联父节点或者子节点
					zTree.setting.check.chkboxType = { "Y":"", "N":"" };
					if (data != "") {
						var roleIds = data.split(",");
						if(roleIds!=""){
							for (var i=0;i<roleIds.length;i++) {
								var node = zTree.getNodeByParam("id",roleIds[i], null);
								if(node != null)
									zTree.checkNode(node, true, false, false);
							}
						}
		            }
		        },
				complete: function(XMLHttpRequest, textStatus){
					$("#roleTree").show();
				}
		    });
		}else{
			$.fn.zTree.init($("#roleTree"), setting, zNodes);
			zTree = $.fn.zTree.getZTreeObj("roleTree");
			//是否关联父节点或者子节点
			zTree.setting.check.chkboxType = { "Y":"", "N":"" };
		}
	}

	/**
	 * 获取选中的角色id
	 */
	function getCheckedRoleIdsValue(type){
		var selectedRoleIds = "";
		zTree = $.fn.zTree.getZTreeObj("roleTree");
		var nodes = zTree.getCheckedNodes(true);
		if (nodes != "") {
			for (var i=0;i<nodes.length;i++) {
				selectedRoleIds += nodes[i].id+",";
			}
			selectedRoleIds = selectedRoleIds.substr(0,selectedRoleIds.length-1);
		}else{
			if(type=="userRole")
				art.dialog.alert('请至少在角色树中选择一个角色！');
			return false;
		}
		return selectedRoleIds;
	}

	/**
	 * 绑定角色
	 */
	function toBoundRole(){
		var checkedUserIds = getCheckedUserIdsValue("user");
		var checkedRoleIds = getCheckedRoleIdsValue("userRole");
		if(checkedUserIds && checkedRoleIds){
			//alert(checkedUserIds+"--"+checkedRoleIds);
			if(confirm("是否确认绑定角色?")){
				window.location.href="user-role!boundRole.action?userIds="+checkedUserIds+"&roleIds="+checkedRoleIds;
			}
		}else{
			$.fn.zTree.init($("#roleTree"), setting, zNodes);
			zTree = $.fn.zTree.getZTreeObj("roleTree");
			//是否关联父节点或者子节点
			zTree.setting.check.chkboxType = { "Y":"", "N":"" };
		}
	}
</script>
</head>
<body>
	<div class="main">
		<p class="position">当前位置：系统管理 > 用户角色绑定</p>
		<div class="btnbar">
			<span style="float:left">
				<security:authorize ifAnyGranted="ROLE_绑定角色">
					<input type="button" id="boundRole" name="boundRole" value="绑定角色" class="btn04" onclick="toBoundRole()"/>
				</security:authorize>
	        </span>
      	</div>
		<form id="mainForm" action="user-role.action" method="post">
			<input type="hidden" name="page.pageNo" id="pageNo" value="${page.pageNo}" />
			<input type="hidden" name="page.orderBy" id="orderBy" value="${page.orderBy}" />
			<input type="hidden"vbname="page.order" id="order" value="${page.order}" />
			<div id="message">
				<s:actionmessage theme="custom" cssClass="success" />
			</div>
			<div class="seachbar">
				<p class="topline"></p>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right">用户账号： <label for="textfield2"></label></td>
						<td>
							<input type="text" id="sysUserCode" name="filter_LIKES_sysUserCode" value="${param['filter_LIKES_sysUserCode']}" maxlength="40"/>
						</td>
						<td align="right">用户名：</td>
						<td>
							<input type="text" id="sysUserName" name="filter_LIKES_sysUserName" value="${param['filter_LIKES_sysUserName']}" maxlength="20"/>
						</td>
						<td align="right">用户手机：</td>
						<td width="117">
							<input type="text" id="mobile" name="filter_LIKES_mobile" value="${param['filter_LIKES_mobile']}" maxlength="11"/>
						</td>
						<td align="right">用户状态： <label for="textfield3"></label></td>
						<td>
							<sw:select type="isuse_flag" id="status" name="filter_EQS_status" value="${param['filter_EQS_status']}" blank="true" showValue="false"></sw:select>
						</td>
						<td align="right">
							<input class="btn02" type="submit" name="button0" id="button0" value="查询">
							<input class="btn02" type="button" name="button1" id="button1" value="重置" onclick="clearForm('mainForm')">
						</td>
					</tr>
				</table>
			</div>
			<table class="list" width="100%" border="0" cellspacing="1" cellpadding="0">
				<thead>
					<tr>
						<td colspan="6">选择需绑定的用户</td>
						<td>角色树</td>
					</tr>
				</thead>
				<tr>
					<td style="border:1px solid #b1ddfa; background:url(${ctx}/images/tablebg.png) repeat-x bottom #eff6fe; white-space:nowrap;"><input type="checkbox" id="checkAll"/></td>
					<td style="border:1px solid #b1ddfa; background:url(${ctx}/images/tablebg.png) repeat-x bottom #eff6fe; white-space:nowrap;">用户账号</td>
					<td style="border:1px solid #b1ddfa; background:url(${ctx}/images/tablebg.png) repeat-x bottom #eff6fe; white-space:nowrap;">用户姓名</td>
					<td style="border:1px solid #b1ddfa; background:url(${ctx}/images/tablebg.png) repeat-x bottom #eff6fe; white-space:nowrap;">用户手机</td>
					<td style="border:1px solid #b1ddfa; background:url(${ctx}/images/tablebg.png) repeat-x bottom #eff6fe; white-space:nowrap;">用户状态</td>
					<td style="border:1px solid #b1ddfa; background:url(${ctx}/images/tablebg.png) repeat-x bottom #eff6fe; white-space:nowrap;">用户角色</td>
					<td rowspan="11" width="30%" valign="top" align="left"><div id="roleTree" class="ztree"></div></td>
				</tr>
				<s:iterator value="page.result">
				<tr>
					<td><input type="checkbox" name="checkbox" id="checkbox" value="${id}" onclick="toGetRoleByUserIds()"></td>
					<td title="${sysUserCode}"><tags:truncate value="${sysUserCode}" length="15"></tags:truncate>&nbsp;</td>
					<td title="${sysUserName}"><tags:truncate value="${sysUserName}" length="15"></tags:truncate></td>
					<td>${mobile}&nbsp;</td>
					<td><sw:write type="isuse_flag" value="${status}"></sw:write>&nbsp;</td>
					<td title="${roleNames}">
						<s:if test="roleNames.length()>15">${fn:substring(roleNames,0,15)}...</s:if>
						<s:else>${roleNames}</s:else> 
					</td>
				</tr>
				</s:iterator>
			</table>
			<p class="btnbar2">
				第${page.pageNo}页, 共${page.totalPages}页 
				<a href="javascript:jumpPage(1)">首页</a>
				<s:if test="page.hasPre">
					<a href="javascript:jumpPage(${page.prePage})">上一页</a>
				</s:if>
				<s:if test="page.hasNext">
					<a href="javascript:jumpPage(${page.nextPage})">下一页</a>
				</s:if>
				<a href="javascript:jumpPage(${page.totalPages})">末页</a>
			</p>
		</form>
	</div>
</body>
</html>
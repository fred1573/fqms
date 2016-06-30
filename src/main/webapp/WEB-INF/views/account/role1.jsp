<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<title>角色管理</title>
<script src="${ctx}/js/common/form.js" type="text/javascript"></script>
<script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<script type="text/javascript">
/**
 * 添加/修改角色
 * @param {Object} id
 */
function toAddRole(id) {
	var url = "role!input.action";
	var title = "新增";
	if(id!=""){
		url += "?id="+id;
		title = "修改";
 	}
	art.dialog.open(url, {
		title: title+'角色', 
		width: 1020, 
		height: 340,
		close: function(){
				if(art.dialog.data("flag") == "true"){
					art.dialog.tips('保存成功!',2);
					window.location.href="role.action";
				}
				art.dialog.data("flag","");
	    	}
		}, false);
}
 
/**
 * 获取选中的角色id
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
 * 启用角色
 */
function toEnableRole(){
	var checkedValue = getCheckedUserIdsValue("role");
	if(checkedValue)
		if(confirm("是否确认启用角色?"))
			window.location.href="role!enableRole.action?ids="+checkedValue;
}

/**
 * 禁用角色
 */
function toForbiddenRole(){
	var checkedValue = getCheckedUserIdsValue("role");
	if(checkedValue)
		if(confirm("是否确认禁用角色?"))
			window.location.href="role!forbiddenRole.action?ids="+checkedValue;
}

/**
 * 通过用户id启用角色
 */
function toEnableRoleById(id){
	if(confirm("是否确认启用角色?"))
		window.location.href="role!enableRole.action?ids="+id;
}

/**
 * 通过用户id禁用角色
 */
function toForbiddenRoleById(id){
	if(confirm("是否确认禁用角色?"))
		window.location.href="role!forbiddenRole.action?ids="+id;
}

/**
 * 删除角色
 */
function toDelRole(){
	var checkedValue = getCheckedUserIdsValue("role");
	if(checkedValue)
		if(confirm("是否确认删除角色?"))
			window.location.href="role!delete.action?ids="+checkedValue;
}
</script>
</head>
<body>
	<div class="main">
		<p class="position">当前位置：系统管理 > 角色管理</p>
		<div class="btnbar">
			<span style="float:left">
				<!-- 
				<security:authorize ifAnyGranted="ROLE_启用角色">
					<input type="button" id="button2" name="button2" value="启用角色" class="btn04" onclick="toEnableRole()"/>
				</security:authorize>
				<security:authorize ifAnyGranted="ROLE_禁用角色">
					<input type="button" id="button3" name="button3" value="禁用角色" class="btn04" onclick="toForbiddenRole()"/>
				</security:authorize>
				 -->
				<security:authorize ifAnyGranted="ROLE_新增角色">
					<input type="button" id="button1" name="button1" value="新增角色" class="btn04" onclick="toAddRole('')"/>
				</security:authorize>
				<security:authorize ifAnyGranted="ROLE_删除角色">
					<input type="button" id="button2" name="button2" value="删除角色" class="btn04" onclick="toDelRole()"/>
				</security:authorize>
	        </span>
      	</div>
		<form id="mainForm" action="role.action" method="post">
			<input type="hidden" name="page.pageNo" id="pageNo" value="${page.pageNo}" />
			<input type="hidden" name="page.orderBy" id="orderBy" value="${page.orderBy}" />
			<input type="hidden" name="page.order" id="order" value="${page.order}" />
			<div id="message">
				<s:actionmessage theme="custom" cssClass="success" />
			</div>
			<div class="seachbar">
				<p class="topline"></p>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<!-- 
						<td align="right">角色CODE： <label for="textfield2"></label></td>
						<td>
							<input type="text" id="sysRoleCode" name="filter_EQS_sysRoleCode" value="${param['filter_EQS_sysRoleCode']}"/>
						</td>
						-->
						<td align="right">角色名称：</td>
						<td>
							<input type="text" id="sysRoleName" name="filter_LIKES_sysRoleName" value="${param['filter_LIKES_sysRoleName']}" maxlength="10"/>
						</td>
						<td align="right">角色状态：</td>
						<td>
							<sw:select type="isuse_flag" id="status" name="filter_EQS_status" value="${param['filter_EQS_status']}" blank="true" showValue="false"></sw:select>
						</td>
						<td align="right">
							<input class="btn02" type="submit" name="button" id="button" value="查询">
							<input class="btn02" type="button" name="button" id="button" value="重置" onclick="clearForm('mainForm')">
						</td>
					</tr>
				</table>
			</div>
			<table class="list" width="100%" border="0" cellspacing="1" cellpadding="0">
				<thead>
					<tr>
						<td><input type="checkbox" id="checkAll"/></td>
						<!-- 
						<td>角色CODE</td>
						 -->
						<td>角色名称</td>
						<td>角色描述</td>
						<td>角色状态</td>
						<td>创建时间</td>
						<td>创建人</td>
						<td>操作</td>
					</tr>
				</thead>
				<s:iterator value="page.result">
				<tr>
					<td><input type="checkbox" name="checkbox" id="checkbox" value="${id}"></td>
					<!--
					<td>${sysRoleCode}&nbsp;</td>
					-->
					<td title="${sysRoleName}">${sysRoleName}&nbsp;</td>
					<td title="${rmk}">
						<s:if test="rmk.length()>15">${fn:substring(rmk,0,15)}...</s:if>
						<s:else>${rmk}</s:else> 
					</td>
					<td><sw:write type="isuse_flag" value="${status}"></sw:write>&nbsp;</td>
					<td><fmt:formatDate value="${createTime}" pattern="yyyy-MM-dd HH:ss"/></td>
					<td>${createUserCode}&nbsp;</td>
					<td>
					<security:authorize ifAnyGranted="ROLE_修改角色">
						<a href="javascript:toAddRole(${id})">修改</a>&nbsp;
					</security:authorize>
					
					<c:if test="${status == 1}">
						<security:authorize ifAnyGranted="ROLE_禁用角色">
							<a href="javascript:toForbiddenRoleById(${id})">禁用</a>
						</security:authorize>
					</c:if>
					<c:if test="${status == 0}">
						<security:authorize ifAnyGranted="ROLE_启用角色">
							<a href="javascript:toEnableRoleById(${id})">启用</a>
						</security:authorize>
					</c:if>
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
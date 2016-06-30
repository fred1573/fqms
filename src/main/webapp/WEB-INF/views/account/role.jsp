<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<title>部门管理</title>
<link rel="stylesheet" href="${ctx}/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script src="${ctx}/js/ztree/jquery.ztree.all-3.5.min.js" type="text/javascript"></script>
<script src="${ctx}/js/common/form.js" type="text/javascript"></script>
<script src="${ctx}/js/account/role.js" type="text/javascript"></script>
<script>
	$(function(){
		$('#search_submit').val('');
	})
</script>
</head>
<body>
    <!--右边内容区域-->
    <div class="container-right">
    	<div class="header">
        	<h1>部门管理</h1>
            <div class="header-button-box">
                <input type="hidden" id="roleTreeData" value='${roleTreeData}' />
                <security:authorize ifAnyGranted="ROLE_部门管理">
                	<a href="javascript:toAddOrModifyRole('')" class="red-button-add add">添加部门</a>
                </security:authorize>
                <form id="mainForm" action="${ctx}/account/role" method="post" style="float: left; margin-top: 18px;">
	                <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}" />
					<input type="hidden" name="orderBy" id="orderBy" value="${page.orderBy}" />
					<input type="hidden" name="order" id="order" value="${page.order}" />
					<div class="search-box">
	                	<input name="filter_LIKES_sysRoleName" value="${param['filter_LIKES_sysRoleName']}" type="text" class="search" placeholder="部门名称" maxlength="20">
	           			<input type="submit" class="search-button" id="search_submit">
	                </div>
                </form>
            </div>
        </div><!--end header-->
        <div class="content2">
        	<ul class="table-header">
            	<li class="percent20">拥有权限</li>
                <li class="percent20">部门名称</li>
                <li class="percent15">创建人</li>
                <li class="percent15">创建时间</li>
                <li class="percent15">权限管理</li>
                <li class="percent15">操作</li>
            </ul>
            <c:forEach items="${page.result}" var="t">
            <ul>
            	<li class="percent20" title="${t.authNames}">${t.authNames}</li>
                <li class="percent20">${t.sysRoleName}</li>
                <li class="percent15">${t.createUserCode}</li>
                <li class="percent15"><fmt:formatDate value="${t.createTime}" pattern="yyyy-MM-dd HH:ss"/></li>
                <li class="percent15">
                	<security:authorize ifAnyGranted="ROLE_部门管理">
                		<c:if test="${t.id != 1}"><a href="javascript:showAuthority(${t.id});">设定</a></c:if>
                	</security:authorize>		
                </li>
                <li class="percent15">
                	<security:authorize ifAnyGranted="ROLE_部门管理">
                	<c:if test="${t.id != 1}">
                		<c:if test="${t.status == 1}">
							<a href="javascript:void(0);" onclick="toChangeRoleStatusById(${t.id},0,this)">禁用</a>
						</c:if>
						<c:if test="${t.status == 0}">
							<a href="javascript:void(0);" onclick="toChangeRoleStatusById(${t.id},1,this)">启用</a>
						</c:if>
						<%--<a href="javascript:deleteRole(${t.id});">删除</a>--%>
                		<a href="javascript:toAddOrModifyRole('${t.id}')">修改</a>
                	</c:if>
                	</security:authorize>
               	</li>
            </ul>
            </c:forEach>
        </div><!--end content-->
        
		<tags:pagination page="${page}" paginationSize="5"/>
	</div><!--end container-right-->

	<!---------------------添加部门弹窗---------------------->
	<div id="add_role_div" class="center-box" style="display:none">
		<form:form id="inputForm" modelAttribute="role" action="${ctx}/account/role/save" method="post">
		<input type="hidden" id="id" name="id">
		<input type="hidden" id="oldRoleName" name="oldRoleName" value="">
		<div class=" center-box-in team2">
	        <a href="javascript:closeByClass('center-box')" class="close-window"></a>
	        <div id="roleTree" class="team2-left ztree"></div>
        	<div class="team2-right">
		        <h1>添加部门</h1>
		        <ul>
		        	<input type="hidden" id="parentId" name="parentId"/>
		        	<li id="parent_role_li">
		            	<dd>上级部门</dd>
						<input type="text" id="parentName" name="parentName" readonly="readonly" class="ipt" placeholder="请在左侧角色树中选择">
		            </li>
		        	<li><dd>部门名称</dd><input type="text" id="sysRoleName" name="sysRoleName" class="validate[required,maxSize[10]] ipt" maxlength="10"/></li>
		            <li>是否启用<sw:select type="isuse_flag" id="status" name="status" showValue="false"></sw:select></li>
		            <li><textarea id="rmk" name="rmk" class="validate[maxSize[200]]" placeholder="部门描述"></textarea></li>
		            <li>
		            	<a id="add_role_button" href="javascript:void(0);" class="green-button-ok">确&nbsp;&nbsp;&nbsp;&nbsp;定</a>
		            	<em class="error-tips" style="display:none"></em>
		            </li>
		        </ul>
			</div>
	    </div>
	    </form:form>
	</div>
	<!---------------------end----------------------------->
	
	<!---------------------权限管理弹窗---------------------->
	<div id="authority_div" class="center-box" style="display:none">
		<input type="hidden" id="role_id_input">
		<div class=" center-box-in root-set" >
	        <a href="javascript:closeByClass('center-box')" class="close-window"></a>
	        <h1>权限管理</h1>
	        <ul>
	        	<c:forEach items="${authoritys}" var="t">
	        	<li><input id="auth_${t.id}" name="sysAuthorityIds" type="checkbox" value="${t.id}" />${t.sysAuthorityName}</li>
	            </c:forEach>
	            <li class="select-button">
	            	<input id="checkAll" type="checkbox" onclick="checkAll('sysAuthorityIds',this);"/>全部选择
	            	<a id="bound_role_auth_button" href="javascript:void(0);" class="green-button-ok">确定</a>
	            	<em class="error-tips" style="display:none"></em>
	            </li>
	        </ul>
	    </div>
	</div>
	<!---------------------end----------------------------->
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.project.core.utils.springsecurity.SpringSecurityUtil" %>

<!---------------------修改密码弹窗---------------------->
<div id="modify_pwd_div" class="center-box" style="display:none">
	<form id="modifyPwdForm" action="<c:url value="/account/user/saveNewPwd"/> " method="post">
	<input type="hidden" name="userName" id="modifyPwd_userName" value="" />
	<div class="center-box-in user-reset-password">
    <a href="javascript:closeByClass('center-box')" class="close-window"></a>
    <h1>修改个人密码</h1>
    	<ul>
            <li><dd>新密码</dd><input type="password" id="modifyPwd_sysUserPwd" name="sysUserPwd" class="validate[required,custom[username],minSize[6],maxSize[18]] ipt" maxlength="18"/></li>
            <li><dd>请确认</dd><input type="password" id="modifyPwd_sysUserPwd_confirm" name="sysUserPwd_confirm" class="validate[required,equals[modifyPwd_sysUserPwd]] ipt" maxlength="18"></li>
            <li>
            	<a id="modify_pwd_button" href="#" class="green-button-ok">确&nbsp;&nbsp;&nbsp;&nbsp;定</a>
            	<em class="error-tips" style="display:none"></em>
            </li>
        </ul>
    </div>
    </form>
</div>
<!----------------------------------------------------->

<!--左边内容区-->
<div class="container-left">
	<div class="logo"></div>
    <div class="login-content"><!--登陆信息显示-->
    	<div class="login-photo"></div>
        <div class="login-phone">您好：<em><%=SpringSecurityUtil.getCurrentUserName()%></em></div>
        <a href="javascript:toModifyPwd('')" class="change-password">修改密码</a>
        <a href="<c:url value="/logout" />" class="logout">退出登录</a>
    </div><!--end login-content-->
    <div class="nav"><!--侧面导航-->
    	<ul>
            <security:authorize ifAnyGranted="ROLE_权限管理">
                <li <c:if test="${currentPage == 'role'}">class="select"</c:if>><a href="<c:url  value="/role/index"/> " class="team-management">权限管理</a></li>
            </security:authorize>

            <security:authorize ifAnyGranted="ROLE_报表统计">
                <li <c:if test="${currentPage == 'funcReport'}">class="select"</c:if>><a href="${ctx}/funcReport/index" class="inn-count">报表统计</a></li>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_结算信息">
            	<li <c:if test="${currentPage == 'bankInfo'}">class="select"</c:if>><a href="<c:url value="/inn/info"/> " class="inn-count">结算信息</a></li>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_房态切换">
                <li <c:if test="${currentPage == 'statusSwitch'}">class="select"</c:if>><a href="<c:url value="/roomStatus/list"/> " class="inn-count">房态切换</a></li>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_入住统计">
                <li <c:if test="${currentPage == 'regionRadio'}">class="select"</c:if>><a href="<c:url value="/report/active/region"/> " class="inn-count">入住统计</a></li>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_小站对账">
                <li <c:if test="${currentPage == 'xzCount'}">class="select"</c:if>><a href="<c:url value="/bill/count"/> " class="inn-count">小站对账</a></li>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_地区后台">
       		    <li <c:if test="${currentPage == 'region'}">class="select"</c:if>><a href="<c:url value="/region/welcome"/> " class="inn-hot">地区后台</a></li>
       		</security:authorize>
            <!-- 代销平台 -->
			<security:authorize ifAnyGranted="ROLE_代销平台">
                <li <c:if test="${currentPage == 'proxysale'}">class="select"</c:if>><a href="<c:url value="/proxysale/inn/del_list"/> " class="inn-hot">代销平台</a></li>
			</security:authorize>
			<!-- 代销订单管理-->
			<security:authorize ifAnyGranted="ROLE_订单管理">
			    <li <c:if test="${currentPage == 'proxySaleOrder'}">class="select"</c:if>><a href="<c:url value="/proxySaleOrder/list"/> " class="inn-hot">订单管理</a></li>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_代销数据">
                <li <c:if test="${currentPage == 'data'}">class="select"</c:if>><a href="<c:url value="/data/statistics"/> " class="inn-hot">代销数据</a></li>
            </security:authorize>
			<security:authorize ifAnyGranted="ROLE_财务结算">
                <li <c:if test="${currentPage == 'finance'}">class="select"</c:if>><a href="<c:url value="/finance/order/list"/> " class="inn-hot">财务结算</a></li>
			</security:authorize>
			<security:authorize ifAnyGranted="ROLE_直连订单">
                <li <c:if test="${currentPage == 'direct'}">class="select"</c:if>><a href="<c:url value="/direct/order"/> " class="inn-hot">直连订单</a></li>
			</security:authorize>
            <security:authorize ifAnyGranted="ROLE_运营活动">
                <li <c:if test="${currentPage == 'activity'}">class="select"</c:if>><a href="${ctx}/activity/list" class="inn-hot">运营活动</a></li>
            </security:authorize>
        </ul>
    </div><!--end nav-->
</div><!--end container-left-->

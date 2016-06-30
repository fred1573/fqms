<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ include file="/common/taglibs.jsp" %>
<%@ page import="org.slf4j.Logger,org.slf4j.LoggerFactory" %>

<%
	Throwable ex = null;
	if (exception != null)
		ex = exception;
	if (request.getAttribute("javax.servlet.error.exception") != null)
		ex = (Throwable) request.getAttribute("javax.servlet.error.exception");

	//记录日志
	Logger logger = LoggerFactory.getLogger("500.jsp");
	logger.error(ex.getMessage(), ex);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>500 - 系统内部错误</title>
<style type="text/css">
<!--
body {
	margin-top: 100px;
}
.CSS {
	font-size: 14px;
	line-height: 22px;
	color: #003300;
}
a:link {
	font-size: 14px;
	color: #333333;
	text-decoration: none;
}
a:visited {
	font-size: 14px;
	color: #333333;
	text-decoration: none;
}
a:hover {
	font-size: 14px;
	color: #333300;
	text-decoration: underline;
}
-->
</style>
</head>

<body>
	<div>
		<center>
			<table width="600" border="0" cellpadding="0" cellspacing="0">
			  <tr>
			    <td width="600" height="57"><img src="${ctx}/images/err_01.jpg" width="600" height="57"></td>
			  </tr>
			</table>
			<table width="600" border="0" cellpadding="0" cellspacing="1" bgcolor="D1CBD0">
			  <tr>
			    <td height="321" bgcolor="#F9F9F9">
				    <table width="585" height="232" border="0" align="center" cellpadding="0" cellspacing="0">
				      <tr>
				        <td width="162" rowspan="3"><img src="${ctx}/images/logo.png" width="113" height="124"></td>
				        <td width="423" height="42"><img src="${ctx}/images/err_02.jpg" width="302" height="28"></td>
				      </tr>
				      <tr>
				        <td height="76" class="CSS">&nbsp;&nbsp;&nbsp;&nbsp;系统发生内部错误，请稍后重试。
			            </td>
				      </tr>
				      <tr>
				        <td height="30" align="left" class="CSS"></td>
				      </tr>
				      <tr>
				        <td colspan="2"></td>
			          </tr>
				    </table>
			    </td>
			  </tr>
			</table>
			<br />
			<div class="tablnr" style="text-align: center;">
				<a href="javascript:window.history.back(-1);">返回</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="<c:url value="/logout"/>">退出登录</a>
			</div>
		</center>
	</div>
</body>
</html>

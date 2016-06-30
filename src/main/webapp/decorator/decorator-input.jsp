<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<%@ include file="/common/meta.jsp" %>
	<title><sitemesh:write property="title"/></title>
	<link href="${ctx}/css/style.css" type="text/css" rel="stylesheet"/>
	<script src="${ctx}/js/artDialog/artDialog.js?skin=blue" type="text/javascript"></script>
	<script src="${ctx}/js/artDialog/iframeTools.js" type="text/javascript"></script>
	<script src="${ctx}/js/common/jquery-1.9.1.min.js" type="text/javascript"></script>
	<sitemesh:write property="head"/>
</head>
<body>
<div>
	<sitemesh:write property="body"/>
</div>
</body>
</html>
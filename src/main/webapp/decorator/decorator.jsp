<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<%@ include file="/common/meta.jsp" %>
	<title><sitemesh:write property="title"/></title>
	<link type="image/x-icon" href="${ctx}/images/favicon.ico" rel="shortcut icon">
	<link href="${ctx}/css/style.css" type="text/css" rel="stylesheet"/>
	<link rel="stylesheet" href="${ctx}/css/formValidator/validationEngine.jquery.css" type="text/css"/>
	<script src="${ctx}/js/artDialog/artDialog.js?skin=opera" type="text/javascript"></script>
	<script src="${ctx}/js/artDialog/iframeTools.js" type="text/javascript"></script>
	<%-- <script src="${ctx}/js/common/jquery-1.9.1.min.js" type="text/javascript"></script> --%>
	<script src="http://assets.fanqiele.com/core/js/tomasky.core.all.js" type="text/javascript"></script>
	<script src="${ctx}/js/formValidator/jquery.validationEngine-zh_CN.js" type="text/javascript"></script>
	<script src="${ctx}/js/formValidator/jquery.validationEngine.js" type="text/javascript"></script>
	<script src="${ctx}/js/common/const.js" type="text/javascript"></script>
	<script src="${ctx}/js/common/app.js" type="text/javascript"></script>
	<script src="${ctx}/js/common/head.js" type="text/javascript"></script>
	<script src="${ctx}/js/common/tomato.js" type="text/javascript"></script>
	<sitemesh:write property="head"/>
</head>
<body class="bg1">
<!-- 无色透明遮照 -->
<div id="whole_mask" class="body-hidden-white" style="display: none;"></div>  
<!--黑色透明遮照--> 
<div id="fullbg" class="body-hidden-block" style="display: none;"></div>
<img id="loading_icon" src="${ctx}/images/webloading.gif" style="display: none;position: absolute; z-index: 99999; top: 49%; left: 49%" />

<div class="container">
	<%@ include file="/common/left.jsp" %>
	<sitemesh:write property="body"/>
</div>
</body>
</html>
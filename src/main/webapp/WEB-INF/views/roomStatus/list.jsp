<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>房态切换管理</title>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
    <script src="${ctx}/js/common/layer/layer.js" type="text/javascript"></script>
    <script src="${ctx}/js/roomStatus/roomStatus.js" type="text/javascript"></script>
</head>
<body>
<form action="${ctx}/roomStatus/list" method="post" id="mainForm">
    <input type="hidden" name="userCode" value="${userCode}"/>
</form>
<div class="container-right">
    <div class="header" style="height:30px;">
        <h1>房态切换管理</h1>

        <div style="left: 200px;top:15px;" class="header-button-box kc">
            <div class="search-box">
                <input type="text" id="userCode" maxlength="30" class="search" placeholder="注册账号"
                       <c:if test="${not empty userCode}">value="${userCode}"</c:if>/>
                <input type="button" id="search_submit" class="search-button">
            </div>
        </div>
    </div>
    <table class="kz-table" cellpadding="0">
        <thead>
        <tr>
            <th>客栈名称</th>
            <th>注册账号</th>
            <th>目前使用房态</th>
            <th>操作</th>
        </tr>
        </thead>
        <c:forEach items="${list}" var="adminType">
            <tr>
                <td>${adminType.innName}</td>
                <td>${adminType.userCode}</td>
                <td>${adminType.adminTypeStr}</td>
                <td>
                    <input type="hidden" value="${adminType.innId}"/>
                    <input type="hidden" value="${adminType.adminType}"/>
                    <a style="color: blue;" href="javascript:void(0);" class="modify">修改</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
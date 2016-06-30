<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
    <title>代销平台-客栈管理</title>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>

    <link href="${ctx}/css/jquery-ui.css" rel="stylesheet" type="text/css">
    <script src="${ctx}/js/proxysale/jquery-ui-1.10.1.min.js"></script>
    <link href="${ctx}/css/proxysale/jquery-ui-1.10.1.css" rel="stylesheet">
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>

</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/activity/list" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
    </form>
    <!--end header-->
    <div class="search-area" style="width:98%;">
        <%--<input type="text" class="search" placeholder="搜索区域">--%>
        <form action="/activity/list" method="post" style="float:right;">
            <input name="activityName" class="search" id="searchAreaName" placeholder="模糊搜索活动关键字" maxlength="20"
                   type="text">
            <input class="search-button" type="submit" value="&nbsp;">
        </form>
        <a class="red-button-add add" style="margin-right:5px;width: 100px;"
           href="${ctx}/activity/to/add?operate=add">新增活动</a>
    </div>

    <table class="kz-table" cellpadding="12">
        <tr>
            <th>活动名称及简介</th>
            <th>操作</th>
            <th>发布时间</th>
        </tr>
        <c:forEach items="${page.result}" var="activity">
            <tr>
                <td>${activity.activityName}</td>
                <td><a href="${ctx}/activity/to/add?operate=edit&id=${activity.id}">
                    <button <c:if test="${activity.status==0}">disabled</c:if>>编辑</button>
                </a>
                    <a href="${ctx}/activity/finish?id=${activity.id}">
                        <button <c:if test="${activity.status==0}">disabled</c:if>>结束</button>
                    </a>
                    <a href="${ctx}/activity/inn?activityId=${activity.id}">
                        <button>报名管理</button>
                    </a>
                </td>
                <td>${activity.publishTime}</td>
            </tr>
        </c:forEach>
    </table>
    <c:if test="${not empty page}">
        <tags:pagination page="${page}" paginationSize="15"/>
    </c:if>
</div>

</body>
</html>
<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>财务结算-结算账期</title>
    <link href="${ctx}/css/finance/period.css" rel="stylesheet">
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/finance/period.js" type="text/javascript"></script>
    <link rel="stylesheet" type="text/css" href="${ctx}/css/finance/period.css">
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/period/list" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
    </form>
    <div class="header">
        <%@ include file="navigation.jsp" %>
    </div>
    <div class="ctrl-bar" style="margin-bottom: 30px">
        获取订单(退房日期)为
        <input type="text" class="t-start" readonly onfocus="WdatePicker({dateFmt:'yyyy-MM-dd', onpicked:syncData})"
               value="<fmt:formatDate value="<%=new Date()%>" pattern="yyyy-MM-dd"/>">
        <span>至</span>
        <input type="text" class="t-end" readonly onfocus="WdatePicker({dateFmt:'yyyy-MM-dd', onpicked:syncData})"
               value="<fmt:formatDate value="<%=new Date()%>" pattern="yyyy-MM-dd"/>">
        为本次结算周期
        <button onclick="addPeriod()" id="grp">获取</button>
    </div>
    <table class="kz-table" cellpadding="8">
        <tr>
            <td>结账日期</td>
            <td>操作</td>
        </tr>
        <c:forEach items="${page.result}" var="period">
            <tr>
                <td>${period.settlementTime}</td>
                <td>
                    <c:choose>
                        <c:when test="${period.accountStatus eq '1'}">已锁定</c:when>
                        <c:when test="${period.accountStatus eq '0'}">修改账期</c:when>
                        <c:otherwise>未知</c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </table>
    <c:if test="${not empty page}">
        <tags:pagination page="${page}" paginationSize="10"/>
    </c:if>
</div>
<div id="progress">
    <div id="progressBox">
        <div id="progressBar">0%</div>
        <div id="progressText">0%</div>
    </div>
</div>
</body>
</html>

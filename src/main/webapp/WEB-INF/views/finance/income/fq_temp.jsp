<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>财务结算-进账核算</title>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/proxysale/jquery-ui-1.10.1.min.js"></script>
    <link href="${ctx}/css/proxysale/jquery-ui-1.10.1.css" rel="stylesheet">
    <script src="${ctx}/js/finance/finance.js" type="text/javascript"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/finance/manualOrders" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <input type="hidden" name="settlementTime" id="settlementTime" value="${settlementTime}"/>
        <input type="hidden" name="channelId" value="${channelId}"/>
    </form>
    <div class="header">
        <%@ include file="../navigation.jsp" %>
        <div style="left: 550px; top: 12px;" class="header-button-box duizhang kc">
            <div class="search-box">
                <input type="text" id="orderId" maxlength="50" class="search" placeholder="搜索订单号" value="${orderId}"/>
                <input type="button" id="search_order_submit" class="search-button">
            </div>
            账期 : <em style="color: red;font-size: 16px;font-weight: bold;">${settlementTime}</em>
        </div>
        <input type="hidden" name="channelName" value="${channelName}"/>
        <div class="header-button-box">
            <a class="red-button-add add" href="javascript:exportFqTemp()">导出Excel</a>
        </div>
    </div>
    <table class="kz-table" cellpadding="8">
        <thead>
        <th>城市</th>
        <th>客栈名称</th>
        <th>订单个数</th>
        <th>订单总金额</th>
        <th>分销商应结算金额</th>
        <th>番茄暂收</th>
        </tr>
        </thead>
        <c:forEach items="${page.result}" var="inn">
            <tr>
                <td>${inn.region}</td>
                <td>${inn.innname}</td>
                <td>${inn.count}</td>
                <td>${inn.total}</td>
                <td>${inn.channel}</td>
                <td>${inn.temp}</td>
            </tr>
        </c:forEach>
    </table>

    <c:if test="${not empty page}">
        <tags:pagination page="${page}" paginationSize="15"/>
    </c:if>
</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>财务结算-进账核算</title>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/finance/finance.js" type="text/javascript"></script>
    <script src="${ctx}/js/finance/income_detail.js" type="text/javascript"></script>
    <script src="${ctx}/js/proxysale/jquery-ui-1.10.1.min.js"></script>
    <link href="${ctx}/css/proxysale/jquery-ui-1.10.1.css" rel="stylesheet">
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/finance/income/innChannel" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <input type="hidden" name="settlementTime" value="${settlementTime}"/>
        <input type="hidden" name="channelId" value="${channelId}"/>
        <input type="hidden" name="innName"/>
    </form>
    <div class="header">
        <%@ include file="../navigation.jsp" %>
        <div style="left: 550px; top: 12px;" class="header-button-box duizhang kc">
            <div class="search-box">
                <input type="text" id="innName" maxlength="50" class="search" placeholder="客栈名称搜索"/>
                <input type="button" id="search_order_submit" class="search-button">
            </div>
            账期 : <em style="color: red;font-size: 16px;font-weight: bold;">${settlementTime}</em>
        </div>
        <div class="header-button-box">
            <form id="exportForm" action="${ctx}/finance/income/export/inns" method="post">
                <input type="hidden" name="settlementTime" value="${settlementTime}"/>
                <input type="hidden" name="channelId" value="${channelId}"/>
                <input type="hidden" name="channelName" value="${channelName}">
            </form>
            <a class="red-button-add add" href="javascript:exportIncomeInn()">导出Excel</a>


        </div>
    </div>
    <table class="kz-table" cellpadding="8">
        <thead>
        <th>城市</th>
        <th>客栈名称</th>
        <th>分销商应结算金额</th>
        <th>客栈赔付金额</th>
        <th>客栈赔付番茄承担金额</th>
        <th>客栈赔付番茄收入金额</th>
        <th>本期客栈退款金额</th>
        <th>本期番茄退收入金额</th>
        <th>本期番茄退往来金额</th>
        <th>后期番茄退往来金额</th>
        <th>分销商实际结算金额</th>
        <th>番茄正常订单收入</th>
        </tr>
        </thead>
        <c:forEach items="${page.result}" var="item">
            <tr>
                <td>${item.financeInnSettlementInfo.regionName }</td>
                <td><a href="/finance/income/detail?channelId=${channelId}&settlementTime=${settlementTime}&innId=${item.financeInnSettlementInfo.id}"><span style="color: dodgerblue;">${item.financeInnSettlementInfo.innName }</span></a></td>
                <td>${item.channelSettlementAmount }</td>
                <td>${item.innPayment}</td>
                <td>${item.fqBearAmount}</td>
                <td>${item.fqIncomeAmount}</td>
                <td>${item.refundAmount}</td>
                <td>${item.fqRefundCommissionAmount}</td>
                <td>${item.curFqRefundContacts}</td>
                <td>${item.aftFqRefundContacts}</td>
                <td>${item.channelRealSettlementAmount}</td>
                <td>${item.fqSettlementAmount}</td>
            </tr>
        </c:forEach>
    </table>
    <p>
        该账期内，共有 <em style="color:red;">${amountMap.inn_count }</em> 家客栈，<em style="color:red;">${amountMap.total }</em>
        个订单，分销商实际结算总金额为 <em
            style="color:red;"><c:if test="${amountMap.real_amount==null}">0</c:if>${amountMap.real_amount}</em> 元， 客栈实际结算金额为 <em style="color:red;">${amountMap.inn_settlement_amount}</em> 元。
    </p>
    <c:if test="${not empty page}">
        <tags:pagination page="${page}" paginationSize="15"/>
    </c:if>
</div>
</body>
</html>

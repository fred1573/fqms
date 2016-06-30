<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2016/3/9
  Time: 18:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>财务结算-出账核对</title>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <link href="${ctx}/css/finance/finance.css" rel="stylesheet">
    <script src="${ctx}/js/finance/special_balance/normal.js" type="text/javascript"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/finance/out/specialBalance/recovery" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <input type="hidden" name="innId" value="${innId}"/>
        <input type="hidden" name="channelId" value="${channelId}"/>
        <input type="hidden" name="settlementTime" value="${settlementTime}"/>
        <input type="hidden" name="channelOrderNo" value="${channelOrderNo}"/>
    </form>
    <div class="header">
        <%@ include file="../../navigation.jsp" %>
        <div style="left: 550px;top: 12px" class="header-button-box duizhang kc">
            <div class="search-box">
                <input type="text" id="channelOrderNo" maxlength="20" class="search" placeholder="模糊搜索订单号"
                       value="${channelOrderNo}"/>
                <input type="button" id="search_submit" class="search-button">
            </div>
            账期 : <em style="color: red;font-size: 16px;font-weight: bold;">${settlementTime}</em>
        </div>
        <div class="header-button-box">
            <a class="green-button-return add"
               href="${ctx}/finance/out/special/channel/detail?settlementTime=${settlementTime}&innId=${innId}">返回</a>


            <a class="red-button-add add" href="javascript:exportOut()">导出Excel</a>

            <form id="exportForm" action="${ctx}/finance/export/out" method="post">
                <input type="hidden" name="exportSettlementTime" value="${settlementTime}"/>
                <input type="hidden" name="exportInnId" value="${innId}"/>
            </form>
        </div>
    </div>
    <div class="header-sub-tab">
        <a href="${ctx}/finance/out/list?settlementTime=${settlementTime}&status=normal"
           <c:if test="${status=='normal'}">class="active" </c:if>>正常账单</a>
        <a href="${ctx}/finance/out/special/list?settlementTime=${settlementTime}&status=special"
           <c:if test="${status=='special'}">class="active" </c:if> >特殊结算</a>
        <a href="${ctx}/finance/out/delay/list?settlementTime=${settlementTime}&status=delay"
           <c:if test="${status=='delay'}">class="active" </c:if> >延期结算</a>
        <a href="${ctx}/finance/out/arrears?settlementTime=${settlementTime}&arrearsStatus=1&status=arrears"
           <c:if test="${status=='arrears'}">class="active" </c:if>>挂账</a>
        <div class="header-sub-tab1">
            <a
                    <c:if test="${statusType=='normal'}">class="active" </c:if>
                    href="${ctx}/finance/out/specialBalance/normal?settlementTime=${settlementTime}&channelId=${channelId}&channelOrderNo=${channelOrderNo}&innId=${innId}
              &statusType=normal&status=${status}">普通订单</a>
            <a
                    <c:if test="${statusType=='debit'}">class="active" </c:if>
                    href="${ctx}/finance/out/specialBalance/recovery?settlementTime=${settlementTime}&channelId=${channelId}&channelOrderNo=${channelOrderNo}&innId=${innId}
            &statusType=debit&status=${status}">赔付订单</a>
            <a
                    <c:if test="${statusType=='refund'}">class="active" </c:if>
                    href="${ctx}/finance/out/specialBalance/refund?settlementTime=${settlementTime}&channelId=${channelId}&channelOrderNo=${channelOrderNo}&innId=${innId}
            &statusType=refund&status=${status}">退款订单</a>
            <a
                    <c:if test="${statusType=='replenishment'}">class="active" </c:if>
                    href="${ctx}/finance/out/specialBalance/replenishment?settlementTime=${settlementTime}&channelId=${channelId}&channelOrderNo=${channelOrderNo}&innId=${innId}
            &statusType=replenishment&status=${status}">补款订单</a>
        </div>

    </div>


    <table class="kz-table" cellpadding="8">
        <thead>
        <tr>

            <th>
                价格模式
            </th>
            <th>分销商订单号</th>
            <th>OMS订单号</th>
            <th>预订人</th>
            <th>手机号码</th>
            <th>房型</th>
            <th>房间数</th>
            <th>入住-退房日期</th>
            <th>客栈订单金额</th>
            <th>番茄总调价</th>
            <th>分销商订单金额</th>
            <th>分销商扣赔付金额</th>
            <th>番茄承担客栈赔付</th>
            <th>番茄营业外收入</th>
            <th>客栈赔付金额</th>
            <th>产生周期</th>
        </tr>
        </thead>
        <c:forEach items="${page.result}" var="specialOrder">
            <tr>
                <td>${specialOrder.financeParentOrder.innerOrderMode}</td>
                <td>${specialOrder.financeParentOrder.channelOrderNo}</td>
                <td>${specialOrder.financeParentOrder.orderNo}</td>
                <td>${specialOrder.financeParentOrder.userName}</td>
                <td>${specialOrder.financeParentOrder.contact}</td>
                <td>${specialOrder.financeParentOrder.channelRoomTypeName}</td>
                <td>${specialOrder.financeParentOrder.roomTypeNums}</td>
                <td>${specialOrder.financeParentOrder.checkDate}</td>
                <td>${specialOrder.financeParentOrder.innAmount}</td>
                <td>${specialOrder.financeParentOrder.extraPrice}</td>
                <td>${specialOrder.financeParentOrder.channelAmount}</td>
                <td>${specialOrder.channelDebit}</td>
                <td>${specialOrder.fqBear}</td>
                <td>${specialOrder.fqIncome}</td>
                <td>${specialOrder.innPayment}</td>
                <td>${specialOrder.financeParentOrder.produceTime}</td>
            </tr>
        </c:forEach>
    </table>
    <p>
        <c:if test="${not empty innOrderCount and innOrderCount.orders !=0}">
            截止您所选时间段内，共有<em style="color:red;">${innOrderCount.orders}</em>个订单，分销商扣赔付总额为<em style="color:red;">${innOrderCount.recovery}</em>，客栈赔付总额为<em style="color:red;">${innOrderCount.innpay}</em>，番茄承担总额为<em style="color:red;">${innOrderCount.bear}</em>，番茄收入总额为<em
            style="color:red;">${innOrderCount.income}</em>
        </c:if>
    </p>
    <c:if test="${not empty page}">
        <tags:pagination page="${page}" paginationSize="15"/>
    </c:if>
</div>
</body>
</html>

<%--
  Created by IntelliJ IDEA.
  User: 番茄桑
  Date: 2015/8/20
  Time: 9:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="header-sub-tab">
    <a class="<c:if test="${subBtn == 'normal'}">active</c:if>" href="${ctx}/finance/order/detail?channelId=${channelId}&settlementTime=${settlementTime}">普通订单</a>
    <a class="<c:if test="${subBtn == 'debit'}">active</c:if>" href="${ctx}/finance/order/special/debit?channelId=${channelId}&settlementTime=${settlementTime}">赔付订单</a>
    <a class="<c:if test="${subBtn == 'refund'}">active</c:if>" href="${ctx}/finance/order/special/refund?channelId=${channelId}&settlementTime=${settlementTime}">退款订单</a>
    <a class="<c:if test="${subBtn == 'replenishment'}">active</c:if>" href="${ctx}/finance/order/special/replenishment?channelId=${channelId}&settlementTime=${settlementTime}">补款订单</a>
</div>
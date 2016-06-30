<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: 番茄桑
  Date: 2015/8/20
  Time: 9:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="header-sub-tab">
    <a class="<c:if test="${subBtn == 'normal'}">active</c:if>" href="${ctx}/finance/income/detail?channelId=${channelId}&settlementTime=${settlementTime}&innId=${innId}">普通订单</a>
    <a class="<c:if test="${subBtn == 'debit'}">active</c:if>" href="${ctx}/finance/income/debit?channelId=${channelId}&settlementTime=${settlementTime}&innId=${innId}">赔付订单</a>
    <a class="<c:if test="${subBtn == 'refund'}">active</c:if>" href="${ctx}/finance/income/refund?channelId=${channelId}&settlementTime=${settlementTime}&innId=${innId}">退款订单</a>
    <a class="<c:if test="${subBtn == 'replenishment'}">active</c:if>" href="${ctx}/finance/income/replenishment?channelId=${channelId}&settlementTime=${settlementTime}&innId=${innId}">补款订单</a>
</div>
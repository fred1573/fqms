<%--
  Created by IntelliJ IDEA.
  User: 番茄桑
  Date: 2015/8/20
  Time: 14:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>财务结算-进账核对</title>
    <link href="${ctx}/css/finance/finance.css" rel="stylesheet">
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/finance/income_detail.js" type="text/javascript"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
</head>
<body>
<div class="container-right">

       <div class="header">
           <div style="width: 100%;float: left;">
               <%@ include file="../navigation.jsp" %>
               <div style="left: 550px; top: 12px;" class="header-button-box duizhang kc">
                   <div class="search-box">
                       <input type="text" id="channelOrderNo" maxlength="20" class="search" placeholder="模糊搜索订单号"
                              value="${channelOrderNo}"/>
                       <input type="button" id="search_submit" class="search-button">
                   </div>
                   账期 : <em style="color: red;font-size: 16px;font-weight: bold;">${settlementTime}</em>
               </div>
           </div>
           <%@include file="sub_navigation.jsp"%>
           <div class="header-button-box">
               <form id="exportForm" action="${ctx}/finance/export/income" method="post">
                   <input type="hidden" name="exportSettlementTime" value="${settlementTime}"/>
                   <input type="hidden" name="exportChannelId" value="${channelId}"/>
               </form>
               <a class="green-button-return add" href="${ctx}/finance/income/innChannel?settlementTime=${settlementTime}&channelId=${channelId}">返回</a>

           </div>

           <%--<%@ include file="sub_navigation.jsp" %>--%>
       </div>
    <form id="mainForm" action="${ctx}/finance/income/refund" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <input type="hidden" name="settlementTime" value="${settlementTime}"/>
        <input type="hidden" name="channelId" value="${channelId}"/>
        <input type="hidden" name="innId" value="${innId}"/>
        <input type="hidden" name="channelOrderNo" value="${channelOrderNo}"/>
        <input type="hidden" name="priceStrategy" value="${priceStrategy}"/>
        <input type="hidden" name="auditStatus" value="${auditStatus}"/>
    </form>
    <table class="kz-table" cellpadding="8">
        <thead>
        <tr>
            <th>
                <select id="priceStrategy">
                    <option value="">价格模式</option>
                    <option value="1" <c:if test="${priceStrategy==1}">selected</c:if>>精品(活动)</option>
                    <option value="2" <c:if test="${priceStrategy==2}">selected</c:if>>普通(卖)</option>
                    <option value="3" <c:if test="${priceStrategy==3}">selected</c:if>>普通(底)</option>
                </select>
            </th>
            <th>订单号</th>
            <th>客人姓名</th>
            <th>手机号码</th>
            <th>房型</th>
            <th>房间数</th>
            <th>住离日期</th>
            <th>产生周期</th>
            <th>分销商订单金额</th>
            <th>分销商扣退款金额</th>
            <th>番茄总调价</th>
            <th>客栈退款金额</th>
            <th>番茄退佣金收入</th>
            <th>往来状态</th>
            <th>番茄退往来款</th>
            <th>
                <select id="auditStatus">
                    <option value="">核单</option>
                    <option value="0" <c:if test="${auditStatus eq '0'}">selected</c:if>>未核</option>
                    <option value="1" <c:if test="${auditStatus eq '1'}">selected</c:if>>已核成功</option>
                    <option value="2" <c:if test="${auditStatus eq '2'}">selected</c:if>>已核失败</option>
                </select>
            </th>
        </tr>
        </thead>
        <c:forEach items="${page.result}" var="order">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${order.financeParentOrder.priceStrategy == 1}">精品</c:when>
                        <c:when test="${order.financeParentOrder.priceStrategy == 2}">普通(卖)</c:when>
                        <c:when test="${order.financeParentOrder.priceStrategy == 3}">普通(底)</c:when>
                        <c:otherwise><em style="color:red;">error</em></c:otherwise>
                    </c:choose>

                </td>
                <td>${order.financeParentOrder.channelOrderNo}</td>
                <td>${order.financeParentOrder.userName}</td>
                <td>${order.financeParentOrder.contact}</td>
                <td>${order.financeParentOrder.channelRoomTypeName}</td>
                <td>${order.financeParentOrder.rooms}</td>
                <td>${order.financeParentOrder.checkDate}</td>
                <td>${order.financeParentOrder.produceTime}</td>
                <td>${order.financeParentOrder.totalAmount}</td>
                <td>${order.financeSpecialOrder.channelRefund}</td>
                <td>${order.financeParentOrder.extraPrice}</td>
                <td>${order.financeSpecialOrder.innRefund}</td>
                <td>${order.financeSpecialOrder.fqRefundCommission}</td>
                <td>
                    <c:choose>
                        <c:when test="${order.financeSpecialOrder.contactsStatus == 1}">后期(挂)</c:when>
                        <c:when test="${order.financeSpecialOrder.contactsStatus == 2}">本期(平)</c:when>
                        <c:otherwise>- - </c:otherwise>
                    </c:choose>
                </td>
                <th>${order.financeSpecialOrder.fqRefundContacts}</th>
                <td>
                    <c:choose>
                        <c:when test="${order.financeParentOrder.auditStatus == '0'}">未核</c:when>
                        <c:when test="${order.financeParentOrder.auditStatus == '1'}">已核成功</c:when>
                        <c:when test="${order.financeParentOrder.auditStatus == '2'}">已核失败</c:when>
                        <c:otherwise><em style="color:red;">没对哦</em></c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </table>

    <c:if test="${not empty orderStatistic}">
        <p>
            共有<em style="color:red;"> ${orderStatistic.order_num} </em>个订单，
            分销商扣退款总额<em style="color:red;"> ${orderStatistic.channel_refunds } </em> 元，
            客栈退款总额<em style="color:red;"> ${orderStatistic.inn_refunds } </em>元，
            番茄退佣金收入总额<em style="color:red;"> ${orderStatistic.fq_refund_commissions } </em>元，
            番茄退往来款总额<em style="color:red;"> ${orderStatistic.fq_refund_contactss } </em>元。
        </p>
    </c:if>
    <c:if test="${not empty page}">
        <tags:pagination page="${page}" paginationSize="15"/>
    </c:if>
</div>
</body>
</html>
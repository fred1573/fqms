<%--
  Created by IntelliJ IDEA.
  User: 番茄桑
  Date: 2015/8/13
  Time: 10:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>财务结算-进账核算</title>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/finance/finance.js" type="text/javascript"></script>
    <script src="${ctx}/js/proxysale/jquery-ui-1.10.1.min.js"></script>
    <link href="${ctx}/css/proxysale/jquery-ui-1.10.1.css" rel="stylesheet">
    <script src="${ctx}/js/common/ajaxfileupload.js" type="text/javascript"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/finance/income/list" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <input type="hidden" name="settlementTime" value="${settlementTime}"/>
        <input type="hidden" name="channelName" value="${channelName}"/>
        <input type="hidden" name="auditStatus" value="${auditStatus}"/>
        <input type="hidden" name="isArrival" value="${isArrival}"/>
    </form>
    <div class="header">
        <%@ include file="../navigation.jsp" %>
        <div style="left: 550px;top: 15px;" class="header-button-box duizhang kc">
            <div class="search-box">
                <input type="text" id="channelName" maxlength="20" class="search" placeholder="模糊搜索渠道名称"
                       value="${channelName}"/>
                <input type="button" id="search_submit" class="search-button">
            </div>
            <select id="settlementTime">
                <option value="">选择账期</option>
                <c:forEach var="period" items="${financeAccountPeriodList}">
                    <option value="${period.settlementTime}"
                            <c:if test="${settlementTime eq period.settlementTime}">selected</c:if>>${period.settlementTime}</option>
                </c:forEach>
            </select>
        </div>
        <div class="header-button-box">
            <form id="exportForm" action="${ctx}/finance/income/export/channels" method="post">
                <input type="hidden" name="exportSettlementTime" value="${settlementTime}"/>
                <input type="hidden" name="test" value="test">
            </form>
            <a class="red-button-add add" href="javascript:exportIncome()">导出Excel</a>
        </div>
    </div>
    <table class="kz-table" cellpadding="8">
        <thead>

        <th>分销商</th>
        <th>分销商应结算金额（正常订单）</th>
        <th>分销商扣番茄金额（赔付）</th>
        <th>上期未结算,本期平账(退款)</th>
        <th>本期不结算,下期平账(退款)</th>
        <th>已结算退款(退款)</th>
        <th>分销商扣款（无订单）</th>
        <th>番茄暂收金额</th>
        <th>分销商实际结算金额</th>
        <th>实收金额</th>
        <th>备注</th>
        <th>
            <select id="auditStatus">
                <option value="-1">正常单核单</option>
                <option value="0" <c:if test="${auditStatus eq '0'}">selected</c:if>>未核</option>
                <option value="1" <c:if test="${auditStatus eq '1'}">selected</c:if>>已核成功</option>
                <option value="2" <c:if test="${auditStatus eq '2'}">selected</c:if>>已核失败</option>
            </select>
        </th>
        <th>
            <select id="isArrival">
                <option value="">操作</option>
                <option value="false" <c:if test="${isArrival == false}">selected</c:if>>未收到</option>
                <option value="true" <c:if test="${isArrival == true}">selected</c:if>>已收到</option>
            </select>
        </th>
        <th>番茄收入（实际收入）</th>
        </tr>
        </thead>
        <c:forEach items="${page.result}" var="order">
            <tr>
                <td><%--<a style="color: blue;"
                       href="javascript:showDetail('${order.channelId}','${settlementTime}')">${order.channelName}</a>--%>
                        <a style="color: blue;"
                           href="/finance/income/innChannel?channelId=${order.channelId}&settlementTime=${settlementTime}&channelName=${order.channelName}">${order.channelName}</a>
                </td>
                <td>${order.channelSettlementAmount}</td>
                <td style="color: red">${order.channelDebit }</td>
                <td style="color: red">${order.currentRefundAmount }</td>
                <td style="color: red">${order.nextRefundAmount }</td>
                <td style="color: red">${order.refundedAmount }</td>
                <td ><a href="/finance/income/manualOrders?channelId=${order.channelId}&settlementTime=${settlementTime}"><span style="color: #6A6AFF">${order.noOrderDebitAmount }</span></a></td>
                <td><a href="/finance/income/fqTemp?channelId=${order.channelId}&settlementTime=${settlementTime}&channelName=${order.channelName}"><span style="color: #6A6AFF">${order.fqTemp}</span></a></td>
                <td>${order.channelRealAmount } </td>
                <td><a style="color: blue;"
                       onclick="openEditDiv(this, '${order.id}','${order.channelName}','${order.incomeAmount}','${order.remarks}')"><c:choose><c:when
                        test="${not empty order.incomeAmount}">${order.incomeAmount}</c:when><c:otherwise>填写实收金额</c:otherwise></c:choose></a>
                </td>
                <td>${order.remarks}</td>
                <td>
                    <c:if test="${order.auditStatus=='0'}">未核</c:if>
                    <c:if test="${order.auditStatus=='1'}">已核成功</c:if>
                    <c:if test="${order.auditStatus=='2'}">已核失败</c:if>
                </td>

                <td>
                    <c:choose>
                        <c:when test="${order.isArrival}">
                            已收到
                        </c:when>
                        <c:otherwise>

                            <security:authorize ifAnyGranted="ROLE_进账核算-未收到">
                                <a style="color: blue;"
                                   href="javascript:arrival('${order.id}','${order.channelId}','${order.channelName}','${settlementTime}')">未收到</a>
                            </security:authorize>

                        </c:otherwise>
                    </c:choose>
                </td>
                <td>${order.fqRealIncome }</td>
            </tr>
        </c:forEach>
    </table>
    <p>
        <c:if test="${not empty settlementCountAllMap and settlementCountAllMap.channels !=0}">
            截止您所选时间段内，共有<em style="color:red;">${settlementCountAllMap.channels}</em>个分销商，分销商应结算金额为<em style="color:red;">${settlementCountAllMap.channel_settlement_amount }</em>元；分销商扣番茄金额为<em style="color:red;">${settlementCountAllMap.channel_debit }</em>元；上期未结算,本期平账(退款)为<em style="color:red;">${settlementCountAllMap.current_refund_amount }</em>元；
            本期不结算,下期平账(退款)为<em style="color:red;">${settlementCountAllMap.next_refund_amount}</em>元；已结算退款(退款)为<em style="color:red;">${settlementCountAllMap.refunded_amount}</em>元；分销商扣款（无订单)为<em style="color:red;">${settlementCountAllMap.no_order_debit_amount}</em>元；实收金额为<em style="color:red;">${settlementCountAllMap.income_amount}</em>元；番茄收入（实际收入）为<em style="color:red;">${settlementCountAllMap.fq_real_income}</em>元；
            分销商实际结算金额为<em style="color:red;">${settlementCountAllMap.amounts }</em>元；
        </c:if>
        其中<em style="color:red;">${settlementCountArrivalMap.channels}</em>个分销商款项已收到，已收到实收金额为<em style="color:red;"><c:choose><c:when test="${empty settlementCountArrivalMap.amounts}">0</c:when><c:otherwise>${settlementCountArrivalMap.amounts}</c:otherwise></c:choose></em>元；
    </p>
    <c:if test="${not empty page}">
        <tags:pagination page="${page}" paginationSize="15"/>
    </c:if>
</div>
<!---------------对账单上传弹出层----------------->
<div class="center-box">
    <div class="center-box-in audit-window" style="display:none;" id="edit">
        <a href="javascript:close()" class="close-window"></a>
        <h1><span id="channelNameTitle"></span></h1>
        <ul>
            <input type="hidden" name="id" style="width: 200px;"/>
            <li>实收金额:<input type="text" id="incomeAmount"/></li>
            <li>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注:<textarea id="remarks" cols="40"></textarea></li>
            <li>
                <button style="margin-left: 110px;" id="inner_id_check" type="button">确定</button>
                <button style="margin-left: 50px;" onclick="$('#edit').fadeOut();" type="button">取消</button>
            </li>
        </ul>
        </form>
    </div>
</div>
<!---------------对账单上传弹出层----------------->
</body>
</html>

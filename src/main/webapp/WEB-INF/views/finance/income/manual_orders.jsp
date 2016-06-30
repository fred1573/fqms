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
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/finance/manualOrders" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <input type="hidden" name="settlementTime" value="${settlementTime}"/>
        <input type="hidden" name="channelId" value="${channelId}"/>
        <input type="hidden" name="orderId" value="${orderId}"/>
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
        <div class="header-button-box">
            <form id="exportForm" action="${ctx}/finance/export/income" method="post">
                <input type="hidden" name="exportSettlementTime" value="${settlementTime}"/>
                <input type="hidden" name="exportChannelId" value="${channelId}"/>
            </form>
            <a class="green-button-return add" id="addOrder" style="font-size: smaller">添加一条无订单赔付</a>
            <a class="red-button-add add" href="javascript:exportIncome()">导出Excel</a>
        </div>
    </div>
    <table class="kz-table" cellpadding="8">
        <thead>
        <th>订单号</th>
        <th>分销商扣番茄金额</th>
        <th>备注</th>
        <th>操作</th>
        </tr>
        </thead>
        <c:forEach items="${page.result}" var="order">
            <tr>
                <td>${order.orderId }</td>
                <td>${order.refund }</td>
                <td>${order.remark }</td>
                <td>
                    <button onclick="editOrder(${order.id}, '${order.orderId}', ${order.refund}, '${order.remark}')">编辑</button>
                    <button onclick="delOrder(${order.id})">删除</button>
                </td>
            </tr>
        </c:forEach>
    </table>
    <p>
        该账期内，共有 <em style="color:red;">${amountMap.count }</em> 个订单，分销商扣番茄总额为 <em style="color:red;">${amountMap.amount}</em> 元。
    </p>
    <c:if test="${not empty page}">
        <tags:pagination page="${page}" paginationSize="15"/>
    </c:if>
</div>
<!---------------对账单上传弹出层----------------->
<div class="center-box">
    <div class="center-box-in audit-window" style="display:none;" id="edit">
        <a href="javascript:close()" class="close-window"></a>
        <input type="hidden" id="editId"/>
        <ul>
            <li>订单号:<input type="text" id="orderIdEdit"/></li>
            <li>扣款金额:<input type="text" id="refundEdit"/></li>
            <li>备注:<textarea id="remarkEdit" cols="40"></textarea></li>
            <li>
                <button style="margin-left: 110px;" id="editOrderSub" type="button">确定</button>
                <button style="margin-left: 50px;" onclick="$('#edit').fadeOut();" type="button">取消</button>
            </li>
        </ul>
        </form>
    </div>

    <div class="center-box-in audit-window" style="display:none;" id="add">
        <a href="javascript:closeAdd()" class="close-window"></a>
        <ul>
            <li>订单号:<input type="text" id="orderIdAdd"/></li>
            <li>扣款金额:<input type="text" id="refundAdd"/></li>
            <li>备注:<textarea id="remarkAdd" cols="40"></textarea></li>
            <li>
                <button style="margin-left: 110px;" id="addOrderSub" type="button">确定</button>
                <button style="margin-left: 50px;" onclick="$('#add').fadeOut();" type="button">取消</button>
            </li>
        </ul>
        </form>
    </div>
</div>
<!---------------对账单上传弹出层----------------->
</body>
</html>

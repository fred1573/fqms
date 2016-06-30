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
    <title>财务结算-账单核对</title>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/finance/order_list.js" type="text/javascript"></script>
    <link href="${ctx}/css/finance/finance.css" rel="stylesheet">
    <script src="${ctx}/js/common/ajaxfileupload.js" type="text/javascript"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/finance/order/list" method="post">
        <input type="hidden" name="settlementTime" value="${settlementTime}"/>
    </form>
    <div class="header">
        <%@ include file="../navigation.jsp" %>
        <div style="left: 550px; top: 15px" class="header-button-box duizhang kc">
            <select id="settlementTime">
                <option value="">选择账期</option>
                <c:forEach var="period" items="${financeAccountPeriodList}">
                    <option value="${period.settlementTime}" <c:if test="${settlementTime eq period.settlementTime}">selected</c:if>>${period.settlementTime}</option>
                </c:forEach>
            </select>
        </div>
        <div class="header-button-box">
            <security:authorize ifAnyGranted="ROLE_生成进出账单">
                <a class="red-button-add add" id="createOrder">生成进出账单</a>
            </security:authorize>
        </div>
    </div>
    <table class="kz-table" cellpadding="8">
        <thead>
            <tr>
                <th>分销商</th>
                <th>订单总数</th>
                <th>分销商订单总额</th>
                <th>分销商结算金额</th>
                <th>客栈订单总额</th>
                <th>客栈结算金额</th>
                <th>核单</th>
                <th>操作</th>
            </tr>
        </thead>
        <c:forEach items="${data}" var="order">
            <tr>
                <td><a style="color: blue;" href="javascript:showOrderDetail('${order.id}','${settlementTime}')">${order.name}</a></td>
                <td>${order.total}</td>
                <td>${order.channel}</td>
                <td>${order.amount}</td>
                <td>${order.orders}</td>
                <td>${order.inn}</td>
                <td>
                    <c:if test="${order.auditStatus=='0'}">未核</c:if>
                    <c:if test="${order.auditStatus=='1'}">已核成功</c:if>
                    <c:if test="${order.auditStatus=='2'}">已核失败</c:if>
                </td>
                <td>
                    <security:authorize ifAnyGranted="ROLE_账单核对-上传核单">
                        <a style="color: blue;" href="javascript:openDiv('${order.id}','${settlementTime}')">上传核单</a>
                    </security:authorize>
                </td>
            </tr>
        </c:forEach>
    </table>
    <p>
        <c:if test="${not empty totalOrder and totalOrder.total !=0}">
            共计<em style="color:red;">${totalOrder.total}</em>个订单;分销商订单总额为<em style="color:red;">${totalOrder.channel}</em>元;分销商结算金额<em style="color:red;">${totalOrder.amount}</em>元;
            客栈订单总额为<em style="color:red;">${totalOrder.orders}</em>元;客栈结算金额<em style="color:red;">${totalOrder.inn}</em>元。
        </c:if>
    </p>
</div>
<!---------------对账单上传弹出层----------------->
<div class="center-box">
    <div class="center-box-in audit-window" style="display:none;" id="edit">
        <a href="javascript:close()" class="close-window"></a>

        <h1><span>上传渠道对账单</span></h1>
        <ul>
            <input type="hidden" name="channelId"/>
            <input type="hidden" name="uploadSettlementTime"/>
            <li><input type="file" name="file" id="file"/></li>
            <li><a href="##" class="green-button-ok" id="upload">确&nbsp;&nbsp;&nbsp;&nbsp;定</a></li>
        </ul>
        </form>
    </div>
</div>
<!---------------对账单上传弹出层----------------->
</body>
</html>

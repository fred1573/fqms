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
    <title>财务结算-进账核对</title>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/finance/order_detail.js" type="text/javascript"></script>
    <link href="${ctx}/css/finance/finance.css" rel="stylesheet">
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/finance/order/detail" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <input type="hidden" name="channelId" value="${channelId}"/>
        <input type="hidden" name="settlementTime" value="${settlementTime}"/>
        <input type="hidden" name="auditStatus" value="${auditStatus}"/>
        <input type="hidden" name="isBalance" value="${isBalance}"/>
        <input type="hidden" name="priceStrategy" value="${priceStrategy}"/>
        <input type="hidden" name="keyWord" value="${keyWord}"/>
        <input type="hidden" name="orderStatus" value="${orderStatus}"/>
    </form>
    <div class="header">
        <div style="width:100%;float:left;">
            <%@ include file="../navigation.jsp" %>
            <div style="left: 550px;top: 12px" class="header-button-box duizhang kc">
                <div class="search-box">
                    <input type="text" id="keyWord" maxlength="20" class="search" placeholder="客栈名称/订单号/客栈id"
                           value="${keyWord}"/>
                    <input type="button" id="search_submit" class="search-button">
                </div>
                账期 : <em style="color: red;font-size: 16px;font-weight: bold;">${settlementTime}</em>
            </div>
        </div>
        <%@ include file="sub_navigation.jsp" %>
    </div>
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
            <th>
                <select id="orderStatus">
                    <option value="">订单状态</option>
                    <c:if test="${empty  status || fn:contains(status,'0')}">
                        <option value="0" <c:if test="${orderStatus==0}">selected</c:if>>未处理</option>
                    </c:if>
                    <c:if test="${empty  status || fn:contains(status,'1')}">
                        <option value="1" <c:if test="${orderStatus==1}">selected</c:if>>已接受(已分房)</option>
                    </c:if>
                    <c:if test="${empty  status || fn:contains(status,'2')}">
                        <option value="2" <c:if test="${orderStatus==2}">selected</c:if>>已拒绝</option>
                    </c:if>
                    <c:if test="${empty  status || fn:contains(status,'3')}">
                        <option value="3" <c:if test="${orderStatus==3}">selected</c:if>>已取消</option>
                    </c:if>
                    <c:if test="${empty  status || fn:contains(status,'4')}">
                        <option value="4" <c:if test="${orderStatus==4}">selected</c:if>>验证失败</option>
                    </c:if>
                    <c:if test="${empty  status || fn:contains(status,'5')}">
                        <option value="5" <c:if test="${orderStatus==5}">selected</c:if>>已接受(未分房)</option>
                    </c:if>
                </select>
            </th>
            <th>客栈名称(id)</th>
            <th>分销商订单号</th>
            <th>分销商订单金额/分销商结算金额/客栈结算金额</th>
            <th>番茄总调价</th>
            <th>预订人</th>
            <th>手机号码</th>
            <th>房型</th>
            <%--<th>房间数</th>--%>
            <%--<th>分销商单价</th>--%>
            <th>住离日期</th>
            <th>产生周期</th>
            <th>
                <select id="auditStatus">
                    <option value="">核单</option>
                    <option value="0" <c:if test="${auditStatus eq '0'}">selected</c:if>>未核</option>
                    <option value="1" <c:if test="${auditStatus eq '1'}">selected</c:if>>已核成功</option>
                    <option value="2" <c:if test="${auditStatus eq '2'}">selected</c:if>>已核失败</option>
                </select>
            </th>
            <th>
                <select id="isBalance">
                    <option value="">结算</option>
                    <option value="0" <c:if test="${isBalance eq '0'}">selected</c:if>>未结算</option>
                    <option value="1" <c:if test="${isBalance eq '1'}">selected</c:if>>已结算</option>
                </select>
            </th>
            <th>
                操作
            </th>
        </tr>
        </thead>
        <c:forEach items="${page.result}" var="order">
            <tr>
                <td>${order.innerOrderMode}</td>
                <td>${order.orderStatusStr}</td>
                <td>${order.innName}(${order.innId})</td>
                <td>${order.channelOrderNo}</td>
                <td>${order.totalAmount}/${order.channelSettlementAmount}/${order.innSettlementAmount}</td>
                <td>${order.extraPrice}</td>
                <td>${order.userName}</td>
                <td>${order.contact}</td>
                <td>
                    <c:forEach items="${order.channelOrderList}" var="childOrder">
                        <c:if test="${not childOrder.deleted}">
                            ${childOrder.channelRoomTypeName}<br/>
                        </c:if>
                    </c:forEach>
                </td>
                <%--<td>--%>
                    <%--<c:forEach items="${order.channelOrderList}" var="childOrder">--%>
                        <%--<c:if test="${not childOrder.deleted}">--%>
                            <%--${childOrder.roomTypeNums}<br/>--%>
                        <%--</c:if>--%>
                    <%--</c:forEach>--%>
                <%--</td>--%>
                <%--<td>--%>
                    <%--<c:forEach items="${order.channelOrderList}" var="childOrder">--%>
                        <%--<c:if test="${not childOrder.deleted}">--%>
                            <%--${childOrder.bookPrice}<br/>--%>
                        <%--</c:if>--%>
                    <%--</c:forEach>--%>
                <%--</td>--%>
                <td>
                    <c:forEach items="${order.channelOrderList}" var="childOrder">
                        <c:if test="${not childOrder.deleted}">
                            <fmt:formatDate value="${childOrder.checkInAt}" pattern="yyyy-MM-dd"/>/<fmt:formatDate
                                value="${childOrder.checkOutAt}" pattern="yyyy-MM-dd"/><br/>
                        </c:if>
                    </c:forEach>
                </td>
                <td>${order.produceTime}</td>
                <td>${order.auditStatusStr}</td>
                <td>${order.isBalanceStr}</td>
                <td>
                    <a style="color: blue;" href="javascript:modifyBill('${order.id}')">修改</a>
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
    <c:if test="${not empty page}">
        <tags:pagination page="${page}" paginationSize="15"/>
    </c:if>
</div>
<%@ include file="popup.jsp" %>
</body>
</html>

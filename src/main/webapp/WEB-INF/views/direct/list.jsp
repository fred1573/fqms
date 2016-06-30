<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
    <title>直连订单</title>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/direct/direct.js" type="text/javascript"></script>
    <style>
        .percentage {
            height: 20px;
            width: 60px;
            display: inline-block;
            text-indent: 0;
            text-align: center
        }
    </style>
</head>
<body>
<form action="${ctx}/direct/order" method="post" id="mainForm">
    <input type="hidden" name="channelId" value="${directOrderForm.channelId}"/>
    <input type="hidden" name="orderStatus" value="${directOrderForm.orderStatus}"/>
    <input type="hidden" name="innName" value="${directOrderForm.innName}"/>
    <input type="hidden" name="searchTimeTyep" value="${directOrderForm.searchTimeTyep}"/>
    <input type="hidden" name="startDate" value="${directOrderForm.startDate}"/>
    <input type="hidden" name="endDate" value="${directOrderForm.endDate}"/>
    <input type="hidden" name="page" id="page" value="${directOrderForm.page}"/>
</form>

<div class="container-right">
    <div class="header">
        <h1>直连订单</h1>

        <div style="left: 400px;" class="header-button-box duizhang kc">
            <div class="search-box">
                <input type="text" id="innName" maxlength="20" class="search" placeholder="模糊搜索客栈名称"
                       <c:if test="${not empty directOrderForm.innName}">value="${directOrderForm.innName}"</c:if>/>
                <input type="button" id="search_submit" class="search-button">
            </div>
            <select id="searchTimeTyep">
                <option value="CREATE" <c:if test="${directOrderForm.searchTimeTyep eq 'CREATE'}">selected</c:if>>下单日期
                </option>
                <option value="CHECK_IN" <c:if test="${directOrderForm.searchTimeTyep eq 'CHECK_IN'}">selected</c:if>>
                    入住日期
                </option>
                <option value="CHECK_OUT" <c:if test="${directOrderForm.searchTimeTyep eq 'CHECK_OUT'}">selected</c:if>>
                    离店日期
                </option>
            </select>

            <div class="date-date" style="width: 300px;padding: 0">
                <a id="startDate" style="float: none;display: inline-block" class="date WdateFmtErr"
                   href="javascript:void(0)"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:changeStartDate})"
                   class="date">
                    <c:choose>
                        <c:when test="${not empty directOrderForm.startDate}">
                            ${directOrderForm.startDate}
                        </c:when>
                        <c:otherwise>
                            <fmt:formatDate value="<%=new Date()%>" pattern="yyyy-MM-dd"/>
                        </c:otherwise>
                    </c:choose>
                </a>
                至
                <a id="endDate" style="float: none;display: inline-block" class="date WdateFmtErr"
                   href="javascript:void(0)"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:changeStartDate})"
                   class="date">
                    <c:choose>
                        <c:when test="${not empty directOrderForm.endDate}">
                            ${directOrderForm.endDate}
                        </c:when>
                        <c:otherwise>
                            <fmt:formatDate value="<%=new Date()%>" pattern="yyyy-MM-dd"/>
                        </c:otherwise>
                    </c:choose>
                </a>
            </div>
        </div>
    </div>

    <!--end header-->
    <table class="kz-table" cellpadding="8">
        <thead>
        <tr>
            <th>
                <select id="channelId">
                    <option value="">渠道来源</option>
                    <c:forEach var="ota" items="${otaMap}">
                        <option value="${ota.key}" <c:if test="${directOrderForm.channelId eq ota.key}">selected</c:if>>${ota.value}</option>
                    </c:forEach>
                </select>
            </th>
            <th>客栈名称</th>
            <th>
                渠道订单号
            </th>
            <th>
                <select id="orderStatus">
                    <option value="">订单状态</option>
                    <option value="0" <c:if test="${directOrderForm.orderStatus eq '0'}">selected</c:if>>未处理</option>
                    <option value="1" <c:if test="${directOrderForm.orderStatus eq '1'}">selected</c:if>>已接受</option>
                    <option value="2" <c:if test="${directOrderForm.orderStatus eq '2'}">selected</c:if>>已拒绝</option>
                    <option value="3" <c:if test="${directOrderForm.orderStatus eq '3'}">selected</c:if>>已取消</option>
                    <option value="4" <c:if test="${directOrderForm.orderStatus eq '4'}">selected</c:if>>验证失败</option>
                </select>
            </th>
            <th>客人姓名</th>
            <th>手机号</th>
            <th>房型</th>
            <th>房间数</th>
            <th>住离日期</th>
            <th>单价</th>
            <th>总价/预付金额</th>
            <th>操作人</th>
            <th>下单时间</th>
        </tr>
        </thead>
        <c:forEach items="${orderList}" var="order">
            <tr>
                <td>${order.channelName}</td>
                <td>${order.innName}</td>
                <td>${order.channelOrderNo}</td>
                <td>${order.conName}</td>
                <td>${order.userName}</td>
                <td>${order.contact}</td>
                <td>
                    <c:forEach items="${order.channelOrderList}" var="subOrder">
                        ${subOrder.channelRoomTypeName}<br>
                    </c:forEach>
                </td>
                <td>
                    <c:forEach items="${order.channelOrderList}" var="subOrder">
                        ${subOrder.roomTypeNums}<br>
                    </c:forEach>
                </td>
                <td>
                    <c:forEach items="${order.channelOrderList}" var="subOrder">
                        ${subOrder.checkInAt}/${subOrder.checkOutAt}<br>
                    </c:forEach>
                </td>
                <td>
                    <c:forEach items="${order.channelOrderList}" var="subOrder">
                        ${subOrder.bookPrice}<br>
                    </c:forEach>
                </td>
                <td>${order.totalAmount}/${order.paidAmount}</td>
                <td>${order.operatedUser}</td>
                <td>${order.orderTime}</td>
            </tr>
        </c:forEach>
    </table>
    <c:if test="${not empty pageUtil}">
        <p class="acount"> 截止您所选时间段内，共有 <span style="color: red">${pageUtil.recordCount}</span> 个订单</p>
        <div class="page-list">
            <ul>
                <li class="disabled"><a href="#" onclick="jumpPage(1)">首页</a></li>
                <c:if test="${pageUtil.currentPage != 1}">
                    <li class="disabled"><a href="#" onclick="jumpPage(${pageUtil.currentPage - 1})">上一页</a></li>
                </c:if>
                <li>${pageUtil.currentPage}/${pageUtil.pageCount}</li>
                <c:if test="${pageUtil.currentPage != pageUtil.pageCount}">
                    <li class="disabled"><a href="#" onclick="jumpPage(${pageUtil.currentPage + 1})">下一页</a></li>
                </c:if>
                <li class="disabled"><a href="#" onclick="jumpPage(${pageUtil.pageCount})">末页</a></li>
            </ul>
        </div>
    </c:if>
</div>
</body>
</html>
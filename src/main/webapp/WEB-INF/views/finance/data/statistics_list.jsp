<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>代销数据-数据统计</title>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/chart/echarts.js" type="text/javascript"></script>
    <script src="${ctx}/js/finance/statistics_list.js" type="text/javascript"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
    <link rel="stylesheet" type="text/css" href="${ctx}/css/finance/statisticalData.css">
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/data/statistics" method="post">
        <input type="hidden" name="beginDate" value="${dataForm.beginDate}"/>
        <input type="hidden" name="endDate" value="${dataForm.endDate}"/>
        <input type="hidden" name="channelId" value="${dataForm.channelId}"/>
        <input type="hidden" name="channelName" value="${dataForm.channelName}"/>
        <input type="hidden" name="isAcceptedOnly" value="${dataForm.isAcceptedOnly}"/>
    </form>
    <div class="header">
        <%@ include file="title.jsp" %>
        <div class="ctrl-bar">
            <input type="text" class="t-start" readonly onclick="WdatePicker({dateFmt:'yyyy-MM-dd', onpicked:syncData,skin:'whyGreen',maxDate:'%y-%M-{%d-1}'})" value="<c:choose><c:when test="${empty dataForm.beginDate}"><fmt:formatDate value="<%=new Date()%>" pattern="yyyy-MM-dd"/></c:when><c:otherwise>${dataForm.beginDate}</c:otherwise></c:choose>">
            <span>至</span>
            <input type="text" class="t-end" readonly onclick="WdatePicker({dateFmt:'yyyy-MM-dd', onpicked:syncData,skin:'whyGreen',maxDate:'%y-%M-{%d-1}'})"  value="<c:choose><c:when test="${empty dataForm.endDate}"><fmt:formatDate value="<%=new Date()%>" pattern="yyyy-MM-dd"/></c:when><c:otherwise>${dataForm.endDate}</c:otherwise></c:choose>">
            <select id="channelId">
                <option value="">全部分销商</option>
                <c:forEach var="ota" items="${otaMap}">
                    <option value="${ota.key}" <c:if test="${dataForm.channelId eq ota.key}">selected</c:if>>${ota.value}</option>
                </c:forEach>
            </select>
            <label><input type="checkbox" id="isAcceptedOnly" <c:if test="${dataForm.isAcceptedOnly}">checked</c:if>> 只包含已接受订单</label>
        </div>
    </div>
    <div class="clearfix"></div>
    <div class="wrap">
        <p class="salestatus-title">
            [<span>&nbsp;<c:choose><c:when test="${empty dataForm.beginDate}"><fmt:formatDate value="<%=new Date()%>" pattern="yyyy-MM-dd"/></c:when><c:otherwise>${dataForm.beginDate}</c:otherwise></c:choose></span>
            &nbsp;至&nbsp;<span><c:choose><c:when test="${empty dataForm.endDate}"><fmt:formatDate value="<%=new Date()%>" pattern="yyyy-MM-dd"/></c:when><c:otherwise>${dataForm.endDate}</c:otherwise></c:choose></span>&nbsp;下单]<span>所有</span>的客栈在<span><c:choose><c:when test="${not empty dataForm.channelName}">${dataForm.channelName}</c:when><c:otherwise>全部分销商</c:otherwise></c:choose></span>上销售的状况为：</p>
        <div class="salestatus-box salestatus-box-01">
            <p class="salestatus-box-text01">分销商总订单数</p>
            <p class="salestatus-box-text02">${statisticsSaleData.channelOrderAmount}</p>
            <div class="salestatus-box-circle">
                <p>
                    占比<br>
                    <span><fmt:formatNumber type="number" value="${statisticsSaleData.channelOrderAmountRatio }" maxFractionDigits="2"/>%</span>
                </p>
            </div>
        </div>
        <div class="salestatus-box">
            <p class="salestatus-box-text01">总间夜数</p>
            <p class="salestatus-box-text02">${statisticsSaleData.channelRoomNightAmount}</p>
            <div class="salestatus-box-circle">
                <p>
                    占比<br>
                    <span><fmt:formatNumber type="number" value="${statisticsSaleData.channelRoomNightAmountRatio}" maxFractionDigits="2"/>%</span>
                </p>
            </div>
        </div>
        <div class="salestatus-box">
            <p class="salestatus-box-text01">分销商总订单金额</p>
            <p class="salestatus-box-text02"><fmt:formatNumber type="number" value="${statisticsSaleData.channelPriceAmount}" maxFractionDigits="2"/></p>
            <div class="salestatus-box-circle">
                <p>
                    占比<br>
                    <span><fmt:formatNumber type="number" value="${statisticsSaleData.channelPriceAmountRatio}" maxFractionDigits="2"/>%</span>
                </p>
            </div>
        </div>
        <p class="salestatus-tips clearfix">
            共计<span>${statisticsSaleData.soldInnCount}</span>个已售客栈；日平均间夜价格为<span><fmt:formatNumber type="number" value="${statisticsSaleData.dailyAverageRoomNightPrice}" maxFractionDigits="2"/></span>元；
            日间夜价格中位数为<span><fmt:formatNumber type="number" value="${statisticsSaleData.midNum}" maxFractionDigits="2"/></span>元；
            订单间数比<span><fmt:formatNumber type="number" value="${statisticsSaleData.orderRoomRatio}" maxFractionDigits="2"/></span>；
            订单夜数比<span><fmt:formatNumber type="number" value="${statisticsSaleData.nightRoomRatio}" maxFractionDigits="2"/></span>；
            平均提前订房天数<span><fmt:formatNumber type="number" value="${statisticsSaleData.advanceBookDay}" maxFractionDigits="2"/></span>天；
            平均停留天数<span><fmt:formatNumber type="number" value="${statisticsSaleData.averageStayDay}" maxFractionDigits="2"/></span>天
        </p>
        <div class="data-box">
            <h2>曲线走势</h2>
            <div class="graphic-box" id="main"></div>
        </div>
        <div class="data-box">
            <h2>客栈排行Top10</h2>
            <div class="tabs-nav">
                <label><input type="radio" name="radio" data-index="0" checked>按照总间夜数排序</label>
                <label><input type="radio" name="radio" data-index="1">按照总订单金额排序</label>
                <label><input type="radio" name="radio" data-index="2">按照总订单数排序</label>
                <%--<button class="btn-excel btn-excel-leaderboard">导出Excel</button>--%>
            </div>
            <%--按照总间夜数排序--%>
            <div class="tabs-box" style="display: block">
                <table>
                    <thead>
                        <tr>
                            <th>排行</th>
                            <th>客栈名称</th>
                            <th>总间夜数</th>
                            <th>总订单金额</th>
                            <th>总订单数</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${statisticsTableData.roomNightRank}" varStatus="idx">
                            <tr>
                                <td>${idx.index + 1}</td>
                                <td>${order.inn_name}</td>
                                <td>${order.room_nights}</td>
                                <td><fmt:formatNumber type="number" value="${order.total_amount}" maxFractionDigits="2"/></td>
                                <td>${order.orders}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <%--按照总订单金额排序--%>
            <div class="tabs-box">
                <table>
                    <thead>
                    <tr>
                        <th>排行</th>
                        <th>客栈名称</th>
                        <th>总间夜数</th>
                        <th>总订单金额</th>
                        <th>总订单数</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="order" items="${statisticsTableData.totalAmountRank}" varStatus="idx">
                        <tr>
                            <td>${idx.index + 1}</td>
                            <td>${order.inn_name}</td>
                            <td>${order.room_nights}</td>
                            <td><fmt:formatNumber type="number" value="${order.total_amount}" maxFractionDigits="2"/></td>
                            <td>${order.orders}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <%--按照总订单数排序--%>
            <div class="tabs-box">
                <table>
                    <thead>
                    <tr>
                        <th>排行</th>
                        <th>客栈名称</th>
                        <th>总间夜数</th>
                        <th>总订单金额</th>
                        <th>总订单数</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="order" items="${statisticsTableData.ordersRank}" varStatus="idx">
                        <tr>
                            <td>${idx.index + 1}</td>
                            <td>${order.inn_name}</td>
                            <td>${order.room_nights}</td>
                            <td><fmt:formatNumber type="number" value="${order.total_amount}" maxFractionDigits="2"/></td>
                            <td>${order.orders}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>

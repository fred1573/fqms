<%@ page import="java.util.Date" %>
<%--
  Created by IntelliJ IDEA.
  User: 番茄桑
  Date: 2015/8/13
  Time: 10:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>代销数据-数据分析</title>
    <script src="${ctx}/js/finance/artTemplate.js" type="text/javascript"></script>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/chart/echarts.js"></script>
    <script src="${ctx}/js/finance/analysis_list.js" type="text/javascript"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
    <link rel="stylesheet" type="text/css" href="${ctx}/css/finance/statisticalData.css">
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/data/analysis" method="post">
        <input type="hidden" name="beginDate" value="${dataForm.beginDate}"/>
        <input type="hidden" name="endDate" value="${dataForm.endDate}"/>
        <input type="hidden" name="channelId" value="${dataForm.channelId}"/>
    </form>
    <div class="header">
        <%@ include file="title.jsp" %>
        <div class="ctrl-bar">
            <input type="text" class="t-start" readonly onfocus="WdatePicker({dateFmt:'yyyy-MM-dd', onpicked:syncData,skin:'whyGreen',maxDate:'%y-%M-{%d-1}'})" value="<c:choose><c:when test="${empty dataForm.beginDate}"><fmt:formatDate value="<%=new Date()%>" pattern="yyyy-MM-dd"/></c:when><c:otherwise>${dataForm.beginDate}</c:otherwise></c:choose>">
            <span>至</span>
            <input type="text" class="t-end" readonly onfocus="WdatePicker({dateFmt:'yyyy-MM-dd', onpicked:syncData,skin:'whyGreen',maxDate:'%y-%M-{%d-1}'})" value="<c:choose><c:when test="${empty dataForm.endDate}"><fmt:formatDate value="<%=new Date()%>" pattern="yyyy-MM-dd"/></c:when><c:otherwise>${dataForm.endDate}</c:otherwise></c:choose>">
            <select id="channelId">
                <option value="">全部分销商</option>
                <c:forEach var="ota" items="${otaMap}">
                    <option value="${ota.key}" <c:if test="${dataForm.channelId eq ota.key}">selected</c:if>>${ota.value}</option>
                </c:forEach>
            </select>
        </div>
    </div>
    <!--end header-->
    <div class="wrap">
        <h1>全部订单总数:<span>${allOrderAmount}</span></h1>
        <%--data show 1--%>
        <div class="section">
            <div class="data-chart" id="main"></div>
            <div class="data-list">
                <table>
                    <thead>
                        <tr>
                            <th></th>
                            <th>订单数</th>
                            <th>间夜数</th>
                            <th>金额</th>
                        </tr>
                    </thead>
                    <tbody id="chart"></tbody>
                </table>
            </div>
        </div>
        <%--data show 2--%>
        <div class="section">
            <div class="data-chart" id="main1"></div>
            <div class="data-list">
                <table>
                    <thead>
                    <tr>
                        <th></th>
                        <th>订单数</th>
                        <th>间夜数</th>
                        <th>金额</th>
                    </tr>
                    </thead>
                    <tbody id="chart1"></tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<script type="text/html" id="dataTpl">
    <? for(var i=0; i<listMap.length; i++){ ?>
        <tr>
            <td><?= listMap[i].zt ?></td>
            <td><?= listMap[i].orders ?></td>
            <td><?= listMap[i].nights ?></td>
            <td><?= listMap[i].amounts ?></td>
        </tr>
    <?}?>
</script>
</body>
</html>

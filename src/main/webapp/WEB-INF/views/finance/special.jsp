<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2016/3/8
  Time: 10:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title></title>
</head>
<body>
<div style=" margin-left: 20px">


        <a href="${ctx}/finance/order/list?settlementTime=${settlementTime}"><button class="kc-btn <c:if test="${currentBtn == 'order'}">kc-active</c:if>">账单核对</button></a>
        <a href="${ctx}/finance/income/list?settlementTime=${settlementTime}"><button class="kc-btn <c:if test="${currentBtn == 'income'}">kc-active</c:if>">进账核算</button></a>
        <a href="${ctx}/finance/out/list?settlementTime=${settlementTime}"><button class="kc-btn <c:if test="${currentBtn == 'out'}">kc-active</c:if>"> 出账核算</button></a>
        <a href="${ctx}/finance/operate/list"><button class="kc-btn <c:if test="${currentBtn == 'operate'}">kc-active</c:if>">操作记录</button></a>
</div>

<div class="windowBg2">
    <div><img src="${ctx}/images/webloading.gif"/></div>
</div>
</body>
</html>

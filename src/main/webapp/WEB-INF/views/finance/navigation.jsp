<%--
  Created by IntelliJ IDEA.
  User: 番茄桑
  Date: 2015/8/20
  Time: 9:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div style="margin-left: 20px">
    <security:authorize ifAnyGranted="ROLE_结算账期">
        <a href="${ctx}/period/list">
            <button class="kc-btn <c:if test="${currentBtn == 'period'}">kc-active</c:if>">结算账期</button>
        </a>
    </security:authorize>
    <security:authorize ifAnyGranted="ROLE_账单核对">
        <a href="${ctx}/finance/order/list?settlementTime=${settlementTime}">
            <button class="kc-btn <c:if test="${currentBtn == 'order'}">kc-active</c:if>">账单核对</button>
        </a>
    </security:authorize>
    <security:authorize ifAnyGranted="ROLE_进账核算">
        <a href="${ctx}/finance/income/list?settlementTime=${settlementTime}">
            <button class="kc-btn <c:if test="${currentBtn == 'income'}">kc-active</c:if>">进账核算</button>
        </a>
    </security:authorize>
    <security:authorize ifAnyGranted="ROLE_出账核算">
        <a href="${ctx}/finance/out/list?settlementTime=${settlementTime}&status=normal">
            <button class="kc-btn <c:if test="${currentBtn == 'out'}">kc-active</c:if>"> 出账核算</button>
        </a>
    </security:authorize>
    <security:authorize ifAnyGranted="ROLE_财务操作记录">
        <a href="${ctx}/finance/operate/list">
            <button class="kc-btn <c:if test="${currentBtn == 'operate'}">kc-active</c:if>">操作记录</button>
        </a>
    </security:authorize>
</div>
<div class="windowBg2">
    <div><img src="${ctx}/images/webloading.gif"/></div>
</div>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="header-sub-tab">
  <a href="${ctx}/finance/out/list?settlementTime=${settlementTime}&status=normal"
     <c:if test="${status=='normal'}">class="active" </c:if>>正常账单</a>
  <a href="${ctx}/finance/out/special/list?settlementTime=${settlementTime}&status=special"
     <c:if test="${status=='special'}">class="active" </c:if> >特殊结算</a>
  <a href="${ctx}/finance/out/delay/list?settlementTime=${settlementTime}&status=delay"
     <c:if test="${status=='delay'}">class="active" </c:if> >延期结算</a>
  <a href="${ctx}/finance/out/arrears?settlementTime=${settlementTime}&arrearsStatus=1&status=arrears"
     <c:if test="${status=='arrears'}">class="active" </c:if>>挂账</a>

  <div class="header-sub-tab1">
    <a href="${ctx}/finance/out/arrears?settlementTime=${settlementTime}&arrearsStatus=1&status=${status}"
       <c:if test="${arrearsStatus=='1'}">class="active" </c:if>>平账</a>
    <a href="${ctx}/finance/out/arrears?settlementTime=${settlementTime}&arrearsStatus=2&status=${status}"
       <c:if test="${arrearsStatus=='2'}">class="active" </c:if>    >部分平账</a>
    <a href="${ctx}/finance/out/arrears?settlementTime=${settlementTime}&arrearsStatus=3&status=${status}"
       <c:if test="${arrearsStatus=='3'}">class="active" </c:if>    >当期挂账</a>
    <a href="${ctx}/finance/out/arrears?settlementTime=${settlementTime}&arrearsStatus=4&status=${status}"
       <c:if test="${arrearsStatus=='4'}">class="active" </c:if>    >累计挂账</a>
  </div>
</div>
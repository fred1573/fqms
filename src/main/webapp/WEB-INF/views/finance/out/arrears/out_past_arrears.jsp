<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2016/3/7
  Time: 9:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
  <title>财务结算-出账核算</title>
  <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
  <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
  <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
  <script src="${ctx}/js/finance/out_special_balance.js" type="text/javascript"></script>
  <link href="${ctx}/css/finance/finance.css" rel="stylesheet">
  <script type="text/javascript">
    var ctx = '${ctx}';
  </script>

</head>
<body>
<div class="container-right">
  <form id="mainForm" action="${ctx}/finance/out/arrears" method="post">
    <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
    <input type="hidden" name="innName" value="${innName}"/>
    <input type="hidden" name="settlementTime" value="${settlementTime}"/>
    <input type="hidden" name="confirmStatus" value="${confirmStatus}"/>
    <input type="hidden" name="settlementStatus" value="${settlementStatus}"/>
    <input type="hidden" name="isMatch" value="${isMatch}"/>
    <input type="hidden" name="isTagged" value="${isTagged}"/>
    <input type="hidden" name="arrearsStatus" value="${arrearsStatus}">
  </form>
  <div class="header">
    <%@ include file="../../navigation.jsp" %>
    <div style="float: right;margin-top: 15px;margin-right: 5px;" class="duizhang kc">


      <%--<div class="search-box">
        <input type="text" id="innName" maxlength="20" class="search" placeholder="模糊搜索客栈名称"
               value="${innName}"/>
        <input type="button" id="search_submit" class="search-button">
      </div>--%>
      <%--<select id="settlementTime">
        <option value="">选择账期</option>
        <c:forEach var="period" items="${financeAccountPeriodList}">
          <option value="${period.settlementTime}"
                  <c:if test="${settlementTime eq period.settlementTime}">selected</c:if>>${period.settlementTime}</option>
        </c:forEach>
      </select>--%>
     <%-- <a class="red-button-add add" style="margin-right:5px;margin-top:10px;width: 100px;"
         href="javascript:arrearsExportOut()">导出Excel</a>--%>
    </div>

  </div>

  <%@include file="../out_arrears_head.jsp"%>
  <table class="kz-table" cellpadding="8">
    <tr>
      <th>账期</th>
      <th>应结算客栈金额(正常订单)</th>
      <th>客栈退款金额</th>
      <th>客栈赔付金额</th>
      <th>番茄补款客栈金额</th>
      <th>分销商应结算金额</th>
      <th>番茄佣金收入</th>
      <th>客栈结算金额</th>
      <th>确认状态</th>

    </tr>
    <c:forEach items="${pastArrears}" var="past">
      <tr>
        <td><a style="color: #0033CC" href="${ctx}/finance/out/arrears/channel?settlementTime=${past.settlementTime}&innId=${past.financeInnSettlementInfo.id}&type=arrears&statusType=normal&arrearsStatus=${arrearsStatus}&status=${status}">${past.settlementTime}</a></td>
        <td>${past.innSettlementAmount}</td>
        <td>${past.refundAmount}</td>
        <td>${past.innPayment}</td>
        <td>${past.fqReplenishment}</td>
        <td>${past.channelSettlementAmount}</td>
        <td>${past.fqSettlementAmount}</td>
        <td>${past.afterArrearsAmount}</td>
        <td>
          <c:choose>
            <c:when test="${past.confirmStatus == '1'}">
              已确认
            </c:when>
            <c:when test="${past.confirmStatus == '2'}">
              系统自动确认
            </c:when>
            <c:otherwise>未确认</c:otherwise>
          </c:choose>
        </td>
      </tr>
    </c:forEach>

  </table>
</div>



</body>
</html>



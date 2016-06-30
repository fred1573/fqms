<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2016/3/8
  Time: 17:12
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

<div class="header">
  <%@ include file="../../navigation.jsp" %>
  <div style="float: right;margin-top: 15px;margin-right: 5px;" class="duizhang kc">
    <a class="red-button-add add" style="margin-right:5px;margin-top:10px;width: 100px;"
       href="${ctx}/finance/inn/fill?settlementTime=${settlementTime}&innId=${innId}&status=${status}&ret=special">一键填充</a>
  </div>
  <div class="header-button-box">
    账期 : <em style="color: red;font-size: 10px;font-weight: bold;margin-right: 200px">${settlementTime}</em>
  </div>

</div>
  <div class="header-sub-tab">
    <a href="${ctx}/finance/out/list?settlementTime=${settlementTime}&status=normal"
       <c:if test="${status=='normal'}">class="active" </c:if>>正常账单</a>
    <a href="${ctx}/finance/out/special/list?settlementTime=${settlementTime}&status=special"
       <c:if test="${status=='special'}">class="active" </c:if> >特殊结算</a>
    <a href="${ctx}/finance/out/delay/list?settlementTime=${settlementTime}&status=delay"
       <c:if test="${status=='delay'}">class="active" </c:if> >延期结算</a>
    <a href="${ctx}/finance/out/arrears?settlementTime=${settlementTime}&arrearsStatus=1&status=arrears"
       <c:if test="${status=='arrears'}">class="active" </c:if>>挂账</a>
  </div>
<table class="kz-table" cellpadding="8">
  <tr>
    <th>分销商</th>
    <th>分销商结算金额(正常订单)</th>
    <th>分销商实际结算金额</th>
    <th><br>客栈应结金额(正常订单)</th>
    <th>客栈赔付金额</th>
    <th>本期客栈退款金额</th>
    <th>番茄补款客栈金额</th>
    <th>番茄佣金收入</th>
    <th>客栈实际结算金额</th>
    <th>实付金额</th>
    <th>
      <select id="isMatch">
        <option value="">确认</option>
        <option value="true" <c:if test="${not empty isMatch && isMatch}">selected</c:if>>账实相符</option>
        <option value="false" <c:if test="${not empty isMatch && !isMatch}">selected</c:if>>账实不符</option>

      </select>
    </th>
  </tr>

  <c:forEach items="${mapList}" var="order">
    <tr>


      <td><a style="color: blue"
             href="${ctx}/finance/out/specialBalance/normal?settlementTime=${settlementTime}&channelId=${order.channelId}&channelOrderNo=${channelOrderNo}&innId=${innId}&status=${status}&statusType=normal">${order.channelName}</a>
      </td>
      <td>${order.channelSettlementAmount}</td>
      <td>${order.channelRealSettlementAmount}</td>
      <td>${order.innSettlementAmount}</td>
      <td>${order.innPayment}</td>
      <td>${order.refundAmount}</td>
      <td>${order.fqReplenishment}</td>
      <td>${order.fqSettlementAmount}</td>
      <td>${order.innRealSettlement}</td>
      <td>
        <input type="hidden" name="id" value="${order.id}">
        <input type="hidden" name="realPayment" value="${order.realPayment}">
        <input type="hidden" name="paymentRemark" value="${order.paymentRemark}">
        <a onclick="amountPaid(this)">
          <c:choose >
            <c:when test="${order.realPayment==null}"><p style="color: #0033CC">输入实付金额</p></c:when>
            <c:otherwise><p style="color: #0033CC">${order.realPayment}</p></c:otherwise>
          </c:choose>
        </a>
      </td>

      <td>
        <c:choose>
          <c:when test="${order.isMatch}">账实相符</c:when>
          <c:otherwise>账实不符</c:otherwise>
        </c:choose>
      </td>
    </tr>
  </c:forEach>
</table>
<p>

  截止您所选时间段内，共有<em style="color:red;">${total.count}</em>个分销商，共有<em style="color:red;">${total.to}</em>个订单，客栈订单总金额为<em
        style="color:red;">${total.ta}</em>，分销商订单总金额为<c:if test="${total.ca==null}"><em
        style="color:red;">0.00</em></c:if><em style="color:red;">${total.ca}</em>,分销商实际结算金额为<c:if
        test="${total.csa==null}"><em style="color:red;">0.00</em></c:if><em style="color:red;">${total.csa}</em>，
  番茄收入金额为<c:if test="${total.fsa==null}"><em style="color:red;">0.00</em></c:if><em
        style="color:red;">${total.fsa}</em>,客栈实际结算金额为<c:if test="${total.isa==null}"><em
        style="color:red;">0.00</em></c:if><em style="color:red;">${total.isa}</em>


</p>


</div>

<div id="dialogBlackBg" style="display:none;">
  <div class="center-box">
    <div class="center-box-in audit-window" id="accountPaid">
      <a href="javascript:close('1')" class="close-window"></a>
      <div class="ann-name" id="annName"></div>
      <div class="dialog-content">
        <div>
          <span>实付款：</span>
          <input type="hidden" id="id">
          <input type="text" class="paid" id="realPayment">
        </div>
        <div>
          <span>备注：</span>
              <textarea class="mark" id="paymentRemark">

              </textarea>
        </div>
        <div style="margin-bottom:20px" class="btn-submit">
          <button type="button" id="submitPaidData" style="margin-left: 80px;" value="2682" onclick="submitPaidData()">确定</button>
          <button type="button" onclick="$('#dialogBlackBg').hide();$('#accountPaid').hide();" style="margin-left: 50px;">取消</button>
        </div>
      </div>
    </div>
  </div>
</div>

</body>
</html>





















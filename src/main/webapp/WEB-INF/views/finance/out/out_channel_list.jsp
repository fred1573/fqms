<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2016/1/13
  Time: 17:41
  To change this template use File | Settings | File Templates.
--%><%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
  <title>财务结算-出账核算</title>
  <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
  <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
  <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
  <link href="${ctx}/css/finance/finance.css" rel="stylesheet">
  <script src="${ctx}/js/finance/out_channel_list.js" type="text/javascript"></script>
  <script type="text/javascript">
    var ctx = '${ctx}';
  </script>
  <style>
    .ann-name {
      text-align: center;
      font-size: 16px;
      padding: 10px;
      border-bottom: 1px solid #ccc;
    }
    .dialog-content {
      width: 90%;
      margin: 0 auto;
    }
    .dialog-content > div {
      position: relative;
      padding-left: 50px;
      margin: 10px 0;
    }
    .dialog-content > div >span {
      position: absolute;
      margin-left: 0;
      left: 0;
      width: 50px;
    }
    .mark {
      width: 98%;
      height: 100px;
    }
  </style>
</head>
<body>
<div class="container-right">
  <form id="mainForm" action="${ctx}/finance/inn/channelSettlement" method="post">
    <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
    <input type="hidden" id="settlementTime" name="settlementTime" value="${settlementTime}"/>
    <input type="hidden"  name="channelId"  value="${channelId}"/>
    <input type="hidden" name="isMatch" value="${order.isMatch}">
    <input type="hidden" name="innName"/>
  </form>
  <div class="header">
    <%@ include file="../navigation.jsp" %>
    <div style="left: 550px;top: 15px;" class="header-button-box duizhang kc">
      <div class="search-box">
        <input type="text" id="innName" maxlength="20" class="search" placeholder="模糊搜索客栈名称"
              />
        <input type="button" id="search_submit" class="search-button">
      </div>
      结算周期:${settlementTime}
      <select id="channelId">
        <option value="">全部分销商</option>
        <c:forEach var="ota" items="${otaMap}">
          <option value="${ota.key}" <c:if test="${channelId eq ota.key}">selected</c:if>>${ota.value}</option>
        </c:forEach>
      </select>
    </div>
    <div class="header-button-box">
      <%--<form id="exportForm" action="${ctx}/finance/export/out" method="post">--%>
      <%--<input type="hidden" name="exportSettlementTime" value="${settlementTime}"/>--%>
      <%--</form>--%>

        <a class="red-button-add add" style="margin-right:5px;width: 100px;" href="javascript:batchChannelExportOut()">导出Excel</a>

     <%-- <security:authorize ifAnyGranted="ROLE_出账核算-发送账单">
      </security:authorize>--%>
        <a class="red-button-add add" style="margin-right:5px;width: 100px;"
           href="${ctx}/finance/inn/fill?settlementTime=${settlementTime}&channelId=${channelId}">填充实付金额</a>
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
    <thead>
    <tr>
      <th>城市</th>
      <th>客栈名称<br/>(联系号码)</th>
      <th>收款信息（银行卡）</th>
      <th>订单总数（个）</th>
      <th>客栈订单总金额</th>
      <th>分销商订单总金额</th>
      <th>渠道商结算金额</th>
      <th>番茄收入金额</th>
      <th>客栈结算金额</th>
      <th>实付金额</th>
      <th>
        <select id="isMatch">
          <option value="">确认</option>
          <option value="true" <c:if test="${not empty isMatch && isMatch}">selected</c:if>>账实相符</option>
          <option value="false" <c:if test="${not empty isMatch && !isMatch}">selected</c:if>>账实不符</option>

        </select>
      </th>
    </tr>
    </thead>
    <c:forEach items="${page.result}" var="order">
      <tr>
        <td>
            ${order.financeInnSettlementInfo.regionName}
        </td>
        <td>
          <a style="color: blue"
             href="javascript:showDetail('${order.financeInnSettlementInfo.id}','${settlementTime}')">${order.financeInnSettlementInfo.innName}</a><br/>${order.financeInnSettlementInfo.innContact}

        </td>
        <td>
          <c:choose>
            <c:when test="${not empty order.financeInnSettlementInfo.bankCode}">
              ${order.financeInnSettlementInfo.bankType}:${order.financeInnSettlementInfo.bankCode}/${order.financeInnSettlementInfo.bankAccount}</br>${order.financeInnSettlementInfo.bankName}(${order.financeInnSettlementInfo.bankProvince}/${order.financeInnSettlementInfo.bankCity}/${order.financeInnSettlementInfo.bankRegion})
            </c:when>
            <c:otherwise><em style="color:red">暂无</em></c:otherwise>]
          </c:choose>
        </td>
        <td>${order.totalOrder}</td>
        <td>${order.totalAmount}</td>
        <td>${order.channelAmount}</td>
        <td>${order.channelSettlementAmount}</td>
        <td>${order.fqSettlementAmount}</td>
        <td>${order.innSettlementAmount}</td>
        <td >
          <input type="hidden" name="id" value="${order.id}">
          <input type="hidden" name="realPayment" value="${order.realPayment}">
          <input type="hidden" name="paymentRemark" value="${order.paymentRemark}">
            <a onclick="amountPaid(this)">
            <c:choose>
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
    <c:if test="${not empty allMap and allMap.orders !=0 and allMap.orders != null}">
      截止您所选时间段内，包含 <em style="color:red;"><c:if test="${allMap.ids==null}">0</c:if>${allMap.ids}</em> 家客栈，共有<em style="color:red;"><c:if test="${allMap.orders==null}">0</c:if>${allMap.orders}</em>个订单，客栈订单总金额为<em style="color:red;"><c:if test="${allMap.amounts==null}">0</c:if>${allMap.amounts}</em>，客栈实付金额为<em style="color:red;"><c:if test="${allMap.inns==null}">0</c:if>${allMap.inns}</em>；
    </c:if>
  </p>
  <c:if test="${not empty page}">
    <tags:pagination page="${page}" paginationSize="15"/>
  </c:if>
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


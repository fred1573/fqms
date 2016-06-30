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
    <script src="${ctx}/js/finance/out_arrears_channel.js" type="text/javascript"></script>
    <link href="${ctx}/css/finance/finance.css" rel="stylesheet">
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

        .select-way {
            text-align: center;
            padding: 20px;
        }

        .select-way a {
            padding: 10px;
            font-size: 16px;
            color: blue;
        }
    </style>
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


            <div class="search-box">
                <input type="text" id="innName" maxlength="20" class="search" placeholder="模糊搜索客栈名称"
                       value="${innName}"/>
                <input type="button" id="search_submit" class="search-button">
            </div>
            <select id="settlementTime">
                <option value="">选择账期</option>
                <c:forEach var="period" items="${financeAccountPeriodList}">
                    <option value="${period.settlementTime}"
                            <c:if test="${settlementTime eq period.settlementTime}">selected</c:if>>${period.settlementTime}</option>
                </c:forEach>
            </select>
            <c:if test="${arrearsStatus!=4}">
                <a class="red-button-add add" style="margin-right:5px;margin-top:10px;width: 100px;"
                   href="javascript:arrearsExportOut()">导出Excel</a></c:if>
        </div>

    </div>
    <%@include file="../out_arrears_head.jsp" %>

    <table class="kz-table" cellpadding="8">
        <tr>
            <th>城市</th>
            <th>客栈名称<br/>(联系号码)</th>
            <th>收款信息（银行卡）</th>
            <c:if test="${arrearsStatus!='4'}">
                <th>分销商结算金额(正常订单)</th>
                <th>客栈应结金额(正常订单)</th>
                <th>番茄佣金收入（正常订单）</th>
                <th>分销商实际结算金额</th>
                <th>客栈赔付</th>
                <th>客栈退款</th>
                <th>本期挂账</th>
                <th>往期挂账</th>
            </c:if>
            <th>剩余挂账金额</th>
            <c:if test="${arrearsStatus=='4'}">
                <th>操作</th>
            </c:if>


        </tr>
        <c:forEach items="${page.result}" var="order">
            <tr>
                <td>${order.financeInnSettlementInfo.regionName}</td>
                <td><a style="color: blue"
                       href="${ctx}/finance/out/arrears/channel?settlementTime=${order.settlementTime}&channelOrderNo=${channelOrderNo}&innId=${order.financeInnSettlementInfo.id}&type=arrears&statusType=normal&arrearsStatus=${arrearsStatus}&status=${status}">${order.financeInnSettlementInfo.innName}</a><br/>${order.financeInnSettlementInfo.innContact}
                </td>
                <td>
                    <c:choose>
                        <c:when test="${not empty order.financeInnSettlementInfo.bankCode}">
                            ${order.financeInnSettlementInfo.bankType}:${order.financeInnSettlementInfo.bankCode}/${order.financeInnSettlementInfo.bankAccount}</br>${order.financeInnSettlementInfo.bankName}(${order.financeInnSettlementInfo.bankProvince}/${order.financeInnSettlementInfo.bankCity}/${order.financeInnSettlementInfo.bankRegion})
                        </c:when>
                        <c:otherwise><em style="color:red">暂无</em></c:otherwise>
                    </c:choose>
                </td>
                <c:if test="${arrearsStatus!='4'}">
                    <td>${order.channelSettlementAmount}</td>
                    <td>${order.innSettlementAmount}</td>
                    <td>${order.fqSettlementAmount}</td>
                    <td>${order.channelRealSettlement}</td>
                    <td>${order.innPayment}</td>
                    <td>${order.refundAmount}</td>
                    <td>${order.arrearsRemaining-order.arrearsPast}</td>
                    <td>${order.arrearsPast}</td>
                </c:if>
                <td><a style="color: #0033CC"
                       href="${ctx}/finance/out/past/arrears?innId=${order.financeInnSettlementInfo.id}&settlementTime=${order.settlementTime}&type=arrears&statusType=normal&arrearsStatus=${arrearsStatus}&status=${status}"><c:if
                        test="${order.arrearsRemaining==null}">0.00</c:if>${order.arrearsRemaining}</a></td>
                <c:if test="${arrearsStatus=='4'}">
                    <td>
                        <input type="hidden" name="remaining" value="${order.arrearsRemaining}">
                        <input type="hidden" name="id" value="${order.financeInnSettlementInfo.id}">
                        <a onclick="amountPaid(this)" style="color: #0033CC"> 平账</a></td>
                </c:if>
            </tr>
        </c:forEach>

    </table>
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
                    <span>平账款：</span>
                    <input type="hidden" id="id">
                    <input type="text" class="paid" id="realPayment">
                    <input type="hidden" id="time">
                    <input type="hidden" id="remaining">
                </div>
                <div>
                    <span>备注：</span>
              <textarea class="mark" id="paymentRemark">

              </textarea>
                </div>
                <div style="margin-bottom:20px" class="btn-submit">
                    <button type="button" id="submitPaidData" style="margin-left: 80px;" value="2682"
                            onclick="submitPaidData()">确定
                    </button>
                    <button type="button" onclick="$('#dialogBlackBg').hide();$('#accountPaid').hide();"
                            style="margin-left: 50px;">取消
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>


</body>
</html>



<%--
  Created by IntelliJ IDEA.
  User: 番茄桑
  Date: 2015/8/13
  Time: 10:53
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
    <link href="${ctx}/css/finance/finance.css" rel="stylesheet">
    <script src="${ctx}/js/finance/out_list.js" type="text/javascript"></script>
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
    <form id="mainForm" action="${ctx}/finance/out/list" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <input type="hidden" name="innName" value="${innName}"/>
        <input type="hidden" name="settlementTime" value="${settlementTime}"/>
        <input type="hidden" name="confirmStatus" value="${confirmStatus}"/>
        <input type="hidden" name="settlementStatus" value="${settlementStatus}"/>
        <input type="hidden" name="isMatch" value="${isMatch}"/>
        <input type="hidden" name="isTagged" value="${isTagged}"/>
        <input type="hidden" name="status" value="normal">
    </form>
    <div class="header">
        <%@ include file="../navigation.jsp" %>

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
           <%-- <select id="channelId">
                <option value="">全部分销商</option>
                <c:forEach var="ota" items="${otaMap}">
                    <option value="${ota.key}">${ota.value}</option>
                </c:forEach>
            </select>--%>
        </div>
        <div class="header-button-box">
            <%--<form id="exportForm" action="${ctx}/finance/export/out" method="post">--%>
            <%--<input type="hidden" name="exportSettlementTime" value="${settlementTime}"/>--%>
            <%--</form>--%>

            <a class="red-button-add add" style="margin-right:5px;width: 100px;"
               href="javascript:exportOut()">导出Excel</a>
            <security:authorize ifAnyGranted="ROLE_出账核算一键结算">
                <a class="red-button-add add" style="margin-right:5px;width: 100px;"
                   href="javascript:batchSettlement('${settlementTime}')">一键结算</a>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_出账核算-发送账单">
                <a class="red-button-add add" style="margin-right:5px;width: 100px;"
                   href="javascript:sendBill('${settlementTime}')">发送账单</a>
            </security:authorize>
                <a class="red-button-add add" style="margin-right:5px;width: 100px;" href="${ctx}/finance/export/pay?settlementTime=${settlementTime}">导出批量代付表</a>
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
            <th>客栈订单总金额</th>
            <th>分销商订单总金额</th>
            <th>分销商结算金额</th>
            <th>番茄收入金额</th>
            <th>客栈结算金额</th>
            <th>
                <select id="isMatch">
                    <option value="">实付金额</option>
                    <option value="false" <c:if test="${not empty isMatch && !isMatch}">selected</c:if>>账实不符</option>
                    <option value="true" <c:if test="${not empty isMatch && isMatch}">selected</c:if>>账实相符</option>
                </select>
            </th>
            <th>
                <select id="confirmStatus">
                    <option value="">确认状态</option>
                    <option value="0" <c:if test="${confirmStatus == '0'}">selected</c:if>>未确认</option>
                    <option value="1" <c:if test="${confirmStatus == '1'}">selected</c:if>>已确认</option>
                    <option value="2" <c:if test="${confirmStatus == '2'}">selected</c:if>>系统自动确认</option>
                </select>
            </th>
            <th>
                <select id="settlementStatus">
                    <option value="">操作</option>
                    <option value="0" <c:if test="${settlementStatus eq '0'}">selected</c:if>>未结算</option>
                    <option value="1" <c:if test="${settlementStatus eq '1'}">selected</c:if>>已结算</option>
                    <%--<option value="2" <c:if test="${settlementStatus eq '2'}">selected</c:if>>纠纷延期</option>--%>
                </select>
            </th>
            <th>
                <select id="isTagged">
                    <option value="">标注</option>
                    <option value="true" <c:if test="${isTagged == true}">selected</c:if>>已标注</option>
                    <option value="false" <c:if test="${isTagged == false}">selected</c:if>>未标注</option>
                </select>
            </th>
        </tr>
        </thead>
        <c:forEach items="${page.result}" var="order">
            <tr>
                <td>${order.financeInnSettlementInfo.regionName}</td>

                <td><a style="color: blue"
                       href="javascript:showDetail('${order.financeInnSettlementInfo.id}','${settlementTime}','${status}')">${order.financeInnSettlementInfo.innName}</a><br/>${order.financeInnSettlementInfo.innContact}
                </td>
                <td>
                    <c:choose>
                        <c:when test="${not empty order.financeInnSettlementInfo.bankCode}">
                            ${order.financeInnSettlementInfo.bankType}:${order.financeInnSettlementInfo.bankCode}/${order.financeInnSettlementInfo.bankAccount}</br>${order.financeInnSettlementInfo.bankName}(${order.financeInnSettlementInfo.bankProvince}/${order.financeInnSettlementInfo.bankCity}/${order.financeInnSettlementInfo.bankRegion})
                        </c:when>
                        <c:otherwise><em style="color:red">暂无</em></c:otherwise>
                    </c:choose>
                </td>
                <td>${order.totalAmount}</td>
                <td>${order.channelAmount}</td>
                <td>${order.channelSettlementAmount}</td>
                <td>${order.fqSettlementAmount}</td>
                <td>${order.innSettlementAmount}</td>
                <td>
                    <c:choose>
                        <c:when test="${not empty order.payment}">
                            ${order.payment}
                        </c:when>
                        <c:otherwise>--</c:otherwise>
                    </c:choose>

                </td>
                <td>
                    <c:choose>
                        <c:when test="${order.confirmStatus == '1'}">
                            已确认
                        </c:when>
                        <c:when test="${order.confirmStatus == '2'}">
                            系统自动确认
                        </c:when>
                        <c:otherwise>未确认</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose >
                        <c:when test="${order.settlementStatus eq '1'}">
                            <a style="color: blue"
                               href="javascript:settlement('${order.id}','${order.financeInnSettlementInfo.innName}','${order.settlementStatus}','${settlementTime}','${order.financeInnSettlementInfo.id}')">已结算</a>
                        </c:when>
                        <c:when test="${order.settlementStatus eq '0'}">
                            <security:authorize ifAnyGranted="ROLE_出账核算-未结算">
                                <a  style="color: blue"
                                   href="javascript:settlement('${order.id}','${order.financeInnSettlementInfo.innName}','${order.settlementStatus}','${settlementTime}','${order.financeInnSettlementInfo.id}')">未结算</a>
                            </security:authorize>
                        </c:when>
                        <c:otherwise>
                            <security:authorize ifAnyGranted="ROLE_出账核算-未结算">
                                <a  style="color: blue"
                                   href="javascript:settlement('${order.id}','${order.financeInnSettlementInfo.innName}','${order.settlementStatus}','${settlementTime}','${order.financeInnSettlementInfo.id}')" >纠纷延期</a>
                            </security:authorize>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <security:authorize ifAnyGranted="ROLE_出账核算-打标签">
                        <c:choose>
                            <c:when test="${order.isTagged}">
                                <a style="color: blue"
                                   href="javascript:tagInn('${order.id}','${order.financeInnSettlementInfo.innName}',false)">已标注</a>
                            </c:when>
                            <c:otherwise>
                                <a style="color: blue"
                                   href="javascript:tagInn('${order.id}','${order.financeInnSettlementInfo.innName}',true)">未标注</a>
                            </c:otherwise>
                        </c:choose>
                    </security:authorize>
                </td>
            </tr>
        </c:forEach>
    </table>
    <p>
        <c:if test="${not empty allMap and allMap.orders !=0 and allMap.orders != null}">
            截止您所选时间段内，共有<em style="color:red;">${allMap.orders}</em>个订单，分销商结算金额为<em style="color:red;">${allMap.channels}</em>，番茄收入金额为<em
            style="color:red;">${allMap.fqs}</em>，客栈结算金额为<em style="color:red;">${allMap.inns}</em>；
        </c:if>
        <c:if test="${not empty unSettlementMap and unSettlementMap.orders !=0 and unSettlementMap.orders != null}">
            其中<em style="color:red;">${unSettlementMap.orders}</em>个订单未结算，分销商结算金额为<em style="color:red;">${unSettlementMap.channels}</em>，番茄收入金额为<em
            style="color:red;">${unSettlementMap.fqs}</em>，客栈未结算金额为<em style="color:red;">${unSettlementMap.inns}</em>。
        </c:if>
    </p>
    <c:if test="${not empty page}">
        <tags:pagination page="${page}" paginationSize="15"/>
    </c:if>
</div>

<div id="dialogBlackBg" style="display:none;">
    <div class="center-box">
        <div class="center-box-in audit-window" id="disputesDelay">
            <a class="close-window" onclick="$('#dialogBlackBg').hide();$('#disputesDelay').hide();"></a>

            <div id="AnnName" class="ann-name"></div>

            <div class="dialog-content">
                <input type="hidden" id="idNum">
                <input type="hidden" id="time">
                <input type="hidden" id="bstatus">
                <input type="hidden" id="innId">
                <div class="select-way" id="Settlement">
                    <a onclick="confirmSettlement(this)" id="balanced">已结算</a>
                    <a onclick="confirmSettlement(this)" id="dispute">纠纷延期</a>
                    <a onclick="confirmSettlement(this)" id="notBalance">未结算</a>
                </div>
                <div class="confirm-settlement" id="confirmSettlement" style="display: none;">
                    <div style="padding:10px;text-align:center;font-size:16px">您确定将该客栈设置为<span id="settlementWay"
                                                                                               style="float:none;width:100px;margin-left:0"></span>
                    </div>
                    <div style="margin-bottom:20px; text-align:center;" class="btn-submit">
                        <button type="button" id="" value="" onclick="confirmSelct()">确定</button>
                        <button type="button" onclick="$('#dialogBlackBg').hide();$('#disputesDelay').hide();"
                                style="margin-left: 50px;">取消
                        </button>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>


</body>
</html>

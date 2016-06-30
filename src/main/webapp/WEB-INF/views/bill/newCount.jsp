<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
    <title>番茄小站对账统计</title>
    <link rel="stylesheet" href="${ctx}/css/zTreeStyle/zTreeStyle.css" type="text/css">
    <script type="text/javascript">
    var ctx = '${ctx}';
    </script>
    <script src="${ctx}/js/ztree/jquery.ztree.all-3.5.min.js" type="text/javascript"></script>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/bill/bill.js" type="text/javascript"></script>
</head>
<body class="bg1">
<!--确认结算-->
<div class="center-box">
    <div class=" center-box-in audit-window confirmEnd" style="display:none;top: -200px" id="msgbox">
        <h2>确认结算？</h2>
        <span>
        	<a href="javascript:void(0)" id="changeBtn" class="audit-pass-button">确认</a>
        	<a href="#" onclick="$('#msgbox').hide()" class="audit-nopass-button">取消</a>
        </span>
    </div>
</div>
<!--右边内容区域-->
<div class="container-right">
    <div class="header">
        <div style="margin-left: 20px">
            <button urls="${ctx}/bill/count"
                    class="kc-btn kc-active"
                    >代收对账
            </button>
            <button urls="${ctx}/bill/cashpay"
                    class="kc-btn"
                    >现付对账
            </button>
            <button urls="${ctx}/bill/getFinancialAccount" class="kc-btn">财务对账
			</button>
        </div>

        <div class="header-button-box duizhang" style="width: 600px">
            <select id="searchCondition" style="position: relative;height: 35px;font-size: 1.2em">
                <option value="0"
                        <c:if test="${billSearchBean.searchCondition==0}">selected="selected"</c:if> >客栈名称
                </option>
                <option value="1"
                        <c:if test="${billSearchBean.searchCondition==1}">selected="selected"</c:if>  >订单号码
                </option>
            </select>

            <div class="search-box" style="position: relative;">
                <input type="text" id="keyWord" class="search" maxlength="20" id="search_input"
                       value="${billSearchBean.keyWord}">
                <input type="button" onclick="cleanInnIdAndSearch()" class="search-button" id="search_submit">
            </div>
            <div class="date-date" style="width: 300px;padding: 0">
                <a id="startDate" style="float: none;display: inline-block"
                   href="javascript:void(0)" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d}',onpicked:changeStartDate})"
                   class="date WdateFmtErr WdateFmtErr WdateFmtErr">
                    <fmt:formatDate value="${billSearchBean.startDate}" pattern="yyyy-MM-dd"/>
                </a>
                至
                <a id="endDate" onchange="changeEndDate()"
                   style="float: none;display: inline-block;position: relative;left: 12px;" href="javascript:void(0)"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d}',onpicked:changeEndDate})" class="date WdateFmtErr WdateFmtErr WdateFmtErr">
                    <fmt:formatDate value="${billSearchBean.endDate}" pattern="yyyy-MM-dd"/>
                </a>
            </div>

        </div>

    </div>
    <!--end header-->
    <div class="content inn-cehckList duizhang-table">
        <table cellpadding="8">
            <tr>
                <th>客栈/支付宝账号</th>
                <th>
                <select id="productCode" name="productCode" style="position: relative;height: 35px;font-size: 1.2em">
	                <option value=""
	                        <c:if test="${billSearchBean.productCode==''}">selected="selected"</c:if> >类型
	                </option>
	                <option value="xz_order"
	                        <c:if test="${billSearchBean.productCode=='xz_order'}">selected="selected"</c:if> >小站下单
	                </option>
	                <option value="xz_fast_checkIn"
	                        <c:if test="${billSearchBean.productCode=='xz_fast_checkIn'}">selected="selected"</c:if> >快速入住
	                </option>
	                <option value="checkstand"
	                        <c:if test="${billSearchBean.productCode=='checkstand'}">selected="selected"</c:if>  >扫码支付
	                </option>
            	</select>
                </th>
                <th>订单号</th>
                <th>订单信息</th>
                <th>已付金额</th>
                <th>支付时间</th>
                <th>
                <select id="isBalance" name="isBalance" style="position: relative;height: 35px;font-size: 1.2em">
	                <option value="-1"
	                        <c:if test="${billSearchBean.isBalance==-1}">selected="selected"</c:if> >平账情况
	                </option>
	                <option value="0"
	                        <c:if test="${billSearchBean.isBalance==0}">selected="selected"</c:if> >未平账
	                </option>
	                <option value="1"
	                        <c:if test="${billSearchBean.isBalance==1}">selected="selected"</c:if>  >已平账
	                </option>
            	</select>
                </th>
            </tr>
            <c:forEach var="detailBean" items="${page.result}">
                <tr>
                    <td><a href="javascript:void(0)" style="color: #000000"
                           onclick="searchByInnId('${detailBean.innId}')">${detailBean.innName}</a>
                        <br/>
                            <hr style="border:1;margin: 0">
                            ${detailBean.ZFBName} 
                    </td>
                    <td>${detailBean.productName}</td>
                    <td>${detailBean.orderNo}</td>
                    <td>${detailBean.orderInfos}</td>
                    <td id="totalAmount${detailBean.orderNo}">${detailBean.totalAmount}</td>
                    <td><fmt:formatDate value="${detailBean.payAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td>
                            <c:if test="${detailBean.isBalance=='0'}">
                            	未结算
                            </c:if>
                            <c:if test="${detailBean.isBalance=='1'}">
                               	已结算
                            </c:if>
                        </td>
                </tr>
            </c:forEach>
        </table>
        <p class="acount">
                 	截止您所选时间段内，共有 <span style="color: red">${paramMap.orderNum}</span> 
                	个订单，总金额为 
        			<span style="color: red">
                		<fmt:formatNumber type="number" value="${paramMap.allMoney}" maxFractionDigits="2"/>
                	</span>，其中 
                	<span style="color: red" id="notBalanceOrders">${paramMap.unbalanceNum}</span>
                	个订单未结算，未结算金额为 
                	<span style="color: red" id="notBalanceAmount">
                		<fmt:formatNumber type="number" value="${paramMap.unbalanceMoney}" maxFractionDigits="3"/>
                	</span>。
        </p>
        <c:if test="${null!=innDetail}">
            <p class="acount">
                <c:if test="${null!=innDetail.innName}">客栈名：${innDetail.innName}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.contactName}"> 联系人：${innDetail.contactName}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.contact}"> 电话号码：${innDetail.contact}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.alipayCode}"> 支付宝账号：${innDetail.alipayCode}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.alipayUser}"> 支付宝开户人：${innDetail.alipayUser}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <br/>
                <c:if test="${null!=innDetail.bankName}"> 银行名称：${innDetail.bankName}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.bankCard}"> 银行账号：${innDetail.bankCard}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.bankCardHolder}"> 开户人：${innDetail.bankCardHolder}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.bankArea}"> 所在地区：${innDetail.bankArea}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
            </p>
        </c:if>
        <tags:pagination page="${page}" paginationSize="5"/>
    </div>
    <!--end content-->
    <form action="${ctx}/bill/count" method="post" id="mainForm">
        <input type="hidden" name="innId" value="${innId}"/>
        <input type="hidden" name="isBalance" value="${billSearchBean.isBalance}"/>
        <input type="hidden" name="productCode" value="${billSearchBean.productCode}"/>
        <input type="hidden" name="totalPage" value="${billSearchBean.totalPage}"/>
        <input type="hidden" name="nowPage" value="${billSearchBean.nowPage}"/>
        <input type="hidden" name="searchCondition" value="${billSearchBean.searchCondition}"/>
        <input type="hidden" name="keyWord" value="${billSearchBean.keyWord}"/>
        <input type="hidden" name="collection" value="${billSearchBean.collection}"/>
        <input type="hidden" name="startDate"
               value=" <fmt:formatDate value="${billSearchBean.startDate}" pattern="YYYY-MM-dd"/>"/>
        <input type="hidden" name="endDate"
               value=" <fmt:formatDate value="${billSearchBean.endDate}" pattern="YYYY-MM-dd"/>"/>
    </form>
</div>
<!--end container-right-->
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
	<title>快速入住对账统计</title>
	<link rel="stylesheet" href="${ctx}/css/zTreeStyle/zTreeStyle.css" type="text/css">
	<script type="text/javascript">
    	var ctx = '${ctx}';
    </script>
	<script src="${ctx}/js/ztree/jquery.ztree.all-3.5.min.js" type="text/javascript"></script>
	<script src="${ctx}/js/common/form.js" type="text/javascript"></script>
	<script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
	<script src="${ctx}/js/bill/financial.js" type="text/javascript"></script>
</head>
<body class="bg1">
	<!--确认结算-->
	<div class="center-box">
		<div class=" center-box-in audit-window confirmEnd"
			style="display: none; top: -200px" id="msgbox">
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
			<h1>
				<button urls="${ctx}/bill/count" class="kc-btn">代收对账
				</button>
				<button urls="${ctx}/bill/cashpay" class="kc-btn">现付对账
				</button>
				<button urls="${ctx}/bill/getFinancialAccount" class="kc-btn kc-active">财务对账
				</button>
			</h1>

			<div class="header-button-box duizhang" style="width: 600px">
				<select id="searchCondition"
					style="position: relative; height: 35px; font-size: 1.2em">
					<option value="0"
						<c:if test="${billSearchBean.searchCondition==0}">selected="selected"</c:if>>客栈名称
					</option>
					<option value="1"
						<c:if test="${billSearchBean.searchCondition==1}">selected="selected"</c:if>>订单号码
					</option>
				</select>

				<div class="search-box" style="position: relative;">
					<input type="text" id="keyWord" class="search" maxlength="20" value="${billSearchBean.keyWord}"> 
					<input type="button" onclick="cleanInnIdAndSearch()" class="search-button" id="search_submit">
				</div>
				<div class="date-date" style="width: 300px; padding: 0">
					<a id="startDate" style="float: none; display: inline-block" href="javascript:void(0)"
						onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d}',onpicked:changeStartDate})"
						class="date WdateFmtErr WdateFmtErr WdateFmtErr"> 
						<fmt:formatDate value="${billSearchBean.startDate}" pattern="yyyy-MM-dd" /></a> 至 
					<a id="endDate" onchange="changeEndDate()" style="float: none; display: inline-block; position: relative; left: 12px;"
						href="javascript:void(0)" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d}',onpicked:changeEndDate})"
						class="date WdateFmtErr WdateFmtErr WdateFmtErr"> 
						<fmt:formatDate value="${billSearchBean.endDate}" pattern="yyyy-MM-dd" />
					</a>
				</div>
				<a href="javascript:exportToExcel('main_table');" class="button_yuding">&nbsp;导出excel</a>
			</div>

		</div>
		<!--end header-->
		<div class="content inn-cehckList duizhang-table">
			<table id="main_table" cellpadding="8">
				<tr>
					<th>客栈名称 <br/>（联系号码/联系人）</th>
					<th>收款信息<br/>（支付宝/财付通/银行卡）</th>
					<th>
					<select id="payMode"
					style="position: relative; height: 35px; font-size: 1.2em">
						<option value="-1"
							<c:if test="${billSearchBean.payMode==-1}">selected="selected"</c:if>>进账方式
						</option>
						<option value="1"
							<c:if test="${billSearchBean.payMode==1}">selected="selected"</c:if>>支付宝
						</option>
						<option value="2"
							<c:if test="${billSearchBean.payMode==2}">selected="selected"</c:if>>财付通
						</option>
					</select>
					</th>
					<th>订单号</th>
					<th>手续费</th>
					<th>结算金额</th>
					<th>小计</th>
					<th>
					<select id="payBalance"
					style="position: relative; height: 35px; font-size: 1.2em">
						<option value="-1"
							<c:if test="${billSearchBean.isBalance==-1}">selected="selected"</c:if>>操作
						</option>
						<option value="0"
							<c:if test="${billSearchBean.isBalance==0}">selected="selected"</c:if>>未结算
						</option>
						<option value="1"
							<c:if test="${billSearchBean.isBalance==1}">selected="selected"</c:if>>已结算
						</option>
					</select>
					</th>
				</tr>
				<c:forEach var="detailBean" items="${page.result}">
					<tr>
						<td class="tag_inn_name" innId="${detailBean.id}">${detailBean.innname} <br/> ${detailBean.contacts}</td>
						<td class="tag_account" innId="${detailBean.id}">${detailBean.aliaccount} <br/> ${detailBean.tenpayaccount} <br/>${detailBean.bankaccount}</td>
						<td class="tag_payWay" innId="${detailBean.id}" status="${detailBean.status}" payId="${detailBean.payid}">
						<c:if test="${detailBean.incometype == '1'}">支付宝</c:if>
						<c:if test="${detailBean.incometype == '2'}">财付通</c:if>
						</td>
						<td><c:if test="${detailBean.incometype == '1'}">TB</c:if>${detailBean.orderno}</td>
						<td>
						<fmt:formatNumber value="${detailBean.poundage}" pattern="##.##" minFractionDigits="2" ></fmt:formatNumber>
						</td>
						<td class="tag_accountfee" innId="${detailBean.id}" status="${detailBean.status}" >${detailBean.accountfee}</td>
						<td class="tag_total" innId="${detailBean.id}" status="${detailBean.status}"></td>
						<td class="tag_do" innId="${detailBean.id}" status="${detailBean.status}">
							<c:if test="${detailBean.status == 0}">
								<a href="javascript:void(0)"
									class="audit-nopass-button"
									<security:authorize ifNotGranted="ROLE_小站对账">style="background: gray" </security:authorize>>未结算</a>
							</c:if>
							<c:if test="${detailBean.status == 1}">
								<a href="javascript:void(0)" class="audit-pass-button">已结算</a>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</table>
			<p class="acount">
                	截止您所选时间段内，共有 <span style="color: red">${billSearchBean.countBean.totalOrders}</span> 个订单，总金额为 
                	<span style="color: red">
                		<fmt:formatNumber type="number" value="${billSearchBean.countBean.totalAmount}" maxFractionDigits="2"/>
                	</span>，其中 
                	<span style="color: red" id="notBalanceOrders">${billSearchBean.countBean.notBalanceOrders}</span>个订单未结算，未结算金额为 
                	<span style="color: red" id="notBalanceAmount">
                		<fmt:formatNumber type="number" value="${billSearchBean.countBean.notBalanceAmount}" maxFractionDigits="3"/>
                	</span>。
            </p>
			<tags:pagination page="${page}" paginationSize="5" />
		</div>
		<!--end content-->
		<form action="${ctx}/bill/getFinancialAccount" method="post" id="mainForm">
			<input type="hidden" name="payMode" value="${billSearchBean.payMode}" />
			<input type="hidden" name="isBalance" value="${billSearchBean.isBalance}" />
			<input type="hidden" name="innId" value="${innId}" />
			<input type="hidden" name="type" value="${billSearchBean.type}" />
			<input type="hidden" name="totalPage" value="${billSearchBean.totalPage}" />
			<input type="hidden" name="nowPage" value="${billSearchBean.nowPage}" />
			<input type="hidden" name="searchCondition" value="${billSearchBean.searchCondition}" /> 
			<input type="hidden" name="keyWord" value="${billSearchBean.keyWord}" /> 
			<input type="hidden" name="startDate" value=" <fmt:formatDate value="${billSearchBean.startDate}" pattern="YYYY-MM-dd"/>" />
			<input type="hidden" name="endDate" value=" <fmt:formatDate value="${billSearchBean.endDate}" pattern="YYYY-MM-dd"/>" />
		</form>
	</div>
	<!--end container-right-->
</body>
</html>

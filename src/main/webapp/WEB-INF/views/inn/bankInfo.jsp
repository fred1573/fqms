<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
	<link rel="stylesheet" media="screen" href="http://assets.fanqiele.com/core/css/tomasky.core.all.css">
	<link rel="stylesheet" href="${ctx}/js/select2/select2.css">
	<style>
		.audit-window {
			width: 560px;
			padding: 0 20px;
		}
		.audit-window p {
			text-align: left;
		}
		.ipt-common-style {
			height: 30px;
			text-indent: 10px;
		}
		.select2-container {
			margin-right: 15px;
		}
		.table {
			width: 100%;
			max-width: 100%;
			border: 1px solid #e0e0e0;
			table-layout: fixed;
		}

		.table > thead,
		.table .table-head {
			background: #f2f2f2;
		}

		.table > thead > tr,
		.table .table-head {
			line-height: 40px;
			font-weight: 700;
		}

		.table > thead > tr.table-white {
			background-color: white;
		}

		.table > thead > tr > th {
			text-align: left;
			vertical-align: middle;
		}

		.table > thead > tr,
		.table > tbody > tr {
			border-top: 1px solid #e0e0e0;
			height: 30px;
			line-height: 30px;
		}

		.table > thead > tr > td,
		.table > thead > tr > th,
		.table > tbody > tr > td,
		.table > tbody > tr > th {
			padding: 8px;
			line-height: 1.42857143;
			vertical-align: middle;
			word-break: break-all;
			text-align: left;
		}

		.table > thead > tr > td.table-right,
		.table > thead > tr > th.table-right,
		.table > tbody > tr > td.table-right,
		.table > tbody > tr > th.table-right {
			text-align: right !important;
		}

		.table > thead > tr > td.table-center,
		.table > thead > tr > th.table-center,
		.table > tbody > tr > td.table-center,
		.table > tbody > tr > th.table-center {
			text-align: center !important;
		}

		.table > thead > tr > td.table-center,
		.table > tbody > tr > td.table-center {
			cursor: pointer;
		}

		.table > thead > tr > td.table-cursor,
		.table > thead > tr > th.table-cursor,
		.table > tbody > tr > td.table-cursor,
		.table > tbody > tr > th.table-cursor {
			cursor: pointer;
		}

		.table-striped > tbody > tr:nth-child(even) {
			background-color: #f6f6f6;
		}

		.table-hover > tbody > tr:hover {
			background-color: #f5f5f5;
		}
		.table-bordered > thead > tr > td,
		.table-bordered > thead > tr > th,
		.table-bordered > tbody > tr > td,
		.table-bordered > tbody > tr > th {
			border-right: 1px solid #e0e0e0;
		}

		.table-nowrap > thead > tr > td,
		.table-nowrap > tbody > tr > td,
		.table-nowrap > tbody > tr > th {
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}

		.table > thead > tr > th .tomasky-tomasky-ui-dropdown {
			width: 100%;
			vertical-align: top;
		}

		.table > thead > tr > th .tomasky-ui-dropdown > a {
			height: 22px;
			line-height: 22px;
			white-space: normal;
			overflow: hidden;
		}

		.table > thead > tr > th .tomasky-ui-dropdown-list > a {
			height: 25px;
			line-height: 25px;
		}
	</style>
	<title>结算信息</title>
	</head>
	<body id="body">
		<div ms-controller="vm_bank" id="bank_info_edit_div" class="center-box" style="display: none;">
			<div  class=" center-box-in audit-window" >
				<input type="hidden" ms-duplex="inn.id"/>
		    	<h3 style="text-align:center;font-size:16px;padding:10px 0">编辑客栈结算资料</h3>
				<p>
					支付宝信息：&nbsp;&nbsp;
					<input type="text" ms-duplex="inn.alipayCode" placeholder="支付宝账号" class="ipt-common-style"/> <input type="text" placeholder="支付宝开户人姓名" ms-duplex="inn.alipayUser" class="ipt-common-style"/>
				</p>
				<p>
					财付通信息：&nbsp;&nbsp;
					<input type="text" ms-duplex="inn.tenpayCode" placeholder="财付通账号" class="ipt-common-style"/> <input type="text" placeholder="财付通开户人姓名" ms-duplex="inn.tenpayUser" class="ipt-common-style"/>
				</p>
				<p>
	          		账户类型:&nbsp;&nbsp;<input type="radio" name="bankType" value="1" ms-click="changeBankType(1)" ms-attr-checked="inn.bankType == 1" class="ipt-common-style">个人账户&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" ms-click="changeBankType(2)" name="bankType" value="2" ms-attr-checked="inn.bankType == 2" class="ipt-common-style">公司账户&nbsp;&nbsp;&nbsp;&nbsp;
	          	</p>
				<p>
					户      名：&nbsp;&nbsp;
					<input type="text" ms-duplex="inn.bankAccount" class="ipt-common-style" />
				</p>
				<p>
					账 户 号 码：&nbsp;&nbsp;
					<input type="text" ms-duplex="inn.bankCode" class="ipt-common-style" />
				</p>
				<p>
					银行名称：&nbsp;&nbsp;
					<input id="bank-name" type="text" ms-duplex="inn.bankName" style="width:150px;display: block;" />（{{inn.bankName}}）
				</p>
				<p>
					开户支行：&nbsp;&nbsp;
					<input id="bank-province" type="hidden" ms-duplex="inn.bankProvince" style="width:120px;display: block;" /><input id="bank-city" type="hidden" ms-duplex="inn.bankCity" style="width:120px;display: block;" /><input type="text" ms-duplex="inn.bankRegion" class="ipt-common-style" />
				</p>
		        <span>
		        	<a ms-click="updateBankInfo()" href="javascript:void(0)" class="reset-button">确认</a>
		        	<a ms-click="clear()" href="javascript:closeByClass('center-box')" class="audit-nopass-button">取消</a>
		        </span>
		    </div>
		</div>
		<div ms-controller="vm_bank" class="container-right">
			<div class="header">
				<h1>结算信息</h1>
		        <div class="header-button-box">
					<input type="hidden" id="ctx" value="${ctx}" />
		            <input type="hidden" name="pageNo" id="pageNo" ms-duplex="page.pageNo" />
					<input type="hidden" name="orderBy" id="orderBy" ms-duplex="page.orderBy" />
					<input type="hidden" name="order" id="order" ms-duplex="page.order" />
		            <div class="search-box" style="margin-top:20px">
		             	<input id="innName" name="keyWord" ms-duplex="datas.keyWord" type="text" class="search" placeholder="注册账号" maxlength="20"/>
		        		<input ms-attr-innid="1" ms-click="searchInnInfo()" type="button" class="search-button"/>
		             </div>
		            <div class="search-box" style="margin-top:25px;">
		            	<input name="filter" ms-click="searchInnInfo()" type="checkbox" ms-duplex="datas.isFilt" />
		            	过滤银行卡信息为空
		            </div>
		        </div>
		     </div>
		     
			 <div class="content">
				 <table class="111" id="list_table">
				 	<thead>
				 		<tr><th>客栈名称</th><th>注册账号</th><th>财物结算信息</th><th>操作</th></tr>
				 	</thead>
				 	<tbody>
					 	<tr ms-repeat-el="inns">
					 		<td>{{el.name}}</td>
					 		<td>{{el.mobile}}</td>
					 		<td>{{el.bankAccount}}({{el.bankCode}})</td>
					 		<td>编辑</td>
					 	</tr>
				 	</tbody>
				 </table>
				 <div id="page_div"></div>
			</div>
		</div>
		<script src="http://assets.fanqiele.com/core/js/tomasky/ui/widgets/page/widget.page.js"></script>
		<script src="${ctx}/js/common/avalon-1.4.3.min.js" type="text/javascript"></script>
		<script src="${ctx}/js/select2/select2.js" type="text/javascript"></script>
		<script src="${ctx}/js/inn/bank-info.js" type="text/javascript"></script>
	</body>
</html>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.project.utils.CacheUtil"%>

<html>
<head>
<title>功能使用统计报表</title>
<script src="${ctx}/js/common/form.js" type="text/javascript"></script>
<script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<script src="${ctx}/js/report/funcReport.js" type="text/javascript"></script>
</head>
<body>
    <!--右边内容区域-->
    <div class="container-right">
    	<div class="header">
        	<h1>报表统计</h1>
        	<div class="header-button-box">
                <a id="funcReport-date" href="javascript:void(0)" 
                onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d',onpicked:searchWithDate})" class="date">
                	${searchBean.selectDate}
                </a>
            </div>
        </div><!--end header-->
        <div class="content inn-count">
        	<div class="table">
        		<div class="thead">
        			<table cellpadding="8">
        	<tr>
        		<th rowspan="3" colspan="2">相关功能</th>
        		<th></th>
        		<th colspan="14">报表统计</th>
        	</tr>
        	<tr>
        		<th></th>
        		<c:forEach items="${items}" var="i">
        		<th colspan="2">${ i }</th>
        		</c:forEach>
        	</tr>
        		<tr>
        			<th></th>
        			<th>使用量</th>
        			<th>占比</th>
        			<th>使用量</th>
        			<th>占比</th>
        			<th>使用量</th>
        			<th>占比</th>
        			<th>使用量</th>
        			<th>占比</th>
        			<th>使用量</th>
        			<th>占比</th>
        			<th>使用量</th>
        			<th>占比</th>
        			<th>使用量</th>
        			<th>占比</th>
        		</tr>
        	</table>
        		</div>
        		<div class="tbody">
        			<table cellpadding="8">
        				<tbody><tr>
                        	<td colspan="2">锁屏</td>
                        	<c:forEach items="${items}" var="i">
                        	<td>${reportMap.get((i.concat("-").concat("10001"))).applicationAmount}</td>
        					<td>${reportMap.get(i.concat("-").concat("10001")).innPercent}</td>
                        	</c:forEach>
        				</tr>
        					<tr>
        					<td colspan="2">发通知</td>
        					<c:forEach items="${items}" var="i">
                        	<td>${reportMap.get(i.concat("-").concat("10002")).applicationAmount}</td>
        					<td>${reportMap.get(i.concat("-").concat("10002")).innPercent}</td>
                        	</c:forEach>
        				</tr>
        				<tr>
        					<td colspan="2">房型排序</td>
        					<c:forEach items="${items}" var="i">
                        	<td>${reportMap.get(i.concat("-").concat("10003")).applicationAmount}</td>
        					<td>${reportMap.get(i.concat("-").concat("10003")).innPercent}</td>
                        	</c:forEach>
        				</tr>
        				<c:forEach items="${plugFuncs}" var="p" varStatus="status">
        				<tr>
        					<td colspan="2"><sw:write type="report_item_type" value="${p.id}"></sw:write></td>
        					<c:forEach items="${items}" var="i">
                        	<td>${reportMap.get(i.concat("-").concat(p.id)).applicationAmount}</td>
        					<td>${reportMap.get(i.concat("-").concat(p.id)).innPercent}</td>
                        	</c:forEach>
        				</tr>
                        </c:forEach>
        				<c:forEach items="${otas}" var="p" varStatus="status">
        				<tr>
        					<td>对接OTA</td>
        					<td><sw:write type="report_item_type" value="${p.id + 1000}"></sw:write></td>
        					<c:forEach items="${items}" var="i">
                        	<td>${reportMap.get(i.concat("-").concat(p.id + 1000)).applicationAmount}</td>
        					<td>${reportMap.get(i.concat("-").concat(p.id + 1000)).innPercent}</td>
                        	</c:forEach>
        				</tr>
                        </c:forEach>
        				<tr>
        					<td>房态风格</td>
        					<td><sw:write type="report_item_type" value="2001"></sw:write></td>
        					<c:forEach items="${items}" var="i">
                        	<td>${reportMap.get(i.concat("-").concat("2001")).applicationAmount}</td>
        					<td>${reportMap.get(i.concat("-").concat("2001")).innPercent}</td>
                        	</c:forEach>
        				</tr>
        				<tr>
        					<td>房态风格</td>
        					<td><sw:write type="report_item_type" value="2002"></sw:write></td>
        					<c:forEach items="${items}" var="i">
                        	<td>${reportMap.get(i.concat("-").concat("2002")).applicationAmount}</td>
        					<td>${reportMap.get(i.concat("-").concat("2002")).innPercent}</td>
                        	</c:forEach>
        				</tr>
        				<tr>
        					<td>房态风格</td>
        					<td><sw:write type="report_item_type" value="2004"></sw:write></td>
        					<c:forEach items="${items}" var="i">
                        	<td>${reportMap.get(i.concat("-").concat("2004")).applicationAmount}</td>
        					<td>${reportMap.get(i.concat("-").concat("2004")).innPercent}</td>
                        	</c:forEach>
        				</tr>
        				<tr>
        					<td colspan="2">活跃用户数</td>
        					<c:forEach items="${items}" var="i">
                        	<td>${reportMap.get(i.concat("-").concat("10004")).applicationAmount}</td>
        					<td>${reportMap.get(i.concat("-").concat("10004")).innPercent}</td>
                        	</c:forEach>
        				</tr>
        				<tr>
        					<td colspan="2">连锁运营</td>
        					<c:forEach items="${items}" var="i">
                        	<td>${reportMap.get(i.concat("-").concat("10005")).applicationAmount}</td>
        					<td>${reportMap.get(i.concat("-").concat("10005")).innPercent}</td>
                        	</c:forEach>
        				</tr>
        			</tbody></table>
        		</div>
        		
        	</div>
        	<div class="info" style="display:none"> 
        		<h1>相关统计规则说明：</h1>
        		<ol>
        			<li>数据更新，每天凌晨12:00更新数据，查看报表则点击刷新，可同步最新数据。</li>
        			<li>日期选择，默认显示当前日期，当选择其中某一天时，报表则自动读取前7天的数据，例如：选择2014-05-23，则读取16-22的数据。</li>
        			<li>当前使用统计锁屏、发通知、房型排序，功能库中所有功能（14个），对接OTA，房态风格选择的用户总量，占比为当前使用该功能的用户总量/已注册所有用户总量。</li>
        			<li>活跃用户数，统计一周内办过订单的客栈，占比为一周内办过订单的用户总量/已注册所有用户总量。</li>
        			<li>连锁运营，统计客栈具有从属关系用户的总量，占比为客栈具有从属关系用户的总量/已注册所有用户总量。</li>
        		</ol>
        		
        	</div>
        	<form id="mainForm" action="${ctx}/funcReport/index" method="post">
        	<input type="hidden" name="selectDate" id="selectDate" value="3453" />
        	</form>
        </div><!--end content-->
    </div>
</body>
</html>
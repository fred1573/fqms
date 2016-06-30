<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<title>活跃报表</title>
<script src="${ctx}/js/common/form.js" type="text/javascript"></script>
<script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<script src="${ctx}/js/report/region-radio.js" type="text/javascript"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
</script>
</head>
<body>
    <!--右边内容区域-->
	<div class="container-right">
    	<div class="header">
        	<h1>统计分析</h1>
        	<div class="date-choose" style="left: 65%;">
        		<span class="cursor" ><span id="from" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d}',onpicked:searchWithFromDate})">${searchBean.fromDate}</span><em></em></span>
        		至
        		<span class="cursor"><span id="to" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d}',onpicked:searchWithToDate})">${searchBean.toDate}</span><em></em></span>
        		<button style="display:none;">导出EXCEL</button>
        	</div>
        </div><!--end header-->
        <div class="content inn-count hh">
        	<div style="width: 100%;overflow: auto;">
        		<table cellpadding="8">
	        		<thead>
	        			<tr>
	        				<th><div>日期</div></th>
	        				<c:forEach items="${timelineCells}" var="t">
	        				<fmt:formatDate value="${t.cdate}" pattern="yyyy-MM-dd" var="tt" />
	        				<th colspan="3">${tt}</th>
	        				</c:forEach>
	        			</tr>
	        			<tr>
	        				<th><div>地区</div></th>
	        				<c:forEach items="${timelineCells}" var="t">
	        				<th><div>地区入住率</div></th>
	        				<th><div>平均房间价</div></th>
	        				<th><div>入住房间数</div></th>
	        				</c:forEach>
	        			</tr>
	        		</thead>
	        		<c:forEach items="${regions}" var="r">
	        		<tr>
	        			<td style="font-weight: bold;">${r.name}</td>
	        			<c:forEach items="${timelineCells}" var="t">
	        				<fmt:formatDate value="${t.cdate}" pattern="yyyy-MM-dd" var="tt" />
	        				<c:choose>
	        					<c:when test="${reportMap.get(tt.concat('_').concat(r.id)) == null }">
	        					<td></td>
	        					<td></td>
	        					<td></td>
	        					</c:when>
	        					<c:otherwise>
	        					<td>${reportMap.get(tt.concat("_").concat(r.id)).checkInRadio}%</td>
		        				<td>${reportMap.get(tt.concat("_").concat(r.id)).avgPrice}</td>
		        				<td>${reportMap.get(tt.concat("_").concat(r.id)).checkInRooms}</td>
	        					</c:otherwise>
	        				</c:choose>
	        			</c:forEach>
	        		</tr>
	        		</c:forEach>
        		</table>
        	</div>
        </div><!--end content-->
        <form id="mainForm" action="${ctx}/report/active/region" method="post">
        	<input type="hidden" name="fromDate" id="fromDate" value="${searchBean.fromDate}" />
        	<input type="hidden" name="toDate" id="toDate" value="${searchBean.toDate}" />
        </form>
    </div>
</body>
</html>
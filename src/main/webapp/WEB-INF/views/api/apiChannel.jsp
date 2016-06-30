<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.project.utils.CacheUtil"%>

<html>
<head>
<title>第三方渠道详情</title>
<script src="${ctx}/js/common/form.js" type="text/javascript"></script>
<script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
</script>
</head>
<body>
   <div class="container-right">
    	<div class="header">
    		<c:choose>
    			<c:when test="${currentBtn == 'apiChannel'}">
		        	<button urls="${ctx}/apisale/channel" class="kc-btn kc-active">渠道管理</button>
    			</c:when>
    			<c:otherwise>
		        	<button urls="${ctx}/apisale/channel" class="kc-btn">渠道管理</button>
    			</c:otherwise>
    		</c:choose>
    		<c:choose>
    			<c:when test="${currentBtn == 'marketInn'}">
		        	<button urls="${ctx}/apisale/channel/marketInn" class="kc-btn kc-active">库存管理</button>
    			</c:when>
    			<c:otherwise>
		        	<button urls="${ctx}/apisale/channel/marketInn" class="kc-btn">库存管理</button>
    			</c:otherwise>
    		</c:choose>
    		<c:choose>
    			<c:when test="${currentBtn == 'channelOrder'}">
		        	<button urls="${ctx}/apisale/channel/orders" class="kc-btn kc-active">对账管理</button>
    			</c:when>
    			<c:otherwise>
		        	<button urls="${ctx}/apisale/channel/orders" class="kc-btn">对账管理</button>
    			</c:otherwise>
    		</c:choose>
        </div><!--end header-->
        
        	<table class="kz-table" cellpadding="8">
        		<thead>
	        		<tr>
	        			<th>渠道名称</th>
	        			<th colspan="3">价格策略</th>
	        			<th>添加渠道时间</th>
	        			<th>添加操作人</th>
	        		</tr>
        		</thead>
        		<c:forEach items="${page.result}" var="r">
        		<tr>
        			<td>${ r.name }</td>
        			<c:choose>
        				<c:when test="${r.pricePolicy=='1'}">
        					<td>底价</td>
        					<td>加价比例</td>
	        				<td>${ r.upRatio }%</td>
        				</c:when>
        				<c:otherwise>
        					<td>卖价</td>
        					<td>分佣比例</td>
	        				<td>${ r.commissionRatio }%</td>
        				</c:otherwise>
        			</c:choose>
	        		<td>${ r.createdAt }</td>
	        		<td>${ r.createdUser }</td>
        		</tr>
        		</c:forEach>
        	</table>
    </div>
</body>
</html>
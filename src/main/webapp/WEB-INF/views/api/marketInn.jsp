<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.project.utils.CacheUtil"%>

<html>
<head>
<title>库存管理</title>
<script src="${ctx}/js/common/form.js" type="text/javascript"></script>
<script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
<script src="${ctx}/js/api/channel.js" type="text/javascript"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
</script>
</head>
<body>
	<div id="add_inn_div" class="center-box" style="display:none">
		<div class=" center-box-in audit-window addinn1">
	    	<p>你确定要<em id="op_type">添加</em><em id="add_inn_name">云水谣客栈</em><em id="op_word">到代销平台？</em></p>
	    	<div>价格策略：<input id="price_buttom" type="checkbox" value="1">底价<input id="price_sale" type="checkbox" value="2">卖价</div>
	    	<div id="commission_div" style="display:none">抽佣比例：<input id="commission_input" class="input" type="text" value=""></div>
	        <span>
	        	<a href="javascript:void(0)" onclick="addInn2Stock()" class="audit-pass-button">确定</a>
	        	<a href="javascript:void(0)" onclick="closeAlertDialog('add_inn_div')" class="audit-nopass-button">取消</a>
	        </span>
	    </div>
	    <input type="hidden" id="add_inn_id" />
	</div>

	<div id="search_inn_div" class="center-box" style="display:none">
		<div class=" center-box-in audit-window addinn">
	    	<p>客栈账号：<input id="search_inn_input" type="text"></p>
	    	
	        <span><a id="search_inn_btn" href="javascript:void(0)" class="audit-pass-button">搜索</a></span>
	        <div id="search_inn_erorr" class="error-tips" style="display:none"></div>
	    </div>
	</div>
	
   <div id="remove_inn_div" class="center-box" style="display:none">
    	<div class=" center-box-in audit-window confirmEnd">
	    	<h2>确认删除？</h2>
	        <span>
	        	<a href="javascript:void(0)" class="audit-pass-button">确认</a>
	        	<a href="javascript:void(0)" onclick="closeAlertDialog('remove_inn_div')" class="audit-nopass-button">取消</a>
	        </span>
	    </div>
   </div>
   <div class="container-right">
    	<div class="header">
    		<p class="kc-p">（添加客栈后，该客栈可在番茄来了客栈管理系统的分销管理-对接管理-代销平台中进行管理）</p>
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
    		<div class="header-button-box duizhang kc">
    			<security:authorize ifAnyGranted="ROLE_代销管理">
        		<a id="add_inn_btn" href="javascript:void(0)" class="red-button-add add">添加客栈</a>
        		</security:authorize>
        		<select id="searchType_select" name="">
        			<c:choose>
        				<c:when test="${searchBean.searchType == 1}">
		        			<option value="1" selected="selected">客栈名称</option>
		        			<option value="2" >客栈账号</option>
        				</c:when>
        				<c:otherwise>
		        			<option value="1" >客栈名称</option>
		        			<option value="2" selected="selected">客栈账号</option>
        				</c:otherwise>
        			</c:choose>
        		</select>
        		<div class="search-box">
                	<input type="text" class="search" maxlength="20" id="search_input" value="${searchBean.input}">           			
                	<input type="button" class="search-button" id="search_submit">
        		</div>
            </div>
        </div><!--end header-->
        <form id="mainForm" action="${ctx}/apisale/channel/marketInn" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}" />
		<input type="hidden" name="orderBy" id="orderBy" value="${page.orderBy}" />
		<input type="hidden" name="searchType" id="searchType" value="${searchBean.searchType}" />
		<input type="hidden" name="input" id="input" value="${searchBean.input}" />
        	<table class="kz-table" cellpadding="8">
        		<thead>
	        		<tr>
	        			<th>客栈名称</th>
	        			<th>客栈账号</th>
	        			<th>价格模式</th>
	        			<th>抽佣比例</th>
	        			<th>加入时间</th>
	        			<th>可销售房数</th>
	        			<th>操作人</th>
	        			<th>操作</th>
	        		</tr>
        		</thead>
        		<c:forEach items="${page.result}" var="r">
        		<tr innId="${ r.id }">
        			<td tag="name">${ r.name }</td>
        			<td tag="mobile">${ r.mobile }</td>
        			<c:choose>
        				<c:when test="${r.pricePolicy=='1'}">
        					<td tag="pricePolicy" type="1">底价</td>
        				</c:when>
        				<c:otherwise>
        					<c:choose>
	        					<c:when test="${r.pricePolicy=='2'}">
	        						<td tag="pricePolicy" type="2">卖价</td>
	        					</c:when>
	        					<c:otherwise>
	        						<td tag="pricePolicy" type="3">底价+卖价</td>
	        					</c:otherwise>
        					</c:choose>
        				</c:otherwise>
        			</c:choose>
	        		<td tag="commission">${ r.totalCommissionRatio }</td>
	        		<td>${ r.joinMarketTime }</td>
	        		<td>${ r.marketRooms }</td>
	        		<td>${ r.inMarketCreatedUser }</td>
	        		<td class="oper">
	        			<security:authorize ifAnyGranted="ROLE_代销管理">
	        				<a tag="remove" innId="${ r.id }" href="javascript:void(0)" class="del-button">删除</a>
	        				<a tag="edit" innId="${ r.id }" mobile="${ r.mobile }" href="javascript:void(0)" class="editor-button">编辑</a>
	        			</security:authorize>
	        		</td>
        		</tr>
        		</c:forEach>
        	</table>
        </form>
        <tags:pagination page="${page}" paginationSize="5"/>
        
    </div>
</body>
</html>
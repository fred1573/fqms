<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.project.utils.CacheUtil"%>

<html>
<head>
<title>对账管理</title>
<script src="${ctx}/js/common/form.js" type="text/javascript"></script>
<script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
<script src="${ctx}/js/api/api-orders.js" type="text/javascript"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
</script>
</head>
<body>
	<div id="order_detail_div" class="center-box" style="display:none">
		<div class=" center-box-in audit-window innChe">
	    	<table>
	    		<thead>
	    			<tr>
		    			<th>入住人姓名</th>
		    			<th>手机号码</th>
		    			<th>入住时间</th>
		    			<th>退房时间</th>
		    			<th>房间号</th>
	    			</tr>
	    		</thead>
	    		<tr>
	    			<td tag="name">某某</td>
	    			<td tag="mobile">13899965425</td>
	    			<td tag="checkIn"><span>2014/08/09</span><hr><span>2014/03/01</span><hr><span>2014/03/01</span></td>
	    			<td tag="checkOut"><span>2014/08/09</span><hr><span>2014/03/01</span><hr><span>2014/03/01</span></td>
	    			<td tag="roomNos"><span>156</span><hr><span>2685</span><hr><span>2685</span></td>
	    		</tr>
	    	</table>
	    </div>
	</div>
	<div id="balace_order_div" class="center-box" style="display:none">
    	<div class=" center-box-in audit-window confirmEnd">
	    	<h2>确认结算？</h2>
	        <span>
	        	<a href="javascript:void(0)" class="audit-pass-button">确认</a>
	        	<a href="javascript:void(0)" onclick="closeAlertDialog('balace_order_div')" class="audit-nopass-button">取消</a>
	        </span>
	    </div>
	    <input id="balace_order_id" type="hidden" value="" />
	</div>
	<div id="cancel_balace_order_div" class="center-box" style="display:none">
    	<div class=" center-box-in audit-window confirmEnd">
	    	<h2>确认取消结算？</h2>
	        <span>
	        	<a href="javascript:void(0)" class="audit-pass-button">确认</a>
	        	<a href="javascript:void(0)" onclick="closeAlertDialog('cancel_balace_order_div')" class="audit-nopass-button">取消</a>
	        </span>
	    </div>
	    <input id="cancel_balace_order_id" type="hidden" value="" />
	</div>
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
    		<div class="header-button-box duizhang kc">
        		<select id="searchType_select" name="">
        			<c:choose>
        				<c:when test="${searchBean.searchType == 1}">
		        			<option value="1" selected="selected">客栈名称</option>
		        			<option value="2" >订单号</option>
        				</c:when>
        				<c:otherwise>
		        			<option value="1" >客栈名称</option>
		        			<option value="2" selected="selected">订单号</option>
        				</c:otherwise>
        			</c:choose>
        		</select>
        		<div class="search-box">
                	<input type="text" class="search" maxlength="20" id="search_input" value="${searchBean.input}">           			
                	<input type="button" class="search-button" id="search_submit">
        		</div>
        		<div class="date-date">
	        		<span id="fromDate_select" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d}',onpicked:searchWithFromDate})">${searchBean.fromDate}</span>
	        		至
	        		<span id="toDate_select" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d}',onpicked:searchWithToDate})">${searchBean.toDate}</span>
        		</div>
            </div>
        </div><!--end header-->
        <form id="mainForm" action="${ctx}/apisale/channel/orders" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}" />
		<input type="hidden" name="orderBy" id="orderBy" value="${page.orderBy}" />
		<input type="hidden" name="searchType" id="searchType" value="${searchBean.searchType}" />
		<input type="hidden" name="input" id="input" value="${searchBean.input}" />
		<input type="hidden" name="fromDate" id="fromDate" value="${searchBean.fromDate}" />
		<input type="hidden" name="toDate" id="toDate" value="${searchBean.toDate}" />
		<input type="hidden" name="innIds" id="innIds" value="${searchBean.innIds}" />
        	<table class="kz-table" cellpadding="8">
        		<thead>
	        		<tr>
		        		<th>
		        			<select name="channelId" id="channelId">
		        				<option value="0">来源OTA</option>
		        				<c:forEach items="${channels}" var="c">
		        					<c:choose>
		        						<c:when test="${searchBean.channelId == c.id}">
		        							<option value="${ c.id }" selected="selected">${ c.name }</option>
		        						</c:when>
		        						<c:otherwise>
		        							<option value="${ c.id }">${ c.name }</option>
		        						</c:otherwise>
		        					</c:choose>
		        				</c:forEach>
		        			</select>
		        		</th>
		        		<th colspan="3">价格策略</th>
		        		<th>客栈名称</th>
		        		<th>客栈账号</th>
		        		<th>订单号</th>
		        		<th>房型</th>
		        		<th>时间</th>
		        		<th>夜</th>
		        		<th>进价（间）</th>
		        		<th>售价（间）</th>
		        		<th>总差价</th>
		        		<th>支付时间</th>
		        		<th>
			        		<select flag="${ searchBean.isBalance }" name="isBalance" id="isBalance">
			        			<c:choose>
			        				<c:when test="${ searchBean.isBalance == '0' }">
			        					<option value="">订单状态</option>
			        					<option value="0" selected="selected">未结算</option>
			        					<option value="1">已结算</option>
			        				</c:when>
			        				<c:otherwise>
			        					<c:choose>
			        						<c:when test="${ searchBean.isBalance == '1' }">
			        							<option value="">订单状态</option>
			        							<option value="0">未结算</option>
			        							<option value="1" selected="selected">已结算</option>
			        						</c:when>
			        						<c:otherwise>
			        							<option value="" selected="selected">订单状态</option>
			        							<option value="0">未结算</option>
			        							<option value="1" >已结算</option>
			        						</c:otherwise>
			        					</c:choose>
			        				</c:otherwise>
			        			</c:choose>
	        				</select>
		        		</th>
	        	     </tr>
        		</thead>
        		<c:forEach items="${page.result}" var="r">
        		<tr orderId="${ r.id }">
        			<td>
        				<sw:apiName value="${r.fxChannelId}"></sw:apiName>
        			</td>
        			<c:choose>
        				<c:when test="${r.channelPricePolicy=='1'}">
        					<td>底</td>
			        		<td>${ r.channelUpRatio }</td>
			        		<td></td>
        				</c:when>
        				<c:when test="${r.channelPricePolicy==''}">
        					<td></td>
			        		<td></td>
			        		<td></td>
        				</c:when>
        				<c:otherwise>
	        				<td>卖</td>
			        		<td>${ r.innCommissionRatio }</td>
			        		<td>${ r.channelCommissionRatio }</td>
        				</c:otherwise>
        			</c:choose>
	        		<td innId="${ r.innId }">
	        			<span tag="name" class="name">
		        			<sw:innName value="${ r.innId }"></sw:innName>
	        			</span>
	        		</td>
	        		<td><sw:innMobile value="${ r.innId }"></sw:innMobile></td>
	        		<td orderNo="${ r.channelOrderNo }">
		        		<span tag="orderNo" class="name">
		        			${ r.channelOrderNo }
		        		</span>
	        		</td>
	        		<td>
	        			<c:forEach items="${r.channelOrders}" var="c" varStatus="status">
	        				<span>${c.channelRoomTypeName }</span>
	        				<c:choose>
	        					<c:when test="${ status.index == r.channelOrders.size()-1 }">
	        					</c:when>
	        					<c:otherwise>
	        						<hr/>
	        					</c:otherwise>
	        				</c:choose>
	        			</c:forEach>
	        		</td>
	        		<td>
	        			<c:forEach items="${r.channelOrders}" var="c" varStatus="status">
	        				<span index="${ status.index }" class="date_s" from="${ c.checkInAt }" to="${c.checkOutAt }">
		        				<fmt:formatDate value="${c.checkInAt }" pattern="MM-dd"/>
		        				至
		        				<fmt:formatDate value="${c.checkOutAt }" pattern="MM-dd"/>
	        				</span>
	        				<c:choose>
	        					<c:when test="${ status.index == r.channelOrders.size()-1 }">
	        					</c:when>
	        					<c:otherwise>
	        						<hr/>
	        					</c:otherwise>
	        				</c:choose>
	        			</c:forEach>
	        		</td>
	        		<td>
	        			<c:forEach items="${r.channelOrders}" var="c" varStatus="status">
	        				<span class="night_s" index="${ status.index }">1</span>
	        				<c:choose>
	        					<c:when test="${ status.index == r.channelOrders.size()-1 }">
	        					</c:when>
	        					<c:otherwise>
	        						<hr/>
	        					</c:otherwise>
	        				</c:choose>
	        			</c:forEach>
	        		</td>
	        		<td>
	        			<c:forEach items="${r.channelOrders}" var="c" varStatus="status">
	        				<span class="inprice_s" index="${ status.index }">${c.originalPrice }</span>
	        				<c:choose>
	        					<c:when test="${ status.index == r.channelOrders.size()-1 }">
	        					</c:when>
	        					<c:otherwise>
	        						<hr/>
	        					</c:otherwise>
	        				</c:choose>
	        			</c:forEach>
	        		</td>
	        		<td>
	        			<c:forEach items="${r.channelOrders}" var="c" varStatus="status">
	        				<span class="saleprice_s" index="${ status.index }">${c.salePrice }</span>
	        				<c:choose>
	        					<c:when test="${ status.index == r.channelOrders.size()-1 }">
	        					</c:when>
	        					<c:otherwise>
	        						<hr/>
	        					</c:otherwise>
	        				</c:choose>
	        			</c:forEach>
	        		</td>
	        		<td tag="totalCount">总差价</td>
	        		<td><fmt:formatDate value="${r.orderTime }" pattern="yyyy-MM-dd HH:mm"/></td>
	        		<td balace="${ r.isBalance }" class="op_type">
	        			<security:authorize ifAnyGranted="ROLE_代销管理">
	        			<c:choose>
	        				<c:when test="${ r.isBalance == '0' }">
	        					<span class="un">未结算</span>
	        				</c:when>
	        				<c:otherwise>
	        					<span class="done">
	        						<fmt:formatDate value="${ r.balanceTime }" pattern="yyyy-MM-dd HH:mm"/>
	        					</span>
	        				</c:otherwise>
	        			</c:choose>
	        			</security:authorize>
	        		</td>
        		</tr>
        		</c:forEach>
        	</table>
        	<p class="acount">
				截止您所选时间段内，共有 <span>${ page.totalCount }</span> 个订单，总进价为<span>${ totalInPrice }</span>，总售价为 <span>${ totalSalePrice }</span>，总差价总和为<span>${totalSalePrice - totalInPrice }</span>。	
			</p>
			<c:choose>
				<c:when test="${ searchBean.innIds > 0 }">
					<p class="acount">
						<span><sw:innName value="${ searchBean.innIds }"></sw:innName></span> 
						<span class="no">联系人：</span>
						<span>${ accountMap.get("person") }</span> 
						<span class="no">电话号码：</span>
						<span>${ accountMap.get("contact") }</span>
						<span class="no">支付账号：</span>
						<span>${ accountMap.get("alipay") }</span>		
						<span class="no">银行卡号：</span>
						<span>${ accountMap.get("bankno") }</span>		
					</p>
				</c:when>
			</c:choose>
        </form>
        <tags:pagination page="${page}" paginationSize="5"/>
    </div>
</body>
</html>
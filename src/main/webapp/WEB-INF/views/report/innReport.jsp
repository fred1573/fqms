<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.project.utils.CacheUtil"%>

<html>
<head>
<title>小站统计报表</title>
<script src="${ctx}/js/common/form.js" type="text/javascript"></script>
<script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<script src="<c:url value="/js/report/innReport.js"/> " type="text/javascript"></script>
	<style media="screen">
		a.xzdetail{
			float: right;
			border: 1px solid gray;
			margin: 0;
			padding: 3px;
		}
	</style>
</head>
<body>
   <div class="container-right">
    	<div class="header">
        	<h1>客栈统计</h1>
        	<%--<div class="header-button-box">
                <a id="funcReport-date" href="javascript:void(0)" 
                onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d',onpicked:searchWithDate})" class="date">
                	${searchBean.selectDate}
                </a>
            </div>--%>
            <div class="header-button-box duizhang kc" style="right:0; top: 20px">
        		<select id="searchType_select" name="">
		        	<option value="1" selected="selected">客栈名称</option>
        		</select>
        		<div class="search-box">
                	<input type="text" class="search" maxlength="20" id="search_input" value="${searchBean.innName}">
                	<input type="button" class="search-button" id="search_submit">
        		</div>
            </div>
        </div><!--end header-->
        <c:set var="xzhost" value="http://www.fanqielaile.com/"/>
        <div class="content inn-cehckList duizhang-table">
        	<table cellpadding="8">
        		<tbody><tr>
        			<th>客栈名称</th>
        			<th>小站名称</th>
        			<th>番茄小站网址</th>
        			<th>是否开通美洽</th>
        			<th>开通水牌</th>
        		</tr>
        		<c:forEach items="${page.result}" var="r">
        		<tr>
        			<td>${ r.innName }</td>
        			<td>${ r.xzName }</td>
	        		<td>
						<c:if test="${not empty r.webSite}">
							<span style="float: left;">${xzhost}${r.webSite}</span>
							<a href="http://xz.fanqielaile.com/querylink.aspx?code=${r.webSite}" target="_blank" class="xzdetail">查看详情</a>
						</c:if>
					</td>
					<td>
						<c:choose>
							<c:when test="${ r.openMc != null && !''.equals(r.openMc) }">是</c:when>
							<c:otherwise>否</c:otherwise>
						</c:choose>
					</td>
					<td>
						<input type="checkbox" name="hasBrand" value="${r.id}"
							<c:if test="${r.hasBrand}"> checked="checked" </c:if>
						>
					</td>
        		</tr>
        		</c:forEach>
        			</tbody></table>
        			<%--<p class="acount">--%>
	<%--截止目前，共有 <span>${ page.totalCount }</span> 家客栈开通了小站服务，占比为 <span>${ proportion }</span>%,--%>
	<%--其中有<span>${ mcInnAmount }</span>家客栈已经开通了美洽在线客服服务，占比为${ mcProportion }%--%>
					<%--</p>--%>
        	<tags:pagination page="${page}" paginationSize="10"/>
        </div><!--end content-->
    </div>
   <input type="hidden" value="<c:url value="/inn/updateBrand"/>" id="updateBrandUrl">
    <form id="mainForm" action="/funcReport/innReport" method="get">
    	<input type="hidden" name="pageNo" id="pageNo" value="${ searchBean.pageNo }" />
    	<input type="hidden" name="innName" id="innName" value="${ searchBean.innName }" />
    </form>
</body>
</html>
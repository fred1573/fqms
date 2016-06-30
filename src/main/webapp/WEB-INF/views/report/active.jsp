<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<title>活跃报表</title>
<script src="${ctx}/js/common/form.js" type="text/javascript"></script>
<script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<script src="${ctx}/js/report/active.js" type="text/javascript"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
</script>
</head>
<body>
    <!--右边内容区域-->
    <div class="container-right">
    	<div class="header">
        	<h1>活跃报表</h1>
        	<div>
        		<c:choose>  
			    	<c:when test="${activeSearchBean.areaName!=''}">
			    		<span class="area-new">${activeSearchBean.areaName}
			    			<em class="down-select-table"></em>
			    			<ol id="areas" class="table-header-ol" style="display:none; width:100px">
                				<c:forEach items="${regions}" var="r">
                    				<li value="${r.id}">${r.name}</li>
                   				 </c:forEach>                    
                			</ol>
			    		</span>
			    	</c:when>  
			    	<c:otherwise> 
			    		<span class="area-new">区域<em></em></span>
			    	</c:otherwise>  
			    </c:choose> 
        	</div>
        	<div class="date-choose">
        		<span class="cursor">
        			<span id="activeDate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d}',onpicked:searchWithFromDate})">${timelineCells.get(0).cdateOfString}</span>
        			<em id="activeDate_em"></em>
        		</span>
        			至
        		<span class="cursor">
        			<span id="toDate_select" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d}',onpicked:searchWithToDate})">${activeSearchBean.toDate}</span>
        			<em id="toDate_select_em"></em>
        		</span>
        		<button id="active_report_export_btn" onclick="activeExport()" style="display:none">导出EXCEL</button>
        	</div>
            <div class="header-button-box">
            	<span class="choice">
                     <input name="selectRadio" type="radio" value="" onclick="searchWithType(0)" <c:if test="${activeSearchBean.activeType == 0}">checked="checked"</c:if>/><i style="color:#000">全部</i>&nbsp;
                     <!-- 
                     <input name="selectRadio" type="radio" value="" onclick="searchWithType(1)" <c:if test="${activeSearchBean.activeType == 1}">checked="checked"</c:if>/><i style="color:#434343">登陆</i>&nbsp;
                      -->
                     <input name="selectRadio" type="radio" value="" onclick="searchWithType(2)" <c:if test="${activeSearchBean.activeType == 2}">checked="checked"</c:if>/><i style="color:#44a51e">预定</i>&nbsp;
                     <input name="selectRadio" type="radio" value="" onclick="searchWithType(3)" <c:if test="${activeSearchBean.activeType == 3}">checked="checked"</c:if>/><i style="color:#ff7200">入住</i>
                 </span>
            </div>
        </div><!--end header-->
        <form id="mainForm" action="${ctx}/report/active" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}" />
		<input type="hidden" name="orderBy" id="orderBy" value="${page.orderBy}" />
		<input type="hidden" name="order" id="order" value="${page.order}" />
		<input type="hidden" name="selectDate" id="selectDate" value="${activeSearchBean.fromDate}" />
		<input type="hidden" name="fromDate" id="fromDate" value="${activeSearchBean.fromDate}" />
		<input type="hidden" name="toDate" id="toDate" value="${activeSearchBean.toDate}" />
		<input type="hidden" name="areaId" id="areaId" value="${activeSearchBean.areaId}" />
		<input type="hidden" name="sortDate" id="sortDate" value="${activeSearchBean.sortDate}" />
		<input type="hidden" name="areaName" id="areaName" value="${activeSearchBean.areaName}" />
		<input type="hidden" name="activeFlag" id="activeFlag" value="${activeSearchBean.activeFlag}" />
		<input type="hidden" name="activeType" id="activeType" value="${activeSearchBean.activeType}" />
        <div class="content2 hot-inn">
        	<ul class="table-header table-header1">
        		<li class="hot-table-innArea"></li>
        		<li class="hot-table-innName"></li>
            	<li class="hot-table-innArea position-new">
           			注册时间
                </li>
                <li class="hot-table-innName position-new1">客栈名称</li>
                <c:forEach items="${timelineCells}" var="t">
                	<fmt:formatDate value="${t.cdate}" pattern="yyyy-MM-dd" var="tt" />
                	<c:choose>
                		<c:when test="${ tt == activeSearchBean.sortDate }"><li tag="sortDate" date="${ tt }" class="down active"><fmt:formatDate value="${t.cdate}" pattern="MM-dd"/></li></c:when>
                		<c:otherwise>
                			<li tag="sortDate" date="${ tt }" class="down"><fmt:formatDate value="${t.cdate}" pattern="MM-dd"/></li>
                		</c:otherwise>
                	</c:choose>
                </c:forEach>
            </ul>
            <c:forEach items="${page.result}" var="r">
            <ul>
            	<li class="hot-table-innArea"></li>
        		<li class="hot-table-innName"></li>
            	<li class="hot-table-innArea position-new">${r.registeredAt}</li>
                <li class="hot-table-innName position-new1">${r.name}</li>
                <c:forEach items="${timelineCells}" var="t">
                <c:set value="${t.cdateOfString}_${r.id}" var="a"></c:set>
               	<c:choose>  
		            <c:when test="${innDateActiveMap.get(a).checkNum!=null && innDateActiveMap.get(a).checkNum!=0}"><li class="ruzhu-inn">${innDateActiveMap.get(a).checkNum}</li></c:when>  
		            <c:otherwise>
			            <c:choose>  
			            	<c:when test="${innDateActiveMap.get(a).bookNum!=null && innDateActiveMap.get(a).bookNum!=0}"><li class="yuding-inn">${innDateActiveMap.get(a).bookNum}</li></c:when>  
			            	<c:otherwise>
			            		<c:choose>  
		            				<c:when test="${innDateActiveMap.get(a).loginNum!=null && innDateActiveMap.get(a).loginNum!=0}"><li class="login-inn">${innDateActiveMap.get(a).loginNum}</li></c:when>  
		           	 				<c:otherwise><li></li></c:otherwise>
		           	 			</c:choose>
			            	</c:otherwise>  
			        	</c:choose>
		            </c:otherwise>  
		        </c:choose> 
                </c:forEach>
            </ul>
            </c:forEach>
        </div><!--end content-->
        </form>
		<tags:pagination page="${page}" paginationSize="5"/>
	</div><!--end container-right-->
</body>
</html>
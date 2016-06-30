<%@tag pageEncoding="UTF-8"%>
<%@ attribute name="page" type="com.project.core.orm.Page" required="true"%>
<%@ attribute name="paginationSize" type="java.lang.Integer" required="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
long current =  page.getPageNo();
long begin = Math.max(1, current - paginationSize/2);
long end = Math.min(begin + (paginationSize - 1), page.getTotalPages());

request.setAttribute("current", current);
request.setAttribute("begin", begin);
request.setAttribute("end", end);
%>
<div class="page-list">
	<ul>
		 <% if (page.isHasPre()){%>
               	<li><a href="javascript:jumpPage(1)">首页</a></li>
                <li><a href="javascript:jumpPage(${page.prePage})">&lt;&lt;</a></li>
         <%}else{%>
                <li class="disabled"><a href="#">首页</a></li>
                <li class="disabled"><a href="#">&lt;&lt;</a></li>
         <%} %>
 
		<c:forEach var="i" begin="${begin}" end="${end}">
            <c:choose>
                <c:when test="${i == current}">
                    <li class="active"><a href="javascript:jumpPage(${i})">${i}</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="javascript:jumpPage(${i})">${i}</a></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
	  
	  	 <% if (page.isHasNext()){%>
               	<li><a href="javascript:jumpPage(${page.nextPage})">&gt;&gt;</a></li>
                <li><a href="javascript:jumpPage(${page.totalPages})">末页</a></li>
         <%}else{%>
                <li class="disabled"><a href="#">&gt;&gt;</a></li>
                <li class="disabled"><a href="#">末页</a></li>
         <%} %>

	</ul>
</div>


<%@tag pageEncoding="UTF-8"%>
<%@ attribute name="page" type="com.github.miemiedev.mybatis.paginator.domain.PageBounds" required="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div class="page-list">
	<ul>
        <c:choose>
            <c:when test="${paginator.hasPrePage}">
                <li><a href="javascript:jumpPage(1)">首页</a></li>
                <li><a href="javascript:jumpPage(${paginator.prePage})">&lt;&lt;</a></li>
            </c:when>
            <c:otherwise>
                <li class="disabled"><a href="#">首页</a></li>
                <li class="disabled"><a href="#">&lt;&lt;</a></li>
            </c:otherwise>
        </c:choose>

 
		<c:forEach var="i" begin="1" end="${paginator.totalPages}">
            <c:choose>
                <c:when test="${i == current}">
                    <li class="active"><a href="javascript:jumpPage(${i})">${i}</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="javascript:jumpPage(${i})">${i}</a></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:choose>
            <c:when test="${paginator.hasNextPage}">
                <li><a href="javascript:jumpPage(${paginator.nextPage})">&gt;&gt;</a></li>
                <li><a href="javascript:jumpPage(${paginator.totalPages})">末页</a></li>
            </c:when>
            <c:otherwise>
                <li class="disabled"><a href="#">&gt;&gt;</a></li>
                <li class="disabled"><a href="#">末页</a></li>
            </c:otherwise>
        </c:choose>
	</ul>
</div>


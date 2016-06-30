<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2016/5/17
  Time: 17:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
    <title>代销平台-客栈管理</title>
    <link href="${ctx}/css/jquery-ui.css" rel="stylesheet" type="text/css">
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
    <script src="${ctx}/js/activity/activity.js" type="text/javascript"></script>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
</head>
<body>

<div class="container-right">
    <div style="font-size: 18px; margin: 10px 10px 0 20px;float: left;width: 98%;">
        <div style="float:left;">共有<c:if test="${count==null}"> 0</c:if>${count}家客栈参与活动</div>
        <a class="red-button-add add" style="margin-right:5px;width: 100px; float: right;"
           href="javascript:exportInn()">导出参与客栈名称</a>
    </div>
    <div class="search-area" style="width: 98%;">
        <%--<input type="text" class="search" placeholder="搜索区域">--%>

        <a class="red-button-add add" style="margin-right:5px;width: 100px; float: left;"
           href="javascript:agreeAll()">一键同意</a>

        <input type="hidden" name="activityId" value="${activityId}">
            <form  id="mainForm" action="${ctx}/activity/inn?activityId=${activityId}" method="post" style="float:right;width: 179px;">
                <input name="innName" class="search" id="searchAreaName" placeholder="模糊搜索客栈关键字" maxlength="20"
                       type="text">
                <input class="search-button" type="submit" value="&nbsp;">
                <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
            </form>
    </div>
    <table class="kz-table" cellpadding="12">
        <tr>
            <td>区域</td>
            <td>目的地</td>
            <td>客栈名称</td>
            <td>客栈id(pms)</td>
            <td>操作</td>
        </tr>
        <c:forEach items="${page.result}" var="inn">
            <tr>

                <td>${inn.area}</td>
                <td>${inn.region}</td>
                <td>${inn.innname}</td>
                <td>${inn.pmsid}</td>

                <td>
                    <input type="hidden" name="innId" value="${inn.pmsid}">
                    <c:choose>
                        <c:when test="${inn.status==1}">
                            <a href="javascript:agree(${inn.pmsid})">
                                <button>同意</button>
                            </a>
                            <a href="javascript:refuse(${inn.pmsid})">
                                <button>拒绝</button>
                            </a>
                        </c:when>
                        <c:when test="${inn.status==2}">已同意</c:when>
                        <c:when test="${inn.status==3}">已拒绝</c:when>
                        <c:otherwise>未知</c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </table>
    </form>
    <c:if test="${not empty page}">
        <tags:pagination page="${page}" paginationSize="15"/>
    </c:if>
</div>


</body>
</html>
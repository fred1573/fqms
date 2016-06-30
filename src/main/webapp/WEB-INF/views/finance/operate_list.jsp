<%@ page import="java.util.Date" %>
<%--
  Created by IntelliJ IDEA.
  User: 番茄桑
  Date: 2015/8/13
  Time: 10:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>财务结算-操作记录</title>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/finance/operate_list.js" type="text/javascript"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/finance/operate/list" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <%--<input type="hidden" name="settlementTime" value="${settlementTime}"/>--%>
        <input type="hidden" name="keyWord" value="${keyWord}"/>
        <input type="hidden" name="operateType" value="${operateType}"/>
        <input type="hidden" name="startDate" value="${startDate}"/>
        <input type="hidden" name="endDate" value="${endDate}"/>
    </form>
    <div class="header">
        <%@ include file="navigation.jsp" %>
        <div style="left: 550px; top: -5px;" class="header-button-box duizhang kc">
            <div class="search-box">
                <input type="text" id="keyWord" maxlength="20" class="search" placeholder="分销商名称或客栈名称"
                       <c:if test="${not empty keyWord}">value="${keyWord}"</c:if>/>
                <input type="button" id="search_submit" class="search-button">
            </div>
            <%--<select id="settlementTime">--%>
            <%--<option value="">选择结算月份</option>--%>
            <%--<c:forEach var="ota" items="${financeTimeMap}">--%>
            <%--<option value="${ota.key}"--%>
            <%--<c:if test="${settlementTime eq ota.key}">selected</c:if>>${ota.value}</option>--%>
            <%--</c:forEach>--%>
            <%--</select>--%>

            <div class="date-date" style="width: 300px;padding: 0;margin-left: 20px;">
                <a id="startDate" style="float: none;display: inline-block" class="date WdateFmtErr"
                   href="javascript:void(0)"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:changeStartDate})" class="date">
                    <c:choose>
                        <c:when test="${not empty startDate}">
                            ${startDate}
                        </c:when>
                        <c:otherwise>
                            <fmt:formatDate value="<%=new Date()%>" pattern="yyyy-MM-dd"/>
                        </c:otherwise>
                    </c:choose>
                </a>
                至
                <a id="endDate" style="float: none;display: inline-block" class="date WdateFmtErr"
                   href="javascript:void(0)"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:changeEndDate})" class="date">
                    <c:choose>
                        <c:when test="${not empty endDate}">
                            ${endDate}
                        </c:when>
                        <c:otherwise>
                            <fmt:formatDate value="<%=new Date()%>" pattern="yyyy-MM-dd"/>
                        </c:otherwise>
                    </c:choose>
                </a>
            </div>
        </div>
    </div>
    <!--end header-->
    <table class="kz-table" cellpadding="8">
        <thead>
        <tr>
            <th>
                <select id="operateType">
                    <option value="">操作类型</option>
                    <option value="1" <c:if test="${operateType==1}">selected</c:if>>渠道对账</option>
                    <option value="2" <c:if test="${operateType==2}">selected</c:if>>收到渠道款项</option>
                    <option value="3" <c:if test="${operateType==3}">selected</c:if>>发送客栈账单</option>
                    <option value="4" <c:if test="${operateType==4}">selected</c:if>>结算客栈款项</option>
                    <option value="8" <c:if test="${operateType==8}">selected</c:if>>修改账单</option>
                    <option value="9" <c:if test="${operateType==9}">selected</c:if>>新增结算账期</option>
                    <option value="10" <c:if test="${operateType==9}">selected</c:if>>取消订单</option>
                    <option value="201" <c:if test="${operateType==201}">selected</c:if>>编辑结算信息</option>
                </select>
            </th>
            <th>操作内容</th>
            <th> 操作账号</th>
            <th> 操作时间</th>
        </tr>
        </thead>
        <c:forEach items="${page.result}" var="log">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${log.operateType==1}">渠道对账</c:when>
                        <c:when test="${log.operateType==2}">收到渠道款项</c:when>
                        <c:when test="${log.operateType==3}">发送客栈账单</c:when>
                        <c:when test="${log.operateType==4}">结算客栈款项</c:when>
                        <c:when test="${log.operateType==8}">修改账单</c:when>
                        <c:when test="${log.operateType==9}">新增结算账期</c:when>
                        <c:when test="${log.operateType==10}">取消订单</c:when>
                        <c:when test="${log.operateType==201}">编辑结算信息</c:when>
                    </c:choose>
                </td>
                <td>${log.operateContent}</td>
                <td>${log.operateUser}</td>
                <td><fmt:formatDate value="${log.operateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            </tr>
        </c:forEach>
    </table>
    <c:if test="${not empty page}">
        <tags:pagination page="${page}" paginationSize="15"/>
    </c:if>
</div>
</body>
</html>

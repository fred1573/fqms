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
    <title>代销平台-操作记录</title>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/proxysale/operate_list.js" type="text/javascript"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/proxysale/inn/operateList" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <input type="hidden" name="settlementTime" value="${settlementTime}"/>
        <input type="hidden" name="innName" value="${innName}"/>
        <input type="hidden" name="operateType" value="${operateType}"/>
        <input type="hidden" name="startDate" value="${startDate}"/>
        <input type="hidden" name="endDate" value="${endDate}"/>
    </form>
    <div class="header">
        <%@ include file="../header_fragment.jsp" %>
        <div style="left: 800px;" class="header-button-box duizhang kc">
            <div class="search-box">
                <input type="text" id="innName" maxlength="20" class="search" placeholder="客栈名称"
                       <c:if test="${not empty innName}">value="${innName}"</c:if>/>
                <input type="button" id="search_submit" class="search-button">
            </div>
            <div class="date-date" style="width: 300px;padding: 0">
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
            <th width="20%">
                <select id="operateType">
                    <option value="">操作类型</option>
                    <option value="101" <c:if test="${operateType==101}">selected</c:if>>编辑分销商</option>
                    <option value="102" <c:if test="${operateType==102}">selected</c:if>>客栈管理-上架</option>
                    <option value="103" <c:if test="${operateType==103}">selected</c:if>>客栈管理下架</option>
                    <option value="104" <c:if test="${operateType==104}">selected</c:if>>客栈管理-修改总抽佣比例</option>
                    <option value="105" <c:if test="${operateType==105}">selected</c:if>>客栈管理-修改渠道设置</option>
                    <option value="106" <c:if test="${operateType==106}">selected</c:if>>客栈管理-关房</option>
                    <option value="107" <c:if test="${operateType==107}">selected</c:if>>客栈管理-移出客栈</option>
                    <option value="108" <c:if test="${operateType==108}">selected</c:if>>客栈管理-批量上线渠道</option>
                    <option value="109" <c:if test="${operateType==109}">selected</c:if>>客栈管理-批量下线渠道</option>
                    <option value="110" <c:if test="${operateType==110}">selected</c:if>>客栈管理-批量关房</option>
                    <option value="111" <c:if test="${operateType==111}">selected</c:if>>客栈管理-价格审核</option>
                    <option value="112" <c:if test="${operateType==112}">selected</c:if>>客栈管理-合同审核</option>
                    <option value="113" <c:if test="${operateType==113}">selected</c:if>>客栈管理-调价</option>
                    <option value="114" <c:if test="${operateType==关房结果}">selected</c:if>>关房结果</option>
                    <option value="115" <c:if test="${operateType==115}">selected</c:if>>房态切换</option>
                    <option value="116" <c:if test="${operateType==116}">selected</c:if>>下架房型</option>
                    <option value="117" <c:if test="${operateType==117}">selected</c:if>>运营活动</option>
                    <option value="118" <c:if test="${operateType==118}">selected</c:if>>批量调价</option>
                </select>
            </th>
            <th>操作对象</th>
            <th>操作内容</th>
            <th> 操作账号</th>
            <th> 操作时间</th>
        </tr>
        </thead>
        <c:forEach items="${page.result}" var="log">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${log.operateType==101}">编辑分销商</c:when>
                        <c:when test="${log.operateType==102}">客栈管理-上架</c:when>
                        <c:when test="${log.operateType==103}">客栈管理下架</c:when>
                        <c:when test="${log.operateType==104}">客栈管理-修改总抽佣比例</c:when>
                        <c:when test="${log.operateType==105}">客栈管理-修改渠道设置</c:when>
                        <c:when test="${log.operateType==106}">客栈管理-关房</c:when>
                        <c:when test="${log.operateType==107}">客栈管理-移出客栈</c:when>
                        <c:when test="${log.operateType==108}">客栈管理-批量上线渠道</c:when>
                        <c:when test="${log.operateType==109}">客栈管理-批量下线渠道</c:when>
                        <c:when test="${log.operateType==110}">客栈管理-批量关房</c:when>
                        <c:when test="${log.operateType==111}">客栈管理-价格审核</c:when>
                        <c:when test="${log.operateType==112}">客栈管理-合同审核</c:when>
                        <c:when test="${log.operateType==113}">客栈管理-调价</c:when>
                        <c:when test="${log.operateType==114}">关房结果</c:when>
                        <c:when test="${log.operateType==115}">房态切换</c:when>
                        <c:when test="${log.operateType==116}">下架房型</c:when>
                        <c:when test="${log.operateType==117}">运营活动</c:when>
                        <c:when test="${log.operateType==118}">批量调价</c:when>
                    </c:choose>
                </td>
                <td>${log.operateObject}</td>
                <td>${log.operateContent}</td>
                <td>${log.operateUser}</td>
                <td><fmt:formatDate value="${log.operateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            </tr>
        </c:forEach>
    </table>
    <c:if test="${not empty page.result}">
        <tags:pagination page="${page}" paginationSize="15"/>
    </c:if>
</div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
    <title>代销平台-已移除客栈</title>
    <link href="${ctx}/css/jquery-ui.css" rel="stylesheet" type="text/css">
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/proxysale/jquery-ui-1.10.1.min.js"></script>
    <link href="${ctx}/css/proxysale/jquery-ui-1.10.1.css" rel="stylesheet">
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
    <style type="text/css">
        .red{color: red;}
        .table_count{clear: both; width: 100%;height: 40px;position: relative;top: -20px;}
        .dul{width:100%;height:100%;}
        .dul .all span{position:relative;top:5px;}
        .dul ul{margin:0px;padding:0px;margin-top: -30px;}
        .dul ul li{list-style:none;width:80px;margin-left:30px;float:left;height:30px;}
        .all{width:100%;height:30px;margin-top:10px;}
        .all span{margin-left:30px;}
        .area_content{width:100%;}
        .area_content_title	{width:100%;height:25px;margin-top:10px;clear:both;border-bottom: 1px solid #abcdef;}
        .area_content_title span{margin-left:30px;}
    </style>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/proxysale/inn/del_list" method="post">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <input type="hidden" name="orderBy" id="orderBy" value="${page.orderBy}"/>
        <input type="hidden" name="order" id="order" value="${page.order}"/>
    </form>
    <jsp:include page="../header_fragment.jsp"/>
    <!--end header-->
    <table class="kz-table" cellpadding="5">
        <thead>
        <tr>
            <th>客栈ID</th>
            <th>客栈名称</th>
            <th>移除时间</th>
            <th>移除人</th>
            <th>移除原因</th>
        </tr>
        </thead>
        <c:forEach items="${page.result}" var="r">
            <tr>
                <td>${r.proxyInn.inn}</td>
                <td>${r.proxyInn.innName}</td>
                <td>${r.delTime}</td>
                <td>${r.user.sysUserName}</td>
                <td>${r.reason}</td>
            </tr>
        </c:forEach>
    </table>
    <tags:pagination page="${page}" paginationSize="15"/>
</div>

</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>地区后台</title>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/leadin/leadin.js" type="text/javascript" ></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
</head>
<body class="bg1">
<!---------------搜索框----------------->
<div class="center-box">
    <div class=" center-box-in audit-window" style="display:none;" id="search">
        <a href="javascript:close()" class="close-window"></a>
        <h1>客栈运营</h1>
        <ul>
            <form id="searchInnForm" method="get">
                <li><dd>微网址:</dd><input id="phone" name="phone" type="text" class="validate[required,custom[mobile]] ipt" /></li>
            </form>
            <li>
                <a href="javascript:search()" class="green-button-ok">确&nbsp;&nbsp;&nbsp;&nbsp;定</a>
            </li>
        </ul>
        </form>
    </div>
</div>
<!---------------搜索框 end----------------->
<!---------------信息修改框----------------->
<div class="center-box">
    <div class=" center-box-in audit-window" style="display:none;" id="update">
        <a href="javascript:closeUpdate()" class="close-window"></a>
        <h1>客栈运营</h1>
        <ul>
            <form id="updateInnForm" method="post">
                <input type="hidden" id="weiShopId"/>
                <li><dd>账号:</dd><input id="mobile" readonly="readonly"></li>
                <li><dd>客栈名字:</dd><input id="innName" readonly="readonly"></li>
                <li><dd>区域:</dd><input id="region" readonly="readonly"><input type="hidden" id="regionId"></li>
                <li><dd>是否爆款:</dd><input name="explosionRecommend" type="checkbox" id="explosionRecommend" style="float: right"/></li>
                <li><dd>权重:</dd><input name="explosionWeight" id="explosionWeight"/></li>
                <li><span id="weightTips"></span></li>
                <li><dd>标题:</dd><input name="explosionDesc" id="explosionDesc"/></li>
            </form>
            <li>
                <a href="javascript:update()" class="green-button-ok">确&nbsp;&nbsp;&nbsp;&nbsp;定</a>
            </li>
        </ul>
        </form>
    </div>
</div>

<div class="container-right">
    <div class="header">
        <p style="width:100%; height:100%; line-height:67px; text-indent:15px; font-size:14px;" id="content"></p>
        <div class="header-button-box">
            <a href="javascript:show()" class="red-button-add add">搜索客栈</a>
            <form id="mainForm" action="${ctx}/leadIn/qq" method="get">
                <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
            </form>
        </div>
    </div>
    <div class="content2">
        <table border="0" cellpadding="0" cellspacing="0" class="room-date">
            <tr>
                <th scope="col">账号</th>
                <th scope="col">客栈名称</th>
                <th scope="col">微网址</th>
                <th scope="col">区域</th>
                <th scope="col">是否爆款</th>
                <th scope="col">权重</th>
                <th scope="col">标题</th>
            </tr>
                <c:forEach items="${page.result}" var="item">
                    <tr>
                        <td>${item.mobile}</td>
                        <td>${item.innName}</td>
                        <td>${item.weiAddress}</td>
                        <td>${item.regionName}</td>
                        <td>${item.explosionRecommend eq '1'?"爆款":""}</td>
                        <td>${item.explosionWeight}</td>
                        <td>${item.explosionDesc}</td>
                    </tr>
                </c:forEach>
        </table>
    </div>
    <div class="page-list">
        <tags:pagination page="${page}" paginationSize="5" />
    </div>
</div>
</body>
</html>

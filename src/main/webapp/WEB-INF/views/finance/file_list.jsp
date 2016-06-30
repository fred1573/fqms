<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>财务结算-Excel下载</title>
    <script type="text/javascript">
        var ctx = '${ctx}';
        function deleteFile(fileName) {
            if(confirm("是否要删除文件【" + fileName + "】?")) {
                $.post(ctx + "/finance/delete/file",{fileName:fileName},function(data){
                    alert(data.message);
                    window.location.reload();
                });
            }
        }
    </script>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/finance/order/list" method="post">
        <input type="hidden" name="settlementTime" value="${settlementTime}"/>
    </form>
    <div class="header">
        <div style="left: 550px; top: 15px" class="header-button-box duizhang kc">
        </div>
    </div>
    <table class="kz-table" cellpadding="8">
        <thead>
            <tr>
                <th>文件名</th>
                <th>最后修改时间</th>
                <th>操作</th>
            </tr>
        </thead>
        <c:forEach items="${fileList}" var="file">
            <tr>
                <td>${file.name}</td>
                <td><sw:dateTag value="${file.lastModified()}"></sw:dateTag></td>
                <td>
                    <a style="color: blue;" href="/download/${file.name}">下载</a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <a style="color: blue;" href="javascript:void(0);" onclick="deleteFile('${file.name}')">删除</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>

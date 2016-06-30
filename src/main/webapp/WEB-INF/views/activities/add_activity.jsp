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
    <script src="${ctx}/js/activity/activity.js"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
    <style>
        .red {
            color: red;
        }

        .container-right {
            padding-top: 10px;
            padding-left: 10px;
            box-sizing: border-box;
        }

        .container-right p,
        .container-right div {
            margin-bottom: 10px;
            display: table;
        }

        .container-right p label,
        .container-right div label {
            padding-right: 10px;
            display: table-cell;
            vertical-align: top;
        }

        .container-right p input,
        .container-right div textarea {
            display: table-cell;
        }

        .container-right div textarea {
            width: 450px;
            height: 100px;
        }

        .btn {
            width: 80px;
            height: 28px;
            margin-right: 10px;
            background: #d70007;
            color: #fff;
            border: none;
            cursor: pointer;
        }

        .btncancel {
            background: #999;
        }

        .date-line{
            width: 100px;height: 22px;font-size: 12px;text-indent: 15px
        }
    </style>
</head>

<body>

<div class="container-right">

    <%-- <form enctype="multipart/form-data" method="post">
         活动封面:<input type="file" name="imgFile"></br>
         <input type="text" name="fileId">
         <button id="SubMit"> 提交</button>
         <input type="submit" value="确认">
     </form>
 <p>================================================</p>--%>
    <form action="${ctx}/activity/add" enctype="multipart/form-data" method="post" onsubmit="return formBtn()">
        <p><label><i class="red">*</i>活动名称:</label> <input maxlength="30" type="text" name="activityName" value="${activity.activityName}" style="width: 450px; "
                                                           placeholder="最多输入30个文字"/></p>

        <p><label><i class="red" style="vertical-align: top">*</i><span style="vertical-align: top">活动封面:</span><img src="${activity.coverPicture}" height="70px" width="100px" style="margin-left: 11px"></label><input type="file" name="file"><label
                style="padding: 5px 0 0 5px;"><i class="red">*</i>支持不超过2M的图片</label></p>

        <p><label><i class="red">*</i>报名截止:</label><input class="date-line" type="text" name="dateLine" value="${activity.dateLine}"><input type="checkbox" name="recommend" value="true" <c:if test="${activity.recommend==true}">checked</c:if> style="margin-left: 15px;">推荐</p>

        <p><label><i class="red">*</i>活动时间:</label><input class="start-time" type="text" name="startTime" value="${activity.startTime}"
                                                          style="margin-right: 5px;">至<input class="end-time" type="text" name="endTime" value="${activity.endTime}"
                                                                                             style="margin-left: 5px;">
        </p>

        <div><label><i class="red">*</i>活动内容:</label> <textarea name="content" maxlength="99">${activity.content}</textarea></div>
        <div><label><i class="red">*</i>参加要求:</label><textarea name="require" maxlength="200">${activity.require}</textarea></div>
        <input type="hidden" name="id" value="${id}">
        <input type="hidden" name="operate" value="${operate}">
        <input type="hidden" name="publish" value="${activity.publishTime}">
        <input type="hidden" name="coverPicture" value="${activity.coverPicture}">
        <p style="margin-left: 68px;"><input type="submit" value="确认" class="btn">
            <button class="btn btncancel">取消</button>
        </p>
    </form>

</div>


</body>
</html>
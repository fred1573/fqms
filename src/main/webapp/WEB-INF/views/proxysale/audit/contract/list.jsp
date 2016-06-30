<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>代销平台-代销审核-合同审核</title>
    <link href="/css/proxysale/jquery-ui-1.10.1.css" rel="stylesheet">
    <link href="/css/proxysale/latoja.datepicker.css" rel="stylesheet">
    <link href="/css/proxysale/ui-dialog.css" rel="stylesheet">
    <link href="/css/proxysale/ui-widget.css" rel="stylesheet">
    <link href="/css/proxysale/ui-pagination.css" rel="stylesheet">
    <link href="/css/proxysale/audit.css" rel="stylesheet">
    <%--<link href="/css/zTreeStyle/style.css" rel="stylesheet">--%>
    <%--    <script src="http://libs.baidu.com/jquery/1.8.3/jquery.min.js"></script>--%>
    <script src="/js/proxysale/jquery-ui-1.10.1.min.js"></script>
    <script src="/js/proxysale/tc-popups.js"></script>
    <script src="/js/proxysale/tc-pagination.js"></script>
    <script src="/js/proxysale/contract.js"></script>
    <style type="text/css">
        .red {
            color: red;
        }

        .table_count {
            clear: both;
            width: 100%;
            height: 40px;
            position: relative;
            top: -20px;
        }

        .dul {
            width: 100%;
            height: 100%;
        }

        .dul .all span {
            position: relative;
            top: 5px;
        }

        .dul ul {
            margin: 0px;
            padding: 0px;
            margin-top: -30px;
        }

        .dul ul li {
            list-style: none;
            width: 80px;
            margin-left: 30px;
            float: left;
            height: 30px;
        }

        .all {
            width: 100%;
            height: 30px;
            margin-top: 10px;
        }

        .all span {
            margin-left: 30px;
        }

        .area_content {
            width: 100%;
            float: left;
        }

        .area_content_title {
            width: 100%;
            height: 25px;
            margin-top: 10px;
            clear: both;
            float: left
        }

        .area_content_title span {
            margin-left: 30px;
            float: left;
        }

        .boutique, .general {
            border-bottom: 1px solid #abcdef;
            float: left;
            width: 100%;
        }

        .general {
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<div class="container-right">
    <div class="header">
        <ul class="header-menu">
            <security:authorize ifAnyGranted="ROLE_代销平台-渠道管理">
                <li class="active"><a href="${ctx}/proxysale/ota">渠道管理</a></li>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_代销平台-客栈管理">
                <li class="active"><a href="${ctx}/proxysale/inn">客栈管理</a></li>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_代销平台-代销审核">
                <li><a href="#">代销审核</a></li>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_代销平台-调价管理">
                <li class="active"><a href="${ctx}/proxysale/price/list">调价管理</a></li>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_代销平台-已移除客栈">
                <li class="active"><a href="${ctx}/proxysale/inn/del_list">已移除客栈</a></li>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_代销操作记录">
                <li class="active"><a href="${ctx}/proxysale/inn/operateList">操作记录</a></li>
            </security:authorize>
        </ul>
        <div class="date-date">
            <a id="startDate" class="date"></a>
            <span class="to">至</span>
            <a id="endDate" class="date"></a>

            <input class="datepicker ll-skin-latoja startDate">
            <input class="datepicker ll-skin-latoja endDate">
        </div>
        <div class="search-box">
            <input type="text" placeholder="模糊搜索客栈名称/提交人" class="search" maxlength="40" id="keyword">
            <input type="button" class="search-button" id="search_submit">
        </div>
    </div>
    <ul class="tabLink">
        <li class="active"><a href="/proxysale/inn/price">房价审核</a></li>
        <li><a href="">合同审核</a></li>
    </ul>

    <table class="dx-table">
        <thead>
        <tr>
            <th width="15%">客栈名称</th>
            <th width="15%">提交时间</th>
            <th width="15%">提交人</th>
            <th width="15%">
                <select id="sh" autocomplete="off">
                    <option value="">任意状态</option>
                    <option value="UNCHECK">待审核</option>
                    <option value="REPEAT">已重新提交</option>
                    <option value="CHECKED">审核通过</option>
                    <option value="REJECTED">审核否决</option>
                </select>
            </th>
            <th width="15%">详情</th>
            <th width="25%">操作</th>
        </tr>
        </thead>
        <tbody>

        </tbody>
    </table>

    <div id="pager"></div>
</div>


<!--审核通过-->
<div id="review-pass" class="ui-popups">
    <div class="ui-popups-dialog">
        <em class="close" data-dismiss="popups">x</em>

        <div class="ui-popups-head">
            <h3 class="ui-popups-title">审核通过</h3>
        </div>
        <div class="ui-popups-body">

            <div class="scale">
                <div id="innName3"></div>
                <div class="select-consignment">设置总抽拥比例： <input type="text" id="scale"> %</div>
                <div class="center">
                    <button class="nextStep" id="nextStep1">下一步</button>
                </div>
                <div class="red warmmessage">*请输入正确的总抽佣比例，如不选择请点击取消</div>
            </div>
            <div class="dul">
                <div id="innName1"></div>
                <div class="area_content">
                    <%--<div class="boutique">
                        <div class="area_content_title"><span>精品</span></div>
                        <ul id="base_ul"></ul>
                    </div>--%>
                    <div class="general">
                        <div class="area_content_title"><span>普通</span><label style="float:right;">全选<input type="checkbox"  id="checkAllChannel" ></label> </div>
                        <ul id="sale_ul"></ul>
                    </div>
                </div>

            </div>
            <%-- <p class="review_tip">你确认通过 <span class="iName"></span> 的合同吗？</p>
             <input type="hidden" class="recordCode" value="">
             <input type="hidden" class="innId" value="">
             <input type="hidden" class="innName" value="">--%>
        </div>
        <div class="ui-popups-foot">
            <span id="confirm-pass" class="dialog_btn">确定</span>
            <span class="dialog_btn cancel_btn" data-dismiss="popups">取消</span>
        </div>
        <%--<div class="ui-popups-foot">
            <span id="confirm-pass" class="dialog_btn">确定</span>
            <span class="dialog_btn cancel_btn" data-dismiss="popups">取消</span>
        </div>--%>
    </div>
</div>


<!--审核否决-->
<div id="review-nopass" class="ui-popups">
    <div class="ui-popups-dialog">
        <em class="close" data-dismiss="popups">x</em>

        <div class="ui-popups-body">
            <p class="review_tip">请填写否决原因（必填）</p>
            <textarea class="reason"></textarea>
            <input type="hidden" class="innId" value="">
        </div>
        <div class="ui-popups-foot">
            <span id="confirm-nopass" class="dialog_btn">确定</span>
        </div>
    </div>
</div>


</body>

</html>

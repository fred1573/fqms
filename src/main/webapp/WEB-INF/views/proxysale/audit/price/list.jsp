<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<html>

<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>代销平台-代销审核-价格审核</title>
    <link href="/css/proxysale/jquery-ui-1.10.1.css" rel="stylesheet">
    <link href="/css/proxysale/latoja.datepicker.css" rel="stylesheet">
    <link href="/css/proxysale/ui-dialog.css" rel="stylesheet">
    <link href="/css/proxysale/ui-widget.css" rel="stylesheet">
    <link href="/css/proxysale/ui-pagination.css" rel="stylesheet">
    <link href="/css/proxysale/audit.css" rel="stylesheet">
    <%--    <script src="http://libs.baidu.com/jquery/1.8.3/jquery.min.js"></script>--%>
    <script src="/js/proxysale/jquery-ui-1.10.1.min.js"></script>
    <script src="/js/proxysale/tc-popups.js"></script>
    <script src="/js/proxysale/tc-pagination.js"></script>
    <script src="/js/proxysale/custom.js"></script>
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
            <input type="text" placeholder="模糊搜索客栈名称" class="search" maxlength="40" id="innName">
            <input type="button" class="search-button" id="search_submit">
        </div>
    </div>
    <ul class="tabLink">
        <li>
            <security:authorize ifAnyGranted="ROLE_代销审核-房价审核">
                <a href="#">房价审核</a>
            </security:authorize>
        </li>
        <li class="active">
            <security:authorize ifAnyGranted="ROLE_代销审核-合同审核">
                <a href="/proxysale/contract">合同审核</a>
            </security:authorize>
        </li>
    </ul>

    <table class="dx-table">
        <thead>
        <tr>
            <th width="15%">申请单号</th>
            <th width="15%">提交时间</th>
            <th width="15%">
                <select id="dx" autocomplete="off">
                    <option value="">合作模式</option>
                    <option value="NORMAL">普通代销</option>
                    <option value="JINGPIN">精品代销</option>
                </select>
            </th>
            <th width="15%">客栈名称</th>
            <th width="15%">
                <select id="sh" autocomplete="off">
                    <option value="">任 意</option>
                    <option value="UNCHECK">待审核</option>
                    <option value="CHECKED">审核通过</option>
                    <option value="REJECT">审核否决</option>
                </select>
            </th>
            <th width="25%">详情</th>
        </tr>
        </thead>
        <tbody></tbody>
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
            <p class="review_tip"><span class="iName"></span>，你确认通过？</p>
            <input type="hidden" class="recordCode" value="">
            <input type="hidden" class="innId" value="">
            <input type="hidden" class="innName" value="">
            <input type="hidden" class="pattern" value="">
        </div>
        <div class="ui-popups-foot">
            <span id="confirm-pass" class="dialog_btn">确定</span>
            <span class="dialog_btn cancel_btn" data-dismiss="popups">取消</span>
        </div>
    </div>
</div>


<!--审核否决-->
<div id="review-nopass" class="ui-popups">
    <div class="ui-popups-dialog">
        <em class="close" data-dismiss="popups">x</em>

        <div class="ui-popups-head">
            <h3 class="ui-popups-title">审核否决</h3>
        </div>
        <div class="ui-popups-body">
            <p class="review_tip"><span class="iName"></span>，请填写否决原因（选填）</p>
            <textarea class="reason"></textarea>
            <input type="hidden" class="recordCode" value="">
            <input type="hidden" class="innId" value="">
            <input type="hidden" class="innName" value="">
            <input type="hidden" class="pattern" value="">
        </div>
        <div class="ui-popups-foot">
            <span id="confirm-nopass" class="dialog_btn">确定</span>
        </div>
    </div>
</div>

</body>

</html>
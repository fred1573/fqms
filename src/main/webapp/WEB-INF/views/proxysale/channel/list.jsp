<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>代销平台-渠道管理</title>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/proxysale/channel.js" type="text/javascript"></script>
    <script src="${ctx}/js/area/area.js" type="text/javascript"></script>
    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script type="text/javascript">
        var ctx = '${ctx}';
        $.ready(getProvinces(1,''));

    </script>
    <style>
        .percentage{ height:20px;width: 60px;display: inline-block;text-indent: 0;text-align: center}
        .price{
            width: 15px;
            height: 15px;
        }
    </style>
</head>
<body>
<div class="container-right">
    <jsp:include page="../header_fragment.jsp"/>

    <table class="kz-table" cellpadding="8">
        <thead>
        <tr>
            <th>渠道名称</th>
            <th colspan="3">价格策略</th>
            <th>操作人</th>
            <th colspan="">操作</th>
        </tr>
        </thead>
        <c:forEach items="${otaInfos}" var="r">
            <input type="hidden" id="${r.channel.id}" value="${r.name}"/>
            <tr>
                <td>${ r.name }</td>
                <td>
                    <c:choose>
                        <c:when test="${r.channel.validBasePriceStrategy == null}">
                            -
                        </c:when>
                        <c:otherwise>精品(活动)（加价比例${r.channel.validBasePriceStrategy}%）</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${r.channel.validSalePriceStrategy == null}">
                            -
                        </c:when>
                        <c:otherwise>普通(卖)（分佣比例${r.channel.validSalePriceStrategy}%）</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${r.channel.validSaleBasePriceStrategy == null}">
                            -
                        </c:when>
                        <c:otherwise>普通(卖转底)（比例${r.channel.validSaleBasePriceStrategy}%）</c:otherwise>
                    </c:choose>
                </td>
                <td>${ r.channel.operator.sysUserCode }</td>
                <td><security:authorize ifAnyGranted="ROLE_渠道管理-编辑">
                <a onclick="show(${r.channel.id})" href="javascript:void(0)">编辑</a>
                </security:authorize></td>

            </tr>
        </c:forEach>
    </table>
</div>

<!-- 编辑框 start-->
<div class="center-box">
    <div class=" center-box-in audit-window" style="display:none;" id="edit">
        <a href="javascript:close('1')" class="close-window"></a>
        <h1>编辑渠道</h1>
        <input type="hidden" id="channelId" />
        <ul>
            <form id="editChannelForm" method="post">
                    <ul>
                        <li>
                            <dl class="chnnel-name">
                                <dt>渠道名称:</dt>
                                <dd id="channelNameEdit"></dd>
                                <span style="color: red">* 如果您选择了某种策略，请一定不要把比例留空，否则系统将视为没有勾选此策略</span>
                            </dl>
                        </li>
                        <li>
                            <dl class="chnnel-name">
                                <dt>选择价格策略:</dt>
                                <dd>
                                   <div><input id="basePS" type="checkbox" value="1" onclick="strategyChoose(1);"/>&nbsp;精品代销&nbsp;&nbsp;</div>
                                    <div><input type="text" id="basePer" class="percentage" disabled="true"/>%<br/></div>
                                    <div><input id="salePS" type="checkbox" value="2" onclick="strategyChoose(2);"/>&nbsp;普通代销&nbsp;&nbsp;</div>
                                    <div><label><input type="radio" name="price" class="price" value="0" checked>卖价 </label><label><input type="radio" name="price" class="price" value="1">卖转底价 </label></div>
                                    <div><input type="text" id="salePer" class="percentage" disabled="true"/>%</div>
                                </dd>
                            </dl>
                        </li>
                        <li>
                            <dl class="chnnel-name">
                                <dt>销售区域:</dt>
                                <dd class="chnnel-name-dd">
                                    <span><select class="areaLV1"></select></span>
                                   <%-- <select class="areaLV2" style="display: none"></select>--%>
                                    <a class="add-area">
                                        添加
                                    </a>
                                </dd>
                            </dl>
                        </li>
                    </ul>
                </form>
                <li>
                    <a href="javascript:edit()" class="green-button-ok">确&nbsp;&nbsp;&nbsp;&nbsp;定</a>
                </li>
                <%--<table>
                    <tr>
                        <td>渠道名称:</td>
                        <td id="channelNameEdit"></td>
                    </tr>
                    <tr>
                        <td>价格策略:</td>
                        <td>
                            <input id="basePS" type="checkbox" value="1" onclick="strategyChoose(1);"/>&nbsp;底价 加价比例&nbsp;&nbsp;<input type="text" id="basePer" class="percentage" readonly="readonly"/>%<br/>
                            <input id="salePS" type="checkbox" value="2" onclick="strategyChoose(2);"/>&nbsp;卖价 分佣比例&nbsp;&nbsp;<input type="text" id="salePer" class="percentage" readonly="readonly"/>%
                        </td>
                    </tr>
                    <tr>
                        <td>销售区域:</td>
                        <td>
                            <select id="areaLV1"></select>
                            <select id="areaLV2" style="display: none"></select>
                        </td>
                    </tr>


                </table>
            </form>
            <li>
                <a href="javascript:edit()" class="green-button-ok">确&nbsp;&nbsp;&nbsp;&nbsp;定</a>
            </li>
        </ul>--%>
        </form>
       </ul>
</div>
<!-- 编辑框 end-->
</body>
</html>
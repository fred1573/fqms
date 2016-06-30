<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
    <title>客诉中心</title>
    <script src="${ctx}/js/direct/complaint_list.js" type="text/javascript"></script>
    <script src="${ctx}/js/proxysale/jquery-ui-1.10.1.min.js" type="text/javascript"></script>
    <script src="${ctx}/js/common/layer/layer.js" type="text/javascript"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
    <link href="${ctx}/css/proxysale/jquery-ui-1.10.1.css" rel="stylesheet">
    <link href="${ctx}/css/direct/list.css" rel="stylesheet">
    <link href="${ctx}/js/common/layer/skin/layer.css" rel="stylesheet">
</head>
<body>
<form action="${ctx}/proxySaleOrder/complaint/search" method="post" id="mainForm">
    <input type="hidden" name="channelId" value="${complaintSearch.channelId}"/>
    <input type="hidden" name="channelName" value="${complaintSearch.channelName}"/>
    <input type="hidden" name="childChannelName" value="${complaintSearch.childChannelName}"/>
    <input type="hidden" name="page" id="page" value="${paginator.page}"/>
    <input type="hidden" name="queryValue" value="${complaintSearch.queryValue}"/>
    <input type="hidden" name="startTime" value="${complaintSearch.startTime}"/>
    <input type="hidden" name="endTime" value="${complaintSearch.endTime}"/>
    <input type="hidden" name="searchType" value="${complaintSearch.searchType}">
    <input type="hidden" name="complaintType"value="${complaintSearch.complaintType}">
    <input type="hidden" name="complaintStatus"value="${complaintSearch.complaintStatus}">
    <input type="hidden" name="searchTimeType"value="${complaintSearch.searchTimeType}">
</form>

<div class="container-right">
    <div class="header">
        <div style="margin-left: 20px;float: left;width: 98%;border-bottom: 1px solid #ccc;padding-bottom: 10px;">
            <a href="${ctx}/proxySaleOrder/complaint/search" class="kc-btn">客诉中心</a>
        </div>
    </div>
    <div style="left: 20px;top:75px;height: auto;" class="header-button-box duizhang kc">
        <select id="searchType">
            <option value="INN_NAME" <c:if test="${complaintSearch.searchType eq 'INN_NAME'}">selected</c:if>>客栈名称</option>
            <option value="REGION_NAME" <c:if test="${complaintSearch.searchType eq 'REGION_NAME'}">selected</c:if>>目的地</option>
            <option value="CUSTOMER_MANAGER" <c:if test="${complaintSearch.searchType eq 'CUSTOMER_MANAGER'}">selected</c:if>>客户经理</option>

        </select>

        <div class="search-box">
            <input type="text" id="queryValue" maxlength="30" class="search" placeholder="模糊搜索"
                   <c:if test="${not empty complaintSearch.queryValue}">value="${complaintSearch.queryValue}"</c:if>/>
            <input type="button" id="search_submit" class="search-button">
        </div>
        <select id="searchTimeType">
            <option value="CREATE_TIME" <c:if test="${complaintSearch.searchTimeType eq 'CREATE_TIME'}">selected</c:if>>
                首次处理日期
            </option>
            <option value="ORDER_TIME" <c:if test="${complaintSearch.searchTimeType eq 'ORDER_TIME'}">selected</c:if>>
                下单日期
            </option>
            <option value="CHECK_IN_AT"
                    <c:if test="${complaintSearch.searchTimeType eq 'CHECK_IN_AT'}">selected</c:if>>
                入住日期
            </option>
            <option value="CHECK_OUT_AT"
                    <c:if test="${complaintSearch.searchTimeType eq 'CHECK_OUT_AT'}">selected</c:if>>
                离店日期
            </option>
        </select>
        <input id="startTime" style="float: none;display: inline-block" class="date"
               value="<fmt:formatDate value="${complaintSearch.startTime}" pattern="yyyy-MM-dd"/>">至
        <input id="endTime" style="float: none;display: inline-block" class="date"
               value="<fmt:formatDate value="${complaintSearch.endTime}" pattern="yyyy-MM-dd"/>">
        <div style="position: absolute;right:12px;top:0;">
            <select id="ksCenterHandleType">
                <option value="">请选择类型</option>
                <option value="NONCOOPERATION" <c:if test="${complaintSearch.complaintType eq 'NONCOOPERATION'}">selected</c:if>>不与番茄合作</option>
                <option value="NO_ROOM" <c:if test="${complaintSearch.complaintType eq 'NO_ROOM'}">selected</c:if>>到店无房</option>
                <option value="PRICE_INCREMENT" <c:if test="${complaintSearch.complaintType eq 'PRICE_INCREMENT'}">selected</c:if>>客栈加价</option>
                <option value="UNABLE" <c:if test="${complaintSearch.complaintType eq 'UNABLE'}">selected</c:if>>不会操作系统</option>
                <option value="SYSTEM_ERROR" <c:if test="${complaintSearch.complaintType eq 'SYSTEM_ERROR'}">selected</c:if>>系统原因</option>
                <option value="ROOM_TYPE_ERROR" <c:if test="${complaintSearch.complaintType eq 'ROOM_TYPE_ERROR'}">selected</c:if>>房型/外网匹配错误</option>
                <option value="CANCEL_ORDER" <c:if test="${complaintSearch.complaintType eq 'CANCEL_ORDER'}">selected</c:if>>客人原因取消订单</option>
                <option value="CAN_NOT_CONTACT" <c:if test="${complaintSearch.complaintType eq 'CAN_NOT_CONTACT'}">selected</c:if>>客人联系不上商家</option>
                <option value="SUSPENSION_BUSINESS" <c:if test="${complaintSearch.complaintType eq 'SUSPENSION_BUSINESS'}">selected</c:if>>暂停营业</option>
                <option value="OTHER" <c:if test="${complaintSearch.complaintType eq 'OTHER'}">selected</c:if>>其他</option>
            </select>
            <label style="margin:0 10px"><input type="checkbox" id="ksCenterComplaintStatus" <c:if test="${complaintSearch.complaintStatus eq 'STARTED'}">checked</c:if>>未处理完成</label>
            <form id="exportForm" action="${ctx}/proxySaleOrder/complaint/exportExcel" method="post" class="fr">
                <a class="red-button-add add" href="javascript:exportExcel()">导出Excel</a>
            </form>
        </div>
    </div>

    <%--<div class="header-button-box" style="top:0px;">--%>
        <%--<form id="exportForm" action="${ctx}/proxySaleOrder/exportExcel" method="post">--%>
        <%--</form>--%>
        <%--<a class="red-button-add add" href="javascript:exportExcel()">导出Excel</a>--%>
    <%--</div>--%>

    <!--end header-->
    <table class="kz-table" cellpadding="0">
        <thead>

        <th>
            <select id="channelId">`
                <option value="">分销商</option>
                <option value="信用住" <c:if test="${complaintSearch.channelName eq '信用住'}">selected</c:if>>信用住</option>
                <c:forEach var="ota" items="${otaMap}">
                    <option value="${ota.key}"
                            <c:if test="${complaintSearch.channelName eq ota.value}">selected</c:if>>${ota.value}</option>
                </c:forEach>
            </select>
        </th>
        <th>
            <select id="childChannelId">
                <option value="">子分销商</option>
                <c:if test="${not empty childOtaMap}">
                    <c:forEach var="childOta" items="${childOtaMap}">
                        <option value="${childOta.key}"
                                <c:if test="${complaintSearch.childChannelName eq childOta.value}">selected</c:if>>${childOta.value}</option>
                    </c:forEach>
                </c:if>
            </select>
        </th>
        <th>目的地</th>
        <th>客栈名称</th>
        <th>客栈前台电话</th>
        <th>客户经理</th>
        <th>分销商订单号</th>
        <th>客人姓名</th>
        <th>手机号</th>
        <th>房型</th>
        <th>房间数</th>
        <th>分销商订单总价</th>
        <th>住离日期</th>
        <th>下单时间</th>
        <th>操作</th>
        </tr>
        </thead>
        <c:forEach items="${complaintList}" var="complaint">
            <tr>
                <td   <c:if test="${complaint.channelName eq '猎景'}"> style="color: #9031FE" </c:if>
                        <c:if test="${complaint.channelName eq '淘乐旅游'}"> style="color: #417505" </c:if>
                        <c:if test="${complaint.channelName eq '番茄众荟'}"> style="color: #950808 " </c:if>
                        <c:if test="${complaint.channelName eq '德比(同程)'}"> style="color: #D86507" </c:if>
                        <c:if test="${complaint.channelName eq '美团快订'}"> style="color: #30A585" </c:if>
                        <c:if test="${complaint.channelName eq '乐活旅行'}"> style="color: #325AA1" </c:if>
                        <c:if test="${complaint.channelName eq '笃行客栈'}"> style="color: #1C1C1C" </c:if>
                        <c:if test="${complaint.channelName eq '番茄运营'}"> style="color: #CF000F" </c:if>
                        <c:if test="${complaint.channelName eq '度假客'}"> style="color: #52B3D9" </c:if>
                        <c:if test="${complaint.channelName eq '一块去'}"> style="color: #F0A813" </c:if>
                        <c:if test="${complaint.channelName eq '代销平台'}"> style="color: #FF0C4A" </c:if>
                        <c:if test="${complaint.channelName eq '笑玩旅行社'}"> style="color: #818181" </c:if>>${complaint.channelName}
                </td>
                <td>${complaint.channelCodeName}</td>

                <td>${complaint.regionName}</td>
                <td>${complaint.innName}</td>
                <td>${complaint.innPhone}</td>
                <td>${complaint.customerManager}</td>
                <td>${complaint.channelOrderNo}</td>

                <td>${complaint.userName}</td>
                <td>${complaint.contact}</td>
                <td>
                    <c:forEach items="${complaint.channelOrderList}" var="subOrder">
                        ${subOrder.channelRoomTypeName}<br>
                    </c:forEach>
                </td>
                <td>
                    <c:forEach items="${complaint.channelOrderList}" var="subOrder">
                        ${subOrder.roomNums}<br>
                    </c:forEach>
                </td>
                <td>${complaint.totalAmount}</td>
                <td>
                    <c:forEach items="${complaint.channelOrderList}" var="subOrder">
                        <fmt:formatDate value="${subOrder.checkInAt}" pattern="yyyy-MM-dd"/>/<fmt:formatDate value="${subOrder.checkOutAt}" pattern="yyyy-MM-dd"/><br>
                    </c:forEach>
                </td>
                <td><fmt:formatDate value="${complaint.orderTime}" pattern="yyyy-MM-dd"/></td>
                <td>
                    <input type="hidden" name="orderNo" value="${complaint.orderNo}"/><%--oms订单号--%>


                    <span href="" style="color: #0033CC;cursor: pointer;" onclick="complaintHandling(this)">客诉处理</span>

                </td>
            </tr>
        </c:forEach>
    </table>
    <p class="acount">共有<span style="color: red">${paginator.totalCount}</span>个投诉，<span style="color: red">${complaintInnCount}</span>家客栈

    <div class="page-list">
        <ul>
            <li class="disabled"><a href="#" onclick="jumpPage(1)">首页</a></li>
            <c:if test="${paginator.page != 1}">
                <li class="disabled"><a href="#" onclick="jumpPage(${paginator.page - 1})">上一页</a></li>
            </c:if>
            <li>${paginator.page}/${paginator.totalPages}</li>
            <c:if test="${paginator.page != paginator.totalPages}">
                <li class="disabled"><a href="#" onclick="jumpPage(${paginator.page + 1})">下一页</a></li>
            </c:if>
            <li class="disabled"><a href="#" onclick="jumpPage(${paginator.totalPages})">末页</a></li>
        </ul>
    </div>
</div>
<div id="dialog">
    <div class="center-box">
        <div class="center-box-in audit-window" style="display:none;" id="orderDetails">
            <a class="close-window" id="orderDetailsClose"></a>

            <h3>订单详情</h3>

            <div>
                <ul>
                    <li><i id="ChannelId" style="display:none"></i></li>
                    <li>订单状态：<i id="conName">已接受</i></li>
                    <li>分销商订单号：<i id="channelOrderNo"></i></li>
                    <li>OMS订单号：<i id="orderNo"></i></li>
                    <li>价格模式：<i id="strategyType"></i></li>
                    <li>客栈名称：<i id="innName"></i></li>
                    <li>客人姓名：<i id="userName"></i></li>
                    <li>手机号码：<i id="contact"></i></li>
                    <li>房型：<i id="channelRoomTypeName"></i></li>
                    <li>
                        <div class="fl">房价：</div>
                        <div class="fl room-price-info">
                            <table class="fl">
                                <thead>
                                <th>住离日期</th>
                                <th>客栈单价</th>
                                <th>间/夜数</th>
                                </thead>
                                <tbody id="roomPriceInfo">
                                </tbody>

                            </table>
                        </div>
                    </li>
                    <li>分销商订单总价:<i id="totalAmount"></i></li>
                    <li>预付金额：<i id="paidAmount"></i></li>
                    <li>番茄总调价：<i id="extraPrice"></i><i style="float:right;margin-right:10px;"
                                                        id="customerManager">客户经理</i></li>
                </ul>
            </div>
            <div class="foot" id="footButton">
                <%--<button>修改状态</button>--%>
            </div>
        </div>
        <div class="center-box-in audit-window" style="display:none;" id="modifyOrderStatus">
            <a class="close-window" id="modifyOrderStatusClose"></a>

            <h3>修改状态</h3>

            <div style="padding: 10px;">
                <div>订单状态：<select>
                    <option>已取消</option>
                </select></div>
                <div>
                    <div>备注：</div>
                    <div>
                        <textarea class="mark" id="mark"></textarea>
                    </div>
                    <div class="fr">最多输入80个字</div>
                </div>
            </div>
            <div class="foot">
                <security:authorize ifAnyGranted="ROLE_取消订单">
                    <button class="fl" id="saveModify">保存</button>
                </security:authorize>
                <button class="fr" style="margin-right: 20px;" id="cancelModify">取消</button>
            </div>
        </div>
        <div class="center-box-in audit-window" style="display:none;" id="modifyRecordDia">
            <a class="close-window" id="modifyRecordClose"></a>

            <h3>修改状态</h3>

            <div style="padding: 10px;">
                <div><b id="operator"></b> 将订单状态:已接受修改为 <b style="color: red;">已取消</b></div>
                <div style="margin-top:10px;">
                    <div>备注：</div>
                    <div id="Mark">

                    </div>
                    <div id="operatTime"></div>
                </div>
            </div>
            <div class="foot">
                <button class="fl" id="returnOrderDetail">返回</button>
            </div>
        </div>
        <div class="center-box-in audit-window" style="display:none;" id="complaintHandling">
            <a class="close-window" onclick="closeWindow('complaintHandling')"></a>
            <h3>客诉处理</h3>
            <div style="padding: 10px;" class="handle-report">
                <div style="width:100%;float:left;">
                    <div class="fl">类型：</div>
                    <select id="handleType">
                        <option value="">请选择类型</option>
                        <option value="NONCOOPERATION">不与番茄合作</option>
                        <option value="NO_ROOM">到店无房</option>
                        <option value="PRICE_INCREMENT">客栈加价</option>
                        <option value="UNABLE">不会操作系统</option>
                        <option value="SYSTEM_ERROR">系统原因</option>
                        <option value="ROOM_TYPE_ERROR">房型/外网匹配错误</option>
                        <option value="CANCEL_ORDER">客人原因取消订单</option>
                        <option value="CAN_NOT_CONTACT">客人联系不上商家</option>
                        <option value="SUSPENSION_BUSINESS">暂停营业</option>
                        <option value="OTHER">其他</option>
                    </select>
                    <button class="fr" id="handleOver">处理完成</button>
                </div>
                <div style="width:100%">跟进纪录：</div>
                <textarea style="width:98%;height:100px;" id="trackRecord">

                </textarea>
                <div class="fr">最多输入80个字</div>
            </div>
            <div class="foot">
                <button class="fl" id="savecomplaintHandle">保存</button>
                <button onclick="closeWindow('complaintHandling')">取消</button>
            </div>
        </div>
        <div class="center-box-in audit-window" style="display:none;" id="complaintReport">
            <a class="close-window" onclick="closeWindow('complaintReport')"></a>
            <h3>客诉记录</h3>
            <div style="padding: 10px;" class="handle-list" id="handleList">
            </div>
            <div class="foot">
                <button class="fl" id="appendRecord">添加记录</button>
                <button onclick="closeWindow('complaintReport')">取消</button>
            </div>
        </div>
    </div>
    </div>
</div>
</body>
</html>
<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
    <title>代销订单</title>
    <script src="${ctx}/js/direct/order_list.js" type="text/javascript"></script>
    <script src="${ctx}/js/proxysale/jquery-ui-1.10.1.min.js" type="text/javascript"></script>
    <script src="${ctx}/js/common/layer/layer.js" type="text/javascript"></script>
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
    <link href="${ctx}/css/proxysale/jquery-ui-1.10.1.css" rel="stylesheet">
    <link href="${ctx}/css/direct/list.css" rel="stylesheet">
    <link href="${ctx}/js/common/layer/skin/layer.css" rel="stylesheet">
</head>
<security:authorize ifAnyGranted="ROLE_代销订单">

    <body>
    <form action="${ctx}/proxySaleOrder/list" method="post" id="mainForm">
        <input type="hidden" name="channelId" value="${proxySaleOrderForm.channelId}"/>
        <input type="hidden" name="childChannelId" value="${proxySaleOrderForm.childChannelId}"/>
        <input type="hidden" name="orderStatus" value="${proxySaleOrderForm.orderStatus}"/>
        <input type="hidden" name="queryValue" value="${proxySaleOrderForm.queryValue}"/>
        <input type="hidden" name="searchTimeType" value="${proxySaleOrderForm.searchTimeType}"/>
        <input type="hidden" name="startDate" value="${proxySaleOrderForm.startDate}"/>
        <input type="hidden" name="endDate" value="${proxySaleOrderForm.endDate}"/>
        <input type="hidden" name="page" id="page" value="${proxySaleOrderForm.page}"/>
        <input type="hidden" name="strategyType" value="${proxySaleOrderForm.strategyType}"/>
        <input type="hidden" name="queryType" value="${proxySaleOrderForm.queryType}">
    </form>

    <div class="container-right">
        <div style="height:30px;">

            <%@ page contentType="text/html;charset=UTF-8" language="java" %>
            <%@ include file="/common/taglibs.jsp" %>
            <div class="header">
                <div style="margin-left: 20px;float: left;width: 98%;border-bottom: 1px solid #ccc;padding-bottom: 10px;">
                    <security:authorize ifAnyGranted="ROLE_代销订单">
                        <a href="/proxySaleOrder/list" class="kc-btn">代销订单</a>

                    </security:authorize>
                    <security:authorize ifAnyGranted="ROLE_信用住订单">
                        <a href="/proxySaleOrder/creditList" class="kc-btn kc-active">信用住订单</a>
                    </security:authorize>
                    <security:authorize ifAnyGranted="ROLE_客诉管理">

                        <a href="/proxySaleOrder/complaint/search" class="fr ks-center-nav" target="_blank">客诉中心</a>
                    </security:authorize>
                </div>
            </div>


            <div style="left: 20px;top:75px;height: auto;" class="header-button-box duizhang kc">
                <select id="queryType">
                    <option value="1" <c:if test="${directOrderForm.queryType=='1'}">selected</c:if>>客栈名称</option>
                    <option value="2" <c:if test="${directOrderForm.queryType=='2'}">selected</c:if>>分销商订单号</option>
                    <option value="3" <c:if test="${directOrderForm.queryType=='3'}">selected</c:if>>OMS订单号</option>
                    <option value="4" <c:if test="${directOrderForm.queryType=='4'}">selected</c:if>>目的地</option>
                    <option value="5" <c:if test="${directOrderForm.queryType=='5'}">selected</c:if>>客户经理</option>
                </select>

                <div class="search-box">
                    <input type="text" id="queryValue" maxlength="30" class="search" placeholder="模糊搜索"
                           <c:if test="${not empty proxySaleOrderForm.queryValue}">value="${proxySaleOrderForm.queryValue}"</c:if>/>
                    <input type="button" id="search_submit" class="search-button">
                </div>
                <select id="searchTimeType">
                    <option value="CREATE" <c:if test="${proxySaleOrderForm.searchTimeType eq 'CREATE'}">selected</c:if>>
                        下单日期
                    </option>
                    <option value="CHECK_IN"
                            <c:if test="${proxySaleOrderForm.searchTimeType eq 'CHECK_IN'}">selected</c:if>>
                        入住日期
                    </option>
                    <option value="CHECK_OUT"
                            <c:if test="${proxySaleOrderForm.searchTimeType eq 'CHECK_OUT'}">selected</c:if>>
                        离店日期
                    </option>
                </select>
                <input id="startDate" style="float: none;display: inline-block" class="date"
                       value="${proxySaleOrderForm.startDate}">至
                <input id="endDate" style="float: none;display: inline-block" class="date"
                       value="${proxySaleOrderForm.endDate}">
                <div style="position: absolute;right:12px;top:0;">
                    <form id="exportForm" action="${ctx}/proxySaleOrder/exportExcel" method="post">
                        <a class="red-button-add add" href="javascript:exportExcel()">导出Excel</a>
                    </form>
                </div>
            </div>
                <%--<div class="header-button-box" style="top:0px;">--%>
                <%----%>
                <%--</div>--%>
        </div>

        <!--end header-->
        <table class="kz-table" cellpadding="0">
            <thead>

            <th>
                <select id="channelId">
                    <option value="">分销商</option>
                    <c:forEach var="ota" items="${otaMap}">
                        <option value="${ota.key}"
                                <c:if test="${proxySaleOrderForm.channelId eq ota.key}">selected</c:if>>${ota.value}</option>
                    </c:forEach>
                </select>
            </th>
            <th>
                <select id="childChannelId">
                    <option value="">子分销商</option>
                    <c:if test="${not empty childOtaMap}">
                        <c:forEach var="childOta" items="${childOtaMap}">
                            <option value="${childOta.key}"
                                    <c:if test="${proxySaleOrderForm.childChannelId eq childOta.key}">selected</c:if>>${childOta.value}</option>
                        </c:forEach>
                    </c:if>
                </select>
            </th>


            <th>目的地</th>
            <th>
                <select id="orderStatus">
                    <option value="">订单状态</option>
                    <option value="0" <c:if test="${proxySaleOrderForm.orderStatus eq '0'}">selected</c:if>>未处理</option>
                    <option value="1" <c:if test="${proxySaleOrderForm.orderStatus eq '1'}">selected</c:if>>已接受</option>
                    <option value="2" <c:if test="${proxySaleOrderForm.orderStatus eq '2'}">selected</c:if>>已拒绝</option>
                    <option value="3" <c:if test="${proxySaleOrderForm.orderStatus eq '3'}">selected</c:if>>已取消</option>
                    <option value="4" <c:if test="${proxySaleOrderForm.orderStatus eq '4'}">selected</c:if>>验证失败</option>
                </select>
            </th>
            <th>客栈名称</th>
            <th>客户经理</th>
            <th>分销商订单号</th>
            <th>客人姓名</th>
            <th>房型</th>
            <th>房间数</th>
            <th>夜数</th>
            <th>番茄总调价</th>
            <th>分销商订单总价/预付金额</th>
            <th>住离日期</th>
            <th>下单时间</th>
            <th>操作</th>
            </tr>
            </thead>
            <c:forEach items="${orderList}" var="order">
                <tr>
                    <td   <c:if test="${order.channelName eq '猎景'}"> style="color: #9031FE" </c:if>
                            <c:if test="${order.channelName eq '淘乐旅游'}"> style="color: #417505" </c:if>
                            <c:if test="${order.channelName eq '番茄众荟'}"> style="color: #950808 " </c:if>
                            <c:if test="${order.channelName eq '德比(同程)'}"> style="color: #D86507" </c:if>
                            <c:if test="${order.channelName eq '美团快订'}"> style="color: #30A585" </c:if>
                            <c:if test="${order.channelName eq '乐活旅行'}"> style="color: #325AA1" </c:if>
                            <c:if test="${order.channelName eq '笃行客栈'}"> style="color: #1C1C1C" </c:if>
                            <c:if test="${order.channelName eq '番茄运营'}"> style="color: #CF000F" </c:if>
                            <c:if test="${order.channelName eq '度假客'}"> style="color: #52B3D9" </c:if>
                            <c:if test="${order.channelName eq '一块去'}"> style="color: #F0A813" </c:if>
                            <c:if test="${order.channelName eq '代销平台'}"> style="color: #FF0C4A" </c:if>
                            <c:if test="${order.channelName eq '笑玩旅行社'}"> style="color: #818181" </c:if>>
                            ${order.channelName}
                    </td>
                    <td>${order.channelCodeName}</td>

                    <td>${order.regionName}</td>
                    <td>${order.conName}</td>
                    <td>${order.innName}</td>
                    <td>${order.customerManager}</td>
                    <td>${order.channelOrderNo}</td>

                    <td>${order.userName}</td>
                    <td>
                        <c:forEach items="${order.channelOrderList}" var="subOrder">
                            ${subOrder.channelRoomTypeName}<br>
                        </c:forEach>
                    </td>
                    <td>
                        <c:forEach items="${order.channelOrderList}" var="subOrder">
                            ${subOrder.roomTypeNums}<br>
                        </c:forEach>
                    </td>
                    <td>
                        <c:forEach items="${order.channelOrderList}" var="subOrder">
                            ${subOrder.nightNumber}<br>
                        </c:forEach>
                    </td>

                    <td>${order.extraPrice}</td>
                    <td>${order.totalAmount}/${order.paidAmount}</td>
                    <td>
                        <c:forEach items="${order.channelOrderList}" var="subOrder">
                            ${subOrder.checkInAt}/${subOrder.checkOutAt}<br>
                        </c:forEach>
                    </td>
                    <td>${order.orderTime}</td>
                    <td>
                        <input type="hidden" name="channelOrderId" value="${order.channelId}"/><%--渠道订单Id --%>
                        <input type="hidden" name="channelName" value="${order.channelName}"/><%--渠道名称 --%>
                        <input type="hidden" name="channelCodeName" value="${order.channelCodeName}"/><%--子分销商名称 --%>
                        <input type="hidden" name="regionName" value="${order.regionName}"/><%--目的地 --%>
                        <input type="hidden" name="innId" value="${order.innId}"/><%--客栈Id --%>
                            <%--<input type="hidden" name="innPhone" value="${order.innPhone}"/>&lt;%&ndash;客栈电话 &ndash;%&gt;--%>
                        <input type="hidden" name="contact" value="${order.contact}"/><%--联系方式 --%>
                        <input type="hidden" name="orderTime" value="${order.orderTime}"/> <%--下单时间--%>

                        <input type="hidden" name="conName" value="${order.conName}"/><%--订单状态--%>
                        <input type="hidden" name="channelOrderNo" value="${order.channelOrderNo}"/><%--分销商订单号--%>
                        <input type="hidden" name="orderNo" value="${order.orderNo}"/><%--oms订单号--%>
                        <input type="hidden" name="strategyType" value="${order.strategyType}"/><%--价格模式--%>
                        <input type="hidden" name="innName" value="${order.innName}"/><%--客栈名称--%>
                        <input type="hidden" name="userName" value="${order.userName}"/><%--客人姓名--%>
                        <input type="hidden" name="contact" value="${order.contact}"/><%--手机号码--%>
                        <input type="hidden" name="channelRoomTypeName"
                               value="${order.channelOrderList[0].channelRoomTypeName}"/><%--房型--%>
                        <c:forEach items="${order.channelOrderList}" var="o">
                            <div class="roomInfo">
                                <input type="hidden" name="channelRoomTypeName" value="${o.channelRoomTypeName}"/><%--房型--%>
                                <input type="hidden" name="bookPrice" value="${o.bookPrice}"/><%--价格--%>
                                <input type="hidden" name="roomTypeNums" value="${o.roomTypeNums}"/><%--房间数--%>
                                <input type="hidden" name="nightNumber" value="${o.nightNumber}"/><%--夜数--%>
                                <input type="hidden" name="checkInAt" value="${o.checkInAt}"/><%--入住日期--%>
                                <input type="hidden" name="checkOutAt" value="${o.checkOutAt}"/><%--离店日期--%>
                            </div>
                        </c:forEach>
                        <input type="hidden" name="totalAmount" value="${order.totalAmount}"/><%--订单总额--%>
                        <input type="hidden" name="paidAmount" value="${order.paidAmount}"/><%--预付金额--%>
                        <input type="hidden" name="extraPrice" value="${order.extraPrice}"/><%--番茄总调价--%>
                        <input type="hidden" name="customerManager" value="${order.customerManager}"/><%--客栈经理--%>
                        <input type="hidden" name="channelId" value="${order.channelChildId}"/><%--子渠道ID--%>

                        <span style="color: #0033CC;cursor: pointer;" onclick="details(this)">查看详情</span>

                        <security:authorize ifAnyGranted="ROLE_客诉管理">
                            <span style="color: #0033CC;cursor: pointer;padding-left:15px;" onclick="complaintHandling(this)">客诉处理</span>
                        </security:authorize>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <c:if test="${not empty pageUtil}">
            <p class="acount">共有<span style="color: red">${totalInfo.total}</span>个订单，总间夜数为<span
                    style="color: red">${totalInfo.nightNum}</span>，客栈订单总金额为<span
                    style="color: red">${totalInfo.totalPrice}</span>元,番茄调价总金额为<span
                    style="color: red">${totalInfo.totalExtraPrice}0</span>元</p>

            <div class="page-list">
                <ul>
                    <li class="disabled"><a href="#" onclick="jumpPage(1)">首页</a></li>
                    <c:if test="${pageUtil.currentPage != 1}">
                        <li class="disabled"><a href="#" onclick="jumpPage(${pageUtil.currentPage - 1})">上一页</a></li>
                    </c:if>
                    <li>${pageUtil.currentPage}/${pageUtil.pageCount}</li>
                    <c:if test="${pageUtil.currentPage != pageUtil.pageCount}">
                        <li class="disabled"><a href="#" onclick="jumpPage(${pageUtil.currentPage + 1})">下一页</a></li>
                    </c:if>
                    <li class="disabled"><a href="#" onclick="jumpPage(${pageUtil.pageCount})">末页</a></li>
                </ul>
            </div>
        </c:if>
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
    <div id="fullbg" class="body-hidden-block"></div>
    </body>
</security:authorize>

</html>
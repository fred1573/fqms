<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
    <title>番茄小站对账统计</title>
    <link rel="stylesheet" href="${ctx}/css/zTreeStyle/zTreeStyle.css" type="text/css">
    <script src="${ctx}/js/ztree/jquery.ztree.all-3.5.min.js" type="text/javascript"></script>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/bill/bill.js" type="text/javascript"></script>
</head>
<body class="bg1">
<!--确认结算-->
<div class="center-box">
    <div class=" center-box-in audit-window confirmEnd" style="display:none;top: -200px" id="msgbox">
        <h2>确认结算？</h2>
        <span>
        	<a href="javascript:void(0)" id="changeBtn" class="audit-pass-button">确认</a>
        	<a href="#" onclick="$('#msgbox').hide()" class="audit-nopass-button">取消</a>
        </span>
    </div>
</div>
<!--右边内容区域-->
<div class="container-right">
    <div class="header">
        <div style="margin-left: 20px;">
            <button urls="/fqms/apisale/channel"
                    class="kc-btn <c:if test='${billSearchBean.collection==true}'>kc-active</c:if>"
                    onclick="changePayType('true');">代收对账
            </button>
            <button urls="/fqms/apisale/channel/marketInn"
                    class="kc-btn <c:if test='${billSearchBean.collection==false}'>kc-active</c:if>"
                    onclick="changePayType('false');">现付对账
            </button>
            <button urls="${ctx}/bill/getFinancialAccount" class="kc-btn">财务对账
			</button>
            <button urls="${ctx}/bill/fastCheckAndCheckstand?type=xz_fast_checkIn" 
				<c:if test="${billSearchBean.type=='xz_fast_checkIn'}">class="kc-btn kc-active"</c:if>
				<c:if test="${billSearchBean.type!='xz_fast_checkIn'}">class="kc-btn"</c:if>
			>快速入住对账</button>
			<button urls="${ctx}/bill/fastCheckAndCheckstand?type=checkstand" 
				<c:if test="${billSearchBean.type=='checkstand'}">class="kc-btn kc-active"</c:if>
				<c:if test="${billSearchBean.type!='checkstand'}">class="kc-btn"</c:if>
			>扫码支付对账</button>
        </div>

        <div class="header-button-box duizhang" style="width: 600px">
            <select id="searchCondition" style="position: relative;height: 35px;font-size: 1.2em">
                <option value="0"
                        <c:if test="${billSearchBean.searchCondition==0}">selected="selected"</c:if> >客栈名称
                </option>
                <option value="1"
                        <c:if test="${billSearchBean.searchCondition==1}">selected="selected"</c:if>  >订单号码
                </option>
                <option value="2"
                        <c:if test="${billSearchBean.searchCondition==2}">selected="selected"</c:if> >手机号码
                </option>
            </select>

            <div class="search-box" style="position: relative;">
                <input type="text" id="keyWord" class="search" maxlength="20" id="search_input"
                       value="${billSearchBean.keyWord}">
                <input type="button" onclick="cleanInnIdAndSearch()" class="search-button" id="search_submit">
            </div>
            <div class="date-date" style="width: 300px;padding: 0">
                <a id="startDate" style="float: none;display: inline-block"
                   href="javascript:void(0)" onclick="WdatePicker({onpicked:changeStartDate})"
                   class="date WdateFmtErr WdateFmtErr WdateFmtErr">
                    <fmt:formatDate value="${billSearchBean.startDate}" pattern="yyyy-MM-dd"/>
                </a>
                至
                <a id="endDate" onchange="changeEndDate()"
                   style="float: none;display: inline-block;position: relative;left: 12px;" href="javascript:void(0)"
                   onclick="WdatePicker({onpicked:changeEndDate})" class="date WdateFmtErr WdateFmtErr WdateFmtErr">
                    <fmt:formatDate value="${billSearchBean.endDate}" pattern="yyyy-MM-dd"/>
                </a>
            </div>

        </div>

    </div>
    <!--end header-->
    <div class="content inn-cehckList duizhang-table">
        <table cellpadding="8">
            <tr>
                <c:if test="${billSearchBean.collection==true}">
                    <th>客栈/支付宝账号</th>
                </c:if>
                <c:if test="${billSearchBean.collection==false}">
                    <th>客栈</th>
                </c:if>
                <th>订单号</th>
                <th>姓名</th>
                <th>手机号码</th>
                <th>入住日期</th>
                <th>退房日期</th>
                <th>房型</th>
                <%--<th>数量</th>--%>
                <%--<th>单价</th>--%>
                <c:if test="${billSearchBean.collection==true}">
                    <th>已付金额</th>
                </c:if>
                <c:if test="${billSearchBean.collection==false}">
                    <th>订单金额</th>
                </c:if>
                <c:if test="${billSearchBean.collection==true}">
                    <th>支付时间</th>
                </c:if>
                <c:if test="${billSearchBean.collection==false}">
                    <th>下单时间</th>
                </c:if>
                <c:if test="${billSearchBean.collection==true}">
                    <th>操作</th>
                </c:if>
            </tr>
            <c:forEach var="detailBean" items="${list}">
                <tr>
                    <td><a href="javascript:void(0)" style="color: #000000"
                           onclick="searchByInnId('${detailBean.innId}')">${detailBean.innName}</a>
                        <c:if test="${billSearchBean.collection==true}"><br/>
                            <hr style="border:1;margin: 0">
                            ${detailBean.ZFBName} </c:if>
                    </td>
                    <td>${detailBean.orderNo}</td>
                    <td>${detailBean.name}</td>
                    <td>${detailBean.contact}</td>
                    <td>
                        <c:forEach var="roomDetailBean" items="${detailBean.roomDetailBeans}" varStatus="index">
                            <div <c:if
                                    test="${index.count==fn:length(detailBean.roomDetailBeans)}"> class="last" </c:if> >
                                <fmt:formatDate value="${roomDetailBean.checkInAt}" pattern="yyyy-MM-dd hh:mm"/></div>
                        </c:forEach>
                    </td>
                    <td>
                        <c:forEach var="roomDetailBean" items="${detailBean.roomDetailBeans}" varStatus="index">
                            <div <c:if
                                    test="${index.count==fn:length(detailBean.roomDetailBeans)}"> class="last" </c:if> >
                                <fmt:formatDate value="${roomDetailBean.checkOutAt}" pattern="yyyy-MM-dd HH:mm"/></div>
                        </c:forEach>
                    </td>
                    <td>
                        <c:forEach var="roomDetailBean" items="${detailBean.roomDetailBeans}" varStatus="index">
                            <div <c:if
                                    test="${index.count==fn:length(detailBean.roomDetailBeans)}"> class="last" </c:if> >${roomDetailBean.roomTypeName}</div>
                        </c:forEach>

                    </td>
                        <%--  <td>
                              <c:forEach var="roomDetailBean" items="${detailBean.roomDetailBeans}" varStatus="index">
                                  <div  <c:if
                                          test="${index.count==fn:length(detailBean.roomDetailBeans)}"> class="last" </c:if> >${roomDetailBean.count}</div>
                              </c:forEach>
                          </td>--%>
                        <%--<td>
                            <c:forEach var="roomDetailBean" items="${detailBean.roomDetailBeans}" varStatus="index">
                                <div <c:if
                                        test="${index.count==fn:length(detailBean.roomDetailBeans)}"> class="last" </c:if> >${roomDetailBean.unitPrice}</div>
                            </c:forEach>
                        </td>--%>
                    <td id="totalAmount${detailBean.mainOrderId}">${detailBean.totalAmount}</td>
                    <td><fmt:formatDate value="${detailBean.payAt}" pattern="yyyy-MM-dd hh:mm"/></td>
                    <c:if test="${billSearchBean.collection==true}">
                        <td>

                            <c:if test="${detailBean.isBalance=='0'}">
                                <a href="javascript:void(0)" <security:authorize
                                        ifAnyGranted="ROLE_小站对账"> onclick="changeIsBalance('${detailBean.payId}', '${detailBean.mainOrderId}', this)" </security:authorize>
                                   class="audit-nopass-button"
                                   <security:authorize
                                           ifNotGranted="ROLE_小站对账">style="background: gray" </security:authorize> >未结算</a>
                            </c:if>

                            <c:if test="${detailBean.isBalance=='1'}">
                                <a href="javascript:void(0)" class="audit-pass-button">已结算</a>
                            </c:if>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
        </table>
        <c:if test="${billSearchBean.collection==true}">
            <p class="acount">
                截止您所选时间段内，共有 <span style="color: red">${countBean.totalOrders}</span> 个订单，总金额为 <span
                    style="color: red"><fmt:formatNumber type="number" value="${countBean.totalAmount} "
                                                         maxFractionDigits="2"/></span>，其中 <span
                    style="color: red" id="notBalanceOrders">${countBean.notBalanceOrders}</span>个订单未结算，未结算金额为 <span
                    style="color: red" id="notBalanceAmount"><fmt:formatNumber type="number"
                                                                               value="${countBean.notBalanceAmount} "
                                                                               maxFractionDigits="3"/></span>。
            </p>
        </c:if>
        <c:if test="${billSearchBean.collection==false}">
            <p class="acount">
                截止您所选时间段内，共有 <span style="color: red">${countBean.totalOrders}</span> 个订单，总金额为 <span
                    style="color: red"><fmt:formatNumber type="number" value="${countBean.totalAmount} "
                                                         maxFractionDigits="2"/></span>。
            </p>
        </c:if>
        <c:if test="${null!=innDetail}">
            <p class="acount">
                <c:if test="${null!=innDetail.innName}">客栈名：${innDetail.innName}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.contactName}"> 联系人：${innDetail.contactName}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.contact}"> 电话号码：${innDetail.contact}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.ZFBName}"> 支付宝账号：${innDetail.ZFBName}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <br/>
                <c:if test="${null!=innDetail.bankName}"> 银行名称：${innDetail.bankName}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.bankCard}"> 银行账号：${innDetail.bankCard}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.bankCardHolder}"> 开户人：${innDetail.bankCardHolder}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
                <c:if test="${null!=innDetail.bankArea}"> 所在地区：${innDetail.bankArea}&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
            </p>
        </c:if>
        <div class="page-list">
            <ul>
                <c:if test="${billSearchBean.totalPage!=0}">
                    <li class="disabled"><a href="javascript:jumpPage(1)">首页</a></li>
                    <c:if test="${billSearchBean.nowPage>1}">
                        <li><a href="javascript:jumpPage(${billSearchBean.nowPage-1})">&lt;&lt;</a></li>
                    </c:if>
                    <c:if test="${billSearchBean.nowPage!=1}">
                        <li><a
                                href="javascript:jumpPage(${billSearchBean.nowPage-1})">${billSearchBean.nowPage-1}</a>
                        </li>
                    </c:if>
                    <li class="active"><a
                            href="javascript:jumpPage(${billSearchBean.nowPage})">${billSearchBean.nowPage}</a></li>
                    <c:if test="${billSearchBean.nowPage<billSearchBean.totalPage}">
                        <li><a href="javascript:jumpPage(${billSearchBean.nowPage+1})">${billSearchBean.nowPage+1}</a>
                        </li>
                    </c:if>
                    <c:if test="${billSearchBean.nowPage<billSearchBean.totalPage}">
                        <li><a href="javascript:jumpPage(${billSearchBean.nowPage+1})">&gt;&gt;</a></li>
                    </c:if>
                    <li><a href="javascript:jumpPage(${billSearchBean.totalPage})">末页</a></li>
                </c:if>
                <c:if test="${billSearchBean.totalPage==0}">
                    <li><a href="#">首页</a></li>
                    <li><a href="#">&lt;&lt;</a></li>
                    <li><a href="#">1</a></li>
                    <li><a href="#">&gt;&gt;</a></li>
                    <li><a href="#">末页</a></li>
                </c:if>
            </ul>
        </div>
    </div>
    <!--end content-->
    <form action="${ctx}/bill/count" method="post" id="searchForm">
        <input type="hidden" name="innId" value="${innId}"/>
        <input type="hidden" name="totalPage" value="${billSearchBean.totalPage}"/>
        <input type="hidden" name="nowPage" value="${billSearchBean.nowPage}"/>
        <input type="hidden" name="searchCondition" value="${billSearchBean.searchCondition}"/>
        <input type="hidden" name="keyWord" value="${billSearchBean.keyWord}"/>
        <input type="hidden" name="collection" value="${billSearchBean.collection}"/>
        <input type="hidden" name="startDate"
               value=" <fmt:formatDate value="${billSearchBean.startDate}" pattern="YYYY-MM-dd"/>"/>
        <input type="hidden" name="endDate"
               value=" <fmt:formatDate value="${billSearchBean.endDate}" pattern="YYYY-MM-dd"/>"/>
    </form>
</div>
<!--end container-right-->
</body>
</html>

<%--
  Created by IntelliJ IDEA.
  User: sam
  Date: 2016/3/18
  Time: 下午 03:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!---------------对账单上传弹出层----------------->
<div id="dialogBlackBg" style="display:none;">
    <div class="dialog-center-box">
        <div class="window-dialog" style="display:none;" id="modifyBill">
            <div style="margin:10px;float:left;width:97%;">
                <a href="javascript:close()" class="close-window"
                   onclick="$('#modifyBill').hide();$('#dialogBlackBg').hide();">X</a>
                <h1>修改订单</h1>
                <div>
                    <ul class="patternInfo">
                        <li>分销商：<span id="channelName"></span></li>
                        <li>价格模式：
                            <select id="orderMode" disabled="disabled">
                                <option value="">请选择模式</option>
                                <option value="1">精品（活动）</option>
                                <option value="2">普通（卖）</option>
                                <option value="3">普通（底）</option>
                            </select>
                        </li>
                        <li class="innCommissionRate">客栈总抽佣比例：<input type="text" class="ipt-pattern"
                                                                     id="innCommissionRate">%
                        </li>
                        <li class="channelCommissionRate">渠道分佣比例：<input type="text" class="ipt-pattern"
                                                                        id="channelCommissionRate">%
                        </li>
                        <li class="fqIncreaseRate">番茄加价比例:<input type="text" class="ipt-pattern" id="fqIncreaseRate">%
                        </li>
                        <li class="fqTemp">番茄暂收:<input type="text" class="fq-temp" id="fqTemp">
                        </li>
                    </ul>
                    <ul class="patternInfo">
                        <li>客栈名称：<span id="innName"></span></li>
                        <li>客栈ID：
                            <span id="innId"></span>
                        </li>
                        <li>分销商订单号：<span id="channelOrderNo"></span></li>
                        <li>预付金额：
                            <span id="paidAmount"></span>
                        </li>
                    </ul>
                    <ul class="patternInfo">
                        <li>客栈总价：<span id="innAmount"></span></li>
                        <li>分销商总价：<span id="totalAmount"></span></li>
                        <li>
                            <div class="ordinary-order">
                                <div>分销商结算金额：<span id="channelSettlementAmount"></span></div>
                                <div>客栈结算金额：<span id="innSettlementAmount"></span></div>
                            </div>
                            <div class="payout-order">
                                <div>分销商扣赔付金额<input id="channelDebit" type="text" class="ipt-pattern price"></div>
                                <div>客栈赔付金额：<input id="innPayment" type="text" class="ipt-pattern price"></div>
                                <div>番茄承担：<input id="fqBear" type="text" class="ipt-pattern price"></div>
                                <div>番茄收入：<input id="fqIncome" type="text" class="ipt-pattern price"></div>
                            </div>
                            <div class="refund-order">
                                <div>分销商扣款金额：<input id="channelRefund" type="text" class="ipt-pattern price"></div>
                                <div>客栈退款金额：<input id="innRefund" type="text" class="ipt-pattern price"></div>
                                <div class="fqRefundContactsOutDiv">番茄退往来：<input id="fqRefundContacts" type="text" class="ipt-pattern price"></div>
                                <div class="fqRefundCommissionOutDiv">番茄退佣金收入：<input id="fqRefundCommission" type="text" class="ipt-pattern price"></div>
                            </div>
                            <div class="replenishment-order">
                                <div>分销商结算金额：<span id="channelSettlementAmount2"></span></div>
                                <div>客栈结算金额：<span id="innSettlementAmount2"></span></div>
                                <div>番茄补款金额：<input id="fqReplenishment" type="text" class="ipt-pattern price"></div>
                            </div>
                        </li>
                    </ul>
                    <div id="patternInfo">
                        <ul class="patternInfo patternInfo1">
                            <li id="channelRoomTypeName" class="channelRoomTypeName">
                                <%-- <div>
                                    <div class="delete-mask"></div>
                                    <div class="delete-liner"></div>
                                     房型:<input type="text" class="room-type">
                                     房间数：<input type="text" class="ipt-pattern">单价：<input type="text" class="ipt-pattern">住离日期：<input type="text" class="ipt-innInfo datepicker">至<input type="text" class="ipt-innInfo datepicker">
                                     <button class="deleteChannalOrder" value="删除">删除</button>
                                     &lt;%&ndash;<button class="add-room">添加房间 </button>&ndash;%&gt;
                                 </div>--%>
                            </li>
                        </ul>
                    </div>
                    <ul class="patternInfo">
                        <li>
                            订单状态：
                            <select class="order-status" id="orderStatusStr">
                                <option>订单状态</option>
                                <option value="0">未处理</option>
                                <option value="1">已接受</option>
                                <option value="2">已拒绝</option>
                                <option value="3">已取消</option>
                                <option value="4">验证失败</option>
                                <option value="66">赔付</option>
                                <option value="77">退款</option>
                                <option value="88">补款</option>
                            </select>
                        </li>
                        <li style="display:none" class="innSettlement">
                            <input type="checkbox" id="innSettlement">与客栈结算
                        </li>
                        <li>
                            修改原因： <input type="text" class="modify-reason" id="modifyReason">
                        </li>
                    </ul>
                    <ul class="patternInfo">
                        <li>
                            产生周期：
                            <select id="accountPeriodList">
                                <%-- <option></option>--%>
                            </select>
                        </li>
                    </ul>
                    <div class="footbtn">
                        <button id="enterModify">确认修改</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!---------------对账单上传弹出层----------------->

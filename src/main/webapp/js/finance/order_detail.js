// 绑定事件
$(document).ready(function () {
    $(".header-sub-tab a").on("click", function () {
        var $this = $(this)
        if (!$this.hasClass('active')) {
            $(".header-sub-tab").find('.active').removeClass('active')
            $this.addClass('active')
        }
    })
    $("#orderStatusStr").on("change", function () {
        selectOption($(this).val());
    })
    $.datepicker.regional["zh-CN"] = {
        closeText: "关闭",
        prevText: "&#x3c;上月",
        nextText: "下月&#x3e;",
        currentText: "今天",
        monthNames: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
        monthNamesShort: ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"],
        dayNames: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"],
        dayNamesShort: ["周日", "周一", "周二", "周三", "周四", "周五", "周六"],
        dayNamesMin: ["日", "一", "二", "三", "四", "五", "六"],
        weekHeader: "周",
        dateFormat: "yy-mm-dd",
        firstDay: 1,
        isRTL: !1,
        showMonthAfterYear: !0,
        yearSuffix: "年"
    }
    $.datepicker.setDefaults($.datepicker.regional["zh-CN"]);
    $("#channelOrderList").on('focus', '.datepicker', function () {
        $(this).datepicker();
    });
    //点击查询
    $("#search_submit").bind("click", function () {
        search();
    });
    $("#priceStrategy").bind("change", function () {
        search();
    });
    $("#orderStatus").bind("change", function () {
        search();
    });
    $("#auditStatus").bind("change", function () {
        search();
    });
    $("#isBalance").bind("change", function () {
        search();
    });
});
document.onkeydown = keyDownSearch;
function keyDownSearch(e) {
    // 兼容FF和IE和Opera
    var theEvent = e || window.event;
    var code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    if (code == 13) {
        //具体处理函数
        search();
        return false;
    }
    return true;
}
function getLocalTime(nS) {
    var date = new Date(nS);
    return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();
}
function selectOption(channelDebit) {
    switch (channelDebit) {
        case "66" :
            $(".refund-order,.replenishment-order,.ordinary-order").hide();
            $(".payout-order").show();
            $(".innSettlement").hide();
            break;
        case "77" :
            $(".payout-order,.replenishment-order,.ordinary-order").hide();
            $(".innSettlement").show();
            $(".refund-order").show();
            break;
        case "88" :
            $(".payout-order,.refund-order,.ordinary-order").hide();
            $(".replenishment-order").show();
            $(".innSettlement").hide();
            break;
        default :
            $(".payout-order,.refund-order,.replenishment-order,.ordinary-order").hide();
            $(".ordinary-order").show();
            $(".innSettlement").hide();
            break;
    }
}
// 查看订单详情
function modifyBill(id) {
    var specitalOrderId;
    $('.delete-mask').hide();
    $('.delete-liner').hide();
    $.post(ctx + "/finance/order/getOrder", {id: id}, function (data) {
        $('#channelRoomTypeName').html('');
        $('#channelOrderList>div').html('');
        $('#modifyReason').val('');
        // 请求成功
        if (data.status == 200) {
            if (data.result.specialOrder && data.result.specialOrder.id) {
                specitalOrderId = data.result.specialOrder.id;
            }
            selectOption(data.result.parentOrder.status.toString())
            $.each(data.result.accountPeriodList, function (akey, aval) {
                $("#accountPeriodList").append("<option>" + aval + "</option>");
            })
            $("#accountPeriodList").val(data.result.parentOrder.produceTime);
            $('#orderStatusStr').val(data.result.parentOrder.status);
            $('#dialogBlackBg').show();
            $('#modifyBill').show();
            $('#channelName').html(data.result.parentOrder.channelName);
            $('#orderMode').val(data.result.parentOrder.priceStrategy);
            $('#fqTemp').val(data.result.parentOrder.fqTemp);
            $('#modifyReason').val(data.result.parentOrder.modifyReason);
            if (data.result.parentOrder.priceStrategy === 1) {
                $('.fqIncreaseRate').show();
                $('#fqIncreaseRate').val(data.result.parentOrder.increaseRate);
                $('.innCommissionRate').hide();
                $('.channelCommissionRate').hide();
            } else {
                $('.fqIncreaseRate').hide();
                $('.innCommissionRate').show();
                $('.channelCommissionRate').show();
                $('#innCommissionRate').val(data.result.parentOrder.innCommissionRate);
                $('#channelCommissionRate').val(data.result.parentOrder.channelCommissionRate);
            }
            $('#innName').html(data.result.parentOrder.innName);
            $('#innId').html(data.result.parentOrder.innId);
            $('#channelOrderNo').html(data.result.parentOrder.channelOrderNo);
            $('#innAmount').html(data.result.parentOrder.innAmount);
            $('#totalAmount').html(data.result.parentOrder.totalAmount);
            $('#paidAmount').html(data.result.parentOrder.paidAmount);
            $('#channelRoomTypeName').val(data.result.parentOrder.channelRoomTypeName);
            $('#channelSettlementAmount').html(data.result.parentOrder.channelSettlementAmount);
            $('#channelSettlementAmount2').html(data.result.parentOrder.channelSettlementAmount);
            $('#innSettlementAmount').html(data.result.parentOrder.innSettlementAmount);
            $('#innSettlementAmount2').html(data.result.parentOrder.innSettlementAmount);
            if (data.result.specialOrder) {
                $("#channelDebit").val(data.result.specialOrder.channelDebit)
                $("#innPayment").val(data.result.specialOrder.innPayment)
                $("#fqBear").val(data.result.specialOrder.fqBear);
                $("#fqIncome").val(data.result.specialOrder.fqIncome);
                $("#channelRefund").val(data.result.specialOrder.channelRefund);
                $("#innRefund").val(data.result.specialOrder.innRefund);
                $("#fqRefundCommission").val(data.result.specialOrder.fqRefundCommission);
                $("#fqRefundContacts").val(data.result.specialOrder.fqRefundContacts);
                $("#fqReplenishment").val(data.result.specialOrder.fqReplenishment);
                if (data.result.specialOrder.innSettlement) {
                    $("#innSettlement").attr("checked", "checked");
                    $(".fqRefundContactsOutDiv").hide();
                    $(".fqRefundCommissionOutDiv").css('display', 'table-cell');
                }
            }
            for (var i = 0; i < data.result.parentOrder.channelOrderList.length; i++) {
                $('#channelRoomTypeName').append('<div id=' + data.result.parentOrder.channelOrderList[i].id + '><div class="delete-mask"></div><div class="delete-liner"></div>房型:<span class="room-type">' + data.result.parentOrder.channelOrderList[i].channelRoomTypeName + '</span>房间数：<input type="text" class="ipt-pattern room-number" value=' + data.result.parentOrder.channelOrderList[i].roomTypeNums + '>客栈单价：<input type="text" class="ipt-pattern childInnAmount" value=' + data.result.parentOrder.channelOrderList[i].innAmount + '>分销商单价：<input type="text" class="ipt-pattern channel-account" value=' + data.result.parentOrder.channelOrderList[i].bookPrice + '>住离日期：<input type="text" class="ipt-innInfo datepicker checkin" value=' + getLocalTime(data.result.parentOrder.channelOrderList[i].checkInAt) + '>至<input type="text" class="ipt-innInfo datepicker checkout" value=' + getLocalTime(data.result.parentOrder.channelOrderList[i].checkOutAt) + '> <button class="deleteChannalOrder" value="删除">删除</button></div>');
                if (data.result.parentOrder.channelOrderList[i].deleted) {
                    $("#channelRoomTypeName").find(".delete-mask").eq(i).show();
                    $("#channelRoomTypeName").find(".delete-liner").eq(i).show();
                    $("#channelRoomTypeName").find(".deleteChannalOrder").eq(i).text("恢复");
                }
            }
            $('#channelRoomTypeName').off().on('click', '.deleteChannalOrder', function () {
                if ($(this).text() == '删除') {
                    $(this).siblings('.delete-mask').show();
                    $(this).siblings('.delete-liner').show();
                    $(this).text('恢复');
                } else {
                    $(this).siblings('.delete-mask').hide();
                    $(this).siblings('.delete-liner').hide();
                    $(this).text('删除');
                }

            })
            if (data.result.parentOrder.settlementStatus == "1") {
                $(".ipt-pattern,.order-status,.ipt-innInfo,.modify-reason,.deleteChannalOrder").attr("disabled", true)
            }
        } else {
            // 请求失败提示错误原因
            alert(data.message);
        }
    });
    $("#innSettlement").on("change", function () {
        if ($(this).is(":checked")) {
            $(".fqRefundCommissionOutDiv").css("display", "table-cell");
            $(".fqRefundContactsOutDiv").hide();
        } else {
            $(".fqRefundCommissionOutDiv").hide();
            $(".fqRefundContactsOutDiv").css("display", "table-cell");
        }
    })
    $('#enterModify').off().on('click', function () {
        var channelOrderList = [];
        for (var i = 0; i < $('#channelRoomTypeName>div').length; i++) {
            channelOrderList[i] = {
                id: $('#channelRoomTypeName>div').eq(i).attr('id'),
                channelRoomTypeName: $('.room-type').eq(i).val(),
                roomTypeNums: $('.room-number').eq(i).val(),
                innAmount: $('.childInnAmount').eq(i).val(),
                checkInAtStr: $('.checkin').eq(i).val(),
                checkOutAtStr: $('.checkout').eq(i).val(),
                bookPrice: $('.channel-account').eq(i).val()
            }
            if ($('.deleteChannalOrder').eq(i).text() == '恢复') {
                channelOrderList[i].deleted = true;
            } else {
                channelOrderList[i].deleted = false;
            }
        }
        var status = $('#orderStatusStr').val();
        var specialOrder = {
            id: specitalOrderId
        };
        if (status == 66) {
            specialOrder.channelDebit = $("#channelDebit").val();
            specialOrder.innPayment = $("#innPayment").val();
            specialOrder.fqBear = $("#fqBear").val();
            specialOrder.fqIncome = $("#fqIncome").val();
        }
        if (status == 77) {
            specialOrder.channelRefund = $("#channelRefund").val();
            specialOrder.innRefund = $("#innRefund").val();
            specialOrder.innSettlement = $("#innSettlement").is(":checked");
            if ($("#fqRefundCommission").is(":hidden")) {
                specialOrder.fqRefundContacts = $("#fqRefundContacts").val();
            } else {
                specialOrder.fqRefundCommission = $("#fqRefundCommission").val();
            }
        }
        if (status == 88) {
            specialOrder.fqReplenishment = $("#fqReplenishment").val();
        }
        var jsonData = {
            id: id,
            innAmount: $('#innAmount').html(),
            totalAmount: $('#totalAmount').html(),
            channelOrderList: channelOrderList,
            status: status,
            produceTime: $("#accountPeriodList").val(),
            fqTemp:$('#fqTemp').val(),
            modifyReason: $('#modifyReason').val()
        }
        var modifyReason = $('#modifyReason').val();
        if ($('#orderMode').val() == 1) {
            jsonData.increaseRate = $('#fqIncreaseRate').val();
        } else {
            jsonData.innCommissionRate = $('#innCommissionRate').val();
            jsonData.channelCommissionRate = $('#channelCommissionRate').val();
        }
        var json = {
            parentOrder: jsonData,
            modifyReason: modifyReason
        }
        if (status == 66 || status == 77 || status == 88) {
            json.specialOrder = specialOrder;
        }
        var updateOrderUrl = ctx + "/finance/order/updateOrder?jsonData=" + JSON.stringify(json);
        $.post(updateOrderUrl, function (rs) {
            if (rs.status === 500) {
                alert(rs.message);
                return;
            }
            $('#modifyBill').hide();
            $('#dialogBlackBg').hide();
            // submitForm();
            location.reload();
        })
    })
}
// 提交表单
function submitForm() {
    $("#mainForm").submit();
}
// 分页查询
function search() {
    $("input[name='priceStrategy']").val($("#priceStrategy").val());
    $("input[name='auditStatus']").val($("#auditStatus").val());
    $("input[name='isBalance']").val($("#isBalance").val());
    $("input[name='orderStatus']").val($("#orderStatus").val());
    $("input[name='keyWord']").val($("#keyWord").val());
    submitForm();
}
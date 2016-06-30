
$(document).ready(function () {
    // 改变结算月份事件
    $("#settlementTime").bind("change", function () {
        search();
    });
    // 生成进、出账单
    $("#createOrder").bind("click", function() {
        var settlementTime = $("input[name='settlementTime']").val();
        if (confirm("您确定要生成【" + settlementTime + "】的进出账单？")) {
            alert("本次生成进出账的客栈较多，预计耗时10分钟");
            $.post(ctx + "/finance/order/settlement",{settlementTime:settlementTime},function(data){
                alert(data.message);
            });
        }
    });
    $("#upload").on('click', function () {
        $(".windowBg2").show();
        close();
        var channelId = $("input[name='channelId']").val();
        var settlementTime = $("input[name='uploadSettlementTime']").val();
        $.ajaxFileUpload({
            url: ctx + '/finance/upload/income?channelId=' + channelId + '&settlementTime=' + settlementTime,
            secureuri: false,
            fileElementId: 'file',//file标签的id
            dataType: 'json',
            success: function (result) {
                if (result.status == 200) {
                    var auditStatusStr = "已核失败";
                    if(result.auditStatus == '1') {
                        auditStatusStr = "已核成功";
                    }
                    var str = "本次对账的结果:" + auditStatusStr + "\n";
                    str += "本月番茄订单数量:" + result.fqOrders + "\n";
                    str += "渠道上传对账单中的订单数量:" + result.channelOrders + "\n";
                    str += "对账成功订单数:" + result.successOrders + "\n";
                    str += "对账失败订单数:" + result.failureOrders + "\n";
                    str += "账单中遗漏的订单数量:" + result.channelMissOrderAmount + "\n";
                    str += "对账单中遗漏的订单号码:" + result.channelMissOrderNo + "\n";
                    str += "番茄遗漏的订单数量:" + result.fqMissOrderAmount + "\n";
                    str += "番茄遗漏的订单号码:" + result.fqMissOrderNo + "\n";
                    alert(str);
                    close();
                    $(".windowBg2").hide();
                    window.location.reload();
                } else {
                    alert(result.message);
                    close();
                    $(".windowBg2").hide();
                    window.location.reload();
                }
            },
            error: function (data, status, e) {
                alert(e);
                close();
                $(".windowBg2").hide();
                window.location.reload();
            }
        });
    });
});
// 上传对账单
function openDiv(channelId, settlementTime) {
    $("#file").val("");
    $("input[name='channelId']").val(channelId);
    $("input[name='uploadSettlementTime']").val(settlementTime);
    $("#edit").fadeIn();
}
//关闭显示框
function close() {
    $("#edit").fadeOut();
}

// 点击渠道名称，跳转到渠道账单详情页面
function showOrderDetail(channelId, settlementTime) {
    if (settlementTime == null || settlementTime == '') {
        alert("请选择结算月份");
        return;
    }
    window.location = ctx + "/finance/order/detail?channelId=" + channelId + "&settlementTime=" + settlementTime;
}
// 提交表单
function submitForm() {
    $("#mainForm").submit();
}
// 分页查询
function search() {
    $("input[name='settlementTime']").val($("#settlementTime").val());
    submitForm();
}

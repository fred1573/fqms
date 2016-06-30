// 绑定时间
$(document).ready(function () {
    $(".header-sub-tab a").on("click", function () {
        var $this = $(this)
        if (!$this.hasClass('active')) {
            $(".header-sub-tab").find('.active').removeClass('active')
            $this.addClass('active')
        }
    })
    //点击查询
    $("#search_submit").bind("click", function () {
        search();
    });
    $("#settlementTime").bind("change", function () {
        search();
    });
    $("#confirmStatus").bind("change", function () {
        search();
    });
    $("#settlementStatus").bind("change", function () {
        search();
    });
    $("#isTagged").bind("change", function () {
        search();
    });
    $("#isMatch").bind("change", function () {
        search();
    });
    $("#channelId").bind("change", function () {
        var channelId = $("#channelId").val();
        var settlementTime = $("#settlementTime").val();
        if (settlementTime == null || settlementTime == '' || settlementTime == undefined) {
            alert("请先选择账期");
            return;
        }
        if (channelId != null && channelId != '' && channelId != undefined) {
            location.href = ctx + "/finance/inn/channelSettlement?settlementTime=" + settlementTime + "&channelId=" + channelId;
        }
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

// 导出Excel
function exportOut() {
    var settlementTime = $("#settlementTime").val();
    var status = "normal";
    if (settlementTime == "") {
        alert("请选择结算月份");
        return;
    }
    if (confirm("您确定要生成【" + settlementTime + "】的全部客栈结算详情的Excel？本次生成进出账的客栈较多，预计耗时5分钟")) {
        $.post(ctx + "/finance/batch/export/out", {settlementTime: settlementTime, status: status}, function () {
            alert("生成完毕!");
        });
    }
}

// 一键结算
function batchSettlement(settlementTime) {
    if (settlementTime == "") {
        alert("请选择结算月份");
    } else {
        if (confirm("您确定要将【" + settlementTime + "】的全部客栈(除已标注)出账结算设置为已结算？")) {
            $.post(ctx + "/finance/inn/batchSettlement", {settlementTime: settlementTime}, function (result) {
                alert(result.message);
                window.location.reload();
            });
        }
    }
}

// 发送账单
function sendBill(settlementTime) {
    if (settlementTime == "") {
        alert("请选择结算月份");
    } else {
        if (confirm("是否确认发送【" + settlementTime + "】的客栈结算账单？")) {
            $.post(ctx + "/finance/inn/sendBill", {settlementTime: settlementTime}, function (result) {
                alert(result.message);
            });
        }
    }
}

// 结算
function settlement(id, innName, status, settlementTime,innId) {


    $("#Settlement").show();
    $("#confirmSettlement").hide();
    $("#dialogBlackBg").show();
    $("#disputesDelay").show();
    $("#AnnName").html(innName);
    $("#bstatus").val(status);
    $("#time").val(settlementTime);
    $("#idNum").val(id);
    $("#innId").val(innId);
}

function confirmSelct() {
    var id = $("#idNum").val();
    var settlementStatus;
    var time = $("#time").val()
    var status = $("#bstatus").val();
    var innId=$("#innId").val();
    var message;
    if ($("#settlementWay").html() == "已结算") {
        settlementStatus = 1;
    }
    if ($("#settlementWay").html() == "未结算") {
        settlementStatus = 0;
    }
    if ($("#settlementWay").html() == "纠纷延期") {
        settlementStatus = 2;
    }
    if (status == 1 && settlementStatus == 0) {
        message = 1;
    } else {
        message = 0
    }

    $.post(ctx + "/finance/inn/settlement", {
        id: id,
        settlementStatus: settlementStatus,
        message: message,
        settlementTime: time,
        innId:innId
    }, function (result) {
        if (result.status == 200) {
            window.location.reload();
        } else {
            alert(result.message);
        }
    });
}

// 标注客栈/取消标注客栈
function tagInn(id, innName, isTagged) {
    var operator = isTagged ? "标注" : "取消标注";
    if (confirm("是否确认" + operator + "【" + innName + "】？")) {
        $.post(ctx + "/finance/inn/tag", {id: id, isTagged: isTagged}, function (result) {
            if (result.status == 200) {
                search();
            }
        });
    }
}
function showDetail(innId, settlementTime, status) {
    if (settlementTime == null || settlementTime == '') {
        alert("请选择结算月份");
        return;
    }
    window.location = ctx + "/finance/out/channel/detail?innId=" + innId + "&settlementTime=" + settlementTime + "&status=" + status;
}

function showDetailChannel(channelId, innId, settlementTime, status) {
    if (settlementTime == null || settlementTime == '') {
        alert("请选择结算月份");
        return;
    }


    window.location = ctx + "/finance/out/detail?innId=" + innId + "&settlementTime=" + settlementTime + "&channelId=" + channelId + "&status=" + status;
}

// 提交表单
function submitForm() {
    $("#mainForm").submit();
}
// 分页查询
function search() {
    $("input[name='innName']").val($("#innName").val());
    $("input[name='settlementTime']").val($("#settlementTime").val());
    $("input[name='confirmStatus']").val($("#confirmStatus").val());
    $("input[name='settlementStatus']").val($("#settlementStatus").val());
    $("input[name='isTagged']").val($("#isTagged").val());
    $("input[name='isMatch']").val($("#isMatch").val());
    submitForm();
}
//确定为已结算还是纠纷延期
function confirmSettlement(This) {
    $("#Settlement").hide();
    $("#confirmSettlement").show();
    $("#settlementWay").html($(This).html());
}
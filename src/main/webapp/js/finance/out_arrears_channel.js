/**
 * Created by admin on 2016/4/12.
 */
$(document).ready(function () {
    $("#isMatch").bind("change", function () {
        search();
    });
    $("#settlementTime").bind("change", function () {
        search();
    });
    $("#search_submit").bind("click", function () {
        search();
    });
})

function search() {
    $("input[name='isMatch']").val($("#isMatch").val());
    $("input[name='settlementTime']").val($("#settlementTime").val());
    $("input[name='innName']").val($("#innName").val());
    submitForm();
}
//平账结算导出Excel
function partialArrearsExportOut() {
    var settlementTime = $("#settlementTime").val();
    var status = "partial";
    if (settlementTime == "") {
        alert("请选择结算月份");
        return;
    }
    if (confirm("您确定要生成【" + settlementTime + "】的全部客栈结算详情的Excel？")) {
        $.post(ctx + "/finance/batch/export/out", {settlementTime: settlementTime, status: status}, function () {
            alert("生成完毕!");
        });
    }
}

function submitForm() {
    $("#mainForm").submit();
}

function close(num) {
    switch (num) {
        case "1" :
            $("#dialogBlackBg").hide();
            $("#accountPaid").hide();
            break;
    }
}
//平账金额
function amountPaid(This) {
    $("#dialogBlackBg").show();
    $("#accountPaid").show();
    $("#id").val($(This).prev().val());
    $("#remaining").val($(This).prev().prev().val());
}
//提交平账对话框数据
function submitPaidData() {
    var datas = {};
    datas.id = $("#id").val();

    datas.paymentRemark = $("#paymentRemark").val();
    datas.settlementTime = $("input[name='settlementTime']").val();
    datas.remaining = $("#realPayment").val();
    datas = JSON.stringify(datas);
    $.post("/finance/out/levelArrears?jsonData=" + datas, function (result) {
        close(1);
        alert(result.message);
        //刷新当前页面.
        location.reload();
    })
}

//实付金额
function amountPaidA(This) {
    $("#dialogBlackBg").show();
    $("#accountPaid").show();
    $("#annName").html($(This).parent().parent().children().find('a').eq(0).html());
    $("#realPayment").val($(This).prev().prev().val());
    $("#paymentRemark").val($(This).prev().val());
    $("#id").val($(This).prev().prev().prev().val());
}
//提交实付金额对话框数据
function submitPaidDataA() {
    var datas = {};
    datas.id = $("#id").val();
    datas.realPayment = $("#realPayment").val();
    datas.paymentRemark = $("#paymentRemark").val();
    datas.settlementTime = $("input[name='settlementTime']").val();
    datas.channelId = $("#channelId").val();
    datas = JSON.stringify(datas);
    $.post("/finance/inn/updatePayment?jsonData=" + datas, function (result) {
        close(1);
        alert(result.message);
        //刷新当前页面.
        location.reload();
    })
}
function close(num) {
    switch (num) {
        case "1" :
            $("#dialogBlackBg").hide();
            $("#accountPaid").hide();
            break;
    }
}
// 挂账结算导出Excel
function arrearsExportOut() {
    var settlementTime = $("#settlementTime").val();
    var status = "arrears";
    if (settlementTime == "") {
        alert("请选择结算月份");
        return;
    }
    if (confirm("您确定要生成【" + settlementTime + "】的全部客栈结算详情的Excel？")) {
        $.post(ctx + "/finance/batch/export/out", {settlementTime: settlementTime, status: status}, function () {
            alert("生成完毕!");
        });
    }
}
// 绑定时间
$(document).ready(function () {
    //点击查询
    $("#search_submit").bind("click", function () {
        search();
    });
    $("#priceStrategy").bind("change", function () {
        search();
    });
    $("#auditStatus").bind("change", function () {
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

// 导出Excel
function exportIncomeInn() {
    var settlementTime = $("input[name='settlementTime']").val();
    var channelId = $("input[name='channelId']").val();
    var channelName = $("input[name='channelName']").val();
    if (settlementTime == "" || settlementTime == null) {
        alert("请选择结算月份");
    }
    if (channelId == "" || channelId == null) {
        alert("分销商ID不能为空");
    }
    if (confirm("您确定要生成【" + settlementTime + "】的分销商[" + channelName + "]结算详情的Excel？")) {
        $.post(ctx + "/finance/income/export/inns", {settlementTime: settlementTime,channelId : channelId}, function () {
            alert("生成完毕!");
        });
    }
}

// 收款
function arrival(id, channelName) {
    if (confirm("是否确认收款【" + channelName + "】")) {
        $.post(ctx + "/finance/channel/arrival", {id: id}, function (result) {
            alert(result.message);
            if (result.status == 200) {
                window.location.reload();
            }
        });
    }
}

// 提交表单
function submitForm() {
    $("#mainForm").submit();
}
// 分页查询
function search() {
    $("input[name='priceStrategy']").val($("#priceStrategy").val());
    $("input[name='auditStatus']").val($("#auditStatus").val());
    $("input[name='channelOrderNo']").val($("#channelOrderNo").val());
    submitForm();
}



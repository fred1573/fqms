// 绑定时间
$(document).ready(function () {
    $(".header-sub-tab a").on("click",function(){
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
    $("#channelId").bind("change", function () {
        search();
    });
    $("#priceStrategy").bind("change", function () {
        search();
    });
    $("#auditStatus").bind("change", function () {
        search();
    });
    $("#isArrival").bind("change", function () {
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
function exportOut() {
    var settlementTime = $("input[name='exportSettlementTime']").val();
    var innId = $("input[name='exportInnId']").val();
    if (innId == "" || innId == null) {
        alert("客栈ID不能为空");
        return;
    }
    if (settlementTime == "" || settlementTime == null) {
        alert("请选择结算月份");
        return;
    }
    $("#exportForm").submit();
}

// 提交表单
function submitForm() {
    $("#mainForm").submit();
}
// 分页查询
function search() {
    $("input[name='channelId']").val($("#channelId").val());
    $("input[name='channelOrderNo']").val($("#channelOrderNo").val());
    $("input[name='auditStatus']").val($("#auditStatus").val());
    $("input[name='priceStrategy']").val($("#priceStrategy").val());
    $("input[name='isArrival']").val($("#isArrival").val());
    submitForm();
}



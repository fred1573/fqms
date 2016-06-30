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
        search(1);
    });
    $("#isMatch").bind("change", function () {
        search(1);
    });
    $("#channelId").bind("change", function () {
        var channelId = $("#channelId").val();
        var settlementTime = $("input[name='settlementTime']").val();
        if(settlementTime == null || settlementTime == '' || settlementTime == undefined) {
            alert("请先选择账期");
            return;
        }
        if(channelId == null || channelId == '' || channelId == undefined) {
            location.href=ctx + "/finance/out/list?settlementTime=" + settlementTime;
        } else {
            search(1);
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
        search(1);
        return false;
    }
    return true;
}

function showDetail(innId, settlementTime) {
    if (settlementTime == null || settlementTime == '') {
        alert("请选择结算月份");
        return;
    }
    window.location = ctx + "/finance/out/detail?innId=" + innId + "&settlementTime=" + settlementTime;
}

// 提交表单
function submitForm() {
    $("#mainForm").submit();
}
// 分页查询
function search(pageNo) {
    $("input[name='innName']").val($("#innName").val());
    $("input[name='isMatch']").val($("#isMatch").val());
    $("input[name='channelId']").val($("#channelId").val());
    if(pageNo != null && pageNo != '' && pageNo != undefined) {
        $("input[name='pageNo']").val(pageNo);
    }
    submitForm();
}

//实付金额
function amountPaid(This) {
    $("#dialogBlackBg").show();
    $("#accountPaid").show();
    $("#annName").html($(This).parent().parent().children().find('a').eq(0).html());
    $("#realPayment").val($(This).prev().prev().val());
    $("#paymentRemark").val($(This).prev().val());
    $("#id").val($(This).prev().prev().prev().val());
}
//提交实付金额对话框数据
function submitPaidData(){
    var datas = {};
    datas.id = $("#id").val();
    datas.realPayment = $("#realPayment").val();
    datas.paymentRemark = $("#paymentRemark").val();
    datas.settlementTime = $("input[name='settlementTime']").val();
    datas.channelId = $("#channelId").val();
    datas = JSON.stringify(datas);
   $.post("/finance/inn/updatePayment?jsonData=" + datas,function(result){
       close(1);
       alert(result.message);
       //刷新当前页面.
       location.reload();
    })
}
function close(num){
    switch(num){
        case "1" :
            $("#dialogBlackBg").hide();
            $("#accountPaid").hide();
            break;
    }
}

// 渠道导出Excel
function batchChannelExportOut() {
    var strText=$("#channelId").find("option:selected").text();
    var settlementTime = $("#settlementTime").val();
    var channelId = $("#channelId").val();
    if (settlementTime == "") {
        alert("请选择结算月份");
        return;
    }
    if (confirm("您确定要生成【" + settlementTime +"("+strText+ ")】的全部客栈结算详情的Excel？")) {
        $.post(ctx + "/finance/batch/out/channel", {settlementTime: settlementTime,channelName:strText,channelId:channelId}, function () {
            alert("生成完毕!");
        });
    }
}

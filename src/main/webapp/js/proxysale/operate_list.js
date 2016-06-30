$(document).ready(function (e) {
    //点击查询
    $("#search_submit").bind("click", function () {
        search();
    });
    $("#settlementTime").bind("change", function () {
        search();
    });
    $("#operateType").bind("change", function () {
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

//时间比较
function dateCompare(startdate, enddate) {
    var arr = startdate.split("-");
    var starttime = new Date(arr[0], arr[1], arr[2]);
    var starttimes = starttime.getTime();

    var arrs = enddate.split("-");
    var lktime = new Date(arrs[0], arrs[1], arrs[2]);
    var lktimes = lktime.getTime();

    if (starttimes > lktimes) {
        return false;
    }
    else
        return true;
}
//提交表单
function submitForm() {
    $("#mainForm").submit();
}
//查询
function search() {
    if (dateCompare($("#startDate").html().trim(), $("#endDate").html().trim())) {
        $("input[name='startDate']").val($("#startDate").html().trim());
        $("input[name='endDate']").val($("#endDate").html().trim());
        $("input[name='innName']").val($("#innName").val());
        $("input[name='settlementTime']").val($("#settlementTime").val());
        $("input[name='operateType']").val($("#operateType").val());
        submitForm();
    }
    else {
        alert("起始时间必须小于结束时间");
    }
}
function changeStartDate() {
    $("input[name='startDate']").val($("#startDate").html().trim());
    search();
}
function changeEndDate() {
    $("input[name='endDate']").val($("#startDate").html().trim());
    search();
}


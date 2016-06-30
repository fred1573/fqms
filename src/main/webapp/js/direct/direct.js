$(document).ready(function (e) {
    //点击查询
    $("#search_submit").bind("click", function () {
        search(1);
    });
    $("#channelId").bind("change", function () {
        search(1);
    });
    $("#orderStatus").bind("change", function () {
        search(1);
    });
    $("#searchTimeTyep").bind("change", function () {
        search(1);
    });
});

//页面跳转
function jumpPage(page) {
    $("input[name='page']").val(page);
    search();
}

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
    } else {
        return true;
    }
}

//提交表单
function submitForm() {
    $("#mainForm").submit();
}
//查询
function search(page) {
    if (dateCompare($("#startDate").html().trim(), $("#endDate").html().trim())) {
        $("input[name='channelId']").val($("#channelId").val());
        $("input[name='orderStatus']").val($("#orderStatus").val());
        $("input[name='innName']").val($("#innName").val());
        $("input[name='searchTimeTyep']").val($("#searchTimeTyep").val());
        $("input[name='startDate']").val($("#startDate").html().trim());
        $("input[name='endDate']").val($("#endDate").html().trim());
        if(page != null && page != '' && page != undefined) {
            $("input[name='page']").val(page);
        } else {
            $("input[name='page']").val($("#page").val());
        }
        submitForm();
    } else {
        alert("起始时间必须小于结束时间");
    }
}
function changeStartDate() {
    $("input[name='totalPage']").val("0");
    $("input[name='startDate']").val($("#startDate").html().trim());
    search(1);
}
function changeEndDate() {
    $("input[name='totalPage']").val("0");
    $("input[name='endDate']").val($("#endDate").html().trim());
    search(1);
}


/**
 * Created by xiamaoxuan on 2014/8/5.
 */

$(document).ready(function(e){
	$("div.header button").bind("click", function(event){
		var $btn = $(event.currentTarget);
		url = $btn.attr("urls");
		window.location = url;
	});
	
	$("#isBalance,#productCode").on("change", function(){
		var $enty = $(this);
		var name = $enty.attr("name");
		$("#mainForm input[name="+name+"]").val($enty.val());
		search();
	});
});

document.onkeydown=keyDownSearch;
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
//改变结算状态
function changeIsBalance(id, code, obj) {
    $("#msgbox").show();
    $("#changeBtn").bind("click", function () {
        $.getJSON(ctx+"/bill/change", {
        	"id": id,
        	"code": code
        }, function (reponse) {
            if(reponse=="true"||reponse==true){
                $obj=$(obj);
                try{
	                var oldNotBalanceAmount=$("#notBalanceAmount").html().replace(",", "").trim();
	                var oldNotBalanceOrders=$("#notBalanceOrders").html().trim();
	                var clickTotalAmount=$("#totalAmount"+code).html().trim();
	                $("#notBalanceAmount").html((oldNotBalanceAmount-clickTotalAmount).toFixed(2));
	                $("#notBalanceOrders").html(oldNotBalanceOrders-1);
                }catch(e){
                }
                $obj.removeClass("audit-nopass-button");
                $obj.addClass("audit-pass-button");
                $obj.html("已结算");
                $obj.unbind("click");
            }else{
                confirm("服务器内部错误，请稍后再试");
            }
            $("#changeBtn").unbind("click");
            $("#msgbox").hide();
        });
    });
}
function searchByName(name){
    $("input[name='searchCondition']").val(0);
    $("input[name='keyWord']").val(name);
    $("input[name='startDate']").val($("#startDate").html().trim());
    $("input[name='endDate']").val($("#endDate").html().trim());
    $("input[name='nowPage']").val(1);
    submitForm();
}
//提交表单
function submitForm() {
    $("#mainForm").submit();
}
//页面跳转
function jumpPage(page) {
    $("input[name='nowPage']").val(page);
    //去掉时间格式的空格
    $("input[name='startDate']").val($("#startDate").html().trim());
    $("input[name='endDate']").val($("#endDate").html().trim());
    submitForm();
}
//查询
function search() {
    if (dateCompare($("#startDate").html().trim(), $("#endDate").html().trim())) {
        $("input[name='searchCondition']").val($("#searchCondition").val());
        $("input[name='nowPage']").val(1);
        $("input[name='keyWord']").val($("#keyWord").val());
        $("input[name='startDate']").val($("#startDate").html().trim());
        $("input[name='endDate']").val($("#endDate").html().trim());
        submitForm();
    }
    else {
        alert("起始时间必须小于结束时间");
    }
}
function changeStartDate() {
    $("input[name='totalPage']").val("0");
    $("input[name='startDate']").val($("#startDate").html().trim());
    search();
}
function changeEndDate() {
    $("input[name='totalPage']").val("0");
    $("input[name='endDate']").val($("#startDate").html().trim());
    search();
}

function checkDate($date){
	var id = $date.attr("id");
	var $other = $("div.date-choose span[id!="+id+"]");
	var from = new Date($("#activeDate").text());
	var to = new Date($("#toDate_select").text());
	var days = Date.diffDay($("#activeDate").text(), $("#toDate_select").text()) + 1;
	$("#selectDate").val($("#activeDate").text());
	$("#toDate").val($("#toDate_select").text());
	$("#sortDate").val("");
}
function searchByInnId(innId){
    $("input[name='innId']").val(innId);
    $("input[name='totalPage']").val("0");
    search();
}
function cleanInnIdAndSearch() {
    $("input[name='innId']").val("");
    $("input[name='totalPage']").val("0");
    search();
}

function changePayType(isCollection){
    $("input[name='collection']").val(isCollection);
    $("input[name='innId']").val("");
    search();
}

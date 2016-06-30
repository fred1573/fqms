var ENUM_BALANCE = {
		"OK":"1",
		"NOT":"0"
}


$(document).ready(function(e){
	
	$("#searchType_select").bind("change", function(){
		$("#searchType").val($("#searchType_select").val());
	});
	
	$("#search_submit").bind("click", function(){
		$("#input").val($("#search_input").val());
		if(!isEmpty($("#input").val())){
			search();
		}
	});
	
	$("#channelId,#isBalance").bind("change", function(){
		search();
	})
	
	$("table.kz-table").on("click", "td.op_type[balace=0] span", function(){
		$btn = $(this).parent();
		var orderId = $btn.parent().attr("orderId");
		showBalanceOrderDiv(orderId);
	});
	
	$("table.kz-table").on("click", "td.op_type[balace=1] span", function(){
		$btn = $(this).parent();
		var orderId = $btn.parent().attr("orderId");
		showCancelBalanceOrderDiv(orderId);
	});
	
	$("table.kz-table").on("click", "td span.name[tag=name]", function(){
		$btn = $(this);
		var innId = $btn.parent().attr("innId");
		$("#innIds").val(innId);
		search();
	});
	
	$("table.kz-table").on("click", "td span.name[tag=orderNo]", function(){
		$btn = $(this);
		var orderNo = $btn.parent().attr("orderNo");
		getOrderInfo(orderNo);
	});
	
	initTotalAmount();
});

function searchWithFromDate(){
	var $date = $("#fromDate_select");
	$("#pageNo").val("1");
	checkDate($date);
	search();
}

function searchWithToDate(){
	var $date = $("#toDate_select");
	$("#pageNo").val("1");
	checkDate($date);
	search();
}

function checkDate($date){
	var id = $date.attr("id");
	var $other = $("div.date-date span[id!="+id+"]");
	var from = new Date($("#fromDate_select").text());
	var to = new Date($("#toDate_select").text());
	var days = Date.diffDay($("#fromDate_select").text(), $("#toDate_select").text()) + 1;
	if(days > 31){
		if(id == "fromDate_select"){
			var tem = from;
			tem.addDays(30);
			$other.html(tem.format("yyyy-MM-dd"));
		}else{
			var tem = to;
			tem.addDays(-30);
			$other.html(tem.format("yyyy-MM-dd"));
		}
	}
	
	if(days < 1){
		if(id == "fromDate_select"){
			$other.html(from.format("yyyy-MM-dd"))
		}else{
			$other.html(to.format("yyyy-MM-dd"))
		}
	}
	$("#fromDate").val($("#fromDate_select").text());
	$("#toDate").val($("#toDate_select").text());
}

function showBalanceOrderDiv(orderId){
	$("#balace_order_div a.audit-pass-button").attr("onclick", "balanceOrder("+orderId+", "+ENUM_BALANCE.OK+")");
	showAlertDialog('balace_order_div');
}

function showCancelBalanceOrderDiv(orderId){
	$("#cancel_balace_order_div a.audit-pass-button").attr("onclick", "balanceOrder("+orderId+", "+ENUM_BALANCE.NOT+")");
	showAlertDialog('cancel_balace_order_div');
}

function balanceOrder(orderId, isBalance){
	var url = ctx+"/apisale/channel/balanceOrder"+generateUrlEndStr();
	var data = {
			"id": orderId,
			"isBalance": isBalance
	};
	$.post(url, data).done(function(json) {
		if(json.status == 200){
			refresh();
		}
	});
}

function initTotalAmount(){
	$trs = $("table.kz-table tr[orderId!=0]");
	$trs.each(function(i,obj){
		countPriceDifference(obj);
	});
}

function countPriceDifference(obj){
	var $tr = $(obj);
	var money = 0.0;
	$tr.find("span.date_s").each(function(i, obj){
		var $span = $(obj);
		var from = new Date($span.attr("from"));
		var to = new Date($span.attr("to"));
		var nights = Date.diffDay(from.format("yyyy-MM-dd"), to.format("yyyy-MM-dd"));
		$tr.find("span.night_s[index="+i+"]").html(nights);
		var inPrice = parseFloat($tr.find("span.inprice_s[index="+i+"]").text());
		var salePrice = parseFloat($tr.find("span.saleprice_s[index="+i+"]").text());
		money += (salePrice - inPrice)*nights;
	});
	$tr.find("td[tag=totalCount]").text(money);
}

function getOrderInfo(orderNo){
	var url = ctx+"/apisale/channel/getOrder"+generateUrlEndStr();
	var data = {
			"channelOrderNo": orderNo
	};
	$.post(url, data).done(function(json) {
		if(json.status == 200){
			setOrderInfo2Div(json.result);
			showAlertDialog('order_detail_div');
		}
	});
}

function setOrderInfo2Div(mainOrder){
	$("#order_detail_div td[tag=name]").text(mainOrder.userName);
	$("#order_detail_div td[tag=mobile]").text(mainOrder.contact);
	$("#order_detail_div td[tag=checkIn]").html("");
	$("#order_detail_div td[tag=checkOut]").html("");
	$("#order_detail_div td[tag=roomNos]").html("");
	var length = mainOrder.channelOrders.length;
	$.each(mainOrder.channelOrders, function(i, obj){
		var from = new Date(obj.checkInAt);
		var to = new Date(obj.checkOutAt);
		var content = '';
		content = '<span>'+from.format("yyyy-MM-dd")+'</span>';
		if(i+1 != length){
			content += '<hr/>'
		}
		$("#order_detail_div td[tag=checkIn]").append(content);
		content = '<span>'+to.format("yyyy-MM-dd")+'</span>';
		if(i+1 != length){
			content += '<hr/>'
		}
		$("#order_detail_div td[tag=checkOut]").append(content);
		content = '<span>'+obj.roomNo+'</span>';
		if(i+1 != length){
			content += '<hr/>'
		}
		$("#order_detail_div td[tag=roomNos]").append(content);
	});
}
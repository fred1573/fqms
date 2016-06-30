var ENUM_OP_TYPE = {
		"ADD":"添加",
		"EDIT":"编辑"
}

var g_join_market = "1";

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
	
	$("#add_inn_btn").bind("click", function(event){
		showAlertDialog('search_inn_div');
	});
	
	$("table.kz-table").on("click", "td.oper a[tag=remove]", showRemoveDiv);
	
	$("#search_inn_btn").bind("click", searchInn);
	
	$("table.kz-table").on("click", "td.oper a[tag=edit]", showEditDiv);
	
	$("#price_sale").bind("click", function(){
		isShowCommission();
	});
	
});

function showRemoveDiv(event){
	var $btn = $(event.currentTarget);
	var innId = $btn.attr("innId");
	var mobile = $btn.parent().parent().find("td[tag=mobile]").html();
	var $removeBtn = $("#remove_inn_div a.audit-pass-button");
	$removeBtn.attr("onclick", "removeInnFromStock("+innId+",'"+mobile+"')");
	showAlertDialog('remove_inn_div');
}

function showEditDiv(event){
	var $btn = $(event.currentTarget);
	var $tr = $btn.parent().parent();
	var innId = $btn.attr("innId");
	var name = $tr.find("td[tag=name]").html();
	var pricePolicy = $tr.find("td[tag=pricePolicy]").attr("type");
	var totalCommissionRatio = $tr.find("td[tag=commission]").html();
	setInnInfoOnPage(innId, name, pricePolicy, totalCommissionRatio);
}

function removeInnFromStock(innId, mobile){
	var url = "/apisale/channel/removeStock"+generateUrlEndStr();
	var data = {
			"id": innId,
			"mobile": mobile
	};
	$.post(ctx+"/apisale/channel/removeStock", data).done(function(json) {
		if(json.status == 200){
			$("tr[innId="+innId+"]").remove();
			closeAlertDialog('remove_inn_div');
		}
	});
}

function searchInn(mobile){
	var url = ctx+"/apisale/channel/searchInn"+generateUrlEndStr();
	var input = $("#search_inn_input").val();
	if(isEmpty(input)){
		return;
	}
	var data = {
			"mobile": input
	};
	$.post(url, data).done(function(json) {
		if(json.status == 200){
			$("#search_inn_div").hide();
			$("#search_inn_input").val("");
			setInnInfo(json.result, ENUM_OP_TYPE.ADD);
		}else{
			showErrors(json.result, 'search_inn_erorr');
		}
	});
}

function addInn2Stock(){
	var url = ctx+"/apisale/channel/saveMarketInn"+generateUrlEndStr();
	var innId = $("#add_inn_id").val();
	var pricePolicy = "-1";
	if(document.getElementById("price_buttom").checked){
		pricePolicy = "1";
	}
	if(document.getElementById("price_sale").checked && pricePolicy == "-1"){
		pricePolicy = "2";
	}else if(document.getElementById("price_sale").checked && pricePolicy == "1"){
		pricePolicy = "3"
	}
	if(pricePolicy == "-1"){
		alert("请选择至少一种价格策略！");
		return;
	}
	var totalCommissionRatio = $("#commission_input").val();
	if(pricePolicy != '1' && isEmpty(totalCommissionRatio)){
		alert("请设置分佣比例!");
		return;
	}
	var data = {
			"id": innId,
			"inMarket": g_join_market,
			"pricePolicy": pricePolicy,
			"totalCommissionRatio": totalCommissionRatio
	};
	$.post(url, data).done(function(json) {
		if(json.status == 200){
			closeAlertDialog('add_inn_div');
			if($("#op_type").html == ENUM_OP_TYPE.ADD){
				$("#pageNo").val("1");
			}
			refresh();
		}else{
			alert(json.result);
		}
	});
}

function setInnInfoOnPage(id, name, pricePolicy, totalCommissionRatio){
	var data = {
		"id": id,
		"name":	name,
		"pricePolicy":	pricePolicy,
		"totalCommissionRatio": totalCommissionRatio
	};
	setInnInfo(data, ENUM_OP_TYPE.EDIT);
}

function setInnInfo(inn, opType){
	$("#op_type").html(opType);
	if(opType == ENUM_OP_TYPE.ADD){
		$("#op_word").html("到代销平台？");
	}else{
		$("#op_word").html("在代销平台的策略？");
	}
	$("#add_inn_name").html(inn.name);
	$("#add_inn_id").val(inn.id);
	if(inn.pricePolicy == '1'){
		$("#price_buttom").attr("checked", "checked");
		document.getElementById("price_buttom").checked = true;
	}else if(inn.pricePolicy == '2'){
		$("#price_sale").attr("checked", "checked");
		document.getElementById("price_sale").checked = true;
	}else if(inn.pricePolicy == '3'){
		$("#price_buttom").attr("checked", "checked");
		$("#price_sale").attr("checked", "checked");
		document.getElementById("price_buttom").checked = true;
		document.getElementById("price_sale").checked = true;
	}
	isShowCommission(inn.totalCommissionRatio);
	showAlertDialog('add_inn_div');
}

function isShowCommission(num){
	if(document.getElementById("price_sale").checked){
		$("#commission_div").show();
		$("#commission_input").val(num);
	}else{
		$("#commission_div").hide();
		$("#commission_input").val("");
	}
}


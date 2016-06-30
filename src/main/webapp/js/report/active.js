$(document).ready(function(e){
	$("#areas li").bind("click",function(e) {
		searchWithArea(this.value,$(this).text());
	});
	
	$("div.content2 li[tag=sortDate]").bind("click", function(event){
		var $date = $(event.currentTarget);
		sortByDate($date);
	});
	
	showView();
	
//	#("#active_report_export_btn").bind("click", function(e){
//		//报表导出
//		activeExport();
//	});
});

function search() {
	$("#order").val("");
	$("#orderBy").val("");
	$("#pageNo").val("1");
	$("#mainForm").submit();
}

function searchWithFromDate(){
	var $date = $("#activeDate");
	checkDate($date);
	search();
}

function searchWithToDate(){
	var $date = $("#toDate_select");
	checkDate($date);
	search();
}

function searchWithArea(areaId,areaText){
	$("#areaId").val(areaId);
	$("#areaName").val(areaText);
	search();
}

function searchWithFlag(flag){
	$("#activeFlag").val(flag);
	search();
}

function searchWithType(type){
	$("#activeType").val(type);
	search();
}

function activeExport(){
	var url = ctx+"/report/active/export"+generateUrlEndStr();
	var data = $("#mainForm").serialize();
	url += data +"&temp=" + new Date().getTime();
	window.location.href = url;
}

function showView(){
	var activeType = $("#activeType").val();
	switch(activeType){
	case "0":
		$("#active_report_export_btn").hide();
		break;
	case "2":
	case "3":
		$("#active_report_export_btn").show();
		break;
	}
}

function checkDate($date){
	var id = $date.attr("id");
	var $other = $("div.date-choose span[id!="+id+"]");
	var from = new Date($("#activeDate").text());
	var to = new Date($("#toDate_select").text());
	var days = Date.diffDay($("#activeDate").text(), $("#toDate_select").text()) + 1;
	if(days > 31){
		if(id == "activeDate"){
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
		if(id == "activeDate"){
			$other.html(from.format("yyyy-MM-dd"))
		}else{
			$other.html(to.format("yyyy-MM-dd"))
		}
	}
	$("#selectDate").val($("#activeDate").text());
	$("#toDate").val($("#toDate_select").text());
	$("#sortDate").val("");
}

function sortByDate($date){
	if($date.hasClass("active")){
		return;
	}
	$("#sortDate").val($date.attr("date"));
	search();
}

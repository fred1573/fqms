$(document).ready(function(e){
	
	$("#search_submit").bind("click", function(){
		$("#input").val($("#search_input").val());
		jumpPage(1);
	});
	
});


function searchWithFromDate(){
	$("#fromDate").val($("#from").text());
	search();
}

function searchWithToDate(){
	$("#toDate").val($("#to").text());
	checkDate($("#from"), $("#to"));
	search();
}

function search() {
	$("#mainForm").submit();
}

function jumpPage(pageNo){
	var date = ($("#funcReport-date").text() != '')?$("#funcReport-date").text().trim():"";
	$("#selectDate").val(date);
	$("#pageNo").val(pageNo);
	search();
}

function checkDate($from, $to){
	var id = $from.attr("id");
	var $other = $("div.date-choose span[id!="+id+"]");
	var from = new Date($from.text());
	var to = new Date($to.text());
	var days = Date.diffDay($from.text(), $to.text()) + 1;
	if(days > 31){
		if(id == "from"){
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
		if(id == "from"){
			$other.html(from.format("yyyy-MM-dd"))
		}else{
			$other.html(to.format("yyyy-MM-dd"))
		}
	}
	$("#fromDate").val($("#from").text());
	$("#toDate").val($("#to").text());
}

function importData(from, to){
	var num = Date.diffDay(from, to) + 1;
	var url = ctx+"/report/active/import";
	var data = {
			"day":from,
			"to":to
	}
	if(num > 0){
		$.post(url, data).done(function(json) {
			if(json.status == 200){
				console.log("导入"+from+"的数据成功!");
				importData(json.from, json.to);
			}
		});
	}
}
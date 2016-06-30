/**
 * X
 */
var money = new Array()
$(document).ready(function(e){
	$("div.header button").bind("click", function(event){
		var $btn = $(event.currentTarget);
		url = $btn.attr("urls");
		window.location = url;
	});
	initMergeRow('tag_inn_name');
	
	$("#main_table td.tag_do[status=0] a").bind("click", function(e){
		$("#msgbox").show();
		var $td = $(this).parent();
		var innId = $td.attr("innId");
		var status = $td.attr("status");
		$("#changeBtn").attr('onclick', 'balancedOrders('+innId+', '+status+')');
	});
	
	$("#payBalance,#payMode").bind("change", function(){
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
        $("input[name='payMode']").val($("#payMode").val());
        $("input[name='isBalance']").val($("#payBalance").val());
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

function cleanInnIdAndSearch(){
	search();
}

function initMergeRow(clazz){
	var $tds = $("#main_table td."+clazz+"");
	var innIds = new Array();
	var j = 0;
	$($tds).each(function(i, td){
		var innId = $(td).attr("innId");
		if(i == 0){
			innIds[j] = innId;
		}else if(innIds[j] != innId){
			j++;
			innIds[j] = innId;
		}
	});
	$(innIds).each(function(i, id){
		mergeRow('tag_inn_name', id);
		mergeRow('tag_account', id);
		mergeCountRow('tag_total', id, '0');
		mergeCountRow('tag_total', id, '1');
		mergeCountRow('tag_do', id, '1');
		mergeCountRow('tag_do', id, '0');
		mergeCount('tag_accountfee', id, 1);
		mergeCount('tag_accountfee', id, 0);
		showCountNum('tag_total', id, 1);
		showCountNum('tag_total', id, 0);
	});
}

function mergeRow(clazz, innId){
	var $tds = $("#main_table td."+clazz+"[innId="+innId+"]");
	var max = $tds.length;
	$($tds).each(function(i, td){
		if(i == 0){
			$(td).attr("rowspan", max);
		}else{
			$(td).remove();
		}
	});
}

function mergeCountRow(preClazz, innId, status){
	var $tds = $("#main_table td."+preClazz+"[innId="+innId+"][status="+status+"]");
	var max = $tds.length;
	$($tds).each(function(i, td){
		if(i == 0){
			$(td).attr("rowspan", max);
		}else{
			$(td).remove();
		}
	});
}

function mergeCount(preClazz, innId, status){
	var $tds = $("#main_table td."+preClazz+"[innId="+innId+"][status="+status+"]");
	var max = $tds.length;
	$($tds).each(function(i, td){
		var innId = $(td).attr("innId");
		var show = (money[innId] != undefined)?money[innId]:new Array();
		show[status] = (show[status] == undefined)?0.0:show[status];
		show[status] += parseFloat($(td).text());
		money[innId] = show;
	});
}

function showCountNum(preClazz, innId, status){
	var $tds = $("#main_table td."+preClazz+"[innId="+innId+"][status="+status+"]");
	$($tds).each(function(i, td){
		$(td).text(money[innId][status].toFixed(2));
	});
}

function getParams(innId, status){
	var arrayObj = [];
	arrayObj.push(getParam("payIds", getPayIds(innId, status)));
	return arrayObj;
}

function getPayIds(innId, status){
	var $tds = $("#main_table td.tag_payWay[innId="+innId+"][status="+status+"]");
	var ids = '';
	$($tds).each(function(i, td){
		ids += $(td).attr("payId");
		if(i < $tds.length - 1){
			ids += ','
		}
	});
	return ids;
}

function balancedOrders(innId, status){
	var data = getParams(innId, status);
	jQuery.ajax({
		url: ctx+"/bill/balanceOrders?tmp="+new Date(),
		data: data,
		type: 'post',
		dataType: 'json',
		success: function(data) {
			if(data){
				search();
			}
		},error:function(){
			alert("保存出错!");
		}
	});
}


var idTmr;
function  getExplorer() {
	var explorer = window.navigator.userAgent ;
	//ie 
	if (explorer.indexOf("MSIE") >= 0) {
		return 'ie';
	}
	//firefox 
	else if (explorer.indexOf("Firefox") >= 0) {
		return 'Firefox';
	}
	//Chrome
	else if(explorer.indexOf("Chrome") >= 0){
		return 'Chrome';
	}
	//Opera
	else if(explorer.indexOf("Opera") >= 0){
		return 'Opera';
	}
	//Safari
	else if(explorer.indexOf("Safari") >= 0){
		return 'Safari';
	}
}

function exportToExcel(tableid) {
	if(getExplorer()=='ie'){
		var curTbl = document.getElementById(tableid);
		try {
			var oXL = new ActiveXObject("Excel.Application");
			
			var oWB = oXL.Workbooks.Add();
			var xlsheet = oWB.Worksheets(1);
			var sel = document.body.createTextRange();
			sel.moveToElementText(curTbl);
			sel.select();
			sel.execCommand("Copy");
			xlsheet.Paste();
			oXL.Visible = true;

			var fname = oXL.Application.GetSaveAsFilename("Excel.xls", "Excel Spreadsheets (*.xls), *.xls");
		} catch (e) {
			alert("无法启动Excel!\n\n如果您确信您的电脑中已经安装了Excel，"+"那么请调整IE的安全级别。\n\n具体操作：\n\n"+"工具 → Internet选项 → 安全 → 自定义级别 → 对没有标记为安全的ActiveX进行初始化和脚本运行 → 启用");
			print("Nested catch caught " + e);
		} finally {
			oWB.SaveAs(fname);

			oWB.Close(savechanges = false);
			//xls.visible = false;
			oXL.Quit();
			oXL = null;
			idTmr = window.setInterval("Cleanup();", 1);

		}
		
	} else{
		tableToExcel(tableid)
	}
}
function Cleanup() {
    window.clearInterval(idTmr);
    CollectGarbage();
}
var tableToExcel = (function() {
	  var uri = 'data:application/vnd.ms-excel;base64,',
	  template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>',
		base64 = function(s) { return window.btoa(unescape(encodeURIComponent(s))) },
		format = function(s, c) {
			return s.replace(/{(\w+)}/g,
			function(m, p) { return c[p]; }) }
		return function(table, name) {
		if (!table.nodeType) table = document.getElementById(table)
		var ctx = {worksheet: name || 'Worksheet', table: table.innerHTML}
		window.location.href = uri + base64(format(template, ctx))
	  }
})()

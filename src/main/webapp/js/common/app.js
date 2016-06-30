var _isMSIE = /msie/.test(navigator.userAgent.toLowerCase());

$(function() {
});

var getParam = function(paramName, value) {
	var param = {};
	param.name = paramName;
	param.value = value;
	return param;
};

// 生成url请求结尾字符串（?temp=xxxx，防止浏览器缓存不请求）
function generateUrlEndStr() {
	return "?temp=" + new Date().getTime();
}

function checkAll(checkboxName,obj){
	$("input[name="+checkboxName+"]").prop("checked",$(obj).prop("checked")); 
}

function setAllNoChecked(checkboxName){
	$.each($("input[name="+checkboxName+"]"), function(i,obj){
		$(obj).prop("checked",false);
	});
}

function getCheckedValue(checkboxName){
	var checkedValue = "";
	$.each($("input[name="+checkboxName+"]:checked"), function(i,obj){
		checkedValue += $(obj).val()+",";
	});
	if(checkedValue!=""){
		checkedValue = checkedValue.substr(0,checkedValue.length-1);
	}
	return checkedValue;
}

function showFlashBox(message, url, time) {
	if (time == "" || time == undefined) {
		time = 1000;
	}
	$.blockUI({
		css : {
			border : 'none',
			padding : '15px',
			backgroundColor : '#000',
			'-webkit-border-radius' : '10px',
			'-moz-border-radius' : '10px',
			opacity : .5,
			color : '#fff'
		},
		message : message
	});
	setTimeout(function() {
		$.unblockUI;
		if (url != "" && url != undefined)
			window.location.href = url;
		else
			window.location.reload();
	}, time);
}

function showFlashBoxNoJump(message, time) {
	if (time == "" || time == undefined) {
		time = 1000;
	}
	$.blockUI({
		css : {
			border : 'none',
			padding : '15px',
			backgroundColor : '#000',
			'-webkit-border-radius' : '10px',
			'-moz-border-radius' : '10px',
			opacity : .5,
			color : '#fff'
		},
		message : message
	});
	setTimeout(function() {
		$(".blockUI").remove();
		hideCoverBox();
	}, time);
}

function clearValidationInfo() {
	$("#modifyPwdForm").validationEngine('hide');
	$("#inputForm").validationEngine('hide');
}

function closeByDivId(divId){
	clearValidationInfo();
	hideCoverBox();
	$("#"+divId).fadeOut();
}

function closeByClass(_class){
	clearValidationInfo();
	hideCoverBox();
	$("."+_class).fadeOut();
}

/**
 * 显示某个弹出层,并且打开黑色遮罩
 * 
 * @param divId
 */
function showAlertDialog(divId) {
	var $Enty = $("#" + divId);
	showCoverBox();
	$("#fullbg").attr("ondblclick", "closeAlertDialog('"+divId+"')");
	$Enty.center();
	$Enty.fadeIn();
}

function closeAlertDialog(divId) {
	var $Enty = $("#" + divId);
	hideCoverBox();
	$("#fullbg").removeAttr("ondblclick");
	$Enty.fadeOut();
	clearInput($Enty);
}

function clearInput($div) {
	$div.find("input").val("");
	$div.find("input").removeAttr("checked");
	$div.find("textarea").val("");
}

// 白色loading遮罩
function showWholeLoading() {
	$("#whole_mask").height($('body').height()).show();
	$("#loading_icon").show();
}

function hideWholeLoading() {
	$("#whole_mask").hide();
	$("#loading_icon").hide();
}

function showCoverBox() {
	var bh = $(document).height();
	var bw = $(document).width();
	$("#fullbg").css({
		height : bh,
		width : bw
	});
	$("#fullbg").fadeIn();
}
// 添加取消遮罩方法 2013-07-11 candy
function hideCoverBox() {
	$("#fullbg").fadeOut();
}

function showErrors(errors, divId) {
	$("#" + divId).slideDown().text(errors).delay(SHOW_ERROR_INTERVAL_TIME).slideUp();
}

function showMessageByClass(json, objId) {
	var tip = $("#" + objId);
	if (tip != null) {
		if (typeof json === "string") {
			tip.html(json);
		} else if (json != null) {
			var message = "";
			$.each(json.message, function(index) {
				message += json.message[index] + " | ";
			});
			tip.html(message);
		}
		tip.slideDown().delay(SHOW_ERROR_INTERVAL_TIME).slideUp();
	}
}

function showErrorTip(meg, dialog) {
	var tip = $("#"+dialog).find("em[class='error-tips']").first();
	if (tip != null && typeof meg === "string") {
		$(tip).html(meg);
	} else if (tip != null && meg != null) {
		var message = "";
		$.each(meg.message, function(index) {
			message += meg.message[index] + "<br/>";
		});
		$(tip).html(message);
	}
	$(tip).slideDown().delay(SHOW_ERROR_INTERVAL_TIME).slideUp();
}

/**
 * 获取兼容IE6,7,8日期对象操作的日期字符串
 * 
 * @param cdate
 *            yyyy-MM-dd格式的日期字符串
 * @returns {MM/dd/yyyy格式的字符串}
 */
function getDateStrForIE(cdate) {
	if (cdate == null)
		return "00/00/1970";
	var dash1 = cdate.indexOf("-");
	var dash2 = cdate.lastIndexOf("-");
	var year = cdate.substring(0, dash1);
	var month = cdate.substring(dash1 + 1, dash2);
	var day = cdate.substring(dash2 + 1);
	return month + "/" + day + "/" + year;
}

$.fn.center = function(parent) {
	if (parent) {
		parent = this.parent();
	} else {
		parent = window;
	}
	this.css({
		"position" : "absolute",
		"top" : ((($(parent).height() - this.outerHeight()) / 2) + $(parent).scrollTop() + "px"),
		"left" : ((($(parent).width() - this.outerWidth()) / 2) + $(parent).scrollLeft() + "px"),
		"z-index" : 10000
	});
	return this;
}

jQuery.fn.outerHTML = function(s) {
	return s ? this.before(s).remove() : jQuery("<p>").append(this.eq(0).clone()).html();
};

//扩展jquery browser
(function ($, undefined) {
	var uaMatch = function( ua ) {
		ua = ua.toLowerCase();
		var match = /(chrome)[ \/]([\w.]+)/.exec( ua ) ||
			/(webkit)[ \/]([\w.]+)/.exec( ua ) ||
			/(opera)(?:.*version|)[ \/]([\w.]+)/.exec( ua ) ||
			/(msie) ([\w.]+)/.exec( ua ) ||
			ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec( ua ) ||
			[];
		return {
			browser: match[ 1 ] || "",
			version: match[ 2 ] || "0"
		};
	};
	// Don't clobber any existing jQuery.browser in case it's different
	if ( !jQuery.browser ) {
		matched = uaMatch( navigator.userAgent );
		browser = {};
		if ( matched.browser ) {
			browser[ matched.browser ] = true;
			browser.version = matched.version;
		}
		// Chrome is Webkit, but Webkit is also Safari.
		if ( browser.chrome ) {
			browser.webkit = true;
		} else if ( browser.webkit ) {
			browser.safari = true;
		}
		jQuery.browser = browser;
	}
})(jQuery);


/** 默认日期格式化格式 */
var DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
var DATE_FORMAT_HOUR_MINUTE = "yyyy-MM-dd hh:mm";
var DATE_FORMAT_HOUR_MINUTE_SECOND = "yyyy-MM-dd hh:mm:ss";

// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
Date.prototype.format = function(fmt) { // author: meizz
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"h+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}

Date.prototype.addDays = function(d) {
	this.setDate(this.getDate() + d);
}

Date.prototype.addWeeks = function(w) {
	this.addDays(w * 7);
}

Date.prototype.addMonths = function(m) {
	var d = this.getDate();
	this.setMonth(this.getMonth() + m);
	if (this.getDate() < d)
		this.setDate(0);
}

Date.prototype.addYears = function(y) {
	var m = this.getMonth();
	this.setFullYear(this.getFullYear() + y);
	if (m < this.getMonth()) {
		this.setDate(0);
	}
}

// 计算两个日期相差几天 格式：yyyy-MM-dd
Date.diffDay = function(date1, date2) {
	date1 = new Date(date1.replace(/-/g, "/"));
	date2 = new Date(date2.replace(/-/g, "/"));
	var date3 = date2.getTime() - date1.getTime(); // 时间差的毫秒数
	var days = Math.floor(date3 / (24 * 60 * 60 * 1000));
	return days;
}

Array.prototype.getIndexByUniqueValue = function(value) {
	var index = -1;
	for ( var i = 0; i < this.length; i++) {
		if (this[i] == value) {
			index = i;
			break;
		}
	}
	return index;
}

// 将字符串拆成字符，并存到数组中
String.prototype.strToChars = function() {
	var chars = new Array();
	for ( var i = 0; i < this.length; i++) {
		chars[i] = [ this.substr(i, 1), this.isCHS(i) ];
	}
	String.prototype.charsArray = chars;
	return chars;
}

// 判断某个字符是否是汉字
String.prototype.isCHS = function(i) {
	if (this.charCodeAt(i) > 255 || this.charCodeAt(i) < 0)
		return true;
	else
		return false;
}

// 截取字符串（从start字节到end字节）
String.prototype.subCHString = function(start, end) {
	var len = 0;
	var str = "";
	this.strToChars();
	for ( var i = 0; i < this.length; i++) {
		if (this.charsArray[i][1])
			len += 2;
		else
			len++;
		if (end < len)
			return str;
		else if (start < len)
			str += this.charsArray[i][0];
	}
	return str;
}

String.prototype.getBytes = function() {
	var cArr = this.match(/[^\x00-\xff]/ig);
	return this.length + (cArr == null ? 0 : cArr.length);
}

function lenc(s) {
	return s.replace(/[^\x00-\xff]/g, "xx").length;
}

function setMaxLength(divId, max) {
	var content = $(divId).val()
	var allNum = content.getBytes();
	if (max < allNum)
		return false;
	else
		return true;
}

function setMaxLengths(divId, max) {
	var content = $("#" + divId).val()
	var allNum = content.getBytes();
	if (max < allNum)
		return false;
	else
		return true;
}

function cut_str(str, len) {
	var char_length = 0;
	var sub_len = 0;
	for ( var i = 0; i < len; i++) {
		var son_str = str.charAt(i);
		lenc(son_str) == 2 ? char_length += 2 : char_length += 1;
		if (char_length <= len) {
			sub_len += 1;
		}
	}
	return str.substr(0, sub_len);
}

function getNextDaysStr(today, days) {
	var tomorrowDate = new Date(today.replace(/-/g, "/"));
	tomorrowDate.addDays(days * 1);
	return tomorrowDate.format("yyyy-MM-dd");
}

/**
 * 判空
 * 
 * @param obj
 * @returns
 */
function isEmpty(obj) {
	return isEmpty(obj, true);
}

function isEmpty(obj, isTrim) {
	if (null == obj || undefined == obj) {
		return true;
	}
	var type = typeof obj;
	if (type == 'string') {
		return isTrim ? obj.trim().length == 0 : obj.length == 0;
	} else if (type == 'array') {
		return obj.length == 0;
	} else if (type == 'object') {
		return $.isEmptyObject(obj);
	}
}

function isNotEmpty(obj) {
	return isNotEmpty(obj, true);
}

function isNotEmpty(obj, isTrim) {
	return !isEmpty(obj, isTrim);
}

/**
 * 扩展字符串去空格方法[去除两边空格]
 */
String.prototype.trim = function() {
	return this.replace(/^\s*|\s*/g, "");
};

/**
 * 去除左边空格
 */
String.prototype.ltrim = function() {
	return this.replace(/^\s*/g, "");
};

/**
 * 去除右边空格
 */
String.prototype.rtrim = function() {
	return this.replace(/\s*$/g, "");
};

/**
 * 转换json字符串为单个json对象
 * 
 * @param str
 * @returns
 */
function convertToJson(str) {
	if (isEmpty(str)) {
		return null;
	}
	var json = null;
	try {
		json = $.parseJSON(str);
	} catch (exception) {
		return null;
	}
	return json;
}

/**
 * 转换字符串为json数组
 * 
 * @param strs
 * @returns
 */
function convertToJsonArr(strs) {
	var arr = [];
	if (isEmpty(strs)) {
		return arr;
	}
	try {
		var entrys = strs.replace(/^\[|\]$/g, "");
		if (entrys.trim().length == 0) {
			return arr;
		}
		entrys = entrys.split("},");
		var len = entrys.length;
		var entry = null;
		for ( var i = 0; i < len; i++) {
			entry = entrys[i];
			if (i != len - 1) {
				entry += "}";
			}
			arr.push($.parseJSON(entry));
		}
	} catch (exception) {
		return arr;
	}
	return arr;
}

/**
 * 判断是否为int型
 * 
 * @param str
 * @returns
 */
function isInt(str) {
	return /^(-|\+)?\d+$/.test(str);
}

/**
 * 判断是否为正数
 * 
 * @param str
 * @returns
 */
function isBigZero(str) {
	return /^\d+$/.test(str);
}

function isFloatOne(str) {
	return /^-?\d+\.?\d{0,1}$/.test(str);
}

/**
 * 判断是否为负数
 * 
 * @param str
 * @returns
 */
function isLessZero(str) {
	return /^-\d+$/.test(str);
}

/**
 * 判断是否为float型数值
 * 
 * @param str
 * @returns
 */
function isFloat(str) {
	return /^(-?\d+)(\.\d+)?$/.test(str);
}
/**
 * 判断是否为非负数
 * 
 * @param str
 * @returns
 */
function isNoLessZero(str) {
	return /^(([1-9]+)|([0-9]+\.[0-9]{1}))$/.test(str);
}

function isEmail(str) {
	var pattern = /^[a-zA-Z0-9_\-]{1,}@[a-zA-Z0-9_\-]{1,}\.[a-zA-Z0-9_\-.]{1,}$/;
	return pattern.test(str);
}

function isPhone(str) {
	var reg = /(^[0-9]{3,4}\-[0-9]{3,8}$)|(^[0-9]{3,8}$)|(^\([0-9]{3,4}\)[0-9]{3,8}$)|(^0{0,1}13[0-9]{9}$)/;
	return reg.test(str);
}

function isMobile(str) {
	var reg = /^(13|14|15|18)[0-9]{9}$/;
	return reg.test(str);
}

/**
 * 是否为中文
 * 
 * @param str
 * @returns
 */
function isChinese(str) {
	var pattern = /[^\x00-\xff]/g;
	return pattern.test(str);
}

function isQQ(str) {
	return /^\d{5,9}$/.test(str);
}

function isValidDate(date) {
	return !(isEmpty(date) || isNaN(date.getFullYear()) || isNaN(date.getMonth()) || isNaN(date.getDate()));
}

/* js 模拟实现placeholder start */
function checkValueOnKeyUp(ele, placeholderstr, toCheckNum) {
	var val = ele.value;
	val = val == placeholderstr ? "" : val;
	if (toCheckNum) {
		if (isChinese(val)) {
			ele.value = "";
		} else {
			if (isNaN(val)) {
				ele.value = val.replace(/\D*/, "");
			}
		}
	}
}

function checkValueOnFocus(ele, placeholderstr) {
	if (ele.value == placeholderstr) {
		ele.value = "";
	}
}

function checkValueOnBlur(ele, placeholderstr) {
	if (ele.value == "") {
		ele.value = placeholderstr;
	}
}
/* js 模拟实现placeholder end */

/**
 * 检查并显示服务端返回的错误信息
 * 
 * @param data
 * @returns {Boolean}
 */
function checkAndShowServerError(data) {
	if (typeof data == "object") {
		if (data.status && data.status == 500) {
			alert(data.message);
			return false;
		}
	}
	return true;
}

/**
 * 检查并显示服务端返回的forbidden信息
 * 
 * @param data
 */
function showFailMessage(data) {
	if (isNotEmpty(data.status)) {
		var status = data.status;
		switch (status) {
		case 401:
			window.location.href = "/forbiden";
			break;
		case 404:
			window.location.href = "/notFound";
			break;
		case 500:
			window.location.href = "/serverError";
			break;
		}
	} else {
		var result = data.responseText;
		if (isEmpty(result)) {
			return;
		}
		var jsonResult = convertToJson(result);
		if (null == jsonResult || isEmpty(jsonResult.message)) {
			return;
		}
		alert(jsonResult.message);
	}
}

/**
 * 按最多5位且最多一位小数的规则校验价格数值
 * 
 * @param val
 * @returns 符合规则返回true，否则返回false
 */
function checkPrice(val) {
	var gz1 = /^\d{1,5}$/;
	var gz2 = /^\d{1,4}(\.\d{1})?$/;
	if (!gz1.test(val)) {
		if (!gz2.test(val)) {
			return false;
		}
	}
	return true;
}

/**
 * 禁用指定的输入框，并添加相应样式
 * 
 * @param inputs
 *            输入框[数组]
 * @param updclass
 *            要更新的样式，此样式格式为：{"addClass" : "要添加的样式名", "removeClass" : "要删除的样式名"}
 * @param isDisabled
 *            是否为禁用操作
 */
function disabledOrEnableInput(inputs, updclass, isDisabled) {
	for ( var i = 0; i < inputs.length; i++) {
		disabledOrEnableSingleInput($(inputs[i]), updclass, isDisabled);
	}
}

/**
 * 禁用指定的输入框，并添加相应样式
 * 
 * @param input
 *            输入框
 * @param updclass
 *            要更新的样式，此样式格式为：{"addClass" : "要添加的样式名", "removeClass" : "要删除的样式名"}<br/>
 *            不需要添加或删除样式时其key值设置为"",都不需要时参数updclass传null
 * @param isDisabled
 *            是否为禁用操作
 */
function disabledOrEnableSingleInput(input, updclass, isDisabled) {
	var $curr = $(input);
	if (null != updclass) {
		if ("" != updclass.removeClass && $curr.hasClass(updclass.removeClass)) {
			$curr.removeClass(updclass.removeClass);
		}
		if ("" != updclass.addClass) {
			$curr.addClass(updclass.addClass);
		}
	}

	if (isDisabled) {
		$curr.attr("disabled", "disabled").attr("readonly", "readonly");
	} else {
		$curr.removeAttr("disabled").removeAttr("readonly");
	}
}

/**
 * 清除checkbox选择项
 * 
 * @param parent
 */
function cleanCheckboxSelect(parent) {
	$.each(parent.find("input[type='checkbox']"), function(i, item) {
		item.chekced = false;
	});
}

/**
 * 获得checkbox选择项，且把选择项的值以指定的连接符连接成字符串。默认以“，”连接
 * 
 * @param parent
 * @param join
 * @returns
 */
function getCheckboxSelect(parent, join) {
	var selects = "";
	join = isEmpty(join) ? "," : join;
	$.each(parent.find("input[type='checkbox']"), function(i, item) {
		if (item.checked) {
			selects += item.value.concat(join);
		}
	});
	return "" != selects ? selects.substring(0, selects.length - 1) : "";
}

function isIE() {
	return navigator.userAgent.toLowerCase().indexOf("msie") != -1;
}

function isFirefox() {
	return navigator.userAgent.toLowerCase().indexOf("firefox") != -1;
}

function isOpera() {
	return navigator.userAgent.toLowerCase().indexOf("opr") != -1;
}

function isSafari() {
	var agentStr=navigator.userAgent.toLowerCase();
	return agentStr.indexOf("chrome") == -1 && agentStr.indexOf("safari") != -1 && agentStr.indexOf("version") != -1;
}

function isChrome(){
	var agentStr=navigator.userAgent.toLowerCase();
	return agentStr.indexOf("chrome") != -1 && agentStr.indexOf("version") == -1 && agentStr.indexOf("opr") == -1;
}

/**
 * 是否在指定区间内(闭区间)
 * 
 * @param sDate
 *            指定时间
 * @param sBegin
 *            开始时间
 * @param sEnd
 *            结束时间
 * @returns
 */
function isBetween(sDate, sBegin, sEnd) {
	var bResult = true;
	if (Date.diffDay(sBegin, sDate) < 0 || Date.diffDay(sEnd, sDate) > 0) {
		bResult = false;
	}
	return bResult;
}

/**
 * 是否不在指定区间内(闭区间)
 * 
 * @param sDate
 *            指定时间
 * @param sBegin
 *            开始时间
 * @param sEnd
 *            结束时间
 * @returns
 */
function isNotBetween(sDate, sBegin, sEnd) {
	return !isBetween(sDate, sBegin, sEnd);
}

/**
 * 初始化弹出框
 * @param {Object} art.dialog.defaults
 */
(function (config) {
    config['lock'] = true;
    config['fixed'] = true;
    config['okVal'] = '确认';
    config['cancelVal'] = '取消';
    // [more..]
})(art.dialog.defaults);

/**
 * 弹出框调用方法
 * 
 * @param title
 *            弹出框标题
 * @param content
 *            弹出框内容
 * @param showTime
 *            弹出框展示时间（秒为单位）
 */
function alertArtDialog(title, content, showTime) {
	var timer;
	var dialog = art.dialog({
		title : title,
		init : function() {
			var that = this, i = showTime;
			var fn = function() {
				that.title(title + "&nbsp;&nbsp;&nbsp;&nbsp;" + i + '秒后关闭');
				!i && that.close();
				i--;
			};
			timer = setInterval(fn, 1000);
			fn();
		},
		content : content,
		ok : function() {
			this.close();
		},
		close : function() {
			clearInterval(timer);
		}
	});
}

/**
 * 通知类弹出框
 * 
 * @param title
 * @param content
 * @param options
 *            配置项，目前配置了三项{"width":宽,"height":高,"showCloseIcon":是否显示关闭图标按钮}
 * @author hai
 * @date 2014年1月9日下午6:10:00
 */
function alertAdviceArtDialog(title, content, sendDate, options) {
	var width = 460;
	var height = 240;
	var showCloseIcon = false;
	if (isNotEmpty(options)) {
		if (options.width && options.width > 0) {
			width = options.width;
		}
		if (options.height && options.height > 0) {
			height = options.height;
		}
		if (isNotEmpty(options.showCloseIcon)) {
			showCloseIcon = options.showCloseIcon;
		}
	}
	var scrollWidth = 15;
	var left = document.body.clientWidth - width - scrollWidth;
	sendDate=(new Date(sendDate)).format(DATE_FORMAT_HOUR_MINUTE_SECOND);
	var sendDateHtmls="<div align='right' style='padding-top:10px;'>" + sendDate + "</div>";
	content+=sendDateHtmls;
	art.dialog({
		width : width,
		title : title,
		height : height,
		content : content,
		top : '99%',
		drag : false,
		resize : false,
		init : function() {
			var aui_state_focus = $(".aui_state_focus");
			aui_state_focus.width(width).css("left", left + "px");
			var aui_close = aui_state_focus.find(".aui_close");
			if (!showCloseIcon) {
				aui_close.hide();
			} else {
				aui_close.css("left", width - 45);
			}
			aui_state_focus.find(".aui_content").css({
				"width" : (width - 30) + "px",
				"height" : (height - 80) + "px",
				"padding" : "20px 10px 0 10px",
				"margin" : "0",
				"word-wrap" : "break-word",
				"word-break" : "break-all",
				"overflow" : "auto"
			});
		},
		ok : function() {
			this.close();
		}
	});
}

/**
 * 限制Textarea的最大内容
 * 
 * @param ele
 * @author hai
 * @date 2014年1月15日下午6:14:33
 */
function restrictMaxLength(ele, maxlength) {
	var $this = $(ele);
	var msg = $this.val();
	if (msg.trim().length > maxlength) {
		msg = msg.substring(0, maxlength);
		$this.val(msg);
	}
}

/**
 * 过滤html标签（ < >）
 * 
 * @return {[type]} [description]
 */
function filterHtml(str) {
	if (isEmpty(str)) {
		return "";
	};
	var reg  = /[<>]/g;
    str = str.replace(reg, "");
    return str;
}
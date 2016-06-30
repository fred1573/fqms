$(function () {
	
    var proxyId = $("#proxyInnId").val();
    var baseAccId = $("#" + proxyId + "-baseAcc").val();
    var saleAccId = $("#" + proxyId + "-saleAcc").val();

    $('.ota-span').find("input").css("text-decoration","underline");
    $('.ota-span').find("input").css("color","blue");
    
    headTime(0);
    headTime(1);
    tableData('/proxysale/inn/' + baseAccId + '/inninfo', 0);
    tableData('/proxysale/inn/' + saleAccId + '/inninfo', 1);

    timePage('.floorPrice', 4);
    timePage('.sellPrice', 4);

    $('.datetime').val(getFormatDate());

    function get30date(date) {
        var arrDate = [];
        var firstDate = TC.date(date) - 0;
        var today = new Date;
        var todayY = today.getFullYear();
        var todayM = today.getMonth() + 1;
        var todayD = today.getDate();
        for (var i = 0; i < 15; i++) {
            var date = new Date(firstDate + i * 86400000);
            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var day = date.getDate();
            var weekDay = '日一二三四五六'[date.getDay()];
            month = month < 10 ? '0' + month : month;
            day = day < 10 ? '0' + day : day;
            var isHoliday = TC.getHoliday(year + '-' + month + '-' + day);
            if (year == todayY && month == todayM && day == todayD) {
                isHoliday = '今天';
            }
            arrDate.push({date: isHoliday || (month + '-' + day), isHoliday: isHoliday ? 1 : 0, weekDay: weekDay})
        }
        return arrDate;
    }

    //url ajaxUrl type: 0底价 1卖价
    function tableData(url, type, page, ptime) {
        if(page == undefined){
            page = 1;
        }
        url = url + '?pageNo=' + page;
        $.ajax({
            dataType: "json",
            async: false,
            url: url,
            success: function (json) {
                if (json.status == 200) {
                    var html = template('tablePrice', json);
                    if (!type) {
                        $('.floorPrice .ct').html(html);
                        if (ptime != 'undefined') {
                            $('.floorPrice .datetime').val(ptime);
                        }
                        num = $('.floorPrice .roomList li').length;
                        $('.floorPrice').css('height', (num * 56 + 100) + 'px');
                        $('.floorPrice .contentBox').css('height', (num * 57 + 79) + 'px');
                        $('.floorPrice .contentFooter').css('height', (num * 55 + (num * 1)) + 'px');
                        // timePage('.floorPrice',pageTotal);
                    } else {
                        $('.sellPrice .ct').html(html);
                        if (ptime != 'undefined') {
                            $('.sellPrice .datetime').val(ptime);
                        }
                        num = $('.sellPrice .roomList li').length;
                        $('.sellPrice').css('height', (num * 56 + 100) + 'px');
                        $('.sellPrice .contentBox').css('height', (num * 57 + 79) + 'px');
                        $('.sellPrice .contentFooter').css('height', (num * 55 + (num * 1)) + 'px');
                        // timePage('.sellPrice',pageTotal);
                    }
                }
            }
        });

    }


    //日期翻页
    function timePage(obj, pageTotal) {
        var type = obj == '.floorPrice' ? 0 : 1;
        var page = 1;
        var proxyId = $("#proxyInnId").val();
        var accId = (type == 0 ? $("#" + proxyId + "-baseAcc").val() : $("#" + proxyId + "-saleAcc").val());
        var url = "/proxysale/inn/" + accId + "/inninfo";
        //上一页
        $(obj + ' .timeLastM').click(function () {
            if (page == 1) {
                return;
            } else {
                page -= 1;
                page == 0 ? 1 : page;
            }
            curTime = $(obj + ' .datetime').val();
            if (curTime == getFormatDate()) {
                return;
            }
            var time = TC.date(curTime) - 0;
            var timeNext = new Date(time - 86400000 * 15);
            $(obj + ' .datetime').val(getFormatDate(timeNext));
            ptime = getFormatDate(timeNext);
            headTime(type, ptime);
            tableData(url, type, page, ptime);
        });

        //下一页
        $(obj + ' .timeNextM').click(function () {
            if (page < pageTotal) {
                page += 1;
            } else {
                return;
            }
            curTime = $(obj + ' .datetime').val();
            var time = TC.date(curTime) - 0;
            var timeLast = new Date(time + 86400000 * 15);
            $(obj + ' .datetime').val(getFormatDate(timeLast));
            ptime = getFormatDate(timeLast);
            headTime(type, ptime);
            tableData(url, type, page, ptime);
        });

    }


    function getFormatDate(date) {
        if (!date) {
            var date = new Date();
        }
        var seperator1 = "-";
        var seperator2 = ":";
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var currentdate = year + seperator1 + month + seperator1 + strDate;
        return currentdate;
    }

    //0底价 1卖价
    function headTime(type, datetime) {
        var data = {}
        if (datetime) {
            data.dataArr = get30date(datetime);
        } else {
            data.dataArr = get30date();
        }
        var html = template('headTime', data);
        if (!type) {
            $('.floorPrice .ht .timeShow').html(html);
        } else {
            $('.sellPrice .ht .timeShow').html(html);
        }
    }

    $('.obtn').click(function () {
         $.ajax({
           type:'POST',
           url:'/proxysale/inn/'+ $("#proxyInnId").val() +'/otalink/modify',
           data:{otaLink:$(".ota-span").children("input").val()},
           success:function(){
                alert('修改成功');
           }
         });
    });
    
    $('.ota-span').find("input").dblclick(function () {
       if ($(this).val()){
    	   window.open($(this).val());
       }
   });
    
    $('.ota-foot > ul > li > span.rectangle ').click(function () {
    	$(this).css("display","block")
    	$(".ota-foot ul li .detail").css("visibility","visible");
    });

})
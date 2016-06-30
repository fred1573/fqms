$(function() {
    $.datepicker.regional['zh-CN'] = {
        closeText: '关闭',
        prevText: '<上月',
        nextText: '下月>',
        currentText: '今天',
        monthNames: ['一月','二月','三月','四月','五月','六月',
            '七月','八月','九月','十月','十一月','十二月'],
        monthNamesShort: ['一','二','三','四','五','六',
            '七','八','九','十','十一','十二'],
        dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],
        dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],
        dayNamesMin: ['日','一','二','三','四','五','六'],
        weekHeader: '周',
        dateFormat: 'yy-mm-dd',
        firstDay: 1,
        isRTL: false,
        showMonthAfterYear: true,
        yearSuffix: '年'
    };
    $.datepicker.setDefaults($.datepicker.regional['zh-CN']);
    $(".startDate" ).datepicker({
        inline: true,
        showOtherMonths: true,
        selectOtherMonths:true,
        maxDate: new Date(),
        onSelect: function(dateText, inst) {
            $('#startDate').html(dateText);
            var result = filter();
            pager(result[0],result[1]);
        }
    }).datepicker('widget').wrap('<div class="ll-skin-latoja"/>');

    $(".endDate" ).datepicker({
        inline: true,
        showOtherMonths: true,
        selectOtherMonths:true,
        maxDate: new Date(),
        onSelect: function(dateText, inst) {
            $('#endDate').html(dateText);
            var start = $('#startDate').html();
            if(dateText < start){
                alert('结束日期不能小于开始日期');
            }else{
                var result = filter();
                pager(result[0],result[1]);
            }
        }
    }).datepicker('widget').wrap('<div class="ll-skin-latoja"/>');

    function getNowFormatDate() {
        var date = new Date();
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

    $('.startDate').val('');
    $('.endDate').val('');
    $('#startDate').html('yyyy-mm-d');
    $('#endDate').html('yyyy-mm-d');
    opendiv();

    function opendiv(){
        $('.dx-table > tbody > tr').each(function(i,obj){
            if($(obj).is(':visible')){
                $(obj).addClass('eClass');
            }
        })

        $('.eClass').each(function(i,obj){
            i%2 && $(obj).addClass('evenClass');
        })
    }
    $('.dx-table').on('click', '.pass', function(){
        var recordCode = $(this).parent().parent().find('.recordCode').val();
        var innId = $(this).parent().parent().find('.innId').val();
        var innName = $(this).parent().parent().find('.innName').val();
        var pattern = $(this).parent().parent().find('.pattern').val();
        if(pattern == '精品代销'){
            pattern = 1;
        }else if(pattern == '普通代销'){
            pattern = 2;
        }else{
            alert('合作模式异常, pattern=' + pattern);
        }
        $('#review-pass .recordCode').val(recordCode);
        $('#review-pass .innId').val(innId);
        $('#review-pass .innName').val(innName);
        $('#review-pass .iName').html(innName);
        $('#review-pass .pattern').val(pattern);

        $.ajax({
            dataType: "json",
            url:'/proxysale/inn/price/checkOnOffLog',
            type:"post",
            data:{innId:innId},
            success:function(data){
                if(data.result) {
                    if(confirm("温馨提示：该客栈有被下架记录，上次下架原因：" + data.message + "\n是否让该客栈自动上架？")) {
                        $('#review-pass').popups();
                    }
                } else {
                    $('#review-pass').popups();
                }
            }
        });
    });

    $('.dx-table').on("click",'.nopass',function(){
        var recordCode = $(this).parent().parent().find('.recordCode').val();
        var innId = $(this).parent().parent().find('.innId').val();
        var innName = $(this).parent().parent().find('.innName').val();
        var pattern = $(this).parent().parent().find('.pattern').val();
        if(pattern == '精品代销'){
            pattern = 1;
        }else if(pattern == '普通代销'){
            pattern = 2;
        }else{
            alert('合作模式异常, pattern=' + pattern);
        }
        $('#review-nopass .recordCode').val(recordCode);
        $('#review-nopass .innId').val(innId);
        $('#review-nopass .innName').val(innName);
        $('#review-nopass .iName').html(innName);
        $('#review-nopass .pattern').val(pattern);

        $('#review-nopass').popups();
    });

    $.ajax({
        dataType: "json",
        url:'/proxysale/inn/price/list',
        success:function(json){
            if(json.status){
                tableCreate(json);
                pager(json.total,json.pageSize);

                $("#sh option[value='UNCHECK']").attr("selected", "selected");
                $('#sh').change();
            }
        }
    });

    //json数据生成表格
    function tableCreate(json){
        var tr = '';

        $.each(json.data,function(key,data) {
            var dataStatus = "";
            var dx = data.pattern =='NORMAL'?'普通代销':'精品代销';
            switch(data.status)
            {
                case 'UNCHECK':
                    dataStatus = "待审核";
                    break;
                case 'CHECKED':
                    dataStatus = "审核通过";
                    break;
                case 'REJECT':
                    dataStatus = "否决";
            }

            tr += '<tr><td title="' + data.recordCode + '">' + data.recordCode.substring(0, 15) + '...' + '<input type="hidden" class="recordCode" value="' + data.recordCode + '"><input type="hidden" class="innId" value="' + data.innId + '"><input type="hidden" class="innName" value="' + data.innName+ '"></td><td>' + data.dateUpdated + '</td><td>' + dx + '<input type="hidden" class="pattern" value="'+ dx +'">' + '</td><td>' + data.innName + '</td><td>' + dataStatus + '</td><td><span class="begin up">展开</span><span class="pass">通过</span><span class="nopass">否决</span></td></tr><tr class="opentr" ><td colspan="6"><div class="inndiv"><table><thead><tr><th width="10%">房型名称</th><th width="10%">默认价</th><th width="30%"><p>周末价</p><p><b>一</b><b>二</b><b>三</b><b>四</b><b>五</b><b>六</b><b>七</b></p></th><th width="30%">特殊价</th></tr></thead><tbody>';
            for(var index =0;index<data.priceRecordJsonBeanVo.length;index++){
                var p = data.priceRecordJsonBeanVo[index].weekPriceVoList;
                var weekPrice = "";//周末价
                for(var j = 1;j<=7;j++){
                    var weekStr = "";
                    for(var i=0;i<p.length;i++){
                        if(parseInt(p[i].weekDate) === j){
                            weekStr+='<b>'+p[i].weekSellingPrice+'</b>';
                        }
                    }
                    if(!weekStr){
                        weekPrice+="<b></b>"
                    }else{
                        weekPrice += weekStr;
                    }
                }
                var defultPrice = data.priceRecordJsonBeanVo[index].defaultPrice ? data.priceRecordJsonBeanVo[index].defaultPrice : '';
                tr +='<tr><td width="10%">' + data.priceRecordJsonBeanVo[index].roomTypeName + '</td><td width="5%">' + defultPrice;
                tr +='</td><td width="40%" class="prices">' + weekPrice + '</td><td width="35%" class="std">';
                if(data.priceRecordJsonBeanVo[index].specialPriceVoList){
                    for(var k = 0;k<data.priceRecordJsonBeanVo[index].specialPriceVoList.length;k++){
                        tr +='<i class="specialPrice"><b>' + data.priceRecordJsonBeanVo[index].specialPriceVoList[k].specialBeginDate + ' —— ' + data.priceRecordJsonBeanVo[index].specialPriceVoList[k].sepcialEndDate + '</b><b class="sprice">' + data.priceRecordJsonBeanVo[index].specialPriceVoList[k].otherSellingPrice + '</b></i>';
                    }
                }
                tr +='</td></tr>';
            }
            tr +='</tbody></table></div><div class="mark">备注:<a href="' + data.otaLink + '">' + data.otaLink + '</a></div></td></tr>';
        });
        $('.dx-table tbody').html(tr);

    }

    function pager(total,pageSize){
        hiddenButton();
        $('#pager').pagination({
            items: total,//总条数
            itemsOnPage: pageSize,//每页显示条数
            // theme: 'simple-theme',
            onPageClick: function(pageNo, e) {
                var keyword = $('#innName').val();
                keyword = encodeURI(keyword);
                filter(pageNo,pageSize,keyword);
                hiddenButton();
            }
        })
    }


    //条件过滤
    function filter(pageNo,pageSize,keyword){
        var pageNo = pageNo ? pageNo : 1;
        var pageSize = pageSize ? pageSize : 10;
        var keyword = keyword ? keyword : '';
        var from = $('.startDate').val();
        var to = $('.endDate').val();
        var pattern = $('#dx').val();
        var status = $('#sh').val();
        var result =new Array();
        //var keyword = $('#innName').val();
        var data = {};
        if(keyword){
            data.keyword = keyword;
            data.pageNo = pageNo;
            data.pageSize = pageSize;
        }else{
            data = {
                from:from,
                to:to,
                pattern :pattern,
                status:status,
                pageNo:pageNo,
                pageSize:pageSize
            }
        }
        $.ajax({
            dataType: "json",
            async: false,
            url:'/proxysale/inn/price/list',
            data:data,
            success:function(json){
                if(json.status){
                    tableCreate(json);
                    total = json.total;
                    var status = $('#sh').val();
                    result[0] = json.total;
                    result[1] = json.pageSize;
                    if(status =='CHECKED' || status =='REJECT'){
                        $('.pass').css('display','none');
                        $('.nopass').css('display','none');
                    }
                }
            }
        });
        return result;
    }

    var status = $('#sh').val();
    // console.log(status);
    if(status =='CHECKED' || status =='REJECT'){
        // console.log(status);
        $('.pass').css('display','none');
        $('.nopass').css('display','none');
    }

    $('#dx').change(function() {
        var result = filter();
        pager(result[0],result[1]);
    });

    $('#sh').change(function() {
        var result = filter();
        // console.log(result);
        pager(result[0],result[1]);
    });

    $('#confirm-pass').click(function(){
        var innId = $(this).parent().parent().find('.ui-popups-body .innId').val();
        var status = 'CHECKED';
        var code = $(this).parent().parent().find('.ui-popups-body .recordCode').val();
        var pattern = $(this).parent().parent().find('.ui-popups-body .pattern').val();

        $.ajax({
            type: "POST",
            dataType: "json",
            url:'/proxysale/inn/price/checkout/'+code,
            data:{
                innId:innId,
                status:status,
                pattern:pattern
            },
            success:function(json){
                if(json.status == 200){
                    $('#review-pass').popups('hide');
		$('.reason').val('');
                    var obj = $("td .recordCode[value='"+code+"']").parent().parent();
                    obj.next().remove();
                    obj.remove();

                    var result = filter();
                    pager(result[0],result[1]);
                }else{
                    alert("系统错误，联系技术," + json.message);
                }
            }
        });
    })

    $('#confirm-nopass').click(function(){
        var innId = $(this).parent().parent().find('.ui-popups-body .innId').val();
        var status = 'REJECT';
        var code = $(this).parent().parent().find('.ui-popups-body .recordCode').val();
        var reason = $('.reason').val();
        var pattern = $(this).parent().parent().find('.ui-popups-body .pattern').val();
        $.ajax({
            type: "POST",
            dataType: "json",
            url:'/proxysale/inn/price/checkout/'+code,
            data:{
                innId:innId,
                status:status,
                reason:reason,
                pattern:pattern
            },
            success:function(json){
                if(json.status){
                    $('#review-nopass').popups('hide');
                    var obj = $("td .recordCode[value='"+code+"']").parent().parent();
                    obj.next().remove();
                    obj.remove();
                    var result = filter();
                    pager(result[0],result[1]);

                }
            }
        });

    })

    $('#search_submit').click(function(){
       var keyword = $('#innName').val();
        keyword = encodeURI(keyword);
        var result = filter(1,10,keyword);
        pager(result[0],result[1]);
        hiddenButton();
        $("#sh").val("");
    })

    $("#innName").keydown(function(e){
        var curkey = e.which;
        if(curkey == 13){
            $('#search_submit').click();
        }
    })


    $('.dx-table').on('click','.begin', function() {
        if($(this).hasClass("up")){
            $(this).parents('tr').next('.opentr').css('display','table-row');
            $(this).removeClass('up').addClass('down').html('收缩');
        }else{
            $(this).parents('tr').next('.opentr').css('display','none');
            $(this).removeClass('down').addClass('up').html('展开');
        }
    });

    function hiddenButton(){
        $('.dx-table tbody tr:visible').each(function(){
            var ele = $(this).find('td').eq(4);
            if(ele.text() =='审核通过' || ele.text() == '否决'){
                ele.next().find('.pass, .nopass').hide()
            }
        })
    }

});
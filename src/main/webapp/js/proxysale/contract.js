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
        var innNameVal = $(this).parent().parent().children().find('.innName').val();
        var id = $(this).parent().parent().children('td').eq(0).find('.innId').val();
        var html = '通过' + innNameVal + '的合同前，请先设置客栈的总抽佣比例'
        $('#innName3').html(html);
        $('.scale').show();
        $('#review-pass').show();
        $('.ui-popups-foot').hide();
        $('.dul').hide();
        var innId = $('.innName').prev().val();
        $.get("inn/getProxyInnPricePattern?pricePattern=" + 2 + "&innId=" + id, function (result) {
            if (result.status == 200) {
                $('#scale').val(result.result);
                console.log(result.result);
            }else{
                alert(result.message);
                $('#scale').val('');
                $('#review-pass').hide();
                return;
            }
        })
        $('#review-pass').popups();
        $("#checkAllChannel").on("click",function () {
            if(this.checked){
                $.each($("#sale_ul").find("input[type='checkbox']"),function () {
                    if(!$(this).attr("disabled")) {
                        $(this).prop("checked",true)
                    }
                })
                //$("#sale_ul").find("input[type='checkbox']").prop("checked",true)
            }else {
                $("#sale_ul").find("input[type='checkbox']").prop("checked",false)
            }
        })
        $('#nextStep1').off().on('click',function(){
            $('.scale').hide();
            $('.dul').show();
            $('.ui-popups-foot').show();
            var html1 = '确认通过' + innNameVal +  '的合同，并设置售卖渠道?'
            $('#innName1').html(html1);
           // var id = $('.innName').prev().attr('class').val();
           // var id = $('.innName').prev().val();
            var pricePattern =parseFloat(parseInt($('#scale').val()*100)/100);
            var url = "/proxysale/inn/getChannelByType?innId=" + id + '&pricePattern=' + pricePattern;
            var baseUl =  $("#base_ul");
            baseUl.empty();
            var saleUl =  $("#sale_ul");
            saleUl.empty();
            var li ;
            function Pattern(pattern,num){
                $.each(pattern, function(i) {
                    if(num==1){
                        li =  $("<li>").appendTo(baseUl);
                    }else{
                        li =  $("<li>").appendTo(saleUl);
                    }
                    if(pattern[i].isOpen) {
                        $("<input type='checkbox' name='area'  checked='checked' >").attr("value", pattern[i].channelId).appendTo(li);
                    } else {
                        if(pattern[i].isCanOpen) {
                            $("<input type='checkbox' name='area'>").attr("value",pattern[i].channelId).appendTo(li);
                        } else {
                            $("<input type='checkbox' name='area'  disabled='disabled' >").attr("value",pattern[i].channelId).appendTo(li);
                        }
                    }
                    $(li).append(pattern[i].channelName);
                    if($('#base_ul,#sale_ul').find('input:checkbox[name="area"]').not(":disabled").length === $('#base_ul,#sale_ul').find('input:checkbox[name="area"]:checked').length){
                        $('#checkAll').prop("checked", true);
                    }else{
                        $('#checkAll').prop("checked", false);
                    }
                    $('#edit_channel').show();
                });
            }
            $.get(url, function (result) {
                console.log(result);
                if(result.status != 200){
                    alert("获取渠道失败");
                    return;
                }else{
                    var sale =  result.result.sale;
                    var base = result.result.base;
                    Pattern(sale,2);
                    Pattern(base,1);
                }
            })
        })
        $('#confirm-pass').off().on('click',function(){
            //var id = $('.innName').prev().val();
            var sale = [];
            for(var i=0;i<$('#sale_ul li input[name="area"]:checked').length;i++){
                sale.push($('#sale_ul li input[name="area"]:checked').eq(i).val());
            }
            var datas = {
                type : 0,
                innId : id,
                sale : sale,
                pricePattern : $('#scale').val()
            }
            $.get("/proxysale/contract/passAuditContract",{jsonData:JSON.stringify(datas)},function(rs){
                if(rs.status==200){
                    $('#review-pass').hide();
                   alert('审核通过，设置渠道成功！')
                }else{
                    alert(rs.message)
                }
            })
        })
    });



    $('.dx-table').on("click",'.nopass',function(){
        var innId = $(this).parent().parent().find('.innId').val();
        $('#review-nopass .innId').val(innId);
        $('#review-nopass').popups();
    });

    $.ajax({
        dataType: "json",
        url:'/proxysale/contract/list?from=&to=',
        success:function(json){
            if(json.status == true){
                tableCreate(json);
                pager(json.total,json.pageSize);
            }
        }
    });

    //json数据生成表格
    function tableCreate(json){
        var tr = '';

        $.each(json.data,function(key,data) {
            var dataStatus = "";
            switch(data.status)
            {
                case 'UNCHECK':
                    dataStatus = "待审核";
                    break;
                case 'CHECKED':
                    dataStatus = "审核通过";
                    break;
                case 'REJECTED':
                    dataStatus = "否决";
                    break;
                case 'REPEAT':
                    dataStatus = "已重新提交";
            }
            var imagesUrl = '/proxysale/contract/{innId}/images?innName={innName}';
            imagesUrl = imagesUrl.replace('{innId}', data.innId).replace('{innName}', data.innName);
            tr += '<tr><td title="' + data.innName + '">' + data.innName + '...' + '<input type="hidden" class="innId" value="' + data.innId + '"><input type="hidden" class="innName" value="' + data.innName + '"></td><td>' + data.commitTime + '</td><td>' + data.userName + '</td><td>' + dataStatus + '</td><td><span class="detail"><a href="'+ imagesUrl +'" target="_blank">查看</a></span></td><td><span class="pass">通过</span><span class="nopass">否决</span></td></tr>';
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
                if(keyword == undefined){
                    keyword = "";
                }
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
        var status = $('#sh').val();
        if(keyword == ""){
            keyword = $("#keyword").val();
        }
        var result =new Array();
        var data = {
                from:from,
                to:to,
                status :status,
                pageNo:pageNo,
                pageSize:pageSize,
                keyword:keyword
            }

        $.ajax({
            dataType: "json",
            async: false,
            url:'/proxysale/contract/list',
            data:data,
            success:function(json){
                if(json.status){
                    tableCreate(json);
                    total = json.total;
                    result[0] = json.total;
                    result[1] = json.pageSize;
                    if(status =='CHECKED' || status =='REJECTED'){
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
    if(status =='CHECKED' || status =='REJECTED'){
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

        $.ajax({
            type: "POST",
            dataType: "json",
            url:'/proxysale/contract/'+ innId + '/audit',
            data:{
                innId:innId,
                status:status
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
        var status = 'REJECTED';
        var reason = $('.reason').val();

        $.ajax({
            type: "POST",
            dataType: "json",
            url:'/proxysale/contract/' + innId + '/audit',
            data:{
                innId:innId,
                status:status,
                reason:reason
            },
            success:function(json){
                if(json.status == 200){
                    $('#review-nopass').popups('hide');
                    $('.reason').val('');
                    var result = filter();
                    pager(result[0],result[1]);
                }else{
                    $('#review-nopass').popups('hide');
                    alert(json.message);
                }
            }
        });

    })
    $("#keyword").keydown(function(e){
        var curkey = e.which;
        if(curkey == 13){
            $("#search_submit").click();
            return false;
        }
    });
    $('#search_submit').click(function(){
        var keyword = $('#keyword').val();
        keyword = encodeURI(keyword);
        var result = filter(1,10,keyword);
        pager(result[0],result[1]);
        hiddenButton();
    })

    function hiddenButton(){
        $('.dx-table tbody tr:visible').each(function(){
            var ele = $(this).find('td').eq(3);
            if(ele.text() =='审核通过' || ele.text() == '否决'){
                ele.next().next().find('.pass, .nopass').hide()
            }
        })
    }

});
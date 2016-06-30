//global var
isEdited = false;
$(function () {
    $("#Sale").on("click","#checkAllSale",function() {
        if($(this).is(":checked")) {
            $.each($("input[name='checkAllSale']"),function() {
                $(this).prop("checked",true)
            })
             $("input[name='checkAllSale']").prop("checked",true)
         } else {
            $.each($("input[name='checkAllSale']"),function() {
                $(this).prop("checked",false)
            })
         }
    })
    $("#Base").on("click","#checkAllBase",function() {
        if($(this).is(":checked")) {
            $.each($("input[name='checkAllBase']"),function() {
                $(this).prop("checked",true)
            })
        } else {
            $.each($("input[name='checkAllBase']"),function() {
                $(this).prop("checked",false)
            })
        }
    })
    $("#Base").on("click","input[name='checkAllBase']",function() {
        if($(this).is(":checked")) {
            var len = 0;
            $.each($("input[name='checkAllBase']"),function() {
                if($(this).is(":checked")) {
                    len++
                }
            })
            if(len==$("input[name='checkAllBase']").length) {
                $("#checkAllBase").prop("checked",true)
            }
        } else {
            if($("#checkAllBase").is(":checked")) {
                $("#checkAllBase").prop("checked",false)
            }
        }
    })
    $("#Sale").on("click","input[name='checkAllSale']",function() {
        if($(this).is(":checked")) {
            var len = 0;
            $.each($("input[name='checkAllSale']"),function() {
                if($(this).is(":checked")) {
                    len++
                }
            })
            if(len==$("input[name='checkAllSale']").length) {
                $("#checkAllSale").prop("checked",true)
            }
        } else {
            if($("#checkAllSale").is(":checked")) {
                $("#checkAllSale").prop("checked",false)
            }
        }
    })
    $.datepicker.regional["zh-CN"] = {
        closeText: "关闭",
        prevText: "&#x3c;上月",
        nextText: "下月&#x3e;",
        currentText: "今天",
        monthNames: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
        monthNamesShort: ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"],
        dayNames: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"],
        dayNamesShort: ["周日", "周一", "周二", "周三", "周四", "周五", "周六"],
        dayNamesMin: ["日", "一", "二", "三", "四", "五", "六"],
        weekHeader: "周",
        dateFormat: "yy-mm-dd",
        firstDay: 1,
        isRTL: !1,
        showMonthAfterYear: !0,
        yearSuffix: "年"
    }
    $.datepicker.setDefaults($.datepicker.regional["zh-CN"]);
    $('.search-button').val("");
    $('#remove').off().on('click', function () {
        if ($('.remove-reason textarea').val().trim() == '') {
            alert('原因为必填项！');
            $('.reason textarea').focus();
            return;
        }
        $('.remove-reason textarea').hide();
        $('.remove-reason div').html($('.remove-reason textarea').val()).show();
        $('.enterRemove').hide();
        $('.enterRemove2').show();
    })
    $('#nextStep').off().on('click', function () {
        var pricePattern = $('input[name="consignment"]:checked').val();
        var areaId = $('input[name="areaId"]').val();
        if ($('.select-tab').val() == 2) {
            $('.area_content_title span').html('确认上线渠道：');
        } else {
            $('.area_content_title span').html('确认下线渠道：');
        }
        $('#saleChannel').html('');
        $('#batchStep1').hide();
        $('#batchStep2').show();
        checkAll('checkAll1', 'batchStep2', 'salechannel');
        $.get("inn/getChannelByArea?areaId=" + areaId + "&pricePattern=" + pricePattern, function (result) {
            if (result.status == 200) {
                var baseUl = $("#saleChannel");
                $.each(result.result, function (i) {
                    li = $("<li>").appendTo(baseUl);
                    if (result.result[i].isSaleBase) {
                        baseUl.find('li').eq(i).css('color', 'blue');
                    }
                    if (result.result[i].isOpen) {
                        $("<input type='checkbox' name='salechannel'  checked='checked' >").attr("value", result.result[i].channelId).appendTo(li);
                    } else {
                        if (result.result[i].isCanOpen) {
                            $("<input type='checkbox' name='salechannel'>").attr("value", result.result[i].channelId).appendTo(li);
                        } else {
                            $("<input type='checkbox' name='salechannel'  disabled='disabled' >").attr("value", result.result[i].channelId).appendTo(li);
                        }
                    }
                    $(li).append(result.result[i].channelName);
                });
            }
        })
    })
    function message(id1, id2, str) {
        $('#' + id1).slideDown();
        $('#' + id2).html(str);
        var timeout = setTimeout(function () {
            $('#' + id1).slideUp();
        }, 10000)
        $('#' + id1).on('mouseover', function () {
            clearTimeout(timeout);
            $('#' + id1).slideDown();
        })
        $('#' + id1).on('mouseout', function () {
            timeout = setTimeout(function () {
                $('#' + id1).slideUp();
            }, 5000)
        })
    }

    function onOrOffShelf(num) { //num为上线渠道和下线渠道
        $('#inner_id_check1').off().on('click', function () {
            var pricePattern = parseInt(parseFloat($('input[name="consignment"]:checked').val() * 100) / 100);
            var areaId = $('input[name="areaId"]').val();
            var channelIds = [];
            var saleBase = [];
            for (var i = 0; i < $('#saleChannel li').length; i++) {
                if ($('#saleChannel').find('li').eq(i).find('input').is(':checked')) {
                    if ($('#saleChannel').find('li').eq(i).css('color') == 'rgb(0, 0, 255)') {
                        saleBase.push($('#saleChannel li input[name="salechannel"]').eq(i).val());
                    } else {
                        channelIds.push($('#saleChannel li input[name="salechannel"]').eq(i).val());
                    }
                }
            }
            var datas = [];
            var json = {
                priceStrategy: 1,
                channelIds: channelIds
            }
            if (pricePattern == 2) {
                json.priceStrategy = 2;
                var json1 = {
                    priceStrategy: 3,
                    channelIds: saleBase
                }
                datas[0] = json;
                datas[1] = json1;
            } else {
                datas[0] = json;
            }
            var jsonData = {
                areaId: areaId,
                datas: datas
            }
            if (channelIds.length === 0 && saleBase.length === 0) {
                alert('您未选择渠道！')
                return;
            }
            var pattern;
            if (pricePattern == 1) {
                pattern = "精品代销";
            } else {
                pattern = "普通代销";
            }
            if (num == 2) {
                $.get("inn/batchOnShelfByArea", {jsonData: JSON.stringify(jsonData)}, function (rs) {
                    if (rs.status == 200) {
                        $('#dialogBlackBg').hide();
                        $('#areaSearchOnline').hide();
                        var html = '您批量上线的' + pattern + '操作成功！';
                        message('onOroffShelfSuccess', 'operateMessage', html)
                        // submitForm();
                    } else {
                        alert(rs.message)
                    }
                })
            } else {
                $.get("inn/batchOffShelfByArea", {jsonData: JSON.stringify(jsonData)}, function (rs) {
                    if (rs.status == 200) {
                        $('#dialogBlackBg').hide();
                        $('#areaSearchOnline').hide();
                        var html = '您批量下线的' + pattern + '操作成功！';
                        message('onOroffShelfSuccess', 'operateMessage', html);
                        // submitForm();
                    } else {
                        alert(rs.message)
                    }
                })
            }
        })
    }

    $('.select-tab').on('change', function () {
        switch ($(this).val()) {
            case '0':
                break;
            case '1':
                //AreaSearch();
                areaBatchOff();
                break;
            case '2':
                $('#dialogBlackBg').show();
                $('#areaSearchOnline').fadeIn();
                $('#batchStep1').show();
                $('#batchStep2').hide();
                onOrOffShelf(2);
                $('#batchStep1 .title').html('请选择批量上线的模式 ：');
                break;
            case '3':
                $('#dialogBlackBg').show();
                $('#saleChannel').html('');
                $('#areaSearchOnline').show();
                $('#batchStep1').show();
                $('#batchStep2').hide();
                onOrOffShelf(3);
                $('#batchStep1 .title').html('请选择批量下线的模式 ：');
                break;
        }
    })
    $('#saleChannel').on('change', 'input:checkbox[name="salechannel"]', function () {
        if (!this.checked) {
            $('#checkAll1').prop("checked", false);
        }
        if ($('#saleChannel').find('input:checkbox[name="salechannel"]').not(":disabled").length === $('#saleChannel').find('input:checkbox[name="salechannel"]:checked').length) {
            $('#checkAll1').prop("checked", true);
        }
    })
    $('#base_ul,#sale_ul').on('change', 'input:checkbox[name="area"]', function () {
        if (!this.checked) {
            $('#checkAll').prop("checked", false);
        }
        if ($('#base_ul,#sale_ul').find('input:checkbox[name="area"]').not(":disabled").length === $('#base_ul,#sale_ul').find('input:checkbox[name="area"]:checked').length) {
            $('#checkAll').prop("checked", true);
        }
    })
    $('.dialoag-bottom').on('click', '.cancel,.cancel1', function () {
        $('.windowBg').hide();
        $('.dialoag-content').hide();
    })
    $('.select-closeroomtime').on('click', '.delete-time', function () {
        $(this).parent().children().first().show();
        $(this).next().show();
        $(this).hide();
    })
    $('.select-closeroomtime').on('click', '.revert-time', function () {
        $(this).parent().children().first().hide();
        $(this).prev().show();
        $(this).hide();
        $('.warm').html('*请选择正确的日期，如不选择请点击取消');
    })
    $('.add-a-closeroom').on('click', function () {
        if ($('.addtime').html() != '') {
            var Time = $('.start-time');
            for (var i = 0; i < Time.length; i++) {
                if ($('.start-time').eq(i).val() == '' || $('.end-time').eq(i).val() == '') {
                    $('.warm').html('*提示：日期选择不能为空！');
                    return;
                }
            }
            ;
        }
        $('.addtime').append('<p><span class="delete-line">_______________________________________________________</span><input type="text" class="start-time time placeholder="开始时间">至<input type="text" class="end-time time1" "placeholder="结束时间"><button class="delete-time">删除</button><button class="revert-time">恢复</button></p>');
    })
    $(".select-closeroomtime").on('focus', '.time1', function () {
        var dateStr = $(this).prev().val();
        var date = new Date(Date.parse(dateStr.replace(/-/g, "/")));
        $(this).datepicker({
            minDate: date,
            inline: true
        });
    });
    $(".select-closeroomtime").on('focus', '.time', function () {
        $(this).datepicker({
            minDate: new Date(),
            inline: true
        });
    });
    $('.dialoag-bottom .time-enter').on('click', function () {
        if ($('.addtime').html() != '') {
            var Time = $('.start-time');
            if (Time.length == 0) {
                $('.warm').html('*提示：请添加至少一条关房');
                return;
            }
            var Num = 0;
            for (i = 0; i < Time.length; i++) {
                if ($('.delete-time').css('display') == 'none') {
                    Num++;
                }
            }
            if (Num == Time.length) {
                $('.warm').html('*提示：请添加至少一条关房');
                return;
            }
            // if(Time.length==)
            for (var i = 0; i < Time.length; i++) {
                if ($('.start-time').eq(i).val() == '' || $('.end-time').eq(i).val() == '') {
                    $('.warm').html('*提示：日期选择不能为空！');
                    return;
                }
            }
            ;
        } else {
            $('.warm').html('*提示：请添加至少一条关房');
            return;
        }
        $('#list-time').html('');
        $('.windowBg').hide();
        $('.windowBg1').show();
        var Length = $('.select-closeroomtime p').length - 1;
        var timeValue = [];
        var timeValue1 = [];
        for (var i = 0; i < Length; i++) {
            if ($('.delete-time').eq(i).css('display') == 'none') {
                continue;
            }
            timeValue[i] = $('.start-time').eq(i).val();
            timeValue1[i] = $('.end-time').eq(i).val();
            $('#list-time').append('<p><span class="list-time-span">' + timeValue[i] + '</span>至<span class="list-time-span1">' + timeValue1[i] + '</span><p>');
        }
    })
    $('.dialoag-bottom .time-enter1').on('click', function () {
        if ($('.addtime').html() != '') {
            var Time = $('.start-time');
            if (Time.length == 0) {
                $('.warm').html('*提示：请添加至少一条关房');
                return;
            }
            for (var i = 0; i < Time.length; i++) {
                if ($('.start-time').eq(i).val() == '' || $('.end-time').eq(i).val() == '') {
                    $('.warm').html('*提示：日期选择不能为空！');
                    return;
                }
            }
            ;
        } else {
            $('.warm').html('*提示：请添加至少一条关房');
            return;
        }
        $('#list-time').html('');
        $('.windowBg').hide();
        $('.windowBg1').show();
        var Length = $('.select-closeroomtime p').length - 1;
        var timeValue = [];
        var timeValue1 = [];
        for (var i = 0; i < Length; i++) {
            if ($('.delete-time').eq(i).css('display') == 'none') {
                continue;
            }
            timeValue[i] = $('.start-time').eq(i).val();
            timeValue1[i] = $('.end-time').eq(i).val();
            $('#list-time').append('<p><span class="list-time-span">' + timeValue[i] + '</span>至<span class="list-time-span1">' + timeValue1[i] + '</span><p>');
        }
        var Num = 0;
        for (i = 0; i < Time.length; i++) {
            if ($('.delete-time').eq(i).css('display') == 'none') {
                Num++;
            }
        }
        if (Num == Time.length) {
            $('#list-time').html('<p>删除关房？<p>');
        }
    });
    $('.return-cancel,.return-cance2').on('click', function () {
        $('.windowBg').show();
        $('.windowBg1').hide();
    })
    $('.tab-close-room').on('click', function () {
        $('.windowBg').show();
        $('.addarea').text($(this).parent().siblings().eq(2).text());
        $('.warm').html('*请选择正确的日期，如不选择请点击取消');
        $('.addtime').html('');
        if ($('.add-a-closeroom').prevAll().length > 0) {
            $('.add-a-closeroom').prevAll().remove();
        }
        if ($('.search-area input.search').val() != '') {
            $('.addarea').text($('.search-area input.search').val());
        }
        else {
            $('.addarea').text('全部');
        }
    })
    $('.last-time-enter1').off('click').on('click', function () {
        var Data = {};
        Data.areaId = $('input[name="areaId"]').val();
        Data.closeDate = [];
        var closeBeginDateLength = $('.list-time-span').length;
        for (var i = 0; i < closeBeginDateLength; i++) {
            Data.closeDate[i] = {};
            Data.closeDate[i].closeBeginDate = $('.list-time-span').eq(i).text();
            Data.closeDate[i].closeEndDate = $('.list-time-span1').eq(i).text();
            ;
        }
        $('.windowBg1').hide();
        $('.windowBg2').show();
        $.post(ctx + "/proxysale/inn/areaOff", {closeInfo: JSON.stringify(Data)}, function (result) {
            $('.windowBg2').hide();
            alert(result.message);
            submitForm();
        })
    });
    function checkAll(id, id1, name) {
        $("#" + id).click(function () {
            var flag = $(this).get(0).checked;
            if (flag) {
                $.each($("#" + id1).find("input[name=" + name + "]").not(":disabled"), function (index, ele) {
                    ele.checked = true;
                });
            } else {
                $("#" + id1).find("input[name=" + name + "]").not(":disabled").attr("checked", false);
            }
        });
    }

    checkAll('checkAll', 'edit_channel', 'area');
    checkAll('checkAll1', 'saleChannel', 'salechannel');
    function AreaSearch() {
        $('.warm').html('*请选择正确的日期，如不选择请点击取消');
        $('.dialoag-content').show();
        $('.windowBg').show();
        $('.p-time-enter').show();
        $('.p-time-enter1').hide();
        $('.dialoag-bottom-p1').hide();
        $('.dialoag-bottom-p2').show();
        $('.addtime').html('');
        if ($('.add-a-closeroom').prevAll().length > 0) {
            $('.add-a-closeroom').prevAll().remove();
        }
        if ($('.search-area input.search').val() != '') {
            $('.addarea').text($('.search-area input.search').val());
        }
        else {
            $('.addarea').text('全部');
        }
    }
    // 区域批量关房
    function areaBatchOff() {
        // 获取区域名称
        var areaName = "全国";
        var selectAreaName = $('.search-area input.search').val();
        if(selectAreaName != '') {
            areaName = selectAreaName;
        }
        layer.open({
            type: 1,
            title:"添加【" + areaName + "】区域的关房时间段",
            area: ['360px', '360px'],
            shadeClose: true, //点击遮罩关闭
            btn: ['确定', '取消', '添加'], //可以无限个按钮
            // 确定按钮的事件
            yes: function(index, layero){
                var dateSet = $(".dateSet");
                if(dateSet.html() != '') {
                    var startTime = $(".start-time");
                    if(startTime.length == 0) {
                        layer.msg('请至少添加一条关房记录');
                        return;
                    } else {
                        for(var i = 0;i < startTime.length; i ++){
                            if($('.start-time').eq(i).val() == '' || $('.end-time').eq(i).val() == ''){
                                layer.msg('日期不能为空');
                                return;
                            }
                        }
                    }
                    if(startTime.length > 1) {
                        layer.alert("批量关房每次只能选择一个时间段");
                        return false;
                    }
                    var size = $(layero).find(".time-set").length;
                    var content = "";
                    for(var i = 0; i < size; i ++){
                        content += '<div class="time-set"><span class="list-time-span">' + $('.start-time').eq(i).val() + '</span>至<span class="list-time-span1">' + $('.end-time').eq(i).val() +'</span></div>';
                    }
                    content += "  该时间段进行关房";
                    content += "</br><input type='radio' name='offType' value='1'/> 关闭OMS房量";
                    content += "<input type='radio' name='offType' value='2' checked='checked'/> 仅关闭分销商房量";
                    layer.open({
                        type: 1,
                        title:"您确定为【" + areaName + "】区域的客栈添加",
                        area: ['360px', '360px'],
                        shadeClose: true, //点击遮罩关闭
                        btn: ['确定', '返回'], //可以无限个按钮
                        yes: function(index2, layero2) {
                            var Data = {};
                            Data.areaId = $('input[name="areaId"]').val();
                            var offType =  $(layero2).find("input[type=radio]:checked").val();
                            Data.offType =offType;
                            Data.closeBeginDate = $(layero2).find(".list-time-span").text();
                            Data.closeEndDate = $(layero2).find('.list-time-span1').text();
                            var closeBeginDateLength = $('.list-time-span').length;
                            if(closeBeginDateLength > 1) {
                                layer.alert("批量关房每次只能选择一个时间段");
                                return false;
                            }
                            layer.close(index);
                            layer.close(index2);
                            var index3 = layer.load(1, {
                                shade: [0.5,'#fff'] //0.1透明度的白色背景
                            });
                            $.post(ctx + "/proxysale/inn/areaOff",{closeInfo:JSON.stringify(Data)}, function (result){
                                layer.alert(result.message);
                                layer.close(index3);
                            })
                        },
                        content: content
                    });
                } else {
                    layer.msg('请至少添加一条关房记录');
                }
            },
            // 添加关房日期
            btn3:function(index, layero){
                var container = $(layero).find(".dateSet");
                container.append('<div class="time-set"><input type="text" class="start-time time" readonly="readonly" placeholder="开始时间"/>&nbsp;至&nbsp;<input type="text" class="end-time time1" readonly="readonly" placeholder="结束时间"/><button class="delete-time">删除</button></div>');
            },
            content: '<div class="dateSet"></div>'
        });
        // 关房开始日期
        $("body").on('focus','.time',function(){
            $(this).datepicker({
                minDate: new Date(),
                inline: true
            });
        });
        // 关房结束日期
        $("body").on('focus','.time1',function(){
            var dateStr = $(this).prev().val();
            var date= new Date(Date.parse(dateStr.replace(/-/g,   "/")));
            $(this).datepicker({
                minDate: date,
                inline: true
            });
        });
        // 删除关房日期
        $("body").on('click','.delete-time',function(){
            var btn = $(this);
            layer.confirm('您确定要删除关房日期？', {
                btn: ['删除','暂不'] //按钮
            },function() {
                btn.parent().remove();
                layer.msg('删除成功', {icon: 1});
            })
        });
    }
})

//删除客栈
function del(id, This) {
    $('#dialogBlackBg').show();
    $('#del').show();
    $('.enterRemove').show();
    $('.enterRemove2').hide();
    $('.remove-reason div').html("").hide();
    $('.remove-reason textarea').show().val('').focus();
    $('#innNameVal').html($('#' + id).val());
    $('#removeEnter').off().on('click', function () {
        var url = '/proxysale/inn/del';
        var jsonData = {
            id: id,
            reason: $('.remove-reason textarea').val()
        }
        $.post(url, jsonData, function (rs) {
            if (!(rs.status == 200)) {
                alert(rs.message);
                return;
            }
            $(This).parent().parent().remove();
            $('#del').hide();
            $('#edit').hide();
            $('#dialogBlackBg').hide();
            alert('成功移除');

        })
    })
}
//提交客栈删除
function ensureDel() {
    var id = $("#delId").val();
    var url = ctx + "/proxysale/inn/${id}/del";
    url = url.replace("${id}", id);
    $.get(url, function (obj) {
        close(1);
        setTimeout(ref, 500);
    });
}
//上线渠道
function onshelf(id, type) {
    var html = "确定要{onOrOff}架 {innName} 的 {baseOrSale} 模式吗?"
    if (type == 1) {//上架精品代销
        html = html.replace("{onOrOff}", "上");
        html = html.replace("{baseOrSale}", "底价");
        html = html.replace("{innName}", "" + $("#" + id).val());
        $("#onshelfPrompt").html(html);
    } else if (type == 2) {//下架精品代销
        html = html.replace("{onOrOff}", "下");
        html = html.replace("{baseOrSale}", "底价");
        html = html.replace("{innName}", "" + $("#" + id).val());
        $("#onshelfPrompt").html(html);
    } else if (type == 3) {//上架普通代销
        html = html.replace("{onOrOff}", "上");
        html = html.replace("{baseOrSale}", "卖价");
        html = html.replace("{innName}", "" + $("#" + id).val());
        $("#onshelfPrompt").html(html);
    } else if (type == 4) {//下架普通代销
        html = html.replace("{onOrOff}", "下");
        html = html.replace("{baseOrSale}", "卖价");
        html = html.replace("{innName}", "" + $("#" + id).val());
        $("#onshelfPrompt").html(html);
    } else {
        alert("上下架类型异常，快联系技术");
        return;
    }
}
var hasSubmit = false;
//提交上架信息
function ensureOnshelf() {
    if (hasSubmit) {
        alert("已提交上下架请求，请耐心等待");
        return;
    }
    hasSubmit = true;
    var id = $("#onshelfId").val();
    var type = $("#type").val();
    var url = ctx + "/proxysale/inn/${id}/${operate}";
    url = url.replace("${id}", id);
    if (type == 1) {//上架精品代销
        url = url.replace("${operate}", "baseonshelf");
    } else if (type == 2) {//下架精品代销
        url = url.replace("${operate}", "baseoffshelf");
    } else if (type == 3) {//上架普通代销
        url = url.replace("${operate}", "saleonshelf");
    } else if (type == 4) {//下架普通代销
        url = url.replace("${operate}", "saleoffshelf");
    } else {
        alert("上下架类型异常，快联系技术");
        return;
    }
    $.get(url, function (obj) {
        hasSubmit = false;
        close(2);
        setTimeout(ref, 500);
    });
}
//刷新页面
function ref() {
    location.reload();
}

//关闭显示框
function close(status) {
    if (status == '1') {
        $("#del").fadeOut();
        $('#dialogBlackBg').hide();
    } else if (status == '2') {
        $("#onshelf").fadeOut();
        $('#dialogBlackBg').hide();
    } else if (status == '3') {
        $("#edit").fadeOut();
        $('#dialogBlackBg').hide();
    } else if (status == '4') {
        $("#edit_channel").fadeOut()
        $('#dialogBlackBg').hide();
    } else if (status == '5') {
        $("#soldOut").fadeOut();
        $('#dialogBlackBg').hide();
    } else if (status == '6') {
        $('#areaSearchOnline').fadeOut();
        $('#dialogBlackBg').hide();
    } else if (status == '7') {
        $('#onOroffShelfSuccess').fadeOut();
        $('#dialogBlackBg').hide();
    } else if (status == '8') {
        $("#edit_inn_percentage").fadeOut();
        $('#dialogBlackBg').hide();
    } else if(status == '9') {
        $("#roomType").hide();
    } else if(status == '10') {
        $("#enterRoomType").hide()
    }
    else {
        alert('close exception');
    }
    //hideCoverBox();
}
// 客栈关房操作
function getInnCloseInfo(innId, This) {
    $.post(ctx + "/proxysale/inn/getInnCloseInfo", {innId: innId}, function (result) {
        if (result.status != 200) {
            alert(result.message);
            return;
        }
        $('.windowBg').show();
        $('.dialoag-content').show();
        $('.dialoag-bottom-p1').show();
        $('.dialoag-bottom-p2').hide();
        $('.p-time-enter1').show();
        $('.p-time-enter').hide();
        $('.addarea').text($(This).parent().siblings().eq(2).text());
        $('.warm').html('*请选择正确的日期，如不选择请点击取消');
        $('.addtime').html('');
        if ($('.add-a-closeroom').prevAll().length > 0) {
            $('.add-a-closeroom').prevAll().remove();
        }
        for (var i = 0; i < result.result.length; i++) {
            $('.addtime').append('<p><span class="delete-line">_______________________________________________________</span><input type="text" class="start-time">至<input type="text" class="end-time"><button class="delete-time">删除</button><button class="revert-time">恢复</button></p>');
            $('.start-time').eq(i).val(result.result[i].closeBeginDate).css('border', 'none');
            $('.end-time').eq(i).val(result.result[i].closeEndDate).css('border', 'none');
        }
        $('.last-time-enter').off('click').on('click', function () {
            var Data = {};
            Data.innId = innId;
            Data.closeDate = [];
            var closeBeginDateLength = $('.list-time-span').length;
            for (var i = 0; i < closeBeginDateLength; i++) {
                Data.closeDate[i] = {};
                Data.closeDate[i].closeBeginDate = $('.list-time-span').eq(i).text();
                Data.closeDate[i].closeEndDate = $('.list-time-span1').eq(i).text();
                ;
            }
            $.post(ctx + "/proxysale/inn/innOff", {closeInfo: JSON.stringify(Data)}, function (result) {
                $('.windowBg1').hide();
                alert(result.message);
                submitForm();
            })
        })
    });
}
//总抽佣比例
function scale(id) {
    $("#innName").html($("#" + id).val());
}
//编辑
function edit(id) {
    $("#proxyInnId").val(id);
    $.get(ctx + "/proxysale/inn/" + id + "/channels", function (result) {
        if (result.status != 200) {
            alert("获取渠道失败");
            return;
        }
        $("#innName").html($("#" + id).val());
        var otaInfos = result.otaInfoVOs;
        if (otaInfos == null) {
            return;
        }
        var html = "";
        var template = "<input type='checkbox' name='channels' value=':value' :checked>:name&nbsp;";
        $.each(otaInfos, function (index, value) {
            var tempHtml = template.replace(":value", value.otaInfo.otaId).replace(":name", value.otaInfo.name);
            if (value.selected) {
                tempHtml = tempHtml.replace(":checked", "checked");
            } else if (value.canRelate) {
                tempHtml = tempHtml.replace(":checked", "");
            } else {
                tempHtml = tempHtml.replace(":checked", "disabled='disabled'");
            }
            html = html + tempHtml;
        })
        $("#channels").html(html);
    });
    $("#edit").fadeIn();
};

function soldOut(id, type) {
    $('#dialogBlackBg').show();
    $('#soldOut').fadeIn();
    $('.remark textarea').val('');
    var html;
    if (type == 'boutique') {
        html = '您确定要下架' + $('#' + id).val() + '的精品代销吗？';
        $('#enter-soldout').html(html);
    } else if (type == 'general') {
        html = '您确定要下架' + $('#' + id).val() + '的普通代销吗？';
        $('#enter-soldout').html(html);
    }
    $('#enterSoldout').off().on('click', function () {
        var remark = $('.remark textarea').val();
        if (type == 'boutique') {
            var pricePattern = 1;
        } else {
            var pricePattern = 2;
        }
        $.get("/proxysale/inn/offShelf?proxyInnId=" + id + "&pricePattern=" + pricePattern + "&reason=" + remark, function (result) {
            if (result.status == 200) {
                $('#soldOut').hide();
                $('#dialogBlackBg').hide();
                alert("提交成功");
                // 提交表单，刷新页面
                submitForm();
                return;
            } else {
                alert(result.message)
            }
            // 提交表单，刷新页面
            submitForm();
        })

    })
}
function General(id) {

    $.get("/proxysale/audit/hasContractChecked/" + id, function (result) {
        function renderChannels() {
            $('#dialogBlackBg').show();
            $("#edit_channel").fadeIn();
            $('.scale').show();
            $('.dul').hide();
            $.get("inn/getProxyInnPricePattern?proxyInnId=" + id + "&pricePattern=" + 2, function (result) {
                if (result.status == 200) {
                    $('#scale').val(result.result);
                    console.log(result.result);
                } else {
                    alert(result.message);
                    return;
                }
            })
            $('#nextStep1').off().on('click', function () {
                setChannelsDialog(id, 2, 3);
            });
            return;
        }

        if (!result.result) {
            if (confirm("没有审核通过的合同哟，你真要上架？")) {
                if (confirm("不后悔？")) {
                    return renderChannels();
                }
            }
            alert("good boy~");
        }else{
            renderChannels();
        }
    });

}
function setChannelsDialog(id, type, number) {
    $('#dialogBlackBg').show();
    var url = "/proxysale/inn/getChannelByType?proxyInnId=" + id;
    $("#inner_id_check").val(id);
    $('#innName1').html($('#' + id).val());
    var baseUl = $("#base_ul");
    baseUl.empty();
    var saleUl = $("#sale_ul");
    saleUl.empty();
    var li;

    function Pattern(pattern, num) {
        $.each(pattern, function (i) {
            if (num == 1) {
                li = $("<li>").appendTo(baseUl);
            } else {
                li = $("<li>").appendTo(saleUl);
                if (pattern[i].isSaleBase) {
                    saleUl.find('li').eq(i).css('color', 'blue');
                }
            }
            if (pattern[i].isOpen) {
                $("<input type='checkbox' name='area'  checked='checked' >").attr("value", pattern[i].channelId).appendTo(li);
            } else {
                if (pattern[i].isCanOpen) {
                    $("<input type='checkbox' name='area'>").attr("value", pattern[i].channelId).appendTo(li);
                } else {
                    $("<input type='checkbox' name='area'  disabled='disabled' >").attr("value", pattern[i].channelId).appendTo(li);
                }
            }
            $(li).append(pattern[i].channelName);
            if ($('#base_ul,#sale_ul').find('input:checkbox[name="area"]').not(":disabled").length === $('#base_ul,#sale_ul').find('input:checkbox[name="area"]:checked').length) {
                $('#checkAll').prop("checked", true);
            } else {
                $('#checkAll').prop("checked", false);
            }
            $('#edit_channel').show();
        });
    }

    if (number == 1) {//上架精品代销
        html = '您确定要上架' + $('#' + id).val() + '的精品代销吗？';
        $("#onshelfPrompt").html(html);
        $('.boutique').show();
        $('.general').hide();
        $('.scale').hide();
        $('.dul').show();

        $.get(url, function (result) {
            console.log(result);
            if (result.status != 200) {
                alert("获取渠道失败");
                return;
            } else {
                var base = result.result.base;
                if (base.length == 0) {
                    alert("没有可以上架的精品代销");
                    return;
                }
                Pattern(base, 1);
            }
        })
    } else if (number == 3) {//上架普通代销
        html = '您确定要上架' + $('#' + id).val() + '的普通代销吗？';
        $("#onshelfPrompt").html(html);
        $('.boutique').hide();
        $('.general').show();
        $('.scale').hide();
        $('.dul').show();
        $.get(url, function (result) {
            console.log(result);
            if (result.status != 200) {
                alert("获取渠道失败");
                return;
            } else {
                var sale = result.result.sale;
                if (sale.length == 0) {
                    alert("没有可以上架的普通代销");
                    return;
                }
                Pattern(sale, 2);
            }
        })
    } else if (number == 0) {
        html = $("#" + id).val();
        $('.boutique').show();
        $('.general').show();
        $('.scale').hide();
        $('.dul').show();

        $.get(url, function (result) {
            console.log(result);
            if (result.status != 200) {
                alert("获取渠道失败");
                return;
            } else {
                var sale = result.result.sale;
                var base = result.result.base;
                Pattern(sale, 2);
                Pattern(base, 1);
            }
        })
    } else {
        alert("上下架类型异常，快联系技术");
        return;
    }
    var pricePattern;
    if (type == 2) {
        pricePattern = parseInt(parseFloat($('#scale').val() * 100)) / 100;
    }
    function message(id1, id2, str) {
        $('#' + id1).slideDown();
        $('#' + id2).html(str);
        var timeout = setTimeout(function () {
            $('#' + id1).slideUp();
        }, 10000)
        $('#' + id1).on('mouseover', function () {
            clearTimeout(timeout);
            $('#' + id1).slideDown();
        })
        $('#' + id1).on('mouseout', function () {
            timeout = setTimeout(function () {
                $('#' + id1).slideUp();
            }, 5000)
        })
    }

    $('#inner_id_check').off().on('click', function () {
        var base = [];
        for (var i = 0; i < $('#base_ul li input[name="area"]:checked').length; i++) {
            base.push($('#base_ul li input[name="area"]:checked').eq(i).val());
        }
        var sale = [];
        var saleBase = [];
        for (var i = 0; i < $('#sale_ul li').length; i++) {
            if ($('#sale_ul').find('li').eq(i).find('input').is(':checked')) {
                if ($('#sale_ul').find('li').eq(i).css('color') == 'rgb(0, 0, 255)') {
                    saleBase.push($('#sale_ul li input[name="area"]').eq(i).val());
                } else {
                    sale.push($('#sale_ul li input[name="area"]').eq(i).val());
                }
            }
        }
        var datas = {
            proxyInnId: id,
            type: type,
            pricePattern: pricePattern,
            sale: sale,
            base: base,
            saleBase: saleBase
        };
        $.get("inn/modifyProxyInnChannel", {jsonData: JSON.stringify(datas)}, function (rs) {
            if (rs.status == 200) {
                $('#edit_channel').hide();
                var pattern;
                if (type == 1) {
                    pattern = "精品代销";
                    var html = $('#' + id).val() + '上架的' + pattern + '操作成功！';
                    if ($.trim($('#' + id).parent().next().html()) == '已下架') {
                        $('#' + id).parent().next().html('已上架精品(活动)代销')
                    } else {
                        $('#' + id).parent().next().html('已上架')
                    }
                    var tdhtml = "<a herf='javascript:void(0)' " + "onclick=soldOut(" + id + ",'boutique') " + "style='color:blue;'>" + "下架精品(活动)代销</a>";
                    $('#' + id).parents('tr').find('td').eq(8).html(tdhtml);
                } else if (type == 2) {
                    pattern = "普通代销";
                    var html = $('#' + id).val() + '上架的' + pattern + '操作成功！';
                    if ($.trim($('#' + id).parent().next().html()) == '已下架') {
                        $('#' + id).parent().next().html('已上架普通(卖)代销');
                    } else {
                        $('#' + id).parent().next().html('已上架')
                    }
                    var tdhtml = "<a herf='javascript:void(0)' " + "onclick=soldOut(" + id + ",'general') " + "style='color:blue;'>" + "下架普通(卖)代销</a>";
                    $('#' + id).parents('tr').find('td').eq(9).html(tdhtml);
                } else {
                    pattern = "设置渠道";
                    var html = $('#' + id).val() + pattern + '操作成功！';
                }
                message('onOroffShelfSuccess', 'operateMessage', html);
                $('#dialogBlackBg').hide();
                // submitForm();
            } else {
                alert(rs.message)
            }
            // 提交表单，刷新页面
            // submitForm();
        })
    })
}
function closeDialog() {
    $("#edit_channel").fadeOut();
}
function doEdit() {
    var proxyInnId = $("#proxyInnId").val();
    var channels = "";
    $("input[name='channels']:checked").each(function (key, value) {
        channels = channels + value.value + ",";
    });
    var percentage = $("#percentage").val();
    if (isNaN(percentage) || percentage <= 0) {
        if (isEdited) {
            alert("分佣比例不能为空,不能传空格,不能小于等于0,\n\n咋说不听呢!---____---!");
        } else {
            alert("分佣比例不能为空,不能传空格,不能小于等于0");
            isEdited = true;
        }
        return;
    }
    var url = "/proxysale/inn/" + proxyInnId + "/modify";
    $.post(url, {channels: channels, percentage: percentage}, function (result) {
        if (result.status == 200) {
            close(3);
            alert("修改成功");
            return;
        }
        alert(result.message);
    });
}
//显示框
function show(status) {
    if (status == '1') {
        $("#del").fadeIn();
    } else if (status == '2') {
        $("#onshelf").fadeIn();
    } else if (status == '3') {
        $("#edit").fadeIn();
    } else {
        alert('show exception');
    }
    //showCoverBox();
}
function wrap(list, info) {
    var template = "<td>{content}</td>"
    $.each(list, function (index, key) {
        info = info + "<tr>";
        info = info + template.replace("{content}", key.roomTypeName);
        $.each(key.roomDetail, function (k, v) {
            var roomDate = v.roomDate;
            info = info + template.replace("{content}", roomDate.substring(5) + "\n星期" + getDay(roomDate) + "\n" + v.roomPrice);
        });
        info = info + "</tr>";
    });
    info = info + "</table>";
    return info;
}
function renderInnInfo() {
    var proxyInnId = $("#proxyInnId").val();
    var baseAccId = $("#" + proxyInnId + "-baseAcc").val();
    if (baseAccId == "") {
        alert('还没有底价');
    } else {
        $.get("/proxysale/inn/" + baseAccId + "/inninfo", function (data) {
            if (data.status != 200) {
                alert('底价：' + data.result);
                return;
            }
            var json = data.result;
            var list = json.list;
            var info = "<table border='1'><tr style='text-align: center' ><th colspan='29' id='base'>底价</th></tr>";
            info = wrap(list, info)
            $("#basePattern").html(info);
        });
    }
    var saleAccId = $("#" + proxyInnId + "-saleAcc").val();
    if (saleAccId == "") {
        alert('还没有卖价');
    } else {
        $.get("/proxysale/inn/" + saleAccId + "/inninfo", function (data) {
            if (data.status != 200) {
                alert('卖价：' + data.result);
                return;
            }
            var json = data.result;
            var list = json.list;
            var info = "<table border='1'><tr style='text-align: center' ><th colspan='29' id='sale'>卖价</th></tr>";
            info = wrap(list, info);
            $("#salePattern").html(info);
        });
    }
}
function getDay(riqi) {
    var arr = riqi.split('-'); //日期为输入日期，格式为 2013-3-10
    var ssdate = new Date(arr[0], parseInt(arr[1] - 1), arr[2]);
    return ssdate.getDay();
}
function changeStatus() {
    var status = $("#filterStatus").val();
    $("#fs").val(status);
    $("#mainForm").submit();
}
// 提交表单
function submitForm() {
    $("#mainForm").submit();
};
function setSalePercentage(id, perc) {
    $("#edit_percentage_inn_id").val(id);
    $("#sale-percentage-value").val(perc);
    $('#dialogBlackBg').show();
    $('#edit_inn_percentage').fadeIn();
};
function reqUpdatePerrcentage() {
    var id = $("#edit_percentage_inn_id").val();
    var perc = $("#sale-percentage-value").val();
    $.get(ctx + "/proxysale/inn/update/inn/percentage?id=" + id + "&percentage=" + perc, function (result) {
        if (result.status == 200) {
            close('8');
            submitForm();
        }
    })
}

function roomTypeList(This) {
    var url = ctx + "/proxysale/inn/roomType";
    $("#Base").html("<li>活动房型</li><li><label for='checkAllBase'><input type='checkbox' id='checkAllBase'>全选</label> </li>");
    $("#Sale").html("<li>代销房型</li><li><label for='checkAllSale'><input type='checkbox' id='checkAllSale'>全选</label> </li>");
    $.get(url,{innId: $(This).prevAll("input[name='innId']").val()},function(data) {
        if(data.result) {
            if(data.result.base || data.result.sale) {
                $.each(data.result.base,function() {
                    $("#Base").append("<li><label><input type='checkbox' name='checkAllBase' value="+this.roomTypeName+" data-accountId="+this.accountId+" data-omsRoomTypeId="+this.omsRoomTypeId+">"+this.roomTypeName+"</label></li>")
                })
                $.each(data.result.sale,function() {
                    $("#Sale").append("<li><label><input type='checkbox' name='checkAllSale' value="+this.roomTypeName+" data-accountId="+this.accountId+" data-omsRoomTypeId="+this.omsRoomTypeId+">"+this.roomTypeName+"</label></li>")
                })
            }
            if(!data.result.base.length && !data.result.sale.length) {
                $("#sheefRoomType").attr("disabled",true)
            }else {
                $("#sheefRoomType").attr("disabled",false)
            }
        }
        $("#roomType").show();
        $("#roomTypeInnName").html($(This).prevAll("input[name='InnName']").val());
    })
}

function sheefRoomType() {
    $("#roomType").hide();
    $("#enterRoomType").show();
    $("#enterSaleRoomType").html("代销房型:")
    $("#enterBaseRoomType").html("活动房型:");
    $.each($("input[name='checkAllSale']"),function() {
        if($(this).is(":checked")) {
            $("#enterSaleRoomType").append("<i>"+$(this).val()+"</i>")
        }
    })
    $.each($("input[name='checkAllBase']"),function() {
        if($(this).is(":checked")) {
            $("#enterBaseRoomType").append("<i>"+$(this).val()+"</i>")
        }
    })
}
function cancelEnterRoomType() {
    $("#roomType").show();

    $("#enterRoomType").hide();
}
function enterRoomType() {
    var url = ctx + "/proxysale/inn/down/roomType"
    var data = {
        innName : $("#roomTypeInnName").html(),
        sale : [],
        base : [],
        saleName : [],
        baseName : []
    }
    $.each($("#Base").find("input[name='checkAllBase']"),function() {
       if($(this).is(":checked")) {
           data.base.push({
               accountId : $(this).attr("data-accountId"),
               omsRoomTypeId : $(this).attr("data-omsRoomTypeId")
           })
           data.baseName.push($(this).val())
       }
    })
    $.each($("#Sale").find("input[name='checkAllSale']"),function() {
        if($(this).is(":checked")) {
            data.sale.push({
                accountId : $(this).attr("data-accountId"),
                omsRoomTypeId : $(this).attr("data-omsRoomTypeId")
            })
            data.saleName.push($(this).val())
        }
    })
    $.post("/proxysale/inn/down/roomType?data="+JSON.stringify(data),function(rs) {
        if(rs.status == 200) {
            $("#enterRoomType").hide();
            alert("下架成功！")
        }else {
            alert(rs.message);
        }
    })
}

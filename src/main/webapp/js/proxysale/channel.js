/**
 * Created by Administrator on 2015/6/8.
 */
$(function(){
    $('.chnnel-name').on('click','.remove',function(){
       $(this).parent().remove();
    })
    $('.chnnel-name-dd').on('click','.add-area',function(){
        for(var i=0;i<$('.areaLV1').length-1;i++){
            if($('.areaLV1:last').val()===$('.areaLV1').eq(i).val() && $(this).prev().find('select').val()!=1){
                alert('区域选择重复了！已将重复区域删除。');
                $(this).prev().remove();
                return;
            }
        }
        if($(this).prev().find('select').val()==1){
            alert('区域已经为全国，不能再添加。');
           return;
        }else{
            if($('.areaLV1').length<10){
                $(this).before('<span><em class="remove">一</em><select class="areaLV1"></select></span>');
                getProvinces(1,$(this));
            }else{
                alert('最多添加十个区域');
            }
        }
    })
    $('.chnnel-name').on('change','.areaLV1',function(){
        if($('.areaLV1').index(this)==0 && $(this).val()==1){
           $(this).parent().nextAll('span').remove();
        }
        if($('.areaLV1').index(this)<$('.areaLV1').length-1){
            for(var i=0;i<$('.areaLV1').length;i++){
                if($('.areaLV1').index(this)==i){
                    i++;
                }
                if($('.areaLV1').eq(i).val()===$(this).val()) {
                    alert('不能选择重复区域,已将重复区域删除。');
                    $('.areaLV1').eq(i).parent().remove();
                }
            }
        }else{
            for(var i=0;i<$('.areaLV1').length-1;i++){
                if($('.areaLV1').eq(i).val()===$(this).val()) {
                   alert('不能选择重复区域,已将重复区域删除。');
                    $('.areaLV1').eq(i).parent().remove();
                }
            }
        }
     })
})
//刷新页面
function ref() {
    location.reload();
}
//关闭显示框
function close(status) {
    if(status=='1'){
        $("#edit").fadeOut();
    }else{
        alert('close exception');
    }
    //hideCoverBox();
}
//显示框
function show(id) {
    $("#basePer").val('');
    $("#salePer").val('');
    $("#edit").fadeIn();
    $("#channelId").val(id);
    $("#channelNameEdit").html($("#" + id).val());
    $.get("/proxysale/ota/" + id,function(result){
        if(result.status != 200){
            alert("get edit_info error");
            return;
        }
        var res = result.result;
        var basePriceStrategry = res.basePriceStrategry;
        if(basePriceStrategry > 0){
            $("#basePer").val(basePriceStrategry);
            $("#basePS").prop("checked", true);
            $("#basePer").removeAttr("disabled");
        }else{
            $("#basePS").prop("checked", false);
            $("#basePer").attr("disabled",true);
        }
        var salePriceStrategry = res.salePriceStrategry;
        var saleBasePriceStrategry = res.saleBasePriceStrategry;
        if(salePriceStrategry > 0 || saleBasePriceStrategry>0){
            if(salePriceStrategry != -1){
                $("input[name='price']").eq(0).prop("checked","true");
                $("#salePer").val(salePriceStrategry);
            }else{
                $("input[name='price']").eq(1).prop("checked","true");
                $("#salePer").val(saleBasePriceStrategry);
            }
            $("#salePS").prop("checked", true);
            $("#salePer").removeAttr("disabled");
        }else{
            $("#salePS").prop("checked", false);
            $("#salePer").attr("disabled",true);
            $("input[name='price']").removeAttr("checked");
            //$("input[name='price']").eq(1).attr("checked","checked");
        }

        var areas = res.areas;
        console.log(areas)
        var html = '';
        for(var i=0;i<areas.length;i++){
            html += '<span><em class="remove">一</em><select class="areaLV1"></select></span>';
        }
        html += '<a class="add-area"> 添加 </a>';
        $('.chnnel-name-dd').html(html);
        getProvinces(1,'',fn);
        function fn(){
            for(var i=0;i<areas.length;i++){
                $('.areaLV1').eq(i).val(areas[i].id);
            }
        }
        if($('#salePS').is(':checked')){
            $('.price').removeAttr('disabled');
        }else{
            $('.price').attr('disabled','disabled');
        }
    })
}
//编辑
function edit(){
    if(!confirm("修改渠道信息会引发后台同步与客栈的关联关系并频繁调用外部接口，\n可能需要等待十几秒至几分钟，您可以先喝杯咖啡或做点别的。\n再问一次，您确定吗？")){
        return;
    }
    var arr = [];
    for(var i=0;i<$('.areaLV1').length;i++){
        arr.push($('.areaLV1').eq(i).val());
    }
    var id = $("#channelId").val();
    var url = "/proxysale/ota/{id}/modify".replace("{id}", id);
    var basePer = $("#basePer").val();
    var salePer = $("#salePer").val();
    var saleBasePer = null;
    var channelArea = '';
    for(i=0; i<arr.length; i++){
        channelArea = channelArea + arr[i] + ",";
    }
    if( $("input[name='price']:checked").val()==1){
        saleBasePer = $("#salePer").val();
        salePer = null;
    }

    var data = {
        basePer: basePer,
        salePer: salePer,
        areas : channelArea,
        saleBasePer: saleBasePer
    }
    close(1);
    $.post(url, data, function(result){
        if(result.status != 200){
            alert(result.message);
            return;
        };
    });
}
function strategyChoose(type){
    if(type == 1){
        if($("#basePS").prop( "checked" )){
            $("#basePS").prop("checked", true);
            $('#basePer').removeAttr("disabled");
        }else{
            $("#basePS").prop("checked", false);
            $('#basePer').attr("disabled","true")
        }
    }else{
        if($("#salePS").prop( "checked" )){
            $("#salePS").prop("checked", true);
            $('#salePer').removeAttr("disabled");
            $('.price').removeAttr('disabled');
        }else{
            $("#salePS").prop("checked", false);
            $('#salePer').attr("disabled","true");
            $('.price').attr('disabled','disabled');
        }
    }
}
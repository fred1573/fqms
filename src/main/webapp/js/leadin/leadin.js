$(function(){
    clearUpdateForm();
    $("#explosionRecommend").bind('click',function(){
        if($(this).is(':checked')){
            $(this).val('1');
        }else{
            $(this).val('0');
        }
    })
    $("#explosionWeight").bind('blur',function () {
        var weight = $(this).val();
        //var regionId = $("#regionId").val();
        var city = $("#region").val();
        var shopId = $("#weiShopId").val();
        $.get(ctx+"/leadIn/ranking",{weight:weight,city:city,shopId:shopId},function(retVal){
            if(retVal.status==200){
                $("#weightTips").show();
                $("#weightTips").html("该小站前面有"+retVal.result.ranking+"个<br/>同区域前面有"+retVal.result.regionRanking+"个");
            }
        });
    });
});
function clearUpdateForm(){
    $("#mobile").val('');
    $("#innName").val('');
    $("#region").val('');
    $("#regionId").val('');
    $("#explosionWeight").val('');
    $("#explosionDesc").val('');
    var $weightTips = $("#weightTips");
    $weightTips.hide();
    $weightTips.html('');
}
function search(){
    clearUpdateForm();
    $.get(ctx+"/leadIn/search",{weiUrl:$("#phone").val()},function(retVal){
        if(retVal.status==200){
            close();
            var obj = retVal.result;
            $("#weiShopId").val(obj.id);
            $("#mobile").val(obj.mobile);
            $("#innName").val(obj.innName);
            $("#region").val(obj.regionName);
            $("#regionId").val(obj.regionId);
            $("#explosionWeight").val(obj.explosionWeight);
            $("#explosionDesc").val(obj.explosionDesc);
            var $explosionRecommend = $("#explosionRecommend");
            if(obj.explosionRecommend == '1'){
                $explosionRecommend.attr("checked",'checked');
                $explosionRecommend.val('1');
            }else{
                $explosionRecommend.removeAttr("checked");
                $explosionRecommend.val('0');
            }
            $("#update").fadeIn();
            showCoverBox();
        }else{
            alert("该账号所对应的客栈不存在");
        }
    });
}

function update() {
    $("#explosionRecommend").val();
    $("#explosionWeight").val();
    $("#explosionDesc").val();
    $("#weiShopId").val();
    $.post(ctx + '/leadIn/update',
        {shopId:$("#weiShopId").val(),explosionRecommend:$("#explosionRecommend").val(),
            explosionDesc:$("#explosionDesc").val(),explosionWeight:$("#explosionWeight").val()}, function (retVal) {
        if (retVal.status == 200) {
            closeUpdate();
            clearUpdateForm();
            jumpPage(1);
        } else {
            alert("该账号所对应的客栈不存在");
        }
    });
}
function closeUpdate(){
    $("#update").fadeOut();
    hideCoverBox();
}

function show(){
    $("#search").fadeIn();
    showCoverBox();
}
function close(){
    $("#search").fadeOut();
    hideCoverBox();
}
function jumpPage(pageNo){
    $("#pageNo").val(pageNo);
    $("#mainForm").submit();
}

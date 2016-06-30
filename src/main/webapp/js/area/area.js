/**
 * Created by Administrator on 2015/6/19.
 */
function getProvinces(id,This,callback){
    $.ajax({
        url:ctx + "/area/" + id + "/children",
        async: false,
        success: function(result){
            var areas = result.areas;
            if($('.areaLV1').length===1){
                var html = "<option value='1'class='area'>全国";
            }else{
                var html = "";
            }
            var template = "<option value=':areaId'class='area' >:name";
            $.each(areas, function(index, value){
                var areaId = value.id;
                var name = value.name;
                html = html + template.replace(":areaId", areaId).replace(":name", name).replace("{:areaId}", areaId);
            });
            if(This==''){
                $('.areaLV1').html(html);
            }else{
                $(This).prev().children().eq(1).html(html);
            }
        }
    })
    callback && callback();
}

function getCities(id,This){
    $.get(ctx + "/area/" + id + "/children", function(result){
        var areas = result.areas;
        var html = '';
        var template = "<option value=':areaId' class='area' >:name";
        $.each(areas, function(index, value){
            var areaId = value.id;
            var name = value.name;
            html = html + template.replace(":areaId", areaId).replace(":name", name).replace("{:areaId}", areaId);
        });
       $(This).next().html(html).show();
    })
}

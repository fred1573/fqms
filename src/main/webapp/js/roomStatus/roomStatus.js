$(document).ready(function () {
//点击查询
    $("#search_submit").bind("click", function () {
        search();
    });

    $(".modify").bind("click", function () {
        var $this = $(this);
        // PMS客栈ID
        var innId = $this.prev().prev().val();
        if(innId == null || innId == '') {
            layer.alert("PMS客栈ID获取失败");
            return false;
        }
        // 客栈当前的房态
        var adminType = $this.prev().val();
        if(adminType == null || adminType == '') {
            layer.alert("房态类型获取失败");
            return false;
        }
        var adminTypeStr = $this.parent().prev().text();
        if(adminTypeStr == null || adminTypeStr == '') {
            layer.alert("房态类型描述获取失败");
            return false;
        }
        // PMS客栈名称
        var innName = $this.parent().parent().find("td:first").text();
        if(innName == null || innName == '') {
            layer.alert("客栈名称失败");
            return false;
        }
        var content = '<div class="changeAdminType"><table><tr><td colspan="2">当前房态：'+adminTypeStr+'</td></tr>';
        if(adminType == 1) {
            content += '<tr><td><label><input type="radio" name="adminType" checked="checked" value="1"/>PMS</label></td><td><label><input type="radio" name="adminType" value="2"/>EBK</label></td></tr>';
        } else if(adminType == 2) {
            content += '<tr><td><label><input type="radio" name="adminType" value="1"/>PMS</label></td><td><label><input type="radio" name="adminType" checked="checked" value="2"/>EBK</label></td></tr>';
        } else {
            layer.alert("房态类型异常");
            return false;
        }
        content += '</table></div>';
        layer.open({
            type: 1,
            title: "修改<span style='color:red;font-weight:bold;'>" + innName + "</span>的房态",
            area: ['300px', '150px'],
            shadeClose: true, //点击遮罩关闭
            btn: ['确定', '取消'], //可以无限个按钮
            // 确定按钮的事件
            yes: function (index, layero) {
                var modify = $("input[name='adminType']:checked").val();
                if(adminType == modify) {
                    layer.msg("房态未发生变化");
                } else {
                    $.post(ctx + "/roomStatus/modify",{adminType:modify, innId:innId, innName:innName}, function (result){
                        layer.msg(result.message);
                        layer.close(index);
                        window.location.href=ctx + "/roomStatus/list?userCode=" + $("#userCode").val();;
                    })
                }
            },
            content: content
        });
    });
});

document.onkeydown = keyDownSearch;
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

// 提交表单
function submitForm() {
    $("#mainForm").submit();
}

// 提交查询
function search() {
    $("input[name='userCode']").val($("#userCode").val());
    submitForm();
}
// 绑定时间
$(document).ready(function () {
    //点击查询
    $("#search_submit").bind("click", function () {
        search();
    });
    $("#search_order_submit").bind("click", function () {
        $("input[name='innName']").val($("#innName").val());
        submitForm();
    });
    $("#addOrder").bind("click", function () {
        $('#add').fadeIn();
    });
    $("#addOrderSub").bind("click", function () {
        var channelIdAdd = $("input[name='channelId']").val();
        var settlementTimeAdd = $("input[name='settlementTime']").val();
        if (channelIdAdd == null || channelIdAdd == '' || settlementTimeAdd == null || settlementTimeAdd == '') {
            $("#add").fadeOut();
            alert("数据异常，没有分销商ID或账期");
            return;
        }
        var orderIdAdd = $("#orderIdAdd").val();
        var refundAdd = $("#refundAdd").val();
        var remarkAdd = $("#remarkAdd").val();
        if (orderIdAdd == null || orderIdAdd == '') {
            alert('订单号不能为空');
            return;
        }
        if (refundAdd == null || refundAdd == '') {
            alert('扣款金额不能为空');
            return;
        }
        if (remarkAdd == null || remarkAdd == '') {
            alert('备注不能为空');
            return;
        }
        var data = {
            channelId: channelIdAdd,
            settlementTime: settlementTimeAdd,
            orderId: orderIdAdd,
            refund: refundAdd,
            remark: remarkAdd
        }
        $.post('/finance/income/manualOrder/add', data, function (result) {
            if (result.status == 200) {
                alert("添加无订单赔付成功");
                $("#add").fadeOut();
                return;
            } else {
                alert("添加无订单赔付呵呵了，原因：" + result.message);
                return;
            }
        });
    });

    $("#editOrderSub").bind("click", function () {
        var id = $("#editId").val();
        var orderIdEdit = $("#orderIdEdit").val();
        var refundEdit = $("#refundEdit").val();
        var remarkEdit = $("#remarkEdit").val();
        if (orderIdEdit == null || orderIdEdit == '') {
            alert('订单号不能为空');
            return;
        }
        if (refundEdit == null || refundEdit == '') {
            alert('扣款金额不能为空');
            return;
        }
        if (remarkEdit == null || remarkEdit == '') {
            alert('备注不能为空');
            return;
        }
        var data = {
            id: id,
            orderId: orderIdEdit,
            refund: refundEdit,
            remark: remarkEdit
        }
        $.post('/finance/manualOrder/edit', data, function (result) {
            if (result.status == 200) {
                alert("编辑无订单赔付成功");
                $("#edit").fadeOut();
                return;
            } else {
                alert("编辑无订单赔付呵呵了，原因：" + result.message);
                return;
            }
        });
    });

    $("#settlementTime").bind("change", function () {
        search();
    });
    $("#auditStatus").bind("change", function () {
        search();
    });
    $("#isArrival").bind("change", function () {
        search();
    });
    // 提交实收金额修改
    $("#inner_id_check").bind("click", function () {
        var id = $(this).parent().parent().find("input[name=id]").val();
        if (id == null || id == '') {
            alert("id不能为空");
        }
        var incomeAmount = $("#incomeAmount").val();
        var remarks = $("#remarks").val();
        $.post("/finance/income/update", {id: id, incomeAmount: incomeAmount, remarks: remarks}, function (result) {
            alert(result.message)
            $("#edit").fadeOut();
            search();
        });
    });
    $("#upload").on('click', function () {
        $(".windowBg2").show();
        $.ajaxFileUpload({
            url: ctx + '/finance/upload/income',
            secureuri: false,
            fileElementId: 'file',//file标签的id
            dataType: 'json',
            success: function (result) {
                if (result.status == 200) {
                    var str = "本次对账的结果:" + result.auditStatus + "\n";
                    str += "本月番茄订单数量:" + result.fqOrders + "\n";
                    str += "渠道上传对账单中的订单数量:" + result.channelOrders + "\n";
                    str += "对账成功订单数:" + result.successOrders + "\n";
                    str += "对账失败订单数:" + result.failureOrders + "\n";
                    alert(str);
                    close();
                    $(".windowBg2").hide();
                    window.location.reload();
                } else {
                    alert(result.message);
                    close();
                    $(".windowBg2").hide();
                    window.location.reload();
                }
            },
            error: function (data, status, e) {
                alert(e);
                close();
                $(".windowBg2").hide();
                window.location.reload();
            }
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

// 导出Excel
function exportIncome() {
    var settlementTime = $("#settlementTime").val();
    if (settlementTime == "") {
        alert("请选择结算月份");
        return;
    }
    if (confirm("您确定要生成【" + settlementTime + "】的进账详情的Excel？本次生成进出账的客栈较多，预计耗时5分钟")) {
        $.post(ctx + "/finance/income/export/channels", {settlementTime: settlementTime}, function () {
            alert("生成完毕!");
        });
    }
}

// 导出暂收客栈Excel
function exportFqTemp() {
    var settlementTime = $("#settlementTime").val();
    var channelName= $("input[name='channelName']").val();
    var channelId= $("input[name='channelId']").val();
    if (settlementTime == "") {
        alert("请选择结算月份");
        return;
    }
    if (confirm("您确定要生成【" + channelName + "】暂收详情的Excel？")) {
        $.post(ctx + "/finance/income/fqTemp/export", {
            settlementTime: settlementTime,
            channelName: channelName,
            channelId: channelId
        }, function (data) {
            if (data.status == 200) {
                alert("生成完毕!");
            }
            else {
                alert("导出失败", data.message);
            }
        });
    }
}

function exportIncomeInns() {
    var settlementTime = $("#settlementTime").val();
    if (settlementTime == "") {
        alert("请选择结算月份");
        return;
    }
    $("#exportForm").submit();
}


// 上传对账单
function openDiv() {
    $("#file").val("");
    $("#edit").fadeIn();
}
//关闭显示框
function close() {
    $("#edit").fadeOut();
}

function closeAdd() {
    $("#add").fadeOut();
}

// 收款
function arrival(id, channelId, channelName, settlementTime) {
    if (settlementTime == null || settlementTime == '') {
        alert("请选择结算月份");
        return;
    }
    if (confirm("是否确认收款【" + channelName + "】")) {
        $.post(ctx + "/finance/channel/arrival", {
            id: id,
            channelId: channelId,
            settlementTime: settlementTime
        }, function (result) {
            alert(result.message);
            if (result.status == 200) {
                window.location.reload();
            }
        });
    }
}
function showDetail(channelId, settlementTime) {
    if (settlementTime == null || settlementTime == '') {
        alert("请选择结算月份");
        return;
    }
    window.location = ctx + "/finance/income/detail?channelId=" + channelId + "&settlementTime=" + settlementTime;
}

// 提交表单
function submitForm() {
    $("#mainForm").submit();
}
// 分页查询
function search() {
    $("input[name='channelName']").val($("#channelName").val());
    $("input[name='settlementTime']").val($("#settlementTime").val());
    $("input[name='auditStatus']").val($("#auditStatus").val());
    $("input[name='isArrival']").val($("#isArrival").val());
    submitForm();
}

function openEditDiv(obj, id, channelName, incomeAmount, remarks) {
    /*var remarks = $(obj).parent().next().val();*/
    $("#channelNameTitle").html(channelName);
    $("#incomeAmount").val(incomeAmount);
    $(".center-box-in input[name=id]").val(id);
    $("#remarks").val(remarks);
    $("#edit").fadeIn();
}

function editOrder(id, orderId, refund, remark) {
    $("#editId").val(id);
    $("#orderIdEdit").val(orderId);
    $("#refundEdit").val(refund);
    $("#remarkEdit").val(remark);
    $("#edit").fadeIn();
}

function delOrder(id) {
    if (!confirm("你确定？")) {
        alert("good boy~");
        return;
    }
    if (!confirm("a u sure?")) {
        alert("nice~");
        return;
    }
    $.getJSON("/finance/manualOrder/del/" + id, function (result) {
        if (result.status == 200) {
            alert('删除成功，请手动刷新');
            return;
        } else {
            alert("删除呵呵了，原因：" + result.message);
        }
    })
}
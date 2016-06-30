$(document).ready(function (e) {
    $("#queryType").on("change",function() {
        var ThisValue = $(this).val()
        if( ThisValue !== "1" ) {
            $("#queryValue").attr("placeholder","精确搜索")
        }else {
            $("#queryValue").attr("placeholder","模糊搜索")
        }
    })
    $( "#startDate" ).datepicker({
        onSelect: function() {
            var days = tmsky.date.diffDays(new Date($( "#startDate").val()),new Date($( "#endDate").val()))
            if(days<0) {
                $( "#endDate").val($( "#startDate").val());
            } else if (days>31) {
                $( "#endDate").val(tmsky.date.format(tmsky.date.addDays(new Date($( "#startDate").val()),31)));
            }
            $("input[name='totalPage']").val("0");
            $("input[name='startDate']").val($("#startDate").html().trim());
            search();
        }
    });
    $( "#endDate" ).datepicker({
        onSelect: function() {
            $("input[name='totalPage']").val("0");
            $("input[name='endDate']").val($("#endDate").html().trim());
            search();
        },
        minDate : new Date($("input[name='startDate']").val()),
        maxDate : tmsky.date.addDays(new Date($("input[name='startDate']").val()),31)
    });
    if(!$("#startDate").val()) {
        var str = tmsky.date.format(new Date())
        $("#startDate,#endDate").val(str)
    }
    //点击查询
    $("#search_submit").bind("click", function () {
        search();
    });
    $("#channelId").bind("change", function () {
        search();
    });
    $("#childChannelId").bind("change", function () {
        search();
    });
    $("#orderStatus").bind("change", function () {
        search();
    });
    $("#searchTimeType").bind("change", function () {
        search();
    });
    $("#strategyType").bind("change", function () {
        search();
    });
    $("#orderDetailsClose,#modifyOrderStatusClose,#modifyRecordClose").on("click",function() {
        $(this).parent().hide();
        $("#fullbg").hide();
    })
    $("#ksCenterHandleType,#ksCenterComplaintStatus").on("change",function() {
        search();
    })
    $("#footButton").on("click","#modifyStatus",function() {
        $("#modifyOrderStatus").show();
        $("#orderDetails").hide();
        $("#mark").val("");
    })
    $("#cancelModify").on("click",function() {
        $("#modifyOrderStatus").hide();
        $("#orderDetails").show();
    })
    $("#footButton").on("click","#modifyRecord",function() {
        $("#modifyRecordDia").show();
        $("#orderDetails").hide();
    })
    $("#returnOrderDetail").on("click",function() {
        $("#modifyRecordDia").hide();
        $("#orderDetails").show();
    })
    $("#saveModify").on("click",function() {
        var url = ctx + "/proxySaleOrder/cancelOrder";
        var data = {
            remark : $("#mark").val(),
            channelOrderNo : $("#channelOrderNo").html(),
            channelId : $("#ChannelId").html()
        }
        if(!$("#mark").val()) {
            alert("备注不能为空！")
            return;
        }
        if($("#mark").val().length>80) {
            alert("最多输入80个字！")
            return;
        }
        $.post(url,data,function(rs) {
            if(rs && rs.status && (rs.status==200)) {
                $("#modifyOrderStatus").hide();
                alert("保存成功！")
                location.reload()
            }else {
                alert("保存失败！")
            }
        })
    })
    $("#modifyRecord").on("click",function() {
        $("#orderDetails").hide();
        $("#modifyRecord").show();
    })



});
var CONST = {
    NONCOOPERATION : "不与番茄合作",
    NO_ROOM : "到店无房",
    PRICE_INCREMENT : "客栈加价",
    UNABLE : "不会操作系统",
    SYSTEM_ERROR : "系统原因",
    ROOM_TYPE_ERROR : "外网匹配错误",
    CANCEL_ORDER : "客人原因取消订单",
    CAN_NOT_CONTACT : "客人联系不上商家",
    SUSPENSION_BUSINESS : "暂停营业",
    OTHER : "其它"
}
function format(date) {

}
function details(This) {
    var channelOrderNo = $(This).prevAll("input[name='channelOrderNo']").val(),
        conName = $(This).prevAll("input[name='conName']").val()
    $("#orderDetails").show();
    $("#fullbg").show();
    $("#roomPriceInfo").html("");
    $("#conName").html(conName)
    $("#channelOrderNo").html(channelOrderNo)
    $("#orderNo").html($(This).prevAll("input[name='orderNo']").val())
    $("#strategyType").html($(This).prevAll("input[name='strategyType']").val())
    $("#innName").html($(This).prevAll("input[name='innName']").val())
    $("#userName").html($(This).prevAll("input[name='userName']").val())
    $("#contact").html($(This).prevAll("input[name='contact']").val())
    $("#channelRoomTypeName").html($(This).prevAll("input[name='channelRoomTypeName']").val())
    $("#totalAmount").html($(This).prevAll("input[name='totalAmount']").val())
    $("#paidAmount").html($(This).prevAll("input[name='paidAmount']").val())
    $("#extraPrice").html($(This).prevAll("input[name='extraPrice']").val())
    $("#ChannelId").html($(This).prevAll("input[name='channelId']").val())
    $("#customerManager").html($(This).prevAll("input[name='customerManager']").val())
    $.each($(This).prevAll(".roomInfo"),function() {
        $("#roomPriceInfo").append("<tr><td>"+$(this).find("input[name='checkInAt']").val()+"-"+$(this).find("input[name='checkOutAt']").val()+"</td><td>"+$(this).find("input[name='bookPrice']").val()+"</td><td>"+$(this).find("input[name='roomTypeNums']").val()+"/"+$(this).find("input[name='nightNumber']").val()+"</td></tr>")
    })
    if(conName=="已接受") {
        $("#footButton").html('<button id="modifyStatus">修改状态</button>')
    } else {
        var url = ctx + "/proxySaleOrder/cancelOrderLog"
        $.post(url,{channelOrderNo : channelOrderNo},function(rs) {
            if(!rs) {
                alert("返回值不存在，请重试！")
            }
            if(rs && rs.message!=0) {
                $("#footButton").html('<button id="modifyRecord">修改记录>></button>')
                $("#operator").html(rs.result.operateUser);
                $("#Mark").html(rs.result.remark);
                $("#operatTime").html(rs.result.operateTime);
            }else {
                $("#footButton").html('')
            }
        })
    }
}
//页面跳转
function jumpPage(page) {
    search(page);
}
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
//时间比较
function dateCompare(startdate, enddate) {
    var arr = startdate.split("-");
    var starttime = new Date(arr[0], arr[1], arr[2]);
    var starttimes = starttime.getTime();

    var arrs = enddate.split("-");
    var lktime = new Date(arrs[0], arrs[1], arrs[2]);
    var lktimes = lktime.getTime();

    if (starttimes > lktimes) {
        return false;
    }
    else
        return true;
}

//提交表单
function submitForm() {
    $("#mainForm").submit();
}
//查询

function search(page) {
    if (dateCompare($("#startDate").html().trim(), $("#endDate").html().trim())) {
        if(page == '' || page == null) {
            page = 1;
        }
        $("input[name='page']").val(page);
        $("input[name='channelId']").val($("#channelId").val());
        $("input[name='childChannelId']").val($("#childChannelId").val());
        $("input[name='orderStatus']").val($("#orderStatus").val());
        $("input[name='queryValue']").val($("#queryValue").val());
        $("input[name='searchTimeType']").val($("#searchTimeType").val());
        $("input[name='strategyType']").val($("#strategyType").val());
        $("input[name='startDate']").val($("#startDate").val());
        $("input[name='endDate']").val($("#endDate").val());
        $("input[name='queryType']").val($("#queryType").val())
        submitForm();
    } else {
        alert("起始时间必须小于结束时间");
    }
}
function changeStartDate() {
    $("input[name='totalPage']").val("0");
    $("input[name='startDate']").val($("#startDate").html().trim());
    search();
}
function changeEndDate() {
    $("input[name='totalPage']").val("0");
    $("input[name='endDate']").val($("#startDate").html().trim());
    search();
}

function checkDate($date) {

    var id = $date.attr("id");
    var $other = $("div.date-choose span[id!=" + id + "]");
    var from = new Date($("#activeDate").text());
    var to = new Date($("#toDate_select").text());
    var days = Date.diffDay($("#activeDate").text(), $("#toDate_select").text()) + 1;
    $("#selectDate").val($("#activeDate").text());
    $("#toDate").val($("#toDate_select").text());
    $("#sortDate").val("");
}

function exportExcel() {
    var url = ctx + '/proxySaleOrder/exportExcel?';
    url += "&channelId=" + $("input[name='channelId']").val();
    url += "&childChannelId=" + $("input[name='childChannelId']").val();
    url += "&orderStatus=" + $("input[name='orderStatus']").val();
    url += "&queryValue=" + $("input[name='queryValue']").val();
    url += "&searchTimeType=" + $("input[name='searchTimeType']").val();
    url += "&strategyType=" + $("input[name='strategyType']").val();
    url += "&startDate=" + $("#startDate").val().trim();
    url += "&endDate=" + $("#endDate").val().trim();
    $("#exportForm").attr("action",url);
    $("#exportForm").submit();
}

function closeWindow(id) {
    $("#"+id).hide();
    $("#fullbg").hide();
}
function saveHandle(thisObj,complaintStatus,complaintType) {
    var channelOrderList = []
    $.each($(thisObj).prevAll(".roomInfo"),function() {
        var json = {
            channelRoomTypeName : $(this).children("input[name=channelRoomTypeName]").val(),
            roomNums : $(this).children("input[name=roomTypeNums]").val(),
            checkInAt : $(this).children("input[name=checkInAt]").val(),
            checkOutAt : $(this).children("input[name=checkOutAt]").val()
        }
        channelOrderList.push(json)
    })
    var data = {
        channelId : $(thisObj).prevAll("input[name='channelOrderId']").val(),
        channelName : $(thisObj).prevAll("input[name='channelName']").val(),
        channelChildId : $(thisObj).prevAll("input[name='channelId']").val(),
        channelCodeName : $(thisObj).prevAll("input[name='channelCodeName']").val(),
        regionName : $(thisObj).prevAll("input[name='regionName']").val(),
        innId : $(thisObj).prevAll("input[name='innId']").val(),
        innName : $(thisObj).prevAll("input[name='innName']").val(),
        customerManager : $(thisObj).prevAll("input[name='customerManager']").val(),
        channelOrderNo : $(thisObj).prevAll("input[name='channelOrderNo']").val(),
        userName : $(thisObj).prevAll("input[name='userName']").val(),
        contact : $(thisObj).prevAll("input[name='contact']").val(),
        totalAmount : $(thisObj).prevAll("input[name='totalAmount']").val(),
        orderTime : $(thisObj).prevAll("input[name='orderTime']").val(),
        orderNo : $(thisObj).prevAll("input[name='orderNo']").val(),
        channelOrderList : channelOrderList,
        complaintStatus : complaintStatus,
        processLog : {
            complaintType : complaintType,
            note : $("#trackRecord").val()
        }
    }
    return data;
}
function complaintHandling(thisObj) {
    var url = "/proxySaleOrder/complaint/list?orderNo="+$(thisObj).prevAll("input[name='orderNo']").val();
    var data,complaintType;
    $.post(url,function(data) {
        if(data.status == 200) {
            data = data.result;
            $("#fullbg").show();
            $("#trackRecord").val("");
            $("#handleOver").show();
            $("#handleType").val("")
            $("#handleType").attr("disabled",false)
            if (data.length) {
                $("#complaintReport").show();
                $("#handleList").html("");
                $.each(data,function(key,val) {
                    $("#handleList").prepend("<div class='handleRecord'><div class='handleStatus'><b>"+this.processUserName+"</b></div></div> ")
                    if(this.complaintType) {
                        if(key==0 || this.complaintType !== data[key-1].complaintType ) {
                            $(".handleStatus:first").append("<i>将客诉类型选择为</i><i class='red'>"+CONST[this.complaintType]+"</i>")
                            $("#handleType").val(this.complaintType)
                            complaintType = this.complaintType
                        }
                    }
                    if(this.complaintStatus=="FINISH") {
                        if(key==0 || data[key-1].complaintStatus!=="FINISH" ) {
                            $(".handleStatus:first").append("<i class='red'>处理完成</i>")
                            $("#handleOver").hide()
                            $("#handleType").attr("disabled",true)
                        }
                    }
                    if(this.note) {
                        $(".handleRecord:first").append("<div>跟进记录:</div><div>"+this.note+"</div></div>")
                    }
                    $(".handleRecord:first").append("<div>"+this.createTime+"</div>")
                })
            }else {
                $("#complaintHandling").show()
                $("#trackRecord").focus()
                $("#handleType").attr("disabled",false)
            }
            $("#appendRecord").on("click",function() {
                $("#complaintHandling").show()
                $("#complaintReport").hide();
                $("#trackRecord").focus();
            })
            $("#savecomplaintHandle").on("click",function() {
                if(complaintType == $("#handleType").val()) {
                    if(!$("#trackRecord").val().trim()) {
                        layer.msg('类型未更改，跟进纪录不能为空！', {icon: 2});
                        return;
                    }
                }else {
                    complaintType = $("#handleType").val()
                }
                if(!complaintType && !$("#trackRecord").val().trim()) {
                    layer.msg('类型和跟进纪录必须填其中任意一个内容！', {icon: 2});
                    return;
                }
                if($("#trackRecord").val().length>80) {
                    layer.msg('跟进纪录最多输入80个字！', {icon: 2});
                    return;
                }
                if($("#handleType").attr("disabled") && !$("#trackRecord").val().trim()) {
                    layer.msg('请输入跟进纪录！', {icon: 2});
                    return;
                }

                var complaintStatus;
                if($("#handleOver").css("display")=='none') {
                    complaintStatus = "FINISH"
                }else {
                    complaintStatus = "STARTED"
                }
                if(!complaintType) {
                    complaintType = null
                }
                var data = saveHandle(thisObj,complaintStatus,complaintType)
                var url = "/proxySaleOrder/complaint/save";
                layer.confirm('确认保存客诉处理记录？', {
                    btn: ['确认','取消'] //按钮
                }, function(){
                    $.ajax({
                        url : url,
                        type : "POST",
                        data: {data : JSON.stringify(data)},
                        headers:  {Accept: "application/json; charset=utf-8" },
                        success : function(rs) {
                            if(rs && rs.status == 200) {
                                layer.msg('保存成功！', {icon: 1});
                                location.reload()
                            }else {
                                layer.msg('保存失败！', {icon: 2});
                            }
                        },
                        error : function(rs) {
                            layer.msg('保存失败！', {icon: 2});
                        }
                    })

                })
            })
            $("#handleOver").off().on("click",function() {
                if(!$("#handleType").val()) {
                    layer.msg('必须选择处理类型！', {icon: 2});
                    return;
                }
                if(!$("#trackRecord").val().trim()) {
                    layer.msg('跟进纪录必填！', {icon: 2});
                    return;
                }
                var data = saveHandle(thisObj,"FINISH",$("#handleType").val())
                var url = "/proxySaleOrder/complaint/save";
                layer.confirm('确认将客诉处理设置为处理完成？', {
                    btn: ['确认','取消'] //按钮
                }, function(){
                    $.ajax({
                        url : url,
                        type : "POST",
                        data: {data : JSON.stringify(data)},
                        headers:  {Accept: "application/json; charset=utf-8" },
                        success : function(rs) {
                            if(rs && rs.status == 200) {
                                layer.msg('处理完成！', {icon: 1});
                                location.reload()
                            }else {
                                layer.msg('处理失败！',{icon: 2});
                            }
                        },
                        error : function(rs) {
                            layer.msg('保存失败！', {icon: 2});
                        }
                    })
                });
            })
        }else {
            layer.msg('获取客诉纪录失败！',{icon: 2});
        }
    })
}



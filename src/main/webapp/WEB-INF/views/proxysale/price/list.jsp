<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>代销平台-渠道管理</title>
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/select2/select2.js" type="text/javascript"></script>
    <link href="${ctx}/js/select2/select2.css" rel="stylesheet">
    <link href="${ctx}/css/jquery-ui.css" rel="stylesheet">
    <style>
        .mask{
            display: none;
            position: fixed;
            left: 0px;
            top: 0px;
            width: 100%;
            height: 100%;
            cursor: move;
            opacity: 0.5;
            background: #000;
        }
        .batch-price-adjustment {
            margin-left: 20px;
            width: 100px;
            height: 30px;
            background: #62933E;
            display: inline-block;
            line-height: 30px;
            text-align: center;
            color: #fff;
        }
        .adjust-price-div {
            float: left;
            width: 100%;
            margin: 10px 0;
        }
        .inn-name {
            float: right;
            margin-right: 10px;
        }
        .channel-from {
            padding: 10px 0;
            border-bottom: 1px solid #ccc;
        }
        .channel-from label {
            margin-right: 20px;
            margin-bottom: 10px;
            display: inline-block;
        }
        .select-date, .crease-reduce-price {
            width: 143px;
            border: 1px solid #ccc;
            height: 24px;
            margin: 0 5px;
            padding-left: 10px;
            font-size: 12px;
        }
        .enter-adjust-price-p {
            line-height: 32px;;
        }
        .enter-adjust-price-p label {
            font-size: 18px;
            font-weight: bold;
            padding: 0 5px;
        }
    </style>

</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/proxysale/price/list">
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <input type="hidden" name="orderBy" id="orderBy" value="${page.orderBy}"/>
        <input type="hidden" name="order" id="order" value="${page.order}"/>
        <input type="hidden" name="innName" value="${innName}"/>
    </form>
    <jsp:include page="../header_fragment.jsp"/>
    <div class="adjust-price-div">
        <security:authorize ifAnyGranted="ROLE_批量调价">
            <a class="batch-price-adjustment" id="batchPriceAdjust">批量调价</a>
        </security:authorize>
        <input id="innName"  class="inn-name" type="text" placeholder="输入客栈名称(模糊搜索)"/>
    </div>
    <%--<input id="city" type="hidden" style="width: 150px;">--%>
    <table class="kz-table">
        <tr>
            <th>目的地</th>
            <th>客栈名称</th>
            <th>分销商价格调整</th>
        </tr>
        <c:if test="${result!=null}">
            <c:forEach items="${result}" var="item">
            <tr>
                <td>${item.regionName}</td>
                <td>${item.innName}</td>
                <td>
                    <button onclick="detail(${item.proxyInnId});">调价设置</button>
                </td>
            </tr>
        </c:forEach></c:if>
    </table>
    <tags:pagination page="${page}" paginationSize="15"/>
</div>
<div id="VatchPriceAdjustment" class="center-box" style="display: none;width: 600px;margin-left:-300px;">
        <div class="center-box-in" style="width: 600px;padding:0 15px 15px 15px;">
            <a class="close-window" onclick="closeWindow('VatchPriceAdjustment')"></a>
            <h1>选择需批量调整价格的目的地</h1>
            <div id="selectPlace">
                <input type="hidden" id="place" style="width: 200px;">
            </div>
            <p style="margin: 10px 0">选择需要价格调整的分销商：<label for="checkAll" style="float: right"><input type="checkbox" class="check-all" id="checkAll">全选</label> </p>
            <div class="channel-from">
            </div>
            <div style="margin: 10px 0;">
                <input type="text" class="select-date start-date" placeholder="请选择开始日期" readonly="">
                至 <input type="text" class="select-date end-date" placeholder="请选择结束日期" readonly="">
                在卖价基础上
                <input type="text" class="crease-reduce-price">
            </div>
            <div style="text-align: center;">
                <button class="batch-price-adjustment" style="border: none;cursor: pointer;" id="enterPriceAdjust">确定调价</button>
            </div>
        </div>
</div>
<div id="enterPriceAdjustDialog" class="center-box" style="display: none;width: 600px;margin-left:-300px;">
    <div class="center-box-in" style="width: 600px;padding:0 15px 15px 15px;">
        <a class="close-window" onclick="cancelPriceAdjust()"></a>
        <h1>批量调整价格</h1>
        <div>
            <p class="enter-adjust-price-p">
                确认调整<label id="destination"></label>在<label id="startDate"></label>至<label id="endDate"></label>时间段;在老板给到的卖价基础上<label>全部</label>房型<label id="priceText">减5元</label>给到<label id="checkedChannel">乐活、旅行</label>
            </p>
        </div>
        <div style="text-align: center;">
            <button class="batch-price-adjustment" style="border: none;cursor: pointer;" id="enterPriceAdjust1">确定调价</button>
            <button class="batch-price-adjustment" style="border: none;cursor: pointer; background: #ccc;color: #000;" onclick="cancelPriceAdjust()">返回</button>
        </div>
    </div>
</div>
<div class="mask"></div>
<script type="application/javascript">
    $('#innName').keydown(function (e) {
        if (e.keyCode == 13) {
            var innName = $('#innName').val();
            window.location = "/proxysale/price/list?innName=" + innName;
        }
    });
    function detail(proxyInnId) {
        window.location = "/proxysale/price/detail?proxyInnId=" + proxyInnId;
    }

    function initSelect2Json(listId, jsonList, defaultMsg) {
        var userdata = [];
        $(jsonList).each(function (i, obj) {
            userdata[i] = {};
            userdata[i].id = obj.id || obj;
            userdata[i].text = obj.name || obj;
        });
        $("#" + listId).select2({
            placeholder : defaultMsg,
            data : userdata
        });
    }
    function closeWindow(id) {
        $("#"+id).hide();
        $(".mask").hide();
    }
    function cancelPriceAdjust() {
        $("#enterPriceAdjustDialog").hide();
        $("#VatchPriceAdjustment").show();
    }
    var channelId,channelName;
    $('#place').on('change', function (e) {
        channelId = $(this).val();
       // alert(channelId)
        channelName = $("#selectPlace .select2-choice span").html()
    })
    $("#batchPriceAdjust").on("click",function() {
        $(".channel-from").html("")
        $(".select-date").val("")
        $(".crease-reduce-price").val("")
        $("#checkAll").prop("checked",false)
        var url = "${ctx}/proxysale/price/regionName";
        $.post(url,function(data) {
           if(data && data.status==200) {
                var list = data.result;
                initSelect2Json("place",list,"请选择目的地")
           }else {
                alert("获取目的地失败！")
           }
        })
        var channelfromUrl = "${ctx}/proxysale/price/channelName"
        $.post(channelfromUrl,function(data) {
            if(data && data.status == 200) {
                $.each(data.result,function() {
                    $(".channel-from").append("<label><input type='checkbox' name='channel' value="+this.channelName+" data-channelId="+this.channelId+">"+this.channelName+"</label>")
                })
            }else {
                alert("获取渠道失败！")
            }
        })
        $("#VatchPriceAdjustment").show();
        $(".mask").show();
    })
    $("#enterPriceAdjust").on("click",function() {
        if(!channelName) {
            alert("请选择目的地！")
            return;
        }
        if(!$("input[name='channel']:checked").length) {
            alert("请至少选择一个渠道！")
            return;
        }
        if(!$(".start-date").val()) {
            alert("开始时间必须选择！")
            return;
        }
        if(!$(".end-date").val()) {
            alert("结束时间必须选择！")
            return;
        }
        var creaseReducePrice = $(".crease-reduce-price").val()
        var firstStr = creaseReducePrice.charAt(0)
        if(firstStr!="+" && firstStr != "-") {
            alert("调价只能输入正负数字")
            return;
        }
        var n = Number(creaseReducePrice);
        if (isNaN(n))
        {
            alert("调价只能输入正负数字");
            return;
        }
        $("#destination").html($("#selectPlace .select2-choice span").html())
        $("#startDate").html($( ".start-date").val())
        $("#endDate").html($( ".end-date").val())
        $("#priceText").html($(".crease-reduce-price").val()+"元")
        var str="";
        $.each($("input[name='channel']:checked"),function(key,val) {
            if(key == ($("input[name='channel']:checked").length-1)) {
                str += $(this).val();
            }else {
                str += $(this).val();
                str += ",";
            }
        })
        $("#checkedChannel").html(str)
        $("#enterPriceAdjustDialog").show();
        $("#VatchPriceAdjustment").hide();

    })
    $( ".start-date" ).datepicker({
        onClose: function( selectedDate ) {
            $( ".end-date" ).datepicker( "option", "minDate", selectedDate );
        },
        minDate :　new Date()
    });
    $( ".end-date" ).datepicker({
        onClose: function( selectedDate ) {
            $( ".start-date" ).datepicker( "option", "maxDate", selectedDate );
        }
    });
    $("#checkAll").on("click",function() {
        if(this.checked) {
            $("input[name='channel']").prop("checked",true)
        }else {
            $("input[name='channel']").prop("checked",false)
        }
    })
    $(".channel-from").on("click","input[name='channel']",function() {
        if($("input[name='channel']:checked").length == $("input[name='channel']").length) {
            $("#checkAll").prop("checked",true)
        }else {
            $("#checkAll").prop("checked",false)
        }
    })
    $("#enterPriceAdjust1").off().on("click",function() {
        $("#enterPriceAdjustDialog").hide();
        $(".mask").hide();
        var data = {
            regionId : $("#place").val(),
            channelIds : [],
            channelNames : [],
            from : $(".start-date").val(),
            to : $(".end-date").val(),
            extraPrice : $(".crease-reduce-price").val()
        }
        $.each($("input[name='channel']:checked"),function() {
            data.channelNames.push($(this).val());
            data.channelIds.push($(this).attr("data-channelid"))
        })
        var url = "${ctx}/proxysale/price/batchUpdatePrice";
        $.post(url,{jsonStr : JSON.stringify(data)},function(data) {
            if(data && data.status == 200) {
                alert("调价成功！")
                location.reload();
            }else {
                alert("调价失败！"+data.message)
                location.reload();
            }
        })
    })
</script>
</body>
</html>
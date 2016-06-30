<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html lang="zh-CN">
<head>
    <title>${proxyInn.innName}详情</title>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <link rel="shortcut icon" type="image/ico" href="http://assets.fanqiele.com/1.0.5/images/favicon.ico">
    <link rel="stylesheet" href="/css/proxysale/reset.css">
    <link rel="stylesheet" href="/css/proxysale/xzbackEnd.css">
    <link rel="stylesheet" href="/css/style.css">
    <link href="${ctx}/css/proxysale/jquery-ui-1.10.1.css" rel="stylesheet">
    <style>
        html, body {
            -webkit-user-select: none;
            -moz-user-select: none;
            -khtml-user-select: none;
            -ms-user-select: none;
        }

        .modify-price {
            padding: 5px 10px;
            float: right;
            margin: 5px 5px 0 0;
            cursor: pointer;
        }

        .channels button {
            padding: 5px 10px;
            margin-right: 1px;
            cursor: pointer;
        }

        .show-date {
            width: 80px;
            height: 24px;
            border: 1px solid #e0e0e0;
        }

        .prev, .step {
            cursor: pointer;
            font-size: 18px;
            padding: 5px;
        }

        .adjust-price {
            width: 600px;
            left: -300px;
            background: #fff;
        }

        .adjust-price h3 {
            padding: 10px;
            background: #F2F2F2;
        }

        .channel-from {
            padding: 10px;
        }

        .channel-from label {
            width: 130px;
            display: inline-block;
        }

        .detail .table td:first-child {
            width: 160px;
        }

        .channel-list-adjust-price .table td {
            text-align: center;
        }

        .channel-list-adjust-price .table td:first-child {
            width: 60px;
        }

        .channel-list-adjust-price .table ul {
            margin: 0;
            width: 100%;
        }

        .channel-list-adjust-price .table ul li {
            margin: 0;
        }

        .select-data, .crease-reduce-price {
            width: 110px;
            border: 1px solid #ccc;
            height: 24px;
            margin: 0 5px;
            padding-left: 10px;
            font-size: 12px;
        }

        .save-adjust-price {
            padding: 10px;
            text-align: center;
        }

        .save-adjust-price button {
            padding: 10px;
            width: 100px;
            color: #fff;
            background: #70A847;
            cursor: pointer;
        }

        .channels .active {
            background: #62933E;
            color: #fff;
        }
    </style>
    <script src="/js/proxysale/lib.min.js"></script>
    <script src="${ctx}/js/proxysale/jquery-ui-1.10.1.min.js"></script>
</head>
<body>
<div class="container-right">
    <input type="hidden" value="${proxyInn.id}" id="proxyInnId"/>
    <input type="hidden" value="${proxyInn.inn}" id="innId"/>
    <input type="hidden" value="${proxyInn.saleOuterId}" id="accountId"/>
    <input type="hidden" value="${proxyInn.innName}" id="innName"/>
    <jsp:include page="../header_fragment.jsp"/>
    <div>
        <span style="font-size: 25px;">${proxyInn.innName} </span>
        <span style="font-size: 12px">原价：客栈老板给出的卖价；   调价：运营加减价调整后的价格；</span>
        <span style="margin-right: 0px;font-size: 15px"><button class="modify-price">修改价格</button></span>
    </div>
    <div id="channels" class="channels"></div>
    <div id="detail" class="detail">
        <table class="table">
            <thead>
            <tr id="tableTitle">
                <td><a class="prev"><</a><input type="text" class="show-date" readonly><a class="step">></a></td>
                <%--<td>01-18今天</td>
                <td>01-19二</td>
                <td>01-20三</td>
                <td>01-21</td>
                <td>01-22</td>
                <td>01-23</td>
                <td>01-24</td>
                <td>01-25</td>
                <td>01-26</td>
                <td>01-27</td>
                <td>01-28</td>
                <td>01-29</td>--%>
            </tr>
            </thead>
            <tbody id="roomTypeAdjustPrice">

            </tbody>
        </table>
    </div>
</div>
<div id="dialogBlackBg" >
    <div class="center-box">
        <div class=" center-box-in audit-window adjust-price" style="display:none;" id="modifyPrice">
            <a class="close-window" id="modify-price-close"></a>

            <h3>选择需调整价格的分销商</h3>

            <div class="channel-from">
                <%-- <label>
                      <input type="checkbox" name="channel">乐活旅游
                  </label>--%>
            </div>
            <div class="channel-list-adjust-price">
                <table class="table">
                    <thead>
                    <tr>
                        <td>房型名称</td>
                        <td>
                            <ul>
                                <li>特殊价格策略设置</li>
                                <li>在客栈卖价基础上加减价；输入框中只能输入数字，根据“+，-”来区分价格增加减少。</li>
                            </ul>
                        </td>
                    </tr>
                    </thead>
                    <tbody id="roomTypeList">
                    <%--<tr>
                        <td>海景露台大床房1</td>
                        <td><input type="text" class="select-data">至 <input type="text" class="select-data">在卖价基础上<input type="text" class="crease-reduce-price"></td>
                    </tr>--%>
                    </tbody>
                </table>
            </div>
            <div class="save-adjust-price">
                <button id="saveAdjustPrice">保存</button>
            </div>
        </div>
    </div>
</div>

<script type="application/javascript">
    $(document).ready(function () {
        $(".show-date").val(getFormatDate());
        var oDateList = get30date(new Date());
        for (var i = 0; i < 15; i++) {
            $("#tableTitle").append("<td>" + oDateList[i].date + "</br>" + oDateList[i].weekDay + "</td>")
        }
        $.getJSON("/proxysale/price/getChannels?innId=" + $("#innId").val() + "&proxyInnId=" + $("#proxyInnId").val() + "&pricePattern=2", function (data) {
                    if (data.status != 200) {
                        alert("获取上线的分销商名称已失败，原因：" + data.message);
                    }
                    var html = '';
                    var result = data.result;
                    var channelId;
                    $(result).each(function () {
                        var channelName = this.channelName;
                        channelId = this.channelId;
                        var iBut = "<button>" + channelName + "</button> " + "<input type='text'style='display:none' value=" + channelId + ">";
                        html = html + iBut;
                    });
                    $("#channels").html(html);
                    $("#channels").find("button").eq(0).addClass("active");
                    getRoomTypeChannelPrice($("#channels .active").next().val(), $("#proxyInnId").val());
                    /*渠道选项卡切换*/
                    for (var i = 0; i < $("#channels > button").length; i++) {
                        $("#channels > button").eq(i).off().on("click", function () {
                            $("#channels").find("button").removeClass("active");
                            $(this).addClass("active");
                            $("#roomTypeAdjustPrice").html("");
                            getRoomTypeChannelPrice($(this).next().val(), $("#proxyInnId").val());
                        })
                    }
                    $(".modify-price").on("click", function () {
                        adjustmentPrice();
                    })
                    $("#modify-price-close").on("click", function () {
                        close(0);
                    })
                    $("#saveAdjustPrice").off().on("click", function () {
                        if (!$("input[name='channel']:checked").length) {
                            alert("请至少勾选一个需要调整的分销商");
                            return
                        }
                        /*至少修改一个渠道价格判断*/
                        var str = "";
                        for (var i = 0; i < $(".crease-reduce-price").length; i++) {
                            str += $(".crease-reduce-price").eq(i).val();
                            str += $(".select-data").eq(2 * i).val();
                            str += $(".select-data").eq(2 * i + 1).val();
                        }
                        if (str == "") {
                            alert("请至少修改一个渠道价格1");
                            return;
                        }
                        var roomList = [];
                        for (var i = 0; i < $("#roomTypeList > tr").length; i++) {
                            var selectStartDate = $(".select-data").eq(2 * i).val();
                            var selectEndDate = $(".select-data").eq(2 * i + 1).val();
                            var creaseReducePrice = $(".crease-reduce-price").eq(i).val();
                            if(Date.parse(selectStartDate) > Date.parse(selectEndDate)) {
                                alert("开始时间不能晚于截止时间");
                                return;
                            }
                            var firstStr = creaseReducePrice.charAt(0);
                            if (selectStartDate && selectEndDate && creaseReducePrice) {
                                if (firstStr != "+" && firstStr != "-") {
                                    alert("请输入正负号来区分加价减价");
                                    return;
                                }
                                var jsonData = {};
                                jsonData.extraPrice = creaseReducePrice;
                                jsonData.from = selectStartDate;
                                jsonData.to = selectEndDate;
                                jsonData.otaRoomTypeId = $(".select-data").eq(2 * i).parent().prev().children("input").val();
                                jsonData.otaRoomTypeName = $(".select-data").eq(2 * i).parent().prev().children("span").html();
                                roomList[i] = JSON.stringify(jsonData);

                            } else if (!selectStartDate && !selectEndDate && !creaseReducePrice) {
                                continue;
                            } else if (!selectStartDate || !selectEndDate || !creaseReducePrice) {
                                alert("请将信息填写完整！");
                                return;
                            }
                        }
                        replaceEmptyItem(roomList);
                        var objData = {};
                        objData.accountId = parseInt($("#accountId").val());
                        objData.innName = $("#innName").val();
                        objData.roomList = "[" + roomList.toString() + "]";
                        var otaList = [];
                        var j = 0;
                        for (var i = 0; i < $("input[name='channel']").length; i++) {
                            var isChecked = $("input[name='channel']").eq(i).is(':checked')
                            if (!isChecked) {
                                continue;
                            }
                            var Key = $("#channels > input").eq(i).val();
                            var Value = $("#channels > button").eq(i).html();
                            var json = {};
                            json[Key] = Value;
                            otaList[j] = JSON.stringify(json);
                            j++
                        }
                        objData.otaList = "[" + otaList.toString() + "]";
                        var data = JSON.stringify(objData);
                        var url = "/proxysale/price/doUpdate";
                        $.post(url, objData, function (rs) {
                            if (!(rs.status == 200)) {
                                alert(rs.message);
                                return;
                            }

                            alert("修改价格成功！");
                            close(0);
                        })
                    })
                }
        );

        //上一页
        var curTime;
        $("#tableTitle").on("click", ".prev", function () {
            curTime = $('.show-date').val();
            if (curTime == getFormatDate()) {
                return;
            }
            var time = TC.date(curTime) - 0;
            var timeNext = new Date(time - 86400000 * 15);
            var oDateList = get30date(timeNext);
            $("#tableTitle").html("<td><a class='prev'><</a><input type='text' class='show-date' readonly><a class='step'>></a> </td>");
            for (var i = 0; i < 15; i++) {
                $("#tableTitle").append("<td>" + oDateList[i].date + "</br>" + oDateList[i].weekDay + "</td>")
            }
            $('.show-date').val(getFormatDate(timeNext));
            prevOrStep($('.show-date').val(), getFormatDate(new Date((TC.date(timeNext) - 0) + 86400000 * 14)));
        })
        //下一页
        $("#tableTitle").on("click", ".step", function () {
            curTime = $('.show-date').val();
            var time = TC.date(curTime) - 0;
            console.log(time);
            var timeLast = new Date(time + 86400000 * 15);
            var oDateList = get30date(timeLast);
            $("#tableTitle").html("<td><a class='prev'><</a><input type='text' class='show-date' readonly><a class='step'>></a> </td>");
            for (var i = 0; i < 15; i++) {
                $("#tableTitle").append("<td>" + oDateList[i].date + "</br>" + oDateList[i].weekDay + "</td>")
            }
            $('.show-date').val(getFormatDate(timeLast));
            prevOrStep($('.show-date').val(), getFormatDate(new Date((TC.date(timeLast) - 0) + 86400000 * 14)));
        })
        function prevOrStep(from, to) {
            $.getJSON("/proxysale/price/roomDetail?channelId=" + $("#channels > .active").next().val() + "&proxyInnId=" + $("#proxyInnId").val() + "&from=" + from + "&to=" + to, function (rs) {

            })
        }



        function replaceEmptyItem(arr) {
            for (var i = 0, len = arr.length; i < len; i++) {
                if (!arr[i] || arr[i] == '') {
                    arr.splice(i, 1);
                    len--;
                    i--;
                }
            }
        }

        function getRoomTypeChannelPrice(channelId, proxyInnId) {
            $.getJSON("/proxysale/price/roomDetail?channelId=" + channelId + "&proxyInnId=" + proxyInnId,
                    function (data) {
                        var result = data.result;
                        if (result.status != 200) {
                            alert("获取房型价格失败，原因：" + result.message);
                        }
                        var list = result.list;
                        var html = "";
                        $(list).each(function () {
                            var temp = "<tr><td><span>" + this.roomTypeName + "</span>" + "<input type='text' style='display:none' value='" + this.roomTypeId + "'></td>";
                            for (var i = 0; i < 15; i++) {
                                temp += "<td>原价：" + this.roomDetail[i].originalPrice + "调价：" + this.roomDetail[i].operatorPrice + "</td>";
                            }
                            temp += "</tr>";
                            html = html + temp;
                        });
                        $("#roomTypeAdjustPrice").append(html);
                    }
            );
        }

        function adjustmentPrice() {
            $("#dialogBlackBg").show();
            $("#modifyPrice").show();
            $(".channel-from").html("");
            $("#roomTypeList").html("");
            var channelArrayList = [];
            var channelFromHtml = "";
            var roomTypeArrayList = [];
            var roomTypeId = [];
            var roomTypeListHtml = "";
            for (var i = 0; i < $("#channels > button").length; i++) {
                channelArrayList[i] = $("#channels > button").eq(i).html();
                channelFromHtml += ("<label><input type='checkbox' name='channel'>" + channelArrayList[i] + "</label>")
            }
            $(".channel-from").append(channelFromHtml);
            for (var i = 0; i < $("#roomTypeAdjustPrice > tr").length; i++) {
                roomTypeArrayList[i] = $("#roomTypeAdjustPrice > tr").eq(i).find("td:first-child").find("span").html();
                roomTypeId[i] = $("#roomTypeAdjustPrice > tr").eq(i).find("td:first-child").find("input").val();
                roomTypeListHtml += "<tr><td><span>" + roomTypeArrayList[i] + "</span>" + "<input type='text' style='display:none' value='" + roomTypeId[i] + "'></td>";
                roomTypeListHtml += "<td><input type='text' class='select-data' placeholder='请选择开始日期' readonly>至 <input type='text' class='select-data' placeholder='请选择结束日期' readonly>在卖价基础上<input type='text' class='crease-reduce-price'></td></tr>"
            }
            $("#roomTypeList").append(roomTypeListHtml);
            $("#roomTypeList .select-data").datepicker(
                    {
                        minDate: new Date()
                    }
            );
        }

        function close(num) {
            if (num == 0) {
                $("#dialogBlackBg").hide();
                $("#modifyPrice").hide();
            }
        }

        function get30date(date) {
            var arrDate = [];
            var firstDate = TC.date(date) - 0;
            var today = new Date;
            var todayY = today.getFullYear();
            var todayM = today.getMonth() + 1;
            var todayD = today.getDate();
            for (var i = 0; i < 15; i++) {
                var date = new Date(firstDate + i * 86400000);
                var year = date.getFullYear();
                var month = date.getMonth() + 1;
                var day = date.getDate();
                var weekDay = '日一二三四五六'[date.getDay()];
                month = month < 10 ? '0' + month : month;
                day = day < 10 ? '0' + day : day;
                var isHoliday = TC.getHoliday(year + '-' + month + '-' + day);
                if (year == todayY && month == todayM && day == todayD) {
                    isHoliday = month + '-' + day + '</br>' + '今天';
                    weekDay = "";
                }
                arrDate.push({date: isHoliday || (month + '-' + day), isHoliday: isHoliday ? 1 : 0, weekDay: weekDay})
            }
            console.log(arrDate);
            return arrDate;
        }

        function getFormatDate(date) {
            if (!date) {
                var date = new Date();
            }
            var seperator1 = "-";
            var year = date.getFullYear();
            console.log(year + "----" + date);
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
    });

  /*  var divTitle=$('.center-box');*/
   //可拖动
    $('.center-box').draggable();
   /* divTitle.draggable().click(function ()
    {
        $(this).draggable({ disabled: false });
        $(this).css('backgroundColor', 'transparent');
    }).dblclick(function ()
    {
        $(this).draggable({ disabled: true });
        $(this).css('backgroundColor', '#FFFF6F');
    });*/

</script>
</body>
</html>

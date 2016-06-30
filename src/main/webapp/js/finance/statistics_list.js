/**
 * Created by dev on 2015/12/24.
 */
$(function(){
    // 客栈排行Top10 表格切换
    var btn = $('.tabs-nav input[type=radio]')
    var tabsBox = $('.tabs-box')

    btn.click(function(){
        var index = $(this).data('index')
        tabsBox.hide()
        tabsBox.eq(index).fadeIn()
    })

    // 筛选条件事件绑定
    $('#channelId, #isAcceptedOnly').on('change', function(){
        syncData()
    })
});

/*更新数据*/
var syncData = function(){
    var startDate = $('.t-start').val(),
        endDate = $('.t-end').val(),
        channel = $('#channelId option:selected').val(),
        channelName = $('#channelId option:selected').text(),
        isAcceptedOnly = $('#isAcceptedOnly').prop('checked')

    if(new Date(startDate) > new Date(endDate)){
        alert('开始日期不能大于结束日期哦')
        return
    }
    // 更新隐&&提交隐藏域数据
    var form = $('#mainForm')
    form.find('input[name=beginDate]').val(startDate)
    form.find('input[name=endDate]').val(endDate)
    form.find('input[name=channelId]').val(channel)
    form.find('input[name=channelName]').val(channelName)
    form.find('input[name=isAcceptedOnly]').val(isAcceptedOnly)

    form.submit()
}

var initGraphic = function(postData){
    require.config({
        paths: {
            echarts: '/js/chart/dist'
        }
    });
    require(
        [
            'echarts',
            'echarts/chart/line',   // 按需加载所需图表，如需动态类型切换功能，别忘了同时加载相应图表
            'echarts/chart/bar'
        ],
        function (ec) {
            var myChart = ec.init(document.getElementById('main'));
            var option = {
                tooltip : {
                    trigger: 'axis'
                },
                toolbox: {
                    show : true,
                    feature : {
                        mark : {show: true},
                        dataView : {show: true, readOnly: true},
                        magicType: {show: true, type: ['line', 'bar']},
                        restore : {show: true},
                        saveAsImage : {show: true}
                    }
                },
                calculable : true,
                legend: {
                    data:['订单金额','订单量','间夜量']
                },
                xAxis : [
                    {
                        type : 'category',
                        data : []
                    },

                ],
                yAxis : [
                    {
                        type : 'value',
                        name : '间夜量/订单量',
                        axisLabel : {
                            formatter: '{value} '
                        }
                    },
                    {
                        type : 'value',
                        name : '订单金额',
                        axisLabel : {
                            formatter: '{value} 元'
                        }
                    }
                ],
                series : [
                    {
                        itemStyle: {
                            normal: {
                                label: {
                                    show: true,
                                    textStyle: {
                                        color: '#800080'
                                    }
                                }
                            }
                        },

                        name:'订单金额',
                        type:'bar',
                        yAxisIndex: 1,
                        data:['1']
                    },
                    {
                        name:'订单量',
                        type:'line',
                        data:['1']
                    },
                    {
                        name:'间夜量',
                        type:'line',
                        data:['1']
                    }
                ]
            };

            // 载入动画---------------------
            myChart.showLoading({
                text: '正在努力的读取数据中...',    //loading话术
            });

            $.ajax({
                url: ctx + '/data/getBarData',
                data: {beginDate: postData.beginDate, endDate: postData.endDate, channelId: postData.channelId, isAcceptedOnly: postData.isAcceptedOnly, channelName: postData.channelName},
                success:function(data){
                    //数据接口成功返回
                    //后台需要返回以下结构的json数据
                    var xAxisData=[];
                    var seriesData=[];
                    if(data != null){
                        if(data['axis'].length > 0) {
                            xAxisData=data['axis'];
                        }
                        if(data['series'].length == 3) {
                            seriesData.push({ 'name': "订单金额", 'type': 'bar', 'data': data['series'][0]});
                            seriesData.push({ 'name': "订单量", 'type': 'line', 'data': data['series'][1]});
                            seriesData.push({ 'name': "间夜量", 'type': 'line', 'data': data['series'][2]});
                        }
                        option.xAxis[0]['data']=xAxisData;
                    }
                    myChart.setOption(option);
                    myChart.setSeries(seriesData);
                },
                error:function(){
                    //数据接口异常处理
                    var xAxisData=[''];
                    var seriesData = [
                        {
                            name:'',
                            type: 'line',
                            data: [0]
                        }
                    ];
                    option.xAxis[0]['data']=xAxisData;

                    myChart.setSeries(seriesData);
                    myChart.setOption(option);

                },
                complete:function(){
                    //不管数据接口成功或异常，都终于载入提示
                    //停止动画载入提示
                    myChart.hideLoading();
                }
            });
        }
    )
}

// 页面打开后初始化数据
function firstRequest(){
    var startDate = $('.t-start').val(),
        endDate = $('.t-end').val(),
        channel = $('#channelId option:selected').val(),
        channelName = $('#channelId option:selected').text(),
        isAcceptedOnly = $('#isAcceptedOnly').prop('checked')

    var postData = {
        beginDate: startDate,
        endDate: endDate,
        channelId: channel,
        channelName: channelName,
        isAcceptedOnly: isAcceptedOnly
    }

    initGraphic(postData)
}
$(function(){
    firstRequest()
})




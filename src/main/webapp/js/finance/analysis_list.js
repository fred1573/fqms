/**
 * Created by dev on 2015/12/25.
 */
var syncData = function(){
    var startDate = $('.t-start').val(),
        endDate = $('.t-end').val(),
        channel = $('#channelId option:selected').val()

    if(new Date(startDate) > new Date(endDate)){
        alert('开始日期不能大于结束日期哦')
        return
    }

    // 更新隐&&提交隐藏域数据
    var form = $('#mainForm')
    form.find('input[name=beginDate]').val(startDate)
    form.find('input[name=endDate]').val(endDate)
    form.find('input[name=channelId]').val(channel)

    form.submit()
}

require.config({
    paths: {
        echarts: '/js/chart/dist'
    }
})

function chart(data){
    require(
        [
            'echarts',
            'echarts/chart/pie',   // 按需加载所需图表，如需动态类型切换功能，别忘了同时加载相应图表
        ],
        function (ec) {

            var myChart = ec.init(document.getElementById('main'));
            var keys = [];
            var values = [];
            indexdata = {category: ["已取消", "验证失败", "已接收"], total: ["2", "1", "11"]};
            var option = {
                title: {
                    text: '订单状态占比',

                    x: 'center'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient: 'vertical',
                    x: 'left',
                    data: data['keys'] /*['直接访问','邮件营销','联盟广告','视频广告','搜索引擎']*/
                },
                toolbox: {
                    show: true,
                    feature: {
                        mark: {show: true},
                        dataView: {show: true, readOnly: false},
                        magicType: {
                            show: true,
                            type: ['pie', 'funnel'],
                            option: {
                                funnel: {
                                    x: '25%',
                                    width: '50%',
                                    funnelAlign: 'left',
                                    max: 1548
                                }
                            }
                        },
                        restore: {show: true},
                        saveAsImage: {show: true}
                    }
                },
                calculable: true,

                series: [
                    {
                        name: '访问来源',
                        type: 'pie',
                        radius: '55%',
                        center: ['50%', '60%'],
                        data: (function () {
                            var res = [];
                            var len = 0;
                            for (var i = 0, size = data['values'].length; i < size; i++) {
                                res.push({
                                    name: data['keys'][i],
                                    value:data['values'][i]
                                });
                            }
                            return res;
                        })()

                    }
                ]
            };
            var keys;



            myChart.setOption(option);
            // 载入动画---------------------

        }
    );
}

function chart1(data){
    require(
        [
            'echarts',
            'echarts/chart/pie',   // 按需加载所需图表，如需动态类型切换功能，别忘了同时加载相应图表
        ],
        function (ec1) {

            var myChart1 = ec1.init(document.getElementById('main1'));
            var keys = [];
            var values = [];

            var option1 = {
                title: {
                    text: '下单预定时间段分布',
                    x: 'center'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient: 'vertical',
                    x: 'left',
                    data: data['keys'] /*['直接访问','邮件营销','联盟广告','视频广告','搜索引擎']*/
                },
                toolbox: {
                    show: true,
                    feature: {
                        mark: {show: true},
                        dataView: {show: true, readOnly: false},
                        magicType: {
                            show: true,
                            type: ['pie', 'funnel'],
                            option: {
                                funnel: {
                                    x: '25%',
                                    width: '50%',
                                    funnelAlign: 'left',
                                    max: 1548
                                }
                            }
                        },
                        restore: {show: true},
                        saveAsImage: {show: true}
                    }
                },
                calculable: true,

                series: [
                    {
                        name: '下单预定时间段分布',
                        type: 'pie',
                        radius: '55%',
                        center: ['50%', '60%'],
                        data: (function () {
                            var res = [];
                            var len = 0;
                            for (var i = 0, size = data['values'].length; i < size; i++) {
                                res.push({
                                    name: data['keys'][i],
                                    value:data['values'][i]
                                });
                            }
                            return res;
                        })()

                    }
                ]
            };
            var keys;



            myChart1.setOption(option1);
            // 载入动画---------------------

        }
    );
}

$(function(){
    // 筛选条件事件绑定
    $('#channelId').on('change', function(){
        syncData()
    })

    var startDate = $('.t-start').val(),
        endDate = $('.t-end').val(),
        channel = $('#channelId option:selected').val(),
        postData = {
            beginDate: startDate,
            endDate: endDate,
            channelId: channel
        }

    $.ajax({
        url: '/data/getPieData',
        data: {beginDate: postData.beginDate, endDate: postData.endDate, channelId: postData.channelId},
        success: function (data) {
            chart(data);
            $('#chart').html(template('dataTpl', data))
        }
    });

    $.ajax({
        url: '/data/getPieDataByTime',
        data: {beginDate: postData.beginDate, endDate: postData.endDate, channelId: postData.channelId},
        success: function (data) {
            chart1(data);
            $('#chart1').html(template('dataTpl', data))
        },
    });
})



require.config({
    paths: {
        echarts: '/js/chart/dist'
    }
});
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

$.ajax({
    url: '/data/getPieDataByTime',
    data: {beginDate: "2015-11-12", endDate: "2015-12-24", channelId: 903, isAcceptedOnly: false},
    success: function (data) {
        chart1(data);
    },
});
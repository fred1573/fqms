/**
 * Created by dev on 2015/12/25.
 */
var syncData = function () {
    var startDate = $('.t-start').val(),
        endDate = $('.t-end').val();
    if (new Date(startDate) > new Date(endDate)) {
        alert('开始日期不能大于结束日期哦')
        return
    }
}
//删除左右两端的空格
function trim(str) {
    return str.replace(/(^\s*)|(\s*$)/g, "");
}

var timer = null
var progressBar = (function(){
        var progressFn = function(obj1, obj2, num){
            obj1.style.clip = 'rect(0 ' + num + 'px 40px 0)'
            var percent = parseInt(num / 3)
            obj1.innerHTML = percent + '%'
            obj2.innerHTML = percent + '%'
            if (num >= 290) {
                clearInterval(timer)
            }
        }
        var clip = {
            _start: function() {
                var num = 0
                timer = setInterval(function() {
                    if (num >= 290) {
                        clearInterval(timer)
                    } else {
                        num += Math.floor(Math.random() * 10)
                        num = num > 290 ? 290 : num
                        progressFn(document.getElementById('progressBar'), document.getElementById('progressText'), num)
                    }
                }, 400)
            },
            _completed: function() {
                progressFn(document.getElementById('progressBar'), document.getElementById('progressText'), 300)
                document.getElementById('progressBar').innerHTML = '账期创建成功!'
            }
        }
        return clip
})()
function addPeriod() {
    var startDate = trim($('.t-start').val());
    var endDate = trim($('.t-end').val());
    if (startDate == '' || startDate == null || startDate == undefined) {
        alert("账期开始时间不能为空");
        return false;
    }
    if (endDate == '' || endDate == null || endDate == undefined) {
        alert("账期结束时间不能为空");
        return false;
    }
    if (confirm("是否确认创建【" + startDate + "】至【" + endDate + "】的账期")) {
        // 禁用按钮
        $("#grp").attr("disabled", true);
        $("#progress").toggle();
        progressBar._start();
        $.get("/period/create?beginDate=" + startDate + "&endDate=" + endDate, function (data) {
            if(data.status == 200) {
                progressBar._completed()
                setTimeout(function(){
                   window.location.reload();
                },2000)
            } else {
                $("#progress").toggle();
                alert(data.message);
                window.location.reload();
            }
        });
        // 启用按钮
        $('#grp').removeAttr("disabled");
        //window.location.reload();
    }
}

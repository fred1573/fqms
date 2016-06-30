$(document).ready(function () {

    $(function () {
        $(".start-time").datepicker({
            onClose: function (selectedDate) {
                $(".end-time").datepicker("option", "minDate", selectedDate);
            }
        });
        $(".end-time").datepicker({
            onClose: function (selectedDate) {
                $(".start-time").datepicker("option", "maxDate", selectedDate);
            }
        });
    });
    $('.date-line').datepicker({});

    $("#search_submit").bind("click", function () {
        search();
    });
});

function search() {
    $("input[name=]")
}

function exportInn() {
    var activityId = $("input[name=activityId]").val();
    if (confirm("您确定要导出？")) {
        $.post(ctx + "/activity/export", {activityId: activityId}, function () {
            alert("生成完毕!");
        });
    }
}

function agree(innId) {
    var activityId = $("input[name=activityId]").val();

    if (confirm("确定同意？")) {
        $.post(ctx + "/activity/isAgree", {activityId: activityId, innId: innId, status: "2"}, function () {
            alert("操作成功!");
            location.reload();
        });
    }
}
function refuse() {
    var activityId = $("input[name=activityId]").val();

    if (confirm("确定拒绝？")) {
        $.post(ctx + "/activity/isAgree", {activityId: activityId, innId: innId, status: "3"}, function () {
            alert("操作成功!");
            location.reload();
        });
    }
}

function agreeAll() {
    var activityId = $("input[name=activityId]").val();
    if (confirm("确定一键同意？")) {
        $.post(ctx + "/activity/agreeAll", {activityId: activityId}, function () {
            alert("操作成功!");
            location.reload();
        });
    }
}
function formBtn() {
    var name = $('input[name=activityName]').val();
    var file = $('input[name=file]').val();
    var dateLine = $('input[name=dateLine]').val();
    var startTime = $('input[name=startTime]').val();
    var endTime = $('input[name=endTime]').val();
    var content = $('textarea[name=content]').val();
    var require = $('textarea[name=require]').val();
    var coverPicture = $('input[name=coverPicture]').val();
    if (name == null || name == '') {
        alert("活动名称不能为空");
        return false;
    }
    if (file == null || file == '') {
        if(coverPicture == null || coverPicture == ''){
            alert("活动封面不能为空");
            return false;
        }

    }
    if (dateLine == null || dateLine == '') {
        alert("报名截止时间不能为空");
        return false;
    }
    if (endTime == null || endTime == '' || startTime == null || startTime == '') {
        alert("活动时间不能为空");
        return false;
    }
    if (content == null || content == '') {
        alert("活动内容不能为空");
        return false;
    }
    if (require == null || require == '') {
        alert("参加要求不能为空");
        return false;
    }
    return true;
}
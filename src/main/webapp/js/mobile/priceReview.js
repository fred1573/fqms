var json = JSON.parse( $('#content_data').attr('data-content'))[0]
json.priceRecordJsonBeanVo.forEach(function(el){
	var weekPrice = ['','','','','','','']
	el.weekPriceVoList.forEach(function(h){
		weekPrice[h.weekDate-1] = h.weekSellingPrice
	})
	el.weekPriceVoList = weekPrice
})
$('#priceDetail').html( template('data', json) )

function setEvent(){
	document.addEventListener('touchmove', PreventDefault, false);	
}
function removeEvent(){
	document.removeEventListener('touchmove', PreventDefault, false);	
}
function PreventDefault(event){
	event.preventDefault();
}

var $oBtns = $('footer> div')
// 弹出'审核通过'提示框
$oBtns.eq(0).tap(function(){
	$('#tips_panel_1').show()
	$('#tips-mask').css({'top': document.body.scrollTop+"px"}).show()
	setEvent();
})

// 弹出'拒绝通过'提示框
$oBtns.eq(1).tap(function(){
	$('#tips_panel_2').show()
	$('#tips-mask').css({'top': document.body.scrollTop+"px"}).show()
	setEvent();
})

// 取消操作
$('.cancel').tap(function(){
	$('#tips_panel_1, #tips_panel_2').hide()
	$('#tips-mask').hide()
	removeEvent()
})

// 通过审核
$('#btn_pass').tap(function(){
	$.ajax({
    	type: "post",
    	url: "/proxysale/inn/price/checkout/" + json.recordCode,
    	data: {
			status: json.status,
			innId:json.innId,
			pattern:json.pattern
		},
    	success: function(rs){
    		$('#tips-mask').hide()
    	}
    })
})

// 拒绝通过
$('#btn_refuse').tap(function(){
	$.ajax({
    	type: "post",
		url: "/proxysale/inn/price/checkout/" + json.recordCode,
		data: {
			status: json.status,
			innId:json.innId,
			reason:$('#refuseReason').val(),
			pattern:json.pattern
		},
    	success: function(rs){
    		$('#tips-mask').hide()
    	}
    })
})
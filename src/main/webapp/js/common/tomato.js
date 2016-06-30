// JavaScript Document
$(function(){
	//绑定下拉事件
	selectDown($(".down-select"));
	//绑定头部下拉事件
	selectDown($("em.down-select-table"));
	//设置整体位置
	setContainerMargin();
})

$(window).resize(function() {
  //设置整体位置
	setContainerMargin();
});

/**
*	下拉选择
*	传入点击的对象名称
*   @return 选中的内容
*/
function selectDown($objSelectButton){
	//绑定点击按钮事件
	$objSelectButton.bind("click",function(){
		var $objOl=$(this).next("ol");//获取当前点击对象对应的ol对象
		var $objHead=$(this).prev("span.select-box");//获取当前点击对象对应的显示值
		var $objOlli=$objOl.find("li:not(#add-name)");
		$objOl.show(300);
		//绑定点击li事件,唯独不绑定id=add-name的li
		 $objOlli.bind("click",function(){
			var $this = $(this);
			var selectName=$this.html();//获取li里面的name
			$objHead.html(selectName);
			$objHead.attr("data",selectName);
			$objOl.hide(300);
			$objOlli.unbind();//选择后解除改绑定事件
		});
	});
}

/**
*	设置container的margin-top
*/
/*function setContainerMargin(){
	var windowsHeight=$(window).height();
	//var windowsWidth=$(window).width();
	var $container=$(".container");
	var marginHeight=windowsHeight-$container.height();
	if(marginHeight>0){
		$container.css("margin-top",marginHeight/2);
	}
	else $container.css("margin-top","0");
}*/
function setContainerMargin(){
	var windowsHeight=$(window).height();
	//var windowsWidth=$(window).width();
	var $container=$(".container");
	var marginHeight=windowsHeight-$container.height();
	if(marginHeight>0){
		$container.css("margin-top",0);
		$('.container').css('height',windowsHeight);
	}
	else {
		$container.css("margin-top","0");$('.container').css('height',windowsHeight)
	}
}

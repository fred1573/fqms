/* ========================================================================
 *	tc: popups 扩展
 *	
 * 	弹窗
 * 
 *  author: candy
 *
 *  version 0.0.1
 *
 * 结构：.ui-popups 
 *          .ui-popups-dialog
 *          .close
 *          .ui-popups-head
 *              .ui-popups-title
 *          .ui-popups-body
 *              .ui-popups-content
 *          .ui-popups-foot
 *
 * 
 * <div class="ui-popups">
        <div class="ui-popups-dialog">
            <em class="close" data-dismiss="popups">x</em>
            <div class="ui-popups-head">
                <h3 class="ui-popups-title">弹窗标题</h3>
            </div>
            <div class="ui-popups-body">
                <div class="ui-popups-content">
                    <p>确认删除吗？xxxxxx</p>
                </div>
            </div>
            <div class="ui-popups-foot">
                <button class="btn btn-success" data-dismiss="popups">取消</button>
            </div>
        </div>
    </div>
 *
 *  标记：
 *  data-dismiss="popups"的元素点击会关闭父级弹窗
 *  data-ui="popups" data-target="#xxx"的元素点击会弹出#xxx元素弹窗
 *  
 *  $('#xxx').popups({scope: '#popups_dialog'}); // 手动调用
 * backdrop：显示背景遮罩层 默认true
 * scope：背景层范围（嵌套弹窗时用） 选择器
 * 
 *
 *
 * 回调：分别指显示前后，隐藏前后的回调， e.preventDefault(); // 阻止后续事件运行
 * 
 * show.tc.popups 
 * shown.tc.popups
 * hide.tc.popups
 * hiden.tc.popups
 * 
 *  
 * ======================================================================== */

;!function ($) {
	'use strict';
	var flag = '[data-ui="popups"]';
	var Popups = function (element, options) {
		this.options = options
	    this.$element = $(element)
	    this.isShown = false
	    this.$backdrop = null
  	}

    Popups.VERSION  = '0.0.1'

  	Popups.DEFAULTS = {
		backdrop: true
	}

  	Popups.prototype.show = function (_relatedTarget) {
  		var e = $.Event('show.tc.popups', { relatedTarget: _relatedTarget })

    	this.$element.trigger(e)

    	if (this.isShown || e.isDefaultPrevented()) return;

    	this.isShown = true
  		this.$element.on('click.dismiss.tc.popups', '[data-dismiss="popups"]', $.proxy(this.hide, this))
  		this.backdrop()
  		this.$element.show().css('visibility', 'visible');

  		e = $.Event('shown.tc.popups', { relatedTarget: _relatedTarget })
  		this.$element.trigger(e)

  	}

  	Popups.prototype.hide = function (e) {
  		e = $.Event('hide.tc.popups')
    	this.$element.trigger(e)

    	if (!this.isShown || e.isDefaultPrevented()) return;

  		this.$element.hide().off('click.dismiss.tc.popups')
  		this.isShown = false

  		e = $.Event('hiden.tc.popups')
  		this.$element.trigger(e)
  	}

  	Popups.prototype.toggle = function (_relatedTarget) {
  		return this.isShown ? this.hide() : this.show(_relatedTarget)

  	}


  	Popups.prototype.backdrop = function () {
  		if (!this.options.backdrop) {
  			this.removeBackdrop()
  			return;
  		}
  		if (!this.$backdrop && this.options.backdrop)  {
  			this.$backdrop = $('<div class="ui-popups-backdrop" />').prependTo(this.$element)
  			// 覆盖指定区域
  			if (this.options.scope) {
  				var scope = this.options.scope
  				if (! $(scope).length) return;
  				var $scope = $(scope),
  					pos = $scope.position(),
  					w = $scope.width(),
  					h = $scope.height();
  				this.$backdrop.css({
  					top: pos.top,
  					left: pos.left,
  					bottom: 'auto',
  					right: 'auto',
  					width: w,
  					height: h
  				})

  			}
  		}
  	}

  	Popups.prototype.removeBackdrop = function () {
	    this.$backdrop && this.$backdrop.remove()
	    this.$backdrop = null
	}

	// PLUG 定义
    // ==========================
  	function Plugin(option, params) {
	    return this.each(function () {
			var $this = $(this)
			var data  = $this.data('tc.popups')
			var options = $.extend({}, Popups.DEFAULTS, $this.data(), typeof option == 'object' && option)
			if (!data) 
				$this.data('tc.popups', (data = new Popups(this, options) ) )
			if (typeof option === 'string') 
				data[option](params)
			else 
				data.show(params)
	    });
	}

	$.fn.popups = Plugin;
  	$.fn.popups.Constructor = Popups;

  	// 绑定默认
    // ===================================
  	$(document).on('click.tc.popups', flag, function (e) {
  		var $this = $(this)
  		var $target = $( $this.attr('data-target') )
  		var option = $target.data() || 'toggle'

  		if ($this.is('a')) e.preventDefault()

  		Plugin.call($target, option, this)
  	});

}(jQuery);
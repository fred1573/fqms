<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1,minimum-scale=1, user-scalable=no">
	<link rel="stylesheet" type="text/css" href="/css/mobile/priceReview.css">
	<title>price review</title>
</head>
<body>
<div class="main">
	<section id="priceDetail"></section>
	<footer>
		<div class="btn">通过</div>
		<div class="btn">拒绝</div>
	</footer>
</div>

<!-- 弹出框 -->
<div id="tips-mask">
	<div class="tips-panel" id="tips_panel_1">
		<p>确定审核通过？</p>
		<div class="dividing-line"></div>
		<div class="btn-box">
			<span class="btn cancel">取消</span>
			<span class="btn" id="btn_pass">确定</span>
		</div>
	</div>
	<div class="tips-panel" id="tips_panel_2">
		<p>请输入拒绝原因：</p>
		<textarea rows="5" id="refuseReason"></textarea>
		<div class="btn-box">
			<span class="btn cancel">取消</span>
			<span class="btn" id="btn_refuse">确定</span>
		</div>
	</div>
</div>
<input type="hidden" id="content_data" data-content='${contentData}'/>
<script type="text/html" id="data">
	<?if(otaLink){?>	
		<dl>
			<dd>
				<i></i>
				<a href="<?= otaLink?>" target="_blank">OTA链接</a>
			</dd>
		</dl>
	<?}?>
	<?for(var i=0; i<priceRecordJsonBeanVo.length; i++){?>
		<table>
			<thead>
				<tr>
					<th colspan="8"><?= priceRecordJsonBeanVo[i].roomTypeName?></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td width="18%">默认价</td>
					<td colspan="7" class="price-td"><?= priceRecordJsonBeanVo[i].defaultPrice?></td>
				</tr>
				<tr>
					<td rowspan="2">周末价</td>
					<td>一</td>
					<td>二</td>
					<td>三</td>
					<td>四</td>
					<td>五</td>
					<td>六</td>
					<td>日</td>
				</tr>
				<tr>
					<?for(var j=0; j<priceRecordJsonBeanVo[i].weekPriceVoList.length; j++){?>
						<?if(j==0){?>
							<td class="special-td price-td"><?=priceRecordJsonBeanVo[i].weekPriceVoList[j]?></td>
						<?}else{?>
							<td class="price-td"><?=priceRecordJsonBeanVo[i].weekPriceVoList[j]?></td>
						<?}?>
					<?}?>
				</tr>
				<?for(var j=0; j<priceRecordJsonBeanVo[i].specialPriceVoList.length; j++){?>
					<?if(j==0){?>
						<tr>
							<td rowspan="<?= priceRecordJsonBeanVo[i].specialPriceVoList.length?>">特殊价</td>
							<td colspan="6" class="special-price-date"><?=priceRecordJsonBeanVo[i].specialPriceVoList[j].specialBeginDate?>&nbsp;--&nbsp;<?=priceRecordJsonBeanVo[i].specialPriceVoList[j].sepcialEndDate?></td>
							<td class="price-td"><?=priceRecordJsonBeanVo[i].specialPriceVoList[j].otherSellingPrice?></td>
						</tr>
					<?}else{?>
						<tr>
							<td colspan="6" class="special-td special-price-date"><?=priceRecordJsonBeanVo[i].specialPriceVoList[j].specialBeginDate?>&nbsp;--&nbsp;<?=priceRecordJsonBeanVo[i].specialPriceVoList[j].sepcialEndDate?></td>
							<td class="price-td"><?=priceRecordJsonBeanVo[i].specialPriceVoList[j].otherSellingPrice?></td>
						</tr>
					<?}?>
				<?}?>
			</tbody>
		</table>
	<?}?>
</script>
<script src="/js/mobile/setSize.js"></script>
<script src="/js/mobile/zepto.min.js"></script>
<script src="/js/mobile/artTemplate.js"></script>
<script src="/js/mobile/priceReview.js"></script>
</body>
</html>
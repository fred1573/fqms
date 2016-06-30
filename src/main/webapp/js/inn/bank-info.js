
$(function() {
	/**
	 * X
	 */
	var vm_bank = avalon.define({
		$id : 'vm_bank',
		urls : [ 'inn/searchInnInfo','inn/updateBankInfo','inn/getProvinceCity' ],
		datas : {
			keyWord : '',
			pageNo: 1,
			isFilt: []
		},
		inn : {
			id : '',
			name : '',
			alipayCode : '',
			alipayUser : '',
			tenpayCode : '',
			tenpayUser : '',
			bankType : '',
			bankAccount : '',
			bankCode : '',
			bankName : '',
			bankProvince : '',
			bankCity : '',
			bankRegion : ''
		},
		page : {
			order : '',
			pageNo : 1,
			orderBy : ''
		},
		inns : [],
		cities: [],
		provinces: [],
		cityMap: {},
		codeMap: {},
		initData: function(){
			$('#list_table tbody').on('click', 'tr td:contains("编辑")', function(e){
				var i = $(this).parent().index();
				vm_bank.editBankInfo(vm_bank.inns[i]);
			})
			$('#bank-province').on('change', function(e){
				var code = $(this).val();
				vm_bank.initCitySelect2(code);
			})
			vm_bank.initCities();
			vm_bank.initSelect2Json('bank-name', tmsky.banks, '选择银行');
		},
		searchInnInfo : function(e, pageNo) {
			var url = $("#ctx").val() + '/' + vm_bank.urls[0];
			var data = {
				keyWord : vm_bank.datas.keyWord,
				isFilt: vm_bank.datas.isFilt[0] == 'on',
				pageNo : pageNo || vm_bank.datas.pageNo,
				order : vm_bank.page.order,
				orderBy : vm_bank.page.orderBy
			};
			$.post(url, data).done(function(rs) {
				if (rs.status == 200) {
					vm_bank.inns = rs.result.page.result;
					vm_bank.initPage('page_div', rs.result.page)
				} else {
					tmsky.ui.dialog.tips(rs.message, 'error');
				}
			}).always(function() {
			});
		},
		updateBankInfo: function() {
			var url = $("#ctx").val() + '/' + vm_bank.urls[1];
			var data = {
				id: vm_bank.inn.id,	
				name: vm_bank.inn.name,
				alipayCode: vm_bank.inn.alipayCode,	
				alipayUser: vm_bank.inn.alipayUser,	
				tenpayCode: vm_bank.inn.tenpayCode,	
				tenpayUser: vm_bank.inn.tenpayUser,	
				bankType: vm_bank.inn.bankType,	
				bankAccount: vm_bank.inn.bankAccount,	
				bankCode: vm_bank.inn.bankCode,	
				bankName: vm_bank.inn.bankName,	
				bankProvince: vm_bank.cityMap[vm_bank.inn.bankProvince],	
				bankCity: vm_bank.cityMap[vm_bank.inn.bankCity],	
				bankRegion: vm_bank.inn.bankRegion	
			};
			$.post(url, data).done(function(rs) {
				if (rs.status == 200) {
					hideCoverBox();
					$("#bank_info_edit_div").fadeOut();
					vm_bank.clear();
				} else {
					tmsky.ui.dialog.tips(rs.message, 'error');
				}
			}).always(function() {
			});
		},
		initCities: function(){
			var url = $("#ctx").val() + '/' + vm_bank.urls[2];
			$.post(url).done(function(rs) {
				if (rs.status == 200) {
					vm_bank.provinces = rs.result.province;
					vm_bank.cities = rs.result.city;
					vm_bank.initSelect2Json('bank-province', rs.result.province, '选择省份');
					vm_bank.initSelect2Json('bank-city', rs.result.city, '选择城市');
					vm_bank.initCityMap(rs.result.province);
					vm_bank.initCityMap(rs.result.city);
					vm_bank.initCodeMap(rs.result.province, 1);
					vm_bank.initCodeMap(rs.result.city, 2);
				}
			}).always(function() {
			});
		},
		editBankInfo : function(el) {
			showCoverBox();
			vm_bank.inn.id = el.id;
			vm_bank.inn.name = el.name;
			vm_bank.inn.alipayCode = el.alipayCode;
			vm_bank.inn.alipayUser = el.alipayUser;
			vm_bank.inn.tenpayCode = el.tenpayCode;
			vm_bank.inn.tenpayUser = el.tenpayUser;
			vm_bank.inn.bankType = el.bankType;
			vm_bank.inn.bankAccount = el.bankAccount;
			vm_bank.inn.bankCode = el.bankCode;
			vm_bank.inn.bankName = el.bankName;
			vm_bank.inn.bankProvince = el.bankProvince;
			vm_bank.inn.bankCity = el.bankCity;
			$('#bank-name').select2('val', el.bankName);
			$('#bank-province').select2('val', vm_bank.codeMap[el.bankProvince+1]);
			$('#bank-city').select2('val', vm_bank.codeMap[el.bankCity+2]);
			vm_bank.inn.bankRegion = el.bankRegion;
			$("#bank_info_edit_div").fadeIn();
		},
		clear: function(){
			vm_bank.initSelect2Json('bank-city', rs.result.city, '选择城市');
		},
		changeBankType: function(type){
			vm_bank.inn.bankType = type;
		},
		initCitySelect2: function(code){
			var list = vm_bank.getCityByCode(code);
			vm_bank.initSelect2Json('bank-city', list, '选择城市');
		},
		getCityByCode: function(code){
			var list = [];
			for (var i = 0; i < vm_bank.cities.length; i++) {
				var city = vm_bank.cities[i];
				if(city.code.substring(0, 2) == code.substring(0, 2)){
					var tmp = {name:city.name,code:city.code};
					list.push(tmp);
				}
			}
			return list;
		},
		initCityMap: function(list){
			for (var i = 0; i < list.length; i++) {
				var city = list[i];
				vm_bank.cityMap[city.code] = city.name;
			}
		},
		initCodeMap: function(list, type){
			for (var i = 0; i < list.length; i++) {
				var city = list[i];
				vm_bank.codeMap[city.name+type] = city.code;
			}
		},
		initPage: function(id, page) {
			var list = [];
			tmsky.ui.page.render({
				id : id,
				callbackParams : list,
				callback : function() {
					vm_bank.searchInnInfo(event, list[0].pageNo);
				}, // 或click : click
				pages : page.totalPages,
				pageNo : page.pageNo,
				align : 'center',
				skin : 'red',
				totalCount : page.totalCount
			});
		},
		initSelect2 : function(listId, selectId, defaultMsg) {
			var userdata = [];
			$("#" + selectId + " option").each(function(i, obj) {
				userdata[i] = {};
				userdata[i].id = $(obj).val();
				userdata[i].text = $(obj).text();
			});
			$("#" + listId).select2({ 
				placeholder : defaultMsg,
				data : userdata
			});
		},
		initSelect2Json : function(listId, jsonList, defaultMsg) {
			var userdata = [];
			$(jsonList).each(function(i, obj) {
				userdata[i] = {};
				userdata[i].id = obj.code || obj;
				userdata[i].text = obj.name || obj;
			});
			$("#" + listId).select2({ 
				placeholder : defaultMsg,
				data : userdata
			});
		}
	});
	
	avalon.ready(function() {
		vm_bank.initData();
	})
})

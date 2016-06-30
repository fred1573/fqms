/**
 * 初始化
 * @param {Object} "#checkAll"
 */
$(document).ready(function(){
	
	jQuery("#modifyPwdForm").validationEngine('attach', {promptPosition : "bottomRight"});
	
	//修改密码键盘监听
	$("#modify_pwd_div").keydown(function(event){ 
		if (event.keyCode == 13)
			$("#modify_pwd_button").click( );
	});
	
	$("#modify_pwd_button").on("click",function(){
		if($("#modifyPwdForm").validationEngine('validate')){
			$.post($("#modifyPwdForm").attr("action"), $("#modifyPwdForm").serialize()).done(function(obj) {
				if(obj.status == 200){
					closeByDivId("modify_pwd_div");
					art.dialog.tips('修改密码成功！',2);
			  	}else{
			  		showErrorTip(obj.result,"modify_pwd_div");
			  	}
			});
		}
	});
});

/**
 * 修改密码
 * @param {Object} id
 */
function toModifyPwd(userCode) {
	showCoverBox();
	$("#modifyPwd_userName").val(userCode);
	$("#modify_pwd_div").fadeIn();
	$("#modifyPwd_sysUserPwd").focus();
}
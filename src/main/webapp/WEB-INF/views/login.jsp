<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<title>番茄家族-登录</title>
<script type="text/javascript">
	function refreshJCaptcha() {
		$('#captchaImg').hide().attr('src','${ctx}/security/jcaptcha.jpg?' + Math.floor(Math.random()*100)).fadeIn();
	}
	
	function toSumbit(){
		$('#loginForm').submit();
	}
	
	function toReset() {
		clearForm("loginForm");
	}
</script>
</head>
<body>
	<div class="center-box">
    	<div class="center-box-in login">
        	<h1>番茄家族管理系统Login</h1>
            <form id="loginForm" action="${ctx}/user_login" method="post" style="margin-top: 1em"  onkeydown="if(event.keyCode==13) toSumbit();" >
            <ul>
            	<li><input id='j_username' name='j_username' type="text" placeholder="输入用户名"/></li>
                <li><input id='j_password' name='j_password' type="password" placeholder="输入密码" /></li>
                <li><input name="j_captcha" class="check-code" type="text" size="5"/><img alt="看不清楚?点击更换验证码" id="captchaImg" src="${ctx}/security/jcaptcha.jpg" onclick="refreshJCaptcha()"/><span><a href="javascript:refreshJCaptcha()">换一张</a></span></li>
                <li>
                	<a href="javascript:toSumbit()" class="login-button" >登&nbsp;&nbsp;&nbsp;&nbsp;录</a>
                	<em class="error-tips">
                	<%if ("true".equals(request.getParameter("error"))) {%>
						用户名/密码错误或者用户已被禁用
					<%
						}
						if ("captchaError".equals(request.getParameter("error"))) {
					%>
						验证码错误,请重试.
					<%
						}
						if ("sessionTimeout".equals(request.getParameter("error"))) {
					%>
						此帐号的当前状态已失效或已从别处登录,请重新登录.
					<%
						}
					%>
                	</em>
                </li>
            </ul>
            </form>
        </div>
    </div>
</body>
</html>


<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>含有非法输入字符</title>
<style type="text/css">
<!--
body {
	margin-top: 100px;
}
.CSS {
	font-size: 14px;
	line-height: 22px;
	color: #003300;
}
a:link {
	font-size: 14px;
	color: #333333;
	text-decoration: none;
}
a:visited {
	font-size: 14px;
	color: #333333;
	text-decoration: none;
}
a:hover {
	font-size: 14px;
	color: #333300;
	text-decoration: underline;
}
-->
</style>
</head>

<body>
<div>
	<br />
	<center>
		<table width="600" border="0" cellpadding="0" cellspacing="0">
		  <tr>
		    <td width="600" height="57"><img src="../images/err_01.jpg" width="600" height="57"></td>
		  </tr>
		</table>
		<table width="600" border="0" cellpadding="0" cellspacing="1" bgcolor="D1CBD0">
		  <tr>
		    <td height="321" bgcolor="#F9F9F9">
			    <table width="585" height="232" border="0" align="center" cellpadding="0" cellspacing="0">
			      <tr>
			        <td width="162" rowspan="3"><img src="../images/logo.png" width="113" height="124"></td>
			        <td width="423" height="42"><img src="../images/err_02.jpg" width="302" height="28"></td>
			      </tr>
			      <tr>
			        <td height="76" class="CSS">&nbsp;&nbsp;&nbsp;&nbsp;您的输入字符中包括不合法字符。
		            </td>
			      </tr>
			      <tr>
			        <td height="30" align="left" class="CSS"></td>
			      </tr>
			      <tr>
			        <td colspan="2"></td>
		          </tr>
			    </table>
		    </td>
		  </tr>
		</table>
		<br />
		<div class="tablnr" style="text-align: center;">
			<input type="button" name="button2" id="button2" value="    返    回   " onclick="javascript:window.history.back(-1);">
		</div>
	</center>
</div>
</body>
</html>
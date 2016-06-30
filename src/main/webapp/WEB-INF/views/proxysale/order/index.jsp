<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
  <title>订单管理</title>

  <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
  <script src="${ctx}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
  <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
  <style type="text/css">
    .return {margin: 10%}
  </style>
</head>
<body>
<div class="container-right">
  <div class="header"><span style="font-size: 40px">錄入訂單</span></div>
  <div><span style="color:lightcoral">${info}</span></div>

  <div>
    <div><span><a href="/proxysale/order/template" target="_blank">------------------------下載模板(xlsx)-----------------------</a></span></div>
    <div><span style="color:red; font-size:25px">注意事項:</span>
    <ul class="ul">
      <li>1 所有內容須為文本類型或數字類型</li>
      <li><span style="color:red">2 不能刪除模板文件任意一列,不能添加列,不能改變列的順序，否則肯定（不是可能）会造成訂單數據錯誤</span></li>
      <li>3 每個單元格都不能為空</li>
      <li><img src="http://www.philipcoppens.com/vendetta_07.jpg"></li>
      <li>4 enjoy your life</li>
    </ul></div>
  </div>
  <div class="return">
    <form action="/proxysale/order/batch_create" method="post" enctype="multipart/form-data" onsubmit="return check()">
      <input type="file" name="file" /><input type="submit" value="確認錄入">
    </form>
  </div>
</div>
</body>
</html>
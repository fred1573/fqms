<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>
    <title>绿番茄代销</title>
</head>
<body>
<div class="container-right">
    <div class="header">
        ${proxyInn.innName} 合同详情
    </div>
    <div><span style="font-size: 20px">合同编号:<c:if test="${not empty proxyContractImages }">${proxyContractImages[0].contractNo }</c:if></span></div>
    <div>
        <c:forEach items="${proxyContractImages}" var="contractImage">
            <div style="margin-top: 10%; ">
                <img src="${contractImage.url}" width="950"/>
            </div>
        </c:forEach>
    </div>


</div>
</body>

</html>

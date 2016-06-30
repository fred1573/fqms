<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html lang="zh-CN">
<head>
    <title>${proxyInn.innName}详情</title>
    <link rel="shortcut icon" type="image/ico" href="http://assets.fanqiele.com/1.0.5/images/favicon.ico">
    <link rel="stylesheet" href="/css/proxysale/reset.css">
    <link rel="stylesheet" href="/css/proxysale/xzbackEnd.css">
    <link rel="stylesheet" href="/css/proxysale/xq.css">
    <script src="/js/proxysale/artTemplate.js"></script>
<%--    <script src="/js/common/jquery-1.9.1.min.js"></script>--%>
    <script src="/js/proxysale/lib.min.js"></script>
    <script type="text/javascript">
    
    </script>
</head>
<body>
<div class="container-right">

    <div class="head">
        <span>${proxyInn.innName} 详情</span>
        <%--<a href="${ctx}/proxysale/inn" class="return">返回</a>--%>
    </div>
    <span class="ota-span">OTA链接:<input type="text" value="${proxyInn.otaLink}"><button class="obtn">修改</button></span>
    <span class="ota-dis">
        <div class="address" style="width: 100%;">
            客栈详细地址：${proxyInn.innAddr}
            <span style="margin-left: 50px;">
                前台电话：${proxyInn.phone}
                <c:choose>
                    <c:when test="${not empty proxyInn.phone}">
                        ${proxyInn.phone}
                    </c:when>
                    <c:otherwise><em style="color: red;">暂无</em></c:otherwise>
                </c:choose>
            </span>
            <span style="margin-left: 50px;">总：下架精品代销<em style="color: red;">${summary[1]}</em>次，下架普通代销<em style="color: red;">${summary[3]}</em>次 </span>
        </div>
        <div style="clear: both;"></div>
    </span>
    <div class="ota-foot">
        <ul>
            <li class="first">
                <a href="#" class="contract-bg">
               <%-- <img width="100" height="100" src="/images/proxysale/nopic.jpg" class="ht">--%>
               			<c:choose>
               				
	               				<c:when test="${hasPass}">
	               					 合同已通过
	               				</c:when>
               				<c:otherwise>
 								合同未审核
               				</c:otherwise>	
               			</c:choose>
                </a>
                <span class="join-time">加入代销时间<fmt:formatDate value="${proxyInn.createTime}" pattern="yyyy-MM-dd HH:mm" /></span>
            </li>
            <li>
            	<span class="op rectangle">客栈及签约经理详情</span>
                <span class="op">最近一次上/下架时间: <fmt:formatDate value="${proxyInn.lastOnoffTime}" pattern="yyyy-MM-dd" /> </span>
                <span class="op" style="position: inline-block; width: 980px ; height: 30px;overflow: hidden;" >下架备注:  <span  
               >${offInfo.remark}</span> </span>
                <span class="op">最近一次编辑操作时间: <fmt:formatDate value="${proxyInn.editTime}" pattern="yyyy-MM-dd" /></span>
            </li>
				<li>
					<div class="op detail">
						<span>联系人: </span>
                        <c:choose>
                            <c:when test="${ not empty contact.data.contactVoBean}">
                                <select>
                                    <c:forEach items="${contact.data.contactVoBean }" var="item">
                                        <option>(${item.bossName})${item.bossMobile}</option>
                                    </c:forEach>
                                </select>
                            </c:when>
                            <c:otherwise>
                                <em style="color: red;">暂无</em>
                            </c:otherwise>
                        </c:choose>
						<span class="manager">签约经理：</span>
                        <c:if test="${ not empty contact.data}">
                            ${contact.data.signManagerName}(${contact.data.signManagerMobile})
                        </c:if>
					</div>
					<span class="op">最近一次上架/下架操作人:
						${proxyInn.onOffOperator.sysUserName}</span> 
					<span class="op">&nbsp;</span>	
					<span class="op">最近一次编辑操作人:
						${proxyInn.editOperator.sysUserName}</span>
				</li>
			</ul>
        <div style="clear: both;"></div>

    </div>
    <div class="priceTable floorPrice">
        <h3 class="font-tip">底价</h3>
        <div class="contentBox">
            <div class="ht">
                <div class="contentTime">
                    <div class="timeSelect">
                        <span class="timeLastM">&lt;</span>
                        <input class="hasDatepicker datetime" readonly="readonly" type="text" value="2015-8-20">
                        <span class="timeNextM">&gt;</span>
                    </div>
                    <div class="timeShow">

                    </div>

                </div>
            </div>
            <div class="ct"></div>
        </div>


    </div>

    <div class="priceTable sellPrice">
        <h3 class="font-tip">卖价</h3>
        <div class="contentBox">
            <div class="ht">
                <div class="contentTime">
                    <div class="timeSelect">
                        <span class="timeLastM">&lt;</span>
                        <input class="hasDatepicker datetime" readonly="readonly" type="text" value="2015-8-21">
                        <span class="timeNextM">&gt;</span>
                    </div>
                    <div class="timeShow">

                    </div>
                </div>
            </div>
            <div class="ct"></div>
        </div>

    </div>


</div>
<script id="tablePrice" type="text/html">
    <div class="contentFooter">
        <div class="roomShow">
            <ul class="roomList">
                <?for(var i = 0;i < result.list.length;i++){?>
                <li>
                    <div><p class="roomName"><?=result.list[i].roomTypeName?></p></div>
                </li>
                <?}?>
            </ul>
        </div>

        <div class="dataShow">
            <?for(var i = 0;i < result.list.length;i++){?>
            <ul class="dataList">
                <?for(var j = 0;j < result.list[i].roomDetail.length;j++){?>
                <li class="priceWeekend">
                    <span class="price ">￥<?=result.list[i].roomDetail[j].roomPrice?></span>
                    <p class="num"><span class="forXZNum"><?=result.list[i].roomDetail[j].roomNum?></span></p>
                </li>
                <?}?>
            </ul>
            <?}?>
        </div>
    </div>
</script>

<script id="headTime" type="text/html">


    <ul class="timeList">
        <?for(var i = 0;i < dataArr.length;i++){?>
        <?if(dataArr[i].isHoliday){?>
        <?if(dataArr[i].weekDay=='六'||dataArr[i].weekDay=='日'){?>
        <li class="weekend"><p><span class="date holiday"><?=dataArr[i].date?></span><span class="week"><?=dataArr[i].weekDay?></span></p></li>
        <?}else{?>
        <li><p><span class="date holiday"><?=dataArr[i].date?></span><span class="week"><?=dataArr[i].weekDay?></span></p></li>
        <?}?>
        <?}else{?>
        <?if(dataArr[i].weekDay=='六'||dataArr[i].weekDay=='日'){?>
        <li class="weekend"><p><span class="date"><?=dataArr[i].date?></span><span class="week"><?=dataArr[i].weekDay?></span></p></li>
        <?}else{?>
        <li><p><span class="date"><?=dataArr[i].date?></span><span class="week"><?=dataArr[i].weekDay?></span></p></li>
        <?}?>
        <?}?>
        <?}?>
    </ul>


</script>

<script src="/js/proxysale/xq.js"></script>
<input type="hidden" id="${proxyInn.id}-baseAcc" value="${proxyInn.baseOuterId}" />
<input type="hidden" id="${proxyInn.id}-saleAcc" value="${proxyInn.saleOuterId}" />
<input type="hidden" id="proxyInnId" value="${proxyInn.id}" />
</body>
</html>

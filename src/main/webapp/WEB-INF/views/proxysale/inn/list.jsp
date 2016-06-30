<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
    <title>代销平台-客栈管理</title>
    <link href="${ctx}/css/jquery-ui.css" rel="stylesheet" type="text/css">
    <script src="${ctx}/js/common/form.js" type="text/javascript"></script>
    <script src="${ctx}/js/api/comm.js" type="text/javascript"></script>
    <script src="${ctx}/js/common/layer/layer.js" type="text/javascript"></script>
    <script src="${ctx}/js/proxysale/inn.js" type="text/javascript"></script>
    <script src="${ctx}/js/proxysale/jquery-ui-1.10.1.min.js"></script>
    <link href="${ctx}/css/proxysale/jquery-ui-1.10.1.css" rel="stylesheet">
    <script type="text/javascript">
        var ctx = '${ctx}';
    </script>
    <style type="text/css">
        .red{color: red;}
        .table_count{clear: both; width: 100%;height: 40px;position: relative;top: -20px;}
        .dul{width:100%;height:100%;}
        .dul .all span{position:relative;top:5px;}
        .dul ul{margin:0px;padding:0px;margin-top: -30px;}
        .dul ul li{list-style:none;width:80px;margin-left:30px;float:left;height:30px;}
        .all{width:100%;height:30px;margin-top:10px;}
        .all span{margin-left:30px;}
        .area_content{width:100%;}
        .area_content_title	{width:100%;height:25px;margin-top:10px;clear:both;border-bottom: 1px solid #abcdef;}
        .area_content_title span{margin-left:30px;}
        #enterSaleRoomType,#enterBaseRoomType {
            margin: 10px;
        }
        #enterSaleRoomType i,#enterBaseRoomType i {
            padding: 0 5px;
        }
    </style>
</head>
<body>
<div class="container-right">
    <form id="mainForm" action="${ctx}/proxysale/inn" method="post">
        <input name="status" id="fs" type="hidden" value="${status}"/>
        <input type="hidden" name="pageNo" id="pageNo" value="${page.pageNo}"/>
        <input type="hidden" name="orderBy" id="orderBy" value="${page.orderBy}"/>
        <input type="hidden" name="order" id="order" value="${page.order}"/>
        <input type="hidden" name="areaName" <c:if test="${not empty area}">value="${area.name}"</c:if>>
        <input type="hidden" name="innName" value="${innName}"/>
    </form>
    <jsp:include page="../header_fragment.jsp"/>
    <!--end header-->
    <div class="search-area">
        <%--<input type="text" class="search" placeholder="搜索区域">--%>
        <form action="/proxysale/inn" method="post">
            <input name="areaName" class="search" id="searchAreaName" placeholder="区域" maxlength="20" type="text"
                   <c:if test="${not empty area}">value="${area.name}"</c:if>>
            <input type="hidden" name="areaId" <c:if test="${not empty area}">value="${area.id}"</c:if>/>
            <input class="search-button" type="submit" value="&nbsp;">
        </form>
        <%--<a class="area-search-btn">按区域搜索结果批量关房</a>--%>
    </div>
    <select class="select-tab">
        <option value="0">按区域搜索结果批量操作</option>
        <security:authorize ifAnyGranted="ROLE_客栈管理-批量关房">
            <option value="1">按区域搜索结果批量关房（区域继承）</option>
        </security:authorize>
        <security:authorize ifAnyGranted="ROLE_客栈管理-批量上线分销商">
            <option value="2">按区域搜索结果批量上线渠道（已上架客栈）</option>
        </security:authorize>
        <security:authorize ifAnyGranted="ROLE_客栈管理-批量下线分销商">
            <option value="3">按区域搜索结果批量下线渠道（已上架客栈）</option>
        </security:authorize>
    </select>
    <div class="inn-search">
        <div class="search-box">
            <form action="/proxysale/inn">
                <input name="innName" class="search" placeholder="客栈名称" maxlength="20" type="text" <c:if test="${not empty innName}">value="${innName}"</c:if>>
                <input class="search-button" id="search_submit" type="submit">
            </form>
        </div>
        <%--<div class="btngroup">
            <a href="/proxysale/inn/export/sale/1">
                <button style="margin-right: 10px">导出卖价</button>
            </a>
            <a href="/proxysale/inn/export/sale/2">
                <button style="margin-right: 20px">导出底价</button>
            </a>
            <a href="/proxysale/inn/export/inn">
                <button style="margin-right: 20px">导出客栈</button>
            </a>
        </div>--%>
    </div>
    <table class="kz-table" cellpadding="12">
        <thead>
        <tr>
            <th>PMS客栈ID</th>
            <th>accountId</th>
            <th>城市</th>
            <th>目的地</th>
            <th>客栈名称</th>
            <th>
                <select id="filterStatus" onchange="changeStatus()">
                    <option value="">状态</option>
                    <option value="0" <c:if test="${status eq '0'}">selected</c:if>>已下架</option>
                    <option value="3" <c:if test="${status eq '3'}">selected</c:if>>已上架</option>
                    <option value="2" <c:if test="${status eq '2'}">selected</c:if>>已上架精品代销</option>
                    <option value="1" <c:if test="${status eq '1'}">selected</c:if>>已上架普通代销</option>
                </select>
            </th>
            <th>代销模式</th>
            <th>总抽佣比例</th>
            <th colspan="6">操作</th>
        </tr>
        </thead>
        <c:forEach items="${page.result}" var="r">
            <tr>
                <td>${r.inn}</td>
                <td>${r.accountId}</td>
                <td>${r.area.name}</td>
                <td>${r.innRegion.name}</td>
                <td>
                    <input type="hidden" id="${r.id}" value="${r.innName}"/>
                    <a href="${ctx}/proxysale/inn/${r.id}/detail" style="color: blue;" target="_blank">${r.innName}</a>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${r.status == 3}">已上架</c:when>
                        <c:when test="${r.status == 2}">已上架精品(活动)</c:when>
                        <c:when test="${r.status == 1}">已上架普通(卖)</c:when>
                        <c:when test="${r.status == 0}">已下架</c:when>
                        <c:otherwise>状态异常，快联系技术</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${r.basePriceValid && r.salePriceValid}">精品(活动)/普通(卖)</c:when>
                        <c:when test="${!r.basePriceValid && r.salePriceValid}">普通(卖)</c:when>
                        <c:when test="${r.basePriceValid && !r.salePriceValid}">精品(活动)</c:when>
                        <c:when test="${!r.basePriceValid && !r.salePriceValid}">无</c:when>
                        <c:otherwise>价格模式异常，快联系技术</c:otherwise>
                    </c:choose>
                </td>
                <td>
                  <security:authorize ifAnyGranted="ROLE_客栈管理-抽佣比例">
                    <c:choose>
                        <c:when test="${r.salePercentage != null}">
                        	<a href="javascript:void(0)" onclick="setSalePercentage(${r.id},'${r.salePercentage}')" style="color: blue;">
                        	${r.salePercentage}
                        	</a>
                        	</c:when>
                        <c:otherwise>无</c:otherwise>
                    </c:choose>
               </security:authorize>
                </td>
                <td>
                    <security:authorize ifAnyGranted="ROLE_客栈管理-上/下架精品代销">
                    <c:choose>
                        <c:when test="${r.basePriceOnshelfed}">
                            <a href="javascript:void(0)" onclick="soldOut(${r.id},'boutique')" style="color:blue;">下架精品(活动)代销</a>
                        </c:when>
                        <c:when test="${r.basePriceValid}">
                            <a href="javascript:void(0)" onclick="setChannelsDialog(${r.id},1,1)" style="color:blue;">上架精品(活动)代销</a>
                        </c:when>
                        <c:otherwise><span style="color:gray">上架精品(活动)代销</span></c:otherwise>
                    </c:choose>
                    </security:authorize>
                </td>
                <td>
                    <security:authorize ifAnyGranted="ROLE_客栈管理-上/下架普通代销">
                    <c:choose>
                        <c:when test="${r.salePriceOnshelfed}">
                            <a href="javascript:void(0)" onclick="soldOut(${r.id},'general')" style="color:blue;">下架普通(卖)代销</a>
                        </c:when>
                        <c:when test="${r.salePriceValid}">
                            <a href="javascript:void(0)" onclick="General(${r.id})" style="color:blue;">上架普通(卖)代销</a>
                        </c:when>
                        <c:otherwise><span style="color:gray">上架普通(卖)代销</span></c:otherwise>
                    </c:choose>
                    </security:authorize>
                </td>
                <td>
                    <security:authorize ifAnyGranted="ROLE_客栈管理-设置渠道">
                        <a onclick="setChannelsDialog(${r.id},0,0)" href="javascript:void(0)">设置渠道</a>
                    </security:authorize>
                </td>
                    <%--<td><a class="tab-close-room">关房</a></td>--%>
                <td>
                    <security:authorize ifAnyGranted="ROLE_客栈管理-关房">
                        <a onclick="getInnCloseInfo(${r.inn},this)" href="javascript:void(0)">关房</a>
                    </security:authorize>
                </td>
                <td>
                    <input type="hidden" value="${r.innName}" name="InnName">
                    <input type="hidden" value="${r.inn}" name="innId">
                    <security:authorize ifAnyGranted="ROLE_下架房型">
                        <a onclick="roomTypeList(this)">房型</a>
                    </security:authorize>
                </td>
                <td>
                    <security:authorize ifAnyGranted="ROLE_客栈管理-移除">
                        <a href="javascript:void(0)" class="remove" onclick="del(${r.id},this)">移除</a>
                    </security:authorize>
                </td>
            </tr>
        </c:forEach>
    </table>
    <tags:pagination page="${page}" paginationSize="15"/>
    <div class="table_count">
        <span class="red">【<c:choose><c:when test="${not empty area}">${area.name}</c:when><c:otherwise>全部</c:otherwise></c:choose>】</span>加入代销平台的客栈总数<span class="red">${summary[0] }</span> 家，
        精品代销上架 <span class="red">${summary[1]} </span> 家，普通代销上架<span class="red">${summary[2]} </span>
        家；今日上架精品代销 <span class="red">${toadyUp[0] }</span> 家，
        上架普通代销 <span class="red">${toadyUp[2] }</span> 家；
        今日下架精品代销 <span class="red">${toadyUp[1] }</span> 家，
        下架普通代销 <span class="red">${toadyUp[3] }</span>家
    </div>
</div>

<!-------------------------------->
<%--display: table-cell;
width: 50%;
margin: 0;
box-sizing: border-box;
padding: 0 20px;--%>

<!---------------上下架框----------------->
<div class="center-box">
    <div class=" center-box-in audit-window" style="display:none;" id="roomType">
        <a href="javascript:close('9')" class="close-window"></a>

        <h1><span id="roomTypeInnName"></span></h1>
        <div style="display: table;width:100%;margin-bottom: 20px;">
            <ul  id="Sale" style="display: table-cell;width: 50%;box-sizing: border-box;margin:0;padding:0 20px;border-right:1px solid #ccc;">
                <li>代销房型</li>
                <li><label for="checkAllSale"><input type="checkbox" id="checkAllSale">全选</label> </li>
            </ul>
            <ul id="Base" style="display: table-cell;width: 50%;box-sizing: border-box;margin:0;padding:0 20px;">
                <li>代销房型</li>
                <li><label for="checkAllBase"><input type="checkbox" id="checkAllBase">全选</label> </li>
            </ul>
        </div>
        <div style="text-align: center;margin:20px 0"><button style="width:100px; height: 35px;" onclick="sheefRoomType()" id="sheefRoomType">下架</button></div>
    </div>
    <div class=" center-box-in audit-window" style="display:none;" id="enterRoomType">
        <a href="javascript:close('10')" class="close-window"></a>

        <h1><span>是否要下架以下房型</span></h1>
        <div style="display: table;width:100%;margin-bottom: 20px;">
            <div id="enterSaleRoomType">代销房型:</div>
            <div id="enterBaseRoomType">活动房型:</div>
        </div>
        <div style="text-align: center;margin:20px 0"><button style="width:100px; height: 35px;margin-right:20px;" onclick="enterRoomType()">确定</button><button style="width:100px; height: 35px;" onclick="cancelEnterRoomType()">取消</button></div>
    </div>
</div>
<!---------------编辑框----------------->
<div id="dialogBlackBg">
    <div class="center-box">

        <div class=" center-box-in audit-window" style="display:none;" id="edit">
            <a href="javascript:close('3')" class="close-window"></a>

            <h1><span id="innName"></span></h1>
            <input type="hidden" id="proxyInnId"/>
            <ul>
                <form id="editInnForm" method="post">
                    <li>
                        <span>售卖渠道</span><span id="channels"></span>
                    </li>
                    <li>
                        设置总抽佣比例<input id="percentage" value="13" style="width: 40%"/>%
                    </li>
                </form>
                <li>
                    <a href="javascript:doEdit()" class="green-button-ok">确&nbsp;&nbsp;&nbsp;&nbsp;定</a>
                </li>
            </ul>
            </form>
        </div>
        <!---------------移除对话框----------------->
        <div class=" center-box-in audit-window" style="display:none;" id="del">
            <a class="close-window" onclick="$('#del').fadeOut();$('#dialogBlackBg').hide();" style="margin-left: 50px;"></a>
            <h1>确定移除<b id="innNameVal"></b>客栈？</h1>
            <div class="remove-reason"><span style="width: auto; height: auto;">原因：</span><textarea></textarea><div></div></div>
            <div style="margin-bottom:20px"  class="btn-submit enterRemove">
                    <button type="button" id="remove" style="margin-left: 150px;" >确定</button>
                    <button type="button" onclick="$('#del').fadeOut();$('#dialogBlackBg').hide();" style="margin-left: 50px;" >取消</button>
            </div>
            <div style="margin-bottom:20px;"  class="btn-submit enterRemove2">
                <button type="button" id="removeEnter" style="margin-left: 150px;" >确定</button>
                <button type="button" onclick="$('#del').fadeOut();$('#dialogBlackBg').hide();" style="margin-left: 50px;" >取消</button>
            </div>
        </div>

        <div class=" center-box-in audit-window" style="display:none;width: 460px;" id="edit_channel">
            <a href="javascript:close('4')" class="close-window"></a>
            <div class="scale">
                <div id="innName3"></div>
                <div class="select-consignment">设置总抽拥比例： <input type="text" id="scale"> %</div>
                <div class="center"><button class="nextStep" id="nextStep1">下一步</button></div>
                <div class="red warmmessage">*请输入正确的总抽佣比例，如不选择请点击取消</div>
            </div>
            <div class="dul">
                <div id="innName1"></div>
                <div class="all">
                    <span><input   id="checkAll" name="subBox"  type="checkbox" />全选</span>
                </div>
                <div class="area_content">
                    <div class="boutique">
                        <div class="area_content_title"><span>精品</span></div>
                        <ul id="base_ul">
                        </ul>
                    </div>
                    <div class="general">
                        <div class="area_content_title"><span>普通</span></div>
                        <ul id="sale_ul">
                        </ul>
                    </div>
                </div>
                <div style="margin-bottom:20px"  class="btn-submit">
                    <div style="margin-bottom:20px"  class="btn-submit">
                        <button type="button" id="inner_id_check" style="margin-left: 110px;" >确定</button>
                        <button type="button" onclick="$('#edit_channel').fadeOut();$('#dialogBlackBg').hide();" style="margin-left: 50px;" >取消</button>
                    </div>
                </div>
            </div>
        </div>
        <!---------------上架精品代销/上架普通代销 成功 对话框----------------->

        <!---------------下架精品代销/下架普通代销----------------->
        <div class=" center-box-in audit-window" style="display:none;width: 460px;height: 240px;" id="soldOut">
            <a href="javascript:close('5')" class="close-window"></a>
            <div id="enter-soldout"></div>
            <div class="remark"><textarea></textarea></div>
            <div class="btn-enterOrcansel"><button id="enterSoldout">确认</button> <button onclick="$('#soldOut').fadeOut();$('#dialogBlackBg').hide();">取消</button></div>
        </div>
      <div class=" center-box-in audit-window" style="display:none;width: 460px;height: 100px;" id="edit_inn_percentage">
            <a href="javascript:close('8')" class="close-window"></a>
        
			<div style="margin-top: 20px;margin-left: 120px;">
			<input type="hidden" id="edit_percentage_inn_id">
			总抽佣比例： <input type="text" id="sale-percentage-value"    />
			</div>	
				        
            <div class="btn-enterOrcansel"><button onclick="reqUpdatePerrcentage();">确认</button> <button onclick="$('#soldOut').fadeOut();$('#dialogBlackBg').hide();">取消</button></div>
        </div>
        
        <!---------------按区域搜索结果批量上线/下线渠道（已上架客栈）框----------------->
        <div class=" center-box-in audit-window" style="display:none;width: 460px;min-height: 200px;" id="areaSearchOnline">
            <a href="javascript:close('6')" class="close-window"></a>
            <div id="batchStep1">
                <div class="title"></div>
                <div class="select-consignment"><label for="boutique"><input type="radio" name="consignment" id="boutique" value="1" checked>精品代销 </label><label for="general"><input type="radio" name="consignment" id="general" value="2">普通代销 </label></div>
                <div class="center"><button class="nextStep" id="nextStep">下一步</button></div>
                <div class="red warmmessage">*请选择至少一种模式确认，如不选择请取消</div>
            </div>
            <div id="batchStep2" style="display:none">
                <div class="dul">
                    <div id="innName2"></div>
                    <div class="all">
                        <span><input   id="checkAll1" name="subBox"  type="checkbox" />全选</span>
                    </div>
                    <div class="area_content">
                        <div class="area_content_title"><span></span></div>
                        <ul id="saleChannel">
                            <%--<li><input type="checkbox" name="salechannel">xxx</li>
                            <li><input type="checkbox" name="salechannel">xxx</li>
                            <li><input type="checkbox" name="salechannel">xxx</li>--%>
                        </ul>

                    </div>
                    <div style="margin-bottom:20px" class="btn-submit">
                        <button type="button" id="inner_id_check1" style="margin-left: 110px;" >确定</button>
                        <button type="button" onclick="$('#areaSearchOnline').hide();$('#dialogBlackBg').hide();" style="margin-left: 50px;" >取消</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!---------------编辑框----------------->

<div class="windowBg">
    <div class="dialoag-content">
        <p>
            添加【<span class='addarea'></span>】区域的关房时间段
        </p>

        <div class="select-closeroomtime">
            <div class="addtime">
            </div>
            <%--<p>
                <span class="delete-line">_______________________________________________________</span>
                <input type="text" class="start-time time" placeholder="开始时间">
                至<input type="text" class="end-time time" placeholder="结束时间">
                <button class="delete-time">删除</button>
                <button class="revert-time">恢复</button>
            </p>--%>
            <p>
                <button class="add-a-closeroom">+添加一条关房</button>
            </p>
        </div>
        <div class="dialoag-bottom">
            <p class="p-time-enter">
                <button class="time-enter">确认</button>
                <button class="cancel">取消</button>
            </p>
            <p class="p-time-enter1">
                <button class="time-enter1">确认</button>
                <button class="cancel1">取消</button>
            </p>
            <p class="warm">
                *请选择正确的日期，如不选择请点击取消
            </p>
        </div>
    </div>
</div>
<div class="windowBg1">
    <div class="dialoag-content">
        <p>
            您确认为【<span class="addarea"></span>】的房间添加
        </p>

        <div class="select-closeroomtime">
            <div id="list-time">
                <%--	<p><span class="list-time-span">2015-02-01</span>至<span class="list-time-span">2015-02-01</span></p>
                   <p><span>2015-02-01</span>至<span>2015-02-01</span></p>
                   <p><span>2015-02-01</span>至<span>2015-02-01</span></p>
                   <p><span>2015-02-01</span>至<span>2015-02-01</span></p>
                   <p><span>2015-02-01</span>至<span>2015-02-01</span></p>--%>
            </div>
        </div>
        <div class="dialoag-bottom">
            <p class="dialoag-bottom-p1">
                <button class="last-time-enter">确认</button>
                <button class="return-cancel">取消</button>
            </p>
            <p class="dialoag-bottom-p2">
                <button class="last-time-enter1">确认</button>
                <button class="return-cance2">取消</button>
            </p>
            <p>
                *确认该时间段进行关房
            </p>
        </div>
    </div>
    
    
    <!-- 		  <div class=" center-box-in audit-window" style="display:none;width: 460px; height: 100px; " id="edit_inn_percentage">
            <a href="javascript:close('8')" class="close-window"></a>
         		总抽佣比例： <input type="text" id="sale-percentage-value" />
        </div> -->
    
</div>
  <!--    <div class=" center-box-in audit-window" style="display:none;width: 460px; height: 100px; " id="edit_inn_percentage">
            <a href="javascript:close('8')" class="close-window"></a>
         		总抽佣比例： <input type="text" id="sale-percentage-value" />
        </div> -->

<div style="display:none;width: 400px;padding:20px;position: fixed; right:0;bottom:0;background: #fff; border: 1px solid #82A768" id="onOroffShelfSuccess">
    <div style="padding: 10px" id="operateMessage">   </div>
</div>


<div class="windowBg2">
    <div><img src="${ctx}/images/webloading.gif"/></div>
</div>
<script type="text/javascript">

</script>
</body>
</html>
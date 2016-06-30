<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>地区后台</title>
	<script src="${ctx}/js/common/form.js" type="text/javascript"></script>
	<script src="${ctx}/js/region/region.js" type="text/javascript" ></script>
	<script type="text/javascript">
		var ctx = '${ctx}';
	</script>
	</head>
	<body class="bg1">
	<!---------------删除框----------------->
	<div id="delete" class="center-box" style="display: none;" >
		<div  class=" center-box-in audit-window" >
			<input id="deleteId" type="hidden" value=""/>
	    	<h2>是否删除客栈？</h2>
	    	<p id="regionName"></p>
	        <span><a href="javascript:void(0)" class="reset-button" onclick="ensure()">确定</a><a href="javascript:close('2')" class="audit-nopass-button">取消</a></span>
	    </div>
	</div>
	<!---------------删除框----------------->
	<!---------------添加框----------------->
	<div class="center-box">
		<div class=" center-box-in audit-window" style="display:none;" id="add">
	        <a href="javascript:close('1')" class="close-window"></a>
	        <h1>添加客栈</h1>
		        <ul>
		        	<form id="addInnForm" method="post">
		        		<li><dd>账号:</dd><input id="phone" name="phone" type="text" class="validate[required,custom[mobile],minSize[11],maxSize[11]] ipt" maxlength="11" /></li>
		            </form>
		            <li>
		            	<a href="javascript:add()" class="green-button-ok">确&nbsp;&nbsp;&nbsp;&nbsp;定</a>  	
		            </li>
		        </ul>
	        </form>
	    </div>
	</div>
	<!---------------添加框----------------->
		<div class="container-right">	
				<div class="select-area">
		        	<ul>
		               <li class="on">周庄</li>
		               <li>西塘</li>
		            </ul>
        		</div>
			<div class="header">
					<p style="width:100%; height:100%; line-height:67px; text-indent:15px; font-size:14px;" id="content"></p>
					<div class="header-button-box">
			               		<a href="javascript:show('1')" class="red-button-add add">添加客栈</a>  	
			        	<form id="mainForm" action="${ctx}/region/welcome" method="post" style="float: left">
			        		<input type="hidden" name="pageNo" id="pageNo" value="" />
			               	<input type="hidden" name="status" id="status" value=""/>
			               	<input type="hidden" name="useS" id="useStatus" value="${useS}"/>
			                <div class="search-box" style="margin-top: 20px">
			                	<input id="innName" name="condition" value="${condition}" type="text" class="search" placeholder="账号/客栈名称" maxlength="20">
			           			<input id="condition_button" type="button" class="search-button">
			                </div>
		                </form>
		        	</div>  	
		      </div>
		      <div class="content2">
	             <table border="0" cellpadding="0" cellspacing="0" class="room-date">
			          <tr>
			            <th scope="col">客栈名称</th>
			            <th scope="col">账号</th>
			            <th scope="col">
			            	<select id="useStatu">
			            		<option name="使用状况">使用状况</option>
			            		<option name="未使用">未使用</option>
			            		<option name="正在使用">正在使用</option>
			            	</select>
			            </th>
			            <th scope="col">注册日期</th>
			            <th scope="col">操作</th>
			          </tr>
			          <c:if test="${not empty page.result}">
				          <c:forEach items="${page.result}" var="t">
					           <tr>
						            <td><input type="hidden" id="${t.getId()}" value="${t.getName()}"/>${t.getName()}</td>
						            <td>${t.getContact()}</td>
						            <td>${t.getStatus()==0?"未使用":"正在使用"}</td>
						            <td><fmt:formatDate value="${t.getRegdate()}" pattern="yyyy-MM-dd"/></td>
						            <td><a href="javascript:void(0)"  onclick="deleted(${t.getId()})">删除</a></td>
					          </tr>
				          </c:forEach>
			          </c:if>
       			</table>
        	</div>
        	<div class="page-list">
        		<tags:pagination page="${page}" paginationSize="5" />
        	</div>
		</div>
	</body>
</html>

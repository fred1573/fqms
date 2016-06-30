<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
    <title>权限管理</title>
    <script src="${ctx}/js/ztree/jquery.ztree.all-3.5.min.js" type="text/javascript"></script>
    <script src="${ctx}/js/common/layer/layer.js" type="text/javascript"></script>
    <script>
        var ctx = "${ctx}"
    </script>
    <link rel="stylesheet" href="${ctx}/css/zTreeStyle/zTreeStyle.css" type="text/css">
    <link rel="stylesheet" href="${ctx}/js/common/layer/skin/layer.css" type="text/css">
    <style>
        .ztree li {
            margin: 20px 0;
        }
        .container-right {
            display: table;
            background: #F2F2F2;
        }
        #roleTree {
            display: table-cell;
            width: 10%;
            border-right: 3px solid #ccc;
            height: 100%;
            vertical-align:top;
        }
        .roleTreeContent {
            display: table-cell;
            width: 90%;
            padding: 0 15px;
        }
        .content-header {
            width: 100%;
            margin-top: 15px;
        }
        .red-button-add,
        .search-box {
            float: right;
        }
        .search-box {
            margin-left: 15px;
        }
        .department-info,
        .department-info-child {
            width: 100%;
            float: left;
            font-size: 14px;
            margin: 15px 0;
        }
        .department-info table {
            width: 100%;
            font-weight: bold;
        }
        .department-info table a {
            color: blue;
        }
        .department-info-child table{
            width: 100%;
        }
        .department-info-child a {
            color: blue;
            padding: 10px;
        }
        table {
            border-collapse:collapse;
        }
        .department-info-child {
            margin: 0;
        }
        .department-info-child table td,
        .department-info-child table th {
            text-align: center;
            border: 1px solid #ccc;
            padding: 10px 0;
        }
        .Permissiontitle{
            padding: 0 10px;
            float: left;
            width: 100%;
            box-sizing: border-box;
            font-weight: bold;
        }
        .department {
            display: table;
        }
        .department>div {
            display: table-cell;
            vertical-align: top;
            padding-right: 10px;
            padding-top: 10px;
        }
        .department>div:first-child {
            width: 130px;
            padding-left: 10px;
        }
        .department>div>label {
            margin: 0px 10px 0 0px;
        }
        .save-permission-add,
        .save-permission-modify {
            display: none;
            width: 100px;
            height: 28px;
            line-height: 28px;
            text-align: center;
            background: #70A847;
            display: inline-block;
            color: #fff;
            margin: 20px 0 20px 320px;
        }
        .red-tips {
            color: red;
            margin-left: 145px;
        }
        .pager {
            float: left;
            position: relative;
            left: 50%;
        }
        .pager li {
            position: relative;
            right: 50%;
            float: left;
            background: #ccc;
            margin: 5px;
            padding: 10px 15px;
            cursor: pointer;
        }
        .pager li:hover,
        .pager li.active {
            background: #d70007;
            color: #fff;
        }
        .role-permission {
            display: none;
        }
        .PermissionBody {
            height: 350px;
            overflow: auto;
            float: left;
        }
    </style>
</head>
<body>
<div class="container-right">
    <div id="roleTree" class="team2-left ztree"></div>
    <div class="roleTreeContent">
        <div class="content-header">
            <a class="red-button-add add" class="red-button-add add" style="float:left;" onclick="addDepartment('add')">新增部门</a>
            <a class="red-button-add add" onclick="addUser('add')">添加成员</a>
            <input type="hidden" name="pageNo" id="pageNo" value="1">
            <input type="hidden" name="orderBy" id="orderBy" value="id">
            <input type="hidden" name="order" id="order" value="asc">
            <input type="hidden" name="roleId" id="roleId" value="2">
            <input type="hidden" name="roleName" id="roleName" value="技术部">
            <div class="search-box">
                <input name="sysUserCode" value="" type="text" class="search sysUserCode" placeholder="代号/姓名" maxlength="20">
                <input id="sysUserCode_button" type="button" class="search-button">
            </div>
        </div>

        <div class="department-info">
            <table>
                <tr>
                    <td id="role_name"></td>
                    <td id="role_createUserCode"></td>
                    <td id="role_createTime"></td>
                    <input type="hidden"  id="currentRoleId" value="${currentUser.rootRole.id}">
                    <input type="hidden"  id="currentUserId" value="${currentUser.id}">
                    <td class="role-permission"><a onclick="setPermission()">设置权限</a></td>
                    <td class="role-permission"><a id="isEnabled"></a></td>
                    <td class="role-permission"><a onclick="addDepartment('modify')">修改</a></td>
                    <td class="role-permission"><a onclick="roolDelete()">删除</a></td>
                </tr>
            </table>
        </div>
        <div class="department-info-child">
            <table>
                <thead>
                <tr>
                    <th>番茄代号</th>
                    <th>姓名</th>
                    <th>最后登录时间</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody id="user">
                </tbody>
            </table>
            <div style="text-align: center; width: 100%; margin: 10px 0; overflow: hidden">
                <ul class="pager" id="pager">
                </ul>
            </div>
        </div>
    </div>
</div>

<%--新增成员弹窗--%>
<div id="add_user_div" class="center-box" style="display: none;">
    <form id="inputForm" action="/account/user/save" method="post">
        <input type="hidden" id="id" name="id">
        <input type="hidden" id="user_roleId_init" value="1">
        <input type="hidden" id="user_roleName_init" value="番茄来了科技有限公司">
        <input type="hidden" id="user_roleId_input" name="newRoleId" >
        <input type="hidden" id="oldLoginCode" name="oldLoginCode">
        <div class=" center-box-in team">
            <a class="close-window" onclick="closeWindow('add_user_div')"></a>
            <h1>添加/修改成员</h1>
            <ul>
                <li>
                    <dd>所属部门</dd>
                    <span id="roleName_span" class="select-box"></span>
                    <em class="down-select"></em>
                    <ol id="user_roles_ol" style="display:none">
                    </ol>
                </li>

                <li><dd>番茄代号</dd><input type="text" id="sysUserCode" name="sysUserCode" title="由中英文、数字或者下划线组成的字符串" maxlength="25"></li>
                <li>
                    <dd>姓&nbsp;&nbsp;名</dd>
                    <input type="text" id="sysUserName" name="sysUserName" maxlength="20">
                </li>
                <li id="sysUserPwd_li">
                    <dd>密&nbsp;&nbsp;码</dd>
                    <input type="password" id="sysUserPwd" name="sysUserPwd" maxlength="18"></li>
                <li id="mobile_li">
                    <dd>手&nbsp;&nbsp;机</dd>
                    <input type="text" id="mobile" name="mobile" maxlength="11"></li>
                <li>
                    <a id="add_user_button" href="#" class="green-button-ok">确&nbsp;&nbsp;&nbsp;&nbsp;定</a>
                    <a id="modify_user_button" href="#" class="green-button-ok">确&nbsp;&nbsp;&nbsp;&nbsp;定</a>
                    <em class="error-tips" style="display:none"></em>
                </li>
            </ul>
        </div>
    </form>
</div>
<%--添加部门弹窗--%>
<%--<form id="roleForm" action="<c:url value="/role/save"/> " method="post">--%>
<div id="add_role_div" class="center-box" style="display:none;">
    <div class=" center-box-in team2">
        <a class="close-window" onclick="closeWindow('add_role_div')"></a>
        <div id="roleTree1" class="team2-left ztree"><li id="roleTree_1" class="level0" tabindex="0" hidefocus="true" treenode=""><span id="roleTree_1_switch" title="" class="button level0 switch root_open" treenode_switch=""></span><a id="roleTree_1_a" class="level0" treenode_a="" onclick="" target="_blank" style="" title="番茄来了科技有限公司"><span id="roleTree_1_ico" title="" treenode_ico="" class="button ico_open" style="width:0px;height:0px;"></span><span id="roleTree_1_span">番茄来了科技有限公司</span></a><ul id="roleTree_1_ul" class="level0 " style="display:block"><li id="roleTree_2" class="level1" tabindex="0" hidefocus="true" treenode=""><span id="roleTree_2_switch" title="" class="button level1 switch center_docu" treenode_switch=""></span><a id="roleTree_2_a" class="level1" treenode_a="" onclick="" target="_blank" style="" title="财务部"><span id="roleTree_2_ico" title="" treenode_ico="" class="button ico_docu" style="width:0px;height:0px;"></span><span id="roleTree_2_span">财务部</span></a></li><li id="roleTree_3" class="level1" tabindex="0" hidefocus="true" treenode=""><span id="roleTree_3_switch" title="" class="button level1 switch center_docu" treenode_switch=""></span><a id="roleTree_3_a" class="level1" treenode_a="" onclick="" target="_blank" style="" title="市场部"><span id="roleTree_3_ico" title="" treenode_ico="" class="button ico_docu" style="width:0px;height:0px;"></span><span id="roleTree_3_span">市场部</span></a></li><li id="roleTree_4" class="level1" tabindex="0" hidefocus="true" treenode=""><span id="roleTree_4_switch" title="" class="button level1 switch center_docu" treenode_switch=""></span><a id="roleTree_4_a" class="level1" treenode_a="" onclick="" target="_blank" style="" title="市场审核部"><span id="roleTree_4_ico" title="" treenode_ico="" class="button ico_docu" style="width:0px;height:0px;"></span><span id="roleTree_4_span">市场审核部</span></a></li><li id="roleTree_5" class="level1" tabindex="0" hidefocus="true" treenode=""><span id="roleTree_5_switch" title="" class="button level1 switch center_docu" treenode_switch=""></span><a id="roleTree_5_a" class="level1" treenode_a="" onclick="" target="_blank" style="" title="客服"><span id="roleTree_5_ico" title="" treenode_ico="" class="button ico_docu" style="width:0px;height:0px;"></span><span id="roleTree_5_span">客服</span></a></li><li id="roleTree_6" class="level1" tabindex="0" hidefocus="true" treenode=""><span id="roleTree_6_switch" title="" class="button level1 switch center_docu" treenode_switch=""></span><a id="roleTree_6_a" class="level1" treenode_a="" onclick="" target="_blank" style="" title="产品部"><span id="roleTree_6_ico" title="" treenode_ico="" class="button ico_docu" style="width:0px;height:0px;"></span><span id="roleTree_6_span">产品部</span></a></li><li id="roleTree_7" class="level1" tabindex="0" hidefocus="true" treenode=""><span id="roleTree_7_switch" title="" class="button level1 switch center_open" treenode_switch=""></span><a id="roleTree_7_a" class="level1" treenode_a="" onclick="" target="_blank" style="" title="运营部"><span id="roleTree_7_ico" title="" treenode_ico="" class="button ico_open" style="width:0px;height:0px;"></span><span id="roleTree_7_span">运营部</span></a><ul id="roleTree_7_ul" class="level1 line" style="display:block"><li id="roleTree_8" class="level2" tabindex="0" hidefocus="true" treenode=""><span id="roleTree_8_switch" title="" class="button level2 switch bottom_open" treenode_switch=""></span><a id="roleTree_8_a" class="level2" treenode_a="" onclick="" target="_blank" style="" title="技术部"><span id="roleTree_8_ico" title="" treenode_ico="" class="button ico_open" style="width:0px;height:0px;"></span><span id="roleTree_8_span">技术部</span></a><ul id="roleTree_8_ul" class="level2 " style="display:block"><li id="roleTree_9" class="level3" tabindex="0" hidefocus="true" treenode=""><span id="roleTree_9_switch" title="" class="button level3 switch bottom_docu" treenode_switch=""></span><a id="roleTree_9_a" class="level3" treenode_a="" onclick="" target="_blank" style="" title="测试部"><span id="roleTree_9_ico" title="" treenode_ico="" class="button ico_docu" style="width:0px;height:0px;"></span><span id="roleTree_9_span">测试部</span></a></li></ul></li></ul></li><li id="roleTree_10" class="level1" tabindex="0" hidefocus="true" treenode=""><span id="roleTree_10_switch" title="" class="button level1 switch bottom_docu" treenode_switch=""></span><a id="roleTree_10_a" class="level1" treenode_a="" onclick="" target="_blank" style="" title="电销组"><span id="roleTree_10_ico" title="" treenode_ico="" class="button ico_docu" style="width:0px;height:0px;"></span><span id="roleTree_10_span">电销组</span></a></li></ul></li></div>
        <div class="team2-right">
            <h1 class="adddepartmenth1">添加部门</h1>
            <h1 class="modifydepartmenth1">修改部门</h1>
            <ul>
                <li id="parent_role_li">
                    <dd>上级部门</dd>
                    <input type="hidden" id="parentId" name="parentId" value="">
                    <input type="text" id="parentName" name="parentName" readonly="readonly" class="ipt" placeholder="请在左侧角色树中选择">
                </li>
                <li><dd>部门名称</dd><input type="text" id="sysRoleName" name="sysRoleName" class="validate[required,maxSize[10]] ipt" maxlength="10"></li>
                <li><textarea id="rmk" name="rmk" class="validate[maxSize[200]]" placeholder="部门描述"></textarea></li>
                <li>
                    <a id="nextStep" class="green-button-ok">下一步</a>
                    <a id="add_role_button" class="green-button-ok">确定</a>
                    <em class="error-tips" style="display:none"></em>
                </li>
            </ul>
        </div>
    </div>
</div>
<div id="setPermission" class="center-box" style="display: none">
    <div class="center-box-in user-reset-password" style="width: 700px;height: auto">
        <a class="close-window" onclick="closeWindow('setPermission')"></a>
        <h1>设置权限</h1>
        <div class="Permissiontitle">
            <div style="float: left">模块名称</div>
            <div><label style="float: right"><input type="checkbox" id="checkAllPermission"> 选取全部</label></div>
        </div>
        <div class="PermissionBody" id="PermissionBody">
            <c:forEach var="menu" items="${menuList}">
                <div class="department">
                    <div><label><input type="checkbox" class="department-checkall">${menu.name} </label></div>
                    <div class="department-children">
                        <c:forEach var="authority" items="${menu.authorityList}">
                            <label>
                                    <%-- <input type="hidden" &lt;%&ndash;name="authorityList[].id"&ndash;%&gt; name="permission" data-name="authorityList[].id"/>--%>
                                <input type="checkbox" name="permission" data-id="${authority.id}" value="${authority.sysAuthorityName}">${authority.sysAuthorityName}
                            </label>
                        </c:forEach>
                    </div>
                </div>
            </c:forEach>
        </div>

        <label><a class="save-permission-add" >确定保存</a><a class="save-permission-modify" >确定保存</a></label><label class="red-tips">*请至少选择一个权限</label>
    </div>
</div>
<%--
</form>
--%>
<%--修改密码弹窗--%>
<div id="modify_pwd_div1" class="center-box" style="display: none">
    <form id="modifyPwdForm" action="/account/user/saveNewPwd" method="post">
        <input type="hidden" name="userName" id="modifyPwd_userName" value="番茄猫">
        <div class="center-box-in user-reset-password">
            <a class="close-window" onclick="closeWindow('modify_pwd_div1')"></a>
            <h1>修改个人密码</h1>
            <ul>
                <li><dd>新密码</dd><input type="password" id="modifyPwd_sysUserPwd" name="sysUserPwd" class="validate[required,custom[username],minSize[6],maxSize[18]] ipt" maxlength="18"></li>
                <li><dd>请确认</dd><input type="password" id="modifyPwd_sysUserPwd_confirm" name="sysUserPwd_confirm" class="validate[required,equals[modifyPwd_sysUserPwd]] ipt" maxlength="18"></li>
                <li>
                    <a id="modify_pwd_button" href="#" class="green-button-ok">确&nbsp;&nbsp;&nbsp;&nbsp;定</a>
                    <em class="error-tips" style="display:none"></em>
                </li>
            </ul>
        </div>
    </form>
</div>
<div id="modify_pwd_div1" class="center-box" style="display: none">
    <form id="modifyPwdForm" action="/account/user/saveNewPwd" method="post">
        <input type="hidden" name="userName" id="modifyPwd_userName" value="番茄猫">
        <div class="center-box-in user-reset-password">
            <a class="close-window" onclick="closeWindow('modify_pwd_div1')"></a>
            <h1>修改个人密码</h1>
            <ul>
                <li><dd>新密码</dd><input type="password" id="modifyPwd_sysUserPwd" name="sysUserPwd" class="validate[required,custom[username],minSize[6],maxSize[18]] ipt" maxlength="18"></li>
                <li><dd>请确认</dd><input type="password" id="modifyPwd_sysUserPwd_confirm" name="sysUserPwd_confirm" class="validate[required,equals[modifyPwd_sysUserPwd]] ipt" maxlength="18"></li>
                <li>
                    <a id="modify_pwd_button" href="#" class="green-button-ok">确&nbsp;&nbsp;&nbsp;&nbsp;定</a>
                    <em class="error-tips" style="display:none"></em>
                </li>
            </ul>
        </div>
    </form>
</div>

<script>
    var setting = {
        view: {
            showIcon: false
        },
        data: {
            simpleData: {
                enable: false
            }
        },
        callback: {
            onClick: onClick
        }
    };
    var nodes = ${roleTreeJson};
    $.fn.zTree.init($("#roleTree"), setting, nodes);
    var treeObj = $.fn.zTree.getZTreeObj("roleTree");
    var node = treeObj.getNodeByParam("id", nodes.id);
    treeObj.expandAll(true);
    treeObj.selectNode(node);
    setting.callback.onClick(null, null, nodes,null)

    var setting1 = {
        view: {
            showIcon: false
        },
        data: {
            simpleData: {
                enable: false
            }
        },
        callback: {
            onClick: addDepartmentTree
        }
    };

    $.fn.zTree.init($("#roleTree1"), setting1, nodes);
    var treeObj1 = $.fn.zTree.getZTreeObj("roleTree1");
    treeObj1.expandAll(true);
    var authorityList,rmk,pageList;
    function onClick(event, treeId, treeNode, clickFlag) {
        var url = "/role/detail?id="+ treeNode.id.toString(),
                departmentUrl = "/role/getDescendant?id=1",
                department;
        $.post(departmentUrl,function(data) {
            if(data && data.result) {
                department = data.result;
                $("#user_roles_ol").html("")
                $.each(department,function(akey,aval) {
                    $("#user_roles_ol").append("<li data-id="+aval.id+">"+aval.sysRoleName+"</li>")
                })
            }else {
                alert("请求所有部门信息失败")
            }
        })
        $("#user").html("")
        $("#pager").html("")
        $("#role_name").attr("data-id", treeNode.id)
        if($("#currentRoleId").val() == treeNode.id.toString()) {
            $(".role-permission").hide();
        }else {
            $(".role-permission").show();
        }
        $.post(url,function(data) {
            if(data && data.status == 200) {

                authorityList = data.result.role.authorityList;
                pageList = data.result.pageList;
                rmk = data.result.role.rmk
                if(data.result.role.status== "ENABLED") {
                    $("#isEnabled").html("禁用")
                    $("#isEnabled").on("click",function() {
                        roleDisable("enabled")
                    })
                }else {
                    $("#isEnabled").html("启用")
                    $("#isEnabled").on("click",function() {
                        roleDisable("unenabled")
                    })
                }

                $("#role_name").html(data.result.role.sysRoleName)
                $("#role_createUserCode").html("创建人:"+data.result.role.createUserCode)
                $("#role_createTime").html("创建时间:"+data.result.role.createTime)
                $.each(data.result.pageList,function(key,val){
                    if(this.lastLendedTime==null) {
                        this.lastLendedTime = ""
                    }
                    if(this.status == "DISABLE") {
                        $("#user").append("<tr><td>"+this.sysUserCode+"</td><td>"+this.sysUserName+"</td><td>"+this.lastLendedTime+"</td><td><input type='hidden' value="+this.sysUserName+" data-id="+this.id+" data-sysUserCode="+this.sysUserCode+" data-mobile="+this.mobile+"> <a onclick="+"roleDisableUser(this,'enabled')>启用</a><a onclick="+"addUser('modify',this)"+">修改成员</a><a onclick='roleDeleteUser(this)'>删除</a></td></tr>");
                    }else {
                        $("#user").append("<tr><td>"+this.sysUserCode+"</td><td>"+this.sysUserName+"</td><td>"+this.lastLendedTime+"</td><td><input type='hidden' value="+this.sysUserName+" data-id="+this.id+" data-sysUserCode="+this.sysUserCode+" data-mobile="+this.mobile+"> <a onclick="+"roleDisableUser(this,'unenabled')>禁用</a><a onclick="+"addUser('modify',this)"+">修改成员</a><a onclick='roleDeleteUser(this)'>删除</a></td></tr>");
                    }
                })
                if(data.result.pageList.length) {
                    page(data)
                }
            }else {
                alert("获取部门信息失败！请重试！")
            }
        })
    }
    function isMobile(str) {
        var reg = /^(13|14|15|18)[0-9]{9}$/;
        return reg.test(str);
    }
    function page(data) {
        $("#pager").html("")
        $("#pager").append("<li>首页</li><li>上一页</li>")
        for(var i=0;i<data.result.paginator.totalPages;i++) {
            if(i==0) {
                $("#pager").append("<li class='active'>"+(i+1)+"</li>")
            }else {
                $("#pager").append("<li>"+(i+1)+"</li>")
            }
        }
        $("#pager").append("<li>下一页</li><li>末页</li>")
    }
    function addDepartmentTree(event, treeId, treeNode, clickFlag) {
        $("#parentName").val(treeNode.name)
        $("#parentName").attr("data-id",treeNode.id)
        $("#parentId").val(treeNode.id)
    }
    $("#add_user_button").on("click",function() {
        if(!$("#roleName_span").html() || !$("#sysUserCode").val() || !$("#sysUserPwd").val() || !$("#mobile").val()) {
            alert("请将信息填写完整！")
            return;
        }
        var bool = isMobile($("#mobile").val());
        if(!bool) {
            alert("请填写正确的手机号码！")
            return;
        }
        var url = "/role/saveUser?roleId="+ $("#roleName_span").attr("data-id")+"&sysUserCode="+$("#sysUserCode").val()+"&sysUserName=" + $("#sysUserName").val() + "&sysUserPwd="+$("#sysUserPwd").val()+"&mobile="+$("#mobile").val()
        $.ajax({
            url : url,
            contentType : "application/json",
            headers:  {Accept: "application/json; charset=utf-8" },
            success : function(rs) {
                if(rs && rs.status == 200) {
                    $("#add_user_div").hide();
                    alert("添加成员成功！")
                    location.reload();
                }else {
                    alert("添加成员失败,原因："+rs.message)
                }
            },
            error : function(rs) {
                alert("添加成员失败！")
            }
        })
    })
    $("#modify_user_button").on("click",function() {
        if(!$("#roleName_span").html() || !$("#sysUserCode").val() || !$("#mobile").val()) {
            alert("请将信息填写完整！")
            return;
        }
        var url = "/role/updateUser?roleId="+ $("#roleName_span").attr("data-id")+"&id="+$("#sysUserCode").attr("ms-id")+"&sysUserCode="+$("#sysUserCode").val()+"&sysUserName=" + $("#sysUserName").val() + "&sysUserPwd="+$("#sysUserPwd").val()+"&mobile="+$("#mobile").val()
        $.ajax({
            url : url,
            type : "POST",
            contentType : "application/json",
            headers:  {Accept: "application/json; charset=utf-8" },
            success : function(rs) {
                if(rs && rs.status == 200) {
                    $("#add_user_div").hide();
                    alert("修改成员成功！")
                    location.reload()
                }else {
                    alert("修改成员失败,原因："+rs.message)
                }
            },
            error : function(rs) {
                alert("修改成员失败！")
            }
        })
    })
    function addUser(status,This) {
        var html = $("#role_name").html(),
                id = $("#role_name").attr("data-id")
        $("#roleName_span").html(html)
        $("#roleName_span").attr("data-id",id)
        $("#sysUserPwd").val("")
        if(status=='add') {
            $("#add_user_button").show();
            $("#modify_user_button").hide();
            $("#sysUserCode").val("")
            $("#sysUserName").val("")
            $("#mobile").val("")
            $("#roleName_span").attr('data-id',$("#role_name").attr('data-id'))
        }else {
            $("#sysUserPwd").val("")
            $("#add_user_button").hide();
            $("#modify_user_button").show();
            if(This) {
                var id = $(This).prevAll("input[type='hidden']").attr("data-id"),
                        sysusercode = $(This).prevAll("input[type='hidden']").attr("data-sysusercode"),
                        sysUserName = $(This).prevAll("input[type='hidden']").val(),
                        mobile = $(This).prevAll("input[type='hidden']").attr("data-mobile");
                $("#sysUserCode").val(sysusercode)
                $("#sysUserName").val(sysUserName)
                $("#sysUserCode").attr("ms-id",id)
                if(mobile==null) {
                    mobile = ""
                }
                $("#mobile").val(mobile)
            }
        }
        $("#add_user_div").show();
    }
    function addDepartment(addOrModify) {
        $("#rmk").val(rmk)
        $("#parentName").val("");
        $("#sysRoleName").val("");
        if(addOrModify=="add") {
            $(".adddepartmenth1").show()
            $(".modifydepartmenth1").hide()
            $("#nextStep").show();
            $("#add_role_button").hide();
            $("#add_role_div").show();
            $("#rmk").val("")
        } else {
            var node = treeObj1.getNodeByParam("id", $("#role_name").attr("data-id"));
            treeObj1.selectNode(node);
            setting1.callback.onClick(null, null, {id : $("#role_name").attr("data-id"),name : $("#role_name").html() },null)
            var sNodes = treeObj1.getSelectedNodes();
            if (sNodes.length > 0) {
                node = sNodes[0].getParentNode();
            }
            if (node) {
                $("#parentName").val(node.name)
                $("#parentName").attr("data-id",node.id)

                $("#sysRoleName").attr("data-id",$("#role_name").attr("data-id"))
                $("#rmk").val(rmk)
            }

            $("#sysRoleName").val($("#role_name").html())
            $(".adddepartmenth1").hide()
            $(".modifydepartmenth1").show()
            $("#nextStep").hide();
            $("#add_role_button").show();
            $("#add_role_div").show();
        }
    }
    function closeWindow(id) {
        $("#"+id).hide();
    }
    function modifyPassword() {
        $("#modify_pwd_div1").show();
    }
    function isCheckAll (len,len1) {
        if( len == len1){
            $("#checkAllPermission").prop("checked",true)
        }else {
            $("#checkAllPermission").prop("checked",false)
        }
    }
    function checkAll(This,$Children) {
        if( This.checked ) {
            $Children.prop("checked",true)
        } else {
            $Children.prop("checked",false)
        }
    }
    function setPermission() {
        $("#setPermission").show();
        $(".save-permission-add").hide();
        $(".save-permission-modify").show();
        $("input[name='permission']").prop("checked",false);
        $("#checkAllPermission").prop("checked",false);
        $(".department-checkall").prop("checked",false);
        $.each(authorityList,function(akey,aval) {
            $.each($("input[name='permission']"),function(bkey,bval) {
                if(aval.id == $(this).attr("data-id")) {
                    $(this).prop("checked",true)
                }
            })
        })
        if($("input[name='permission']:checked").length == $("input[name='permission']").length) {
            $("#checkAllPermission").prop("checked",true)
        }
        $.each($(".department"),function() {
            if($(this).find("input[name='permission']:checked").length == $(this).find("input[name='permission']").length) {
                $(this).find(".department-checkall").prop("checked",true);
            }
        })
    }
    //删除部门
    function roolDelete() {
        var message = "确认删除"+$("#role_name").html()+"?"
        layer.confirm(message, {
            btn: ['确认','取消'] //按钮
        }, function(){
            var url = "/role/delete?id=" + $("#role_name").attr("data-id")
            if(pageList.length) {
                alert("需将成员全部删除后，才能删除部门！")
            }
            $.post(url,function(rs) {
                if(rs && rs.status == 200){
                    layer.msg('删除成功！', {icon: 1});
                    location.reload()
                }else {
                    layer.msg('删除失败！', {icon: 2});
                    location.reload()
                }
            })
        });
    }
    //禁用启用部门
    function roleDisable(isEnabled) {
        if(isEnabled=='enabled') {
            var message = "确认禁用"+$("#role_name").html()+"?"
            layer.confirm(message, {
                btn: ['确认','取消'] //按钮
            }, function(){
                var url = "/role/disable?id=" + $("#role_name").attr("data-id")
                $.post(url,function(rs) {
                    if(rs && rs.status == 200){
                        layer.msg('禁用成功！', {icon: 1});
                        var id = $("#role_name").attr("data-id");
                        var name = $("#role_name").html()
                        setting.callback.onClick(null, null, {id :id ,name : name },null)
                    }else {
                        layer.msg('禁用失败！', {icon: 2});
                    }
                })
            });
        }else {
            var message = "确认启用"+$("#role_name").html()+"?"
            layer.confirm(message, {
                btn: ['确认','取消'] //按钮
            }, function(){
                var url = "/role/enabled?id=" + $("#role_name").attr("data-id")
                $.post(url,function(rs) {
                    if(rs && rs.status == 200){
                        layer.msg('启用成功！', {icon: 1});
                        var id = $("#role_name").attr("data-id");
                        var name = $("#role_name").html()
                        setting.callback.onClick(null, null, {id :id ,name : name },null)
                    }else {
                        layer.msg('启用失败！', {icon: 2});
                    }
                })
            });
        }

    }
    //禁用启用成员
    function roleDisableUser(This,isEabled) {
        //alert(isEabled)
        if(isEabled == 'enabled') {
            var message = "确认启用"+$(This).prevAll("input[type='hidden']").val()+"?"
            layer.confirm(message, {
                btn: ['确认','取消'] //按钮
            }, function(){
                var url = "/role/enabledUser?id=" + $(This).prevAll("input[type='hidden']").attr("data-id")
                $.post(url,function(rs) {
                    if(rs && rs.status == 200){
                        layer.msg('启用成功！', {icon: 1});
                        var id = $("#role_name").attr("data-id");
                        var name = $("#role_name").html()
                        setting.callback.onClick(null, null, {id :id ,name : name },null)
                    }else {
                        layer.msg('启用失败！', {icon: 2});
                    }
                })
            });
        }else {
            var message = "确认禁用"+$(This).prevAll("input[type='hidden']").val()+"?"
            layer.confirm(message, {
                btn: ['确认','取消'] //按钮
            }, function(){
                var url = "/role/disableUser?id=" + $(This).prevAll("input[type='hidden']").attr("data-id")
                $.post(url,function(rs) {
                    if(rs && rs.status == 200){
                        layer.msg('禁用成功！', {icon: 1});
                        var id = $("#role_name").attr("data-id");
                        var name = $("#role_name").html()
                        setting.callback.onClick(null, null, {id :id ,name : name },null)
                    }else {
                        layer.msg('禁用失败！', {icon: 2});
                    }
                })
            });
        }
    }
    //删除启用成员
    function roleDeleteUser(This) {
        var message = "确认删除"+$(This).prevAll("input[type='hidden']").val()+"?"
        layer.confirm(message, {
            btn: ['确认','取消'] //按钮
        }, function(){
            var url = "/role/deleteUser?id=" + $(This).prevAll("input[type='hidden']").attr("data-id")
            $.post(url,function(rs) {
                if(rs && rs.status == 200){
                    layer.msg('删除成功！', {icon: 1});
                    // location.reload()
                }else {
                    layer.msg('删除失败！', {icon: 2});
                }
            })
        });
    }
    function curruntPage (curruntPage) {
        var url = "/role/search?id="+$("#role_name").attr("data-id")+"&currentPage="+curruntPage;
        $.post(url,function(data) {
            seachAndPage(data)
        })
    }
    $("#checkAllPermission").on("click",function() {
        if( this.checked ) {
            $("input[name='permission']").prop("checked",true)
            $(".department-checkall").prop("checked",true)
        } else {
            $("input[name='permission']").prop("checked",false)
            $(".department-checkall").prop("checked",false)
        }
    })
    $(".department-checkall").on("click",function() {
        var index = $(".department-checkall").index(this);
        var children = $(".department-children").eq(index).find("input[name='permission']")
        checkAll(this,children)
        var len = $(".PermissionBody").find("input[type='checkbox']:checked").length,
                len1 = $(".PermissionBody").find("input[type='checkbox']").length
        if(len == len1){
            $("#checkAllPermission").prop("checked",true)
        }else {
            $("#checkAllPermission").prop("checked",false)
        }
    })
    $("input[name='permission']").on("click",function() {
        var len = $("input[name='permission']:checked").length;
        var len1 = $("input[name='permission']").length;
        var index = $(".department-children").index($(this).parents(".department-children"))
        var l = $(".department-children").eq(index).find("input[name='permission']").length;
        var l1 = $(".department-children").eq(index).find("input[name='permission']:checked").length
        if(l==l1) {
            $(".department-checkall").eq(index).prop("checked",true)
        } else {
            $(".department-checkall").eq(index).prop("checked",false)
        }
        isCheckAll(len,len1)
    })
    $("#nextStep").on("click",function() {
        $(".save-permission-add").show();
        $(".save-permission-modify").hide();
        $(".PermissionBody").find("input[type='checkbox']").prop("checked",false)
        if($("#parentName").val()=="") {
            alert("请在左侧树中选择部门！");
            return;
        }
        if($("#sysRoleName").val()=="") {
            alert("请填写部门名称！");
            return;
        }
        $("#add_role_div").hide();
        $("#setPermission").show();
    })
    $(".save-permission-add").click(function(){
        var url = "/role/save"
        var data = {
            "parentId" : $("#parentId").val(),
            "sysRoleName" : $("#sysRoleName").val(),
            "rmk" : $("#rmk").val(),
            "authorityList": []
        }
        $.each($("input[name='permission']:checked"),function() {
            data.authorityList.push({
                id : $(this).attr("data-id"),
                sysAuthorityName : $(this).val()
            })
        })
        $.ajax({
            "type": "post",
            "dataType": "json",
            "data":{
                "data" : JSON.stringify(data)
            },
            "url" : url,
            success : function(rs) {
                if(rs && rs.status == 200) {
                    $("#add_user_div").hide();
                    alert("修改部门成功！")
                    location.reload()
                }else {
                    alert("修改部门失败,原因："+rs.message)
                }
            },
            error : function(rs) {
                alert("修改部门失败！")
            }
        })

    })
    $(".save-permission-modify").on("click",function() {
        var url = "/role/updateAuthority?roleId="+ $("#role_name").attr("data-id") + "&authorityIds=",
                authorityIds = "";
        $.each($("input[name='permission']:checked"),function(key,val) {
            var id = $(this).attr("data-id")
            if(key == $("input[name='permission']:checked").length-1) {
                authorityIds+=id
            }else {
                authorityIds+=(id+",");
            }
        })
        url += authorityIds;
        $.post(url,function(rs) {
            if(rs && rs.status== 200) {
                alert("保存成功！")
                $("#setPermission").hide();
            }else {
                alert("保存失败！")
            }
        })

    })
    $("#sysUserCode_button").on("click",function() {
        var url = "/role/search?id="+$("#role_name").attr("data-id")+"&likeName="+$("input[name='sysUserCode']").val();
        $.post(url,function(data) {
            seachAndPage(data,'search')
        })
    })
    $("input[name='sysUserCode']").keyup(function(){
        if(event.keyCode == 13){
            var url = "/role/search?id="+$("#role_name").attr("data-id")+"&likeName="+$("input[name='sysUserCode']").val();
            $.post(url,function(data) {
                seachAndPage(data,'search')
            })
        }
    });

    //搜索和分页数据操作
    function seachAndPage(data,search) {
        if(data && data.status == 200) {
            $("#user").html("")
            if(data.result.pageList.length==0) {
                $("#user").append("<tr><td colspan='4'>无查询信息</td></tr>")
                $("#pager").html("")
            }else {
                $.each(data.result.pageList,function() {
                    if(this.lastLendedTime == null) {
                        this.lastLendedTime = ""
                    }
                    if(this.status == "DISABLE") {
                        $("#user").append("<tr><td>"+this.sysUserCode+"</td><td>"+this.sysUserName+"</td><td>"+this.lastLendedTime+"</td><td><input type='hidden' value="+this.sysUserName+" data-id="+this.id+" data-sysUserCode="+this.sysUserCode+" data-mobile="+this.mobile+"> <a onclick="+"roleDisableUser(this,'enabled')>启用</a><a onclick="+"addUser('modify',this)"+">修改成员</a><a onclick='roleDeleteUser(this)'>删除</a></td></tr>");
                    }else {
                        $("#user").append("<tr><td>"+this.sysUserCode+"</td><td>"+this.sysUserName+"</td><td>"+this.lastLendedTime+"</td><td><input type='hidden' value="+this.sysUserName+" data-id="+this.id+" data-sysUserCode="+this.sysUserCode+" data-mobile="+this.mobile+"> <a onclick="+"roleDisableUser(this,'unenabled')>禁用</a><a onclick="+"addUser('modify',this)"+">修改成员</a><a onclick='roleDeleteUser(this)'>删除</a></td></tr>");
                    }
                })
                if(search) {
                    page(data)
                }
            }
        }else {
            alert("查询出错，请重试！")
        }
    }
    //分页
    $("#pager").on("click","li",function() {
        if($(this).html() == '首页') {
            $("#pager li").removeClass('active');
            $("#pager li").eq(2).addClass('active')
            if( $("#pager li").length > 5) {
                curruntPage(1)
            }

        }else if($(this).html() == '末页') {
            $("#pager li").removeClass('active');
            var len = $("#pager li").length -3;
            if( $("#pager li").length > 5) {
                curruntPage(len-1)
            }
            $("#pager li").eq(len).addClass('active')
        }else if($(this).html() == '上一页') {
            var index = $("#pager").find('.active').index();
            if(index > 2 ) {
                $("#pager li").removeClass('active');
                $("#pager li").eq(index-1).addClass('active')
                if( $("#pager li").length > 5) {
                    curruntPage(index-2)
                }
            }
        }else if($(this).html() == '下一页') {
            var index = $("#pager").find('.active').index();
            if( index < $("#pager li").length -3 ) {
                $("#pager li").removeClass('active');
                $("#pager li").eq(index+1).addClass('active')
                if( $("#pager li").length > 5) {
                    curruntPage(index)
                }
            }
        }else {
            if($("#pager").find('.active').index() == $(this).index()) {
                return;
            }else {
                $("#pager li").removeClass('active');
                $(this).addClass('active')
                var num = $(this).html()
                curruntPage(num)
            }
        }
    })
    $("#add_role_button").on("click",function() {
        var url = "/role/update.json"
        var data = {
            id : $("#sysRoleName").attr("data-id"),
            parentId: $("#parentName").attr("data-id"),
            rmk: $("#rmk").val(),
            sysRoleName: $("#sysRoleName").val()
        }
        $.post(url,{data : JSON.stringify(data)},function(rs) {
            if(rs && rs.status==200) {
                $("#add_role_div").hide();
                alert("修改部门成功！")
                location.reload();
            }else {
                alert("修改部门失败！"+rs.message)
            }
        })
    })
    $("#user_roles_ol").on("click","li",function() {
        $("#roleName_span").attr("data-id",$(this).attr("data-id"))
    })
</script>
</body>
</html>

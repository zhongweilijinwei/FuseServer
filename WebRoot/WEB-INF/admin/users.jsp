<%--
  Created by IntelliJ IDEA.
  User: xiaohei
  Date: 2015/8/22
  Time: 14:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String path = request.getContextPath();
  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;

%>
<base href="<%=basePath%>">
<html>
<head>
  <title></title>

  <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/default/easyui.css">
  <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/icon.css">
  <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/color.css">
  <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/u8server.css">

  <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.min.js"></script>
  <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.easyui.min.js"></script>
  <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/locale/easyui-lang-zh_CN.js"></script>
 <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/plugins/jquery.calendar.js"></script>

</head>
<body>
<div id="users">

</div>

<div id="easyui_toolbar" region="north" border="false"
     style="border-bottom: 1px solid #ddd; height: 32px; padding: 2px 5px; background: #fafafa;">
  <%--<div style="float: left;">--%>
    <%--<a class="easyui-linkbutton" plain="true" icon="icon-filter" onclick="javascript:showAddDialog();">详细信息</a>--%>
  <%--</div>--%>

  <%--<div class="datagrid-btn-separator"></div>--%>

  <div style="float: left;">
     <input id="channelID" type="hidden" name="channelID" value="-1" />
    <a class="easyui-linkbutton" plain="true"
       icon="icon-remove" onclick="javascript:deleteUser();">删除</a>
    &nbsp;选择时间段: &nbsp;
    <input type="text" id="startDate" name="startDate" class="easyui-datetimebox" style="width:135px"/>
     &nbsp;至  &nbsp;
    <input type="text" id="endDate" name="endDate"  class="easyui-datetimebox" style="width:135px"/>
   &nbsp;选择渠道:
     <input id="channels" type="text" class="easyui-combobox" name="channels" style="width:180px" maxlength="255" required="true"/>
   &nbsp;选择游戏:
     <input id="games" type="text" class="easyui-combobox" name="games" style="width:90px" maxlength="255" required="true"/>
  &nbsp;渠道用户名:
   <input id="channelUserName" type="text" class="easyui-textbox" name="channelUserName" style="width:180px" maxlength="255" required="true"/>
     <a class="easyui-linkbutton" plain="true"
       icon="icon-search" onclick="javascript:doSearch();">搜索</a>
   
  </div>
<!--  
  <div id="tb" style="float: right;">
    <input id="search_box" class="easyui-searchbox" style="width: 250px"  data-options="searcher:doSearch,prompt:'请输入查询词',menu:'#search_menu'" />
    <div id="search_menu" style="width:120px">
      <div data-options="name:'user_name'">用户名</div>
      <div data-options="name:'user_id'">渠道userID</div>
      <div data-options="name:'game'">所属游戏</div>
      <div data-options="name:'channel'">所属渠道</div>
    </div>
  </div>
-->
</div>


<script type="text/javascript">



  function deleteUser(){
    var row = $('#users').datagrid('getSelected');
    if(row){
      $.messager.confirm(
              '操作确认',
              '确定要删除该用户吗？(操作不可恢复)',
              function(r){
                if(r){
                  $.post('<%=basePath%>/admin/users/removeUser', {currUserID:row.id}, function(result){
                    if (result.state == 1) {
                      $("#users").datagrid('reload');
                    }

                    $.messager.show({
                      title:'操作提示',
                      msg:result.msg
                    })

                  }, 'json');
                }
              }
      );
    }else{
      $.messager.show({
        title:'操作提示',
        msg:'请选择一条记录'
      })
    }
  }



  function doSearch(){
    //alert("value:"+value+";name:"+name);
    
    search('<%=basePath%>/admin/users/search');
  }

  $("#users").datagrid({
    height:430,
    url:'<%=basePath%>/admin/users/getAllUsers',
    method:'POST',
    idField:'id',
    striped:true,
    fitColumns:true,
    singleSelect:true,
    rownumbers:true,
    pagination:true,
    nowrap:true,
    loadMsg:'数据加载中...',
    pageSize:10,
    pageList:[10,20,50,100],
    showFooter:true,
    columns:[[
      {field:'name', title:'名称', width:80, sortable:true},
      {field:'appName', title:'游戏', width:40, sortable:true},
      {field:'channelName', title:'所属渠道', width:40, sortable:true},
      {field:'channelUserID', title:'渠道userID', width:60, sortable:true},
      {field:'channelUserName', title:'渠道用户名', width:50, sortable:true},
      {field:'channelUserNick', title:'用户昵称', width:50, sortable:true},
      {field:'lastLoginTime', title:'最后登录时间', width:70, sortable:true},
      {field:'createTime', title:'注册时间', width:70, sortable:true}
    ]],
    toolbar:'#easyui_toolbar'
  });
   
   
function search(searchURL){
var startTime = $('#startDate').datetimebox('getValue');
var endTime = $('#endDate').datetimebox('getValue');
var channelID=$('#channels').textbox('getValue');
var uName= $('#channelUserName').textbox('getValue');
var appId = $('#games').textbox('getValue');

 // alert(channelID);

  searchURL=searchURL+ "?appId=" + appId + "&channelUserName="+uName+"&channelID="+channelID+"&startDate="+startTime+"&endDate="+endTime;
  

  $("#users").datagrid({
    height:430,
    url:searchURL,
    method:'POST',
    idField:'id',
    striped:true,
    fitColumns:true,
    singleSelect:true,
    rownumbers:true,
    pagination:true,
    nowrap:true,
    loadMsg:'数据加载中...',
    pageSize:10,
    pageList:[10,20,50,100],
    showFooter:true,
    columns:[[
      {field:'name', title:'名称', width:80, sortable:true},
      {field:'appName', title:'游戏', width:40, sortable:true},
      {field:'channelName', title:'所属渠道', width:40, sortable:true},
      {field:'channelUserID', title:'渠道userID', width:60, sortable:true},
      {field:'channelUserName', title:'渠道用户名', width:50, sortable:true},
      {field:'channelUserNick', title:'用户昵称', width:50, sortable:true},
      {field:'lastLoginTime', title:'最后登录时间', width:70, sortable:true},
      {field:'createTime', title:'注册时间', width:70, sortable:true}
    ]],
    toolbar:'#easyui_toolbar'
  });

}   
   
   
   function fillChannel(){
    $("#channels").combobox({
    url:'<%=basePath%>/admin/channels/getAllChannelsSimple',
    valueField:'channelID',
    textField:'name',
   	 onSelect:function(rec){
      	$('#channelID').val(rec.channelID);
   	 }
  	});

}

function fillGames(){

    $("#games").combobox({
    url:'<%=basePath%>/admin/games/getAllGamesSimpleForList',
    valueField:'appID',
    textField:'name',
   	onSelect:function(rec){
      	$('#appID').val(rec.appID);
   	 }
  	});
}
   
   fillChannel();
   fillGames();
  //默认皮肤  
 $('#startDate').datetimebox({  
     showSeconds:false
      });
     $('#endDate').datetimebox({  
     showSeconds:false }); 
  
</script>

</body>
</html>

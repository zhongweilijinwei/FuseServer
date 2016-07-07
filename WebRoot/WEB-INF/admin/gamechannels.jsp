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

</head>
<body>
<div id="channels">

</div>

<div id="easyui_toolbar" region="north" border="false"
     style="border-bottom: 1px solid #ddd; height: 32px; padding: 2px 5px; background: #fafafa;">
  <div style="float: left;">
    <a class="easyui-linkbutton" plain="true" icon="icon-add" onclick="javascript:showAddDialog();">新增</a>
  </div>

  <div class="datagrid-btn-separator"></div>

  <div style="float: left;">
    <a class="easyui-linkbutton" plain="true" icon="icon-edit" onclick="javascript:showEditDialog();">编辑</a>
  </div>

  <div class="datagrid-btn-separator"></div>

  <div style="float: left;">
    <a class="easyui-linkbutton" plain="true"
       icon="icon-remove" onclick="javascript:deleteChannel();">删除</a>
  </div>

  <div id="tb" style="float: right;">
    <input id="search_box" class="easyui-searchbox" style="width: 250px"  data-options="searcher:doSearch,prompt:'请输入查询词',menu:'#search_menu'" />
    <div id="search_menu" style="width:120px">
      <div data-options="name:'channel_name'">渠道名称</div>
      <div data-options="name:'channel_id'">渠道商ID</div>
    </div>
  </div>

</div>

<div id="dialog_add" class="easyui-dialog u8_form"
     closed="true" buttons="#dlg-buttons" style="height: 300px;width: 500px;">
  <div class="ftitle">渠道信息</div>
  <form id="fm" method="post" novalidate>
    <input id="id" type="hidden" name="id" />
    <input id="appId" type="hidden" name="appId" />
    <input id="payChId" type="hidden" name="payChId" />
      <input id="loginChId" type="hidden" name="loginChId" />
    <div class="u8_form_row">
      <label >所属游戏：</label>
      <input id="games" type="text" class="easyui-combobox" name="allgames" maxlength="255" required="true"/>
    </div>

  
    <div class="u8_form_row">
      <label >游戏渠道编号：</label>
      <input id="code" type="text" value="111" class="easyui-textbox" name="code" maxlength="255" required="true" />
      <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="recommendChannelID()" style="width:70px">推荐</a>
    </div>
    <div class="u8_form_row">
      <label >登陆渠道号：</label>
      <input id="loginChannels" type="text" class="easyui-combobox" name="allLoginChannels" maxlength="255" required="true" />
    </div>

    <div class="u8_form_row">
      <label >支付渠道号：</label>
      <input   id="payChannels" type="text" class="easyui-combobox" name="allPayChannels" maxlength="255"  required="true"/>
    </div>
   

  </form>
</div>
<div id="dlg-buttons">
  <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="saveUser()" style="width:90px">保 存</a>
  <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dialog_add').dialog('close')" style="width:90px">取 消</a>
</div>


<script type="text/javascript">

  var url;
  //必须的渠道商
  var mustMasterID=<%=request.getAttribute("mustMasterID")%>;
  
 
  function showAddDialog(){
    $("#dialog_add").window({
      top:($(window).height() - 400) * 0.5,
      left:($(window).width() - 500) * 0.5
    });

    $("#dialog_add").dialog('open').dialog('setTitle', '添加游戏渠道');

    $('#fm').form('clear');

    url = '<%=basePath%>/admin/gamechannels/addGameChannel';

  }

  function showEditDialog(){

    $("#dialog_add").window({
      top:($(window).height() - 400) * 0.5,
      left:($(window).width() - 500) * 0.5
    });


    var row = $('#channels').datagrid('getSelected');
    if(row){

      $("#dialog_add").dialog('open').dialog('setTitle', '编辑游戏渠道');
      $('#fm').form('load', row);
       url = '<%=basePath%>/admin/gamechannels/saveGameChannel';
      $('#games').combobox('select', row.appId);
      //alert("===="+row.loginChId);
      $('#payChannels').combobox('select', row.payChId);
      $('#loginChannels').combobox('select', row.loginChId);
      
     
		
    }else{
      $.messager.show({
        title:'操作提示',
        msg:'请选择一条记录'
      })
    }
  }

  function deleteChannel(){
    var row = $('#channels').datagrid('getSelected');
    if(row){
      $.messager.confirm(
              '操作确认',
              '确定要删除该游戏渠道吗？(操作不可恢复)',
              function(r){
                if(r){
                  $.post('<%=basePath%>/admin/gamechannels/removeGameChannel', {currGameChannelID:row.id}, function(result){
                    if (result.state == 1) {
                      $('#dialog_add').dialog('close');
                      $("#channels").datagrid('reload');
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

  function saveUser(){

    $('#fm').form('submit', {
      url:url,
      onSubmit:function(){
        return $(this).form('validate');
      },
      success:function(result){
        var result = eval('('+result+')');

        if (result.state == 1) {
          $('#dialog_add').dialog('close');
          $("#channels").datagrid('reload');
        }

        $.messager.show({
          title:'操作提示',
          msg:result.msg
        })
      }
    })

  }


  function recommendChannelID(){

    $.post('<%=basePath%>/admin/gamechannels/recommendGameChannelID', {}, function(result){
      if (result.state == 1) {
        $("#code").textbox('setValue', result.data);
      }else{
        alert(result.msg);
      }
    });

  }

  function doSearch(value, name){
    alert("value:"+value+";name:"+name);
  }

  $("#channels").datagrid({
    height:430,
    url:'<%=basePath%>/admin/gamechannels/getAllGameChannels',
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
    
    
      {field:'id', title:'ID', width:40, sortable:true},
      {field:'code', title:'编号', width:40, sortable:true},
      {field:'gameName', title:'游戏名', width:40, sortable:true},
      {field:'payChId', title:'支付渠道编号', width:40, sortable:true},
      {field:'payMaster', title:'支付渠道商', width:40, sortable:true},
      
      {field:'loginChId', title:'登录渠道编号', width:40, sortable:true},
      {field:'loginMaster', title:'登录渠道商', width:40, sortable:true},
    
    ]],
    toolbar:'#easyui_toolbar'
  });

  $("#games").combobox({
    url:'<%=basePath%>/admin/games/getAllGamesSimple',
    valueField:'appID',
    textField:'name',
    onSelect:function(rec){
      $('#appId').val(rec.appID);
      
      loadGameChannels(rec.appID);
    }
  });
  
  

 function fillGameChannels(jsonData){
 

 
   $("#payChannels").combobox({
   // url:'<%=basePath%>/admin/gamechannels/getChannelByGame?gameId='+gameAppId,
    valueField:'channelID',
    textField:'name',
 	data:jsonData,
    onSelect:function(rec){
      $('#payChId').val(rec.channelID);
   
    }
  });
  
     $("#loginChannels").combobox({
   // url:'<%=basePath%>/admin/gamechannels/getChannelByGame?gameId='+gameAppId,
 
    valueField:'channelID',
    textField:'name',
    data:jsonData,
    onSelect:function(rec){
      $('#loginChId').val(rec.channelID);
      	$('#payChannels').combobox('enable');
      	
      	
      if(getMasterId(jsonData,rec.channelID)==mustMasterID){
      
      
          //让支付的必须和 所选的一样
            $('#payChId').val(rec.channelID);
      	    $('#payChannels').combobox('setValue', mustMasterID);
      		$('#payChannels').combobox('setText', rec.name).combobox('disable');
      	   // $("#payChannels").attr("readonly", true);
      	    
           // $("#payChannels span input.combo-text").attr("readonly",true);
      	  //  $("#payChannels").combobox({ editable: false });
      }else{
            $('payChannels').combobox('enable');
      
      }
    }
  });
 

 }
 
 /**
 根据渠道获取渠道商获取编号
 **/
 function getMasterId(jsonData,channelID){
 
     if(jsonData){
     	for(var i=0;i<jsonData.length;i++){
     	
     	   if(jsonData[i].channelID==channelID){
     	   		return jsonData[i].masterID;
     	   }
     	}
     
     }
     return -1;
 }
 //加载 
  function loadGameChannels(gameAppId){

    $.post('<%=basePath%>/admin/gamechannels/getChannelByGame?gameId='+gameAppId, {}, function(result){
    
       if(result){
       
      
       fillGameChannels(result);
       }
    
    
    });

  }

</script>

</body>
</html>

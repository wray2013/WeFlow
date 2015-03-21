<%@ page contentType="text/html;charset=UTF-8"  pageEncoding="UTF-8"%>
<!doctype html>
<html >
<!--<![endif]-->
  <head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html charset=uft-8">
    <title>${self.title}</title>
    <link rel="icon" href="${base}/static/pic/favicon/favicon.ico" type="image/x-icon"/> 
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="keywords" content="${self.keywords}">
    <meta name="description" content="${self.description}">
    
    <!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
    <!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
    <!--[if !IE]><!-->
    <!-- Web Fonts  -->
    <link rel="stylesheet" href="http://fonts.useso.com/css?family=Open+Sans:300,400,600,700,800%7CShadows+Into+Light" type="text/css" />
    
    <!--GLOBAL MANDATORY STYLES -->
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/font-awesome/css/font-awesome.min.css" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/simple-line-icons/simple-line-icons.min.css" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/bootstrap/css/bootstrap.min.css" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/uniform/css/uniform.default.css" type="text/css" />
    
    <!-- Theme CSS -->
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/css/components.css" id="style_components" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/css/plugins.css" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/admin/layout/css/layout.css" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/admin/layout/css/themes/default.css" id="style_color" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/admin/layout/css/custom.css" type="text/css" />
   
    
    <!-- 当前页面插件样式 -->
    <c:if test="${not empty self.css.plugins}">${self.css.plugins}</c:if>
    <!-- 页面css -->
    <c:if test="${not empty self.css.main}">${self.css.main}</c:if>
  </head>
  <body>
  	<div class="${self.css.body}">
      <!--页面内容头-->
<%--       <%@ include file="/WEB-INF/views/themes/default/include/header.jsp"%> --%>
      <!--页面面包屑-->
      <c:if test="${not empty self.content.contentTitle}">${self.content.contentTitle}</c:if>
      
      <!--页面内容体 -->
      <c:if test="${not empty self.leftNavType}">
      <%-- 	<%@ include file="/WEB-INF/views/themes/default/include/accountLeft.jsp"%>	 --%>
      </c:if>
      <c:if test="${empty self.leftNavType}">
        <c:if test="${not empty self.content.main}"> ${self.content.main} </c:if>
      </c:if>
      
      <!--页面内容尾-->
      <%@ include file="/WEB-INF/views/themes/default/include/footer.jsp"%>
      <!--页面内容其他-->
     
    </div>
    <c:if test="${not empty self.content.free}"> ${self.content.free} </c:if>
    <!--bootstrap样式公用提示弹框      app.openL("提示"，"弹窗内容")-->
    <div class="modal fade" id="alert-modal" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
	  <div class="modal-dialog  alert-modal">
	    <div class="modal-content alert-modal-content">
	      <div class="modal-header alert-modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
	        <h4 class="modal-title alert-modal-title" id="alert-modal-title"> 提示/h4>
	      </div>
	      <div id="alert-modal-body" class="modal-body invest-modal-body"></div>
	    </div>
	  </div>
	</div>
	
    <!-- BEGIN CORE PLUGINS -->
    <!--[if lt IE 9]>
    <script src="../../assets/global/plugins/respond.min.js"></script>
    <script src="../../assets/global/plugins/excanvas.min.js"></script> 
    <![endif]-->
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/jquery.min.js" type="text/javascript"></script> 
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/jquery-migrate.min.js" type="text/javascript"></script> 
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script> 
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/jquery.blockui.min.js" type="text/javascript"></script> 
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/uniform/jquery.uniform.min.js" type="text/javascript"></script> 
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/jquery.cokie.min.js" type="text/javascript"></script> 
    <!-- END CORE PLUGINS -->
    
    <script>
	    var app = {
	      base: '${base}', 
	      version: '${version.app}', 
	      loginName: '${loginUser.loginName}',
	      currencySymbol: '${currencySymbol}'
	    };
		
    </script> 
    
    <!-- 当前页面插件js -->
    <c:if test="${not empty self.js.plugins}"> ${self.js.plugins} </c:if>
    
    <!-- Theme Custom --> 
    <script src="${base}/static/themes/default/js/app.js"></script>
	<%-- <script src="${base}/static/themes/default/js/include/header.js"></script> --%>

	<!--[if lte IE 9]>
	<script type="text/javascript">
		$(function($){
			$(".circular-bar-chart").addClass("manual");
			$(".circular-bar-chart").themePluginChartCircular({
				"barColor": "#0088CC",
				"size":90,
				"lineWidth":9,
				"animate":"{enabled:false}"
			});
		});
  
	</script>
  <script type="text/javascript">
    //判断浏览器是否支持 placeholder属性
    function isPlaceholder(){
      var input = document.createElement('input');
      return 'placeholder' in input;
    }

      if (!isPlaceholder()) {//不支持placeholder 用jquery来完成
          $(document).ready(function() {
              if(!isPlaceholder()){
                  $("input").not("input[type='password']").each(//把input绑定事件 排除password框
                      function(){
                          if($(this).val()=="" && $(this).attr("placeholder")!=""){
                              $(this).val($(this).attr("placeholder"));
                              $(this).focus(function(){
                                  if($(this).val()==$(this).attr("placeholder")) $(this).val("");
                              });
                              $(this).blur(function(){
                                  if($(this).val()=="") $(this).val($(this).attr("placeholder"));
                              });
                          }
                  });                  
              }
          });
        }
  </script>
	<![endif]-->
    
    <!-- 当前页面js -->
    <c:if test="${not empty self.js.main}"> ${self.js.main} </c:if>
  </body>
</html>
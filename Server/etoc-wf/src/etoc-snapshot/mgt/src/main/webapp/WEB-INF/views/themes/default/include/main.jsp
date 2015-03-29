<%@ page contentType="text/html;charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
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
    
    <!-- Web Fonts  -->
    <link rel="stylesheet" href="http://fonts.useso.com/css?family=Open+Sans:300,400,600,700,800%7CShadows+Into+Light" type="text/css" />
    
    <!--GLOBAL MANDATORY STYLES -->
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/font-awesome/css/font-awesome.min.css" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/simple-line-icons/simple-line-icons.min.css" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/bootstrap/css/bootstrap.min.css" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/uniform/css/uniform.default.css" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css"  type="text/css"/>

    
    <!-- Theme CSS -->
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/css/components.css" id="style_components" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/global/css/plugins.css" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/admin/layout2/css/layout.css" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/admin/layout2/css/themes/grey.css" id="style_color" type="text/css" />
    <link rel="stylesheet" href="${base}/static/libs/metronic/${version.metronic}/assets/admin/layout2/css/custom.css" type="text/css" />
   
    
    <!-- 当前页面插件样式 -->
    <c:if test="${not empty self.css.plugins}">${self.css.plugins}</c:if>
    <!-- 页面css -->
    <c:if test="${not empty self.css.main}">${self.css.main}</c:if>
  </head>
  <body class="${self.css.body}"> 
  	<c:if test="${self.headerbar}"> 
      <!--页面内容头-->
       <%@ include file="/WEB-INF/views/themes/default/include/header.jsp"%> 
      <!--页面面包屑-->
     </c:if>
      <c:if test="${not empty self.content.contentTitle}">${self.content.contentTitle}</c:if>
      
      <!--页面内容体 -->
      <c:if test="${not empty self.content.main}"> 
      <div class="container">
      	<div class="page-container">
      		<!-- BEGIN SIDEBAR -->
      		<c:if test="${self.sidebar}"> 
			 <div class="page-sidebar-wrapper">
      		   <%@ include file="/WEB-INF/views/themes/default/include/slidemenu.jsp"%>
      		 </div>
      		</c:if>
      		 <!-- END SIDEBAR -->
      		 <!-- BEGIN CONTENT -->
      		 <div class="page-content-wrapper">
      	       ${self.content.main} 
      	    </div>
      	    <!-- END CONTENT -->
      	</div>
      	<div class="page-footer">
      		<%@ include file="/WEB-INF/views/themes/default/include/footer.jsp"%>
      	</div>
      </div>
      </c:if>      
      
      <!--自定义页面内容体 -->
      <c:if test="${not empty self.selfcontent}"> 
     	${self.selfcontent}
     </c:if>
   
     
	
    <!-- BEGIN CORE PLUGINS -->
    <!--[if lt IE 9]>
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/respond.min.js"></script>
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/excanvas.min.js"></script> 
    <![endif]-->
   
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/jquery.min.js" type="text/javascript"></script> 
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/jquery-migrate.min.js" type="text/javascript"></script> 
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/jquery-ui/jquery-ui-1.10.3.custom.min.js" type="text/javascript"></script>
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script> 
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js" type="text/javascript"></script>
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js" type="text/javascript"></script>
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/jquery.blockui.min.js" type="text/javascript"></script> 
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/uniform/jquery.uniform.min.js" type="text/javascript"></script> 
    <script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/jquery.cokie.min.js" type="text/javascript"></script> 
   	<script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js" type="text/javascript"></script>
   <!-- END CORE PLUGINS -->
    
    <script>
	    var app = {
	      base: '${base}', 
	      version: '${version.app}', 
	      loginName: '${loginUser.loginName}'
	    };
		
    </script> 
    
    <!-- 当前页面插件js -->
    <c:if test="${not empty self.js.plugins}"> ${self.js.plugins} </c:if>
    
    <!-- Theme Custom --> 
    <script src="${base}/static/themes/default/js/app.js"></script>
	
    <c:if test="${not empty self.js.main}"> ${self.js.main} </c:if>
  </body>
</html>
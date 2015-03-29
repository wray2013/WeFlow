<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/global.jsp"%>
<%
 if(org.apache.shiro.SecurityUtils.getSubject().isAuthenticated()||org.apache.shiro.SecurityUtils.getSubject().isRemembered()){
      response.sendRedirect("/account/info");
  }
  String errorMessage = null;
  String error = (String)request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
  if( error != null ){
    if( error.indexOf("UnknownAccountException") != -1 ) {
      errorMessage = "账号或密码错误！";
    } else if( error.indexOf("IncorrectCredentialsException") != -1 ) {
      errorMessage = "账号或密码错误！";
    } else {
      errorMessage = "登录失败！";
    }
  }
%>
<c:set target="${self}" property="errorMessage" value="<%=errorMessage%>" />
<c:set target="${self}" property="title" value="${appVars.websiteTitle}-登录" />
<c:set target="${self}" property="keywords" value="" />
<c:set target="${self}" property="description" value="" />
<c:set target="${self.css}" property="plugins">
	<link rel="stylesheet" href=" ${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/select2/select2.css" type="text/css" />
	<link rel="stylesheet" href=" ${base}/static/libs/metronic/${version.metronic}/assets/admin/pages/css/login-soft.css"type="text/css" />
</c:set>
<c:set target="${self.css}" property="main">
 <link rel="stylesheet" href="${base}/static/themes/default/css/app.css" />
 <style type="text/css">
	.form-group input{
		border-bottom-left-radius: 0;
    	border-top-left-radius: 0;
	}
 </style>
</c:set>

<c:set target="${self.css}" property="body" value="login" />  
<c:set target="${self}" property="headerbar" value="false"/>
<c:set target="${self}" property="sidebar" value="false"/>
<c:set target="${self}" property="selfcontent">
<!-- BEGIN LOGO -->
<div class="logo">
	<a href="index.html">
	<img src="../../assets/admin/layout2/img/logo-big.png" alt=""/>
	</a>
</div>
<!-- END LOGO -->
<!-- BEGIN SIDEBAR TOGGLER BUTTON -->
<div class="menu-toggler sidebar-toggler">
</div>
<!-- END SIDEBAR TOGGLER BUTTON -->
<!-- BEGIN LOGIN -->
<div class="content">
	<!-- BEGIN LOGIN FORM -->
	<form class="login-form" action="" method="post">
		<h3 class="form-title" style="text-align: center;">账号登陆</h3>
		<div class="alert alert-danger display-hide" >
			<button class="close" data-close="alert"></button>
			<span>
			请输入用户名和密码 </span>
		</div>
		<c:if test="${not empty self.errorMessage}">
		<div class="alert alert-danger display-hide" style="display: block;">
			<button class="close" data-close="alert"></button>
			<span>
			${self.errorMessage} </span>
		</div>
		</c:if>
		<div class="form-group">
			<!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
			<label class="control-label visible-ie8 visible-ie9">用户名</label>
			<div class="input-icon">
				<i class="fa fa-user"></i>
				<input class="form-control placeholder-no-fix" type="text" autocomplete="off" placeholder="用户名" name="username"/>
			</div>
		</div>
		<div class="form-group">
			<label class="control-label visible-ie8 visible-ie9">密码</label>
			<div class="input-icon">
				<i class="fa fa-lock"></i>
				<input class="form-control placeholder-no-fix" type="password" autocomplete="off" placeholder="密码" name="password"/>
			</div>
		</div>
		<div class="form-actions">
			<label class="checkbox">
			<input type="checkbox" name="rememberMe" value="1"/> 记住7天 </label>
			<button type="submit" class="btn blue pull-right">
			登陆 <i class="m-icon-swapright m-icon-white"></i>
			</button>
		</div>
	</form>
	
</div>
<!-- END LOGIN -->

</c:set>

<c:set target="${self.js}" property="plugins">
<script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>
<script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/backstretch/jquery.backstretch.min.js" type="text/javascript"></script>
<script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/select2/select2.min.js" type="text/javascript"></script>
</c:set>

<c:set target="${self.js}" property="main">
<script src="${base}/static/libs/metronic/${version.metronic}/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${base}/static/libs/metronic/${version.metronic}/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>
<script src="${base}/static/libs/metronic/${version.metronic}/assets/admin/layout/scripts/demo.js" type="text/javascript"></script>
<script src="${base}/static/themes/default/js/login.js" type="text/javascript"></script>
<script>
jQuery(document).ready(function() {     
  Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	Demo.init();
    Login.init();
       // init background slide images
       $.backstretch([
        "${base}/static/libs/metronic/${version.metronic}/assets/admin/pages/media/bg/1.jpg",
        "${base}/static/libs/metronic/${version.metronic}/assets/admin/pages/media/bg/2.jpg",
        "${base}/static/libs/metronic/${version.metronic}/assets/admin/pages/media/bg/3.jpg",
        "${base}/static/libs/metronic/${version.metronic}/assets/admin/pages/media/bg/4.jpg"
        ], {
          fade: 1000,
          duration: 8000
    }
    );

});
</script>
</c:set>
<%@ include file="/WEB-INF/views/themes/default/include/main.jsp"%>

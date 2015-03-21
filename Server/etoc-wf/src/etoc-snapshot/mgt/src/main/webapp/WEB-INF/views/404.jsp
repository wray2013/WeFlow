<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/global.jsp"%>
<%
response.setStatus(200);
%>
<c:set target="${self.content}" property="main">
<div class="container">
	<div class="row center">
	  <div class="col-md-11 col-md-offset-1">
	     <img class="img-responsive" src="${base}/static/themes/default/img/http-error/404.jpg">
	  </div>
    </div>
</div>

</c:set>

<%@ include file="/WEB-INF/views/themes/default/include/main.jsp"%>
<%@page contentType="text/html;charset=UTF-8" isErrorPage="true" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/global.jsp"%>
<%
Throwable t = (Throwable)request.getAttribute("javax.servlet.error.exception");
if (t == null) {
    t = (Throwable)request.getAttribute("exception");
}
if (t != null) {
    org.slf4j.LoggerFactory.getLogger("500.jsp").error(t.getMessage(), t);
}
response.setStatus(200);
%>
<%@ include file="/WEB-INF/views/include/global.jsp"%>
<c:set target="${self}" property="title" value="${appVars.websiteTitle}-500错误页面" />
<c:set target="${self}" property="keywords" value="" />
<c:set target="${self}" property="description" value="" />

<c:set target="${self.css}" property="plugins"></c:set>
<c:set target="${self.css}" property="main">
  <style type="text/css"></style>
</c:set>

<c:set target="${self.js}" property="plugins"></c:set>
<c:set target="${self.js}" property="main">
  <script type="text/javascript">
   
  </script>
</c:set>

<c:set target="${self.content}" property="contentTitle">
  
</c:set>

<c:set target="${self.content}" property="main">
 
    <div class="container">
  <div class="row center">
    <div class="col-md-11 col-md-offset-1">
         <!-- <%=exception.getMessage()%> -->
       <img class="img-responsive" src="${base}/static/themes/default/img/http-error/500.jpg">
    </div>
    </div>
</div>
</c:set>
<%@ include file="/WEB-INF/views/themes/default/include/main.jsp"%>
 
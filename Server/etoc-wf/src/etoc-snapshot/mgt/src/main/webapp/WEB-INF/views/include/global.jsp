<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglibs.jsp"%>
<c:if test="${empty INCLUDE_GLOBAL_JSP}">
    <%  
      String path = request.getContextPath();  
      String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;  
    %>  
  <c:set scope="request" var="INCLUDE_GLOBAL_JSP" value="INCLUDE_GLOBAL_JSP" />
  <c:set scope="request" var="base" value="<%=basePath%>" />
  <c:set scope="request" var="version" value="<%= new java.util.HashMap()%>" />
  <c:set scope="request" var="self" value="<%= new java.util.HashMap()%>" />
  <c:set var="currencySymbol" value="<%= java.util.Currency.getInstance(java.util.Locale.getDefault()).getSymbol()%>" />
  
  <c:set target="${version}" property="app">1.0</c:set>
  <c:set target="${version}" property="metronic">3.6.1</c:set>
  <c:set target="${version}" property="handlebars">2.0.0</c:set>

  <c:set target="${self}" property="theme"><spring:eval expression="@appVars.theme" /></c:set>
  <c:set target="${self}" property="locale"><%= org.springframework.web.servlet.support.RequestContextUtils.getLocale(request)%></c:set>
  <c:set target="${self}" property="url"><%= request.getAttribute("javax.servlet.forward.request_uri")%></c:set>
  <c:set target="${self}" property="css" value="<%= new java.util.HashMap()%>" />
  <c:set target="${self}" property="js" value="<%= new java.util.HashMap()%>" />
  <c:set target="${self}" property="content" value="<%= new java.util.HashMap()%>" />
  
  <spring:eval var="appVars" expression="@appVars.instance" />
  
  <spring:eval scope="request" var="loginUser" expression="@shiroUserService.getLoginUser()" />
   <c:if test="${!empty loginUser}">
	<spring:eval var="noReadMail" expression="@innerMailService.getUnreadInnerMailCount(${loginUser.userId})" />
  </c:if>
</c:if>
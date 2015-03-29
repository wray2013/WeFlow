<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*"%>
<%!
    Object buildNav(String pageUrl, String text, String[] urls,
			Object[] childNav, String icon) {
		HashMap<String, Object> nav = new HashMap<String, Object>();
		nav.put("text", text);
		nav.put("url", urls[0]);
		nav.put("child", null != childNav ? childNav : null);
		nav.put("icon", null != icon ? icon : null);
		for (String url : urls) {
			if (pageUrl.contains(url)) {
				nav.put("active", "active");
				break;
			}
		}
		return nav;
	}%>
  <%
	String pageUrl = (String) ((Map) pageContext.findAttribute("self")).get("url");
	Object[] childNav = {
			buildNav(pageUrl, "产品维护1",new String[] { "login/product" }, null,null),
			buildNav(pageUrl, "产品维护2",new String[] { "login" }, null,null) 
	};


	Object[] topNav = {
			buildNav(pageUrl, "首页", new String[]{"index"}, null,"icon-home"),
			buildNav(pageUrl, "产品维护", new String[] { "login/product" ,"login"},childNav, "icon-basket"),
	};
	

%>	
<c:set target="${self}" property="pageUrl" value="<%= pageUrl%>" />
<c:set target="${self}" property="topNav" value="<%= topNav%>" /> 
	<div class="page-sidebar navbar-collapse collapse">
				<!-- BEGIN SIDEBAR MENU -->
				<ul class="page-sidebar-menu page-sidebar-menu-hover-submenu " data-keep-expanded="false" data-auto-scroll="true" data-slide-speed="200">
					<c:forEach var="nav" items="${self.topNav}" varStatus="status">
						<li class="<c:if test='${status.index==0}'>start </c:if> 
							<c:if test='${not empty nav.active && empty nav.child}'> active</c:if>
							<c:if test='${not empty nav.active && not empty nav.child}'>open active</c:if>  
							<c:if test='${fn:length(self.topNav) == (status.index+1)}'> last</c:if> ">
						  <a href="javascript:;">
							<i class="${nav.icon}"></i>
							<span class="title">${nav.text}</span>
							<c:if test='${not empty nav.active}'>
							  <span class="selected"></span>
							</c:if>
							<c:if test="${not empty nav.child}">
								<span class="arrow <c:if test='${not empty nav.active}'> open</c:if>"></span>
						    </c:if>
						  </a>
						   <c:if test="${not empty nav.child}">
						     <ul class="sub-menu">
						     	<c:forEach var="childNav" items="${nav.child}">
						     	   <li <c:if test='${not empty childNav.active}'>class="active"</c:if> >
						     	   	 <a href="${base}/${childNav.url}">${childNav.text}</a>
						     	   </li>
						        </c:forEach>
						     </ul>
						   </c:if>
					    </li>
					</c:forEach>
					
				<!-- END SIDEBAR MENU -->
			</div>
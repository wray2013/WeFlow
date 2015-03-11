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
			buildNav(pageUrl, "项目投资",new String[] { "invest/list" }, null,null),
			buildNav(pageUrl, "债权购买",new String[] { "debt/list" }, null,null) 
	};
	Object[] childNav1 = {
			buildNav(pageUrl, "个人借款", new String[] { "borrow/person-add" },null, null),
			buildNav(pageUrl, "企业融资 ", new String[] { "borrow/enterprise-add" },null, null)
	};
	
	Object[] childNav2 = {
			buildNav(pageUrl, "首页", new String[] { "index" },null, null),
			buildNav(pageUrl, "首页2", new String[] { "index2" },null, null),
			buildNav(pageUrl, "首页3 ", new String[] { "index3" },null, null)
	};

	Object[] topNav = {
			buildNav(pageUrl, "首页", new String[]{"index"}, childNav2,null),
			buildNav(pageUrl, "我要投资", new String[] { "invest/list" ,"debt/list"},childNav, null),
			buildNav(pageUrl, "我要借款", new String[] { "borrow/person-add","borrow/enterprise-add"}, childNav1,null),
			buildNav(pageUrl, "我的账户", new String[] { "account/info" },null, null),
			buildNav(pageUrl, "活动专区", new String[] { "activity" },null, null),
			buildNav(pageUrl, "关于我们", new String[] { "static/aboutus","static" }, null, null) 
	};
	

	Object[] topNav_biz = {
			buildNav(pageUrl, "首页", new String[]{"index"}, null,null),
			buildNav(pageUrl, "企业融资", new String[] { "borrow/enterprise-add"}, null,null),
			buildNav(pageUrl, "我的账户", new String[] { "account/info" },null, null),
			buildNav(pageUrl, "活动专区", new String[] { "static/activity" },null, null),
			buildNav(pageUrl, "关于我们", new String[] { "static/aboutus","static" }, null, null) 
	};
	
%>	
<c:set target="${self}" property="pageUrl" value="<%= pageUrl%>" />
<c:set target="${self}" property="topNav" value="<%= topNav%>" /> 
<c:set target="${self}" property="topNav_biz" value="<%= topNav_biz%>" />
<header id="header" class="colored flat-menu">
	<div class="header-top">
		<div class="container">
			<nav>
				<ul class="nav nav-pills nav-top">
					<li>
					   <span><i class="fa fa-hand-o-right"></i>${appVars.websiteTitle},欢迎您!</span>
					</li>
					<li><span><i class="fa fa-phone"></i>${appVars.servicePhone}</span> </li>
					<li><span>关注我们：	</span></li>
					<li style="position:relative;">
						<div class="followus follow-weibo" id="weibo"></div>
						<div class="topop topop-weibo" id="weibo" style="display:none;">
							<div class="toarrow"></div>
							<div class="topop-content">
								<p style="color:black;font-size:12px;">扫码关注百事贷微博
								<img width="100px" height="100px" src="${base}/static/themes/default/img/wbqrcode.png" />
								</p>
							</div>	
						</div>
					</li>
					<li style="position:relative;">
						<div class="followus follow-weixin" id="weixin"></div>
						<div class="topop topop-weixin" id="weixin" style="display:none;">
							<div class="toarrow"></div>
							<div class="topop-content">
								<p style="color:black;font-size:12px;">扫码关注百事贷微信
								<img width="100px" height="100px" src="${base}/static/themes/default/img/wxqrcode.png" />
								</p>
							</div>	
						</div>
					</li>
				</ul>
			</nav>
			<ul class="head-top-right nav nav-pills">
				<c:if test="${not empty loginUser}"> 
					<li><a href ="${base}/account/info">欢迎您,${loginUser.nickName }  </a> </li>
					<li><a href ="${base }/account/toMessagePage">站内信(${noReadMail})</a></li>
					<li> <a href="${base}/logout"> 退出 </a> </li>
				</c:if>
				<c:if test="${ empty loginUser}"> 
				<li> <a href="${base}/login"> 登录 </a> </li>
				<li> <a href="${base}/goRegister"> 快速注册 </a> </li>
				</c:if>
							<li><a href="${base}/static/helpcenter">帮助</a></li>
							<li><a href="${base}/static/safeensure">安全</a></li>
			</ul>
		</div>
	</div>
	<div class="container">
		<div class="logo">
			<a href="${base}/index">
				<img alt="Porto" width="111" height="54" data-sticky-width="82" data-sticky-height="40" src="${base}/static/pic/logo/aa">
			</a>
		</div>
		<button class="btn btn-responsive-nav btn-inverse" data-toggle="collapse" data-target=".nav-main-collapse"> <i class="fa fa-bars"></i> </button>
	</div>
	<div class="navbar-collapse nav-main-collapse collapse">
		<div class="container">
			<nav class="nav-main mega-menu">
				<ul class="nav nav-pills nav-main" id="mainMenu">
					<c:forEach var="nav" items="${self.topNav}">
						<li class="<c:if test='${not empty nav.child}'> dropdown </c:if>  <c:if test='${not empty nav.active}'> active</c:if> "><a class="
							<c:if test='${not empty nav.child}'> dropdown-toggle </c:if>"
							href="${base}/${nav.url}">${nav.text} 
							<c:if test="${not empty nav.child}"><i class="fa fa-angle-down"></i></c:if>
						  </a> 
						  <c:if test="${not empty nav.child}">
							<ul class="dropdown-menu">
								<c:forEach var="childNav" items="${nav.child}">
									<li><a href="${base}/${childNav.url}">${childNav.text}</a></li>
								</c:forEach>
							</ul>
						  </c:if>
						</li>
					</c:forEach>
				</ul>
			</nav>
		</div>
	</div>
</header>
	
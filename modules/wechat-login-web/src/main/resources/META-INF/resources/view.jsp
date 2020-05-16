<%@ include file="/init.jsp"%>

<p>
  <b><liferay-ui:message key="wechatlogin.caption" /></b>

  <%-- 	
	<portlet:actionURL name="/login/view" var="UserAuth" />
	<aui:a href="<%= UserAuth.toString() %>" label="login-by-wechat" />
	 --%>

  <a
    href="https://open.weixin.qq.com/connect/qrconnect?appid=wx2d653b1ce6779c0e&redirect_uri=http%3A%2F%2Fwww.liferaydemo.cn/o/wechat/sendCode&response_type=code&scope=snsapi_login&state=STATE#wechat_redirect">
    Login by Wechat </a>

  <%--	 
	 <portlet:renderURL var="wechat-login">
		<portlet:param name="mvcPath" value="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx74ac7a84f8421a27&redirect_uri=http%3A%2F%2Fsaraliferaydemo.nat300.top/o/wx/sendCode&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
	</portlet:renderURL>
	--%>

</p>
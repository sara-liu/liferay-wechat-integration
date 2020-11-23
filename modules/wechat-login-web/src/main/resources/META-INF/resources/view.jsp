<%@ include file="/init.jsp"%>

<p>
<% 
if (themeDisplay.isSignedIn()) {
%>
  <liferay-ui:message key="Welcome"/> <%= themeDisplay.getRealUser().getFirstName() %> 

<% } else { %>
	<a
	href="https://open.weixin.qq.com/connect/qrconnect?appid=wx2d653b1ce6779c0e&redirect_uri=http%3A%2F%2Fwww.liferaydemo.cn/o/wechat/wechat_login&response_type=code&scope=snsapi_login&state=STATE#wechat_redirect">
	Login by Wechat </a>
	
<% } %>
</p>

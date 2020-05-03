package com.liferay.wechat.login.web.portlet;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.wechat.login.web.constants.WechatLoginPortletKeys;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.osgi.service.component.annotations.Component;


@Component(
		immediate = true,
		property = {
			"javax.portlet.name=" + WechatLoginPortletKeys.WECHATLOGIN,
			"mvc.command.name=/login/view"
		},
		service = MVCActionCommand.class
)
public class SendCodeMVCRenderCommand implements MVCActionCommand {


	@Override
	public boolean processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException {
		// TODO Auto-generated method stub
		
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize";
		String appid = "wx74ac7a84f8421a27";
		String redirect_uri = "http%3A%2F%2Fsaraliferaydemo.nat300.top/o/wx/sendCode";
		String response_type = "code";
		String scope = "snsapi_userinfo";
		String state = "STATE#wechat_redirect";
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(url).append("?").append("appid=").append(appid).append("&redirect_uri=").append(redirect_uri).append("&response_type=")
				.append(response_type).append("&scope=").append(scope).append("&state=").append(state);

		String finalUrl = sb.toString();

		System.out.println(finalUrl);
		
		try {
			sendHttpGet(finalUrl);
			return true;
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public String sendHttpGet(String url) throws JSONException, IOException {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();

			// Use caution: ensure correct character encoding and is not binary data
			String result = new String(responseBody);
			return result;
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Release the connection.
			method.releaseConnection();
		}

		return "";

		// TODO close http client if necessary
	}
	


}

package com.liferay.wechat.login.rest.application;

import com.liferay.portal.kernel.json.JSONException;
/*import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;*/
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author SaraLiu
 */

public class StaticTest {

	public static void main(String[] args) throws JSONException, IOException {
		getAccessToken("081X4Nm112HnCW1SADi11BqUm11X4Nmm");
	}
	
	public static String getAccessToken(String code) throws JSONException, IOException {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
		String appid = "wx74ac7a84f8421a27";
		String secret = "baf71f9e1786a82da9863601366d656e";
		String grantType = "authorization_code";

		StringBuilder sb = new StringBuilder();
		sb.append(url).append("?").append("appid=").append(appid).append("&secret=").append(secret).append("&code=")
				.append(code).append("&grant_type=").append(grantType);

		String finalUrl = sb.toString();
		System.out.println(finalUrl);

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod(finalUrl);

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			String result = new String(responseBody);
			System.out.println(result);
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(result);
			String accessToken = jsonObject.getString("access_token");
			System.out.println(accessToken);
			return accessToken;
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



/*
 * CloseableHttpClient httpClient = HttpClients.createDefault();
 * 
 * String url1 =
 * "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx74ac7a84f8421a27&secret=baf71f9e1786a82da9863601366d656e&code=";
 * String url2 = "&grant_type=authorization_code"; String url = url1 + code +
 * url2;
 * 
 * System.out.println("url=================" + url); HttpGet httpGet = new
 * HttpGet(url);
 * 
 * try { CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
 * 
 * HttpEntity entity = httpResponse.getEntity(); String result =
 * EntityUtils.toString(entity, "UTF-8"); System.out.println(result);
 * 
 * JSONObject jsonObject = JSONObject.parseObject(result); String accessToken =
 * jsonObject.getString("access_token"); System.out.println(accessToken); //
 * JSONObject jsonObject =
 * com.liferay.portal.kernel.json.JSONFactoryUtil.createJSONObject(result); //
 * String access_token = jsonObject.getString("access_token"); //
 * System.out.println("tooooooooken=====" + access_token);
 * 
 * } catch (ClientProtocolException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated catch
 * block e.printStackTrace(); }
 */
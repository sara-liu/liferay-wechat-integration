package com.liferay.wechat.login.rest.application;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

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
 * 
 */
@Component(property = { JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/wechat",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=WechatLogin" }, service = Application.class)

public class RestApplication extends Application {
	Log logger = LogFactoryUtil.getLog(RestApplication.class);

	private static final String SECRET_PROP_FILE_NAME = "secret.properties";
	private static final String TOKEN = "liferaysaratest";
	private static final String APP_ID = "wx2d653b1ce6779c0e";
	private static final String GRANT_TYPE = "authorization_code";
	private final String SECRET = "0145b2ee0613ce531dc0f303c5d16093";

//	{
//		Properties prop = new Properties();
//		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SECRET_PROP_FILE_NAME);
//
//		if (inputStream != null) {
//			try {
//				prop.load(inputStream);
//			} catch (IOException e) {
//				logger.error(e);
//			}
//		} else {
//			FileNotFoundException e = new FileNotFoundException(
//					"property file '" + SECRET_PROP_FILE_NAME + "' not found in the classpath");
//			logger.error(e);
//		}
//
//		SECRET = prop.getProperty("secret");
//	}

	// NOTE: This method is necessary for Liferay Protal, don't delete it.
	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	@GET
	@Path("/hello")
	@Produces("text/plain")
	public String hello() {
		return "hello, this works!";
	}

	@GET
	@Path("/wechat_login")
	@Produces("text/plain")
	public String wechatLogin(@Context HttpServletRequest request, @Context HttpServletResponse response)
			throws Exception {
		response.setCharacterEncoding("utf-8");
		String code = request.getParameter("code");
		AccessTokenInfo accessToken = getAccessToken(code);
		UserInfo userInfo = getUserInfo(accessToken);

		// NOTES: the nickname cannot be Chinese (DefaultScreenNameValidator will throw an exception)
		User user = null;
		boolean newAdded = false;
		try {
			// Wechat opendId is used as the screen name
			user = UserLocalServiceUtil.getUserByScreenName(companyId, accessToken.openId);
		} catch (PortalException e) {
			user = addUser(accessToken.openId, userInfo.nickName);
			newAdded = true;
		}

		String password = user.getPassword();
		// remove the begin "{NONE}"
		password = password.substring(6);

		if (newAdded) {
			AuthenticatedSessionManagerUtil.login(request, response, user.getScreenName(), defaultPassword, true,
					CompanyConstants.AUTH_TYPE_SN);

		} else {
			AuthenticatedSessionManagerUtil.login(request, response, user.getScreenName(), password, true,
					CompanyConstants.AUTH_TYPE_SN);
		}

		response.sendRedirect(redirectUrl);
		return "done.";
	}

	// Hard code
	long companyId = 20101;
	String lastName = "L";
	String defaultPassword = "test";
	String redirectUrl = "http://www.liferaydemo.cn";

	private class AccessTokenInfo {
		String accessToken;
		String openId;
	}

	private class UserInfo {
		String nickName;
		String sex;
		String province;
		JSONArray privilege;
	}

	private User addUser(String openId, String nickName) throws PortalException {
		long[] empty_long_list = {};

		// Use wechat opendId as the screen name
		User user = UserLocalServiceUtil.addUser(20130, companyId, false, defaultPassword, defaultPassword, false,
				openId, openId + "@liferay.com", 0, "", java.util.Locale.CHINA, nickName, "", lastName, 0, 0, true, 1,
				1, 1970, "", empty_long_list, empty_long_list, empty_long_list, empty_long_list, false,
				new ServiceContext());

		return user;
	}

	private AccessTokenInfo getAccessToken(String code) throws JSONException, IOException {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
		StringBuilder sb = new StringBuilder();
		sb.append(url).append("?").append("appid=").append(APP_ID).append("&secret=").append(SECRET).append("&code=")
				.append(code).append("&grant_type=").append(GRANT_TYPE);

		String finalUrl = sb.toString();
		String result = sendHttpGet(finalUrl);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(result);
		String accessToken = jsonObject.getString("access_token");
		String openId = jsonObject.getString("openid");
		AccessTokenInfo info = new AccessTokenInfo();
		info.accessToken = accessToken;
		info.openId = openId;
		return info;
	}

	private UserInfo getUserInfo(AccessTokenInfo info) throws JSONException, IOException {
		String url = "https://api.weixin.qq.com/sns/userinfo";
		String accessToken = info.accessToken;
		String openId = info.openId;
		String lang = "zh_CN";

		StringBuilder sb = new StringBuilder();
		sb.append(url).append("?").append("access_token=").append(accessToken).append("&openid=").append(openId)
				.append("&lang=").append(lang);

		String finalUrl = sb.toString();
		String result = sendHttpGet(finalUrl);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(result);

		UserInfo userInfo = new UserInfo();
		userInfo.nickName = jsonObject.getString("nickname");
		userInfo.sex = jsonObject.getString("sex");
		userInfo.province = jsonObject.getString("province");

		return userInfo;
	}

	public String sendHttpGet(String url) throws JSONException, IOException {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();

			// Use caution: ensure correct character encoding and is not binary data
			String result = new String(responseBody, "utf-8");
			return result;
		} catch (HttpException e) {
			logger.error("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Release the connection.
			method.releaseConnection();
		}

		return "";
	}

}

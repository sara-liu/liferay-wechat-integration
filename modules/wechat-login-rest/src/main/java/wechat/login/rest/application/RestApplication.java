package wechat.login.rest.application;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ietf.jgss.Oid;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

/**
 * @author SaraLiu
 */
@Component(property = { JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/wechat",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=WechatLogin" }, service = Application.class)

public class RestApplication extends Application {
	private static final String TOKEN = "liferaysaratest";

	@GET
	@Path("/hello")
	@Produces("text/plain")
	public String hello() {
		return "hello, this works!";
	}

	// NOTE: This method is necessary for Liferay Protal, don't delete it.
	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	// TODO rename to checkToken
	@GET
	@Path("/tokenCheck")
	@Produces("text/plain")
	public String getToken(@Context HttpServletRequest request, @Context HttpServletResponse response)
			throws IOException, NoSuchAlgorithmException {

		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");

		System.out.println("signature:" + signature);
		System.out.println("timestamp:" + timestamp);
		System.out.println("nonce:" + nonce);
		System.out.println("echostr:" + echostr);
		System.out.println("TOKEN:" + TOKEN);

		String[] params = new String[] { TOKEN, timestamp, nonce };
		Arrays.sort(params);

		String clearText = params[0] + params[1] + params[2];

		String algorithm = "SHA-1";

		String sign = new String(
				Hex.encodeHex(MessageDigest.getInstance(algorithm).digest((clearText).getBytes()), true));

		if (signature.equals(sign)) {
//		  response.getWriter().print(echostr); }
			return echostr;
		} else {
			return "";
		}
	}

	@GET
	@Path("/sendCode")
	@Produces("text/plain")
	public String sendCode(@Context HttpServletRequest request, @Context HttpServletResponse response)
			throws JSONException, IOException {
		String code = request.getParameter("code");
		AccessTokenInfo accessToken = getAccessToken(code);
		UserInfo userInfo = getUserInfo(accessToken);

		return userInfo.nickName;
	}

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

	// TODO to replace those in methods
	private static final String APP_ID = "wx74ac7a84f8421a27";
	private static final String SECRET = "baf71f9e1786a82da9863601366d656e";
	private static final String GRANT_TYPE = "authorization_code";

	private AccessTokenInfo getAccessToken(String code) throws JSONException, IOException {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
		String appid = "wx74ac7a84f8421a27";
		String secret = "baf71f9e1786a82da9863601366d656e";
		String grantType = "authorization_code";
		StringBuilder sb = new StringBuilder();
		sb.append(url).append("?").append("appid=").append(appid).append("&secret=").append(secret).append("&code=")
				.append(code).append("&grant_type=").append(grantType);

		String finalUrl = sb.toString();
		System.out.println(finalUrl);
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
		System.out.println(finalUrl);

		String result = sendHttpGet(finalUrl);
		System.out.println(result);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(result);

		UserInfo userInfo = new UserInfo();

		userInfo.nickName = jsonObject.getString("nickname");
		userInfo.sex = jsonObject.getString("sex");
		userInfo.province = jsonObject.getString("province");
//		JSONArray privilege = jsonObject.getJSONArray("privilege");

		return userInfo;
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

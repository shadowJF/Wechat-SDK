package com.yonyou.nccloud.weixin.sdk.sup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.yonyou.nccloud.weixin.sdk.api.ApiConfig;
import com.yonyou.nccloud.weixin.sdk.api.ApiConfigKit;
import com.yonyou.nccloud.weixin.sdk.api.ApiResult;
import com.yonyou.nccloud.weixin.sdk.api.CallbackIpApi;
import com.yonyou.nccloud.weixin.sdk.api.CustomServiceApi;
import com.yonyou.nccloud.weixin.sdk.api.MenuApi;
import com.yonyou.nccloud.weixin.sdk.api.QrcodeApi;
import com.yonyou.nccloud.weixin.sdk.api.ShorturlApi;
import com.yonyou.nccloud.weixin.sdk.api.TemplateMsgApi;
import com.yonyou.nccloud.weixin.sdk.api.UserApi;
import com.yonyou.nccloud.weixin.sdk.kit.HttpKit;

public class WeixinApiSupport {

	/**
	 * 获取公众号菜单
	 */
	public void getMenu(HttpServletRequest request, HttpServletResponse response) {
		try {
			ApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			ApiResult apiResult = MenuApi.getMenu();
			if (apiResult.isSucceed())
				HttpKit.renderText(response, apiResult.getJson());
			else
				HttpKit.renderText(response, apiResult.getErrorMsg());
		} finally {
			ApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 创建菜单
	 */
	public void createMenu(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			ApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			String str = "{\n"
					+ "    \"button\": [\n"
					+ "        {\n"
					+ "            \"name\": \"进入理财\",\n"
					+ "            \"url\": \"http://m.bajie8.com/bajie/enter\",\n"
					+ "            \"type\": \"view\"\n" + "        },\n"
					+ "        {\n" + "            \"name\": \"安全保障\",\n"
					+ "            \"key\": \"112\",\n"
					+ "\t    \"type\": \"click\"\n" + "        },\n"
					+ "        {\n" + "\t    \"name\": \"使用帮助\",\n"
					+ "\t    \"url\": \"http://m.bajie8.com/footer/cjwt\",\n"
					+ "\t    \"type\": \"view\"\n" + "        }\n" + "    ]\n"
					+ "}";
			ApiResult apiResult = MenuApi.createMenu(str);
			if (apiResult.isSucceed())
				HttpKit.renderText(response, apiResult.getJson());
			else
				HttpKit.renderText(response, apiResult.getErrorMsg());
		} finally {
			ApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 获取公众号关注用户
	 */
	public void getFollowers(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			ApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			ApiResult apiResult = UserApi.getFollows();
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			ApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 获取用户信息
	 */
	public void getUserInfo(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			ApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			ApiResult apiResult = UserApi
					.getUserInfo("ohbweuNYB_heu_buiBWZtwgi4xzU");
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			ApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 发送模板消息
	 */
	public void sendMsg(HttpServletRequest request, HttpServletResponse response) {
		try {
			ApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			String str = " {\n"
					+ "           \"touser\":\"ohbweuNYB_heu_buiBWZtwgi4xzU\",\n"
					+ "           \"template_id\":\"9SIa8ph1403NEM3qk3z9-go-p4kBMeh-HGepQZVdA7w\",\n"
					+ "           \"url\":\"http://www.sina.com\",\n"
					+ "           \"topcolor\":\"#FF0000\",\n"
					+ "           \"data\":{\n"
					+ "                   \"first\": {\n"
					+ "                       \"value\":\"恭喜你购买成功！\",\n"
					+ "                       \"color\":\"#173177\"\n"
					+ "                   },\n"
					+ "                   \"keyword1\":{\n"
					+ "                       \"value\":\"去哪儿网发的酒店红包（1个）\",\n"
					+ "                       \"color\":\"#173177\"\n"
					+ "                   },\n"
					+ "                   \"keyword2\":{\n"
					+ "                       \"value\":\"1元\",\n"
					+ "                       \"color\":\"#173177\"\n"
					+ "                   },\n"
					+ "                   \"remark\":{\n"
					+ "                       \"value\":\"欢迎再次购买！\",\n"
					+ "                       \"color\":\"#173177\"\n"
					+ "                   }\n" + "           }\n" + "       }";
			ApiResult apiResult = TemplateMsgApi.send(str);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			ApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 获取参数二维码
	 */
	public void getQrcode(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			ApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			String str = "{\"expire_seconds\": 604800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": 123}}}";
			ApiResult apiResult = QrcodeApi.create(str);

			// String str =
			// "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"123\"}}}";
			// ApiResult apiResult = QrcodeApi.create(str);
			// renderText(apiResult.getJson());
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			ApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 长链接转成短链接
	 */
	public void getShorturl(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			ApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			String str = "{\"action\":\"long2short\","
					+ "\"long_url\":\"http://wap.koudaitong.com/v2/showcase/goods?alias=128wi9shh&spm=h56083&redirect_count=1\"}";
			ApiResult apiResult = ShorturlApi.getShorturl(str);

			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			ApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 获取客服聊天记录
	 */
	public void getRecord(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			ApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			String str = "{\n" + "    \"endtime\" : 987654321,\n"
					+ "    \"pageindex\" : 1,\n" + "    \"pagesize\" : 10,\n"
					+ "    \"starttime\" : 123456789\n" + " }";
			ApiResult apiResult = CustomServiceApi.getRecord(str);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			ApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 获取微信服务器IP地址
	 */
	public void getCallbackIp(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			ApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			ApiResult apiResult = CallbackIpApi.getCallbackIp();
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			ApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 默认作为参数传入，如果请求没传则通过request.getAttribute("appconfig")获取
	 * 
	 * @param request
	 * @return
	 */
	public static ApiConfig getApiConfig(HttpServletRequest request) {
		String appId = request.getParameter("appId");
		String appSecret = request.getParameter("appSecret");
		String encryptMessage = request.getParameter("encryptMessage");
		String encodingAesKey = request.getParameter("encodingAesKey");
		String token = request.getParameter("token");
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)
				|| StringUtils.isBlank(token)) {
			ApiConfig appconfig = (ApiConfig) request.getAttribute("appconfig");
			if (appconfig != null) {
				return appconfig;
			}
		}
		ApiConfig appconfig = new ApiConfig();
		appconfig.setAppId(appId);
		appconfig.setAppSecret(appSecret);
		appconfig.setEncodingAesKey(encodingAesKey);
		appconfig.setToken(token);
		appconfig.setEncryptMessage(Boolean.valueOf(encryptMessage));
		return appconfig;
	}
}

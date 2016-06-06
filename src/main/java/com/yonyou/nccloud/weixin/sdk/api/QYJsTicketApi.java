
package com.yonyou.nccloud.weixin.sdk.api;

import java.util.concurrent.Callable;

import com.yonyou.nccloud.weixin.sdk.cache.IAccessTokenCache;
import com.yonyou.nccloud.weixin.sdk.kit.ParaMap;
import com.yonyou.nccloud.weixin.sdk.utils.HttpUtils;
import com.yonyou.nccloud.weixin.sdk.utils.RetryUtils;

/**
 * 
 * 生成签名之前必须先了解一下jsapi_ticket，jsapi_ticket是公众号用于调用微信JS接口的临时票据
 * https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=
 * ACCESS_TOKEN
 * 
 */
public class QYJsTicketApi {

	private static String apiUrl = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket";

	static IAccessTokenCache accessTokenCache = QYApiConfigKit.getAccessTokenCache();

	/**
	 * JSApi的类型
	 * 
	 * jsapi: 用于分享等js-api
	 * 
	 * 
	 */
	public enum JsApiType {
		jsapi
	}

	/**
	 * 
	 * http GET请求获得jsapi_ticket（有效期7200秒，开发者必须在自己的服务全局缓存jsapi_ticket）
	 * 
	 * @return JsTicket
	 */
	public static JsTicket getTicket(JsApiType jsApiType) {
		String access_token = AccessTokenApi.getQYAccessTokenStr();
		String appId = QYApiConfigKit.getApiConfig().getCorpId();
		String key = appId + ':' + jsApiType.name();
		final ParaMap pm = ParaMap.create("access_token", access_token);

		JsTicket jsTicket = accessTokenCache.get(key);
		if (null == jsTicket || !jsTicket.isAvailable()) {
			// 最多三次请求
			jsTicket = RetryUtils.retryOnException(3, new Callable<JsTicket>() {

				@Override
				public JsTicket call() throws Exception {
					return new JsTicket(HttpUtils.get(apiUrl, pm.getData()));
				}

			});

			accessTokenCache.set(key, jsTicket);
		}
		return jsTicket;
	}

}
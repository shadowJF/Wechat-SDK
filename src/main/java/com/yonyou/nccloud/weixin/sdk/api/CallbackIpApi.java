package com.yonyou.nccloud.weixin.sdk.api;

import com.yonyou.nccloud.weixin.sdk.utils.HttpUtils;


/**
 * 获取微信服务器IP地址</br>
 * 如果公众号基于安全等考虑，需要获知微信服务器的IP地址列表，以便进行相关限制，可以通过该接口获得微信服务器IP地址列表。
 */
public class CallbackIpApi
{
	private static String apiUrl = "https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=";

	/**
	 * 获取微信服务器IP地址
	 */
	public static ApiResult getCallbackIp() {
		String jsonResult = HttpUtils.get(apiUrl + AccessTokenApi.getAccessTokenStr());
		return new ApiResult(jsonResult);
	}
}


package com.yonyou.nccloud.weixin.sdk.api;

import java.util.Map;
import java.util.concurrent.Callable;

import com.yonyou.nccloud.weixin.sdk.cache.IAccessTokenCache;
import com.yonyou.nccloud.weixin.sdk.kit.ParaMap;
import com.yonyou.nccloud.weixin.sdk.utils.HttpUtils;
import com.yonyou.nccloud.weixin.sdk.utils.RetryUtils;

/**
 * 认证并获取 access_token API
 * http://qydev.weixin.qq.com/wiki/index.php?title=%E4%B8%BB%E5%8A%A8%E8%B0%83%
 * E7%94%A8
 * 
 * AccessToken默认存储于内存中，可设置存储于redis或者实现IAccessTokenCache到数据库中实现分布式可用
 * 
 * 具体配置：
 * 
 * <pre>
 * ApiConfigKit.setAccessTokenCache(new RedisAccessTokenCache());
 * </pre>
 */
public class AccessTokenApi {

	// "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=id&corpsecret=secrect";
	private static String qyurl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
	
	private static String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

	// 利用 appId 与 accessToken 建立关联，支持多账户
	static IAccessTokenCache qyAccessTokenCache = QYApiConfigKit.getAccessTokenCache();
	
	// 利用 appId 与 accessToken 建立关联，支持多账户
    static IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();

	/**
	 * 从缓存中获取 access token，如果未取到或者 access token 不可用则先更新再获取
	 */
	public static AccessToken getQYAccessToken() {
		String corpId = QYApiConfigKit.getApiConfig().getCorpId();
		AccessToken result = qyAccessTokenCache.get(corpId);
		if (result != null && result.isAvailable())
			return result;

		refreshQYAccessToken();
		return qyAccessTokenCache.get(corpId);
	}

	/**
	 * 直接获取 accessToken 字符串，方便使用
	 * 
	 * @return String accessToken
	 */
	public static String getQYAccessTokenStr() {
		return getQYAccessToken().getAccessToken();
	}

	/**
	 * 强制更新 access token 值
	 */
	public static synchronized void refreshQYAccessToken() {
		QYApiConfig ac = QYApiConfigKit.getApiConfig();
		String corpid = ac.getCorpId();
		String corpsecret = ac.getCorpSecret();
		final Map<String, String> queryParas = ParaMap.create("corpid", corpid).put("corpsecret", corpsecret).getData();

		// 最多三次请求
		AccessToken result = RetryUtils.retryOnException(3, new Callable<AccessToken>() {

			@Override
			public AccessToken call() throws Exception {
				String json = HttpUtils.get(qyurl, queryParas);
				return new AccessToken(json);
			}
		});

		// 三次请求如果仍然返回了不可用的 access token 仍然 put 进去，便于上层通过 AccessToken 中的属性判断底层的情况
		qyAccessTokenCache.set(ac.getCorpId(), result);
	}
	
	/**
	 * 从缓存中获取 access token，如果未取到或者 access token 不可用则先更新再获取
	 */
	public static AccessToken getAccessToken() {
		String appId = ApiConfigKit.getApiConfig().getAppId();
		AccessToken result = accessTokenCache.get(appId);
		if (result != null && result.isAvailable())
			return result;
		
		refreshAccessToken();
		return accessTokenCache.get(appId);
	}
	
	/**
	 * 直接获取 accessToken 字符串，方便使用
	 * @return String accessToken
	 */
	public static String getAccessTokenStr() {
		return getAccessToken().getAccessToken();
	}
	
	/**
	 * 强制更新 access token 值
	 */
	public static synchronized void refreshAccessToken() {
		ApiConfig ac = ApiConfigKit.getApiConfig();
		String appId = ac.getAppId();
		String appSecret = ac.getAppSecret();
		final Map<String, String> queryParas = ParaMap.create("appid", appId).put("secret", appSecret).getData();
		
		// 最多三次请求
		AccessToken result = RetryUtils.retryOnException(3, new Callable<AccessToken>() {
			
			@Override
			public AccessToken call() throws Exception {
				String json = HttpUtils.get(url, queryParas);
				return new AccessToken(json);
			}
		});
		
		// 三次请求如果仍然返回了不可用的 access token 仍然 put 进去，便于上层通过 AccessToken 中的属性判断底层的情况
		accessTokenCache.set(ac.getAppId(), result);
	}

}

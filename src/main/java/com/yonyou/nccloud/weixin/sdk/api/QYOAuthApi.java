/**
 * Copyright (c) 2011-2015, Javen  (javenlife@126.com).
 *
 * Licensed under the Apache License, Version 1.0 (the "License");
 */
package com.yonyou.nccloud.weixin.sdk.api;

import org.apache.commons.lang.StringUtils;

import com.yonyou.nccloud.weixin.sdk.utils.HttpUtils;

/**
 * @author Javen 2015年12月27日
 */
public class QYOAuthApi {
	private static String getCodeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=";
	private static String getUserInfoUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=";
	/**
	 * userid转换成openid接口
	 */
	private static String toOpenIdUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_openid?access_token=";
	/**
	 * openid转换成userid接口
	 */
	private static String toUserIdUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_userid?access_token=";

	/**
	 * 获取企业授权codeUrl
	 * 
	 * @param redirectUri
	 * @param state
	 * @return
	 */
	public static String getCodeUrl(String redirectUri, String state) {
		getCodeUrl = getCodeUrl + QYApiConfigKit.getApiConfig().getCorpId() + "&redirect_uri=" + redirectUri
				+ "&response_type=code&scope=snsapi_base";
		if (!StringUtils.isBlank(state)) {
			getCodeUrl = getCodeUrl + "&state=" + state;
		}
		getCodeUrl = getCodeUrl + "#wechat_redirect";
		return getCodeUrl;
	}

	/**
	 * 根据code获取成员信息
	 * 
	 * @param code
	 * @return
	 */
	public static ApiResult getUserInfoByCode(String code) {

		String jsonResult = HttpUtils.get(getUserInfoUrl + AccessTokenApi.getAccessTokenStr() + "&code=" + code);
		return new ApiResult(jsonResult);
	}

	/**
	 * userid转换成openid接口
	 * 
	 * @param data
	 *            {<br/>
	 *            "userid": "zhangsan",<br/>
	 *            "agentid": 1<br/>
	 *            }<br/>
	 * @return
	 */
	public static ApiResult ToOpenId(String data) {
		String jsonResult = HttpUtils.post(toOpenIdUrl + AccessTokenApi.getAccessTokenStr(), data);
		return new ApiResult(jsonResult);
	}

	/**
	 * 
	 * openid转换成userid接口
	 * 
	 * @param data
	 *            { "openid": "oDOGms-6yCnGrRovBj2yHij5JL6E" }
	 * @return
	 */
	public static ApiResult ToUserId(String data) {
		String jsonResult = HttpUtils.post(toUserIdUrl + AccessTokenApi.getAccessTokenStr(), data);
		return new ApiResult(jsonResult);
	}
}

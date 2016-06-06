package com.yonyou.nccloud.weixin.sdk.api;

import com.yonyou.nccloud.weixin.sdk.utils.HttpUtils;

/**
 * menu api
 */
public class MenuApi {
	
	private static String getMenu = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=";
	private static String createMenu = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=";
	
	/**
	 * 查询菜单
	 */
	public static ApiResult getMenu() {
		String jsonResult = HttpUtils.get(getMenu + AccessTokenApi.getAccessTokenStr());
		return new ApiResult(jsonResult);
	}
	
	/**
	 * 创建菜单
	 */
	public static ApiResult createMenu(String jsonStr) {
		String jsonResult = HttpUtils.post(createMenu + AccessTokenApi.getAccessTokenStr(), jsonStr);
		return new ApiResult(jsonResult);
	}
}



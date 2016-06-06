
package com.yonyou.nccloud.weixin.sdk.api;

import com.yonyou.nccloud.weixin.sdk.kit.HttpKit;
import com.yonyou.nccloud.weixin.sdk.kit.ParaMap;
import com.yonyou.nccloud.weixin.sdk.utils.HttpUtils;

/**
 * menu api
 */
public class QYMenuApi {

	private static String getMenu = "https://qyapi.weixin.qq.com/cgi-bin/menu/get";
	private static String createMenu = "https://qyapi.weixin.qq.com/cgi-bin/menu/create";
	private static String deleteMenu = "https://qyapi.weixin.qq.com/cgi-bin/menu/delete";

	/**
	 * 查询菜单
	 */
	public static ApiResult getMenu(String agentid) {
		ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getQYAccessTokenStr()).put("agentid", agentid);
		String jsonResult = HttpUtils.get(getMenu, pm.getData());
		return new ApiResult(jsonResult);
	}

	/**
	 * 创建菜单
	 */
	public static ApiResult createMenu(String jsonStr, String agentid) {
		ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getQYAccessTokenStr()).put("agentid", agentid);
		String jsonResult = HttpKit.post(createMenu, pm.getData(), jsonStr);
		return new ApiResult(jsonResult);
	}

	/**
	 * 删除菜单
	 */
	public static ApiResult deleteMenu(String agentid) {
		ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getQYAccessTokenStr()).put("agentid", agentid);
		String jsonResult = HttpUtils.get(deleteMenu, pm.getData());
		return new ApiResult(jsonResult);
	}
}

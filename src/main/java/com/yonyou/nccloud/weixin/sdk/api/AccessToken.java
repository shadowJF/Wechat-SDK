package com.yonyou.nccloud.weixin.sdk.api;

import java.io.Serializable;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.yonyou.nccloud.weixin.sdk.utils.RetryUtils.ResultCheck;

/**
 * 封装 access_token
 */
public class AccessToken implements ResultCheck, Serializable {

	private static final long serialVersionUID = -822464425433824314L;

	private String access_token; // 正确获取到 access_token 时有值
	private Integer expires_in; // 正确获取到 access_token 时有值
	private Integer errcode; // 出错时有值
	private String errmsg; // 出错时有值

	private Long expiredTime; // 正确获取到 access_token 时有值，存放过期时间
	private String json;

	@SuppressWarnings("unchecked")
	public AccessToken(String jsonStr) {
		this.json = jsonStr;

		try {
			Map<String, Object> temp = JSON.parseObject(jsonStr, Map.class);
			access_token = (String) temp.get("access_token");
			expires_in = getInt(temp, "expires_in");
			errcode = getInt(temp, "errcode");
			errmsg = (String) temp.get("errmsg");

			if (expires_in != null)
				expiredTime = System.currentTimeMillis() + ((expires_in - 5) * 1000);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getJson() {
		return json;
	}

	public boolean isAvailable() {
		if (expiredTime == null)
			return false;
		if (errcode != null)
			return false;
		if (expiredTime < System.currentTimeMillis())
			return false;
		return access_token != null;
	}

	private Integer getInt(Map<String, Object> temp, String key) {
		Number number = (Number) temp.get(key);
		return number == null ? null : number.intValue();
	}

	public String getAccessToken() {
		return access_token;
	}

	public Integer getExpiresIn() {
		return expires_in;
	}

	public Integer getErrorCode() {
		return errcode;
	}

	public String getErrorMsg() {
		if (errcode != null) {
			String result = ReturnCode.get(errcode);
			if (result != null)
				return result;
		}
		return errmsg;
	}

	@Override
	public boolean matching() {
		return isAvailable();
	}
}

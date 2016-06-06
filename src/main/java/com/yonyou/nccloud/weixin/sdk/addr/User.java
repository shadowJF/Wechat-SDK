package com.yonyou.nccloud.weixin.sdk.addr;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * {<br/>
 * "userid": "Javen205",<br/>
 * "name": "Javen205",<br/>
 * "department": [2],<br/>
 * "position": "产品经理",<br/>
 * "mobile": "13545192175",<br/>
 * "gender": "1",<br/>
 * "email": "",<br/>
 * "weixinid": "Javen205",<br/>
 * "avatar_mediaid": "2-G6nrLmr5EC3MNb_-zL1dDdzkd0p7cNliYu9V5w7o8K0",<br/>
 * "extattr": {"attrs":[{"name":"爱好","value":"旅游"},{"name":"卡号","value":"
 * 1234567234"}]} }<br/>
 * 2016年6月4日
 */
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7917404695593984462L;
	private String id;
	private String corpid;
	private String userid;
	private String name;
	private String gender;
	private String department;
	private String mobile;
	private String email;
	private String weixinid;
	private String avatar_mediaid;
	private JSONObject extattr;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}

	public String getCorpid() {
		return corpid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getWeixinid() {
		return weixinid;
	}

	public void setWeixinid(String weixinid) {
		this.weixinid = weixinid;
	}

	public String getAvatar_mediaid() {
		return avatar_mediaid;
	}

	public void setAvatar_mediaid(String avatar_mediaid) {
		this.avatar_mediaid = avatar_mediaid;
	}

	public JSONObject getExtattr() {
		return extattr;
	}

	public void setExtattr(JSONObject extattr) {
		this.extattr = extattr;
	}

}

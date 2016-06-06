package com.yonyou.nccloud.weixin.sdk.sup;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.yonyou.nccloud.weixin.sdk.addr.Department;
import com.yonyou.nccloud.weixin.sdk.addr.User;
import com.yonyou.nccloud.weixin.sdk.api.AgentApi;
import com.yonyou.nccloud.weixin.sdk.api.QYApiConfig;
import com.yonyou.nccloud.weixin.sdk.api.QYApiConfigKit;
import com.yonyou.nccloud.weixin.sdk.api.ApiResult;
import com.yonyou.nccloud.weixin.sdk.api.QYChatApi;
import com.yonyou.nccloud.weixin.sdk.api.QYChatApi.ChatUrl;
import com.yonyou.nccloud.weixin.sdk.api.QYConBatchApi;
import com.yonyou.nccloud.weixin.sdk.api.QYConDepartmentApi;
import com.yonyou.nccloud.weixin.sdk.api.QYConTagApi;
import com.yonyou.nccloud.weixin.sdk.api.QYConUserApi;
import com.yonyou.nccloud.weixin.sdk.api.QYMenuApi;
import com.yonyou.nccloud.weixin.sdk.api.QYOAuthApi;
import com.yonyou.nccloud.weixin.sdk.api.QYSendMessageApi;
import com.yonyou.nccloud.weixin.sdk.api.media.QYMediaApi;
import com.yonyou.nccloud.weixin.sdk.api.media.QYMediaApi.MediaType;
import com.yonyou.nccloud.weixin.sdk.api.media.MediaFile;
import com.yonyou.nccloud.weixin.sdk.kit.HttpKit;
import com.yonyou.nccloud.weixin.sdk.kit.ParaMap;
import com.yonyou.nccloud.weixin.sdk.menu.Menu;
import com.yonyou.nccloud.weixin.sdk.msg.chat.ChatReceiver;
import com.yonyou.nccloud.weixin.sdk.msg.chat.ChatReceiver.ChatType;
import com.yonyou.nccloud.weixin.sdk.msg.chat.TextChat;
import com.yonyou.nccloud.weixin.sdk.msg.chat.TextChatMsg;
import com.yonyou.nccloud.weixin.sdk.msg.send.QiYeFileMsg;
import com.yonyou.nccloud.weixin.sdk.msg.send.QiYeImageMsg;
import com.yonyou.nccloud.weixin.sdk.msg.send.QiYeNewsMsg;
import com.yonyou.nccloud.weixin.sdk.msg.send.QiYeTextMsg;

/**
 * 企业发消息基类
 * @author jinjya
 * 2016年6月5日
 */
public class QYWechatApiSupport {

	/**
	 * 发送文本消息
	 */
	public static void sendTextMssage(HttpServletRequest request, HttpServletResponse response, QiYeTextMsg text) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			ApiResult sendTextMsg = QYSendMessageApi.sendTextMsg(text);
			HttpKit.renderText(response, sendTextMsg.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 图文混排的消息
	 */
	public static void sendNewsMessage(HttpServletRequest request, HttpServletResponse response,
			QiYeNewsMsg qiYeNewsMsg) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			ApiResult sendTextMsg = QYSendMessageApi.sendNewsMsg(qiYeNewsMsg);
			HttpKit.renderText(response, sendTextMsg.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 发送图片
	 */
	public static void sendImage(HttpServletRequest request, HttpServletResponse response, QiYeImageMsg image) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			ApiResult apiResult = QYSendMessageApi.sendImageMsg(image);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 发送文件
	 */
	public static void sendFile(HttpServletRequest request, HttpServletResponse response, QiYeFileMsg file) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			ApiResult apiResult = QYSendMessageApi.sendFileMsg(file);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 获取菜单
	 */
	public static void getMenuApi(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			String agentid = request.getParameter("agentid");
			HttpKit.renderText(response, (QYMenuApi.getMenu(agentid).getJson()));
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 删除菜单
	 */
	public static void deleteMenuApi(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			String agentid = request.getParameter("agentid");
			HttpKit.renderText(response, (QYMenuApi.deleteMenu(agentid).getJson()));
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 创建菜单
	 */
	public static void creatMenuApi(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String agentid = request.getParameter("agentid");
			String menustr = request.getParameter("menu");
			Menu menu = JSON.parseObject(menustr, Menu.class);
			HttpKit.renderText(response, (QYMenuApi.createMenu(JSON.toJSONString(menu), agentid).getJson()));
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 获取指定部分列表
	 * {"errcode":0,"errmsg":"ok","department":[{"id":1,"name":"企业号体验34797078","
	 * parentid":0,"order":200},{"id":2,"name":"研发","parentid":1,"order":200}]}
	 */
	public static void getDepartment(HttpServletRequest request, HttpServletResponse response) {

		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String deptid = request.getParameter("deptid");
			ApiResult apiResult = QYConDepartmentApi.getDepartment(deptid);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 创建部门
	 */
	public static void createDepartment(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String department = request.getParameter("department");
			Department dept = JSON.parseObject(department, Department.class);
			ApiResult apiResult = QYConDepartmentApi.createDepartment(JSON.toJSONString(dept));
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 更新部门
	 */
	public static void updateDepartment(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String department = request.getParameter("department");
			Department dept = JSON.parseObject(department, Department.class);
			ApiResult apiResult = QYConDepartmentApi.updateDepartment(JSON.toJSONString(dept));
			HttpKit.renderText(response, apiResult.toString());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 删除部门
	 */
	public static void deleteDepartment(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String deptid = request.getParameter("deptid");
			ApiResult apiResult = QYConDepartmentApi.deleteDepartment(deptid);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 获取成员
	 */
	public static void getUser(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String userid = request.getParameter("userid");
			ApiResult apiResult = QYConUserApi.getUser(userid);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 创建成员
	 */
	public static void createUser(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String userstr = request.getParameter("user");
			User user = JSON.parseObject(userstr, User.class);
			ApiResult apiResult = QYConUserApi.createUser(JSON.toJSONString(user));
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 更新成员
	 */
	public static void updateUser(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String userstr = request.getParameter("user");
			User user = JSON.parseObject(userstr, User.class);
			ApiResult apiResult = QYConUserApi.updateUser(JSON.toJSONString(user));
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 删除成员
	 */
	public static void deleteUser(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String userid = request.getParameter("userid");
			ApiResult apiResult = QYConUserApi.deleteUser(userid);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 批量删除成员 "{" + "\"useridlist\": [\"zhangsan\", \"lisi\"]\"}"
	 */
	public static void batchDeleteUser(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String useridlist = request.getParameter("useridlist");
			ApiResult apiResult = QYConUserApi.batchDeleteUser(useridlist);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 获取部门成员
	 * 
	 * @param departmentId
	 *            获取的部门id
	 * @param fetch_child
	 *            1/0：是否递归获取子部门下面的成员
	 * @param status
	 *            0获取全部成员，1获取已关注成员列表，2获取禁用成员列表，4获取未关注成员列表。status可叠加，未填写则默认为4
	 */
	public static void getDepartmentUserSimpleList(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String department_id = request.getParameter("department_id");
			String fetch_child = request.getParameter("fetch_child");
			String status = request.getParameter("status");
			ApiResult apiResult = QYConUserApi.getDepartmentUserSimpleList(department_id, fetch_child, status);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 获取部门成员(详情)
	 */
	public static void getDepartmentUserList(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String department_id = request.getParameter("department_id");
			String fetch_child = request.getParameter("fetch_child");
			String status = request.getParameter("status");
			ApiResult apiResult = QYConUserApi.getDepartmentUserSimpleList(department_id, fetch_child, status);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}
	
	public static void replaceDept(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));
			String media_id = request.getParameter("media_id");
			ParaMap pm = ParaMap.create("media_id",media_id);
			ApiResult apiResult = QYConDepartmentApi.replaceDept(pm.getData().toString());
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 邀请成员关注 测试貌似只能使用邮箱要求 {"data":"{\"userid\":\"Javen205\"}"}
	 */
	public static void inviteUser(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String data = request.getParameter("data");
			ApiResult apiResult = QYConUserApi.inviteUser(data);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 创建标签 {"data":"{\"tagname\": \"UI\",\"tagid\": id}"}
	 */
	public static void createTag(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String data = request.getParameter("data");
			ApiResult apiResult = QYConTagApi.createTag(data);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 更新标签名字 {"data":"{\"tagname\": \"UI-test\",\"tagid\": 1}"}
	 */
	public static void updateTag(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String data = request.getParameter("data");
			ApiResult apiResult = QYConTagApi.updateTag(data);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 删除标签
	 */
	public static void deleteTag(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String tagid = request.getParameter("tagid");
			ApiResult apiResult = QYConTagApi.deleteTag(tagid);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 获取标签成员
	 */
	public static void getTagUser(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String tagid = request.getParameter("tagid");
			ApiResult apiResult = QYConTagApi.getTagUser(tagid);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 增加标签成员 {"data": "{\"tagid\": \"1\"," + "\"userlist\":[
	 * \"Javen\",\"lisi\"]," + "\"partylist\": []}"}
	 */
	public static void addTagUsers(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String data = request.getParameter("data");
			ApiResult apiResult = QYConTagApi.addTagUsers(data);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 删除标签成员{"data": "{\"tagid\": \"1\"," + "\"userlist\":[
	 * \"Javen\",\"lisi\"]," + "\"partylist\": []}"}
	 */
	public static void delTagUser(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String data = request.getParameter("data");
			ApiResult apiResult = QYConTagApi.deleteTagUsers(data);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 获取标签列表
	 */
	public static void getTagList(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			ApiResult apiResult = QYConTagApi.getTagList();
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 邀请成员关注{"data": "{" + "\"touser\":\"lisi|Javen\"," + "\"toparty\":\"3\","
	 * + "\"totag\":\"1\"," + "\"callback\":" + "{" + " \"url\":
	 * \"http://javen.ngrok.natapp.cn/qymsg\"," + " \"token\": \"Javen\"," + "
	 * \"encodingaeskey\": \"sPqS4op3rKjOT7XbWJkDr5Kqq6v6oL3enZ8oY6hrK8b\"" +
	 * "}" + " }"}
	 */
	public static void inviteUsers(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String data = request.getParameter("data");
			ApiResult apiResult = QYConBatchApi.inviteUsers(data);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 获取异步任务结果
	 */
	public static void batchGetResult(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String jobId = request.getParameter("jobId");
			ApiResult apiResult = QYConBatchApi.batchGetResult(jobId);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	public static void uploadFile(HttpServletRequest request, HttpServletResponse response, File file) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			ApiResult apiResult = QYMediaApi.uploadMedia(MediaType.FILE, file);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * ApiResult apiResult = MediaApi.uploadMedia(MediaType.IMAGE, file); String
	 * json = apiResult.getJson(); String mediaId =
	 * JSON.parseObject(json).getString("media_id"); QiYeImageMsg image = new
	 * QiYeImageMsg(mediaId); image.setAgentid("22"); image.setSafe("0");
	 * image.setTouser("Javen");
	 */
	public static void uploadImage(HttpServletRequest request, HttpServletResponse response, File file,
			QiYeImageMsg image) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			ApiResult apiResult = QYMediaApi.uploadMedia(MediaType.IMAGE, file);
			String json = apiResult.getJson();
			String mediaId = JSON.parseObject(json).getString("media_id");
			image.setMedia_id(mediaId);
			ApiResult sendImageMsg = QYSendMessageApi.sendImageMsg(image);
			HttpKit.renderText(response, json + " " + sendImageMsg.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	public static void getMediaDate(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String media_id = request.getParameter("media_id");
			MediaFile mediaFile = QYMediaApi.getMedia(media_id);
			HttpKit.renderText(response, mediaFile.toString());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	public static void getMaterialCount(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String agentid = request.getParameter("agentid");
			ApiResult apiResult = QYMediaApi.getMaterialCount(agentid);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	// public void batchGetMaterial() {
	// ApiResult apiResult = MediaApi.batchGetMaterial(MediaType.IMAGE, 0, 20,
	// 22);
	// HttpKit.renderText(response, apiResult.getJson());
	// }

	/**
	 * {"data","{" + "\"media_id\":\"
	 * 1g45y7tvRx9dyk3jnaiMl5XR48dBcrPkl3SxfNJYC4mf3AYb6yLqs_dG1mK1mXVEzQ5zOprkWoF01x2uP290E2g
	 * \"," + "\"callback\":" + "{" + " \"url\":
	 * \"http://javen.ngrok.natapp.cn/qymsg\"," + " \"token\": \"Javen\"," + "
	 * \"encodingaeskey\": \"sPqS4op3rKjOT7XbWJkDr5Kqq6v6oL3enZ8oY6hrK8b\"" +
	 * "}" + " }""}"
	 * 
	 * 
	 * 
	 * @param request
	 * @param response
	 */
	public static void updateSyncUser(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String data = request.getParameter("data");
			ApiResult apiResult = QYConBatchApi.updateSyncUser(data);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 获取企业号应用
	 */
	public static void getAgent(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String agentid = request.getParameter("agentid");
			ApiResult apiResult = AgentApi.getAgent(agentid);
			HttpKit.renderText(response, apiResult.toString());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 设置企业号应用{"data":"{" + "\"agentid\": \"22\"," + "\"report_location_flag\":
	 * \"1\"," + // "\"logo_mediaid\": \"xxxxx\","+ "\"name\": \"智慧云端日记\"," +
	 * "\"description\": \"企业号测试应用\"," + "\"redirect_domain\":
	 * \"javen.ngrok.natapp.cn\"," + "\"isreportuser\":1," +
	 * "\"isreportenter\":1}";}
	 */
	public static void setAgent(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String data = request.getParameter("data");
			ApiResult apiResult = AgentApi.setAgent(data);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 获取应用概况列表
	 */
	public static void getListAgent(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			ApiResult apiResult = AgentApi.getListAgent();
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 如果用户未关注将无法转化
	 * openid转换成userid接口{"data":"{\"openid\":\"oD3e5jpSC3C8Qq5uon_SEeRwc9AA\"}"}
	 */
	public static void toUserId(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String data = request.getParameter("data");
			ApiResult apiResult = QYOAuthApi.ToUserId(data);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}

	}

	/**
	 * 发送人：接收人或chatid, 发送人：sender,内容：content
	 * 
	 * @param request
	 * @param response
	 */
	public static void sendTextChat(HttpServletRequest request, HttpServletResponse response) {
		try {
			QYApiConfigKit.setThreadLocalApiConfig(getApiConfig(request));

			String userid = request.getParameter("userid");
			String sender = request.getParameter("sender");
			String content = request.getParameter("content");
			TextChat textChat = new TextChat();
			ChatReceiver receiver = new ChatReceiver();
			receiver.setType(ChatType.single);
			receiver.setId(userid);
			textChat.setReceiver(receiver);
			textChat.setSender(sender);
			textChat.setText(new TextChatMsg(content));
			String data = JSON.toJSONString(textChat);
			ApiResult apiResult = QYChatApi.Chat(ChatUrl.sendUrl, data);
			HttpKit.renderText(response, apiResult.getJson());
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 默认作为参数传入，如果请求没传则通过request.getAttribute("appconfig")获取
	 * 
	 * @param request
	 * @return
	 */
	public static QYApiConfig getApiConfig(HttpServletRequest request) {
		String corpId = request.getParameter("corpId");
		String secret = request.getParameter("secret");
		String encryptMessage = request.getParameter("encryptMessage");
		String encodingAesKey = request.getParameter("encodingAesKey");
		String token = request.getParameter("token");
		if (StringUtils.isBlank(corpId) || StringUtils.isBlank(secret) || StringUtils.isBlank(token)) {
			QYApiConfig appconfig = (QYApiConfig) request.getAttribute("appconfig");
			if (appconfig != null) {
				return appconfig;
			}
		}
		QYApiConfig appconfig = new QYApiConfig();
		appconfig.setCorpId(corpId);
		appconfig.setCorpSecret(secret);
		appconfig.setEncodingAesKey(encodingAesKey);
		appconfig.setToken(token);
		appconfig.setEncryptMessage(Boolean.valueOf(encryptMessage));
		return appconfig;
	}
}

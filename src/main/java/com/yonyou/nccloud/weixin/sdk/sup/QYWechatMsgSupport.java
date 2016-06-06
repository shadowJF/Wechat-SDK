package com.yonyou.nccloud.weixin.sdk.sup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.yonyou.nccloud.weixin.sdk.api.QYApiConfig;
import com.yonyou.nccloud.weixin.sdk.api.QYApiConfigKit;
import com.yonyou.nccloud.weixin.sdk.kit.HttpKit;
import com.yonyou.nccloud.weixin.sdk.kit.MsgEncryptKit;
import com.yonyou.nccloud.weixin.sdk.kit.SignatureCheckKit;
import com.yonyou.nccloud.weixin.sdk.msg.QYInMsgParser;
import com.yonyou.nccloud.weixin.sdk.msg.OutMsgXmlBuilder;
import com.yonyou.nccloud.weixin.sdk.msg.in.InImageMsg;
import com.yonyou.nccloud.weixin.sdk.msg.in.InLocationMsg;
import com.yonyou.nccloud.weixin.sdk.msg.in.InMsg;
import com.yonyou.nccloud.weixin.sdk.msg.in.InShortVideoMsg;
import com.yonyou.nccloud.weixin.sdk.msg.in.InTextMsg;
import com.yonyou.nccloud.weixin.sdk.msg.in.InVideoMsg;
import com.yonyou.nccloud.weixin.sdk.msg.in.InVoiceMsg;
import com.yonyou.nccloud.weixin.sdk.msg.in.event.InEnterAgentEvent;
import com.yonyou.nccloud.weixin.sdk.msg.in.event.InFollowEvent;
import com.yonyou.nccloud.weixin.sdk.msg.in.event.InJobEvent;
import com.yonyou.nccloud.weixin.sdk.msg.in.event.InLocationEvent;
import com.yonyou.nccloud.weixin.sdk.msg.in.event.InMenuEvent;
import com.yonyou.nccloud.weixin.sdk.msg.in.event.QYInQrCodeEvent;
import com.yonyou.nccloud.weixin.sdk.msg.out.OutMsg;
import com.yonyou.nccloud.weixin.sdk.msg.out.OutTextMsg;

/**
 * 接收微信服务器消息，自动解析成 InMsg 并分发到相应的处理方法
 * @author jinjya
 * 2016年6月5日
 */
public abstract class QYWechatMsgSupport {

	private static final Logger log = Logger.getLogger(QYWechatMsgSupport.class);
	private String inMsgXml = null; // 本次请求 xml数据
	private InMsg inMsg = null; // 本次请求 xml 解析后的 InMsg 对象

	/**
	 * 配置开发者中心微信服务器所需的 url 与 token
	 * 
	 * @return true 为config server 请求，false 正式消息交互请求
	 */
	public void bindServer(HttpServletRequest request, HttpServletResponse response) {
		configServer(request, response);
	}

	/**
	 * weixin 公众号服务器调用唯一入口，即在开发者中心输入的 URL 必须要指向此 action
	 */
	public void processRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 如果是服务器配置请求，则配置服务器并返回
			if (isConfigServerRequest(request)) {
				log.error("是服务器配置请求");
				configServer(request, response);
				return;
			}
			QYApiConfig appconfig = getApiConfig(request);
			QYApiConfigKit.setThreadLocalApiConfig(appconfig);
			processRequestMessage(request, response);
		} finally {
			QYApiConfigKit.removeThreadLocalApiConfig();
		}
	}

	/**
	 * 开始处理消息
	 * 
	 * @param request
	 * @param response
	 */
	protected void processRequestMessage(HttpServletRequest request, HttpServletResponse response) {
		// 开发模式输出微信服务发送过来的 xml 消息
		if (QYApiConfigKit.isDevMode()) {
			System.out.println("接收消息:");
			System.out.println(getInMsgXml(request, response));
		}

		// 解析消息并根据消息类型分发到相应的处理方法
		InMsg msg = getInMsg(request, response);
		if (msg instanceof InTextMsg)
			processInTextMsg((InTextMsg) msg);
		else if (msg instanceof InImageMsg)
			processInImageMsg((InImageMsg) msg);
		else if (msg instanceof InVoiceMsg)
			processInVoiceMsg((InVoiceMsg) msg);
		else if (msg instanceof InVideoMsg)
			processInVideoMsg((InVideoMsg) msg);
		else if (msg instanceof InShortVideoMsg) // 支持小视频
			processInShortVideoMsg((InShortVideoMsg) msg);
		else if (msg instanceof InLocationMsg)
			processInLocationMsg((InLocationMsg) msg);
		else if (msg instanceof InFollowEvent)
			processInFollowEvent((InFollowEvent) msg);
		else if (msg instanceof InLocationEvent)
			processInLocationEvent((InLocationEvent) msg);
		else if (msg instanceof InMenuEvent)
			processInMenuEvent((InMenuEvent) msg);
		else if (msg instanceof InEnterAgentEvent)
			processInEnterAgentEvent((InEnterAgentEvent) msg);
		else if (msg instanceof QYInQrCodeEvent)
			processInQrCodeEvent((QYInQrCodeEvent) msg);
		else if (msg instanceof InJobEvent)
			processInJobEvent((InJobEvent) msg);

		else
			log.error("未能识别的消息类型。 消息 xml 内容为：\n" + getInMsgXml(request, response));
	}

	/**
	 * 在接收到微信服务器的 InMsg 消息后后响应 OutMsg 消息
	 */
	public void render(HttpServletRequest request, HttpServletResponse response, OutMsg outMsg) {
		String outMsgXml = OutMsgXmlBuilder.build(outMsg);
		// 开发模式向控制台输出即将发送的 OutMsg 消息的 xml 内容
		if (QYApiConfigKit.isDevMode()) {
			System.out.println("发送消息:");
			System.out.println(outMsgXml);
			System.out.println("--------------------------------------------------------------------------------\n");
		}

		// 是否需要加密消息
		if (QYApiConfigKit.getApiConfig().isEncryptMessage()) {
			outMsgXml = MsgEncryptKit.encrypt(outMsgXml, request.getParameter("timestamp"),
					request.getParameter("nonce"));
		}

		HttpKit.renderText(response, outMsgXml, "text/xml");
	}

	public void renderOutTextMsg(HttpServletRequest request, HttpServletResponse response, String content) {
		OutTextMsg outMsg = new OutTextMsg(getInMsg(request, response));
		outMsg.setContent(content);
		render(request, response, outMsg);
	}

	public String getInMsgXml(HttpServletRequest request, HttpServletResponse response) {
		if (inMsgXml == null) {
			inMsgXml = HttpKit.readData(request);
			if (!QYApiConfigKit.isDevMode()) {
				if (!SignatureCheckKit.me.checkSignature(request, response, inMsgXml)) {
					HttpKit.renderText(response, "签名验证失败，请确定是微信服务器在发送消息过来");
				}
			}
			// 是否需要解密消息
			if (QYApiConfigKit.getApiConfig().isEncryptMessage()) {
				inMsgXml = MsgEncryptKit.decrypt(inMsgXml, request.getParameter("timestamp"),
						request.getParameter("nonce"), request.getParameter("msg_signature"));
			}
		}
		return inMsgXml;
	}

	public InMsg getInMsg(HttpServletRequest request, HttpServletResponse response) {
		if (inMsg == null)
			inMsg = QYInMsgParser.parse(getInMsgXml(request, response));
		return inMsg;
	}

	// 处理接收到的文本消息
	protected abstract void processInTextMsg(InTextMsg inTextMsg);

	// 处理接收到的图片消息
	protected abstract void processInImageMsg(InImageMsg inImageMsg);

	// 处理接收到的语音消息
	protected abstract void processInVoiceMsg(InVoiceMsg inVoiceMsg);

	// 处理接收到的视频消息
	protected abstract void processInVideoMsg(InVideoMsg inVideoMsg);

	// 处理接收到的视频消息
	protected abstract void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg);

	// 处理接收到的地址位置消息
	protected abstract void processInLocationMsg(InLocationMsg inLocationMsg);

	// 处理接收到的关注/取消关注事件
	protected abstract void processInFollowEvent(InFollowEvent inFollowEvent);

	// 处理接收到的上报地理位置事件
	protected abstract void processInLocationEvent(InLocationEvent inLocationEvent);

	// 处理接收到的自定义菜单事件
	protected abstract void processInMenuEvent(InMenuEvent inMenuEvent);

	// 处理接收到的扫描带参数二维码事件
	protected abstract void processInQrCodeEvent(QYInQrCodeEvent inQrCodeEvent);

	// 处理接收到的成员进入应用的事件
	protected abstract void processInEnterAgentEvent(InEnterAgentEvent inAgentEvent);

	// 处理接收到的异步任务完成事件事件
	protected abstract void processInJobEvent(InJobEvent inJobEvent);

	/**
	 * 是否为开发者中心保存服务器配置的请求
	 */
	protected boolean isConfigServerRequest(HttpServletRequest request) {
		return StringUtils.isNotBlank(request.getParameter("echostr"));
	}

	protected QYApiConfig getApiConfig(HttpServletRequest request) {
		String corpId = request.getParameter("corpId");
		String secret = request.getParameter("secret");
		String encryptMessage = request.getParameter("encryptMessage");
		String encodingAesKey = request.getParameter("encodingAesKey");
		String token = request.getParameter("token");
		QYApiConfig appconfig = new QYApiConfig();
		appconfig.setCorpId(corpId);
		appconfig.setCorpSecret(secret);
		appconfig.setEncodingAesKey(encodingAesKey);
		appconfig.setToken(token);
		appconfig.setEncryptMessage(Boolean.valueOf(encryptMessage));
		return appconfig;
	}

	/**
	 * 配置开发者中心微信服务器所需的 url 与 token
	 * 
	 * @return true 为config server 请求，false 正式消息交互请求
	 */
	protected void configServer(HttpServletRequest request, HttpServletResponse response) {
		String echostr = request.getParameter("echostr");
		String signature = request.getParameter("msg_signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		HttpKit.renderText(response, SignatureCheckKit.me.VerifyURL(signature, timestamp, nonce, echostr));
	}

}

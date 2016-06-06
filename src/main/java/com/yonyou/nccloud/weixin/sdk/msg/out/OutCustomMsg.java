package com.yonyou.nccloud.weixin.sdk.msg.out;

import com.yonyou.nccloud.weixin.sdk.msg.in.InMsg;


/**
    转发多客服消息
	<xml>
		<ToUserName><![CDATA[toUser]]></ToUserName>
		<FromUserName><![CDATA[fromUser]]></FromUserName>
		<CreateTime>12345678</CreateTime>
		<MsgType><![CDATA[transfer_customer_service]]></MsgType>
	</xml>
 */
public class OutCustomMsg extends OutMsg {
	public static final String TEMPLATE =
			"<xml>\n" +
			"<ToUserName><![CDATA[${__msg.toUserName}]]></ToUserName>\n" +
			"<FromUserName><![CDATA[${__msg.fromUserName}]]></FromUserName>\n" +
			"<CreateTime>${__msg.createTime}</CreateTime>\n" +
			"<MsgType><![CDATA[${__msg.msgType}]]></MsgType>\n" +
			"</xml>";

	private String content;

	public OutCustomMsg() {
		this.msgType = "transfer_customer_service";
	}

	public OutCustomMsg(InMsg inMsg) {
		super(inMsg);
		this.msgType = "transfer_customer_service";
	}
	
	public String getContent() {
		return content;
	}
	
	public OutCustomMsg setContent(String content) {
		this.content = content;
		return this;
	}
}



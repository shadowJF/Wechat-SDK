package com.yonyou.nccloud.weixin.sdk.kit;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import com.yonyou.nccloud.weixin.sdk.api.QYApiConfigKit;

public class SignatureCheckKit {
  private static final Logger log = Logger.getLogger(SignatureCheckKit.class);
  public static final SignatureCheckKit me = new SignatureCheckKit();

  public boolean checkSignature(String msgSignature, String timestamp, String nonce) {
    String TOKEN = QYApiConfigKit.getApiConfig().getToken();
    String array[] = {TOKEN, timestamp, nonce};
    Arrays.sort(array);
    String tempStr = new StringBuilder().append(array[0] + array[1] + array[2]).toString();
    tempStr = HashKit.sha1(tempStr);
    return tempStr.equalsIgnoreCase(msgSignature);
  }

  public boolean checkSignature(String msgSignature, String timestamp, String nonce, String content) {
    String TOKEN = QYApiConfigKit.getApiConfig().getToken();
    String array[] = {TOKEN, timestamp, nonce, content};
    Arrays.sort(array);
    String tempStr =
        new StringBuilder().append(array[0] + array[1] + array[2] + array[3]).toString();
    tempStr = HashKit.sha1(tempStr);
    return tempStr.equalsIgnoreCase(msgSignature);
  }

  public String VerifyURL(String msgSignature, String timeStamp, String nonce, String echoStr) {
    String result = null;
    try {
      String token = QYApiConfigKit.getApiConfig().getToken();
      String corpId = QYApiConfigKit.getApiConfig().getCorpId();
      String encodingAesKey = QYApiConfigKit.getApiConfig().getEncodingAesKey();
      WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, corpId);
      result = wxcpt.VerifyURL(msgSignature, timeStamp, nonce, echoStr);
    } catch (AesException e) {
      e.printStackTrace();
    }
    return result;

  }

  /**
   * 检测签名
   */
  public boolean checkSignature(HttpServletRequest request, HttpServletResponse response, String xml) {
    String signature = request.getParameter("msg_signature");
    String timestamp = request.getParameter("timestamp");
    String nonce = request.getParameter("nonce");
    String content = getEncrypt(xml);

    if (StringUtils.isBlank(signature) || StringUtils.isBlank(timestamp)
        || StringUtils.isBlank(nonce)) {
      HttpKit.renderText(response, "check signature failure");
      return false;
    }

    if (SignatureCheckKit.me.checkSignature(signature, timestamp, nonce, content)) {
      return true;
    } else {
      log.error("check signature failure: " + " signature = "
          + request.getParameter("msg_signature") + " timestamp = "
          + request.getParameter("timestamp") + " nonce = " + request.getParameter("nonce")
          + " content = " + getEncrypt(xml));

      return false;
    }
  }

  /**
   * 检测签名 vertx版本
   */
  public boolean checkSignature(HttpServerRequest request, HttpServerResponse response, String xml) {
    String signature = request.getParam("msg_signature");
    String timestamp = request.getParam("timestamp");
    String nonce = request.getParam("nonce");
    String content = getEncrypt(xml);

    if (StringUtils.isBlank(signature) || StringUtils.isBlank(timestamp)
        || StringUtils.isBlank(nonce)) {
      HttpKit.renderText(response, "check signature failure");
      return false;
    }

    if (SignatureCheckKit.me.checkSignature(signature, timestamp, nonce, content)) {
      return true;
    } else {
      log.error("check signature failure: " + " signature = " + request.getParam("msg_signature")
          + " timestamp = " + request.getParam("timestamp") + " nonce = "
          + request.getParam("nonce") + " content = " + getEncrypt(xml));

      return false;
    }
  }

  private String getEncrypt(String xml) {
    try {
      Document doc = DocumentHelper.parseText(xml);
      Element root = doc.getRootElement();
      String content = root.elementText("Encrypt");

      return content;
    } catch (DocumentException e) {
    }

    return null;

  }

}

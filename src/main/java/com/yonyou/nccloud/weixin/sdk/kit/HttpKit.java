package com.yonyou.nccloud.weixin.sdk.kit;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * HttpKit
 */
public class HttpKit {

  private static Logger log = LoggerFactory.getLogger(HttpKit.class);

  private HttpKit() {}

  /**
   * https 鍩熷悕鏍￠獙
   */
  private class TrustAnyHostnameVerifier implements HostnameVerifier {
    public boolean verify(String hostname, SSLSession session) {
      return true;
    }
  }

  /**
   * https 璇佷功绠＄悊
   */
  private class TrustAnyTrustManager implements X509TrustManager {
    public X509Certificate[] getAcceptedIssuers() {
      return null;
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {}

    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {}
  }

  private static final String GET = "GET";
  private static final String POST = "POST";
  private static String CHARSET = "UTF-8";

  private static final SSLSocketFactory sslSocketFactory = initSSLSocketFactory();
  private static final TrustAnyHostnameVerifier trustAnyHostnameVerifier =
      new HttpKit().new TrustAnyHostnameVerifier();

  private static SSLSocketFactory initSSLSocketFactory() {
    try {
      TrustManager[] tm = {new HttpKit().new TrustAnyTrustManager()};
      SSLContext sslContext = SSLContext.getInstance("TLS"); // ("TLS", "SunJSSE");
      sslContext.init(null, tm, new java.security.SecureRandom());
      return sslContext.getSocketFactory();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void setCharSet(String charSet) {
    if (StringUtils.isBlank(charSet)) {
      throw new IllegalArgumentException("charSet can not be blank.");
    }
    HttpKit.CHARSET = charSet;
  }

  private static HttpURLConnection getHttpConnection(String url, String method,
      Map<String, String> headers) throws IOException, NoSuchAlgorithmException,
      NoSuchProviderException, KeyManagementException {
    URL _url = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
    if (conn instanceof HttpsURLConnection) {
      ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
      ((HttpsURLConnection) conn).setHostnameVerifier(trustAnyHostnameVerifier);
    }

    conn.setRequestMethod(method);
    conn.setDoOutput(true);
    conn.setDoInput(true);

    conn.setConnectTimeout(19000);
    conn.setReadTimeout(19000);

    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    conn.setRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");

    if (headers != null && !headers.isEmpty())
      for (Entry<String, String> entry : headers.entrySet())
        conn.setRequestProperty(entry.getKey(), entry.getValue());

    return conn;
  }

  /**
   * Send GET request
   */
  public static String get(String url, Map<String, String> queryParas, Map<String, String> headers) {
    HttpURLConnection conn = null;
    try {
      conn = getHttpConnection(buildUrlWithQueryString(url, queryParas), GET, headers);
      conn.connect();
      return readResponseString(conn);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }

  public static String get(String url, Map<String, String> queryParas) {
    return get(url, queryParas, null);
  }

  public static String get(String url) {
    return get(url, null, null);
  }

  /**
   * Send POST request
   */
  public static String post(String url, Map<String, String> queryParas, String data,
      Map<String, String> headers) {
    HttpURLConnection conn = null;
    try {
      conn = getHttpConnection(buildUrlWithQueryString(url, queryParas), POST, headers);
      conn.connect();

      OutputStream out = conn.getOutputStream();
      out.write(data.getBytes(CHARSET));
      out.flush();
      out.close();

      return readResponseString(conn);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }

  public static String post(String url, Map<String, String> queryParas, String data) {
    return post(url, queryParas, data, null);
  }

  public static String post(String url, String data, Map<String, String> headers) {
    return post(url, null, data, headers);
  }

  public static String post(String url, String data) {
    return post(url, null, data, null);
  }

  private static String readResponseString(HttpURLConnection conn) {
    StringBuilder sb = new StringBuilder();
    InputStream inputStream = null;
    try {
      inputStream = conn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET));
      String line = null;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }
      return sb.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          log.error(e.getMessage(), e);
        }
      }
    }
  }

  /**
   * Build queryString of the url
   */
  private static String buildUrlWithQueryString(String url, Map<String, String> queryParas) {
    if (queryParas == null || queryParas.isEmpty())
      return url;

    StringBuilder sb = new StringBuilder(url);
    boolean isFirst;
    if (url.indexOf("?") == -1) {
      isFirst = true;
      sb.append("?");
    } else {
      isFirst = false;
    }

    for (Entry<String, String> entry : queryParas.entrySet()) {
      if (isFirst)
        isFirst = false;
      else
        sb.append("&");

      String key = entry.getKey();
      String value = entry.getValue();
      if (StringUtils.isNotBlank(value))
        try {
          value = URLEncoder.encode(value, CHARSET);
        } catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
        }
      sb.append(key).append("=").append(value);
    }
    return sb.toString();
  }

  public static String readData(HttpServletRequest request) {
    BufferedReader br = null;
    try {
      StringBuilder result = new StringBuilder();
      br = request.getReader();
      for (String line = null; (line = br.readLine()) != null;) {
        result.append(line).append("\n");
      }

      return result.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (br != null)
        try {
          br.close();
        } catch (IOException e) {
          log.error(e.getMessage(), e);
        }
    }
  }

  /**
   * 
   * <p>
   * 说明：vertx版本
   * <li></li>
   * </p>
   * 
   * @param request
   * @return
   * @date 2016年6月8日 上午9:50:38
   * @since NC6.5
   */
  public static String readData(RoutingContext rc) {
    return rc.getBodyAsString();
  }

  public static void renderText(HttpServletResponse response, String text) {
    renderText(response, text, "text/plain");
  }

  /**
   * 
   * <p>
   * 说明：vertx版本
   * <li></li>
   * </p>
   * 
   * @param response
   * @param text
   * @date 2016年6月8日 上午9:38:25
   * @since NC6.5
   */
  public static void renderText(HttpServerResponse response, String text) {
    renderText(response, text, "text/plain");
  }

  public static void renderText(HttpServletResponse response, String text, String contenttype) {
    PrintWriter writer = null;
    try {
      response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might not implement Cache-Control
                                                // and might only implement Pragma: no-cache
      response.setHeader("Cache-Control", "no-cache");
      response.setDateHeader("Expires", 0);
      response.setContentType(contenttype);
      response.setCharacterEncoding("UTF-8");
      writer = response.getWriter();
      writer.write(text);
      writer.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (writer != null)
        writer.close();
    }
  }

  /**
   * 
   * <p>
   * 说明：vertx版本
   * <li></li>
   * </p>
   * 
   * @param response
   * @param text
   * @param contenttype
   * @date 2016年6月8日 上午9:38:40
   * @since NC6.5
   */
  public static void renderText(HttpServerResponse response, String text, String contenttype) {
    response.putHeader("Pragma", "no-cache");// HTTP/1.0 caches might not implement Cache-Control
                                             // and might only implement Pragma: no-cache
    response.putHeader("Cache-Control", "no-cache");
    response.putHeader("Expires", "Thu, 01 Jan 1970 00:00:00 GMT");
    response.putHeader("Content-Type", contenttype + ";charset=UTF-8");
    response.end(text);
  }


  @Deprecated
  public static String readIncommingRequestData(HttpServletRequest request) {
    return readData(request);
  }
}

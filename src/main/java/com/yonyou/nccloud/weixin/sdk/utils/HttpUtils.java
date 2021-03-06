package com.yonyou.nccloud.weixin.sdk.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.commons.lang.StringUtils;

import com.yonyou.nccloud.weixin.sdk.api.media.MediaFile;
import com.yonyou.nccloud.weixin.sdk.kit.HttpKit;

/**
 * JFinal-weixin Http请求工具类
 * 
 * @author L.cm
 */
public final class HttpUtils {

	private HttpUtils() {
	}

	public static String get(String url) {
		return delegate.get(url);
	}

	public static String get(String url, Map<String, String> queryParas) {
		return delegate.get(url, queryParas);
	}

	public static String post(String url, String data) {
		return delegate.post(url, data);
	}

	public static String postSSL(String url, String data, String certPath, String certPass) {
		return delegate.postSSL(url, data, certPath, certPass);
	}

	public static MediaFile download(String url) {
		return delegate.download(url);
	}

	public static InputStream download(String url, String params) {
		return delegate.download(url, params);
	}

	public static String upload(String url, File file, String params) {
		return delegate.upload(url, file, params);
	}

	/**
	 * http请求工具 委托 优先使用OkHttp 最后使用JFinal HttpKit
	 */
	private interface HttpDelegate {
		String get(String url);

		String get(String url, Map<String, String> queryParas);

		String post(String url, String data);

		String postSSL(String url, String data, String certPath, String certPass);

		MediaFile download(String url);

		InputStream download(String url, String params);

		String upload(String url, File file, String params);
	}

	// http请求工具代理对象
	private static final HttpDelegate delegate;

	static {
		HttpDelegate delegateToUse = null;
		// com.squareup.okhttp.OkHttpClient?
		if (ClassUtils.isPresent("com.squareup.okhttp.OkHttpClient", HttpUtils.class.getClassLoader())) {
			delegateToUse = new OkHttpDelegate();
		}
		// com.yonyou.nccloud.weixin.sdk.kit.HttpKit
		else if (ClassUtils.isPresent("com.yonyou.nccloud.weixin.sdk.kit.HttpKit", HttpUtils.class.getClassLoader())) {
			delegateToUse = new HttpKitDelegate();
		}
		delegate = delegateToUse;
	}

	/**
	 * OkHttp代理
	 */
	private static class OkHttpDelegate implements HttpDelegate {
		private final com.squareup.okhttp.OkHttpClient httpClient;
		private final com.squareup.okhttp.OkHttpClient httpsClient;

		Lock lock = new ReentrantLock();

		public OkHttpDelegate() {
			httpClient = new com.squareup.okhttp.OkHttpClient();
			// 分别设置Http的连接,写入,读取的超时时间为30秒
			httpClient.setConnectTimeout(10, TimeUnit.SECONDS);
			httpClient.setWriteTimeout(10, TimeUnit.SECONDS);
			httpClient.setReadTimeout(30, TimeUnit.SECONDS);

			httpsClient = httpClient.clone();
		}

		private static final com.squareup.okhttp.MediaType CONTENT_TYPE_FORM = com.squareup.okhttp.MediaType
				.parse("application/x-www-form-urlencoded");

		private String exec(com.squareup.okhttp.Request request) {
			try {
				com.squareup.okhttp.Response response = httpClient.newCall(request).execute();

				if (!response.isSuccessful())
					throw new RuntimeException("Unexpected code " + response);

				return response.body().string();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String get(String url) {
			com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(url).get().build();
			return exec(request);
		}

		@Override
		public String get(String url, Map<String, String> queryParas) {
			com.squareup.okhttp.HttpUrl.Builder urlBuilder = com.squareup.okhttp.HttpUrl.parse(url).newBuilder();
			for (Entry<String, String> entry : queryParas.entrySet()) {
				urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
			}
			com.squareup.okhttp.HttpUrl httpUrl = urlBuilder.build();
			com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(httpUrl).get().build();
			return exec(request);
		}

		@Override
		public String post(String url, String params) {
			com.squareup.okhttp.RequestBody body = com.squareup.okhttp.RequestBody.create(CONTENT_TYPE_FORM, params);
			com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(url).post(body).build();
			return exec(request);
		}

		@Override
		public String postSSL(String url, String data, String certPath, String certPass) {
			com.squareup.okhttp.RequestBody body = com.squareup.okhttp.RequestBody.create(CONTENT_TYPE_FORM, data);
			com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(url).post(body).build();

			InputStream inputStream = null;
			try {
				// 移动到最开始，certPath io异常unlock会报错
				lock.lock();

				KeyStore clientStore = KeyStore.getInstance("PKCS12");
				inputStream = new FileInputStream(certPath);
				clientStore.load(inputStream, certPass.toCharArray());

				KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				kmf.init(clientStore, certPass.toCharArray());
				KeyManager[] kms = kmf.getKeyManagers();
				SSLContext sslContext = SSLContext.getInstance("TLSv1");

				sslContext.init(kms, null, new SecureRandom());

				httpsClient.setSslSocketFactory(sslContext.getSocketFactory());

				com.squareup.okhttp.Response response = httpsClient.newCall(request).execute();

				if (!response.isSuccessful())
					throw new RuntimeException("Unexpected code " + response);

				return response.body().string();
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.closeQuietly(inputStream);
				lock.unlock();
			}
		}

		@Override
		public MediaFile download(String url) {
			com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(url).get().build();
			try {
				com.squareup.okhttp.Response response = httpsClient.newCall(request).execute();

				if (!response.isSuccessful())
					throw new RuntimeException("Unexpected code " + response);

				com.squareup.okhttp.ResponseBody body = response.body();
				com.squareup.okhttp.MediaType mediaType = body.contentType();
				MediaFile mediaFile = new MediaFile();
				if (mediaType.type().equals("text")) {
					mediaFile.setError(body.string());
				} else {
					BufferedInputStream bis = new BufferedInputStream(body.byteStream());

					String ds = response.header("Content-disposition");
					String fullName = ds.substring(ds.indexOf("filename=\"") + 10, ds.length() - 1);
					String relName = fullName.substring(0, fullName.lastIndexOf("."));
					String suffix = fullName.substring(relName.length() + 1);

					mediaFile.setFullName(fullName);
					mediaFile.setFileName(relName);
					mediaFile.setSuffix(suffix);
					mediaFile.setContentLength(body.contentLength() + "");
					mediaFile.setContentType(body.contentType().toString());
					mediaFile.setFileStream(bis);
				}
				return mediaFile;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public InputStream download(String url, String params) {
			com.squareup.okhttp.Request request;
			if (StringUtils.isNotBlank(params)) {
				com.squareup.okhttp.RequestBody body = com.squareup.okhttp.RequestBody.create(CONTENT_TYPE_FORM,
						params);
				request = new com.squareup.okhttp.Request.Builder().url(url).post(body).build();
			} else {
				request = new com.squareup.okhttp.Request.Builder().url(url).get().build();
			}
			try {
				com.squareup.okhttp.Response response = httpsClient.newCall(request).execute();

				if (!response.isSuccessful())
					throw new RuntimeException("Unexpected code " + response);

				return response.body().byteStream();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		@Override
		public String upload(String url, File file, String params) {
			com.squareup.okhttp.RequestBody fileBody = com.squareup.okhttp.RequestBody
					.create(com.squareup.okhttp.MediaType.parse("application/octet-stream"), file);

			com.squareup.okhttp.MultipartBuilder builder = new com.squareup.okhttp.MultipartBuilder()
					.type(com.squareup.okhttp.MultipartBuilder.FORM).addFormDataPart("media", file.getName(), fileBody);

			if (StringUtils.isNotBlank(params)) {
				builder.addFormDataPart("description", params);
			}

			com.squareup.okhttp.RequestBody requestBody = builder.build();
			com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(url).post(requestBody)
					.build();

			return exec(request);
		}

	}

	/**
	 * HttpKit代理
	 */
	private static class HttpKitDelegate implements HttpDelegate {

		@Override
		public String get(String url) {
			return HttpKit.get(url);
		}

		@Override
		public String get(String url, Map<String, String> queryParas) {
			return HttpKit.get(url, queryParas);
		}

		@Override
		public String post(String url, String data) {
			return HttpKit.post(url, data);
		}

		@Override
		public String postSSL(String url, String data, String certPath, String certPass) {
			return HttpKitExt.postSSL(url, data, certPath, certPass);
		}

		@Override
		public MediaFile download(String url) {
			try {
				return HttpKitExt.download(url);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public InputStream download(String url, String params) {
			try {
				return HttpKitExt.downloadMaterial(url, params);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String upload(String url, File file, String params) {
			try {
				return HttpKitExt.uploadMedia(url, file, params);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}
}

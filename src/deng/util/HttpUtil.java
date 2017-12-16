package deng.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;

/**
 * 通讯工具类
 * 
 * @author Deng
 *
 */
public class HttpUtil {

	private static String sessionID = "";
	private static String charset = "utf-8";
	private Integer connectTimeout = 10000;
	private Integer socketTimeout = 10000;

	public String doPost(String urlString, Map<String, String> parameterMap)
			throws Exception {

		/* Translate parameter map to parameter date string */
		StringBuffer parameterBuffer = new StringBuffer();
		if (parameterMap != null) {
			Iterator<String> iterator = parameterMap.keySet().iterator();
			String key = null;
			String value = null;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				if (parameterMap.get(key) != null) {
					value = (String) parameterMap.get(key);
				} else {
					value = "";
				}
				parameterBuffer.append(key).append("=").append(value);
				if (iterator.hasNext()) {
					parameterBuffer.append("&");
				}
			}
		}

		// System.out.println("POST parameter : " + parameterBuffer.toString());

		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty("Accept-Charset", charset);
		httpURLConnection.setRequestProperty("Content-Length", parameterBuffer
				.toString().getBytes().length + "");
		httpURLConnection.setConnectTimeout(connectTimeout);
		httpURLConnection.setReadTimeout(socketTimeout);
		if (!sessionID.equals(""))
			httpURLConnection.setRequestProperty("Cookie", sessionID);

		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		InputStream inputStream = null;
		String result = "";

		try {
			outputStream = httpURLConnection.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream);

			outputStreamWriter.write(parameterBuffer.toString());
			outputStreamWriter.flush();

			if (httpURLConnection.getResponseCode() >= 300) {
				throw new Exception(
						"HTTP Request is not success, Response code is "
								+ httpURLConnection.getResponseCode());
			}

			inputStream = httpURLConnection.getInputStream();
			String setCookie = httpURLConnection.getHeaderField("Set-Cookie");
			if (setCookie != null) {
				sessionID = setCookie;
			}
			result = changeInputStream(inputStream);

		} catch (Exception e) {
			System.err.println("Error->" + e.toString());
			throw e;
		} finally {
			if (outputStreamWriter != null) {
				outputStreamWriter.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}

		// System.out.println("POST result : \n" + result);
		return result;
	}

	public String doGet(String urlString, Map<String, String> parameterMap)
			throws Exception {

		StringBuffer parameterBuffer = new StringBuffer();
		if (parameterMap != null) {
			Iterator<String> iterator = parameterMap.keySet().iterator();
			String key = null;
			String value = null;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				if (parameterMap.get(key) != null) {
					value = (String) parameterMap.get(key);
				} else {
					value = "";
				}
				parameterBuffer.append(key).append("=").append(value);
				if (iterator.hasNext()) {
					parameterBuffer.append("&");
				}
			}
			urlString = urlString + "?" + parameterBuffer;
		}
		// System.out.println("GET parameter : " + parameterBuffer.toString());

		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

		httpURLConnection.setRequestProperty("Accept-Charset", charset);
		httpURLConnection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		httpURLConnection.setConnectTimeout(connectTimeout);
		httpURLConnection.setReadTimeout(socketTimeout);
		if (!sessionID.equals(""))
			httpURLConnection.setRequestProperty("Cookie", sessionID);

		InputStream inputStream = null;
		String result = "";

		if (httpURLConnection.getResponseCode() >= 300) {
			throw new Exception(
					"HTTP Request is not success, Response code is "
							+ httpURLConnection.getResponseCode());
		}

		try {
			inputStream = httpURLConnection.getInputStream();
			result = changeInputStream(inputStream);

		} catch (Exception e) {
			System.err.println("Error->" + e.toString());
			throw e;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		// System.out.println("GET result : \n" + result);
		return result;
	}

	/**
	 * 把输入流转换成字符串
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String changeInputStream(InputStream in) throws Exception {
		// TODO 自动生成的方法存根
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = "";
		if (in != null) {
			try {
				while ((len = in.read(data)) != -1) {
					baos.write(data, 0, len);
				}
				result = new String(baos.toByteArray(), charset);
			} finally {
				baos.close();
			}
		}
		return result;
	}

	public static String getSessionID() {
		return sessionID;
	}

	public static void setSessionID(String sessionID) {
		HttpUtil.sessionID = sessionID;
	}

	public static String getCharset() {
		return charset;
	}

	public static void setCharset(String charset) {
		HttpUtil.charset = charset;
	}

	public Integer getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public Integer getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(Integer socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
}

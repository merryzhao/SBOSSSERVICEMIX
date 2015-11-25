package com.ai.sboss.serviceScript.urlRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("httpRequest")
public class HttpRequest implements IUrlRequest {

	private static final Logger LOGGER = Logger.getLogger(HttpRequest.class);

	@Override
	public String sendGet(String url, String param) {
		StringBuilder result = new StringBuilder();
		BufferedReader in = null;
		HttpURLConnection connection = null;
		try {
			final String urlNameString = url + "?" + param;
			final URL realUrl = new URL(urlNameString);
			connection = (HttpURLConnection) realUrl.openConnection();
			if (null != connection) {
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestMethod("GET");

				connection.setRequestProperty("accept", "*/*");
				connection.setRequestProperty("connection", "Keep-Alive");
				connection
						.setRequestProperty("user-agent",
								"Mozilla/5.0 (Windows NT 10.0; Win64; x64; Trident/7.0; rv:11.0) like Gecko");
				connection.setRequestProperty("X-Requested-With",
						"XMLHttpRequest");
				connection.setRequestProperty("Authorization",
						"Basic MDF1cGM6MTIzNDU2");
				connection
						.setRequestProperty("Cookie",
								"base.login.name=01upc; JSESSIONID=2371A115AC64661C55BBF1CE8648319E");

				// Setup connect
				connection.connect();

				// Extract response
				in = new BufferedReader(new InputStreamReader(
						connection.getInputStream(), "UTF-8"));
				if (null != in) {
					String line = StringUtils.EMPTY;
					while ((line = in.readLine()) != null) {
						result.append(line);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (null != connection) {
					connection.disconnect();
				}
			} catch (IOException e2) {
				LOGGER.error(e2);
			}
		}
		return result.toString();
	}

	@Override
	public String sendPost(String url, String param) {
		System.out.print("Input URL : " + url);
		PrintWriter out = null;
		BufferedReader in = null;
		HttpURLConnection conn = null;
		StringBuilder result = new StringBuilder();
		try {
			final URL realUrl = new URL(url);
			conn = (HttpURLConnection) realUrl.openConnection();
			if (null != conn) {
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");
				conn.setUseCaches(false);
				conn.setRequestProperty("Content-Type",
						"application/json;charset=utf-8");

				out = new PrintWriter(new OutputStreamWriter(
						conn.getOutputStream(), "utf-8"), true);
				out.print(param);
				out.flush();

				in = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "utf-8"));
				if (null != in) {
					String line;
					while ((line = in.readLine()) != null) {
						result.append(line);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			try {
				if (null != out) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (null != conn) {
					conn.disconnect();
				}
			} catch (IOException ex) {
				LOGGER.error(ex);
			}
		}
		return result.toString();
	}
}

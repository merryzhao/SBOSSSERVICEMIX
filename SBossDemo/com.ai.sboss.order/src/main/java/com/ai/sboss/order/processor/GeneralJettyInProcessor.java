package com.ai.sboss.order.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 该processor用于处理前端Get/Post方法的参数信息，以便route的下一步能够正常调用
 * 
 * @author chaos
 */
public class GeneralJettyInProcessor implements IBasicInProcessor {
	private Logger logger = Logger.getLogger(GeneralJettyInProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject properIn = getInputParam(exchange);
		exchange.getIn().setBody(properIn.toString());
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
	}

	// 通用Jetty输入参数解析函数，负责将Get/Post方法传入的参数放入Message的Body中
	@Override
	public JSONObject getInputParam(Exchange exchange) {
		Message message = exchange.getIn();
		String params = "";
		// 如果条件成立，说明是客户端使用的是POST方式的HTTP请求，应该从body中取出参数
		if ("POST".equals(message.getHeader(Exchange.HTTP_METHOD))) {
			params = message.getBody(String.class);
		}
		// 如果条件成立，说明客户端使用的GET方式的HTTP请求，应该从head取出参数
		else if ("GET".equals(message.getHeader(Exchange.HTTP_METHOD))) {
			params = message.getHeader(Exchange.HTTP_QUERY, String.class);
		}
		// 其他情况抛出异常
		else {
			throw new IllegalArgumentException("没有发现兼容的HTTP请求类型");
		}

		// 判断参数是否合法
		JSONObject inputJsonObject = null;
		try {
			inputJsonObject = JSONObject.fromObject(params);
		} catch (JSONException e) {
			throw new IllegalArgumentException("Json格式不合法" + e.toString());
		}
		if (!inputJsonObject.containsKey("data") || !inputJsonObject.containsKey("desc")) {
			throw new IllegalArgumentException("输入Json格式不规范");
		}
		String[] headerParams = message.getHeader("HttpQuery").toString().split("&");
		if (headerParams.length < 4) {
			throw new IllegalArgumentException("输入格式不规范:时间戳&用户令牌 &用户ID&客户端服务端约定的加密字符串");
		}
		JSONObject ret = inputJsonObject.getJSONObject("data");
		Long partyId = Long.parseLong(headerParams[2]);
		ret.put("userId", partyId);
		return ret;
	}
}

package com.ai.sboss.serviceScript.processor;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

@Component("generalJettyInProcessor")
public class GeneralJettyInProcessor implements IBasicInProcessor {
	
	/**
	 * Declare object of Logger to output needed logs.
	 */
	private static final Logger LOGGER = Logger.getLogger(GeneralJettyInProcessor.class);

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject properIn = getInputParam(exchange);
		
		LOGGER.info("Imported parameter is : " + properIn.toString());
		
		exchange.getIn().setBody(properIn.toString());
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.common.interfaces.IBasicInProcessor#getInputParam(org.apache.camel.Exchange)
	 * 通用Jetty输入参数解析函数，负责将Get/Post方法传入的参数放入Message的Body中
	 */
	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		String params = StringUtils.EMPTY;
		
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

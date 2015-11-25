package com.ai.sboss.serviceScript.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

@Component("getProcessScriptInProcessor")
public class GetProcessScriptInProcessor implements IBasicInProcessor {

	private static final Logger LOGGER = Logger
			.getLogger(GetProcessScriptInProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject inputParam = getInputParam(exchange);

		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, inputParam.toString());
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		final String bodyString = exchange.getIn().getBody(String.class);
		LOGGER.info(bodyString);
		System.out.println("Input data : " + bodyString);
		
		JSONObject inputJsonObject = JSONObject.fromObject(bodyString);

		return inputJsonObject;
	}

}

package com.ai.sboss.customer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryTimeLineFromJointsInProcessor implements IBasicInProcessor {
	final private static String REQUESTFMT="{data:<DATA>,\"desc\":{\"data_mode\":\"0\",\"digest\":\"\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		String httpquery = (String) exchange.getIn().getHeader("HttpQuery");
		JSONObject retObject = getInputParam(exchange);
		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
		exchange.getIn().setHeader("Content-Type", "application/json");
		exchange.getIn().setHeader("HttpQuery", httpquery);
		exchange.getIn().setBody(REQUESTFMT.replace("<DATA>", retObject.toString()));
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		JSONObject bodyObject = JSONObject.fromObject(in.getBody(String.class));

		Long tradeId = 0L;
		if (bodyObject.containsKey("service_id")) { 
			tradeId = bodyObject.getLong("service_id");
		}
		JSONObject retObject = new JSONObject();
		retObject.put("tradeid", tradeId.toString());
		return retObject;
	}

}

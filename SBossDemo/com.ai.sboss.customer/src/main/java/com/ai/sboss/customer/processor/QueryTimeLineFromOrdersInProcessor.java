package com.ai.sboss.customer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryTimeLineFromOrdersInProcessor implements IBasicInProcessor {
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
		JSONObject retObject = new JSONObject();
		retObject.put("order_state", 0);
		//TODO:先只查买家order列表
		retObject.put("order_role", 1);
		return retObject;
	}

}

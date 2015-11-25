package com.ai.sboss.customer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryTimeLineFromProcessInProcessor implements IBasicInProcessor {
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
		if (!bodyObject.containsKey("customer_id")) {
			throw new IllegalArgumentException("必须传入customer_id");
		}
		String userId = bodyObject.getString("customer_id");
		if (bodyObject.containsKey("order_number")) { //这时候不调用查询实例
			userId = "";
		}
		JSONObject retObject = new JSONObject();
		retObject.put("userid", userId);
		return retObject;
	}

}

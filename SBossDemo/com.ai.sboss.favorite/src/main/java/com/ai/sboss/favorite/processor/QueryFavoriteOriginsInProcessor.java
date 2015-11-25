package com.ai.sboss.favorite.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryFavoriteOriginsInProcessor implements IBasicInProcessor {
	protected Logger logger = Logger.getLogger(this.getClass());
	private static final String HUB_FORMAT_QUERY = "servicecode=queryFavoriteOrigins&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";
	@Override
	public void process(Exchange exchange) throws Exception {
		//JSONObject properInput = getInputParam(exchange);
		JSONObject properInput = JSONObject.fromObject("{\"customerId\": 3}");
		String query = HUB_FORMAT_QUERY.replace("<PARAM_JSON>", properInput.toString());
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) {
		JSONObject ret = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		/*JSONObject returnParam = new JSONObject();
		returnParam.put("customerId", ret.getString("customer_id"));
		return returnParam;*/
		return ret;
	}
}

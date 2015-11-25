package com.ai.sboss.favorite.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;


public class QueryServicesByContentInProcessor implements IBasicInProcessor {

	Logger logger = Logger.getLogger(QueryServicesByContentInProcessor.class);
	//private static final String HUB_FORMAT_QUERY = "servicecode=queryServicesByContent&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";
	@Override
	public void process(Exchange exchange) throws Exception {		
		JSONObject properInput = getInputParam(exchange);
		exchange.getIn().setBody(properInput.toString());
	}
	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {		
		JSONObject ret = new JSONObject();
		JSONObject input = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		ret.put("favorite_id", input.getLong("favorite_id"));
		return ret;
	}
}

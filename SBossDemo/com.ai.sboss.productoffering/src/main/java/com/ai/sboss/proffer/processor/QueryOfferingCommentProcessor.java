package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryOfferingCommentProcessor implements IBasicInProcessor {

	private static final String HUB_FORMAT_QUERY = "servicecode=queryCommentsForServiceDetail&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";
	protected Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject properInput = getInputParam(exchange);
		String query = HUB_FORMAT_QUERY.replace("<PARAM_JSON>", properInput.toString());
		logger.info("query->" + query);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		JSONObject ret = new JSONObject();
		JSONObject input = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		logger.info("传入参数为："+input.toString());
		//Long objectId = input.getJSONObject("data").getLong("service_id");
		Long objectId = input.getLong("service_id");
		String objectType = "Offering";
		logger.info("objectId====>" + objectId);
		ret.put("objectId", objectId);
		ret.put("objectType", objectType);
		return ret;
	}

}

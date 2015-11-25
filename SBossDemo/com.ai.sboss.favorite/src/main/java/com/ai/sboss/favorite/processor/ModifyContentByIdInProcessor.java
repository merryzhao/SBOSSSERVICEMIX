package com.ai.sboss.favorite.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class ModifyContentByIdInProcessor implements IBasicInProcessor {
	private Logger logger = Logger.getLogger(ModifyContentByIdInProcessor.class);
	private static final String HUB_FORMAT_QUERY = "servicecode=modifyContentById&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject request = getInputParam(exchange);
		String query = HUB_FORMAT_QUERY.replace("<PARAM_JSON>", request.toString());
		logger.info("ModifyContentByIdInProcessor********query paramenter===>"+query);
		query = query.replaceAll("\\[", "%5B").replaceAll("\\]", "%5D");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		JSONObject inputparam = JSONObject.fromObject(exchange.getIn().getBody(String.class));

		JSONObject contentJson = new JSONObject();
		contentJson.put("contentId", inputparam.getLong("contentId"));
		contentJson.put("title", inputparam.getString("title"));
		contentJson.put("description", inputparam.getString("description"));
		contentJson.put("url", inputparam.getString("imageUrl"));

		return contentJson;
	}

}

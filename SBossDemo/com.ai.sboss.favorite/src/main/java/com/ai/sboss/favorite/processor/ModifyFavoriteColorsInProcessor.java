package com.ai.sboss.favorite.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class ModifyFavoriteColorsInProcessor implements IBasicInProcessor {
	protected Logger logger = Logger.getLogger(this.getClass());
	private static final String HUB_FORMAT_QUERY = "servicecode=modifyFavoriteColors&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject properInput = getInputParam(exchange);
		//JSONObject properInput = JSONObject.fromObject("{\"customerId\": 3}");
		String query = HUB_FORMAT_QUERY.replace("<PARAM_JSON>", properInput.toString());
		query = query.replaceAll("\\[", "%5B").replaceAll("\\]", "%5D");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		JSONObject param = JSONObject.fromObject(in.getBody(String.class));
		JSONObject ret = new JSONObject();
		ret.put("favoriteEntryId", param.getLong("favorite_id"));
		ret.put("favoriteColors", param.getJSONArray("content_color_flags"));
		return ret;
	}

}

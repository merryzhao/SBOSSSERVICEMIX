package com.ai.sboss.favorite.processor;

import java.net.URLEncoder;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class GenerateContentIdInProcessor implements IBasicInProcessor {
	private static final String HUB_FORMAT_QUERY = "servicecode=addContentToFavorite&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject request = getInputParam(exchange);
		String query = HUB_FORMAT_QUERY.replace("<PARAM_JSON>", request.toString());
		query = query.replaceAll("\\[", "%5B").replaceAll("\\]", "%5D");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		JSONObject ret = new JSONObject();
		JSONObject inputparam = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		/* Fake CustomerId = 2020 */

		Long customerId = new Long(2020);
		JSONObject contentJson = new JSONObject();
		contentJson.put("contentType", inputparam.getLong("content_type"));
		contentJson.put("url", URLEncoder.encode(inputparam.getString("content_url"), "UTF-8"));
		contentJson.put("origin", inputparam.getString("content_origin"));
		contentJson.put("wxOpenid", inputparam.getString("wx_openid"));
		ret.put("content", contentJson);
		ret.put("customerId", customerId);
		return ret;
	}

}

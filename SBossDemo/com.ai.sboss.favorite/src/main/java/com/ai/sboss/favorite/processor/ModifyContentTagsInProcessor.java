package com.ai.sboss.favorite.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class ModifyContentTagsInProcessor implements IBasicInProcessor {
	protected Logger logger = Logger.getLogger(this.getClass());
	private static final String HUB_FORMAT_QUERY = "servicecode=modifyFavoriteTags&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject properInput = getInputParam(exchange);
		//JSONObject properInput = JSONObject.fromObject("{\"customerId\": 3}");
		String query = HUB_FORMAT_QUERY.replace("<PARAM_JSON>", properInput.toString());
		query = query.replaceAll("\\[", "%5B").replaceAll("\\]", "%5D");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) {
		JSONObject ret = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		JSONObject returnJson = new JSONObject();		
		returnJson.put("favoriteEntryId", ret.getLong("favorite_id"));
		JSONArray contentList = ret.getJSONArray("content_tags");
		List<String> tagList = new ArrayList<>();
		for (int i = 0; i < contentList.size(); ++i) {
			tagList.add(contentList.getJSONObject(i).getString("tag_name"));
		}
		returnJson.put("tagNames", tagList.toString());
		return returnJson;
		
	}
}

package com.ai.sboss.favorite.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryCollectedContentsOutProcessor implements IBasicOutProcessor {
	Logger logger = Logger.getLogger(QueryCollectedServicesOutProcessor.class);
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String ret = convert2requst(in.getBody(String.class));
		in.setBody(ret);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject crmret = JSONObject.fromObject(data);
		String code = crmret.getString("hub_code");
		String retdata = "";
		String msg = "failed";
		if (code.equals("1")) {
			retdata = paramjson(crmret.getJSONArray("data"));
			msg = "success";
		} else {
			retdata = "{}";
		}
		
		return RETFMT.replace("<DATA>", retdata).replace("<CODE>", code).replace("<MSG>", msg);
	}

	private String paramjson(JSONArray jsonArray) {
		JSONObject contentItem = null;
		JSONObject retContentItem = null;
		JSONArray retlist = new JSONArray();
		for (int i = 0; i < jsonArray.size(); ++i) {
			contentItem = jsonArray.getJSONObject(i);
			JSONObject currentContent = contentItem.getJSONArray("content").getJSONObject(0);
			retContentItem = new JSONObject();
			retContentItem.put("content_type", currentContent.getInt("contentType"));
			retContentItem.put("favorite_id", contentItem.getLong("favoriteEntryId"));
			retContentItem.put("content_url", currentContent.getString("url"));
			retContentItem.put("content_thumbnail_url", currentContent.getString("imageUrl"));
			retContentItem.put("content_title", currentContent.getString("title"));
			retContentItem.put("content_intro", currentContent.getString("intro"));
			retContentItem.put("content_origin", currentContent.getString("origin"));
			retContentItem.put("content_tags", contentItem.getJSONArray("favoriteEntryTag"));
			retContentItem.put("content_favorite_flag", contentItem.getLong("favoriteFlag"));
			retContentItem.put("content_color_flags", contentItem.getJSONArray("favoriteEntryColor"));
			retlist.add(retContentItem);
		}
		JSONObject ret = new JSONObject();
		ret.put("collected_contents", retlist);
		return ret.toString();
	}

}

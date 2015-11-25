package com.ai.sboss.favorite.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class AddContentToFavoriteOutProcessor implements IBasicOutProcessor {
	Logger logger = Logger.getLogger(AddContentToFavoriteOutProcessor.class);
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String ret = convert2requst(in.getBody(String.class));
		in.setBody(ret);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject inputarg = JSONObject.fromObject(data);
		JSONObject ret = new JSONObject();
		String retstr = "";
		//logger.info("chaos out==>"+inputarg.toString());
		if (inputarg.getString("resultCode").equals("1")) { // update
															// content
			// sucessed
			/* logger.info("chaos==>"+inputarg.toString()); */
			ret.put("favorite_id", inputarg.getLong("favorite_id"));
			ret.put("content_id", inputarg.getString("contentId"));
			ret.put("content_thumbnail_url", inputarg.getString("imageUrl"));
			ret.put("content_title", inputarg.getString("title"));
			ret.put("content_intro", inputarg.getString("description"));
			// ret.put("content_thumbnail_url", value);
			ret.put("content_tags", "[]");
			if (inputarg.getJSONObject("data").containsKey(("content_services"))) {
				ret.put("service_tags", getServiceTags(inputarg.getJSONObject("data").getJSONArray("content_services")));
			}
			ret.put("content_services", inputarg.getJSONObject("data").getJSONArray("content_services"));
			retstr = RETFMT.replace("<DATA>", ret.toString()).replace("<CODE>", "1")
					.replace("<MSG>", inputarg.getString("result_msg"));
		} else {
			retstr = RETFMT.replace("<DATA>", "{}")
					.replace("<CODE>", inputarg.getString("resultCode"))
					.replace("<MSG>", inputarg.getString("result_msg"));
		}
		return retstr;
	}

	private String getServiceTags(JSONArray jsonArray) {
		JSONArray ret = new JSONArray();
		for (int i = 0; i < jsonArray.size(); ++i) {
			JSONObject currentservice = jsonArray.getJSONObject(i);
			for (int j = 0; j < currentservice.getJSONArray("service_tag").size(); ++j) {
				if (!ret.contains(currentservice.getJSONArray("service_tag").getJSONObject(j))) {
					ret.add(currentservice.getJSONArray("service_tag").getJSONObject(j));
				}
			}
		}
		return ret.toString();
	}

}

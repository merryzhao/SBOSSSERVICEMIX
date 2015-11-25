package com.ai.sboss.favorite.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

//convert function not finished

public class ContentTagsConvertOutProcessor implements IBasicOutProcessor {
	protected Logger logger = Logger.getLogger(this.getClass());
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {

		logger.info("query contenttags res---->" + exchange.getIn().getBody(String.class));
		String data = exchange.getIn().getBody(String.class);
		String returnJson = convert2requst(data);

		exchange.getIn().setBody(returnJson);
	}

	@Override
	public String convert2requst(String data) {
		JSONObject ret = JSONObject.fromObject(data);
		JSONArray originsList = JSONArray.fromObject(ret.getString("data"));
		JSONArray originsListForFront = new JSONArray();

		for (int originIdx = 0; originIdx < originsList.size(); originIdx++) {
			JSONObject obj = new JSONObject();
			logger.info("query list res---->" + originsList.get(originIdx).toString());
			obj.put("tag_name", originsList.get(originIdx).toString());
			originsListForFront.add(obj);

		}

		// JSONArray list = JSONArray.fromObject(originsListForFront);
		JSONObject returnJson = new JSONObject();
		returnJson.put("content_tags", originsListForFront.toString());

		return RETFMT.replace("<DATA>", returnJson.toString()).replace("<CODE>", ret.getString("hub_code"))
				.replace("<MSG>", ret.getString("value"));
	}

}

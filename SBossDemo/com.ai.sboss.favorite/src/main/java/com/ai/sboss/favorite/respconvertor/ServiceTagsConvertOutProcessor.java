package com.ai.sboss.favorite.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class ServiceTagsConvertOutProcessor implements IBasicOutProcessor {
	protected Logger logger = Logger.getLogger(this.getClass());
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":1,\"result_msg\":\"sucess\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		String body = inMessage.getBody(String.class);
		String ret = convert2requst(body);
		inMessage.setBody(ret);
	}

	@Override
	public String convert2requst(String data) {
		logger.info("data---->" + data);
		JSONObject ret = JSONObject.fromObject(data);
		JSONArray originsList = JSONArray.fromObject(ret.getString("data"));
		JSONArray originsListForFront = new JSONArray();		
		for(int originIdx = 0; originIdx < originsList.size(); originIdx ++){
			JSONObject obj = new JSONObject();
			String tag_id = null;
			String tag_name = null;
			logger.info("query service tag list res---->"+originsList.get(originIdx).toString());
			tag_id = originsList.getJSONObject(originIdx).get("favoriteEntryTagId").toString();
			tag_name = originsList.getJSONObject(originIdx).get("tagName").toString();
			logger.info("tag_id ----->" + tag_id + " and tag_name ----->" + tag_name);
			obj.put("tag_id", tag_id);
			obj.put("tag_name", tag_name);
			originsListForFront.add(obj);
		}		
		JSONObject returnJson = new JSONObject();
		returnJson.put("service_tags", originsListForFront.toString());	
		return RETFMT.replace("<DATA>", returnJson.toString());
	}
}

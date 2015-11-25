package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.ai.sboss.common.respconvertor.CrmRespConvertor;

public class OfferingInfoRegularConvertor extends CrmRespConvertor {
	private Logger logger = Logger.getLogger(OfferingInfoRegularConvertor.class); 

	@Override
	public String convert2Resp(String json) {
		json = "{\"data\":["+json+"]}";
		logger.info("json string------>"+json);
		
		JSONArray array = JSONObject.fromObject(json).getJSONArray("data");
		JSONObject offering = array.getJSONObject(0).getJSONObject("data");
		JSONArray comments = array.getJSONObject(1).getJSONArray("comments");
		JSONObject provider = array.getJSONObject(2).getJSONObject("provider");
		/*
		JSONObject data = JSONObject.fromObject(json).getJSONObject("data");
		JSONObject offering = data.getJSONObject("data");
		JSONArray comments = data.getJSONArray("comments");
		JSONObject provider = data.getJSONObject("provider");
		*/
		offering.put("comments", comments.toString());
		offering.put("provider", provider.toString());
		JSONObject returnJson = new JSONObject();
		returnJson.put("offering", offering.toString());

		return returnJson.toString();
	}
}

package com.ai.sboss.favorite.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class SplitProductOfferingIdListProcessor implements IBasicOutProcessor {
	Logger logger = Logger.getLogger(SplitProductOfferingIdListProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("***********SplitProductOfferingIdListProcessor************");
		Message in = exchange.getIn();
		JSONObject body = JSONObject.fromObject(in.getBody(String.class));
		if (!body.containsKey("data")) {
			throw new Exception("CRM server call failed");
		}
		String ret = convert2requst(body.getString("data"));
		in.setBody(ret);
		
	}

	@Override
	public String convert2requst(String data) {
		JSONArray favoriteList = JSONArray.fromObject(data);
		JSONArray serviceList = new JSONArray();
		for (int i = 0; i < favoriteList.size(); ++i) {
			JSONObject serviceItem = new JSONObject();
			serviceItem.put("service_id", favoriteList.getJSONObject(i).getLong("entryItemId"));
			serviceItem.put("favorite_id", favoriteList.getJSONObject(i).getLong("favoriteId"));
			JSONArray tagArray = new JSONArray();
			JSONObject tag = new JSONObject();
			tag.put("tag_name", favoriteList.getJSONObject(i).getJSONArray("favoriteEntryTag").getJSONObject(0).getString("tagName"));
			tagArray.add(tag);
			serviceList.add(serviceItem);
		}
		StringBuffer strServiceList = new StringBuffer();
		for (int i = 0; i < serviceList.size(); ++i) {
			strServiceList.append(serviceList.getJSONObject(i).toString()+";");
		}
		strServiceList.deleteCharAt(strServiceList.length()-1);
		return strServiceList.toString();
	}

}

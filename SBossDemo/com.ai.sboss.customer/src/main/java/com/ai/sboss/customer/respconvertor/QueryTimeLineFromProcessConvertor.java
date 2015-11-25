package com.ai.sboss.customer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryTimeLineFromProcessConvertor implements IBasicOutProcessor {
	private Logger logger = Logger.getLogger(QueryTimeLineFromProcessConvertor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn().getBody(String.class));
		exchange.getIn().setHeaders(null);
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject bodyObject = JSONObject.fromObject(data);
		if (!StringUtils.equals(bodyObject.getJSONObject("desc").getString("result_code"), "_200")) {
			logger.error(bodyObject.getJSONObject("desc").getString("result_msg"));
			return new JSONArray().toString();
		}
		JSONArray timeList = new JSONArray();
		JSONArray retList = null;
		try {
			retList = bodyObject.getJSONObject("data").getJSONArray("results");
		} catch (JSONException e) {
			logger.info(e.toString());
			return new JSONArray().toString();
		}
		logger.info("Got list===" + retList.toString());
		for (int i = 0; i < retList.size(); ++i) {
			JSONObject timeObject = new JSONObject();
			timeObject.put("current_time_stamp", retList.getJSONObject(i).getLong("currentTimeStamp"));
			timeObject.put("title", retList.getJSONObject(i).getString("offsetTitle"));
			timeObject.put("desc", retList.getJSONObject(i).getString("promptOffsettime"));
			// TODO: 硬编码
			timeObject.put("expand_type_id", 0L);
			timeObject.put("role", 1L);
			timeList.add(timeObject);
		}
		return timeList.toString();
	}

}

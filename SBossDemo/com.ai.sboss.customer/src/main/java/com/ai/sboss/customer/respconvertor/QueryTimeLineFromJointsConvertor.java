package com.ai.sboss.customer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryTimeLineFromJointsConvertor implements IBasicOutProcessor {
	private Logger logger = Logger.getLogger(QueryTimeLineFromJointsConvertor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn().getBody(String.class));
		exchange.getIn().setHeaders(null);
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject bodyObject = JSONObject.fromObject(data);
		JSONObject retObject = new JSONObject();
		if (!StringUtils.equals(bodyObject.getJSONObject("desc").getString("result_code"), "_200")) {
			logger.error(bodyObject.getJSONObject("desc").getString("result_msg"));
			retObject.put("order_task_list", new JSONArray().toString());
			logger.info("准备返回数据==》"+retObject.toString());
			return retObject.toString();
		}
		JSONArray timeList = new JSONArray();
		JSONArray retList = null;
		try {
			retList = bodyObject.getJSONArray("data");
		} catch (JSONException e) {
			logger.info(e.toString());
			logger.warn(e.toString()+"-->"+bodyObject.toString());
			retObject.put("order_task_list", new JSONArray().toString());
			logger.info("准备返回数据==》"+retObject.toString());
			return retObject.toString();
		}
		logger.info("Got list===" + retList.toString());
		for (int i = 0; i < retList.size(); ++i) {
			JSONObject timeObject = new JSONObject();
			timeObject.put("current_time_stamp", retList.getJSONObject(i).getLong("absOffsettime"));
			timeObject.put("title", retList.getJSONObject(i).getString("offsetTitle"));
			timeObject.put("desc", retList.getJSONObject(i).getString("promptOffsettime"));
			// TODO: 硬编码
			timeObject.put("expand_type_id", 0L);
			timeObject.put("role", 1L);
			timeList.add(timeObject);
		}
		
		retObject.put("order_task_list", timeList.toString());
		logger.info("准备返回数据==》"+retObject.toString());
		return retObject.toString();

		// return data;
	}

}

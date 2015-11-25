package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class GetServiceTopicConvertor implements IBasicOutProcessor {
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(GetServiceTopicConvertor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn().getBody(String.class));
		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader("Content-Type", "application/json");
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		if (StringUtils.isEmpty(data)) {
			logger.error("GetServiceTopicConvertor===got null data");
			return RETFMT.replace("<DATA>", "\"\"").replace("<CODE>", "0").replace("<MSG>", "failed");
		}
		JSONArray resultsList = JSONArray.fromObject("["+data+"]");
		JSONObject retObject = new JSONObject();
		int totalNum = 0;
		for (int i = 0; i < resultsList.size(); ++i) {
			JSONObject items = resultsList.getJSONObject(i);
			if (items.getJSONArray("result_items").size() <= 0) {
				resultsList.remove(i);
				continue;
			}
			totalNum += items.getJSONArray("result_items").size();
		}
		retObject.put("results_number", totalNum);
		retObject.put("results", resultsList.toString());
		return RETFMT.replace("<DATA>", retObject.toString()).replace("<CODE>", "1").replace("<MSG>", "success");
	}

}

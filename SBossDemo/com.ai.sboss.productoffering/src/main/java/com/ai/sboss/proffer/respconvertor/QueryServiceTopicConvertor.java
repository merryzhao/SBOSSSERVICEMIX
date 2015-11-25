package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryServiceTopicConvertor implements IBasicOutProcessor {
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryServiceTopicConvertor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		String data = exchange.getIn().getBody(String.class);
		String returnJson = convert2requst(data);
		exchange.getIn().setBody(returnJson);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject crmret = JSONObject.fromObject(data);
		
		return RETFMT.replace("<DATA>", crmret.getJSONObject("data").toString()).replace("<CODE>", crmret.getString("hub_code"))
				.replace("<MSG>", crmret.getString("value"));
	}

}

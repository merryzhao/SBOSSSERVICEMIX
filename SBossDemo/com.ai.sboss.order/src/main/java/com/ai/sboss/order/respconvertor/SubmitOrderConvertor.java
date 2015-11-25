package com.ai.sboss.order.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

@Component("submitOrderConvertor")
public class SubmitOrderConvertor implements IBasicOutProcessor {
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(SubmitOrderConvertor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		String data = exchange.getIn().getBody(String.class);
		String returnJson = convert2requst(data);
		logger.info("Got returned info:"+returnJson);
		exchange.getIn().setHeader("Content-Type", "application/json");
		exchange.getIn().setBody(returnJson);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject crmret = JSONObject.fromObject(data);
		if (crmret.getString("hub_code").equals("0")) {
			return RETFMT.replace("<DATA>", "{}").replace("<CODE>", crmret.getString("hub_code"))
					.replace("<MSG>", crmret.getString("value"));
		}
		JSONObject retJsonObject = new JSONObject();
		retJsonObject.put("order_id", Long.toString(crmret.getLong("data")));
		retJsonObject.put("result_code", crmret.getString("hub_code"));
		return RETFMT.replace("<DATA>", retJsonObject.toString()).replace("<CODE>", crmret.getString("hub_code"))
				.replace("<MSG>", crmret.getString("value"));
	}
}

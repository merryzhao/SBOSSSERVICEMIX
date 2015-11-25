package com.ai.sboss.customer.respconvertor;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryCustomerConvertor implements IBasicOutProcessor {
	private Logger logger = Logger.getLogger(QueryCustomerConvertor.class);
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		String body = inMessage.getBody(String.class);
		String retString = convert2requst(body);
		Message retMessage = exchange.getOut();
		retMessage.setBody(retString);
		exchange.setIn(retMessage);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject retJsonObject = new JSONObject();

		try {
			logger.info("QueryCustomerConvertor:" + data);
			JSONObject crmretJson = JSONObject.fromObject(data);

			if (crmretJson.getString("hub_code").equals("1")) {
				JSONObject customerInfo = crmretJson.getJSONObject("data");
				retJsonObject.put("user_name", customerInfo.getString("name"));
				retJsonObject.put("email", "");
				retJsonObject.put("phone", customerInfo.getString("mobilenumber"));
				retJsonObject.put("wx_openid", customerInfo.getString("openid"));
				retJsonObject.put("user_id", customerInfo.getLong("id"));
				retJsonObject.put("image_url", customerInfo.getString("imageurl"));
				retJsonObject.put("customer_id", customerInfo.getLong("customerId"));
				retJsonObject.put("provider_id", customerInfo.getLong("providerId"));
				return RETFMT.replace("<DATA>", retJsonObject.toString()).replace("<CODE>", "1").replace("<MSG>", "success");
			}

		} catch (JSONException e) {
			logger.error("query failed in QueryCustomerConvertor");
		}
		return RETFMT.replace("<DATA>", "").replace("<CODE>", "0").replace("<MSG>", "failed");
	}
}

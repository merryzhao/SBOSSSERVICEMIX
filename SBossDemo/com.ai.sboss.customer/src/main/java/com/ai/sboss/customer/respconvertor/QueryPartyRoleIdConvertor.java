package com.ai.sboss.customer.respconvertor;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryPartyRoleIdConvertor  implements IBasicOutProcessor {

	private Logger logger = Logger.getLogger(QueryPartyRoleIdConvertor.class);
//	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		String body = inMessage.getBody(String.class);
		String retString = convert2requst(body);
		inMessage.setBody(retString);
		exchange.setIn(inMessage);
	}

	public String convert2requst(String data) throws Exception {
		JSONObject retJsonObject = new JSONObject();

		try {
			logger.info("QueryPartyRoleIdConvertor:" + data);
			JSONObject crmretJson = JSONObject.fromObject(data);

			if (crmretJson.getString("hub_code").equals("1")) {
				Long customerInfo = crmretJson.getLong("data");
				JSONObject retObject = new JSONObject();
				retObject.put("role_id", customerInfo);
				return retObject.toString();
			}

		} catch (JSONException e) {
			logger.error("query failed in QueryPartyRoleIdConvertor");
		}
		return null;
	}


}

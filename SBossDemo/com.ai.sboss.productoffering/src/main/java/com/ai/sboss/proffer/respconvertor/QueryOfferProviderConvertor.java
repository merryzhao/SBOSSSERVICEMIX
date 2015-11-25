package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryOfferProviderConvertor implements IBasicOutProcessor {
	private final static String CRMFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryOfferProviderConvertor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn().getBody(String.class));
		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader("Content-Type", "application/json");
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject crmObject = JSONObject.fromObject(data);
		if (StringUtils.equals("0", crmObject.getString("hub_code"))) {
			return CRMFMT.replace("<DATA>", "\"\"").replace("<CODE>", "0").replace("<MSG>", crmObject.getString("hub_value"));
		}
		
		JSONArray serviceList = new JSONArray();
		JSONArray crmList = crmObject.getJSONArray("data");
		for (int i = 0; i < crmList.size(); ++i) {
			JSONObject tempObj = new JSONObject();
			tempObj.put("service_id", crmList.getJSONObject(i).getLong("offeringId"));
			tempObj.put("service_name", crmList.getJSONObject(i).getString("offeringName"));
			tempObj.put("service_intro", crmList.getJSONObject(i).getString("offerDesc"));
			tempObj.put("service_intro_url", crmList.getJSONObject(i).getString("offerUrl"));
			serviceList.add(tempObj);
		}
		JSONObject retObject = new JSONObject();
		retObject.put("service_list", serviceList.toString());
		return CRMFMT.replace("<DATA>", retObject.toString()).replace("<CODE>", "1").replace("<MSG>", "success");
	}
}

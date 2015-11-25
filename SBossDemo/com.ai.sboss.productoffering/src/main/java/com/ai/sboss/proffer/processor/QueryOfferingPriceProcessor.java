package com.ai.sboss.proffer.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class QueryOfferingPriceProcessor implements Processor {
	Logger logger = Logger.getLogger(QueryServiceTopicInProcessor.class);
	private final String CRM_QUERY = "servicecode=getPriceByOfferParameter&WEB_HUB_PARAMS={\"data\":<DATA>,\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		String properIn = getInputParam(exchange);
		logger.info("crm query price: "+CRM_QUERY.replace("<DATA>", properIn));
		Message newinMessage = exchange.getOut();
		newinMessage.setHeader(Exchange.HTTP_QUERY, CRM_QUERY.replace("<DATA>", properIn.replace("[", "%5B").replace("]", "%5D")));
		exchange.setIn(newinMessage);
	}
	
	private String getInputParam(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		JSONObject params = JSONObject.fromObject(message.getBody(String.class));
		if (!params.containsKey("userId")) {
			return null;
		}
//		JSONObject crmqueryJsonObject = new JSONObject();
//		crmqueryJsonObject.put("offeringId", Long.toString(params.getLong("service_id")));
//		crmqueryJsonObject.put("customerId", params.getLong("userId"));
		JSONArray paramList = params.getJSONArray("price_param_list");
		JSONArray crmparamList = new JSONArray();
		for (int i = 0; i < paramList.size(); ++i) {
			JSONObject currentparam = paramList.getJSONObject(i);
			if (currentparam.isNullObject() || currentparam.isEmpty()) {
				continue;
			}
			JSONObject crmparam = new JSONObject();
			crmparam.put("offeringId", currentparam.getString("service_id"));
			crmparam.put("charSpecId", currentparam.getString("param_key"));
			crmparam.put("charValueId", currentparam.getString("param_value_id"));
			crmparam.put("roleId", currentparam.getString("role_id"));
			crmparamList.add(crmparam);
		}
		JSONObject charObject = new JSONObject();
		charObject.put("priceParamList", crmparamList.toString());
		String stringParam = charObject.toString();
		stringParam = stringParam.replace("\"", "\'");
		logger.warn("stringParam is ==>"+stringParam);
//		crmqueryJsonObject.put("parameters", "shouldberepalce");
		return charObject.toString();
	}
}

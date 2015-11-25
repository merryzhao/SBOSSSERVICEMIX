package com.ai.sboss.order.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

@Component("submitOrderInProcessor")
public class SubmitOrderInProcessor implements Processor {
	private final Logger logger = Logger.getLogger(QueryOrderDetailProcessor.class);
	private final String CRM_QUERY = "servicecode=newConnectExtendIntgrWithParameters&WEB_HUB_PARAMS={\"data\":<DATA>,\"header\":{\"Content-Type\":\"application/json\"}}";
	@Override
	public void process(Exchange exchange) throws Exception {
		String properIn = getInputParam(exchange);
		logger.info("crm query: "+CRM_QUERY.replace("<DATA>", properIn));
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
		JSONObject crmqueryJsonObject = new JSONObject();
		crmqueryJsonObject.put("offeringId", Long.toString(params.getLong("service_id")));
		crmqueryJsonObject.put("customerId", params.getLong("userId"));
		JSONArray paramList = params.getJSONArray("service_param_list");
		JSONArray crmparamList = new JSONArray();
		for (int i = 0; i < paramList.size(); ++i) {
			JSONObject currentparam = paramList.getJSONObject(i);
			if (currentparam.isNullObject() || currentparam.isEmpty()) {
				continue;
			}
			JSONObject crmparam = new JSONObject();
			crmparam.put("charSpecType", "offer");
			crmparam.put("objId", params.getLong("service_id"));
			crmparam.put("charSpecId", currentparam.getString("param_key"));
			crmparam.put("charValue", currentparam.getString("param_value"));
			crmparam.put("charValueId", currentparam.getLong("param_value_id"));
			crmparamList.add(crmparam);
		}
		crmqueryJsonObject.put("fileName", "");
		JSONObject charObject = new JSONObject();
		charObject.put("chars", crmparamList.toString());
		String stringParam = charObject.toString();
		stringParam = stringParam.replace("\"", "\'");
		logger.warn("stringParam is ==>"+stringParam);
		crmqueryJsonObject.put("parameters", "shouldberepalce");
		return crmqueryJsonObject.toString().replace("shouldberepalce", stringParam);
	}

}

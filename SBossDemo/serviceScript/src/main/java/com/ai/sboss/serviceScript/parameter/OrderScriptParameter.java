package com.ai.sboss.serviceScript.parameter;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("orderScriptParameter")
public class OrderScriptParameter extends DefaultAbstractScriptParameter {

	private static final Logger LOGGER = Logger
			.getLogger(OrderScriptParameter.class);

	private static final String urlOfOrder = "http://10.5.1.247:5095/HubCrmServlet?bridgeEndpoint=true&servicecode=queryOrderDetail&WEB_HUB_PARAMS=";

	private String urlParamOfOrder = "{\"data\":{\"customerOrderId\":\"<ORDERID>\"},\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	protected String getResponseBySendPost() {
		return urlRequest.sendPost(urlOfOrder + urlParamOfOrder,
				urlParamOfOrder);
	}

	@Override
	protected String extractValuableInfo(String requestData) {
		LOGGER.info("CRM system returned data is " + requestData);
		System.out.println("CRM system returned data is" + requestData);
		JSONObject sysRetJsonObj = JSONObject.fromObject(requestData);

		return sysRetJsonObj.toString();
	}

	@Override
	protected void buildCRMFormatedUrl() {
		urlParamOfOrder = urlParamOfOrder.replace("<ORDERID>",
				this.inputParam.getString("order_id"));
	}
}
package com.ai.sboss.serviceScript.parameter;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("serviceScriptParameter")
public class ServiceScriptParameter extends DefaultAbstractScriptParameter {

	private static final Logger LOGGER = Logger
			.getLogger(ServiceScriptParameter.class);

	private static final String urlOfOffering = "http://10.5.1.247:5091/HubCrmServlet?bridgeEndpoint=true&servicecode=acquireProductoffering&WEB_HUB_PARAMS=";

	private String urlParamOfOffering = "{\"data\":{\"productofferingID\":\"<OFFERINGID>\",\"productofferingCode\":\"\"},\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	protected String getResponseBySendPost() {
		return urlRequest.sendPost(urlOfOffering + urlParamOfOffering,
				urlParamOfOffering);
	}

	@Override
	protected String extractValuableInfo(String requestData) {
		LOGGER.info("CRM system returned data is " + requestData);
		System.out.println("CRM system returned data is" + requestData);

		JSONObject sysRetJsonObj = JSONObject.fromObject(requestData);
		return getServiceCharSpecValues(sysRetJsonObj);
	}

	@Override
	protected void buildCRMFormatedUrl() {
		urlParamOfOffering = urlParamOfOffering.replace("<OFFERINGID>",
				this.inputParam.getString("service_id"));
	}

	private String getServiceCharSpecValues(JSONObject retJsonObj) {
		String charSpecValues = StringUtils.EMPTY;
		if (null != retJsonObj) {
			charSpecValues = retJsonObj.toString();
		}

		return charSpecValues;
	}
}
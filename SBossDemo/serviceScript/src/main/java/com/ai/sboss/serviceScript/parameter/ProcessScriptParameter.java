package com.ai.sboss.serviceScript.parameter;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("processScriptParameter")
public class ProcessScriptParameter extends DefaultAbstractScriptParameter {

	private static final Logger LOGGER = Logger
			.getLogger(ProcessScriptParameter.class);

	private static final String urlOfProcess = "http://10.5.1.249:8282/sboss/queryArrangementInstancesByidWithSet";

	private String urlParamOfProcess = "{\"data\":{\"arrangementinstanceid\":\"<INSTANCEID>\"},\"desc\":\"\"}";

	@Override
	protected String getResponseBySendPost() {
		return urlRequest.sendPost(urlOfProcess, urlParamOfProcess);
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
		urlParamOfProcess = urlParamOfProcess.replace("<INSTANCEID>",
				this.inputParam.getString("process_id"));
	}
}

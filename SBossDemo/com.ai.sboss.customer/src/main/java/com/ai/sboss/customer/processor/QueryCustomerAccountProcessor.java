package com.ai.sboss.customer.processor;

import net.sf.json.JSONObject;

import com.ai.sboss.common.processor.CrmHubProcessor;

public class QueryCustomerAccountProcessor extends CrmHubProcessor {

	@Override
	public String getServiceCode() {
		return "queryCustomerAccount";
	}

	@Override
	public String convert2Req(String value) {
		logger.info("queryCustomerAccount request  ----------------------->  " + value);
		StringBuilder requestparam = new StringBuilder("\"queryConditionValue\":{\"conditionType\":[{\"condType\":\"customerId\", \"condValue\":[\"");
		String CustomerAccountId = JSONObject.fromObject(value).getString("customerId");
		requestparam.append(CustomerAccountId).append("\"]}],\"pageSize\":10,\"pageNumber\":1}");
		return requestparam.toString();
	}
}

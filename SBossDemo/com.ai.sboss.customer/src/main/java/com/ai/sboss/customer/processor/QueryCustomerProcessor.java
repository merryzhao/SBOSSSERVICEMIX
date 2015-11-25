package com.ai.sboss.customer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class QueryCustomerProcessor implements Processor {
	Logger logger = Logger.getLogger(QueryCustomerProcessor.class);
	private static final String CUSTOMER_FORMAT_QUERY = "servicecode=queryCustomer&WEB_HUB_PARAMS={\"data\":{\"queryConditionValue\":{\"conditionType\":%5B{\"condType\":\"customerId\", \"condValue\":%5B\"<ID>\"%5D}%5D,\"pageSize\":10,\"pageNumber\":1} },\"header\":{\"Content-Type\":\"application/json\"}}";
	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		String inputValue = JSONObject.fromObject(inMessage.getHeader(Exchange.HTTP_QUERY).toString()).getString("user_id");
		inMessage.setHeader(Exchange.HTTP_QUERY, CUSTOMER_FORMAT_QUERY.replace("<ID>", inputValue));
	}
}

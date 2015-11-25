package com.ai.sboss.order.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
@Component("queryCustomerOrderProcessor")
public class QueryCustomerOrderProcessor implements Processor {

	private static Logger LOGGER = Logger
			.getLogger(QueryCustomerOrderProcessor.class);

	private static final String FORMAT_QUERY = "servicecode=queryCustomerOrder&WEB_HUB_PARAMS={\"data\":{\"queryConditionValue\":{\"conditionType\":%5B{\"condType\":\"customerId\",\"condValue\":%5B\"<customer_id>\"%5D}%5D,\"pageSize\":10,\"pageNumber\":1}},\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange paramExchange) throws Exception {
		
		LOGGER.info("Call CRM's queryCustomerOrder service ...");
		Message inMessage = paramExchange.getIn();

		// get JSON data, include: customer_id, order_state, order_role
		StringBuilder request = new StringBuilder("");
		if ("POST".equals(inMessage.getHeader(Exchange.HTTP_METHOD))) {
			request.append(inMessage.getBody(String.class));
		} else if ("GET".equals(inMessage.getHeader(Exchange.HTTP_METHOD))) {
			request.append(inMessage.getHeader(Exchange.HTTP_QUERY, String.class));
		}
		LOGGER.info("Get inputted JSON data : " + request.toString());
		
		// call CRM's queryCustomerOrder service
		JSONObject param = JSONObject.fromObject(request.toString());
		String query = FORMAT_QUERY.replace("<customer_id>",
				param.getString("user_id"));
		LOGGER.info("Inputted request JSON data : " + query);
		
		inMessage.setHeader(Exchange.HTTP_QUERY, query);
	}
}
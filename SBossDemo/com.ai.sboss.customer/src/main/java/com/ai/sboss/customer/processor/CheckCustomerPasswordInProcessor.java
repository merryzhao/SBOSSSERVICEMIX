package com.ai.sboss.customer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class CheckCustomerPasswordInProcessor implements Processor {

	Logger logger = Logger.getLogger(CheckCustomerPasswordInProcessor.class);
	private static final String CUSTOMER_FORMAT_QUERY = "servicecode=checkCustomerPass&WEB_HUB_PARAMS={\"data\":<DATA>}";
	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();

		String inputString = inMessage.getBody(String.class);

		JSONObject inputJson = JSONObject.fromObject(inputString);
  logger.info("===================================check Customer "+inputJson.toString());

		String password = inputJson.getString("password") ;
		String customerId = inputJson.getString("role_id") ;

		logger.info("======================check password "+password);
logger.info("======================check ROLE_id "+customerId);
		JSONObject retData = new JSONObject();
		retData.put("customerId", customerId);
		retData.put("password", password);

		inMessage.setHeader(Exchange.HTTP_QUERY, CUSTOMER_FORMAT_QUERY.replace("<DATA>", retData.toString()));

		logger.info("======================check CUSTOMER_FORMAT_QUERY "+Exchange.HTTP_QUERY);

	}

}

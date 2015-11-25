package com.ai.sboss.customer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class QueryPartyRoleIdInProcessor  implements Processor  {
	Logger logger = Logger.getLogger(QueryPartyRoleIdInProcessor.class);
	private static final String CUSTOMER_FORMAT_QUERY = "servicecode=queryPartyRoleId&WEB_HUB_PARAMS={\"data\":{\"loginName\":\"<loginName>\"}}";
	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();

		String inputString = inMessage.getBody(String.class);

		JSONObject inputJson = JSONObject.fromObject(inputString);

		String inputValue = inputJson.getString("loginName") ;
		logger.info("CRM请求为："+CUSTOMER_FORMAT_QUERY.replace("<loginName>", inputValue));
		inMessage.setHeader(Exchange.HTTP_QUERY, CUSTOMER_FORMAT_QUERY.replace("<loginName>", inputValue));
	}
}

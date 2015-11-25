package com.ai.sboss.customer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class QueryNewCommonlyUsedAddressProcessor implements Processor {
	Logger logger = Logger.getLogger(QueryNewCommonlyUsedAddressProcessor.class);
	/*private static final String FORMAT_QUERY = "servicecode=queryNewCommonlyUsedAddress&WEB_HUB_PARAMS={\"data\":{\"queryConditionValue\":{\"conditionType\":%5B{\"condType\":\"customerId\",\"condValue\":%5B\"<ID>\"%5D}%5D,\"pageSize\":10,\"pageNumber\":1}},\"header\":{\"content-type\":\"application/json\"}}";*/
	
	private static final String FORMAT_QUERY = "servicecode=queryNewCommonlyUsedAddress&WEB_HUB_PARAMS={\"data\":{\"custId\":\"<ID>\"},\"header\":{\"content-type\":\"application/json\"}}";
	                                                                                                    
	@Override
	public void process(Exchange exchange) throws Exception {		
		JSONObject param = JSONObject.fromObject(exchange.getIn().getHeader(Exchange.HTTP_QUERY).toString());
		String query = FORMAT_QUERY.replace("<ID>", param.getString("user_id"));
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);		
	}
}

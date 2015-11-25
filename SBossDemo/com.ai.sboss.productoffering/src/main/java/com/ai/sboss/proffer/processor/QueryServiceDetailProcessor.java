package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class QueryServiceDetailProcessor implements Processor {
	Logger logger = Logger.getLogger(QueryServiceDetailProcessor.class);
	private static final String FORMAT_QUERY = "servicecode=acquireProductoffering&WEB_HUB_PARAMS={\"data\":{\"productofferingID\":\"<ID>\",\"productofferingCode\":\"<CODE>\"},\"header\":{\"Content-Type\":\"application/json\"}}";
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		String params = message.getBody(String.class);
		JSONObject jsonparam = JSONObject.fromObject(params);
		String query = FORMAT_QUERY.replace("<ID>", jsonparam.getString("service_id")).replace("<CODE>", jsonparam.getString("service_type"));
		logger.info("qeury is "+query);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}
}

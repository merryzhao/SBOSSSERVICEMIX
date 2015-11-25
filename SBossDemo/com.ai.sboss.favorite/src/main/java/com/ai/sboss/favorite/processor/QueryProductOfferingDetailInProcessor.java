package com.ai.sboss.favorite.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryProductOfferingDetailInProcessor implements IBasicInProcessor {
	Logger logger = Logger.getLogger(QueryProductOfferingDetailInProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("***********QueryProductOfferingDetailInProcessor************");
		JSONObject ret = getInputParam(exchange);
		exchange.getIn().setBody(ret.toString());

	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		String body = exchange.getIn().getBody(String.class);
		JSONObject serviceId = new JSONObject();
		serviceId.put("product_id", JSONObject.fromObject(body).getLong("service_id"));
		return serviceId;
	}

}

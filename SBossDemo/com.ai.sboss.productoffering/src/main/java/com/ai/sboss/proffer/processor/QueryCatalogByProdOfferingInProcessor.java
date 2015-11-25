package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryCatalogByProdOfferingInProcessor implements IBasicInProcessor {
	
	private Logger LOGGER = Logger.getLogger(QueryCatalogByProdOfferingInProcessor.class);
	
	private static final String FORMAT_QUERY = "servicecode=queryCatalogByProdOffering&WEB_HUB_PARAMS={\"data\":{\"prodOfferingId\":<SERVICEID>},\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject requstObject = getInputParam(exchange);
		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, FORMAT_QUERY.replace("<SERVICEID>", Long.toString(requstObject.getLong("service_id"))));
		
		LOGGER.info(requstObject.toString());
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		JSONObject input = JSONObject.fromObject(exchange.getIn().getBody(
				String.class));
		LOGGER.info(input.toString());
		return input;
	}
}

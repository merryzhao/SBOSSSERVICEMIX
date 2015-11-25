package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryServicesbyKeyInProcessor implements IBasicInProcessor {
	Logger logger = Logger.getLogger(QueryServicesbyKeyInProcessor.class);
	private static final String FORMAT_QUERY = "servicecode=queryProdOffering&WEB_HUB_PARAMS={\"data\":{\"condition\":{\"conditionType\":%5B{\"condType\":\"offeringName\",\"condValue\":%5B\"<KEYWORD>\"%5D}%5D,\"pageSize\":<PS>,\"pageNumber\":<PN>}},\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject crminputparam = getInputParam(exchange);
		Long ps = crminputparam.getLong("page_size");
		Long pn = crminputparam.getLong("page_num");
		String crmString = FORMAT_QUERY.replace("<KEYWORD>", crminputparam.getString("keyword")).replace("<PS>", ps.toString()).replace("<PN>", pn.toString());
		logger.info("CRM==query==>"+crmString);
		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, crmString);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		JSONObject bodyJsonObject = JSONObject.fromObject(in.getBody(String.class));
		return bodyJsonObject;
	}

}

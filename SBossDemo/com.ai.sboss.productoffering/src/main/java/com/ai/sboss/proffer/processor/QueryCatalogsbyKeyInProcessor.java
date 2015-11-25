package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryCatalogsbyKeyInProcessor implements IBasicInProcessor {
	Logger logger = Logger.getLogger(QueryServicesbyKeyInProcessor.class);
	private static final String FORMAT_QUERY = "servicecode=queryCatalogBySaaS&WEB_HUB_PARAMS={\"data\":{\"condition\":{\"conditionType\":%5B{\"condType\":\"<TYPE>\",\"condValue\":%5B\"<KEYWORD>\"%5D}%5D,\"pageSize\":<PS>,\"pageNumber\":<PN>}},\"header\":{\"Content-Type\":\"application/json\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject crminputparam = getInputParam(exchange);
		Long ps = crminputparam.getLong("page_size");
		Long pn = crminputparam.getLong("page_num");
		String keywords = "";
		if (crminputparam.containsKey("keyword")) {
			keywords = crminputparam.getString("keyword");
		}
		String crmString = "";
		if (StringUtils.isEmpty(keywords)) { //全量查找
			crmString = FORMAT_QUERY.replace("<TYPE>", "allResultFlag").replace("<KEYWORD>", "1").replace("<PS>", ps.toString()).replace("<PN>", pn.toString());
		} else {
			crmString = FORMAT_QUERY.replace("<TYPE>", "catalogName").replace("<KEYWORD>", keywords).replace("<PS>", ps.toString()).replace("<PN>", pn.toString());
		}
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

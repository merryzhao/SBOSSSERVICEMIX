package com.ai.sboss.favorite.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryProductOfferingDetailOutProcessor implements IBasicOutProcessor {
	Logger logger = Logger.getLogger(QueryProductOfferingDetailOutProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("***********QueryProductOfferingDetailOutProcessor************");
		Message in = exchange.getIn();
		String ret = convert2requst(in.getBody(String.class));
		in.setBody(ret);
	}

	@Override
	public String convert2requst(String data) {
		JSONObject serviceDetail = JSONObject.fromObject(data);
		if (serviceDetail.containsKey("service_id")) {
			serviceDetail.remove("service_id");
			serviceDetail.remove("favorite_id");
			serviceDetail.remove("service_tags");
		}
		return serviceDetail.toString();
	}

}

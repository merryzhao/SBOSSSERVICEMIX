package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class GetOfferingLoopIdProcessor implements Processor{
	Logger logger = Logger.getLogger(GetOfferingLoopIdProcessor.class);
	@Override
	//////////panzy3 add 6/2/2015///////////////////
	public void process(Exchange exchange) throws Exception {
		logger.info("index is"+ exchange.getProperty("CamelSplitIndex"));
		//int index = Integer.parseInt(exchange.getProperty("CamelSplitIndex").toString());
		JSONObject offerId = new JSONObject();
		JSONObject offering = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		String offeringId = offering.getString("offerId");
		String service_type = offering.getString("offerCode");
		offerId.put("service_id",offeringId);
		offerId.put("service_type",service_type);
		exchange.getIn().setBody(offerId.toString());
	}
}

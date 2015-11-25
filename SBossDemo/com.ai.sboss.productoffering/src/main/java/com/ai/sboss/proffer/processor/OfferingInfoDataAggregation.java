package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;

public class OfferingInfoDataAggregation implements AggregationStrategy {
	Logger logger = Logger.getLogger(OfferingInfoDataAggregation.class);
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		logger.info("==OfferingInfoDataAggregation==");
		Message newIn = newExchange.getIn();
		String newBody = newIn.getBody(String.class);
		JSONObject json = null;
		if (oldExchange == null) {
			json = JSONObject.fromObject(newBody);
			newIn.setBody(json);
			logger.info(json.toString());
			return newExchange;
		} else {
			Message in = oldExchange.getIn();
			logger.info("old ==>"+in.getBody(String.class));
			logger.info("new ==>"+newBody);
			json = JSONObject.fromObject(in.getBody(String.class));
			String returnStr = oldExchange.getIn().getBody().toString()+","+newBody.toString();
			//logger.info("json before----->"+returnStr);
			//json.putAll(JSONObject.fromObject(newBody));
			//logger.info("json after----->"+json);
			//in.setBody(json);
			oldExchange.getIn().setBody(returnStr);
			return oldExchange;		
		}
	}

}

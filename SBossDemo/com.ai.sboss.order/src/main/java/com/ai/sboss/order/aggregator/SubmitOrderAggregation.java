package com.ai.sboss.order.aggregator;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;

public class SubmitOrderAggregation implements AggregationStrategy {
	Logger logger = Logger.getLogger(SubmitOrderAggregation.class);

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		Message newIn = newExchange.getIn();
		String newBody = newIn.getBody(String.class);
		JSONObject json = null;
		String retbody = null;
		if (oldExchange == null) {
			newIn.setBody(newBody);
			logger.info("new aggregate=>"+newBody);
			return newExchange;
		} else {
			Message in = oldExchange.getIn();
			String olddata = in.getBody(String.class);
			logger.info("new aggregate=>"+newBody);
			logger.info("old aggregate=>"+olddata);
			if (olddata != null && newBody != null) {
				json = JSONObject.fromObject(newBody);				
				json.putAll(JSONObject.fromObject(olddata));
				retbody = json.toString();
			} else {
				logger.error("new got:"+newBody+"-->Header:"+newIn.getHeaders().toString());
				logger.error("old got:"+olddata+"-->Header:"+in.getHeaders().toString());
			}
			oldExchange.getIn().setBody(retbody);
			return oldExchange;
		}
	}

}

package com.ai.sboss.customer.aggregate;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;

public class OrderListAggregation implements AggregationStrategy {
	private Logger logger = Logger.getLogger(OrderListAggregation.class);
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		Message newIn = newExchange.getIn();
		String newBody = newIn.getBody(String.class);
		String retbody = null;
		if (oldExchange == null) {
			//logger.info("newBody===>" + newBody);
			newIn.setBody(newBody);
			return newExchange;
		} else {
			Message in = oldExchange.getIn();
			String olddata = in.getBody(String.class);
			logger.info("newBody===>" + newBody);
			logger.info("olddata===>" + olddata);
			JSONObject retObject = new JSONObject();
			JSONObject oldObj = JSONObject.fromObject(olddata);
			JSONObject newObj = JSONObject.fromObject(newBody);
			retObject.putAll(oldObj);
			retObject.putAll(newObj);
			
			retbody = "{"+newBody+","+olddata+"}";
			logger.info("aggregate===>" + retObject.toString());
			oldExchange.getIn().setBody(retObject);
			return oldExchange;
		}
	}

}

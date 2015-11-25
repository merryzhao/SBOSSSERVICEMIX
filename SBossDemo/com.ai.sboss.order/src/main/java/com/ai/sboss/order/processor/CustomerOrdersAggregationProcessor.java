package com.ai.sboss.order.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("customerOrdersAggregationProcessor")
public class CustomerOrdersAggregationProcessor implements AggregationStrategy {

	private static Logger LOGGER = Logger.getLogger(CustomerOrdersAggregationProcessor.class);

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		LOGGER.info("Begin call aggregate method ...");
		
		Message newIn = newExchange.getIn();
		String newBody = newIn.getBody(String.class);
		LOGGER.info("New Body is " + newBody);
		JSONObject json = null;
		
		if (null == oldExchange) {
			json = JSONObject.fromObject(newBody);
			newIn.setBody(json);
			LOGGER.info("The aggregate method for old exchange is null" + json.toString());
			return newExchange;
		} else {
			Message in = oldExchange.getIn();
			json = in.getBody(JSONObject.class);
			String returnStr = oldExchange.getIn().getBody().toString()+","+newBody.toString();
			oldExchange.getIn().setBody(returnStr);
			LOGGER.info("The aggregate method for old exchange is not null" + returnStr);
			return oldExchange;
		}
	}

}

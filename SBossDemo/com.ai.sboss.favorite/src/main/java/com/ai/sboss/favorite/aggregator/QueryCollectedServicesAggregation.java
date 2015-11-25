package com.ai.sboss.favorite.aggregator;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;

public class QueryCollectedServicesAggregation implements AggregationStrategy {
	Logger logger = Logger.getLogger(QueryCollectedServicesAggregation.class);

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

		Message newIn = newExchange.getIn();
		JSONObject newBody = JSONObject.fromObject(newIn.getBody(String.class));
		JSONObject json = null;
		if (oldExchange == null) {
			json = newBody;
			newIn.setBody(json);
			return newExchange;
		} else {
			Message in = oldExchange.getIn();
			json = JSONObject.fromObject(in.getBody(String.class));
			if (json.isEmpty() || newBody.isEmpty()) {
				oldExchange.getIn().setBody("");
			} else {
				json.putAll(newBody);
				oldExchange.getIn().setBody(json);
			}
			
			return oldExchange;
		}
	}
}

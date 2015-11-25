package com.ai.sboss.favorite.aggregator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;

public class FavoriteServicesInfoAggregation implements AggregationStrategy {
	Logger logger = Logger.getLogger(FavoriteServicesInfoAggregation.class);

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		Message newIn = newExchange.getIn();
		JSONObject newBody = newIn.getBody(JSONObject.class);
		JSONArray json = null;
		if (oldExchange == null) {
			json = new JSONArray();
			if (newBody != null) {
				json.add(newBody);
			}
			newIn.setBody(json.toString());
			return newExchange;
		} else {
			Message in = oldExchange.getIn();
			json = in.getBody(JSONArray.class);
			if (newBody != null) {
				json.add(newBody);
			}
			oldExchange.getIn().setBody(json.toString());
			return oldExchange;
		}
	}

}

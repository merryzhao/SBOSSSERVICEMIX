package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;

public class GetServiceTopicCatalogAggregation implements AggregationStrategy {
	Logger logger = Logger.getLogger(GetServiceTopicCatalogAggregation.class);

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		Message newIn = newExchange.getIn();
		String newBody = newIn.getBody(String.class);
		JSONObject json = null;
		String retbody = null;
		if (oldExchange == null) {
			newIn.setBody(newBody);
			return newExchange;
		} else {
			Message in = oldExchange.getIn();
			String olddata = in.getBody(String.class);
			if (olddata != null && newBody != null) {
				json = JSONObject.fromObject(newBody);				
				json.putAll(JSONObject.fromObject(olddata));
				retbody = json.toString();
			}
			oldExchange.getIn().setBody(retbody);
			return oldExchange;
		}
	}

}

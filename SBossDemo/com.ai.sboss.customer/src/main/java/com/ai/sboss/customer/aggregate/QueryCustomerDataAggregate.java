package com.ai.sboss.customer.aggregate;

import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class QueryCustomerDataAggregate implements AggregationStrategy {
	private JSONObject newJson;

	@Override
	public Exchange aggregate(Exchange exold, Exchange exnew) {
		if (exold != null) {
			JSONObject oldJson = JSONObject.fromObject(exold.getIn().getBody(String.class));
			newJson = JSONObject.fromObject(exnew.getIn().getBody(String.class));
			@SuppressWarnings("unchecked")
			Set<Object> keys = newJson.keySet();
			for (Object key : keys) {
				oldJson.put(key, newJson.get(key));
			}
			exnew.getIn().setBody(oldJson);
		}
		return exnew;
	}
}

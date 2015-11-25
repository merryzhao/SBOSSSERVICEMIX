package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class QueryOfferingProviderProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		String body = inMessage.getBody(String.class);
		String retString = null;
		if (body != null) {
			String providerId = JSONObject.fromObject(body).getString("provider_id");
			JSONObject provider = new JSONObject();
			provider.put("provider_id", providerId);
			retString = provider.toString();
		}
		exchange.getIn().setBody(retString);
	}

}

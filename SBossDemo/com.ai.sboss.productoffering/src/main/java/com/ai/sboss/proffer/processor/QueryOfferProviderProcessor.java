package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryOfferProviderProcessor implements IBasicInProcessor {
	Logger logger = Logger.getLogger(QueryOfferingListInProcessor.class);
	private static final String FORMAT_QUERY = "servicecode=queryOfferProvider&WEB_HUB_PARAMS={\"data\":<DATA>,\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject requstObject = getInputParam(exchange);
		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, FORMAT_QUERY.replace("<DATA>", requstObject.toString()));
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		JSONObject retObject = new JSONObject();
		Message in = exchange.getIn();
		String bodyString = in.getBody(String.class);
		if (!JSONObject.fromObject(bodyString).containsKey("provider_id")) {
			throw new IllegalArgumentException("必须传入provider_id");
		}
		retObject.put("providerId", JSONObject.fromObject(bodyString).getLong("provider_id"));
		return retObject;
	}

}

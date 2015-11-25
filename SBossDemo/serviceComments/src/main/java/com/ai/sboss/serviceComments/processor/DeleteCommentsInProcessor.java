package com.ai.sboss.serviceComments.processor;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class DeleteCommentsInProcessor implements IBasicInProcessor {

	private static Logger LOGGER = Logger
			.getLogger(DeleteCommentsInProcessor.class.getName());

	private static final String HUB_FORMAT_URL = "servicecode=deleteComments&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject properInput = getInputParam(exchange);
		final String query = HUB_FORMAT_URL.replace("<PARAM_JSON>",
				properInput.toString());
		LOGGER.log(Level.INFO, "URL for call CRM service is " + query);

		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		JSONObject ret = JSONObject.fromObject(exchange.getIn().getBody(
				String.class));
		LOGGER.log(Level.INFO, "Input parameter is " + ret.toString());

		JSONObject returnParam = new JSONObject();
		returnParam.put("commentsId", ret.getString("comments_id"));

		return returnParam;
	}

}

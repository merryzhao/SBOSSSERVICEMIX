package com.ai.sboss.serviceComments.processor;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class AddGradeSpecificationsInProcessor implements IBasicInProcessor {

	private static Logger LOGGER = Logger
			.getLogger(AddGradeSpecificationsInProcessor.class.getName());

	private static final String HUB_FORMAT_URL = "servicecode=addGradeSpecifications&WEB_HUB_PARAMS={\"data\":<PARAM_JSON>,\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {

		JSONObject properInput = getInputParam(exchange);
		final String addURL = HUB_FORMAT_URL
				.replace("<PARAM_JSON>", properInput.toString())
				.replace("[", "%5B").replace("]", "%5D");

		LOGGER.log(Level.INFO, addURL);

		exchange.getIn().setHeader(Exchange.HTTP_QUERY, addURL);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {

		JSONObject input = JSONObject.fromObject(exchange.getIn().getBody(
				String.class));

		LOGGER.log(Level.INFO, input.toString());

		return input;
	}

}

package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class FindServiceSchemeInProcessor implements IBasicInProcessor {

	private static final Logger LOGGER = Logger
			.getLogger(FindServiceSchemeInProcessor.class);

	private static final String FORMAT_QUERY = "servicecode=acquireProposalById&WEB_HUB_PARAMS={\"data\":{\"proposalId\":<PROPOSALID>},\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject requstObject = getInputParam(exchange);
		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader(
				Exchange.HTTP_QUERY,
				FORMAT_QUERY.replace("<PROPOSALID>",
						Long.toString(requstObject.getLong("sheme_id"))));

		LOGGER.info(requstObject.toString());
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		JSONObject input = JSONObject.fromObject(exchange.getIn().getBody(
				String.class));
		LOGGER.info(input.toString());
		return input;
	}
}
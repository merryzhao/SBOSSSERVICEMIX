package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class QueryProposalListInProcessor implements IBasicInProcessor {

	private static final Logger LOGGER = Logger
			.getLogger(QueryProposalListInProcessor.class);

	private static final String FORMAT_QUERY = "servicecode=queryProposalListByPartyRole&WEB_HUB_PARAMS={\"data\":{\"partyId\":<PARTYROLEID>,\"partyType\":<PARTYROLETYPE>,\"instanceState\":<STATE>},\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject requstObject = getInputParam(exchange);
		exchange.getIn().setHeaders(null);
		exchange.getIn()
				.setHeader(
						Exchange.HTTP_QUERY,
						FORMAT_QUERY
								.replace(
										"<PARTYROLEID>",
										Long.toString(requstObject
												.getLong("user_id")))
								.replace(
										"<PARTYROLETYPE>",
										Long.toString(requstObject
												.getLong("role_type")))
								.replace(
										"<STATE>",
										Long.toString(requstObject
												.getLong("status"))));

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

package com.ai.sboss.proffer.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class DelServiceSchemeInProcessor implements IBasicInProcessor {

	private static final Logger LOGGER = Logger
			.getLogger(DelServiceSchemeInProcessor.class);

	private static final String FORMAT_QUERY = "servicecode=delServiceCategory&WEB_HUB_PARAMS={\"data\":{\"proposalId\":<PROPOSALID>,\"catalogIds\":[<CATALOGID>]},\"header\":{\"Content-Type\":\"application/json\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject requstObject = getInputParam(exchange);

		String finalFormatQry = FORMAT_QUERY
				.replace("<PROPOSALID>",
						Long.toString(requstObject.getLong("scheme_id")))
				.replace("<CATALOGID>",
						Long.toString(requstObject.getLong("catalog_id")))
				.replace("[", "%5B").replace("]", "%5D");

		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, finalFormatQry);

		LOGGER.info(requstObject.toString());
		LOGGER.info("Exchange.HTTP_QUERY : " + finalFormatQry);
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		JSONObject input = JSONObject.fromObject(exchange.getIn().getBody(
				String.class));
		LOGGER.info(input.toString());
		return input;
	}

}

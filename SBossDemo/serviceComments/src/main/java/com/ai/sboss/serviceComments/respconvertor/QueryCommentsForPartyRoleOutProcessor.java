package com.ai.sboss.serviceComments.respconvertor;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryCommentsForPartyRoleOutProcessor implements
		IBasicOutProcessor {

	private static Logger LOGGER = Logger
			.getLogger(QueryCommentsForPartyRoleOutProcessor.class.getName());

	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		final String retResult = convert2requst(inMessage.getBody(String.class));
		LOGGER.log(Level.INFO, retResult);

		inMessage.setBody(retResult);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject input = JSONObject.fromObject(data);
		LOGGER.log(Level.INFO, input.toString());

		Object dataValue = input.get("data");
		if (null != dataValue) {
			if (dataValue.toString().isEmpty()) {
				return RETFMT.replace("<DATA>", "[]")
						.replace("<CODE>", input.getString("hub_code"))
						.replace("<MSG>", StringUtils.EMPTY);
			} else {
				return RETFMT
						.replace("<DATA>",
								input.getJSONArray("data").toString())
						.replace("<CODE>", input.getString("hub_code"))
						.replace("<MSG>", StringUtils.EMPTY);
			}
		} else {
			return RETFMT.replace("<DATA>", "[]")
					.replace("<CODE>", input.getString("0"))
					.replace("<MSG>", StringUtils.EMPTY);
		}
	}
}

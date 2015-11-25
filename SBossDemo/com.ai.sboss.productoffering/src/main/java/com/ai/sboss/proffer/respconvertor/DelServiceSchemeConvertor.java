package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class DelServiceSchemeConvertor implements IBasicOutProcessor {
	
	private static final Logger LOGGER = Logger
			.getLogger(DelServiceSchemeConvertor.class);

	private final static String RETFMT = "{\"desc\":{\"result_code\":<RETCODE>,\"result_msg\":<RETMSG>,\"data_mode\":\"1\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		final String retResult = convert2requst(inMessage.getBody(String.class));

		LOGGER.info(retResult);

		inMessage.setBody(retResult);

	}

	@Override
	public String convert2requst(String exchange) throws Exception {
		JSONObject input = JSONObject.fromObject(exchange);
		LOGGER.info(input.toString());

		Object dataValue = input.get("data");
		if (null != dataValue) {
			if (dataValue.toString().isEmpty()) {
				return RETFMT.replace("<RETCODE>", "0").replace("<RETMSG>",
						StringUtils.EMPTY);
			} else {
				JSONObject dataJSONObj = input.getJSONObject("data");
				return RETFMT.replace("<RETCODE>",
						dataJSONObj.getString("resultCode")).replace(
						"<RETMSG>", dataJSONObj.getString("result_msg"));
			}
		} else {
			return RETFMT.replace("<RETCODE>", "0").replace("<RETMSG>",
					StringUtils.EMPTY);
		}
	}

}

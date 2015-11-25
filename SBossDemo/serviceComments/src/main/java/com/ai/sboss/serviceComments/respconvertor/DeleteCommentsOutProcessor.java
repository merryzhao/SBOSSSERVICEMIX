package com.ai.sboss.serviceComments.respconvertor;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class DeleteCommentsOutProcessor implements IBasicOutProcessor {

	private static Logger LOGGER = Logger
			.getLogger(DeleteCommentsOutProcessor.class.getName());

	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		final String data = exchange.getIn().getBody(String.class);
		LOGGER.log(Level.INFO, "Returned result of CRM is " + data);

		final String returnJson = convert2requst(data);
		LOGGER.log(Level.INFO, "Returned result of ServiceMix is " + returnJson);

		exchange.getIn().setBody(returnJson);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject ret = JSONObject.fromObject(data);
		JSONObject dataJson = ret.getJSONObject("data");

		JSONObject returnJson = new JSONObject();
		returnJson.put("result_code", dataJson.getString("resultCode"));
		returnJson.put("result_msg", dataJson.getString("result_msg"));

		return RETFMT.replace("<DATA>", returnJson.toString())
				.replace("<CODE>", dataJson.getString("resultCode"))
				.replace("<MSG>", dataJson.getString("result_msg"));
	}

}

package com.ai.sboss.serviceComments.respconvertor;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class AddGradeSpecificationsOutProcessor implements IBasicOutProcessor {

	private static Logger LOGGER = Logger
			.getLogger(AddGradeSpecificationsOutProcessor.class.getName());

	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":<RET_MSG>,\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {

		Message inMessage = exchange.getIn();
		final String retResult = convert2requst(inMessage.getBody(String.class));
		LOGGER.log(Level.INFO, retResult);

		inMessage.setBody(retResult);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject ret = new JSONObject();
		JSONObject input = JSONObject.fromObject(data);
		ret.put("result_code",
				input.getJSONObject("data").getString("resultCode"));
		ret.put("result_msg",
				input.getJSONObject("data").getString("result_msg"));
		return RETFMT.replace("<DATA>", ret.toString())
				.replace("<RET_CODE>", ret.getString("result_code"))
				.replace("<RET_MSG>", ret.getString("result_msg"));
	}
}

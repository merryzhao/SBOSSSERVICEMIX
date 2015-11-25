package com.ai.sboss.favorite.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class ModifyServiceTagsOutProcessor implements IBasicOutProcessor{
	//private Logger logger = Logger.getLogger(AddServiceToFavoriteOutProcessor.class);
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		String ret = convert2requst(inMessage.getBody(String.class));
		inMessage.setBody(ret);

	}

	@Override
	public String convert2requst(String data) {
		JSONObject ret = new JSONObject();
		JSONObject input = JSONObject.fromObject(data);
		ret.put("result_code", input.getJSONObject("data").getString("resultCode"));
		ret.put("result_msg", input.getJSONObject("data").getString("result_msg"));
		return RETFMT.replace("<DATA>", ret.toString()).replace("<CODE>", input.getJSONObject("data").getString("resultCode"))
				.replace("<MSG>", input.getJSONObject("data").getString("result_msg"));
	}

}

package com.ai.sboss.favorite.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class AddServiceToFavoriteOutProcessor implements IBasicOutProcessor {
	//private Logger logger = Logger.getLogger(AddServiceToFavoriteOutProcessor.class);
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":<RET_MSG>,\"data_mode\":\"0\",\"digest\":\"\"}}";
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
		return RETFMT.replace("<DATA>", ret.toString()).replace("<RET_CODE>", ret.getString("result_code")).replace("<RET_MSG>", ret.getString("result_msg"));
	}

}

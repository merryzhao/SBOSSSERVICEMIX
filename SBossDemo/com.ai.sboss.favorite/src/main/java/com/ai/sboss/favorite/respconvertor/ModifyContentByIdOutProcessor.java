package com.ai.sboss.favorite.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class ModifyContentByIdOutProcessor implements IBasicOutProcessor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String ret = convert2requst(in.getBody(String.class));
		in.setBody(ret);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject ret = JSONObject.fromObject(data);
		
		return ret.getJSONObject("data").toString();
	}

}

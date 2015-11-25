package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryServicesbyKeyConvertor implements IBasicOutProcessor {

	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn().getBody(String.class));
		exchange.getIn().setHeaders(null);
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		data = "[" + data + "]";
		JSONObject retObject = new JSONObject();
		retObject.put("service_list", data);
		return retObject.toString();
	}

}

package com.ai.sboss.favorite.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryCollectedServicesOutProcessor implements IBasicOutProcessor {
	Logger logger = Logger.getLogger(QueryCollectedServicesOutProcessor.class);
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":1,\"result_msg\":\"sucess\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("***********QueryCollectedServicesOutProcessor************");
		Message inMessage = exchange.getIn();
		String ret = convert2requst(inMessage.getBody(String.class));
		inMessage.setBody(ret);

	}

	@Override
	public String convert2requst(String data) {
		JSONObject ret = new JSONObject();
		ret.put("collected_services", data);
		return RETFMT.replace("<DATA>", ret.toString());
	}

}

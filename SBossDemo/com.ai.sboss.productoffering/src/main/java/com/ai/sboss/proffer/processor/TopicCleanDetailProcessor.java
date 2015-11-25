package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;

public class TopicCleanDetailProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		JSONObject bodyJson = JSONObject.fromObject(in.getBody(String.class));
		String retString = null;
		if (StringUtils.equals(bodyJson.getJSONObject("desc").getString("result_code"), "1")) {
			retString = bodyJson.getJSONObject("data").toString();
		}
		in.setHeaders(null);
		in.setBody(retString);
	}

}

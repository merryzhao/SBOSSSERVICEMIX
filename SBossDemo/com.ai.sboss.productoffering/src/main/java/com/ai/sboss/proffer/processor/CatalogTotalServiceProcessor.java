package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class CatalogTotalServiceProcessor implements Processor {
	private Logger logger = Logger.getLogger(CatalogTotalServiceProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String body = in.getBody(String.class);
		if (StringUtils.isEmpty(body)) {
			//TODO: Exception Catch
		}
		JSONObject retObject = new JSONObject();
		//TODO: 现在的type是硬编码
		retObject.put("result_type", 0);
		retObject.put("result_items", "[" + body + "]");
		//logger.info("got==>"+retObject.toString());
		in.setHeaders(null);
		in.setBody(retObject.toString());
	}

}

package com.ai.sboss.customer.respconvertor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class CustomerLocationOutProcessor implements IBasicOutProcessor {
	final static Log LOGGER = LogFactory.getLog(CustomerLocationOutProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn().getBody(String.class));
		exchange.getIn().setHeaders(null);
		LOGGER.info("合并处理后的结果====>" + retString);
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject retObj = new JSONObject();
		retObj.put("user_address", data);
		return retObj.toString();
	}

}

package com.ai.sboss.order.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class KeepDataProcessor implements Processor {
	Logger logger = Logger.getLogger(KeepDataProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String body = in.getBody(String.class);
		Message retMessage = exchange.getOut();
		retMessage.setHeader(Exchange.FILE_NAME, "KEEPDATA");
		retMessage.setBody(body);
		exchange.setIn(retMessage);
	}

}

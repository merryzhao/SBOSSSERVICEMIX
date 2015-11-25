package com.ai.sboss.customer.aggregate;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class AggregationProessor implements Processor {
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		String request = "";
		if ("POST".equals(message.getHeader(Exchange.HTTP_METHOD))) {
			request = message.getBody(String.class);
		} else if ("GET".equals(message.getHeader(Exchange.HTTP_METHOD))) {
			request = message.getHeader(Exchange.HTTP_QUERY, String.class);
		}
		message.setHeader(Exchange.HTTP_QUERY, request);
	}
}

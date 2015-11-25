package com.ai.sboss.customer.respconvertor;

import org.apache.camel.Exchange;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class TimeListDataAggregation implements IBasicOutProcessor {

	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn().getBody(String.class));
	}

	@Override
	public String convert2requst(String data) throws Exception {
		
		return data;
	}

}

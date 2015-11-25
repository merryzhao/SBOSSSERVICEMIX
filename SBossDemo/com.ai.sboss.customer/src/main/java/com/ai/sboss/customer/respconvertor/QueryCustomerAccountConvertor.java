package com.ai.sboss.customer.respconvertor;

import org.apache.log4j.Logger;

public class QueryCustomerAccountConvertor {
	private Logger logger = Logger.getLogger(QueryCustomerAccountConvertor.class);

	public String convert(String value) {
		logger.info("QueryCustomerAccountConvertor response " + value);
		return value;
	}
}

package com.ai.sboss.customer.aggregate;

import org.apache.log4j.Logger;

public class QueryCustomerGeneralConvertor {
	private Logger logger = Logger.getLogger(QueryCustomerGeneralConvertor.class);

	public String convert(String value) {
		logger.info("QueryCustomerConvertor response " + value);
		return value;
	}
}

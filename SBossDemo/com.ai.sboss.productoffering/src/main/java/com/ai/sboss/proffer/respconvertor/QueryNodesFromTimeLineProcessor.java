package com.ai.sboss.proffer.respconvertor;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryNodesFromTimeLineProcessor implements IBasicOutProcessor {
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryServiceTopicConvertor.class);
	@Override
	public void process(Exchange exchange) throws Exception {

	}

	@Override
	public String convert2requst(String data) throws Exception {
		return null;
	}

}

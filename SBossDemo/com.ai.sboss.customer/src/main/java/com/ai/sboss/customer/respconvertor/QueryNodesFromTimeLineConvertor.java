package com.ai.sboss.customer.respconvertor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryNodesFromTimeLineConvertor implements IBasicOutProcessor {
	private Logger logger = Logger.getLogger(QueryNodesFromTimeLineConvertor.class);
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		String body = inMessage.getBody(String.class);
		String retString = convert2requst(body);
		exchange.getIn().setHeaders(null);
		exchange.getIn().setHeader("Content-Type", "application/json");
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		if (StringUtils.isEmpty(data)) {
			return RETFMT.replace("<DATA>", "\"\"").replace("<CODE>", "0").replace("<MSG>", "failed");
		}
		
		//TODO yinwenjie更改，暂时先把json中的错误部分去掉，至于为什么会出现这个问题，由开发者自行排除
		//logger.info("origin data111111111111111111=========yinwenjie=>"+data);
		//data = data.replaceAll("\\,\\[\\]", "");
		//logger.info("data111111111111111111=========yinwenjie=>"+data);
		
		return RETFMT.replace("<DATA>", data).replace("<CODE>", "1").replace("<MSG>", "success");
	}
}

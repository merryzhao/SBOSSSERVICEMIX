/**
 * 
 */
package com.ai.sboss.proffer.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
/**
 * @author idot
 *
 */
public class QueryServiceTopicInProcessor implements Processor{

	Logger logger = Logger.getLogger(QueryServiceTopicInProcessor.class);
	private static final String FORMAT_QUERY = "servicecode=queryProdOffering&WEB_HUB_PARAMS={\"data\":{\"condition\":{\"conditionType\":%5B{\"condType\":\"catalogId\",\"condValue\":%5B\"<ID>\"%5D}%5D,\"pageSize\":<PS>,\"pageNumber\":<PN>}},\"header\":{\"Content-Type\":\"application/json\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Message message = exchange.getIn();
		JSONObject param = JSONObject.fromObject(message.getBody(String.class));
		logger.info("list is "+param.toString());
		String query = FORMAT_QUERY.replace("<ID>", param.getString("catalog_id")).replace("<PS>", param.getString("page_size")).replace("<PN>", param.getString("page_no"));
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, query);
	}

}

/**
 * 
 */
package com.ai.sboss.favorite.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

/**
 * @author monica
 * 循环遍历每一个content_id,调用crm服务获取对应的文件服务器url地址
 */
public class GetContentLoopIdProcessor implements Processor{

	Logger logger = Logger.getLogger(GetContentLoopIdProcessor.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		logger.info("GetContentLoopIdProcess index --->"+ exchange.getProperty("CamelSplitIndex"));
		JSONObject contentIdJson = new JSONObject();
		JSONObject content = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		Long contentId = content.getLong("content_id");
		contentIdJson.put("content_id",contentId);
		exchange.getIn().setBody(contentIdJson.toString());
	}

}

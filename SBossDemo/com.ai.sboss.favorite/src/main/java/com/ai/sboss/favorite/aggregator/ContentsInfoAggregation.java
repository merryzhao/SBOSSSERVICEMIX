/**
 * 
 */
package com.ai.sboss.favorite.aggregator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;

/**
 * @author monica
 * 循环遍历所有的content_id，从文件服务器获取到每个content_id对应的文本内容，将其保存为一个json，最后在存放在同一个content中
 * 
 */
public class ContentsInfoAggregation implements AggregationStrategy{

	Logger logger = Logger.getLogger(ContentsInfoAggregation.class);
	
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		// TODO Auto-generated method stub
		
		/**
		 * 根据content_id从文件服务器获取到的content信息是jsonobject格式的
		 * 将多个jsonobject聚合在一起，构成一个jsonarray
		 */
		Message newIn = newExchange.getIn();
//		logger.info("input new Body--->" + newIn.getBody(String.class));
		JSONObject newBody = JSONObject.fromObject(newIn.getBody(String.class));
		JSONArray json = null;
		if (oldExchange == null) {
			json = new JSONArray();
			if (newBody != null) {
				json.add(newBody);
			}
			newIn.setBody(json.toString());
//			logger.info("ContentsInfoAggregation newExchange--->" + newExchange.toString());
			return newExchange;
		} else {
			Message in = oldExchange.getIn();
			json = JSONArray.fromObject(in.getBody(String.class));
//			json = in.getBody(JSONArray.class);
			if (newBody != null) {
//				logger.info("old Body--->" + json.toString());
//				logger.info("new Body--->" + newBody.toString());
				json.add(newBody);
			}
			oldExchange.getIn().setBody(json.toString());
			
			return oldExchange;
		}		
	}
}

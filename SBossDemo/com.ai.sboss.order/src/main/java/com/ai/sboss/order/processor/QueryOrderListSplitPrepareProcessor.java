package com.ai.sboss.order.processor;

import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * 该processor用于在查询crm service List后，在进行split之前，查询data中的数据量，并将order number写入header，
 * 以便在后续进行分组合并时，有数据记录可依据
 * @author yinwenjie
 */
@Component("queryOrderListSplitPrepareProcessor")
public class QueryOrderListSplitPrepareProcessor implements Processor {
	/**
	 * 日志信息
	 */
	private static final Log LOGGER = LogFactory.getLog(QueryOrderListSplitPrepareProcessor.class);

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		/*
		 * 通过对body中data数组数量的分析，得到order number并存入header中。过程为：
		 * 1、转换成json，取其中的data
		 * 2、如果转换错误或者data中没有任何元素，则ordernumber写为0
		 * 3、其他情况下，写入ordernumber=data元素中，数组的数量
		 * */
		Message inmessage = exchange.getIn();
		String orderListBody = inmessage.getBody(String.class);
		QueryOrderListSplitPrepareProcessor.LOGGER.info("CRM返回的查询结果为：" + orderListBody);
		Integer ordernumber = 0;
		
		//为了保证后续split后能够正常的合并，这里在header部分写入一个唯一的值，以便进行分组合并
		String requestuuid = UUID.randomUUID().toString();
		inmessage.setHeader("requestuuid",requestuuid);
		
		//1、===============
		JSONObject orderlistJSON;
		try {
			orderlistJSON = JSONObject.fromObject(orderListBody);
		} catch (Exception e) {
			QueryOrderListSplitPrepareProcessor.LOGGER.info("orderlistSize = " + ordernumber);
			inmessage.setHeader("orderlistSize", ordernumber);
			return;
		}
		
		//2、===============
		//如果条件成立，说明没有在json中找到相应的元素
		if(!orderlistJSON.has("data")) {
			QueryOrderListSplitPrepareProcessor.LOGGER.info("orderlistSize = " + ordernumber);
			inmessage.setHeader("orderlistSize", ordernumber);
			return;
		}
		//注意，由于这个processor被重用了，那么data属性中可能只有一个元素（不一定是数组）
		Object orderListObjects = null;
		try {
			orderListObjects = orderlistJSON.get("data");
			if(orderListObjects == null) {
				ordernumber = 0;
			} 
			//如果条件成立，说明data中只有一个元素（不是数组）
			else if (orderListObjects instanceof JSONObject && ((JSONObject)orderListObjects).has("OFFER_ID")) {
				//3、==========
				ordernumber = 1;
			} 
			//如果条件成立，说明data中有多个order（是一个数组）
			else if(orderListObjects instanceof JSONArray && ((JSONArray)orderListObjects).size() > 0) {
				ordernumber = ((JSONArray)orderListObjects).size();
			}
			//其他情况，一律为0
			else {
				ordernumber = 0;
			}
		} catch(Exception e) {
			QueryOrderListSplitPrepareProcessor.LOGGER.info("orderlistSize = " + ordernumber);
			inmessage.setHeader("orderlistSize", ordernumber);
			return;
		}
		
		//返回
		QueryOrderListSplitPrepareProcessor.LOGGER.info("orderlistSize = " + ordernumber);
		inmessage.setHeader("orderlistSize", ordernumber);
		inmessage.setBody(orderlistJSON, JSONObject.class);
	}
}

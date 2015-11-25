package com.ai.sboss.customer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryNodesFilterListProcessor implements IBasicOutProcessor {
	private Logger logger = Logger.getLogger(QueryNodesFilterListProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		String retString = convert2requst(exchange.getIn().getBody(String.class));
		exchange.getIn().setHeaders(null);
		logger.info("==========================FILTER分割==============================");
		exchange.getIn().setBody(retString);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject totalobj = JSONObject.fromObject(data);
		Long orderId = -1L;
		logger.info("获取到参数"+totalobj.toString());
		Boolean needFilter = false;
		if (totalobj.containsKey("order_number")) {
			
			orderId = totalobj.getLong("order_number");
			logger.info("需要剃掉==>"+orderId);
			needFilter = true;
		}
		JSONArray orderList = totalobj.getJSONArray("order_list");
		/*JSONArray newOrderList = new JSONArray();*/
		StringBuilder newOrderList = new StringBuilder();
		for (int i = 0; i < orderList.size(); ++i) {
			JSONObject tempObj = new JSONObject();
			tempObj.put("order_number", orderList.getJSONObject(i).getLong("order_number"));
			tempObj.put("service_id", orderList.getJSONObject(i).getLong("service_id"));
			tempObj.put("service_name", orderList.getJSONObject(i).getString("service_name"));
			tempObj.put("order_create_time", orderList.getJSONObject(i).getString("service_time_duration"));
			logger.info("轮询到==>"+orderList.getJSONObject(i).getLong("order_number"));
			if (orderList.getJSONObject(i).getLong("order_number") == orderId) {
				logger.info("查到==>"+orderList.getJSONObject(i).getLong("order_number"));
				newOrderList = new StringBuilder();
				newOrderList.append(tempObj.toString());
				logger.info("返回==>"+newOrderList.toString());
				needFilter = false;
				return newOrderList.toString();
			}
			newOrderList.append(tempObj.toString());
			if (i != orderList.size()) {
				newOrderList.append(";");
			}
		}
		if (needFilter) {
			newOrderList = new StringBuilder();
		}
		return newOrderList.toString();
	}

}

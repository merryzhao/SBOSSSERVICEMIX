package com.ai.sboss.order.aggregator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.CompletionAwareAggregationStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;


/**
 * 这是一个信息组合器，用来对上一步split拆分处理的数据进行组合（不关心数据重新组合的顺序）
 * @author yinwenjie
 */
@Component("queryOrderListAggregator")
public class QueryOrderListAggregator implements CompletionAwareAggregationStrategy  {
	/**
	 * 日志信息在这里
	 */
	private static final Log OrderInfoAggregatorLOG = LogFactory.getLog(QueryOrderListAggregator.class);

	/**
	 * 用于临时存储requestuuid和orderlist的KV区域
	 * 提请注意，只有测试代码可以这样用，正式生产代码绝对不能这样用，不然集群模式下或者高并发情况下，都会出现问题
	 */
	private static final ConcurrentHashMap<String , JSONArray> KVMAP = new ConcurrentHashMap<String , JSONArray>();

	/* (non-Javadoc)
	 * @see org.apache.camel.processor.aggregate.AggregationStrategy#aggregate(org.apache.camel.Exchange, org.apache.camel.Exchange)
	 */
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		//首先打出日志，好观察送入AggregationStrategy的每一条数据是什么样子的
		QueryOrderListAggregator.OrderInfoAggregatorLOG.info("==================有一条数据元素来到aggregate==================");
		Message messagein = newExchange.getIn();
		String bodyin = messagein.getBody(String.class);
		Map<String, Object> headers = messagein.getHeaders();
		QueryOrderListAggregator.OrderInfoAggregatorLOG.info("//newExchange:body = " + bodyin + " || header = " + headers);

		/*
		 * 注意，新的数据都是存放在newExchange中的。处理步骤包括：
		 * (2015-08-27 之后不再需要再进行serverDetail的查询，也就是不再需要第 1 步了)
		 * 1、从newExchange中，拿取从getServiceDetailEndpoint中返回的结果特别是：
		 * provider_id、provider_portrait_url、service_time_duration、order_price、service_type
		 * 注意，如果出现取值错误，那么以上错误的值就为null或者""
		 *
		 * 2、从head中取出以下值：
		 * order_id、order_number、order_state、order_state_name、order_create_time、service_id、service_name、
		 * provider_portrait_url、provider_id、order_price、service_time_duration、service_type
		 * 注意、如果赋值出现错误，就是null或者""
		 *
		 * 3、组合成一个json格式，然后存储到requestuuid对应的jsonObject集合中。然后本次处理完成
		 * 4、当onCompletion方法触发，证明aggregate已经complete，这是从静态K-V	中取出所有的临时数据组装成json
		 * 然后清空K-V组。
		 * */

		//1、=============2015-08-27 之后不再需要再进行serverDetail的查询
//		Long provider_id = 0l;
//		String provider_portrait_url = "";
//		String service_time_duration = "";
//		String order_price = "";
//		Integer service_type = 0;
//		JSONObject bodyinJSON = null;
//		try {
//			bodyinJSON = JSONObject.fromObject(bodyin);
//			JSONObject dataJSON = bodyinJSON.getJSONObject("data");
//			provider_id = dataJSON.has("provider_id")?dataJSON.getLong("provider_id"):-1l;
//			provider_portrait_url = dataJSON.has("provider_portrait_url")?dataJSON.getString("provider_portrait_url"):"";
//			//TODO 时间段还没有调相应接口
//			service_time_duration = "";
//			service_type = 0;
//		} catch(Exception e) {
//			QueryOrderListAggregator.OrderInfoAggregatorLOG.warn(e.getMessage(), e);
//		}

		//2、=============
		Long order_id = 0l;
		Long order_number = 0L;
		Integer order_state = 0;
		String order_state_name = "";
		String order_create_time = "";
		Long service_id = 0l;
		String service_name = "";
		String provider_portrait_url = "";
		Long provider_id = 0L;
		String order_price = "";
		String service_time_duration = "";
		String service_type = "";
		String order_charList = "";
		String provider_name = "";
		String price_unit = "$";
		String process_instance_id = "" ;
		Long customer_id = 0L;
		OrderInfoAggregatorLOG.info("===Result Headers===****>"+headers.toString());
		try {
			order_id = (!headers.containsKey("orderid") || headers.get("orderid") == null || StringUtils.isEmpty(headers.get("orderid").toString()))?0l:Long.parseLong(headers.get("orderid").toString());
			order_number = (!headers.containsKey("orderNumber") || headers.get("orderNumber") == null)?0L:Long.valueOf(headers.get("orderNumber").toString());
			order_state = (!headers.containsKey("orderState") || headers.get("orderState") == null)?0:Integer.parseInt(headers.get("orderState").toString());
			order_state_name = (!headers.containsKey("orderStateName") || headers.get("orderStateName") == null)?"":headers.get("orderStateName").toString();
			order_create_time = (!headers.containsKey("orderCreateTime") || headers.get("orderCreateTime") == null)?"":headers.get("orderCreateTime").toString();
			service_id = (!headers.containsKey("service_id") || StringUtils.isEmpty(headers.get("service_id").toString()))?0l:Long.parseLong(headers.get("service_id").toString());
			service_name = (!headers.containsKey("offername") || headers.get("offername") == null)?"":headers.get("offername").toString();

			provider_portrait_url = (!headers.containsKey("provider_portrait_url") || headers.get("provider_portrait_url") == null)?"":headers.get("provider_portrait_url").toString();
			provider_id = (!headers.containsKey("provider_id") || headers.get("provider_id") == null)?0L:Long.valueOf(headers.get("provider_id").toString());
			provider_name = (!headers.containsKey("provider_name") || headers.get("provider_name") == null)?"":headers.get("provider_name").toString();
			customer_id = (!headers.containsKey("customer_id") || headers.get("customer_id") == null)?0L:Long.valueOf(headers.get("customer_id").toString());
			QueryOrderListAggregator.OrderInfoAggregatorLOG.info("这个时候能够获取到provider_id"+provider_id+"\t customer_id"+customer_id);
			order_price = (!headers.containsKey("order_price") || headers.get("order_price") == null)?"":headers.get("order_price").toString();
			price_unit = (!headers.containsKey("price_unit") || headers.get("price_unit") == null)?"":headers.get("price_unit").toString();

			process_instance_id = (!headers.containsKey("process_instance_id") || headers.get("process_instance_id") == null)?"":headers.get("process_instance_id").toString();

			service_time_duration = (!headers.containsKey("service_time_duration") || headers.get("service_time_duration") == null)?"":headers.get("service_time_duration").toString();
			service_type = (!headers.containsKey("service_type") || headers.get("service_type") == null)?"":headers.get("service_type").toString();
			order_charList = (!headers.containsKey("char_list") || headers.get("char_list") == null)?"":headers.get("char_list").toString();
		} catch(Exception e) {
			QueryOrderListAggregator.OrderInfoAggregatorLOG.warn(e.getMessage(), e);
		}

		//3、开始构造JSON信息了
		String requestuuid = headers.get("requestuuid").toString();
		JSONObject  orderItem = new JSONObject();
		orderItem.put("order_id", order_id);
		orderItem.put("order_number", order_number);
		orderItem.put("order_state", order_state);
		orderItem.put("order_state_name", order_state_name);
		orderItem.put("order_create_time", order_create_time);
		orderItem.put("service_id", service_id);
		orderItem.put("service_name", service_name);
		orderItem.put("provider_id", provider_id);
		orderItem.put("provider_name", provider_name);
		orderItem.put("customer_id", customer_id);
		orderItem.put("provider_portrait_url", provider_portrait_url);
		orderItem.put("service_time_duration", service_time_duration);
		orderItem.put("order_price", order_price);
		orderItem.put("service_type", service_type);
		orderItem.put("price_unit", price_unit);
		orderItem.put("process_instance_id", process_instance_id);

		orderItem.put("char_list", order_charList);
		//存入
		JSONArray resultOrderArray = QueryOrderListAggregator.KVMAP.get(requestuuid);
		//如果条件成立，说明是第一条数据
		if(resultOrderArray == null) {
			resultOrderArray = new JSONArray();
			QueryOrderListAggregator.KVMAP.put(requestuuid, resultOrderArray);
		}
		resultOrderArray.add(orderItem);


		OrderInfoAggregatorLOG.info("===Result BodyBody 里面的值 ===****>"+headers.toString());

		//完成一条order信息的存储。
		if(oldExchange == null) {
			return newExchange;
		} else {
			return oldExchange;
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.processor.aggregate.CompletionAwareAggregationStrategy#onCompletion(org.apache.camel.Exchange)
	 */
	@Override
	public void onCompletion(Exchange exchange) {
		//当然成所有数据条目组合后，这个方法被通知
		QueryOrderListAggregator.OrderInfoAggregatorLOG.info("==================onCompletion:已完成合并==================");

		Message messagein = exchange.getIn();
		Map<String, Object> headers = messagein.getHeaders();
		String requestuuid = headers.get("requestuuid").toString();
		JSONArray jsonArray = QueryOrderListAggregator.KVMAP.get(requestuuid);
		QueryOrderListAggregator.OrderInfoAggregatorLOG.info("//构造json得到：" + jsonArray);

		//然后清空KVMAP
		QueryOrderListAggregator.KVMAP.remove(requestuuid);

		exchange.getIn().setBody(jsonArray , JSONArray.class);
		exchange.getIn().setHeader("Content-Type", "application/json");
	}
}


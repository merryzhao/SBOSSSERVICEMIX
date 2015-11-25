package com.ai.sboss.order.processor;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * 按照serviceid（从crm过来的信息表示为offer_id），组装数据，准备查询对应的service详情信息。
 * @author yinwenjie
 */
@Component("queryServiceDetailProcessor")
public class QueryServiceDetailProcessor implements Processor {

	/**
	 * 日志信息
	 */
	private static final Log LOGGER = LogFactory.getLog(QueryServiceDetailProcessor.class);

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		/*
		 * queryServiceDetailProcessor有几个处理过程：
		 * 1、首先根据sboss提供的getServiceDetail接口，进行接口参数的组合。格式类似于：
		 * {"data":{"service_id":201506010102,"service_type":0},"desc":{"data_mode":"0","digest":""}}
		 *
		 * 2、为了保证拆分后数据的orderid等信息完整，这些信息需要传入到head中。
		 * 并且保证之前已经存在head中的ordernumber和requestuuid的存在。
		 * */
		Message in = exchange.getIn();
		JSONObject bodySplitOrderObject = in.getBody(JSONObject.class);

		LOGGER.info("bodySplitOrderObject=====>" + bodySplitOrderObject.toString());
		Map<String, Object> headers = in.getHeaders();
		QueryServiceDetailProcessor.LOGGER.info("每一条拆分后order的信息为：bodyResult = " + bodySplitOrderObject + " || header = " + headers);
		//这里本来应该判断一下相关参数的正误性。
		String offerID = bodySplitOrderObject.getString("OFFER_ID");
		String service_type = bodySplitOrderObject.has("SERVICE_TYPE")?bodySplitOrderObject.getString("SERVICE_TYPE"):"1";

		//1、==================组合参数
		String serviceDetailURLParamters = "{\"data\":{\"service_id\":" + offerID + ",\"service_type\":" + service_type + "},\"desc\":{\"data_mode\":\"0\",\"digest\":\"\"}}";
		in.setBody(serviceDetailURLParamters, String.class);

		//2、==================存入后面所需要，不会变化的重要信息到header中
		//注意，查询订单详情的时候，CRM是没有返回orderid的。
		String orderid = bodySplitOrderObject.has("ORDER_ID")?bodySplitOrderObject.getString("ORDER_ID"):null;
		String orderState = bodySplitOrderObject.getString("ORDER_STATE");
		String orderStateName = bodySplitOrderObject.has("ORDER_STATE_NAME")?bodySplitOrderObject.getString("ORDER_STATE_NAME"):null;
		String offername = bodySplitOrderObject.getString("OFFER_NAME");
		//orderCreateTime在CRM中，可能叫ORDER_CREATE_TIME也可能叫ORDER_TIME
		String orderCreateTime = bodySplitOrderObject.has("ORDER_CREATE_TIME")?bodySplitOrderObject.getString("ORDER_CREATE_TIME"):null;
		orderCreateTime = (bodySplitOrderObject.has("ORDER_TIME") && StringUtils.isEmpty(orderCreateTime))?bodySplitOrderObject.getString("ORDER_TIME"):orderCreateTime;
		Long orderNumber = bodySplitOrderObject.getLong("ORDER_NUMBER");
		//2015-08-27，CRM添加了新的字段，所以这里要进行新字段的获取
		//PROVIDER_PORTRAIT_URL
		String provider_portrait_url = bodySplitOrderObject.has("PROVIDER_PORTRAIT_URL")?bodySplitOrderObject.getString("PROVIDER_PORTRAIT_URL"):"";
		//PROVIDER_ID
		Long provider_id = (bodySplitOrderObject.has("PROVIDER_ID")&& !StringUtils.isEmpty(bodySplitOrderObject.getString("PROVIDER_ID")))?bodySplitOrderObject.getLong("PROVIDER_ID"):0L;
		//PROVIDER_NAME
		String provider_name = bodySplitOrderObject.has("PROVIDER_NAME")?bodySplitOrderObject.getString("PROVIDER_NAME"):"";
		//CUSTOMER_ID
		Long customer_id = (bodySplitOrderObject.has("CUSTOMER_ID")&& !StringUtils.isEmpty(bodySplitOrderObject.getString("CUSTOMER_ID")))?bodySplitOrderObject.getLong("CUSTOMER_ID"):0L;
		//ORDER_PRICE
		String order_price = bodySplitOrderObject.has("ORDER_PRICE")?bodySplitOrderObject.getString("ORDER_PRICE"):"";

		//SERVICE_TIME
		String service_time_duration = bodySplitOrderObject.has("SERVICE_TIME")?bodySplitOrderObject.getString("SERVICE_TIME"):"";
		//process_instance_id
		String process_instance_id = null ;
		//ORDER_UNIT
		String price_unit = "";

		if (bodySplitOrderObject.containsKey("process_instance_id")) {
		 process_instance_id = bodySplitOrderObject.getString("process_instance_id");
		LOGGER.info("process_instance_id=====>" + process_instance_id);
		}
		//增加对订单特定参数的解析
		if (bodySplitOrderObject.containsKey("CHAR_LIST")) {
			JSONArray charList = bodySplitOrderObject.getJSONArray("CHAR_LIST");
			//提取可选参数中的必选参数
			for (int i = 0; i < charList.size(); ++i) {
				JSONObject tempObj = new JSONObject();
				tempObj.put("param_key", charList.getJSONObject(i).getString("CHAR_NAME"));
				tempObj.put("param_id", charList.getJSONObject(i).getLong("CHAR_ID"));
				tempObj.put("param_value", charList.getJSONObject(i).getString("CHAR_VALUE"));
				//found price unit
				if (25043195 == charList.getJSONObject(i).getLong("CHAR_ID")) {
					price_unit = charList.getJSONObject(i).getString("CHAR_VALUE");
				}
				bodySplitOrderObject.getJSONArray("CHAR_LIST").remove(i);
			}
			try {
				in.setHeader("char_list", parserOrderCharList(bodySplitOrderObject.getJSONArray("CHAR_LIST")).toString());
			} catch (JSONException e) {
				QueryServiceDetailProcessor.LOGGER.error(e.toString()+bodySplitOrderObject.get("CHAR_LIST").toString());
				in.setHeader("char_list", new JSONArray().toString());
			}
		}

		if(orderid != null) {
			in.setHeader("orderid", orderid);
		}
		in.setHeader("orderState", orderState);
		in.setHeader("orderStateName", orderStateName);
		in.setHeader("offername", offername);
		in.setHeader("orderCreateTime", orderCreateTime);
		in.setHeader("orderNumber", orderNumber);
		in.setHeader("provider_portrait_url", provider_portrait_url);
		in.setHeader("provider_id", provider_id);
		in.setHeader("provider_name", provider_name);
		in.setHeader("customer_id", customer_id);
		in.setHeader("order_price", order_price);
		in.setHeader("service_time_duration", service_time_duration);
		in.setHeader("price_unit", price_unit);
		in.setHeader("service_type", service_type);
		in.setHeader("service_id", offerID);
		if (process_instance_id != null) {
		in.setHeader("process_instance_id", process_instance_id);
		}
		in.setHeader(Exchange.HTTP_METHOD,"POST");
	}

	private JSONArray parserOrderCharList(JSONArray charList) {
		if (charList == null || charList.isEmpty()) {
			return new JSONArray();
		}
		JSONArray retArray = new JSONArray();
		for (int i = 0; i < charList.size(); ++i) {
			JSONObject tempObj = new JSONObject();
			tempObj.put("param_key", charList.getJSONObject(i).getString("CHAR_NAME"));
			tempObj.put("param_id", charList.getJSONObject(i).getLong("CHAR_ID"));
			tempObj.put("param_value", charList.getJSONObject(i).getString("CHAR_VALUE"));
			retArray.add(tempObj);
		}
		return retArray;
	}
}

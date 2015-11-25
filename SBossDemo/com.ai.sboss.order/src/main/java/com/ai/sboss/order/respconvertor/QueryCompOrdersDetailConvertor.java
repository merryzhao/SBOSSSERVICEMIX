package com.ai.sboss.order.respconvertor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component("QueryCompOrdersDetailConvertor")
public class QueryCompOrdersDetailConvertor implements IBasicOutProcessor {
	private static final Log LOGGER = LogFactory.getLog(QueryCompOrdersDetailConvertor.class);

	private final static String RESULT_FMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":\"<RET_MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		QueryCompOrdersDetailConvertor.LOGGER.info("查询CRM订单详情,开始准备返回信息");

		//返回给客户端
		Message in = exchange.getIn();
		in.setHeaders(null);
		String ret = convert2requst(in.getBody(String.class));
		in.setHeader("Content-Type", "application/json");
		in.setBody(ret);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		if (!data.contains("hub_code")) {
			return RESULT_FMT.replace("<DATA>", new JSONArray().toString()).replace("<RET_CODE>", "0").replace("<RET_MSG>", "inner error");
		}
		JSONObject crmObj = JSONObject.fromObject(data);
		if (!StringUtils.equals("1", crmObj.getString("hub_code"))) {
			return RESULT_FMT.replace("<DATA>", new JSONArray().toString()).replace("<RET_CODE>", crmObj.getString("hub_code")).replace("<RET_MSG>", crmObj.getString("value"));
		}


		JSONObject crmOrderDetail = crmObj.getJSONObject("data");
		QueryCompOrdersDetailConvertor.LOGGER.info("======crm7============"+crmOrderDetail.toString());


		JSONObject orderDetail_out = new JSONObject();
		JSONArray  order_item_list_out  = new JSONArray() ;
//		JSONObject order_item_out = new JSONObject() ;
//        JSONArray  char_list_out = new JSONArray() ;
		 JSONObject char_out = new JSONObject() ;

		JSONArray orderItems = crmOrderDetail.getJSONArray("ORDER_ITEM") ;


		for(int number = 0 ; number < orderItems.size() ; number ++ ){
			JSONObject orderItem = JSONObject.fromObject(orderItems.get(number)) ;
			JSONArray charList = orderItem.getJSONArray("CHAR_LIST") ;
			JSONObject order_item_out = new JSONObject() ;
			 JSONArray  char_list_out = new JSONArray() ;
			for (int index = 0; index < charList.size(); index++) {
				JSONObject obj = JSONObject.fromObject(charList.get(index)) ;

				char_out.put("param_key",obj.getString("CHAR_ID"));
				char_out.put("param_name",obj.getString("CHAR_NAME"));
				char_out.put("param_value",obj.getString("CHAR_VALUE"));
				char_list_out.add(char_out);
			order_item_out.element("param_list", char_list_out) ;
			}


//			order_item_out.put("serv_name", orderItem.get("OFFER_NAME"));
			order_item_out.put("service_id", orderItem.get("OFFER_ID"));
			order_item_out.put("process_instance_id", orderItem.has("process_instance_id")?orderItem.getString("process_instance_id"):"-1");

			order_item_list_out.add(order_item_out) ;
		}


		QueryCompOrdersDetailConvertor.LOGGER.info("======charList_out============"+order_item_list_out.toString());

		orderDetail_out.element("order_item", order_item_list_out) ;

		orderDetail_out.put("order_number",crmOrderDetail.getString("ORDER_NUMBER"));
		orderDetail_out.put("order_state",crmOrderDetail.getString("ORDER_STATE"));
		orderDetail_out.put("order_state_name",crmOrderDetail.getString("ORDER_STATE_NAME"));
		orderDetail_out.put("order_create_time",crmOrderDetail.getString("ORDER_TIME"));
		orderDetail_out.put("customer_id",crmOrderDetail.getString("CUSTOMER_ID"));




		return RESULT_FMT.replace("<DATA>", orderDetail_out.toString()).replace("<RET_CODE>", Integer.toString(1)).replace("<RET_MSG>", "success");

	}

}

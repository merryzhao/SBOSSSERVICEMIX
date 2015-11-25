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

@Component("QueryOrdersDetailConvertor")
public class QueryOrdersDetailConvertor implements IBasicOutProcessor {
	private static final Log LOGGER = LogFactory.getLog(QueryOrdersDetailConvertor.class);

	private final static String RESULT_FMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":\"<RET_MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		QueryOrdersDetailConvertor.LOGGER.info("查询CRM订单详情,开始准备返回信息");

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

//		crmOrderDetail.getString("OFFER_NAME") ;
//		crmOrderDetail.getString("ORDER_STATE_NAME") ;
//		crmOrderDetail.getString("RESULT_CODE") ;
//		crmOrderDetail.getString("ORDER_PRICE") ;
//		crmOrderDetail.getString("ORDER_TIME") ;
//		crmOrderDetail.getString("CUSTOMER_ID") ;
//		crmOrderDetail.getString("SERVICE_TIME") ;
//		crmOrderDetail.getString("ERROR_MESSAGE") ;
//		crmOrderDetail.getString("ORDER_NUMBER") ;
//		crmOrderDetail.getString("PROVIDER_PORTRAIT_URL") ;
//		crmOrderDetail.getString("PROVIDER_ID") ;
//		crmOrderDetail.getString("ORDER_STATE") ;
//		crmOrderDetail.getString("OFFER_ID") ;
//		crmOrderDetail.getString("SERVICE_TYPE") ;
		JSONObject orderDetail = new JSONObject();
		JSONArray charList = crmOrderDetail.getJSONArray("CHAR_LIST");
		JSONArray charListFront = new JSONArray();
		for (int index = 0; index < charList.size(); index++) {
			JSONObject obj = JSONObject.fromObject(charList.get(index)) ;
            JSONObject charObj = new JSONObject();


            charObj.put("param_key",obj.getString("CHAR_ID"));
            charObj.put("param_name",obj.getString("CHAR_NAME"));
            charObj.put("param_value",obj.getString("CHAR_VALUE"));

            charListFront.add(charObj);
		}


		orderDetail.put("order_number",crmOrderDetail.getString("ORDER_NUMBER"));
		orderDetail.put("order_state",crmOrderDetail.getString("ORDER_STATE"));
		orderDetail.put("order_state_name",crmOrderDetail.getString("ORDER_STATE_NAME"));
		orderDetail.put("order_create_time",crmOrderDetail.getString("ORDER_TIME"));

		orderDetail.put("service_id",crmOrderDetail.getString("OFFER_ID"));
		orderDetail.put("provider_id",crmOrderDetail.getString("PROVIDER_ID"));
		orderDetail.put("customer_id",crmOrderDetail.getString("CUSTOMER_ID"));
		orderDetail.put("process_instance_id", crmOrderDetail.has("process_instance_id")?crmOrderDetail.getString("process_instance_id"):"-1");

		orderDetail.element("param_list", charListFront) ;

		return RESULT_FMT.replace("<DATA>", orderDetail.toString()).replace("<RET_CODE>", Integer.toString(1)).replace("<RET_MSG>", "success");

	}

}

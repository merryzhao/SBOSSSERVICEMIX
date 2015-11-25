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

@Component("queryOrdersListConvertor")
public class QueryOrdersListConvertor implements IBasicOutProcessor {
	private static final Log LOGGER = LogFactory.getLog(QueryOrdersListConvertor.class);

	private final static String RESULT_FMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":\"<RET_MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		QueryOrdersListConvertor.LOGGER.info("查询CRM订单详情/列表接口调用完毕，开始准备返回信息");

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
		JSONArray crmOrderList = crmObj.getJSONArray("data");
		JSONArray resultList = new JSONArray();
		for (int index = 0; index < crmOrderList.size(); ++index) {
			JSONObject obj = new JSONObject();
			JSONObject currentcrmObj = crmOrderList.getJSONObject(index);
			obj.put("order_id", currentcrmObj.getLong("ORDER_ID"));
			obj.put("service_id", currentcrmObj.getString("OFFER_ID"));
			obj.put("customer_id", currentcrmObj.getLong("CUSTOMER_ID"));

			obj.put("order_number", currentcrmObj.getLong("ORDER_NUMBER"));
			obj.put("order_state", currentcrmObj.getString("ORDER_STATE"));
			obj.put("order_state_name", currentcrmObj.getString("ORDER_STATE_NAME"));

		QueryOrdersListConvertor.LOGGER.info("查询CRM订单详情======="+currentcrmObj.getString("PROPOSAL_ID"));
        //  因为proposal_id有为空的情况，从 getLong方法改 为 getString()方法 实现
//		 obj.put("proposal_id", currentcrmObj.has("PROPOSAL_ID")?currentcrmObj.getString("PROPOSAL_ID"):-1);
//		 resultList.add(obj);
          // 因为proposal_id有为空的情况，返回的是number型
			try {
			  QueryOrdersListConvertor.LOGGER.info("查询333333333======="+currentcrmObj.getLong("ORDER_ID") );
			  QueryOrdersListConvertor.LOGGER.info("查询444444======="+currentcrmObj.getLong("PROPOSAL_ID"));
			  obj.put("proposal_id", currentcrmObj.has("PROPOSAL_ID")?currentcrmObj.getLong("PROPOSAL_ID"):-1L);
				resultList.add(obj);
			} catch (Exception e) {

				obj.put("proposal_id", -1L);
				resultList.add(obj);
			}

    	}
		return RESULT_FMT.replace("<DATA>", resultList.toString()).replace("<RET_CODE>", Integer.toString(1)).replace("<RET_MSG>", "success");

	}

}

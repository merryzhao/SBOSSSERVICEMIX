package com.ai.sboss.order.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;


/**
 * 该processor在查询CRM7的订单列表接口后，负责向调用的客户端返回相关的查询结构（取得查询数量）
 * @author yinwenjie
 */
@Component("queryOrderNumberConvertor")
public class QueryOrderNumberConvertor implements Processor {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(QueryOrderNumberConvertor.class);
	
	private final static String RESULT_FMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":\"<RET_MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	
	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		QueryOrderNumberConvertor.LOGGER.info("分组查询CRM订单列表数量 接口调用完毕，开始准备返回信息");
		Message in = exchange.getIn();
		
		//将CRM中的数据转为json
		JSONObject crmJson = this.getInputParam(exchange);
		JSONArray crmDataArray = null;
		try {
			crmDataArray = crmJson.getJSONArray("data");
		} catch(Exception e) {
			String ret = convert2requst("");
			in.setBody(ret);
			QueryOrderNumberConvertor.LOGGER.warn(e.getMessage(), e);
			return;
		}
		
		//转化成camel输出给客户端的信息
//		QueryOrderNumberConvertor.LOGGER.info("crmDataArray：" + crmDataArray.toString());
		if(crmDataArray == null || crmDataArray.size() == 0) {
			String ret = convert2requst("");
			in.setBody(ret);
			return;
		}
		JSONArray returnArray = new JSONArray();
		for(int index = 0 ; index < crmDataArray.size() ; index++) {
			JSONObject returnObject = new JSONObject();
			
			JSONObject crmDataItem = crmDataArray.getJSONObject(index);
			Integer order_num = crmDataItem.has("ORDER_NUM")?crmDataItem.getInt("ORDER_NUM"):0;
			String order_state = crmDataItem.has("ORDER_STATE")?crmDataItem.getString("ORDER_STATE"):"0";
			
			returnObject.put("order_num", order_num);
			returnObject.put("order_state", order_state);
			returnArray.add(returnObject);
		}
		
		//开始转换成结果
		String ret = convert2requst(returnArray.toString());
		in.setHeader("Content-Type", "application/json");
		in.setBody(ret);
	}
	
	/**
	 * 将从CRM得到的记过转为json，以便主方法只取得启动的data数据
	 * @param exchange
	 * @return
	 * @throws Exception
	 */
	private JSONObject getInputParam(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		JSONObject ret = JSONObject.fromObject(message.getBody(String.class));
		QueryOrderNumberConvertor.LOGGER.info("crm查询返回的数据为：" + ret.toString());
		return ret;
	}
	
	/**
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String convert2requst(String data) throws Exception {
		return RESULT_FMT.replace("<DATA>", data).replace("<RET_CODE>", Integer.toString(1)).replace("<RET_MSG>", "success");
	}
}

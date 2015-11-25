package com.ai.sboss.order.respconvertor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * 该processor在查询CRM7的订单详情、列表接口后，负责向调用的客户端返回相关的查询结果
 * @author yinwenjie
 * @author Chaos
 */
@Component("queryOrdersConvertor")
public class QueryOrdersConvertor implements IBasicOutProcessor {

	private static final Log LOGGER = LogFactory.getLog(QueryOrdersConvertor.class);

	private final static String RESULT_FMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":\"<RET_MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		QueryOrdersConvertor.LOGGER.info("查询CRM订单详情/列表接口调用完毕，开始准备返回信息");

		//返回给客户端
		Message in = exchange.getIn();
		String ret = convert2requst(in.getBody(String.class));
		in.setBody(ret);
	}

	/**
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@Override
	public String convert2requst(String data) throws Exception {
		if (data.contains("hub_code")) {
			JSONObject crmObj = JSONObject.fromObject(data);
			return RESULT_FMT.replace("<DATA>", new JSONArray().toString()).replace("<RET_CODE>", crmObj.getString("hub_code")).replace("<RET_MSG>", crmObj.getString("value"));
		}
		return RESULT_FMT.replace("<DATA>", data).replace("<RET_CODE>", Integer.toString(1)).replace("<RET_MSG>", "success");
	}
}

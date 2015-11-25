package com.ai.sboss.order.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

/**
 * 该processor用于构造 查询CRM7中订单详情 的参数信息，以便route的下一步能够正常调用CRM的订单详情查询接口
 * @author xiejun
 */
@Component("queryCompOrderDetailProcessor")
public class QueryCompOrderDetailProcessor implements IBasicInProcessor {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(QueryCompOrderDetailProcessor.class);

	private static final String CRM_QUERY = "servicecode=queryCompOrderDetail&WEB_HUB_PARAMS={\"data\":<DATA>,\"header\":{\"Content-Type\":\"application/json\"}}";

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject properIn = getInputParam(exchange);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, CRM_QUERY.replace("<DATA>", properIn.toString()));
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.common.interfaces.IBasicInProcessor#getInputParam(org.apache.camel.Exchange)
	 */
	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		JSONObject params = JSONObject.fromObject(message.getBody(String.class));
		//1、========得到来自于客户端的参数信息，并判断正确性
		Long customerOrderId = 0l;
		if (!params.containsKey("order_id")) {
			throw new IllegalArgumentException("order_id must be input!");
		} else {
			customerOrderId = params.getLong("order_id");
		}

		//2、========准备传输给crm接口的参数。实际上就是customerOrderId
		JSONObject crmqueryJsonObject = new JSONObject();
		crmqueryJsonObject.put("customerOrderId", customerOrderId.toString());
		QueryCompOrderDetailProcessor.LOGGER.info("===============准备开始调用CRM的queryCompOrderDetail接口===============");
		QueryCompOrderDetailProcessor.LOGGER.info("customerOrderId = " + customerOrderId);
		return crmqueryJsonObject;
	}

}

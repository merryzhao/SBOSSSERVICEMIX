package com.ai.sboss.proffer.processor;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

import net.sf.json.JSONObject;

public class FindServiceProcessTemplateProcessor implements IBasicInProcessor {
	private final String TEMPLATE = "{\"data\":<DATA>, \"desc\":\"\"}";
	private final static Log LOGGER = LogFactory.getLog(FindServiceProcessTemplateProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		JSONObject prepare = getInputParam(exchange);
		exchange.getIn().setHeaders(null);
		exchange.getIn().setBody(TEMPLATE.replace("<DATA>", prepare.toString()));
		LOGGER.info("请求JSON=>"+prepare.toString());
		
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		JSONObject inputparam = JSONObject.fromObject(exchange.getIn().getBody(String.class));
		if (!inputparam.has("process_id")) {
			//TODO: 这里后续需要用我们的异常来改写
			throw new IllegalArgumentException("服务流程ID process_id 必须传入！");
		}
		String arrangementId = inputparam.getString("process_id");
		JSONObject retJson = new JSONObject();
		retJson.put("arrangementId", arrangementId);
		return retJson;
	}

}

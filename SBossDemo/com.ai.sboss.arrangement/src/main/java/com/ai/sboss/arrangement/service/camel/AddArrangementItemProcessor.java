package com.ai.sboss.arrangement.service.camel;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.entity.JsonEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.service.IAddOps;
import com.ai.sboss.arrangement.utils.JSONUtils;

/**
 * 利用camel向外部提供的 "添加服务商户、客户自定义的流程模板" 的接口
 * @author yinwenjie
 *
 */
@Component("addArrangementItemProcessor")
public class AddArrangementItemProcessor implements IBasicInProcessor {
	
	/**
	 * 日志
	 */
	private static Log LOGGER = LogFactory.getLog(AddArrangementItemProcessor.class);
	
	@Autowired
	private IAddOps addOps;
	
	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		JsonEntity jsonEntity = null;
		try {
			//这个就是json中的data元素了
			JSONObject httpDateParams = this.getInputParam(exchange);
			jsonEntity = this.addOps.addDefineArrangementItem(httpDateParams);
		} catch(BizException e) {
			AddArrangementItemProcessor.LOGGER.error(e.getMessage(), e);
			jsonEntity = new JsonEntity();
			jsonEntity.setData("");
			jsonEntity.getDesc().setResult_msg(e.getMessage());
			jsonEntity.getDesc().setResult_code(ResponseCode._501);
		}
		
		//返回
		exchange.getIn().setBody(JSONUtils.toString(jsonEntity, new String[]{"parentArrangement","joint","arrangement","arrangementInstances"}));
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.camel.IBasicInProcessor#getInputParam(org.apache.camel.Exchange)
	 */
	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		JSONObject ret = JSONObject.fromObject(message.getBody(String.class));
		return ret;
	}

}

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
import com.ai.sboss.arrangement.service.IDeleteOps;
import com.ai.sboss.arrangement.utils.JSONUtils;

/**
 * 利用camel向外部提供的删除指定编排系统流程的接口<br>
 * 这个接口虽然提供给了客户端，但是建议客户端慎重调用
 * @author yinwenjie
 */
@Component("deleteArrangementItemProcessor")
public class DeleteArrangementItemProcessor implements IBasicInProcessor {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(QueryDefaultArrangementByTradeidProcessor.class);
	
	/**
	 * 整个编排系统最顶层暴露给客户端的服务接口
	 */
	@Autowired
	private IDeleteOps deleteOps;
	
	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		//1、================获取jointid信息
		String arrangementid = null; 
		JsonEntity jsonEntity = null;
		try {
			JSONObject httpDateParams = this.getInputParam(exchange);
			arrangementid = httpDateParams.getString("arrangementid");
			LOGGER.info("========得到参数：" + httpDateParams.toString());
		} catch(BizException e) {
			DeleteArrangementItemProcessor.LOGGER.error(e.getMessage(), e);
			jsonEntity = new JsonEntity();
			jsonEntity.setData("");
			jsonEntity.getDesc().setResult_msg(e.getMessage());
			jsonEntity.getDesc().setResult_code(ResponseCode._501);
		}
		
		//2、================调用数据服务层进行删除
		//TODO 这个服务调用在关系型数据库持久层的调用还没有实现，请技术人员尽快实现
		if(jsonEntity == null) {
			jsonEntity = this.deleteOps.deleteArrangementItem(arrangementid);
			LOGGER.info("========得到结果：" + jsonEntity.toString());
		}
		//返回
		exchange.getIn().setBody(JSONUtils.toString(jsonEntity));
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.camel.IBasicInProcessor#getInputParam(org.apache.camel.Exchange)
	 */
	@Override
	public JSONObject getInputParam(Exchange exchange) throws BizException {
		Message message = exchange.getIn();
		JSONObject ret = JSONObject.fromObject(message.getBody(String.class));
		return ret;
	}

}

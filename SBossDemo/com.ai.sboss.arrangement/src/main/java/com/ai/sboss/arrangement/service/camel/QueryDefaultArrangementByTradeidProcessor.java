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
import com.ai.sboss.arrangement.service.IQueryOps;
import com.ai.sboss.arrangement.utils.JSONUtils;

/**
 * 利用camel向外部提供的查询默认流程的接口
 * @author yinwenjie
 */
@Component("queryDefaultArrangementByTradeidProcessor")
public class QueryDefaultArrangementByTradeidProcessor implements IBasicInProcessor {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(QueryDefaultArrangementByTradeidProcessor.class);
	
	/**
	 * 整个编排系统最顶层暴露给客户端的服务接口
	 */
	@Autowired
	private IQueryOps queryOps;
	
	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		/*
		 * 1、由于从http过来的参数信息（json格式），已经由GeneralJettyInProcessor处理好了，所以只需要验证所需要的参数是不是都有
		 * 		特别是必要的条件参数是否传入(这里不需要报错，因为“2”的步骤已经包含了所有异常的处理)
		 * 2、直接调用queryOps中相应的方法得到查询结果，并且输出
		 * */
		//1、================
		String tradeid = null; 
		JsonEntity jsonEntity = null;
		try {
			JSONObject httpDateParams = this.getInputParam(exchange);
			tradeid = httpDateParams.getString("tradeid");
			LOGGER.info("========得到参数：" + httpDateParams.toString());
		} catch(BizException e) {
			QueryDefaultArrangementByTradeidProcessor.LOGGER.error(e.getMessage(), e);
			jsonEntity = new JsonEntity();
			jsonEntity.setData("");
			jsonEntity.getDesc().setResult_msg(e.getMessage());
			jsonEntity.getDesc().setResult_code(ResponseCode._501);
		}
		
		//2、================
		if(jsonEntity == null) {
			jsonEntity = this.queryOps.queryDefaultArrangementByTradeid(tradeid);
			LOGGER.info("========得到结果：" + jsonEntity.toString());
		}
		//返回
		exchange.getIn().setBody(JSONUtils.toString(jsonEntity , new String[]{"parentArrangement","outputParams","inputParams","trades","jointmapping","childArrangements","arrangementInstances"}));
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

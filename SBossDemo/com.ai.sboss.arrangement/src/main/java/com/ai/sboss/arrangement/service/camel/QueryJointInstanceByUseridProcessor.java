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
 * 利用camel向外部提供的  “查询和指定的用户相关的任务实例列表（用户是创建者或者是相关执行者），并按照创建的先后顺序返回” 的接口
 * @author yinwenjie
 *
 */
@Component("queryJointInstanceByUseridProcessor")
public class QueryJointInstanceByUseridProcessor implements IBasicInProcessor {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(QueryJointInstanceByUseridProcessor.class);
	
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
		//1、================
		String userid = null;
		Integer nowPage = null;
		Integer maxPageRows = null;
		JsonEntity jsonEntity = null;
		try {
			JSONObject httpDateParams = this.getInputParam(exchange);
			userid = httpDateParams.getString("userid");
			nowPage = httpDateParams.has("nowPage")?httpDateParams.getInt("nowPage"):null;
			maxPageRows = httpDateParams.has("maxPageRows")?httpDateParams.getInt("maxPageRows"):null;
		} catch(BizException e) {
			QueryJointInstanceByUseridProcessor.LOGGER.error(e.getMessage(), e);
			jsonEntity = new JsonEntity();
			jsonEntity.setData("");
			jsonEntity.getDesc().setResult_msg(e.getMessage());
			jsonEntity.getDesc().setResult_code(ResponseCode._501);
		}
		
		//2、================
		if(jsonEntity == null) {
			jsonEntity = this.queryOps.queryJointInstancesByUserid(userid, nowPage, maxPageRows);
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
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		JSONObject ret = JSONObject.fromObject(message.getBody(String.class));
		return ret;
	}
}

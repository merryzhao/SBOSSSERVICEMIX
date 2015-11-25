package com.ai.sboss.arrangement.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory;
import com.ai.sboss.arrangement.engine.dao.ArrangementDAOService;
import com.ai.sboss.arrangement.engine.dao.JointDAOService;
import com.ai.sboss.arrangement.entity.JsonEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.service.IDeleteOps;

/**
 * @author Chaos
 * @author yinwenjie
 */
@Component("concreteDeleteOps")
public class ConcreteDeleteOps implements IDeleteOps {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(ConcreteDeleteOps.class);
	
	/**
	 * 
	 */
	@Autowired
	private ArrangementDAOAbstractFactory arrangementDAOFactory;

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IDeleteOps#deleteJointItem(java.lang.String)
	 */
	@Override
	public JsonEntity deleteJointItem(String jointid) {
		JointDAOService jointDAOService = null;
		try {
			jointDAOService = this.arrangementDAOFactory.getJointDAOService();
			jointDAOService.deleteJoint(jointid);
		} catch (BizException e) {
			ConcreteDeleteOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//构造返回信息
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData("");
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IDeleteOps#deleteArrangementItem(java.lang.String)
	 */
	@Override
	public JsonEntity deleteArrangementItem(String arrangementid) {	
		ArrangementDAOService arrangementDAOService = null;
		try {
			arrangementDAOService = this.arrangementDAOFactory.getArrangementDAOService();
			arrangementDAOService.deleteArrangement(arrangementid);
		} catch (BizException e) {
			ConcreteDeleteOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//构造返回信息
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData("");
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IDeleteOps#deleteProcessById(java.lang.Object)
	 */
	@Override
	public JsonEntity deleteProcessById(Object processId) {
		//TODO 暂未实现
		return null;
	}
}
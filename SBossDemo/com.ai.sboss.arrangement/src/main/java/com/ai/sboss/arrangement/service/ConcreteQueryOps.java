package com.ai.sboss.arrangement.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory;
import com.ai.sboss.arrangement.engine.dao.ArrangementDAOService;
import com.ai.sboss.arrangement.engine.dao.InstanceDAOService;
import com.ai.sboss.arrangement.engine.dao.JointDAOService;
import com.ai.sboss.arrangement.entity.JsonEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.utils.JSONUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Chaos
 * @author yinwenjie
 */
@Component("concreteQueryOps")
public class ConcreteQueryOps implements IQueryOps {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(ConcreteQueryOps.class);
	
	/**
	 * 
	 */
	@Autowired
	private ArrangementDAOAbstractFactory arrangementDAOFactory;
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IQueryOps#queryDefaultArrangementByTradeid(java.lang.String)
	 */
	@Override
	public JsonEntity queryDefaultArrangementByTradeid(String tradeid) {
		ArrangementDAOService arrangementDAOService = this.arrangementDAOFactory.getArrangementDAOService();
		List<ArrangementEntity> result = null;
		try {
			result = arrangementDAOService.queryArrangementByTradeidWithoutSet(tradeid, "industry");
		} catch (BizException e) {
			ConcreteQueryOps.LOGGER.error(e.getMessage() , e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(result);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IQueryOps#getProcessByCustomerId(java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public JsonEntity getProcessByCustomerId(Object creator, String tradeid, String scope) {
		//TODO 暂未实现
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData("暂未实现");
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IQueryOps#queryJointByTradeid(java.lang.String, java.lang.String)
	 */
	@Override
	public JsonEntity queryJointByTradeid(String tradeid, String scope) {
		JointDAOService jointDAOService = this.arrangementDAOFactory.getJointDAOService();
		List<JointEntity> result = null;
		try {
			result = jointDAOService.queryJointByTradeid(tradeid, scope);
		} catch (BizException e) {
			ConcreteQueryOps.LOGGER.error(e.getMessage() , e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//先进行转换，因为外部不需要关心哪些字段不能转换
		JSONArray resultJSON = JSONUtils.toJSONArray(result, new String[]{"jointinstances","arrangementJointMappings","outputParams","inputParams","outputParams"});
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(resultJSON);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IQueryOps#queryJointBybusinessID(java.lang.String)
	 */
	@Override
	public JsonEntity queryJointBybusinessID(String businessid) throws BizException {
		JointDAOService jointDAOService = this.arrangementDAOFactory.getJointDAOService();
		List<JointEntity> result = null;
		try {
			result = jointDAOService.queryJointBybusinessID(businessid);
		} catch (BizException e) {
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//先进行转换，因为外部不需要关心哪些字段不能转换
		JSONArray resultJSON = JSONUtils.toJSONArray(result, new String[]{"jointinstances","arrangementJointMappings","outputParams","inputParams","outputParams","trades"});
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(resultJSON);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IQueryOps#queryArrangementInstanceByArrangementInstanceID(java.lang.String)
	 */
	@Override
	public JsonEntity queryArrangementInstanceByArrangementInstanceID(String arrangementInstanceuid) {
		InstanceDAOService instanceDAOService = this.arrangementDAOFactory.getInstanceDAOService();
		JSONObject queryResult = null ;
		try {
			queryResult = instanceDAOService.queryArrangementInstanceByArrangementInstanceID(arrangementInstanceuid);
		} catch (BizException e) {
			ConcreteQueryOps.LOGGER.error(e.getMessage() , e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(queryResult);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IQueryOps#queryMappingJointByTradeid(java.lang.String)
	 */
	@Override
	public JsonEntity queryMappingJointByTradeid(String tradeid) throws BizException {
		JointDAOService jointDAOService = this.arrangementDAOFactory.getJointDAOService();
		List<JointEntity> result = null;
		try {
			result = jointDAOService.queryMappingJointByTradeid(tradeid);
		} catch (BizException e) {
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//先进行转换，因为外部不需要关心哪些字段不能转换
		JSONArray resultJSON = JSONUtils.toJSONArray(result, new String[]{"jointinstances","arrangementJointMappings","outputParams","inputParams","outputParams","trades"});
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(resultJSON);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IQueryOps#queryArrangementInstanceByBusinessID(java.lang.String)
	 */
	@Override
	public JsonEntity queryArrangementInstanceByBusinessID(String businessid) {
		InstanceDAOService instanceDAOService = this.arrangementDAOFactory.getInstanceDAOService();
		JSONObject queryResult = null ;
		try {
			queryResult = instanceDAOService.queryArrangementInstanceByBusinessID(businessid);
		} catch (BizException e) {
			ConcreteQueryOps.LOGGER.error(e.getMessage() , e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(queryResult);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}

	/* (non-Javadoc)
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IQueryOps#queryJointInstancesByUserid(java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public JsonEntity queryJointInstancesByUserid(String userid, Integer nowPage, Integer maxPageRows) {
		InstanceDAOService instanceDAOService = this.arrangementDAOFactory.getInstanceDAOService();
		//注意这个JSONObject是一个分页信息对象
		JSONObject queryResult = null ;
		try {
			queryResult = instanceDAOService.queryJointInstancesByUserid(userid, nowPage, maxPageRows);
		} catch (BizException e) {
			ConcreteQueryOps.LOGGER.error(e.getMessage() , e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(queryResult);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IQueryOps#queryJointInstancesByBusinessID(java.lang.String)
	 */
	@Override
	public JsonEntity queryJointInstancesByBusinessID(String businessID) {
		InstanceDAOService instanceDAOService = this.arrangementDAOFactory.getInstanceDAOService();
		//注意这个JSONObject是一个分页信息对象
		JSONArray queryResult = null ;
		try {
			queryResult = instanceDAOService.queryJointInstancesByBusinessID(businessID);
		} catch (BizException e) {
			ConcreteQueryOps.LOGGER.error(e.getMessage() , e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(queryResult);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}

	@Override
	public JsonEntity queryArrangementByArrangementid(String arrangementId) {
		ArrangementDAOService arrangementDAOService = this.arrangementDAOFactory.getArrangementDAOService();
		JSONObject queryResult = null ;
		ArrangementEntity queryEntity = null;
		try {
			//NOTE：这里的queryEntity，不能做二次查询，因为其和DAO层关联太紧，这里又是在事务外面。
			queryEntity = arrangementDAOService.getArrangementWithSet(arrangementId);
			//TODO:暂不考虑子流程
			queryResult = JSONUtils.toJSONObject(queryEntity, new String[]{"childArrangements",
																			"arrangementInstances",
																			"parentArrangement",
																			"inputParams", 
																			"outputParams", 
																			"trades", 
																			"jointinstances", 
																			"arrangementJointMappings"
																			});
		} catch (BizException e) {
			ConcreteQueryOps.LOGGER.error(e.getMessage() , e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(queryResult);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}

	@Override
	public JsonEntity queryArrangementInstanceByArrangementInstanceIDWithSet(String arrangementInstanceuid) {
		InstanceDAOService instanceDAOService = this.arrangementDAOFactory.getInstanceDAOService();
		JSONObject queryArrangement = null ;
		JSONArray queryJointFlows = null ;
		try {
			queryArrangement = instanceDAOService.queryArrangementInstanceByArrangementInstanceID(arrangementInstanceuid);
			queryJointFlows = instanceDAOService.querySortedJointInstanceFlowByArrangementInstanceId(arrangementInstanceuid);
			if (queryArrangement != null) {
				queryArrangement.put("jointflows", queryJointFlows);
			}
		} catch (BizException e) {
			ConcreteQueryOps.LOGGER.error(e.getMessage() , e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(queryArrangement);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}

	@Override
	public JsonEntity queryArrangementInstanceByBusinessIDWithSet(String businessid) {
		InstanceDAOService instanceDAOService = this.arrangementDAOFactory.getInstanceDAOService();
		JSONObject queryArrangement = null ;
		JSONArray queryJointFlows = null ;
		try {
			queryArrangement = instanceDAOService.queryArrangementInstanceByBusinessID(businessid);
			if (queryArrangement != null && queryArrangement.has("uid")) {
				queryJointFlows = instanceDAOService.querySortedJointInstanceFlowByArrangementInstanceId(queryArrangement.getString("uid"));
				queryArrangement.put("jointflows", queryJointFlows);
			}
		} catch (BizException e) {
			ConcreteQueryOps.LOGGER.error(e.getMessage() , e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(queryArrangement);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}
}
package com.ai.sboss.arrangement.service;

import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory;
import com.ai.sboss.arrangement.engine.dao.ArrangementDAOService;
import com.ai.sboss.arrangement.engine.dao.JointDAOService;
import com.ai.sboss.arrangement.entity.JsonEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;
import com.ai.sboss.arrangement.service.IAddOps;
import com.ai.sboss.arrangement.translation.ArrangementTranslationService;
import com.ai.sboss.arrangement.translation.JointTranslationService;
import com.ai.sboss.arrangement.translation.TranslationAbstractFactory;

/**
 * @author Chaos
 * @author yinwenjie 
 */
@Component("concreteAddOps")
public class ConcreteAddOps implements IAddOps{
	
	/**
	 * 
	 */
	private static final Log LOGGER = LogFactory.getLog(ConcreteAddOps.class);
	
	/**
	 * 
	 */
	@Autowired
	private TranslationAbstractFactory translationFactory;
	
	/**
	 * 
	 */
	@Autowired
	private ArrangementDAOAbstractFactory arrangementDAOFactory;
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IAddOps#addIndustryTemplate(java.lang.String)
	 */
	public JsonEntity addDefaultArrangementItem(String xmlText) {
		/*
		 * 操作步骤首先是:
		 * 1、通过ArrangementXMLTranslationService将xml信息解析成相关的ArrangementEntity
		 * 		注意，这时除了arrangement基本信息本身不带uuid以外，其他信息都带了uuid
		 * 
		 * =============================================================
		 * （以下过程在私有方法中重用）
		 * 2、进行判断，如果Arrangement不是“某个行业”默认的流程模板，则报错
		 * 		还要判断arrangement-joint-mapping中的绝对偏移和相对偏移信息：
		 *  	以上的偏移信息由用户生成流程模板时传入，如果没有传入，则以joint上面所设置的默认偏移量为准
		 * 
		 * 3、通过持久层操作，将这个信息插入数据库
		 * 
		 * 4、构造返回信息的jsonEntity对象
		 * ==============================================================
		 * */
		ArrangementTranslationService arrangementTranslationService = null;
		ArrangementEntity arrangementEntity = null;
		try {
			arrangementTranslationService = this.translationFactory.getArrangementTranslationService();
			arrangementEntity = arrangementTranslationService.translationEntity(xmlText);
			return this.reusingAddDefaultArrangementItem(arrangementEntity);
		} catch(BizException e) {
			ConcreteAddOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IAddOps#addDefaultArrangementItem(net.sf.json.JSONObject)
	 */
	@Override
	public JsonEntity addDefaultArrangementItem(JSONObject arrangementjson) {
		/*
		 * 操作步骤首先是:
		 * 1、通过ArrangementJSONTranslationService将json信息解析成相关的ArrangementEntity
		 * 		注意，这时除了arrangement基本信息本身不带uuid以外，其他信息都带了uuid
		 * 
		 * =============================================================
		 * （以下过程在私有方法中重用）
		 * 2、进行判断，如果Arrangement不是“某个行业”默认的流程模板，则报错
		 * 		还要判断arrangement-joint-mapping中的绝对偏移和相对偏移信息：
		 *  	以上的偏移信息由用户生成流程模板时传入，如果没有传入，则以joint上面所设置的默认偏移量为准
		 * 
		 * 3、通过持久层操作，将这个信息插入数据库
		 * 
		 * 4、构造返回信息的jsonEntity对象
		 * ==============================================================
		 * 
		 * */
		ArrangementTranslationService arrangementJSONTranslationService = null;
		ArrangementEntity arrangementEntity = null;
		try {
			arrangementJSONTranslationService = this.translationFactory.getArrangementTranslationService();
			arrangementEntity = arrangementJSONTranslationService.translationEntity(arrangementjson);
			return this.reusingAddDefaultArrangementItem(arrangementEntity);
		} catch(BizException e) {
			ConcreteAddOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
	}
	
	/**
	 * 重用的“添加默认业务流程”的私有方法
	 * @param arrangementEntity
	 * @throws BizException
	 */
	private JsonEntity reusingAddDefaultArrangementItem(ArrangementEntity arrangementEntity) {
		//如果条件成立，说明不是行业默认的模板
		try {
			String tradeid = arrangementEntity.getTradeid();
			if(!StringUtils.endsWithIgnoreCase(arrangementEntity.getTradeScope(), "industry")) {
				throw new BizException("这个模板经过解析，并不是行业" + tradeid + "的默认模板，请检查（industry）", ResponseCode._405);
			}
		} catch(BizException e) {
			ConcreteAddOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//开始插入
		return this.reusingInsertArrangementItem(arrangementEntity);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IAddOps#addDefineArrangementItem(net.sf.json.JSONObject)
	 */
	@Override
	public JsonEntity addDefineArrangementItem(JSONObject arrangementjson) {
		/*
		 * 操作步骤首先是:
		 * 1、通过ArrangementJSONTranslationService将json信息解析成相关的ArrangementEntity
		 * 		注意，这时除了arrangement基本信息本身不带uuid以外，其他信息都带了uuid
		 * 
		 * =============================================================
		 * （以下过程在私有方法中重用）
		 * 2、进行判断，如果Arrangement不是“某个行业”自定义的流程模板，则报错
		 * 	还要判断arrangement-joint-mapping中的绝对偏移和相对偏移信息：
		 *  以上的偏移信息由用户生成流程模板时传入，如果没有传入，则以joint上面所设置的默认偏移量为准
		 * 
		 * 3、通过持久层操作，将这个信息插入数据库
		 * 
		 * 4、构造返回信息的jsonEntity对象
		 * ==============================================================
		 * 
		 * */
		ArrangementTranslationService arrangementJSONTranslationService = null;
		ArrangementEntity arrangementEntity = null;
		try {
			arrangementJSONTranslationService = this.translationFactory.getArrangementTranslationService();
			arrangementEntity = arrangementJSONTranslationService.translationEntity(arrangementjson);
			return this.reusingAddDefineArrangementItem(arrangementEntity);
		} catch(BizException e) {
			ConcreteAddOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
	}
	
	/**
	 * 重用的“添加服务商户、客户自定义的流程模板”的私有方法
	 * @param arrangementEntity
	 * @throws BizException
	 */
	private JsonEntity reusingAddDefineArrangementItem(ArrangementEntity arrangementEntity) {
		//如果条件成立，说明不是行业默认的模板
		try {
			String tradeid = arrangementEntity.getTradeid();
			if(!StringUtils.endsWithIgnoreCase(arrangementEntity.getTradeScope(), "producer")
				&& !StringUtils.endsWithIgnoreCase(arrangementEntity.getTradeScope(), "consumer")) {
				throw new BizException("这个模板经过解析，并不是行业" + tradeid + "的自定义模板，请检查（producer | consumer）", ResponseCode._405);
			}
		} catch(BizException e) {
			ConcreteAddOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		return this.reusingInsertArrangementItem(arrangementEntity);
	} 
	
	/**
	 * 重用“插入新的流程”的操作。无论这个流程模板是行业模板还是自定义模板
	 * @return
	 * @throws BizException
	 */
	private JsonEntity reusingInsertArrangementItem(ArrangementEntity arrangementEntity) {
		//还要判断arrangement-joint-mapping中的绝对偏移和相对偏移信息：
		//以上的偏移信息由用户生成流程模板时传入，如果没有传入，则以joint上面所设置的默认偏移量为准
		Set<ArrangementJointMappingEntity> jointMappingEntitys = arrangementEntity.getJointmapping();
		if(jointMappingEntitys == null || jointMappingEntitys.isEmpty()) {
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg("请至少设置一个arrangement-joint的关联。");
			returnJson.getDesc().setResult_code(ResponseCode._404);
			return returnJson;
		}
		for (ArrangementJointMappingEntity arrangementJointItem : jointMappingEntitys) {
			String jointuid = arrangementJointItem.getJoint().getUid();
			Long relateOffsettime = arrangementJointItem.getRelateOffsettime();
			Long absOffsettime = arrangementJointItem.getAbsOffsettime();
			
			//说明没有设置relateOffsettime或者没有设置absOffsettime。要查询出joint上面的默认偏移信息
			JointEntity jointEntity = null;
			if(relateOffsettime == null || absOffsettime == null) {
				JointDAOService jointDAOService = this.arrangementDAOFactory.getJointDAOService();
				 try {
					 //TODO 这里的查询方式还要进行优化，最好一次都查询出来
					 jointEntity = jointDAOService.getJointWithoutParams(jointuid);
				} catch (BizException e) {
					ConcreteAddOps.LOGGER.error(e.getMessage(), e);
					JsonEntity returnJson = new JsonEntity();
					returnJson.setData("");
					returnJson.getDesc().setResult_msg(e.getMessage());
					returnJson.getDesc().setResult_code(e.getResponseCode());
					return returnJson;
				}
			} else {
				continue;
			}
			
			//如果条件成立，说明没有查到对应的jointEntity，说明关联关系错误。抛出异常
			if(jointEntity == null) {
				JsonEntity returnJson = new JsonEntity();
				returnJson.setData("");
				returnJson.getDesc().setResult_msg("没有发现对应的任务模板，请检查");
				returnJson.getDesc().setResult_code(ResponseCode._404);
				return returnJson;
			}
			
			//重新赋予默认值
			if(relateOffsettime == null) {
				arrangementJointItem.setRelateOffsettime(jointEntity.getRelateOffsettime());
			}
			if(absOffsettime == null) {
				arrangementJointItem.setAbsOffsettime(jointEntity.getAbsOffsettime());
			}
		}
		
		//开始插入
		try {
			ArrangementDAOService arrangementDAOService = this.arrangementDAOFactory.getArrangementDAOService();
			arrangementDAOService.createArrangement(arrangementEntity);
		} catch (BizException e) {
			ConcreteAddOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(arrangementEntity);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IAddOps#addTaskItem(java.lang.String)
	 */
	public JsonEntity addJointItem(String xmlText) {
		/*
		 * 操作步骤首先是:
		 * 1、通过JointXMLTranslationService将xml信息解析成相关的jointEntity
		 * 		注意，这时除了joint基本信息本身不带uuid以外，其他信息都带了uuid
		 * 
		 * =============================================================
		 * （以下过程在私有方法中重用）
		 * 2、通过持久层操作，将这个信息插入数据库
		 * 
		 * 3、构造返回信息的jsonEntity对象
		 * ==============================================================
		 * */
		JointTranslationService jointTranslationService = null;
		JointEntity jointEntity = null;
		try {
			jointTranslationService = this.translationFactory.getJointTranslationService();
			jointEntity = jointTranslationService.translationEntity(xmlText);
		} catch (BizException e) {
			ConcreteAddOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//开始插入
		return this.reusingInsertJointItem(jointEntity);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.service.IAddOps#addJointItem(net.sf.json.JSONObject)
	 */
	@Override
	public JsonEntity addJointItem(JSONObject jointjson) {
		/*
		 * 操作步骤首先是:
		 * 1、通过JointJSONTranslationService将json信息解析成相关的JointEntity
		 * 		注意，这时除了joint基本信息本身不带uuid以外，其他信息都带了uuid
		 * 
		 * =============================================================
		 * （以下过程在私有方法中重用）
		 * 2、通过持久层操作，将这个信息插入数据库
		 * 
		 * 3、构造返回信息的jsonEntity对象
		 * ==============================================================
		 * */
		JointTranslationService jointTranslationService = null;
		JointEntity jointEntity = null;
		try {
			jointTranslationService = this.translationFactory.getJointTranslationService();
			jointEntity = jointTranslationService.translationEntity(jointjson);
		} catch (BizException e) {
			ConcreteAddOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//开始插入
		return this.reusingInsertJointItem(jointEntity);
	}
	
	/**
	 * 重用“插入新的任务”的操作。
	 * @return 
	 * @throws BizException
	 */
	private JsonEntity reusingInsertJointItem(JointEntity jointEntity) {
		try {
			JointDAOService jointDAOService = this.arrangementDAOFactory.getJointDAOService();
			jointDAOService.createJoint(jointEntity);
		} catch (BizException e) {
			ConcreteAddOps.LOGGER.error(e.getMessage(), e);
			JsonEntity returnJson = new JsonEntity();
			returnJson.setData("");
			returnJson.getDesc().setResult_msg(e.getMessage());
			returnJson.getDesc().setResult_code(e.getResponseCode());
			return returnJson;
		}
		
		//构造返回结果
		JsonEntity returnJson = new JsonEntity();
		returnJson.setData(jointEntity);
		returnJson.getDesc().setResult_msg("");
		returnJson.getDesc().setResult_code(ResponseCode._200);
		return returnJson;
	}
}

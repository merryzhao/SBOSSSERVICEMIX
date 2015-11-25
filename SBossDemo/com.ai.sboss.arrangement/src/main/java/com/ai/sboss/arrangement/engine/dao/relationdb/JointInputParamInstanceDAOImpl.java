package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author chaos
 * @author yinwenjie
 */
@Component("jointInputParamInstanceDAOImpl")
public class JointInputParamInstanceDAOImpl extends AbstractRelationalDBDAO<JointInputParamsInstanceEntity> implements IJointInputParamsInstanceDAO {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(JointInputParamInstanceDAOImpl.class);
	
	@Override
	public JointInputParamsInstanceEntity createInputParamsInstance(
			JointInputParamsInstanceEntity inputParamInstance) throws BizException {
		if (!checkValid(inputParamInstance)) {
			throw new BizException("入参实例inputParamInstance参数格式非法，请检查", ResponseCode._402);
		}

		if (StringUtils.isEmpty(inputParamInstance.getUid())) {
			inputParamInstance.setUid(UUID.randomUUID().toString());
		}

		this.insert(inputParamInstance);
		return inputParamInstance;
	}

	@Override
	public void updateInputParamsInstance(JointInputParamsInstanceEntity inputParamInstance)
			throws BizException {
		if (inputParamInstance == null) {
			throw new BizException("传入入参实例不能为空", ResponseCode._401);
		}

		if (!checkValid(inputParamInstance) || StringUtils.isEmpty(inputParamInstance.getUid())) {
			throw new BizException("inputParamInstance参数传入非法。", ResponseCode._402);
		}
		this.update(inputParamInstance);
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInputParamsInstanceDAO#queryInputParamsInstanceByJointInstanceID(java.lang.String, java.lang.Boolean)
	 */
	@Override
	public List<JointInputParamsInstanceEntity> queryInputParamsInstanceByJointInstanceID(String jointInstanceid, Boolean required) throws BizException {
		if (StringUtils.isEmpty(jointInstanceid)) {
			throw new BizException("传入任务实例ID不能为空", ResponseCode._401);
		}
		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("jointInstanceuid", jointInstanceid);
		if (required != null) {
			conditionMap.put("required", required);
		}

		List<JointInputParamsInstanceEntity> results = null;
		results = this.queryByHqlFile("IJointInputParamsInstanceDAO.queryInputParamsInstanceByJointInstanceID",conditionMap);
		
		return results;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInputParamsInstanceDAO#queryInputParamsInstanceByName(java.lang.String, java.lang.String)
	 */
	@Override
	public JointInputParamsInstanceEntity queryInputParamsInstanceByName(String jointInstanceid, String name) throws BizException {
		if (StringUtils.isEmpty(jointInstanceid)) {
			throw new BizException("传入任务实例ID不能为空", ResponseCode._401);
		}
		if (StringUtils.isEmpty(name)) {
			throw new BizException("参数英文名称不能为空", ResponseCode._401);
		}
		
		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("jointInstanceid", jointInstanceid);
		conditionMap.put("name", name);
		
		List<JointInputParamsInstanceEntity> results = null;
		results = this.queryByHqlFile("IJointInputParamsInstanceDAO.queryInputParamsInstanceByName",conditionMap);
		
		if(results == null || results.isEmpty()) {
			return null;
		} else {
			return results.get(0);
		}
	}

	private boolean checkValid(JointInputParamsInstanceEntity inputParamInstance) {
		if (inputParamInstance == null) {
			JointInputParamInstanceDAOImpl.LOGGER.error("传入入参实例对象不能为空");
			return false;
		}

		boolean ret = true;
		ret = ret && !StringUtils.isEmpty(inputParamInstance.getName());
		ret = ret && !StringUtils.isEmpty(inputParamInstance.getType());
		ret = ret && (StringUtils.equals(inputParamInstance.getType(), "String")
				|| StringUtils.equals(inputParamInstance.getType(), "Boolean")
				|| StringUtils.equals(inputParamInstance.getType(), "Integer")
				|| StringUtils.equals(inputParamInstance.getType(), "Long")
				|| StringUtils.equals(inputParamInstance.getType(), "Float")
				|| StringUtils.equals(inputParamInstance.getType(), "Double")
				|| StringUtils.equals(inputParamInstance.getType(), "JSON")
				|| StringUtils.equals(inputParamInstance.getType(), "XML")
				|| StringUtils.equals(inputParamInstance.getType(), "Date"));
		ret = ret && !StringUtils.isEmpty(inputParamInstance.getDisplayType());
		ret = ret && !StringUtils.isEmpty(inputParamInstance.getDisplayName());
		ret = ret && (inputParamInstance.getJointInstance() != null);
		ret = ret && (inputParamInstance.getJointInputParam() != null);
		return ret;
	}

	@Override
	protected Class<JointInputParamsInstanceEntity> getEntityClass() {
		return JointInputParamsInstanceEntity.class;
	}

	@Override
	public void deleteInputParamsInstancesByJointInstanceID(String jointInstanceid)
			throws BizException {
		if (StringUtils.isEmpty(jointInstanceid)) {
			throw new BizException("传入任务实例ID不能为空", ResponseCode._401);
		}
		
		List<JointInputParamsInstanceEntity> inputParamInstances = queryInputParamsInstanceByJointInstanceID(jointInstanceid, null);
		if (inputParamInstances == null) {
			return;
		}
		for (JointInputParamsInstanceEntity entity:inputParamInstances) {
			deleteInputParamInstanceByInstanceID(entity.getUid());
		}
	}

	@Override
	public void deleteInputParamInstanceByInstanceID(String inputParamInstanceid) throws BizException {
		if (StringUtils.isEmpty(inputParamInstanceid)) {
			throw new BizException("传入入参实例ID不能为空", ResponseCode._401);
		}
		// 删除记录
		this.delete(inputParamInstanceid);
	}
}

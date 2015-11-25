package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author chaos
 */
@Component("jointOutputParamInstanceDAOImpl")
public class JointOutputParamInstanceDAOImpl extends AbstractRelationalDBDAO<JointOutputParamsInstanceEntity> implements IJointOutputParamsInstanceDAO {

	private boolean checkValid(JointOutputParamsInstanceEntity outputParamInstance) {
		if (outputParamInstance == null) {
			return false;
		}

		boolean ret = true;
		ret = ret && !StringUtils.isEmpty(outputParamInstance.getName());
		ret = ret && !StringUtils.isEmpty(outputParamInstance.getType());
		ret = ret && (StringUtils.equals(outputParamInstance.getType(), "String")
				|| StringUtils.equals(outputParamInstance.getType(), "Boolean")
				|| StringUtils.equals(outputParamInstance.getType(), "Integer")
				|| StringUtils.equals(outputParamInstance.getType(), "Long")
				|| StringUtils.equals(outputParamInstance.getType(), "Float")
				|| StringUtils.equals(outputParamInstance.getType(), "Double")
				|| StringUtils.equals(outputParamInstance.getType(), "JSON")
				|| StringUtils.equals(outputParamInstance.getType(), "XML")
				|| StringUtils.equals(outputParamInstance.getType(), "Date"));
		ret = ret && (outputParamInstance.getJointInstance() != null);
		ret = ret && (outputParamInstance.getJointOutputParam() != null);

		return ret;
	}

	@Override
	public JointOutputParamsInstanceEntity createOutputParamsInstance(
			JointOutputParamsInstanceEntity outputParamInstance) throws BizException {
		if (!checkValid(outputParamInstance)) {
			throw new BizException("出参实例outputParamInstance参数格式非法，请检查", ResponseCode._402);
		}

		if (StringUtils.isEmpty(outputParamInstance.getUid())) {
			outputParamInstance.setUid(UUID.randomUUID().toString());
		}

		this.insert(outputParamInstance);
		return outputParamInstance;
	}

	@Override
	public void updateInputParamsInstance(JointOutputParamsInstanceEntity outputParamInstance)
			throws BizException {
		if (outputParamInstance == null) {
			throw new BizException("传入出参实例不能为空", ResponseCode._401);
		}

		if (!checkValid(outputParamInstance) || StringUtils.isEmpty(outputParamInstance.getUid())) {
			throw new BizException("inputParamInstance参数传入非法。", ResponseCode._402);
		}
		this.update(outputParamInstance);
	}

	@Override
	public List<JointOutputParamsInstanceEntity> queryOutputParamsInstanceByJointInstanceID(String jointInstanceid, Boolean required) throws BizException {
		if (StringUtils.isEmpty(jointInstanceid)) {
			throw new BizException("传入任务实例ID不能为空", ResponseCode._401);
		}
		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("jointInstanceuid", jointInstanceid);
		if (required != null) {
			conditionMap.put("required", required);
		}

		List<JointOutputParamsInstanceEntity> results = null;
		results = this.queryByHqlFile("IJointOutputParamsInstanceDAO.queryOutputParamsInstanceByJointInstanceID",conditionMap);
		
		return results;
	}

	@Override
	protected Class<JointOutputParamsInstanceEntity> getEntityClass() {
		return JointOutputParamsInstanceEntity.class;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointOutputParamsInstanceDAO#deleteOutputParamsInstancesByJointInstanceID(java.lang.String)
	 */
	@Override
	public void deleteOutputParamsInstancesByJointInstanceID(String jointInstanceid) throws BizException {
		if (StringUtils.isEmpty(jointInstanceid)) {
			throw new BizException("传入任务实例ID不能为空", ResponseCode._401);
		}
		
		List<JointOutputParamsInstanceEntity> outputParamInstances = queryOutputParamsInstanceByJointInstanceID(jointInstanceid, null);
		if (outputParamInstances == null) {
			return;
		}
		for (JointOutputParamsInstanceEntity entity:outputParamInstances) {
			deleteOutputParamInstanceByInstanceID(entity.getUid());
		}
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointOutputParamsInstanceDAO#deleteOutputParamInstanceByInstanceID(java.lang.String)
	 */
	@Override
	public void deleteOutputParamInstanceByInstanceID(String outputParamInstanceid) throws BizException {
		if (StringUtils.isEmpty(outputParamInstanceid)) {
			throw new BizException("传入出参实例ID不能为空", ResponseCode._401);
		}
		// 删除记录
		this.delete(outputParamInstanceid);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointOutputParamsInstanceDAO#queryInputParamsInstanceByName(java.lang.String, java.lang.String)
	 */
	@Override
	public JointOutputParamsInstanceEntity queryOutputParamsInstanceByName(String jointInstanceid, String name) throws BizException {
		if (StringUtils.isEmpty(jointInstanceid)) {
			throw new BizException("传入任务实例ID不能为空", ResponseCode._401);
		}
		if (StringUtils.isEmpty(name)) {
			throw new BizException("传入参数名称不能为空", ResponseCode._401);
		}
		
		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("jointInstanceid", jointInstanceid);
		conditionMap.put("name", name);

		List<JointOutputParamsInstanceEntity> results = null;
		results = this.queryByHqlFile("IJointOutputParamsInstanceDAO.queryOutputParamsInstanceByName",conditionMap);
		
		if(results == null || results.isEmpty()) {
			return null;
		} else {
			return results.get(0);
		}
	}
}
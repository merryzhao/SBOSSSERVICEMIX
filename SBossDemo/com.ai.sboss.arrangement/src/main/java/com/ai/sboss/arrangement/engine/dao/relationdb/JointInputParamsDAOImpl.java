package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author chaos
 */
@Component("jointInputParamsDAOImpl")
public class JointInputParamsDAOImpl extends AbstractRelationalDBDAO<JointInputParamsEntity> implements IJointInputParamsDAO {

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO#getEntityClass()
	 */
	@Override
	protected Class<JointInputParamsEntity> getEntityClass() {
		return JointInputParamsEntity.class;
	}

	@Override
	public List<JointInputParamsEntity> queryInputParamsByjointuid(String jointuid) throws BizException {
		if (StringUtils.isEmpty(jointuid)) {
			throw new BizException("错误的任务编号jointuid参数，请检查", ResponseCode._402);
		}

		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("jointuid", jointuid);

		// 获取记录
		List<JointInputParamsEntity> result = null;
		result = this.queryByHqlFile("IJointInputParamsDAO.queryInputParamsByjointuid", conditionMap);

		return result;
	}

	@Override
	public void bindJointInputParams(String jointuid, Set<JointInputParamsEntity> inputParams) throws BizException {
		if (inputParams == null) {
			throw new BizException("错误的空inputParams参数，请检查", ResponseCode._402);
		}

		for (JointInputParamsEntity newJointInputParamsEntity : inputParams) {
			// 插入新节点
			// 合法性检查 
			// 检查inputParam对应的parentJoint是不是对应当前输入的jointuid
			if (!checkInputParamsValid(newJointInputParamsEntity) || !StringUtils.equals(jointuid, newJointInputParamsEntity.getJoint().getUid())) {
				throw new BizException("输入参数定义错误", ResponseCode._401);
			}
			this.insert(newJointInputParamsEntity);
		}		
	}

	@Override
	public void releaseAllJointInputParams(String jointuid) throws BizException {
		if (StringUtils.isEmpty(jointuid)) {
			throw new BizException("错误的任务编号jointuid参数，请检查", ResponseCode._402);
		}

		// 删除记录
		List<JointInputParamsEntity> inputParamsEntities = queryInputParamsByjointuid(jointuid);
		if (inputParamsEntities == null) {
			return;
		}
		for (JointInputParamsEntity inputparam: inputParamsEntities) {
			this.delete(inputparam.getUid());
		}
	}
	
	/**
	 * 检查一个InputParams信息，检查InputParams非空必要属性是否全部都已经配置完毕<br>
	 * 其他参数根据业务的实际情况来
	 * 
	 * @param inputParams
	 * @return 若JointInputParamsEntity格式合法则返回True， 否则返回False
	 * @throws BizException
	 */
	private boolean checkInputParamsValid(JointInputParamsEntity inputParams) {
		if (inputParams == null) {
			return false;
		}
		// 若没有填写uid，系统将为这个joint任务生成一个全系统唯一的
		if (StringUtils.isEmpty(inputParams.getUid())) {
			inputParams.setUid(java.util.UUID.randomUUID().toString());
		}

		boolean ret = true;
		ret = ret && !StringUtils.isEmpty(inputParams.getName());
		ret = ret && !StringUtils.isEmpty(inputParams.getType());
		ret = ret && (StringUtils.equals(inputParams.getType(), "String") 
				|| StringUtils.equals(inputParams.getType(), "Boolean") 
				|| StringUtils.equals(inputParams.getType(), "Integer") 
				|| StringUtils.equals(inputParams.getType(), "Long") 
				|| StringUtils.equals(inputParams.getType(), "Float") 
				|| StringUtils.equals(inputParams.getType(), "Double") 
				|| StringUtils.equals(inputParams.getType(), "JSON") 
				|| StringUtils.equals(inputParams.getType(), "XML") 
				|| StringUtils.equals(inputParams.getType(), "Date"));
		ret = ret && !StringUtils.isEmpty(inputParams.getDisplayName());
		ret = ret && !StringUtils.isEmpty(inputParams.getDisplayType());
		ret = ret && (inputParams.getJoint() != null);
		return ret;
	}
}
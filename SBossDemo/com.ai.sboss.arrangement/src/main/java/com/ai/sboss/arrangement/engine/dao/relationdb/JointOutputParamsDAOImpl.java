package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author chaos
 *
 */
@Component("jointOutputParamsDAOImpl")
public class JointOutputParamsDAOImpl extends AbstractRelationalDBDAO<JointOutputParamsEntity> implements IJointOutputParamsDAO {

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO#getEntityClass()
	 */
	@Override
	protected Class<JointOutputParamsEntity> getEntityClass() {
		return JointOutputParamsEntity.class;
	}

	@Override
	public List<JointOutputParamsEntity> queryOutputParamsByjointuid(String jointuid) throws BizException {
		if (StringUtils.isEmpty(jointuid)) {
			throw new BizException("错误的任务编号jointuid参数，请检查", ResponseCode._402);
		}

		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("jointuid", jointuid);

		// 获取记录
		List<JointOutputParamsEntity> result = null;
		result = this.queryByHqlFile("IJointOutputParamsDAO.queryOutputParamsByjointuid", conditionMap);

		return result;
	}

	@Override
	public void bindJointOutputParams(String jointuid, Set<JointOutputParamsEntity> outputParams) throws BizException {
		if (outputParams == null) {
			throw new BizException("错误的空outputParams参数，请检查", ResponseCode._402);
		}
		for (JointOutputParamsEntity newJointOutputParamsEntity : outputParams) {
			// 插入新节点
			// 合法性检查
			// 检查outputParam对应的parentJoint是不是对应当前输入的jointuid
			if (!checkOutputParamsValid(newJointOutputParamsEntity) || !StringUtils.equals(jointuid, newJointOutputParamsEntity.getJoint().getUid())) {
				throw new BizException("输出参数定义错误", ResponseCode._401);
			}
			this.insert(newJointOutputParamsEntity);
		}				
	}

	@Override
	public void releaseAllJointOutputParams(String jointuid) throws BizException {
		if (StringUtils.isEmpty(jointuid)) {
			throw new BizException("错误的任务编号jointuid参数，请检查", ResponseCode._402);
		}

		List<JointOutputParamsEntity> outputParamsEntities = queryOutputParamsByjointuid(jointuid);
		if (outputParamsEntities == null) {
			return;
		}
		// 删除记录
		for (JointOutputParamsEntity outputparam: outputParamsEntities) {
			this.delete(outputparam.getUid());
		}
	}
	
	/**
	 * 检查一个OutputParams信息，检查OutputParams非空必要属性是否全部都已经配置完毕<br>
	 * 其他参数根据业务的实际情况来
	 * 
	 * @param outputParams
	 * @return 若JointOutputParamsEntity格式合法则返回True， 否则返回False
	 * @throws BizException
	 */
	private boolean checkOutputParamsValid(JointOutputParamsEntity outputParams) {
		if (outputParams == null) {
			return false;
		}
		// 若没有填写uid，系统将为这个joint任务生成一个全系统唯一的
		if (StringUtils.isEmpty(outputParams.getUid())) {
			outputParams.setUid(java.util.UUID.randomUUID().toString());
		}

		boolean ret = true;
		ret = ret && !StringUtils.isEmpty(outputParams.getName());
		ret = ret && !StringUtils.isEmpty(outputParams.getType());
		ret = ret && (StringUtils.equals(outputParams.getType(), "String")
					|| StringUtils.equals(outputParams.getType(), "Boolean") 
					|| StringUtils.equals(outputParams.getType(), "Integer") 
					|| StringUtils.equals(outputParams.getType(), "Long") 
					|| StringUtils.equals(outputParams.getType(), "Float") 
					|| StringUtils.equals(outputParams.getType(), "Double") 
					|| StringUtils.equals(outputParams.getType(), "JSON") 
					|| StringUtils.equals(outputParams.getType(), "XML") 
					|| StringUtils.equals(outputParams.getType(), "Date"));
		ret = ret && (outputParams.getRequired() == true || outputParams.getRequired() == false);
		ret = ret && (outputParams.getJoint() != null);
		return ret;
	}
}

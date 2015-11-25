package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.orm.JointInstanceFlowLogEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author yinwenjie
 *
 */
@Component("jointInstanceFlowLogDAOImpl")
public class JointInstanceFlowLogDAOImpl extends AbstractRelationalDBDAO<JointInstanceFlowLogEntity> implements IJointInstanceFlowLogDAO {

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO#getEntityClass()
	 */
	@Override
	protected Class<JointInstanceFlowLogEntity> getEntityClass() {
		return JointInstanceFlowLogEntity.class;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceFlowLogDAO#queryLastForwardFlowLog(java.lang.String)
	 */
	@Override
	public JointInstanceFlowLogEntity queryLastForwardFlowLog(String jointInstanceId) throws BizException {
		if (StringUtils.isEmpty(jointInstanceId)) {
			throw new BizException("传入任务实例ID不能为空", ResponseCode._401);
		}
		
		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("jointInstanceId", jointInstanceId);

		List<JointInstanceFlowLogEntity> results = null;
		results = this.queryByHqlFile("IJointInstanceFlowLogDAO.queryLastForwardFlowLog",conditionMap);
		
		if(results == null || results.isEmpty()) {
			return null;
		} else {
			return results.get(0);
		}
	}
}
package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.orm.InstanceContextParamLogEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author yinwenjie
 *
 */
@Component("InstanceContextParamLogDAOImpl")
public class InstanceContextParamLogDAOImpl extends AbstractRelationalDBDAO<InstanceContextParamLogEntity> implements IInstanceContextParamLogDAO {

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO#getEntityClass()
	 */
	@Override
	protected Class<InstanceContextParamLogEntity> getEntityClass() {
		return InstanceContextParamLogEntity.class;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IInstanceContextParamLogDAO#queryInstanceContextParamLogByFlowLog(java.lang.String)
	 */
	@Override
	public List<InstanceContextParamLogEntity> queryInstanceContextParamLogByFlowLog(String jointInstanceFlowLogId) throws BizException {
		if(StringUtils.isEmpty(jointInstanceFlowLogId)) {
			throw new BizException("错误的jointInstanceFlowLog编号", ResponseCode._403);
		}
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("jointInstanceFlowLogId", jointInstanceFlowLogId);
		
		List<InstanceContextParamLogEntity> results = null;
		results = this.queryByHqlFile("IInstanceContextParamLogDAO.queryInstanceContextParamLogByFlowLog", condition);
		
		return results;
	}	
}
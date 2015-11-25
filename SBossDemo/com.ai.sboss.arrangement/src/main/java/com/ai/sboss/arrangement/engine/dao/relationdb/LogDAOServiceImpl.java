package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ai.sboss.arrangement.engine.dao.LogDAOService;
import com.ai.sboss.arrangement.entity.orm.InstanceContextParamLogEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceFlowLogEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.utils.JSONUtils;

/**
 * @author yinwenjie
 *
 */
@Component("logDAOServiceImpl")
public class LogDAOServiceImpl implements LogDAOService {

	@Autowired
	private IInstanceContextParamLogDAO instanceContextParamLogDAO;
	
	@Autowired
	private IJointInstanceFlowLogDAO jointInstanceFlowLogDAO;
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.LogDAOService#queryInstanceContextParamLogByFlowLog(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public JSONArray queryInstanceContextParamLogByFlowLog(String jointInstanceFlowLogId) throws BizException {
		List<InstanceContextParamLogEntity> paramLogs = this.instanceContextParamLogDAO.queryInstanceContextParamLogByFlowLog(jointInstanceFlowLogId);
		if(paramLogs == null) {
			return new JSONArray();
		}
		
		JSONArray logsObject = JSONUtils.toJSONArray(paramLogs, new String[]{"jointInstanceFlowLog"});
		return logsObject;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.LogDAOService#queryJointInstanceFlowLogById(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public JSONObject queryJointInstanceFlowLogById(String jointInstanceFlowLogId) throws BizException {
		JointInstanceFlowLogEntity jointInstanceFlowLog = this.jointInstanceFlowLogDAO.getEntity(jointInstanceFlowLogId);
		if(jointInstanceFlowLog == null) {
			return new JSONObject();
		}
		
		JSONObject logObject = JSONUtils.toJSONObject(jointInstanceFlowLog, new String[]{"contextParamLogs"});
		return logObject;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.LogDAOService#queryLastForwardFlowLog(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public JSONObject queryLastForwardFlowLog(String jointInstanceId) throws BizException {
		JointInstanceFlowLogEntity flowLog = this.jointInstanceFlowLogDAO.queryLastForwardFlowLog(jointInstanceId);
		if(flowLog == null) {
			return new JSONObject();
		}
		
		JSONObject logObject = JSONUtils.toJSONObject(flowLog, new String[]{"contextParamLogs"});
		return logObject;
	}
}

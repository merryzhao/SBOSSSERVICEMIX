package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.orm.InstanceContextParamLogEntity;
import com.ai.sboss.arrangement.exception.BizException;

public interface IInstanceContextParamLogDAO extends SystemDAO<InstanceContextParamLogEntity> {
	/**
	 * 按照流转日志的id，查询本次流转时，上下文中参数值的变化日志
	 * @param jointInstanceFlowLogId 指定的流传日志编号
	 * @return 
	 * @throws BizException
	 */
	public List<InstanceContextParamLogEntity> queryInstanceContextParamLogByFlowLog(String jointInstanceFlowLogId) throws BizException;
}
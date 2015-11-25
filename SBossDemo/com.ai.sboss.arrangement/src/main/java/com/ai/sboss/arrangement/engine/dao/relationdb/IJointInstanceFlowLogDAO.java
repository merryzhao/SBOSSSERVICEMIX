package com.ai.sboss.arrangement.engine.dao.relationdb;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.orm.JointInstanceFlowLogEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * @author yinwenjie
 *
 */
public interface IJointInstanceFlowLogDAO extends SystemDAO<JointInstanceFlowLogEntity> {
	/**
	 * 查询指定的任务实例最后一次正向流转的日志。这种查询主要是为了进行逆向流转时，查询之前那次正向流转的日志信息。<br>
	 * 所以传入的jointInstanceId将作为formJointInstanceId的查询条件。
	 * @param jointInstanceId 指定的任务实例编号
	 * @return
	 * @throws BizException
	 */
	public JointInstanceFlowLogEntity queryLastForwardFlowLog(String jointInstanceId) throws BizException;
}

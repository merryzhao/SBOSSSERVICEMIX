package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;
import java.util.Set;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * @author chaos
 *
 */
public interface IArrangementJointMappingDAO extends SystemDAO<ArrangementJointMappingEntity> {
	
	/**
	 * 获取一个指定的编排流程的jointmapping集合信息。<br>
	 * @param arrangementuid jointmapping对应的编排流程唯一编号信息
	 * @return 如果有符合编号的jointmapping，将以对象的形式进行返回；其他情况下返回null
	 * @throws BizException
	 */
	public List<ArrangementJointMappingEntity> getArrangementJointmappingSet(String arrangementuid) throws BizException;
	
	/**
	 * 重新绑定指定的流程编排信息所绑定的任务集合。<br>
	 * 既然是重新绑定，那么之前设定的任务集合信息都将不再起作用。
	 * @param arrangementuid 指定的需要重新绑定任务信息的编排流程uid
	 * @param jointmapping 新的任务集合
	 * @throws BizException
	 */
	public void bindArrangementJointmapping(String arrangementuid , Set<ArrangementJointMappingEntity> jointmapping) throws BizException;
	
	/**
	 * 释放绑定的任务集合<br>
	 * @param arrangementuid 需要释放任务节点的流程
	 * @return null
	 * @throws BizException
	 */
	public void releaseAllArrangementJointmapping(String arrangementuid) throws BizException;

}

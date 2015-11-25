package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;
import java.util.Set;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * @author chaos
 *
 */
public interface IJointOutputParamsDAO extends SystemDAO<JointOutputParamsEntity> {
	/**
	 * 按照jointuid任务编号，查询其对应的出参信息集合
	 * @param jointuid 指定的joint任务编号信息
	 * @return 如果有符合条件的出参集合信息将被返回；其他情况返回null
	 * @throws BizException
	 */
	public List<JointOutputParamsEntity> queryOutputParamsByjointuid(String jointuid) throws BizException;
	
	/**
	 * 修改（刷新）joint任务绑定的出参信息。<br>
	 * 注意，既然是刷新，那么之前的outputParams都会无效，以当前传入的outputParams集合为准
	 * @param jointuid 需要更新的指定的任务唯一编号信息
	 * @param outputParams 最新的出参信息集合
	 * @throws BizException 
	 */
	public void bindJointOutputParams(String jointuid , Set<JointOutputParamsEntity> outputParams) throws BizException;
	
	/**
	 * 释放指定任务节点的出参集合<br>
	 * @param jointuid 需要释放任务节点的流程
	 * @return null
	 * @throws BizException
	 */
	public void releaseAllJointOutputParams(String jointuid) throws BizException;
}

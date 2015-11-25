package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;
import java.util.Set;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * @author chaos
 *
 */
public interface IJointInputParamsDAO extends SystemDAO<JointInputParamsEntity> {

	/**
	 * 按照jointuid任务编号，查询其对应的入参信息集合
	 * @param jointuid 指定的joint任务编号信息
	 * @return 如果有符合条件的入参集合信息将被返回；其他情况返回null
	 * @throws BizException
	 */
	public List<JointInputParamsEntity> queryInputParamsByjointuid(String jointuid) throws BizException;
	
	/**
	 * 修改（刷新）joint任务绑定的入参信息。<br>
	 * 注意，既然是刷新，那么之前的inputParams都会无效，以当前传入的inputParams集合为准
	 * @param jointuid 需要更新的指定的任务唯一编号信息
	 * @param inputParams 最新的入参信息集合
	 * @throws BizException
	 */
	public void bindJointInputParams(String jointuid , Set<JointInputParamsEntity> inputParams) throws BizException;
	
	/**
	 * 释放指定任务节点的入参集合<br>
	 * @param jointuid 需要释放任务节点的流程
	 * @return null
	 * @throws BizException
	 */
	public void releaseAllJointInputParams(String jointuid) throws BizException;
}

package com.ai.sboss.arrangement.engine.dao;

import java.util.List;
import java.util.Set;

import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity;
import com.ai.sboss.arrangement.exception.BizException;


/**
 * 该接口向模块外部提供关于流程参数定义相关的持久层服务
 * （包括流程编排的参数定义，流程实例的状态记录）
 * @author yinwenjie
 */
public interface ParamsDAOService {
	/**
	 * 按照jointuid任务编号，查询其对应的入参信息集合
	 * @param jointuid 指定的joint任务编号信息
	 * @return 如果有符合条件的入参集合信息将被返回；其他情况返回null
	 * @throws BizException
	 */
	public List<JointInputParamsEntity> queryInputParamsByjointuid(String jointuid) throws BizException; 
	
	/**
	 * 按照jointuid任务编号，查询其对应的出参信息集合
	 * @param jointuid 指定的joint任务编号信息
	 * @return 如果有符合条件的出参集合信息将被返回；其他情况返回null
	 * @throws BizException
	 */
	public List<JointOutputParamsEntity> queryOutputParamsByjointuid(String jointuid) throws BizException;
	
	/**
	 * 修改（刷新）joint任务绑定的入参信息。<br>
	 * 注意，既然是刷新，那么之前的inputParams都会无效，以当前传入的inputParams集合为准
	 * @param jointuid 需要更新的指定的任务唯一编号信息
	 * @param inputParams 最新的入参信息集合
	 * @throws BizException
	 */
	public void updateJointInputParams(String jointuid , Set<JointInputParamsEntity> inputParams) throws BizException;
	
	/**
	 * 修改（刷新）joint任务绑定的出参信息。<br>
	 * 注意，既然是刷新，那么之前的outputParams都会无效，以当前传入的outputParams集合为准
	 * @param jointuid 需要更新的指定的任务唯一编号信息
	 * @param outputParams 最新的出参信息集合
	 * @throws BizException 
	 */
	public void updateJointOutputParams(String jointuid , Set<JointOutputParamsEntity> outputParams) throws BizException;
}

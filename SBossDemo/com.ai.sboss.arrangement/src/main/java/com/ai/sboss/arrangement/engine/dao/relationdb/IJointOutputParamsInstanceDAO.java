package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * @author chaos
 *
 */
public interface IJointOutputParamsInstanceDAO extends SystemDAO<JointOutputParamsInstanceEntity> {

	/**
	 * 创建出参实例
	 * @param outputParamInstance 按照相关规则建立的出参实例
	 * @return 如果创建成功，出参实例信息将被返回；其他情况返回null
	 * @throws BizException
	 */
	public JointOutputParamsInstanceEntity createOutputParamsInstance(JointOutputParamsInstanceEntity outputParamInstance) throws BizException;
	
	
	/**
	 * 更新出参实例
	 * @param outputParamInstance 需要更新的出参实例
	 * @return 
	 * @throws BizException
	 */
	public void updateInputParamsInstance(JointOutputParamsInstanceEntity outputParamInstance) throws BizException;
	
	/**
	 * 按照jointInstanceid任务实例编号，查询其对应的出参实例信息集合
	 * @param jointInstanceuid 指定的joint任务实例编号信息
	 * @param required 是否只查询必要参数，若true，则只查询必要的出参实例，若false,则查询所有出参
	 * @return 如果有符合条件的出参实例集合信息将被返回；其他情况返回null
	 * @throws BizException
	 */
	public List<JointOutputParamsInstanceEntity> queryOutputParamsInstanceByJointInstanceID(String jointInstanceid, Boolean required) throws BizException;
	
	/**
	 * 按照jointInstanceid任务实例的编号和出参的英文名称，查询对应的出参实例。
	 * @param jointInstanceid 指定的joint任务实例编号 
	 * @param name 入参名称（英文）
	 * @return 如果没有查询到相应的结果，则返回null
	 * @throws BizException
	 */
	public JointOutputParamsInstanceEntity queryOutputParamsInstanceByName(String jointInstanceid, String name) throws BizException;
	
	/**
	 * 按照jointInstanceid任务实例编号，删除其对应的出参实例信息集合
	 * @param jointInstanceuid 指定的joint任务实例编号
	 * @return 
	 * @throws BizException
	 */
	public void deleteOutputParamsInstancesByJointInstanceID(String jointInstanceid) throws BizException;
	
	/**
	 * 按照outputParamInstanceid出参实例编号，删除出参实例
	 * @param outputParamInstanceid 指定的出参实例编号
	 * @return 
	 * @throws BizException
	 */
	public void deleteOutputParamInstanceByInstanceID(String outputParamInstanceid) throws BizException;
}

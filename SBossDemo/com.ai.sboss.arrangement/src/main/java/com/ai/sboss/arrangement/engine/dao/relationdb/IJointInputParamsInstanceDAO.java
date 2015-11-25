package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * @author chaos
 *
 */
public interface IJointInputParamsInstanceDAO extends SystemDAO<JointInputParamsInstanceEntity> {

	/**
	 * 创建入参实例
	 * @param inputParamInstance 按照相关规则建立的入参实例
	 * @return 如果创建成功，入参实例信息将被返回；其他情况返回null
	 * @throws BizException
	 */
	public JointInputParamsInstanceEntity createInputParamsInstance(JointInputParamsInstanceEntity inputParamInstance) throws BizException;
	
	/**
	 * 更新入参实例
	 * @param inputParamInstance 需要更新的入参实例
	 * @return 
	 * @throws BizException
	 */
	public void updateInputParamsInstance(JointInputParamsInstanceEntity inputParamInstance) throws BizException;
	
	/**
	 * 按照jointInstanceid任务实例编号，查询其对应的入参实例信息集合
	 * @param jointInstanceuid 指定的joint任务实例编号信息
	 * @param required 是否只查询必要参数，若true，则只查询必要的入参实例，若false,则查询所有入参
	 * @return 如果有符合条件的入参实例集合信息将被返回；其他情况返回null
	 * @throws BizException
	 */
	public List<JointInputParamsInstanceEntity> queryInputParamsInstanceByJointInstanceID(String jointInstanceid, Boolean required) throws BizException;
	
	/**
	 * 按照jointInstanceid任务实例的编号和入参的英文名称，查询对应的入参实例。
	 * @param jointInstanceid 指定的joint任务实例编号 
	 * @param name 入参名称（英文）
	 * @return 如果没有查询到相应的结果，则返回null
	 * @throws BizException
	 */
	public JointInputParamsInstanceEntity queryInputParamsInstanceByName(String jointInstanceid , String name) throws BizException;
	
	/**
	 * 按照jointInstanceid任务实例编号，删除其对应的入参实例信息集合
	 * @param jointInstanceuid 指定的joint任务实例编号
	 * @return 
	 * @throws BizException
	 */
	public void deleteInputParamsInstancesByJointInstanceID(String jointInstanceid) throws BizException;
	
	/**
	 * 按照inputParamInstanceid入参实例编号，删除入参实例
	 * @param inputParamInstanceid 指定的入参实例编号
	 * @return 
	 * @throws BizException
	 */
	public void deleteInputParamInstanceByInstanceID(String inputParamInstanceid) throws BizException;
}

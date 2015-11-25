package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceFlowEntity;
import com.ai.sboss.arrangement.exception.BizException;

public interface IJointInstanceFlowDAO extends SystemDAO<JointInstanceFlowEntity> {
	/**
	 * 查询指定的流程实例中，正处于executing状态的任务实例。<br>
	 * 在第一个版本中，由于只支持顺序流程，所以正常情况下，处于executing状态的任务实例只有一个。
	 * @param arrangementInstanceId 指定的流程实例编号信息
	 * @return
	 * @throws BizException
	 */
	public JointInstanceFlowEntity queryExecutingJointInstanceByArrangementInstanceId(String arrangementInstanceId) throws BizException;
	
	/**
	 * 按照指定的流程实例ID，查询对应的流程实例流转信息
	 * @param arrangementInstanceId 指定的流程实例编号信息
	 * @return 
	 * @throws BizException
	 */
	public JointInstanceFlowEntity queryJointFlowByJointInstanceId(String jointInstanceId) throws BizException;
	
	/**
	 * 查询指定的流程实例中，处于指定状态的任务实例。
	 * @param arrangementInstanceId 指定的流程实例编号信息
	 * @param statu 任务实例的状态值，只有几种：<br>
	 * 		waiting 这个任务实例已经完成了初始化，等待执行<br>
	 * 		executing 这个任务的前置任务/子流程，已经正常执行完成了，目前正轮到这个任务实例进行执行。注意还有一种情况，就是流程实例回退的时候，会退到了这个任务实例上。<br>
	 * 		followed 这个任务已经正常执行完成，但是其所处的流程实例还没有全部执行完成<br>
	 * 		revoked 这个任务实例之前已经正常执行完成，但操作者进行了回退操作，这个任务实例的执行状态已经被回退。从业务执行特性来看，相当于waiting状态<br>
	 * 		completed 这个任务实例已经正常执行完成，并且其所处的流程实例也已经全部执行完成<br>
	 * 		terminated 这个任务实例所对应的流程实例已经被操作者强制终止了。<br>
	 * @return
	 * @throws BizException
	 */
	public List<JointInstanceFlowEntity> queryJointInstanceByArrangementInstanceId(String arrangementInstanceId , String statu) throws BizException;
	
	/**
	 * 查询指定的流程实例中的（已排序好）任务实例。
	 * @param arrangementInstanceId 指定的流程实例编号信息
	 * @return
	 * @throws BizException
	 */
	public List<JointInstanceFlowEntity> querySortedJointInstanceByArrangementInstanceId(String arrangementInstanceId) throws BizException;
	
	/**
	 * 查询指定的任务实例，其下一个任务实例的基本信息
	 * @param jointInstanceId 当前的任务实例编号
	 * @return 
	 * @throws BizException
	 */
	public JointInstanceEntity queryNextJointInstanceByJointInstanceId(String jointInstanceId) throws BizException;
	
	/**
	 * 更新一个任务实例流转向导的状态信息。
	 * @param flowid 需要修改的流程实例流转的uid。
	 * @param jointStatu 新的状态。注意这种更新方式不适合更新为“Followed”状态。因为“Followed”状态还必须传入exeTime和执行人
	 * @throws BizException
	 */
	public void updateJointFlowStatuByFlowId(String flowid, String jointStatu) throws BizException;
	
	/**
	 * 当一个流程实例完成流转的时候，需要根据这个流程实例id更新其下所有的任务实例状态为completed。
	 * @param arrangementInstanceId 指定的流程实例编号
	 * @throws BizException
	 */
	public void updateCompletedJointInstanceStatuByArrangementInstanceId(String arrangementInstanceId) throws BizException;
	
	/**
	 * 当一个任务实例正常完成流程的，但整个流程实例还没有完成流转的时候，需要更新这个实例流转的状态为followed<br>
	 * 这个方法就是做这个事情的
	 * @param flowid 指定的任务流转信息编号
	 * @throws BizException
	 */
	public void updateFollowedJointInstanceStatuByFlowId(String flowid , Long exeTime, String executor) throws BizException;
	
	/**
	 * 创建任务实例流程<br>
	 * 这个方法就是做这个事情的
	 * @param jointInstanceFlow 指定的任务流转实体信息
	 * @throws BizException
	 */
	public JointInstanceFlowEntity createJointInstanceFlow(JointInstanceFlowEntity jointInstanceFlow) throws BizException;
}

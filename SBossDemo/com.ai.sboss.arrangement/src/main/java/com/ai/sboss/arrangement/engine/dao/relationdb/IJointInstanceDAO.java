package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;

public interface IJointInstanceDAO extends SystemDAO<JointInstanceEntity> {
	
	/**
	 * 按照 任务实例的编号，查询任务实例的基本信息。<br>
	 * 为什么不使用getEntity呢？主要为了连表
	 * @param businessid 外部系统的业务id号
	 * @return 返回的信息将会按照创建的先后顺序进行排序（按照任务的uid进行排序）
	 * @throws BizException
	 */
	public JointInstanceEntity queryJointInstancesByID(String jointInstanceid) throws BizException ;
	
	/**
	 * 按照 任务实例/流程实例 的创建者或者执行者，查询归属的任务实例信息。
	 * @param userid 必须传入的 任务实例
	 * @param pageNumber 当前的页码数，从0开始计算。如果不传入就是从0开始计数
	 * @param perNumber 每页最大的显示数量，默认为20条
	 * @return 返回的信息将会按照创建的先后顺序进行排序（按照任务的uid进行排序）
	 */
	public PageEntity queryJointInstancesByUserid(String userid , Integer pageNumber , Integer perNumber) throws BizException ;
	
	/**
	 * 按照 外部传出的businessID信息，查询这个外部业务对应的流程实例中所有的任务信息
	 * @param businessid 外部系统的业务id号
	 * @return 返回的信息将会按照创建的先后顺序进行排序（按照任务的uid进行排序）
	 * @throws BizException
	 */
	public List<JointInstanceEntity> queryJointInstancesByBusinessID(String businessid) throws BizException ;
	
	/**
	 * 按照流程实例编号（注意，是流程实例编号），查询这个流程实例所关联的符合状态条件的“任务实例”。<br>
	 * 如果没有传入“任务实例状态”条件，则是查询这个流程实例所关联的所有任务实例（并不保证任务实例执行的先后顺序）。
	 * @param arrangementInstanceuid 指定的流程实例编号
	 * @param jointStatu 任务实例状态，任务实例状态只有以下几种值：<p>
	 * 		waiting 这个任务实例已经完成了初始化，等待执行<p>
	 * 		executing 这个任务的前置任务/子流程，已经正常执行完成了，目前正轮到这个任务实例进行执行。<br>
	 * 			注意还有一种情况，就是流程实例回退的时候，回退到了这个任务实例上。<p>
	 * 		followed 这个任务已经正常执行完成，但是其所处的流程实例还没有全部执行完成<p>
	 * 		revoked 这个任务实例之前已经正常执行完成，但操作者进行了回退操作，这个任务实例的执行状态已经被回退。从业务执行特性来看，相当于waiting状态<p>
	 * 		completed 这个任务实例已经正常执行完成，并且其所处的流程实例也已经全部执行完成<p>
	 * 		terminated 这个任务实例所对应的流程实例已经被操作者强制终止了。<br>
	 * @return 
	 * @throws BizException
	 */
	public List<JointInstanceEntity> queryJointInstanceEntityByInstanceID(String arrangementInstanceuid , String jointStatu) throws BizException;
	
	/**
	 * 创建一个任务实例。创建一个新的任务实例时，需要注意以下的细节：
	 * 	1、这个方法本身只做必填信息的格式、值的验证。<br>
	 * 	2、传入的jointInstance对象，处了包括必须填写的属性信息外，jointInputParamInstance、jointOutputParamInstance这样的集合信息也必须被传入。<br>
	 * @param arrangement 传入的按照规则构造好的流程编排对象。
	 * @throws BizException
	 */
	public JointInstanceEntity createJointInstance(JointInstanceEntity jointInstance) throws BizException;
	
	/**
	 * 更新一个任务实例状态信息。
	 * @param jointInstanceuid 需要修改的流程实例uid。
	 * @param jointStatu 新状态
	 * @throws BizException
	 */
	public void updateJointInstanceStatu(String jointInstanceuid, String jointStatu) throws BizException;
	
	/**
	 * 当一个流程实例完成流转的时候，需要根据这个流程实例id更新其下所有的任务实例状态为completed。
	 * @param arrangementInstanceId 指定的流程实例编号
	 * @throws BizException
	 */
	public void updateCompletedJointInstanceStatuByArrangementInstanceId(String arrangementInstanceId) throws BizException;
	
	/**
	 * 当一个任务实例正常完成流程的，但整个流程实例还没有完成流转的时候，需要更新这个实例的状态为followed<br>
	 * 这个方法就是做这个事情的
	 * @param arrangementInstanceId 指定的流程实例编号
	 * @throws BizException
	 */
	public void updateFollowedJointInstanceStatuByJointInstanceId(String jointInstanceuid , Long exeTime, String executor) throws BizException;
	
	/**
	 * 根据任务实例ID，删除一个任务实例的基本信息。<br>
	 * 注意这里只会删除任务实例本身的信息，并不会删除其关联的其他实例（如：入参实例，出参实例）
	 * @param jointInstanceuid 需要删除的任务实例uid编号。
	 * @throws BizException
	 */
	public void deleteJointInstance(String jointInstanceuid) throws BizException;
	
	/**
	 * 根据流程实例ID，删除其对应的任务实例集合的基本信息。<br>
	 * 注意这里会删除任务实例本身的信息，并会删除其关联的其他实例（如：入参实例，出参实例）
	 * @param arrangementInstanceuid 指定的流程实例uid编号。
	 * @throws BizException
	 */
	public void deleteJointInstancesByArrangementInstanceID(String arrangementInstanceuid) throws BizException;
}

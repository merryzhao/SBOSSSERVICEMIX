package com.ai.sboss.arrangement.engine.dao.relationdb;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;

public interface IArrangementInstanceDAO extends SystemDAO<ArrangementInstanceEntity> {
	/**
	 * 按照流程实例 的创建者，查询归属的任务实例信息。
	 * @param userid 必须传入的 创建者ID
	 * @param statu 流程实例状态，若不传，则查询全部，流程实例状态只有以下几种值：<p>
	 * 		@waiting 	这个流程实例已经完成了初始化，等待执行<br>
	 * 		@executing 	这个流程实例，正在执行。<br>
	 * 		@revoked 	这个流程实例之前已经正常执行完成，但操作者进行了回退操作，这个流程实例的执行状态已经被回退。从业务执行特性来看，相当于waiting状态<br>
	 * 		@completed 	这个流程实例已经正常执行完成，并且其对应的所有流程实例也已经全部执行完成。<br>
	 * 		@terminated 这个流程实例已经被操作者强制终止了。<br>
	 * @param pageNumber 当前的页码数，从0开始计算。如果不传入就是从0开始计数
	 * @param perNumber 每页最大的显示数量，默认为20条
	 * @return 返回的信息将会按照创建的先后顺序进行排序（按照任务的uid进行排序）
	 */
	public PageEntity queryArrangementInstancesByUserid(String userid, String statu, Integer pageNumber , Integer perNumber) throws BizException ;
	
	/**
	 * 按照 外部传出的businessID信息，查询这个外部业务对应的流程实例信息
	 * @param businessid 外部系统的业务id号
	 * @return 返回的信息将会按照创建的先后顺序进行排序（按照任务的uid进行排序）
	 * @throws BizException
	 */
	public ArrangementInstanceEntity queryArrangementInstancesByBusinessID(String businessid) throws BizException;
	
	/**
	 * 按照 流程实例id 查询流程的基本信息。<br>
	 * 为什么要有这个类，而不直接使用getEntity，是因为要连表
	 * @param arrangementInstancesid 外部系统的业务id号
	 * @return 返回的信息将会按照创建的先后顺序进行排序（按照任务的uid进行排序）
	 * @throws BizException
	 */
	public ArrangementInstanceEntity queryArrangementInstancesByID(String arrangementInstancesid) throws BizException;
	
	/**
	 * 创建一个流程实例。创建一个新的流程实例时，需要注意以下的细节：
	 * 	1、这个方法本身只做必填信息的格式、值的验证。<br>
	 * 	2、传入的arrangementInstance对象，处了包括必须填写的属性信息外，jointInstances、childArrangementInstances这样的集合信息也必须被传入。<br>
	 * @param arrangement 传入的按照规则构造好的流程编排对象。
	 * @throws BizException
	 */
	public ArrangementInstanceEntity createArrangementInstance(ArrangementInstanceEntity arrangementInstance) throws BizException;
	
	/**
	 * 更新一个流程实例基本信息。
	 * @param jointInstance 传入的按照规则构造好的流程编排对象。
	 * @throws BizException
	 */
	public void updateArrangementInstance(ArrangementInstanceEntity arrangementInstance) throws BizException;
	
	/**
	 * 专门更新某一个流程实例，到“completed”状态
	 * @param arrangementInstanceuid 需要修改的流程实例uid。
	 * @param endTime 设置的结束事件
	 * @throws BizException
	 */
	public void updateArrangementInstanceCompletedStatu(String arrangementInstanceuid , Long endTime) throws BizException;
	
	/**
	 * 更改一个流程实例状态信息。（不能使用这个方法更新arrangementInstance的competled状态，因为还有一个执行时间）
	 * @param arrangementInstanceuid 需要修改的流程实例uid。
	 * @param statu 新状态
	 * @throws BizException
	 */
	public void updateArrangementInstanceStatu(String arrangementInstanceuid, String statu) throws BizException;
	
	/**
	 * 根据流程实例ID，删除一个任务实例的基本信息。<br>
	 * 注意这里只会删除流程实例本身的信息，并不会删除其关联的其他实例（如：任务实例，子流程实例）
	 * @param jointInstanceuid 需要删除的实例uid编号。
	 * @throws BizException
	 */
	public void deleteArrangementInstance(String arrangementInstanceuid) throws BizException;

	/**
	 * 按照流程实例唯一绑定的 “外部业务编号”。从而得到这个流程实例信息
	 * @param businessid 唯一的“外部业务编号”
	 * @return 符合条件的流程实例将被返回；如果没有查到任何符合条件的任务信息，则返回空
	 * @throws BizException 
	 */
	public ArrangementInstanceEntity queryArrangementInstanceBybusinessID(String businessid) throws BizException;
}
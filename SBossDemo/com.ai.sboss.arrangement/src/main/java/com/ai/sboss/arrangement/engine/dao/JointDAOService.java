package com.ai.sboss.arrangement.engine.dao;

import java.util.List;
import java.util.Map;

import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * 该接口向模块外部提供关于任务节点数据相关的持久层服务（包括读写、查询服务）
 * @author yinwenjie
 */
public interface JointDAOService {
	/**
	 * 按照行业编号，查询这个行业下，已经定义的任务信息。<br>
	 * 排序会按照任务编号进行，并且不带分页。
	 * @param tradeid 指定的业务编号信息
	 * @param scope 行业定义的范围条件，这个条件可以指定也可以不指定
	 * @return 符合条件的任务集合将被返回；如果没有查到任何符合条件的任务信息，则返回空
	 * @throws BizException
	 */
	public List<JointEntity> queryJointByTradeid(String tradeid , String scope) throws BizException;
	
	/**
	 * 按照流程实例唯一绑定的 “外部业务编号”。
	 * 从而得到这个流程实例所使用的流程模板下所绑定的“任务模板”
	 * @param businessid 唯一的“外部业务编号”
	 * @return 符合条件的任务模板集合将被返回；如果没有查到任何符合条件的任务信息，则返回空
	 * @throws BizException 
	 */
	public List<JointEntity> queryJointBybusinessID(String businessid) throws BizException;
	
	/**
	 * 按照流程模板的编号，查询这个流程模板下所归属的任务模板信息。并以“时间偏移量”为排序依据进行返回
	 * @param arrangementid 指定的流程模板编号
	 * @return 符合条件的任务模板集合将被返回；如果没有查到任何符合条件的任务信息，则返回空
	 * @throws BizException
	 */
	public List<JointEntity> queryJointByArrangementID(String arrangementid) throws BizException;
	
	/**
	 * 查询tradeid下已建立Arrangement流程模板所关联的任务信息 的列表
	 * @param tradeid 已建立的流程模板对应的“服务id”
	 * @return 
	 * @throws BizException
	 */
	public List<JointEntity> queryMappingJointByTradeid(String tradeid) throws BizException;
	
	/**
	 * 按照行业编号，查询这个行业下，已经定义的任务流程。<br>
	 * 排序会按照任务编号进行，带分页。
	 * @param tradeid 制定的行业编号信息
	 * @param scope 行业定义的范围条件，这个条件可以指定也可以不指定
	 * @param pageNumber 当前的页码号，如果没有指定则说明是第一页（第一页的index=0）
	 * @param maxPerNumber 每页最大的数据量
	 * @return 符合条件的任务信息集合将被返回；如果没有查到任何符合条件的任务信息，则返回空。但是pageEntity不会为null。
	 * @throws BizException
	 */
	public PageEntity queryJointByTradeidPage(String tradeid , String scope , Integer pageNumber , Integer maxPerNumber) throws BizException;
	
	/**
	 * 获取指定joint的基本信息，但是基本信息中，不包括inputparams集合、outputparams集合、trade集合信息<br>
	 * 利用该接口查询查来的joint对象，如果调用个getInputParams这样的集合获取方法，就会报事务超出边界的错误
	 * @param jointuid 需要查询的任务唯一编号信息
	 * @return 如果存在jointuid对应的任务基本信息，则返回；其他情况则返回null
	 * @throws BizException 
	 */
	public JointEntity getJointWithoutParams(String jointuid) throws BizException;
	
	/**
	 * 获取指定joint的基本信息，但是基本信息中，包括inputparams集合、outputparams集合、trade集合信息
	 * @param jointuid 
	 * @return
	 * @throws BizException
	 */
	public JointEntity getJointWithParams(String jointuid) throws BizException;
	
	/**
	 * 添加一个joint信息，添加时joint时，joint对应的inputparams集合、outputparams集合、trade集合信息都必须设置<br>
	 * 另外，joint的uid唯一编号信息不需要指定，即使指定也无效。系统将为这个joint任务生成一个全系统唯一的<br>
	 * 其他参数根据业务的实际情况来
	 * @param joint 
	 * @throws BizException 
	 */
	public void createJoint(JointEntity joint) throws BizException;
	
	/**
	 * 更新一个已经存在的joint任务基本信息<br>
	 * 注意通过这个方法更新的joint不会更新inputparams集合、outputparams集合、trade集合信息<br>
	 * 要更新集合信息，请调用相关的update方法
	 * @param joint 将要更新基本属性的joint任务对象。注意joint任务对象的uid属性必须要有值
	 * @throws BizException
	 */
	public void updateJoint(JointEntity joint) throws BizException;
	
	/**
	 * 修改（刷新）joint任务的行业信息。<br>
	 * 注意，既然是刷新，那么之前的trades都会无效，以当前传入的trades集合为准
	 * @param jointuid 需要更新的指定的任务唯一编号信息
	 * @param trades 最新的行业绑定关系。注意这是一个K-V的对应，K：tradeid；V：scope<br>
	 * scope有三个有效值：<br>
	 * 		industry：只能作为某个行业的默认服务，不能被服务者或者消费者的自定义流程引用<br>
	 * 		producer：可以作为行业的默认服务或者服务者的自定义流程，但是不能被消费者的自定义流程引用<br>
	 * 		consumer：可以作为行业的默认服务、服务者或者消费用的自定义流程<br>
	 * @throws BizException 
	 */
	public void updateJointTrades(String jointuid , Map<String, String> trades) throws BizException;
	
	/**
	 * 删除joint任务。<br>
	 * 注意，既然是刷新，那么之前的trades都会无效，以当前传入的trades集合为准
	 * @param jointuid 需要更新的指定的任务唯一编号信息
	 * @throws BizException 
	 */
	public void deleteJoint(String jointuid) throws BizException;
}

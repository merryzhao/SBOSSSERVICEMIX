package com.ai.sboss.arrangement.service;

import com.ai.sboss.arrangement.entity.JsonEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * 查询操作接口类.
 * @author Chaos
 * @author yinwenjie
 */
public interface IQueryOps {
	/**
	 * 查询行业流程默认模板.
	 * @param tradeid 指定的行业信息。注意，一个行业下肯定只有一个行业默认的流程模板 
	 * @return 
	 */
	public JsonEntity queryDefaultArrangementByTradeid(String tradeid);

	/**
	 * 查询行业流程可选任务节点列表（任务节点将按照编号进行排序，默认的排序）.
	 * @param tradeid 指定的行业信息
	 * @param scope 任务节点的使用范围，这个参数可以不传入，不传入代表查询所有的范围：<br>
	 * 		industry：只能作为某个行业的默认服务，不能被服务者或者消费者的自定义流程引用<br>
	 * 		producer：可以作为行业的默认服务或者服务者的自定义流程，但是不能被消费者的自定义流程引用<br>
	 * 		consumer：可以作为行业的默认服务、服务者或者消费用的自定义流程<br>
	 * 其他属性值无效
	 * @return 
	 */
	public JsonEntity queryJointByTradeid(String tradeid, String scope);
	
	/**
	 * 按照流程实例唯一绑定的 “外部业务编号”。
	 * 从而得到这个流程实例所使用的流程模板下所绑定的“任务模板”
	 * @param businessid 唯一的“外部业务编号”
	 * @return 符合条件的任务模板集合将被返回；如果没有查到任何符合条件的任务信息，则返回空
	 * @throws BizException 
	 */
	public JsonEntity queryJointBybusinessID(String businessid) throws BizException;
	
	/**
	 * 查询tradeid下已建立Arrangement流程模板所关联的任务信息 的列表
	 * @param tradeid 已建立的流程模板对应的“服务id”
	 * @return 
	 * @throws BizException
	 */
	public JsonEntity queryMappingJointByTradeid(String tradeid) throws BizException;

	/**
	 * 查询当前用户所创建的流程列表(流程实例).
	 * @param creator 流程的创建者，这个参数必须传入
	 * @param tradeid 指定的行业id，这个参数可选。如果不传入则查询所有行业
	 * @param scope 任务业务流程的使用范围，这个参数可以不传入，不传入代表查询所有的范围：<br>
	 * 		industry：只能作为某个行业的默认服务，不能被服务者或者消费者的自定义流程引用<br>
	 * 		producer：可以作为行业的默认服务或者服务者的自定义流程，但是不能被消费者的自定义流程引用<br>
	 * 		consumer：可以作为行业的默认服务、服务者或者消费用的自定义流程<br>
	 * @return 
	 */
	public JsonEntity getProcessByCustomerId(Object creator , String tradeid , String scope);
	
	/**
	 * 按照流程实例的编号，查询这个流程实例的基本信息（不包括查询流程实例对象中的各种关联实体信息）
	 * @param arrangementInstanceuid 指定的流程实例编号
	 * @return 
	 * @throws BizException
	 */
	public JsonEntity queryArrangementInstanceByArrangementInstanceID(String arrangementInstanceuid);
	
	/**
	 * 按照流程实例的编号，查询这个流程实例的基本信息及相应（已排序好的）任务实例信息
	 * @param arrangementInstanceuid 指定的流程实例编号
	 * @return 
	 * @throws BizException
	 */
	public JsonEntity queryArrangementInstanceByArrangementInstanceIDWithSet(String arrangementInstanceuid);
	
	/**
	 * 按照流程实例的绑定的业务编号信息，查询这个流程实例的基本信息（不包括查询流程实例对象中的各种关联实体信息）
	 * @param businessid 指定的流程实例绑定的业务编号信息
	 * @return 
	 * @throws BizException
	 */
	public JsonEntity queryArrangementInstanceByBusinessID(String businessid);
	
	/**
	 * 按照流程实例的绑定的业务编号信息，查询这个流程实例的基本信息(包括查询流程实例对象中的各种关联实体信息)<br>
	 * 任务实例信息已排序好
	 * @param businessid 指定的流程实例绑定的业务编号信息
	 * @return 
	 * @throws BizException
	 */
	public JsonEntity queryArrangementInstanceByBusinessIDWithSet(String businessid);
	
	/**
	 * 按照 任务实例/流程实例 的创建者或者执行者，查询归属的任务实例信息。
	 * @param userid 必须传入的 任务实例
	 * @param nowPage 当前的页码数，从0开始计算。如果不传入就是从0开始计数
	 * @param maxPageRows 每页最大的显示数量，默认为20条
	 */
	public JsonEntity queryJointInstancesByUserid(String userid , Integer nowPage , Integer maxPageRows);
	
	/**
	 * 按照 绑定的外部业务编号，查询对应的任务实例信息
	 * @param businessID 任务编号信息
	 * @return 返回的任务信息将会按照创建的先后顺序进行排序（按照任务的uid进行排序）
	 * @throws BizException 
	 */
	public JsonEntity queryJointInstancesByBusinessID(String businessID);
	
	/**
	 * 根据流程模版ID查询流程模板信息.
	 * @param tradeid 指定的行业信息。注意，一个行业下肯定只有一个行业默认的流程模板 
	 * @return 
	 */
	public JsonEntity queryArrangementByArrangementid(String arrangementId);
}
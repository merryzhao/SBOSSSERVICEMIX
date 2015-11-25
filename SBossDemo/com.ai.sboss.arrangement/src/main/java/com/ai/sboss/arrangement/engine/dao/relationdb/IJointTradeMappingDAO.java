package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;
import java.util.Map.Entry;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.orm.JointTradeMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * @author yinwenjie
 *
 */
public interface IJointTradeMappingDAO extends SystemDAO<JointTradeMappingEntity> {
	
	/**
	 * 获取一个指定的任务节点的tradeMapping集合信息。<br>
	 * @param jointuid tradeMapping对应的任务节点唯一编号信息
	 * @return 如果有符合编号的tradeMapping，将以对象的形式进行返回；其他情况下返回null
	 * @throws BizException
	 */
	public List<JointTradeMappingEntity> queryJointTradeMappingSet(String jointuid) throws BizException;
	
	/**
	 * 查询执行的任务模板和行业间的关系信息（只需要查询基本信息）。
	 * @param scope 必须穿入的模板返回信息，只可能有三个值
	 * @param tradeid industry | producer | consumer 
	 * @param jointid 指定的任务模板唯一编号
	 * @return 
	 * @throws BizException
	 */
	public JointTradeMappingEntity queryJointTradeByTradeid(String scope , String tradeid , String jointid) throws BizException;
	
	/**
	 * 绑定任务节点的行业信息。<br>
	 * 注意，这里只会新增行业信息，而不会删除之前的行业信息绑定
	 * @param trademapping 新的行业信息
	 * @throws BizException
	 */
	public void bindJointTrade(JointTradeMappingEntity trademapping) throws BizException;
	
	/**
	 * 绑定行业信息。<br>
	 * @param jointuid 需要更新的指定的任务唯一编号信息
	 * @param tradeInfo 最新的行业绑定关系。注意这是一个K-V的对应，K：tradeid；V：scope<br>
	 * scope有三个有效值：<br>
	 * 		industry：只能作为某个行业的默认服务，不能被服务者或者消费者的自定义流程引用<br>
	 * 		producer：可以作为行业的默认服务或者服务者的自定义流程，但是不能被消费者的自定义流程引用<br>
	 * 		consumer：可以作为行业的默认服务、服务者或者消费用的自定义流程<br>
	 * @throws BizException 
	 */
	public void bindJointTrade(String jointuid, Entry<String, String> tradeInfo) throws BizException;
	
	/**
	 * 释放绑定的行业集合<br>
	 * @param jointuid 需要释放行业信息的任务节点
	 * @return null
	 * @throws BizException
	 */
	public void releaseAllJointTradeMapping(String jointuid) throws BizException;

}
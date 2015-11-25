package com.ai.sboss.arrangement.engine.dao;

/**
 * 编排系统数据持久存储层的服务创建工厂。<br>
 * 通过这个抽象工厂，我们分离了持久层的实现方式，
 * 为日后我们编排系统从关系型数据库存储无缝移植到NoSQL数据库，打下基础<br>
 * 当然，从目前的设计情况看，都是由关系型数据库（mysql）进行存储
 * @author yinwenjie
 */
public abstract class ArrangementDAOAbstractFactory {
	/**
	 * 从构建工厂中获取一个编排系统流程数据操作相关的服务接口
	 * @return
	 */
	public abstract ArrangementDAOService getArrangementDAOService();

	/**
	 * 从构建工厂中获取一个编排系统实例数据操作相关的服务接口
	 * @return
	 */
	public abstract InstanceDAOService getInstanceDAOService();
	
	/**
	 * 从构建工厂中获取一个编排系统任务数据操作相关的服务接口
	 * @return
	 */
	public abstract JointDAOService getJointDAOService();

	/**
	 * 从构建工厂中获取一个编排系统流程参数数据操作相关的服务接口
	 * @return
	 */
	public abstract ParamsDAOService getParamsDAOService();
	
	/**
	 * 从构建工厂中获取一个编排系统日志数据操作相关的服务接口
	 * @return
	 */
	public abstract LogDAOService getLogDAOService();
}
package com.ai.sboss.arrangement.engine;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * 流程编排系统中，最主要的接口之一。通过命令方式实现流程实例的启动、终止、回退、流转等操作<br>
 * 以后随着流程系统的晚上，还会加入更多的命令种类
 * @author yinwenjie
 */
public interface ICommand {
	/**
	 * 命令对象被实例化后，在装入执行队列时，由AbstractCommadQueueManager调用执行的命令对象初始化工作。
	 * @throws BizException
	 */
	public void init() throws BizException;
	/**
	 * 正向命令执行调用。由AbstractCommadQueueManager调用执行
	 * @throws BizException
	 */
	public void execute() throws BizException;
	
	/**
	 * 反向命令，用于恢复执行对象的状态到命令执行前。<br>
	 * 由于编排系统中数据事务隔离性（command和command一般不在一个事务中）的特点。<br>
	 * 所以在正向命令执行失败后，AbstractCommadQueueManager会调用已经执行的command的undo方法，进行整个实例过程的回滚
	 * @throws BizException
	 */
	public void undo() throws BizException;
}
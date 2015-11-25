package com.ai.sboss.arrangement.event;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.support.ApplicationObjectSupport;

import com.ai.sboss.arrangement.exception.BizException;

/**
 * 事件监听工厂。实现这个工厂，可以对系统的事件模块进行隔离
 * @author yinwenjie
 */
public abstract class EventSourceAbstractFactory extends ApplicationObjectSupport {
	/**
	 * 必须进行设定的事件监听工厂的配置。<br>
	 * 不同的事件监听机制有不同的配置信息，所以ReceiveConfigure中到底有哪些属性，还需要看具体的监听机制
	 */
	private ReceiveConfigure configure;
	
	/**
	 * 为了保证这个FactoryBean在spring的加载过程中配置了scope=singlelong的设置。
	 * 我们使用并发计数器，保证这个FactoryBean再被第二次初始化时，系统会报错，并停止初始化
	 */
	private static AtomicInteger BEAN_OBJECT_NUMBER = new AtomicInteger(0);
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.EventSourceAbstractFactory#init()
	 */
	public void init() {
		Integer instanceNumber = EventSourceAbstractFactory.BEAN_OBJECT_NUMBER.getAndIncrement();
		//说明这个FactoryBean被初始化了第二次。这个时候就要报错了
		if(instanceNumber > 0) {
			throw new RuntimeException("EventSourceAMQPFactoryBean must instance once。（ scope=\"singleton\"）");
		}
	}
	
	/**
	 * 这个抽象方法规定了实现事件监听工厂的具体工厂，必须可以添加一个“流程异常”的监听实现
	 * @param listener 添加的新的监听对象。注意，如果这个对象实力已经存在于监听容器中了，那么本次添加监听的操作将被忽略
	 * @throws BizException
	 */
	public abstract void addArrangementExceptionListener(ArrangementExceptionListener listener) throws BizException;
	
	/**
	 * @param listener
	 * @throws BizException
	 */
	public abstract void removeArrangementExceptionListener(ArrangementExceptionListener listener) throws BizException;
	
	/**
	 * 这个抽象方法规定了实现事件监听工厂的具体工厂，必须可以添加一个“流程实例开始事件”的监听实现
	 * @param listener 添加的新的监听对象。注意，如果这个对象实力已经存在于监听容器中了，那么本次添加监听的操作将被忽略
	 * @throws BizException
	 */
	public abstract void addArrangementBeginListener(ArrangementBeginListener listener) throws BizException;
	
	/**
	 * @param listener
	 * @throws BizException
	 */
	public abstract void removeArrangementBeginListener(ArrangementBeginListener listener) throws BizException;
	
	/**
	 * 这个抽象方法规定了实现事件监听工厂的具体工厂，必须可以添加一个“流程实例结束事件”的监听实现
	 * @param listener 添加的新的监听对象。注意，如果这个对象实力已经存在于监听容器中了，那么本次添加监听的操作将被忽略
	 * @throws BizException
	 */
	public abstract void addArrangementEndListener(ArrangementEndListener listener) throws BizException;

	/**
	 * @param listener
	 * @throws BizException
	 */
	public abstract void removeArrangementEndListener(ArrangementEndListener listener) throws BizException;
	
	/**
	 * 这个抽象方法规定了实现事件监听工厂的具体工厂，必须可以添加一个“流程实例流转事件”的监听实现
	 * @param listener 添加的新的监听对象。注意，如果这个对象实力已经存在于监听容器中了，那么本次添加监听的操作将被忽略
	 * @throws BizException
	 */
	public abstract void addArrangementFlowListener(ArrangementFlowListener listener) throws BizException;
	
	/**
	 * @param listener
	 * @throws BizException
	 */
	public abstract void removeArrangementFlowListener(ArrangementFlowListener listener) throws BizException;
	
	/**
	 * @return
	 */
	protected ReceiveConfigure getConfigure() {
		return configure;
	}

	/**
	 * @param configure
	 */
	public void setConfigure(ReceiveConfigure configure) {
		this.configure = configure;
	}
}
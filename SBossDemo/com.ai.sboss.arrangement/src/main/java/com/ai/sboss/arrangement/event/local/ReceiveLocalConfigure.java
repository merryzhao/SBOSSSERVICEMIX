package com.ai.sboss.arrangement.event.local;

import java.util.concurrent.ThreadPoolExecutor;

import com.ai.sboss.arrangement.event.ReceiveConfigure;

/**
 * “本地实现”的事件监听工厂的配置信息（通过spring就可以轻松脱耦了）
 * @author yinwenjie
 */
public class ReceiveLocalConfigure implements ReceiveConfigure {
	/**
	 * 通知线程池。
	 * 负责并发通知注册了相关事件监听的listener
	 */
	private ThreadPoolExecutor listenerExecutors;
	
	/**
	 * 事件消费者线程的bean名称，还是通过配置方式注入
	 * TODO 目前这个依赖的位置还需要改进设计
	 */
	private String eventConsumerThreadName;
	
	/**
	 * 事件通知者线程的名称（这个统治者一定是Thead）
	 * TODO 目前这个依赖的位置还需要改进设计
	 */
	private String eventNotificationThreadName;

	protected ThreadPoolExecutor getListenerExecutors() {
		return listenerExecutors;
	}

	public void setListenerExecutors(ThreadPoolExecutor listenerExecutors) {
		this.listenerExecutors = listenerExecutors;
	}

	protected String getEventConsumerThreadName() {
		return eventConsumerThreadName;
	}

	public void setEventConsumerThreadName(String eventConsumerThreadName) {
		this.eventConsumerThreadName = eventConsumerThreadName;
	}

	public String getEventNotificationThreadName() {
		return eventNotificationThreadName;
	}

	public void setEventNotificationThreadName(String eventNotificationTheadName) {
		this.eventNotificationThreadName = eventNotificationTheadName;
	}
}
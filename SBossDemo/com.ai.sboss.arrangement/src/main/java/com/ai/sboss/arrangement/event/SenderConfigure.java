package com.ai.sboss.arrangement.event;

/**
 * 事件发送工厂的配置接口。由于事件发送机制的不同实现（本地生产-消费模型、kafka消息队列、AMQP队列、zookeeper-watcher），所以
 * 配置项也不相同，所以这个接口只是一个标记接口，便于具体的事件发送工厂取得配置信息即可
 * @author yinwenjie
 */
public interface SenderConfigure {

	public ArrangementBeginEventSender getBeginEventSender();
	
	/**
	 * @return
	 */
	public ArrangementEndEventSender getEndEventSender();

	public ArrangementExceptionEventSender getExceptionEventSender();

	public ArrangementFlowEventSender getFlowEventSender();

}

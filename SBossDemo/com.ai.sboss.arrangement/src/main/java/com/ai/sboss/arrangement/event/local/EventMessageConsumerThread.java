package com.ai.sboss.arrangement.event.local;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.ai.sboss.arrangement.engine.startup.SystemStartupContextListener;
import com.ai.sboss.arrangement.event.ArrangementBeginListener;
import com.ai.sboss.arrangement.event.ArrangementEndListener;
import com.ai.sboss.arrangement.event.ArrangementEvent;
import com.ai.sboss.arrangement.event.ArrangementExceptionListener;
import com.ai.sboss.arrangement.event.ArrangementFlowListener;
import com.ai.sboss.arrangement.event.BeginEvent;
import com.ai.sboss.arrangement.event.EndEvent;
import com.ai.sboss.arrangement.event.ExceptionEvent;
import com.ai.sboss.arrangement.event.FlowEvent;

/**
 * “本地事件”实现中，这个消息消费者线程用于处理送入EventMessageHeap本地队列事件<br>
 * 并操作通知线程池
 * @author yinwenjie
 */
public class EventMessageConsumerThread implements Runnable {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(EventMessageConsumerThread.class);
	
	/**
	 * 待处理的事件队列
	 */
	private EventMessageHeap eventMessageHeap = EventMessageHeap.getNewInstance();
	
	/**
	 * 需要进行异常监听的集合放在这里
	 */
	private ConcurrentLinkedQueue<ArrangementExceptionListener>  exceptionListeners = null;
	
	/**
	 * 需要进行起始监听的集合放在这里
	 */
	private ConcurrentLinkedQueue<ArrangementBeginListener> beginListeners = null;
	
	/**
	 * 需要进行“结束事件”监听的对象集合放在这里
	 */
	private ConcurrentLinkedQueue<ArrangementEndListener> endListeners = null;
	
	/**
	 * 需要进行“流转事件”监听的对象集合放在这里
	 */
	private ConcurrentLinkedQueue<ArrangementFlowListener> flowListeners = null;
	
	/**
	 * 事件通知者线程池
	 */
	private ThreadPoolExecutor notificationTheadPool;
	
	/**
	 * 事件通知者线程在配置信息中的名字
	 */
	private String messageNotificationTheadName;
	
	/**
	 * 为了保证“本地事件”实现机制中，只有一个唯一的时间消费者，我们使用一个并发计数器来管控该类实例的数量
	 */
	private static AtomicInteger BEAN_OBJECT_NUMBER = new AtomicInteger(0);
	
	public EventMessageConsumerThread() {
		Integer instanceNumber = EventMessageConsumerThread.BEAN_OBJECT_NUMBER.getAndIncrement();
		//说明这个EventMessageConsumerThread被初始化了第二次。这个时候就要报错了
		if(instanceNumber > 0) {
			throw new RuntimeException("EventMessageConsumerThread must instance once。（ scope=\"singleton\"）");
		}
	}
	
	@Override
	public void run() {
		/*
		 * “本地事件”实现机制中，唯一的事件消费者怎么处理消费信息呢？步骤如下：
		 * 1、一直等待，直到“待发送事件消息”队列中出现至少一个信息，这个时候eventMessageHeap会发送notify信息
		 * 2、取出一条信息，判断这个信息的事件类型A
		 * 3、从事件类型A，对应的ConcurrentLinkedQueue中取出需要通知的listeners，注意一个listener会有一个Thread负责通知
		 * 4、循环以上工作
		 * */
		//4、================
		while(true) {
			//1、================
			while(this.eventMessageHeap.messageEventSize() == 0) {
				synchronized (this.eventMessageHeap) {
					try {
						this.eventMessageHeap.wait();
					} catch (InterruptedException e) {
						EventMessageConsumerThread.LOGGER.error(e.getMessage() , e);
					}
				}
			}
			
			//2、================
			for(int index = 0 ; index < this.eventMessageHeap.messageEventSize() ; index++) {
				ArrangementEvent arrangementEvent = this.eventMessageHeap.pullEventMessage();
				
				if(arrangementEvent instanceof BeginEvent) {
					//如果条件成立，说明没有需要监听的目标对象
					if(this.beginListeners == null || this.beginListeners.isEmpty()) {
						continue;
					}
					this.executionNotificationTheadPool(this.beginListeners, arrangementEvent);
				} else if(arrangementEvent instanceof EndEvent) {
					//如果条件成立，说明没有需要监听的目标对象
					if(this.endListeners == null || this.endListeners.isEmpty()) {
						continue;
					}
					this.executionNotificationTheadPool(this.endListeners, arrangementEvent);
				} else if(arrangementEvent instanceof ExceptionEvent) {
					//如果条件成立，说明没有需要监听的目标对象
					if(this.exceptionListeners == null || this.exceptionListeners.isEmpty()) {
						continue;
					}
					this.executionNotificationTheadPool(this.exceptionListeners, arrangementEvent);
				} else if(arrangementEvent instanceof FlowEvent) {
					//如果条件成立，说明没有需要监听的目标对象
					if(this.flowListeners == null || this.flowListeners.isEmpty()) {
						continue;
					}
					this.executionNotificationTheadPool(this.flowListeners, arrangementEvent);
				}
			}
		}
	}
	
	public void setEventMessageHeap(EventMessageHeap eventMessageHeap) {
		this.eventMessageHeap = eventMessageHeap;
	}

	public void setExceptionListeners(ConcurrentLinkedQueue<ArrangementExceptionListener> exceptionListeners) {
		this.exceptionListeners = exceptionListeners;
	}

	public void setBeginListeners(ConcurrentLinkedQueue<ArrangementBeginListener> beginListeners) {
		this.beginListeners = beginListeners;
	}

	public void setEndListeners(ConcurrentLinkedQueue<ArrangementEndListener> endListeners) {
		this.endListeners = endListeners;
	}

	public void setFlowListeners(ConcurrentLinkedQueue<ArrangementFlowListener> flowListeners) {
		this.flowListeners = flowListeners;
	}

	public void setNotificationTheadPool(ThreadPoolExecutor notificationTheadPool) {
		this.notificationTheadPool = notificationTheadPool;
	}

	public void setMessageNotificationTheadName(String messageNotificationTheadName) {
		this.messageNotificationTheadName = messageNotificationTheadName;
	}

	/**
	 * 泛化的通知者线程池的启动执行器
	 * @param concurrentLinkedQueue
	 * @param event
	 * TODO 这里前后做了2层的类型判断，是不是有必要把这个接口拆分为5个，避免多次判断带来的性能消耗？可能设计上还需要做一些调整
	 */
	private void executionNotificationTheadPool(Collection<? extends Object> concurrentLinkedQueue , ArrangementEvent event) {
		ApplicationContext applicationContext = SystemStartupContextListener.getApplicationContext();
		//开始初始化
		for (Object listener : concurrentLinkedQueue) {
			EventMessageNotificationThead notificationThead = (EventMessageNotificationThead)applicationContext.getBean(this.messageNotificationTheadName);
			if(event instanceof BeginEvent) {
				notificationThead.setArrangementBeginListener((ArrangementBeginListener)listener);
			} else if(event instanceof EndEvent) {
				notificationThead.setArrangementEndListener((ArrangementEndListener)listener);
			} else if(event instanceof ExceptionEvent) {
				notificationThead.setArrangementExceptionListener((ArrangementExceptionListener)listener);
			} else if(event instanceof FlowEvent) {
				notificationThead.setArrangementFlowListener((ArrangementFlowListener)listener);
			}
			notificationThead.setEvent(event);
			notificationThead.setName("EventMessage-Notification-Thead");
			
			//TODO 这里直接到线程池执行就行了，后续的版本再控制线程池中线程的执行状态
			this.notificationTheadPool.execute(notificationThead);
		}
	}
	
}

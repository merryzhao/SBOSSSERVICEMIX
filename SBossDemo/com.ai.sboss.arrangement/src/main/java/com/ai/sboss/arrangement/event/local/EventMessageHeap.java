package com.ai.sboss.arrangement.event.local;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.sboss.arrangement.event.ArrangementEvent;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * “本地事件”驱动的待发送事件信息队列。全系统唯一
 * @author yinwenjie
 */
final class EventMessageHeap { 
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(EventMessageHeap.class);
	
	/**
	 * 存储当前整个系统中的待发送给消息接收方的事件信息。
	 * 使用一个线程优化的队列，有助于加快队列中待处理消息的消化
	 */
	private static final ConcurrentLinkedQueue<ArrangementEvent> EVENT_MESSAGES = new ConcurrentLinkedQueue<ArrangementEvent>();
	
	/**
	 * 全系统唯一的实例
	 */
	private static final EventMessageHeap EVENT_MESSAGE_HEAP = new EventMessageHeap();
	
	/**
	 * 私有化的构造函数，保证其他地方不能初始化这个类的实例
	 */
	private EventMessageHeap() { 
		
	}
	
	/**
	 * 获取系统中唯一的“本地消息事件队列”实例。<br>
	 * 为什么要实例呢？因为方便做锁
	 * @return
	 */
	public static EventMessageHeap getNewInstance() { 
		return EventMessageHeap.EVENT_MESSAGE_HEAP;
	}
	
	/**
	 * 向队列推送一个事件消息。这是消息队列会试图激活一个等待的消费者线程（从设置的的线程池中）
	 * @param event
	 */
	public void pushNewEventMessage(ArrangementEvent event) throws BizException { 
		if(event == null) {
			throw new BizException("错误的事件信息，请检查", ResponseCode._501);
		}
		
		EventMessageHeap.EVENT_MESSAGES.add(event);
		//为了简单处理，这里只有一个等候的消息消费者，（后续的做法将采用线程池）
		synchronized(this) {
			this.notify();
		}
		EventMessageHeap.LOGGER.info("推入并通知==============event：" + event);
	}
	
	/**
	 * 或者当前“待通知消息队列中”，没有多少消息没有发送出去
	 * @return
	 * @throws BizException
	 */
	public Integer messageEventSize() { 
		return EventMessageHeap.EVENT_MESSAGES.size();
	}
	
	/**
	 * @return
	 * @throws BizException
	 */
	public ArrangementEvent pullEventMessage() { 
		return EventMessageHeap.EVENT_MESSAGES.poll();
	}
}
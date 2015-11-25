package com.ai.sboss.arrangement.event;

/**
 * 流程启动事件的各属性描述
 * @author yinwenjie
 *
 */
public class BeginEvent extends ArrangementEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6910722207819477129L;

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.ArrangementEvent#getEventType()
	 */
	@Override
	public EventType getEventType() {
		return EventType.BEGIN;
	}
}
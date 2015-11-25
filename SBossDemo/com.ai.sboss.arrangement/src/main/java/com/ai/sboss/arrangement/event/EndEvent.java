package com.ai.sboss.arrangement.event;

/**
 * 流程结束事件的各种属性描述
 * @author yinwenjie
 *
 */
public class EndEvent extends ArrangementEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7004464510758539799L;

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.ArrangementEvent#getEventType()
	 */
	@Override
	public EventType getEventType() {
		return EventType.END;
	}

}
package com.ai.sboss.arrangement.event;

import java.util.HashMap;
import java.util.Map;

/**
 * 记录了事件类型。并且这个枚举类支持从json到entity或者从entity到json的转换
 * @author yinwenjie
 */
public enum EventType {
	
	BEFOREFLOW("beforeflow"),AFTERFLOW("afterflow"),EXCEPTION("exception"),BEGIN("begin"),END("end");
	
	private String code;

	EventType(String code) {
        this.code = code;
    }

    public String getEventType() {
        return this.code;
    }

    /**
     * 该私有静态方法用于映射字符串和枚举信息的关系
     */
    private static final Map<EventType, String> stringToEnum = new HashMap<EventType, String>();
    static {
        for (EventType blah : values()) {
            stringToEnum.put(blah, blah.toString());
        }
    }

    @Override
    public String toString() {
        return code;
    }
}
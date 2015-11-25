package com.ai.sboss.arrangement.event;

import java.io.Serializable;
import java.util.Set;

import com.ai.sboss.arrangement.entity.AbstractEntity;
import com.ai.sboss.arrangement.entity.orm.InstanceContextParamEntity;

/**
 * 所有编排系统事件描述类的超类
 * @author yinwenjie
 */
public abstract class ArrangementEvent extends AbstractEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4025501073543595972L;
	
	/**
	 * 业务实例的流程编号
	 */
	private String arrangementInstanceId;
	
	/**
	 * 业务流程的定义编号
	 */
	private String arrangementId;
	
	/**
	 * 当前的流程实例的上下文。上下文中主要存储的是流程实例的全局变量情况（已经由的变量值才进行存储）。
	 */
	private Set<InstanceContextParamEntity> arrangementInstanceContext;

	public String getArrangementInstanceId() {
		return arrangementInstanceId;
	}

	public void setArrangementInstanceId(String arrangementInstanceId) {
		this.arrangementInstanceId = arrangementInstanceId;
	}

	public String getArrangementId() {
		return arrangementId;
	}

	public void setArrangementId(String arrangementId) {
		this.arrangementId = arrangementId;
	}
	
	/**
	 * @return the arrangementInstanceContext
	 */
	public Set<InstanceContextParamEntity> getArrangementInstanceContext() {
		return arrangementInstanceContext;
	}

	/**
	 * @param arrangementInstanceContext the arrangementInstanceContext to set
	 */
	public void setArrangementInstanceContext(
			Set<InstanceContextParamEntity> arrangementInstanceContext) {
		this.arrangementInstanceContext = arrangementInstanceContext;
	}

	/**
	 * 每个下层的事件描述类都要实现这个接口，用来说明这个事件的类型
	 * @return
	 */
	public abstract EventType getEventType();
}

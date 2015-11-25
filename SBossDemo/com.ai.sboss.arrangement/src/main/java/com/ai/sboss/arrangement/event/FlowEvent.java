package com.ai.sboss.arrangement.event;

/**
 * 流程流转过程事件的各种属性描述
 * @author yinwenjie
 */
public class FlowEvent extends ArrangementEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7117604961249482376L;
	
	/**
	 * 
	 */
	private EventType eventType;
	
	/**
	 * 当前任务实例对应的人任务模板的编号
	 */
	private String jointId;
	
	/**
	 * 当前任务实例的唯一编号
	 */
	private String jointInstanceId;
	
	/**
	 * 可能设置的这个任务在任何流程中的绝对偏移时间，单位毫秒
	 */
	private Long absOffsettime;
	
	/**
	 * 可能设置的这个任务在任何流程中的相对偏移时间，单位毫秒
	 */
	private Long relateOffsettime;
	
	/**
	 * 默认的任务偏移量阀值提示（可能没有）
	 */
	private String promptOffsettime;
	
	/**
	 * 当前任务的执行者
	 */
	private String executor;
	
	/**
	 * 时间线的可见性：<br>
	 * 		producer：服务者可见<br>
	 * 		consumer：消费者可见<br>
	 * 		both：两者都可见，如果不设置表示两者都可见
	 */
	private String offsetVisible;
	
	/**
	 * 展开的子页面类型ID，这个id只进行记录就可以了
	 */
	private Integer expandTypeId;
	
	/**
	 * 这个任务实例的创建者。由于在系统中，流程实例和任务实例是一起创建的，所以肯定的：<br>
	 * 同：编排流程实例的创建者
	 */
	private String creator;
	
	/**
	 * 在任务实例创建时，通过这个任务实例关联的“流程实例创建时间”和“实例的绝对时间”偏移计算，得到的这个任务实例可能的执行时间<br>
	 * 注意，如果这个实例是“没有executor执行人的‘自动任务’”，那么这个“可能的执行时间”和上一个任务实例“可能的执行时间”是一致的。<br>
	 * 如果这又是第一个任务，那么“可能的执行时间”就是这个任务实例对应的流程实例的 启动时间。
	 */
	private Long expectedExeTime;
	
	/**
	 * @return the jointId
	 */
	public String getJointId() {
		return jointId;
	}

	/**
	 * @param jointId the jointId to set
	 */
	public void setJointId(String jointId) {
		this.jointId = jointId;
	}

	/**
	 * @return the jointInstanceId
	 */
	public String getJointInstanceId() {
		return jointInstanceId;
	}

	/**
	 * @param jointInstanceId the jointInstanceId to set
	 */
	public void setJointInstanceId(String jointInstanceId) {
		this.jointInstanceId = jointInstanceId;
	}

	/**
	 * @return the absOffsettime
	 */
	public Long getAbsOffsettime() {
		return absOffsettime;
	}

	/**
	 * @param absOffsettime the absOffsettime to set
	 */
	public void setAbsOffsettime(Long absOffsettime) {
		this.absOffsettime = absOffsettime;
	}

	/**
	 * @return the relateOffsettime
	 */
	public Long getRelateOffsettime() {
		return relateOffsettime;
	}

	/**
	 * @param relateOffsettime the relateOffsettime to set
	 */
	public void setRelateOffsettime(Long relateOffsettime) {
		this.relateOffsettime = relateOffsettime;
	}

	/**
	 * @return the promptOffsettime
	 */
	public String getPromptOffsettime() {
		return promptOffsettime;
	}

	/**
	 * @param promptOffsettime the promptOffsettime to set
	 */
	public void setPromptOffsettime(String promptOffsettime) {
		this.promptOffsettime = promptOffsettime;
	}

	/**
	 * @return the executor
	 */
	public String getExecutor() {
		return executor;
	}

	/**
	 * @param executor the executor to set
	 */
	public void setExecutor(String executor) {
		this.executor = executor;
	}

	/**
	 * @return the offsetVisible
	 */
	public String getOffsetVisible() {
		return offsetVisible;
	}

	/**
	 * @param offsetVisible the offsetVisible to set
	 */
	public void setOffsetVisible(String offsetVisible) {
		this.offsetVisible = offsetVisible;
	}

	/**
	 * @return the expandTypeId
	 */
	public Integer getExpandTypeId() {
		return expandTypeId;
	}

	/**
	 * @param expandTypeId the expandTypeId to set
	 */
	public void setExpandTypeId(Integer expandTypeId) {
		this.expandTypeId = expandTypeId;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the expectedExeTime
	 */
	public Long getExpectedExeTime() {
		return expectedExeTime;
	}

	/**
	 * @param expectedExeTime the expectedExeTime to set
	 */
	public void setExpectedExeTime(Long expectedExeTime) {
		this.expectedExeTime = expectedExeTime;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.ArrangementEvent#getEventType()
	 */
	@Override
	public EventType getEventType() {
		return this.eventType;
	}

	/**
	 * @param eventType
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
}

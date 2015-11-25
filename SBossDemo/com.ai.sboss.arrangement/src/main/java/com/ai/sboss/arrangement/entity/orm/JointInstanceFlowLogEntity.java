package com.ai.sboss.arrangement.entity.orm;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.ai.sboss.arrangement.entity.UUIDEntity;

/**
 * 这个实体用来记录流转日志。即什么时间、由谁、从哪个实例流转到了那个实例、流转类型是什么（正向流转还是逆向）<br>
 * 并且这个日志实例还是上下文日志实例的记录基础<br>
 * 这张表的读写频度是最高的，所以尽量减少表间外键关联，做成独立日志表（上下文日志表同样）
 * @author yinwenjie
 */
@Entity
@Table(name="L_JOINTINSTANCEFLOWLOG")
public class JointInstanceFlowLogEntity extends UUIDEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2728935567880431703L;
	
	/**
	 * 当前流转日志的时间，即流程实例被执行的事件<br>
	 * 要用这个排序的，所以要有一个索引
	 */
	@Column(name="FLOWEXETIME" , nullable=false)
	@Index(name="L_JOINTINSTANCEFLOWLOG_INDEX_FLOWEXETIME" , columnNames="FLOWEXETIME")
	private Date flowExeTime;
	
	/**
	 * 任务的执行人（可能没有执行人，比如这是一个“自动任务”）
	 */
	@Column(name="EXECUTOR" , length=50 , nullable=false)
	private String executor;
	
	/**
	 * 对应的“流程实例”编号。这里只记录编号
	 */
	@Column(name="ARRANGEMENTINSTANCEID" , length=100 , nullable=false)
	private String arrangementInstanceId;
	
	/**
	 * 流转的“源任务实例”编号。这里只记录编号
	 */
	@Column(name="FORMJOINTINSTANCEID" , length=100 , nullable=false)
	private String formJointInstanceId;
	
	/**
	 * 流转的“目标任务实例”编号。这里只记录编号<br>
	 * 注意可能没有，没有，则说明这次流转的是流程实例最后一个任务实例
	 */
	@Column(name="TOJOINTINSTANCEID" , length=100 , nullable=true)
	private String toJointInstanceId;
	
	/**
	 * 只有两种流转类型：forward（正向流转）和backward（逆向流转）<br>
	 * 默认是forward（正向流转）
	 */
	@Column(name="FLOWTYPE" , length=50 , nullable=false)
	private String flowType;
	
	/**
	 * 对应的上下文变量日志的变化情况
	 */
	@OneToMany(fetch=FetchType.LAZY , mappedBy="jointInstanceFlowLog")
	private Set<InstanceContextParamLogEntity> contextParamLogs;

	/**
	 * @return the flowExeTime
	 */
	public Date getFlowExeTime() {
		return flowExeTime;
	}

	/**
	 * @param flowExeTime the flowExeTime to set
	 */
	public void setFlowExeTime(Date flowExeTime) {
		this.flowExeTime = flowExeTime;
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
	 * @return the arrangementInstanceId
	 */
	public String getArrangementInstanceId() {
		return arrangementInstanceId;
	}

	/**
	 * @param arrangementInstanceId the arrangementInstanceId to set
	 */
	public void setArrangementInstanceId(String arrangementInstanceId) {
		this.arrangementInstanceId = arrangementInstanceId;
	}

	/**
	 * @return the formJointInstanceId
	 */
	public String getFormJointInstanceId() {
		return formJointInstanceId;
	}

	/**
	 * @param formJointInstanceId the formJointInstanceId to set
	 */
	public void setFormJointInstanceId(String formJointInstanceId) {
		this.formJointInstanceId = formJointInstanceId;
	}

	/**
	 * @return the toJointInstanceId
	 */
	public String getToJointInstanceId() {
		return toJointInstanceId;
	}

	/**
	 * @param toJointInstanceId the toJointInstanceId to set
	 */
	public void setToJointInstanceId(String toJointInstanceId) {
		this.toJointInstanceId = toJointInstanceId;
	}

	/**
	 * @return the flowType
	 */
	public String getFlowType() {
		return flowType;
	}

	/**
	 * @param flowType the flowType to set
	 */
	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	/**
	 * @return the contextParamLogs
	 */
	public Set<InstanceContextParamLogEntity> getContextParamLogs() {
		return contextParamLogs;
	}

	/**
	 * @param contextParamLogs the contextParamLogs to set
	 */
	public void setContextParamLogs(Set<InstanceContextParamLogEntity> contextParamLogs) {
		this.contextParamLogs = contextParamLogs;
	}
}
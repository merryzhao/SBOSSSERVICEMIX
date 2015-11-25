package com.ai.sboss.arrangement.entity.orm;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.ai.sboss.arrangement.entity.UUIDEntity;

/**
 * 表示编排系统中的一个任务节点
 * @author yinwenjie
 */
@Entity
@Table(name="A_JOINT")
public class JointEntity extends UUIDEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1205447170354866535L;
	
	/**
	 * 显示名称
	 */
	@Column(name="DISPALYNAME" , length=100 , nullable=true)
	private String displayName;
	
	/**
	 * 对应的Camel服务
	 */
	@Column(name="CAMELURI" , length=500 , nullable=true)
	private String camelUri;
	
	/**
	 * 这个任务节点已经设置的出参信息
	 */
	@OneToMany(fetch=FetchType.LAZY , mappedBy="joint")
	private Set<JointOutputParamsEntity> outputParams;
	
	/**
	 * 这个任务节点已经设置的出参信息
	 */
	@OneToMany(fetch=FetchType.LAZY , mappedBy="joint")
	private Set<JointInputParamsEntity> inputParams;
	
	/**
	 * 这个任务节点对应的行业信息(多个行业)
	 */
	@OneToMany(fetch=FetchType.LAZY , mappedBy="joint")
	private Set<JointTradeMappingEntity> trades;
	
	/**
	 * 这个任务节点对应的实例化任务节点
	 */
	@OneToMany(fetch=FetchType.LAZY , mappedBy="joint")
	private Set<JointInstanceEntity> jointinstances;
	
	/**
	 * 这个任务节点已经绑定的“流程模板”信息
	 */
	@OneToMany(fetch=FetchType.LAZY , mappedBy="joint")
	private Set<ArrangementJointMappingEntity> arrangementJointMappings;

	/**
	 * 任务默认执行者，如果这个任务没有任务执行者，意味着是一个自动流转的编排任务
	 * 如果填写了任务默认执行者，那么其只可能有三个值：
	 * 		industry：这个任务由管理员执行
	 * 		producer：这个任务由服务提供者执行
	 * 		consumer：这个任务由最终客户执行
	 */
	@Column(name="EXECUTOR" , length=100 , nullable=true)
	private String executor;
	
	/**
	 * 默认的任务偏移量阀值提示（可能没有）<br>
	 * 任务节点的时间线描述
	 */
	@Column(name="PROMPTOFFSETTIME" , length=500 , nullable=true)
	private String promptOffsettime;
	
	/**
	 * 可能设置的这个任务在任何流程中的绝对偏移时间，单位毫秒
	 */
	@Column(name="ABSOFFSETTIME" , nullable=true)
	private Long absOffsettime;
	
	/**
	 * 可能设置的这个任务在任何流程中的相对偏移时间，单位毫秒
	 */
	@Column(name="RELATEOFFSETTIME" , nullable=true)
	private Long relateOffsettime; 
	
	/**
	 * 时间线节点名称（请注意其与节点名称的区别）
	 */
	@Column(name="OFFSETTITLE" , length=200 , nullable=true)
	private String offsetTitle;
	
	/**
	 * 时间线的可见性：<br>
	 * 		producer：服务者可见<br>
	 * 		consumer：消费者可见<br>
	 * 		both：两者都可见，如果不设置表示两者都可见
	 */
	@Column(name="OFFSETVISIBLE" , length=200 , nullable=true)
	private String offsetVisible;
	
	/**
	 * 展开的子页面类型ID，这个id只进行记录就可以了
	 */
	@Column(name="EXPANDTYPEID" , nullable=true)
	private Integer expandTypeId;

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the outputParams
	 */
	public Set<JointOutputParamsEntity> getOutputParams() {
		return outputParams;
	}

	/**
	 * @param outputParams the outputParams to set
	 */
	public void setOutputParams(Set<JointOutputParamsEntity> outputParams) {
		this.outputParams = outputParams;
	}

	/**
	 * @return the inputParams
	 */
	public Set<JointInputParamsEntity> getInputParams() {
		return inputParams;
	}

	/**
	 * @param inputParams the inputParams to set
	 */
	public void setInputParams(Set<JointInputParamsEntity> inputParams) {
		this.inputParams = inputParams;
	}

	/**
	 * @return the trades
	 */
	public Set<JointTradeMappingEntity> getTrades() {
		return trades;
	}

	/**
	 * @param trades the trades to set
	 */
	public void setTrades(Set<JointTradeMappingEntity> trades) {
		this.trades = trades;
	}

	/**
	 * @return the jointinstances
	 */
	public Set<JointInstanceEntity> getJointinstances() {
		return jointinstances;
	}

	/**
	 * @param jointinstances the jointinstances to set
	 */
	public void setJointinstances(Set<JointInstanceEntity> jointinstances) {
		this.jointinstances = jointinstances;
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
	 * @return the offsetTitle
	 */
	public String getOffsetTitle() {
		return offsetTitle;
	}

	/**
	 * @param offsetTitle the offsetTitle to set
	 */
	public void setOffsetTitle(String offsetTitle) {
		this.offsetTitle = offsetTitle;
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
	
	public String getCamelUri() {
		return camelUri;
	}

	public void setCamelUri(String camelUri) {
		this.camelUri = camelUri;
	}
	/**
	 * @return the arrangementJointMappings
	 */
	public Set<ArrangementJointMappingEntity> getArrangementJointMappings() {
		return arrangementJointMappings;
	}

	/**
	 * @param arrangementJointMappings the arrangementJointMappings to set
	 */
	public void setArrangementJointMappings(Set<ArrangementJointMappingEntity> arrangementJointMappings) {
		this.arrangementJointMappings = arrangementJointMappings;
	}
}

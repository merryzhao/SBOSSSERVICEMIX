package com.ai.sboss.arrangement.entity.orm;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.ai.sboss.arrangement.entity.UUIDEntity;

/**
 * 表示编排系统中的一个任务流程实例
 * @author chaos
 */
@Entity
@Table(name="I_ARRANGEMENTINSTANCE")
public class ArrangementInstanceEntity extends UUIDEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7728877773570058232L;
	
	/**
	 * 编排流程实例的创建者
	 */
	@Column(name="CREATOR" , length=100 , nullable=false)
	private String creator;
	
	/**
	 * 编排流程实例的创建时间
	 */
	@Column(name="CREATE_TIME", nullable=false)
	private Long createTime;

	/**
	 * 编排流程实例的结束时间
	 */
	@Column(name="END_TIME", nullable=true)
	private Long endTime;
	
	/**
	 * 编排流程实例的状态。分为几种：
	 * @waiting 这个流程实例已经完成了初始化，等待执行
	 * @executing 这个流程实例，正在执行。
	 * @revoked 这个流程实例之前已经正常执行完成，但操作者进行了回退操作，这个流程实例的执行状态已经被回退。从业务执行特性来看，相当于waiting状态
	 * @completed 这个流程实例已经正常执行完成，并且其对应的所有流程实例也已经全部执行完成。
	 * @terminated 这个流程实例已经被操作者强制终止了。
	 */
	@Column(name="STATE", nullable=false)
	private String statu;
	
	/**
	 * 流程实例对应的业务信息，
	 * 这个业务信息编号是从使用流程系统的第三方系统传入的，用来完成业务id和流程的关联。<br>
	 * 并且会作为查询条件提供出来
	 */
	@Column(name="BUSINESSID", length=128 , nullable=true , unique=true)
	@Index(name="A_ARRANGEMENTINSTANCE_BUSINESSID" , columnNames={"BUSINESSID"})
	private String businessID;
	
	/**
	 * 展现流程实例的中文名称。默认来自于这个流程实例关联的流程模板。<br>
	 * 当然也可以由用户进行设置
	 */
	@Column(name="DISPLAYNAME" , length=100 , nullable=false)
	private String displayName;
	
	/**
	 * 创建者范围，使用industry、producer、consumer，标明创建者的身份
	 */
	@Column(name="CREATORSCOPE" , length=20 , nullable=false)
	private String creatorScope;
	
	/**
	 * 这个流程实例对应的流程模板对象
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ARRANGEMENT" , nullable=false)
	private ArrangementEntity arrangement;
	
	/**
	 * 可能关联的父级流程实例的编号。这种情况建于存在子流程的情况。
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARENTINST" , nullable=true)
	private ArrangementInstanceEntity parentInstance;
	
	/**
	 * 这个流程实例对应的任务实例节点对象
	 */
	@OneToMany(fetch=FetchType.LAZY, mappedBy="arrangementInstance")
	private Set<JointInstanceEntity> jointInstances;
	
	/**
	 * 这个流程实例所关联的子流程节点实例集合（没有顺序）
	 */
	@OneToMany(fetch=FetchType.LAZY, mappedBy="parentInstance")
	private Set<ArrangementInstanceEntity> childArrangementInstances;

	/**
	 * @return the jointInstances
	 */
	public Set<JointInstanceEntity> getJointInstances() {
		return jointInstances;
	}

	/**
	 * @param jointInstances the jointInstances to set
	 */
	public void setJointInstances(Set<JointInstanceEntity> jointInstances) {
		this.jointInstances = jointInstances;
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
	 * @return the createTime
	 */
	public Long getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the endTime
	 */
	public Long getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the statu
	 */
	public String getStatu() {
		return statu;
	}

	/**
	 * @param statu the statu to set
	 */
	public void setStatu(String statu) {
		this.statu = statu;
	}
	
	/**
	 * @return the businessID
	 */
	public String getBusinessID() {
		return businessID;
	}

	/**
	 * @param businessID the businessID to set
	 */
	public void setBusinessID(String businessID) {
		this.businessID = businessID;
	}

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
	 * @return the creatorScope
	 */
	public String getCreatorScope() {
		return creatorScope;
	}

	/**
	 * @param creatorScope the creatorScope to set
	 */
	public void setCreatorScope(String creatorScope) {
		this.creatorScope = creatorScope;
	}

	/**
	 * @return the arrangement
	 */
	public ArrangementEntity getArrangement() {
		return arrangement;
	}

	/**
	 * @param arrangement the arrangement to set
	 */
	public void setArrangement(ArrangementEntity arrangement) {
		this.arrangement = arrangement;
	}

	/**
	 * @return the parentInstance
	 */
	public ArrangementInstanceEntity getParentInstance() {
		return parentInstance;
	}

	/**
	 * @param parentInstance the parentInstance to set
	 */
	public void setParentInstance(ArrangementInstanceEntity parentInstance) {
		this.parentInstance = parentInstance;
	}

	/**
	 * @return the childArrangementInstances
	 */
	public Set<ArrangementInstanceEntity> getChildArrangementInstances() {
		return childArrangementInstances;
	}

	/**
	 * @param childArrangementInstances the childArrangementInstances to set
	 */
	public void setChildArrangementInstances(
			Set<ArrangementInstanceEntity> childArrangementInstances) {
		this.childArrangementInstances = childArrangementInstances;
	}
}
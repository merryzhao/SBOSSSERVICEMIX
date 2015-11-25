package com.ai.sboss.arrangement.entity.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ai.sboss.arrangement.entity.UUIDEntity;

/**
 * 流程关联的若干任务节点的映射关系
 * @author yinwenjie
 */
@Entity
@Table(name="A_ARRANGEMENTJOINTMAPPING")
public class ArrangementJointMappingEntity extends UUIDEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5337361082315981389L;
	
	/**
	 * 对应的任务节点的对象信息
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="JOINT" , nullable=false)
	private JointEntity joint;
	
	/**
	 * 对应的以编排的业务流程信息
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARENTARRANGEMENT" , nullable=false)
	private ArrangementEntity parentArrangement;
	
	/**
	 * 这个流程中，这个任务节点的页面可见性
	 */
	@Column(name="VISIBLE" , nullable=false)
	private Boolean visible = true;
	
	/**
	 * 权重
	 */
	@Column(name="WEIGHT" , length=500 , nullable=true)
	private Long weight;
	
	/**
	 * 可能设置的这个任务在流程中的绝对偏移时间，单位毫秒。<br>
	 * 如果没有设置，则使用joint中的默认设置
	 */
	@Column(name="ABSOFFSETTIME" , nullable=true)
	private Long absOffsettime;
	
	/**
	 * 可能设置的这个任务在流程中的相对偏移时间，单位毫秒。<br>
	 * 如果没有设置，则使用joint中的默认设置
	 */
	@Column(name="RELATEOFFSETTIME" , nullable=true)
	private Long relateOffsettime;

	public JointEntity getJoint() {
		return joint;
	}

	public void setJoint(JointEntity joint) {
		this.joint = joint;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Long getAbsOffsettime() {
		return absOffsettime;
	}

	public void setAbsOffsettime(Long absOffsettime) {
		this.absOffsettime = absOffsettime;
	}

	public Long getRelateOffsettime() {
		return relateOffsettime;
	}

	public void setRelateOffsettime(Long relateOffsettime) {
		this.relateOffsettime = relateOffsettime;
	}

	public ArrangementEntity getParentArrangement() {
		return parentArrangement;
	}

	public void setParentArrangement(ArrangementEntity parentArrangement) {
		this.parentArrangement = parentArrangement;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}
}

package com.ai.sboss.arrangement.entity.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ai.sboss.arrangement.entity.UUIDEntity;

/**
 * 流程关联的若干子流程的映射关系
 * @author yinwenjie
 */
@Entity
@Table(name="A_ARRANGEMENTSELFMAPPING")
public class ArrangementSelfMappingEntity extends UUIDEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5337361082315981389L;
	
	/**
	 * 对应的子流程的对象信息
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ARRANGEMENT" , nullable=false)
	private ArrangementEntity arrangement;
	
	/**
	 * 对应的已编排的父级业务流程信息
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARENTARRANGEMENT" , nullable=false)
	private ArrangementEntity parentArrangement;
	
	/**
	 * 这个流程中，这个任务节点的页面可见性
	 */
	@Column(name="VISIBLE" , nullable=false)
	private Boolean visible = true;

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public ArrangementEntity getArrangement() {
		return arrangement;
	}

	public void setArrangement(ArrangementEntity arrangement) {
		this.arrangement = arrangement;
	}

	public ArrangementEntity getParentArrangement() {
		return parentArrangement;
	}

	public void setParentArrangement(ArrangementEntity parentArrangement) {
		this.parentArrangement = parentArrangement;
	}
}

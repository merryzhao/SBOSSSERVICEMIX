package com.ai.sboss.arrangement.entity.orm;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.ai.sboss.arrangement.entity.UUIDEntity;

/**
 * 该实体描述一个已经完成编排的任务流程。
 * @author yinwenjie
 */
@Entity
@Table(name="A_ARRANGEMENT")
public class ArrangementEntity extends UUIDEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7794624475683413748L;	
	
	/**
	 * 流程模板的创建者；一定记住，这个创建者，和流程实例的创建者，半毛钱的关系都没有
	 */
	@Column(name="CREATOR" , length=100 , nullable=false)
	private String creator;
	
	/**
	 * 可通过这个流程模板，创建流程实例的创建者范围：
	 * 使用industry、producer、consumer，标明创建者的身份
	 */
	@Column(name="CREATORSCOPE" , length=20 , nullable=false)
	private String creatorScope;
	
	/**
	 * 展现流程的中文名称
	 */
	@Column(name="DISPLAYNAME" , length=100 , nullable=false)
	private String displayName;
	
	/**
	 * 一个流程只能属于一个行业，这个属性就是所属的行业编号
	 */
	@Column(name="TRADEID" , length=100 , nullable=false)
	private String tradeid;
	
	/**
	 * 流程定义
	 * （目前暂时没有找到这个xml片段比较好的存储方式，暂时先这样存吧）
	 */
	@Column(name="FLOWS" , length=2000 , nullable=false)
	private String flows;
	
	/**
	 * 这个流程所关联的任务节点集合（没有顺序）
	 */
	@OneToMany(fetch=FetchType.LAZY , mappedBy="parentArrangement")
	private Set<ArrangementJointMappingEntity> jointmapping;
	
	/**
	 *  这个流程所关联的子流程节点集合（没有顺序）
	 */
	@OneToMany(fetch=FetchType.LAZY , mappedBy="parentArrangement")
	private Set<ArrangementSelfMappingEntity> childArrangements;
	
	/**
	 *  这个流程所对应的流程实例集合（没有顺序）
	 */
	@OneToMany(fetch=FetchType.LAZY , mappedBy="arrangement")
	private Set<ArrangementInstanceEntity> arrangementInstances;

	/**
	 * 这个业务流程定义在所属行业的身份：
	 * industry：标明是这个行业的默认流程（一个行业只有一个默认流程）
	 * producer：可以作为行业的默认服务或者服务者的自定义流程，但是不能被消费者的自定义流程引用
	 * consumer：可以作为行业的默认服务、服务者或者消费用的自定义流程
	 */
	@Column(name="TRADESCOPE" , length=50 , nullable=false)
	private String tradeScope;

	public String getCreatorScope() {
		return creatorScope;
	}

	public void setCreatorScope(String creatorScope) {
		this.creatorScope = creatorScope;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getTradeid() {
		return tradeid;
	}

	public void setTradeid(String tradeid) {
		this.tradeid = tradeid;
	}

	public String getFlows() {
		return flows;
	}

	public void setFlows(String flows) {
		this.flows = flows;
	}

	public Set<ArrangementJointMappingEntity> getJointmapping() {
		return jointmapping;
	}

	public void setJointmapping(Set<ArrangementJointMappingEntity> jointmapping) {
		this.jointmapping = jointmapping;
	}

	public Set<ArrangementSelfMappingEntity> getChildArrangements() {
		return childArrangements;
	}

	public void setChildArrangements(Set<ArrangementSelfMappingEntity> childArrangements) {
		this.childArrangements = childArrangements;
	}
	
	public Set<ArrangementInstanceEntity> getArrangementInstances() {
		return arrangementInstances;
	}

	public void setArrangementInstances(Set<ArrangementInstanceEntity> arrangementInstances) {
		this.arrangementInstances = arrangementInstances;
	}
	
	public String getTradeScope() {
		return tradeScope;
	}

	public void setTradeScope(String tradeScope) {
		this.tradeScope = tradeScope;
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
	
}
package com.ai.sboss.arrangement.entity.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ai.sboss.arrangement.entity.UUIDEntity;

/**
 * 这个实体用以描述一个任务信息所映射的行业信息，以及在一个行业中的的可使用范围
 * 
 * @author yinwenjie
 *
 */
@Entity
@Table(name = "A_JOINTTRADEMAPPING")
public class JointTradeMappingEntity extends UUIDEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6153897535496785945L;

	/**
	 * 对应的行业信息（行业唯一编号）
	 */
	@Column(name = "TRADEID", length = 100, nullable = false)
	private String tradeid;

	/**
	 * scope：可以使用这个业务服务的范围： 
	 * industry：只能作为某个行业的默认服务，不能被服务者或者消费者的自定义流程引用
	 * producer：可以作为行业的默认服务或者服务者的自定义流程，但是不能被消费者的自定义流程引用
	 * consumer：可以作为行业的默认服务、服务者或者消费用的自定义流程
	 */
	@Column(name = "SCOPE", length = 20, nullable = false)
	private String scope;

	/**
	 * 这个设置所对应的任务节点信息
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "JOINT", nullable = false)
	private JointEntity joint;

	public String getTradeid() {
		return tradeid;
	}

	public void setTradeid(String tradeid) {
		this.tradeid = tradeid;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public JointEntity getJoint() {
		return joint;
	}

	public void setJoint(JointEntity joint) {
		this.joint = joint;
	}
}
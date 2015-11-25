package com.ai.sboss.arrangement.entity.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ai.sboss.arrangement.entity.UUIDEntity;

/**
 * 表示编排系统中的一个任务节点实例的流转关系，存储在这里
 * @author yinwenjie
 */
@Entity
@Table(name="I_JOINTINSTANCEFLOW")
public class JointInstanceFlowEntity extends UUIDEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8946126263956549311L;
	
	/**
	 * 这个任务节点实例对应的流程实例对象
	 */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="JOINTINSTANCE" , nullable=false , unique=true)
	private JointInstanceEntity jointInstance;
	
	//===============================================
	//		以下信息是任务实例的冗余数据				=
	//===============================================

	/**
	 * 任务实例的指定执行者，只有这个指定的执行者，能够执行这个任务，否则就会报错<br>
	 * JointInstanceEntity中的冗余数据
	 */
	@Column(name="EXECUTOR" , length=100 , nullable=true)
	private String executor;

	/**
	 * 这个任务节点实例对应的任务节点对象
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="JOINT" , nullable=false)
	private JointEntity joint;
	
	/**
	 * 在任务实例创建时，通过这个任务实例关联的“流程实例创建时间”和“实例的绝对时间”偏移计算，得到的这个任务实例可能的执行时间<br>
	 * 注意，如果这个实例是“没有executor执行人的‘自动任务’”，那么这个“可能的执行时间”和上一个任务实例“可能的执行时间”是一致的。<br>
	 * 如果这又是第一个任务，那么“可能的执行时间”就是这个任务实例对应的流程实例的 启动时间。
	 */
	@Column(name="EXPECTEDEXETIME" , nullable=false)
	private Long expectedExeTime;
	
	/**
	 * 任务实例的执行状态（冗余）。分为几种：
	 * @waiting 这个任务实例已经完成了初始化，等待执行
	 * @executing 这个任务的前置任务/子流程，已经正常执行完成了，目前正轮到这个任务实例进行执行。<br>注意还有一种情况，就是流程实例回退的时候，会退到了这个任务实例上。
	 * @followed 这个任务已经正常执行完成，但是其所处的流程实例还没有全部执行完成
	 * @revoked 这个任务实例之前已经正常执行完成，但操作者进行了回退操作，这个任务实例的执行状态已经被回退。从业务执行特性来看，相当于waiting状态
	 * @completed 这个任务实例已经正常执行完成，并且其所处的流程实例也已经全部执行完成
	 * @terminated 这个任务实例所对应的流程实例已经被操作者强制终止了。
	 */
	@Column(name="STATE" , length=50 , nullable=false)
	private String statu;
	
	//===============================================
	//		以下信息是任务实例流转所需要的信息			=
	//===============================================
	/**
	 * 可能的上一个任务实例的信息。
	 * 如果previouJointInstance和previouArrangementInstance都没有值，说明是第一个执行实例
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PREVIOUJOINTINSTANCE" , nullable=true)
	private JointInstanceEntity previouJointInstance;
	
	/**
	 * 可能的上一个子流程实例的关联信息
	 * 如果previouJointInstance和previouArrangementInstance都没有值，说明是第一个执行实例
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PREVIOUARRANGEMENTINSTANCE" , nullable=true)
	private ArrangementInstanceEntity previouArrangementInstance;
	
	/**
	 * 下一个可能的任务实例的引用信息
	 * 如果nextJointInstance和nextArrangementInstance都没有值，说明这是最后一个执行的实例了
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="NEXTJOINTINSTANCE" , nullable=true)
	private JointInstanceEntity nextJointInstance;
	
	/**
	 * 下一个可能的子流程实例的引用信息
	 * 如果nextJointInstance和nextArrangementInstance都没有值，说明这是最后一个执行的实例了
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="NEXTARRANGEMENTINSTANCE" , nullable=true)
	private ArrangementInstanceEntity nextArrangementInstance;
	
	/**
	 * 当前任务实例最后一次被执行的实际时间（之前的执行时间由日志负责记录）
	 */
	@Column(name="EXETIME" , nullable=true)
	private Long exeTime;
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
	 * @return the joint
	 */
	public JointEntity getJoint() {
		return joint;
	}

	/**
	 * @param joint the joint to set
	 */
	public void setJoint(JointEntity joint) {
		this.joint = joint;
	}

	/**
	 * @return the previouArrangementInstance
	 */
	public ArrangementInstanceEntity getPreviouArrangementInstance() {
		return previouArrangementInstance;
	}

	/**
	 * @param previouArrangementInstance the previouArrangementInstance to set
	 */
	public void setPreviouArrangementInstance(
			ArrangementInstanceEntity previouArrangementInstance) {
		this.previouArrangementInstance = previouArrangementInstance;
	}

	/**
	 * @return the nextArrangementInstance
	 */
	public ArrangementInstanceEntity getNextArrangementInstance() {
		return nextArrangementInstance;
	}

	/**
	 * @param nextArrangementInstance the nextArrangementInstance to set
	 */
	public void setNextArrangementInstance(
			ArrangementInstanceEntity nextArrangementInstance) {
		this.nextArrangementInstance = nextArrangementInstance;
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
	 * @return the jointInstance
	 */
	public JointInstanceEntity getJointInstance() {
		return jointInstance;
	}

	/**
	 * @param jointInstance the jointInstance to set
	 */
	public void setJointInstance(JointInstanceEntity jointInstance) {
		this.jointInstance = jointInstance;
	}

	/**
	 * @return the exeTime
	 */
	public Long getExeTime() {
		return exeTime;
	}

	/**
	 * @param exeTime the exeTime to set
	 */
	public void setExeTime(Long exeTime) {
		this.exeTime = exeTime;
	}

	/**
	 * @return the previouJointInstance
	 */
	public JointInstanceEntity getPreviouJointInstance() {
		return previouJointInstance;
	}

	/**
	 * @param previouJointInstance the previouJointInstance to set
	 */
	public void setPreviouJointInstance(JointInstanceEntity previouJointInstance) {
		this.previouJointInstance = previouJointInstance;
	}

	/**
	 * @return the nextJointInstance
	 */
	public JointInstanceEntity getNextJointInstance() {
		return nextJointInstance;
	}

	/**
	 * @param nextJointInstance the nextJointInstance to set
	 */
	public void setNextJointInstance(JointInstanceEntity nextJointInstance) {
		this.nextJointInstance = nextJointInstance;
	}
	
}
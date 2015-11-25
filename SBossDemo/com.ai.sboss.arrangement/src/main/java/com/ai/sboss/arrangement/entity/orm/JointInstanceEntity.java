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
 * 表示编排系统中的一个任务节点实例
 * @author chaos
 * @Index (name="A_JOINTINSTANCE_CREATOR" , columnNames={"CREATOR"})
 * @Index (name="A_JOINTINSTANCE_EXECUTOR" , columnNames={"EXECUTOR"})
 */
@Entity
@Table(name="I_JOINTINSTANCE")
public class JointInstanceEntity extends UUIDEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8946126263956549311L;
	
	/**
	 * 对应的Camel服务
	 */
	@Column(name="CAMELURI" , length=500 , nullable=true)
	private String camelUri;
	
	/**
	 * 权重
	 */
	@Column(name="WEIGHT" , length=500 , nullable=true)
	private Long weight;
	
	/**
	 * JSON参数列表
	 */
	@Column(name="PROPERTIES" , length=500 , nullable=true)
	private String properties;

	/**
	 * 任务默认执行者，如果这个任务没有任务执行者，意味着是一个自动流转的编排任务
	 */
	@Column(name="EXECUTOR" , length=100 , nullable=true)
	@Index (name="A_JOINTINSTANCE_EXECUTOR" , columnNames={"EXECUTOR"})
	private String executor;
	
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
	 * 默认的任务偏移量阀值提示（可能没有）
	 */
	@Column(name="PROMPTOFFSETTIME" , length=500 , nullable=true)
	private String promptOffsettime;
	
	/**
	 * 这个任务节点实例对应的流程实例对象
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ARRANGEMENTINST" , nullable=false)
	private ArrangementInstanceEntity arrangementInstance;
	
	/**
	 * 这个任务节点实例对应的任务节点对象
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="JOINT" , nullable=false)
	private JointEntity joint;
	
	/**
	 * 这个任务节点实例已经设置的出参实例
	 */
	@OneToMany(fetch=FetchType.LAZY , mappedBy="jointInstance")
	private Set<JointOutputParamsInstanceEntity> outputParamInstance;
	
	/**
	 * 这个任务节点实例已经设置的出参实例
	 */
	@OneToMany(fetch=FetchType.LAZY , mappedBy="jointInstance")
	private Set<JointInputParamsInstanceEntity> inputParamInstances;
	
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
	 * 这个任务实例的创建者。由于在系统中，流程实例和任务实例是一起创建的，所以肯定的：<br>
	 * 同：编排流程实例的创建者
	 */
	@Column(name="CREATOR" , length=100 , nullable=false)
	@Index (name="A_JOINTINSTANCE_CREATOR" , columnNames={"CREATOR"})
	private String creator;
	
	//=============================================================================
	//			以上信息属于实例的基本信息，以下信息是实例的冗余执行信息
	//=============================================================================
	/**
	 * 在任务实例创建时，通过这个任务实例关联的“流程实例创建时间”和“实例的绝对时间”偏移计算，得到的这个任务实例可能的执行时间<br>
	 * 注意，如果这个实例是“没有executor执行人的‘自动任务’”，那么这个“可能的执行时间”和上一个任务实例“可能的执行时间”是一致的。<br>
	 * 如果这又是第一个任务，那么“可能的执行时间”就是这个任务实例对应的流程实例的 启动时间。
	 */
	@Column(name="EXPECTEDEXETIME" , nullable=false)
	private Long expectedExeTime;
	
	/**
	 * 任务实例的执行状态。分为几种：
	 * @waiting 这个任务实例已经完成了初始化，等待执行
	 * @executing 这个任务的前置任务/子流程，已经正常执行完成了，目前正轮到这个任务实例进行执行。<br>
	 * 		注意还有一种情况，就是流程实例回退的时候，回退到了这个任务实例上。
	 * @followed 这个任务已经正常执行完成，但是其所处的流程实例还没有全部执行完成
	 * @revoked 这个任务实例之前已经正常执行完成，但操作者进行了回退操作，这个任务实例的执行状态已经被回退。从业务执行特性来看，相当于waiting状态
	 * @completed 这个任务实例已经正常执行完成，并且其所处的流程实例也已经全部执行完成
	 * @terminated 这个任务实例所对应的流程实例已经被操作者强制终止了。
	 */
	@Column(name="STATE" , length=50 , nullable=false)
	private String statu;
	
	/**
	 * 任务实例的实际执行时间
	 */
	@Column(name="EXETIME" , nullable=true)
	private Long exeTime;

	/**
	 * @return the camelUri
	 */
	public String getCamelUri() {
		return camelUri;
	}

	/**
	 * @param camelUri the camelUri to set
	 */
	public void setCamelUri(String camelUri) {
		this.camelUri = camelUri;
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
	 * @return the arrangementInstance
	 */
	public ArrangementInstanceEntity getArrangementInstance() {
		return arrangementInstance;
	}

	/**
	 * @param arrangementInstance the arrangementInstance to set
	 */
	public void setArrangementInstance(ArrangementInstanceEntity arrangementInstance) {
		this.arrangementInstance = arrangementInstance;
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
	 * @return the outputParamInstance
	 */
	public Set<JointOutputParamsInstanceEntity> getOutputParamInstance() {
		return outputParamInstance;
	}

	/**
	 * @param outputParamInstance the outputParamInstance to set
	 */
	public void setOutputParamInstance(
			Set<JointOutputParamsInstanceEntity> outputParamInstance) {
		this.outputParamInstance = outputParamInstance;
	}

	/**
	 * @return the inputParamInstances
	 */
	public Set<JointInputParamsInstanceEntity> getInputParamInstances() {
		return inputParamInstances;
	}

	/**
	 * @param inputParamInstances the inputParamInstances to set
	 */
	public void setInputParamInstances(
			Set<JointInputParamsInstanceEntity> inputParamInstances) {
		this.inputParamInstances = inputParamInstances;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}
}

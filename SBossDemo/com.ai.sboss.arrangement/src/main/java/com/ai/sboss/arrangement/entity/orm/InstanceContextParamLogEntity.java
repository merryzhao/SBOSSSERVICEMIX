package com.ai.sboss.arrangement.entity.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ai.sboss.arrangement.entity.UUIDEntity;

/**
 * 上下文的变化情况。记录了在某一次任务实例流转的时候（JointInstanceFlowLog产生的时候），记录每一个上下文变量的变化情况<br>
 * 注意，日志中并不记录变量类型，由InstanceContextParamEntity实例中的属性进行记录。
 * @author yinwenjie
 */
@Entity
@Table(name="L_INSTANCECONTEXTPARAMLOG")
public class InstanceContextParamLogEntity extends UUIDEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1265806442444253991L;

	/**
	 * 对应的任务实例流转日志
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="JOINTINSTANCEFLOWLOG" , nullable=false)
	private JointInstanceFlowLogEntity jointInstanceFlowLog;
	
	/**
	 * 上下文的变量名称
	 */
	@Column(name="PARAMKEY" , length=50, nullable=false)
	private String key;
	
	/**
	 * 变化前的值
	 */
	@Column(name="FROMVALUE" , length=5000, nullable=true)
	private String fromValue;
	
	/**
	 * 变化后的值
	 */
	@Column(name="TOVALUE" , length=5000, nullable=true)
	private String toValue;

	/**
	 * @return the jointInstanceFlowLog
	 */
	public JointInstanceFlowLogEntity getJointInstanceFlowLog() {
		return jointInstanceFlowLog;
	}

	/**
	 * @param jointInstanceFlowLog the jointInstanceFlowLog to set
	 */
	public void setJointInstanceFlowLog(JointInstanceFlowLogEntity jointInstanceFlowLog) {
		this.jointInstanceFlowLog = jointInstanceFlowLog;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the fromValue
	 */
	public String getFromValue() {
		return fromValue;
	}

	/**
	 * @param fromValue the fromValue to set
	 */
	public void setFromValue(String fromValue) {
		this.fromValue = fromValue;
	}

	/**
	 * @return the toValue
	 */
	public String getToValue() {
		return toValue;
	}

	/**
	 * @param toValue the toValue to set
	 */
	public void setToValue(String toValue) {
		this.toValue = toValue;
	}
}

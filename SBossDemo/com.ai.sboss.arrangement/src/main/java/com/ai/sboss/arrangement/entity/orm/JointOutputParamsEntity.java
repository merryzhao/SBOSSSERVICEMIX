package com.ai.sboss.arrangement.entity.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ai.sboss.arrangement.entity.UUIDEntity;

/**
 * 任务节点的出参定义
 * @author yinwenjie
 */
@Entity
@Table(name="A_JOINTOUTPUTPARAMS")
public class JointOutputParamsEntity extends UUIDEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5635013607204358201L;
	
	/**
	 * 出参名称（英文）
	 */
	@Column(name="PARAMNAME" , length=100 , nullable=false)
	private String name;
	
	/**
	 * 出参类型。目前支持的类型包括：
	 * String，Boolean，Integer，Long，Float，Double，JSON，XML，Date
	 */
	@Column(name="PARAMTYPE" , length=100 , nullable=false)
	private String type;
	
	/**
	 * 这个出参是否是必要的输出参数。
	 */
	@Column(name="REQUIRED" ,  nullable=false)
	private Boolean required = false;
	
	/**
	 * 这个出参是否有默认值（默认值使用字符串进行记录，使用时再根据type进行转换）
	 */
	@Column(name="DEFAULTVALUE" , length=255 ,  nullable=true)
	private String defaultValue;
	
	/**
	 * 这个设置所对应的任务节点信息
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="JOINT" , nullable=false)
	private JointEntity joint;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public JointEntity getJoint() {
		return joint;
	}

	public void setJoint(JointEntity joint) {
		this.joint = joint;
	}
}
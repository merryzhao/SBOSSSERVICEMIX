package com.ai.sboss.arrangement.entity.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ai.sboss.arrangement.entity.UUIDEntity;

/**
 * 编排任务实例对应的入参实例信息，在这里进行描述。
 * @author yinwenjie
 * @index I_JOINTINPUTPARAMSINSTANCE_PARAMNAME_INDEX(PARAMNAME,JOINTINSTANCE) UNIQUE
 */
@Entity
@Table(name="I_JOINTINPUTPARAMSINSTANCE")
public class JointInputParamsInstanceEntity extends UUIDEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9000331448880530965L;
	
	/**
	 * 入参名称（英文）
	 */
	@Column(name="PARAMNAME" , length=100 , nullable=false)
	private String name;
	
	/**
	 * 入参类型。目前支持的类型包括：
	 * String，Boolean，Integer，Long，Float，Double，JSON，XML，Date
	 */
	@Column(name="PARAMTYPE" , length=100 , nullable=false)
	private String type;
	
	/**
	 * 这个入参是否是必要的输入参数。
	 */
	@Column(name="REQUIRED" ,  nullable=false)
	private Boolean required = false;
	
	/**
	 * 这个入参是否有默认值（默认值使用字符串进行记录，使用时再根据type进行转换）
	 */
	@Column(name="DEFAULTVALUE" , length=255 ,  nullable=true)
	private String defaultValue;
	
	/**
	 * 这个入参对应的在页面上显示的类型
	 */
	@Column(name="DISPLAYTYPE" , length=100 ,  nullable=false)
	private String displayType;
	
	/**
	 * 这个入参对应的在页面上显示的中文名称
	 */
	@Column(name="DISPLAYNAME" , length=100 ,  nullable=false)
	private String displayName;
	
	/**
	 * 这个设置所对应的任务节点实例信息
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="JOINTINSTANCE" , nullable=false)
	private JointInstanceEntity jointInstance;
	
	/**
	 * 生成这个入参实例，所依据的任务入参模板
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="JOINTINPUTPARAM" , nullable=false)
	private JointInputParamsEntity jointInputParam;

	public String getName() {
		return name;
	}

	public JointInstanceEntity getJointInstance() {
		return jointInstance;
	}

	public void setJointInstance(JointInstanceEntity jointInstance) {
		this.jointInstance = jointInstance;
	}

	public JointInputParamsEntity getJointInputParam() {
		return jointInputParam;
	}

	public void setJointInputParam(JointInputParamsEntity jointInputParam) {
		this.jointInputParam = jointInputParam;
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

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
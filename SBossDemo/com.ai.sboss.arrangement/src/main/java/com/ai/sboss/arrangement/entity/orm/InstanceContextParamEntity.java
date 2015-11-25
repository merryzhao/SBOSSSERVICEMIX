package com.ai.sboss.arrangement.entity.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ai.sboss.arrangement.entity.UUIDEntity;

/**
 * 流程实例启动后，在其流转过程中，会出现存储在流程实例上下文中的参数信息。
 * 这个上下文中的各种参数和其值都是整个流程中所共享的。<br>
 * @author yinwenjie
 */
@Entity
@Table(name="I_INSTANCECONTEXTPARAM")
public class InstanceContextParamEntity extends UUIDEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4972772669801792029L;

	/**
	 * 上下文参数名称（英文）
	 */
	@Column(name="PARAMNAME" , length=100 , nullable=false)
	private String name;
	
	/**
	 * 上下文参数类型。目前支持的类型包括：
	 * String，Boolean，Integer，Long，Float，Double，JSON，XML，Date
	 */
	@Column(name="PARAMTYPE" , length=100 , nullable=false)
	private String type;
	
	/**
	 * 这个上下文参数目前的值，如果为null，说明没有赋值
	 */
	@Column(name="NOWVALUE" , length=5000 ,  nullable=true)
	private String nowValue;
	
	/**
	 * 这个上下文参数对应的在页面上显示的类型
	 */
	@Column(name="DISPLAYTYPE" , length=100 ,  nullable=true)
	private String displayType;
	
	/**
	 * 这个上下文参数对应的在页面上显示的中文名称
	 */
	@Column(name="DISPLAYNAME" , length=100 ,  nullable=true)
	private String displayName;
	
	/**
	 * 这个流程实例上下文参数所绑定的流程实例信息
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ARRANGEMENTINSTANCE" , nullable=false)
	private ArrangementInstanceEntity arrangementInstance;
	
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

	/**
	 * @return the nowValue
	 */
	public String getNowValue() {
		return nowValue;
	}

	/**
	 * @param nowValue the nowValue to set
	 */
	public void setNowValue(String nowValue) {
		this.nowValue = nowValue;
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
	
}
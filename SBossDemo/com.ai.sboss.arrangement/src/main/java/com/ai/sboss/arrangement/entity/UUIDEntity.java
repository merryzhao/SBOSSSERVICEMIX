package com.ai.sboss.arrangement.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 使用的实体父类，采用关系型数据库进行存储，所以这里进行可以使用hibernate的注解标签
 * @author wenjie
 */
@MappedSuperclass
public class UUIDEntity extends AbstractEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="UUID" , length=100)
	protected String uid;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}

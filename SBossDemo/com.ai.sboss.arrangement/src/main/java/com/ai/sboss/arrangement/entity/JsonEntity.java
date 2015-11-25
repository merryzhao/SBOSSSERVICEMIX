package com.ai.sboss.arrangement.entity;

import java.io.Serializable;

import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * 该数据体用于进行http协议json格式接口的返回信息描述。
 * 
 * @author yinwenjie
 * 
 */
public class JsonEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2456254862958624358L;

	public JsonEntity() {
		this.desc = new DescEntity();
	}

	public JsonEntity(Object data, String result_msg, ResponseCode result_code) {
		this.data = data;
		this.desc = new DescEntity(result_msg, result_code);
	}

	/**
	 * 返回的请求结果查询
	 */
	private Object data;

	private DescEntity desc;

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	public DescEntity getDesc() {
		return desc;
	}

	public void setDesc(DescEntity desc) {
		this.desc = desc;
	}
}

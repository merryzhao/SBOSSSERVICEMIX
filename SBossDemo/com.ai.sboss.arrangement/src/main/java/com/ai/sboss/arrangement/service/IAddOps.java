package com.ai.sboss.arrangement.service;

import net.sf.json.JSONObject;

import com.ai.sboss.arrangement.entity.JsonEntity;

/**
 * 添加操作接口类.
 * @author Chaos
 */
public interface IAddOps {
	/**
	 * 添加任务节点
	 * @param xmlText  以外部传入的符合joint-xml规范的数据为依据
	 * @return 
	 */
	public JsonEntity addJointItem(String xmlText) ;
	
	/**
	 * 添加任务节点
	 * @param xmlText  以外部传入的符合joint-json规范的数据为依据
	 * @return 
	 */
	public JsonEntity addJointItem(JSONObject jointjson) ;

	/**
	 * 添加行业默认流程模板.
	 * @param xmlText 以外部传入的符合arrangement-xml规范的数据为依据
	 * @return 
	 */
	public JsonEntity addDefaultArrangementItem(String xmlText);
	
	/**
	 * 添加行业默认流程模板.
	 * @param arrangementjson 以外部传入的符合arrangement-jso规范的数据为依据
	 * @return 
	 */
	public JsonEntity addDefaultArrangementItem(JSONObject arrangementjson);
	
	/**
	 * 添加服务商户、客户自定义的流程模板.
	 * @param arrangementjson 以外部传入的符合arrangement-jso规范的数据为依据
	 * @return 
	 */
	public JsonEntity addDefineArrangementItem(JSONObject arrangementjson);
}
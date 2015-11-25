package com.ai.sboss.arrangement.translation;

import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * 该接口向上层实现提供了一个“流程实例定义”描述，进行各种描述转换的服务<br>
 * 包括entity-json-xml的互转。
 * @author yinwenjie
 */
public interface ArrangementInstanceTranslationService {
	/**
	 * 传入一个符合arrangementInstance-xml规范的字符串信息。<br>
	 * 如果解析成功，这个服务方法将会为调用者返回解析出来的arrangementInstance实体信息
	 * @param xmlText
	 * @return
	 */
	public ArrangementInstanceEntity translationEntity(String xmlText)  throws BizException;
	
	/**
	 * 传入一个符合arrangementInstance-json规范的json对象信息。<br>
	 * 如果解析成功，这个服务方法将会为调用者返回解析出来的arrangementInstance实体信息
	 * @param jsonObject 符合arrangementInstance-json规范的json对象信息
	 * @return
	 */
	public ArrangementInstanceEntity translationEntity(JSONObject jsonObject) throws BizException;
	
	/**
	 * 传入一个符合arrangementflows-xml规范的字符串信息。<br>
	 * 如果解析成功，这个服务方法将会为调用者返回解析出来的执行顺序列表List<Map<模版ID， 实例ID>>
	 * @param xmlText
	 * @return 
	 */
	public List<Map.Entry<String, String>> translationFlows(String xmlText) throws BizException;
}

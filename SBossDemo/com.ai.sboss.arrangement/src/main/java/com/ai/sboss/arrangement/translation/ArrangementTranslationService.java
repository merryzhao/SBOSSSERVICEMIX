package com.ai.sboss.arrangement.translation;

import java.io.InputStream;

import net.sf.json.JSONObject;

import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * 该接口向上层实现提供了一个“流程JSON”描述，直接转成XML描述或者实体的服务
 * @author yinwenjie
 */
public interface ArrangementTranslationService {
	/**
	 * 传入一个符合arrangement-json规范的对象信息，如果解析成功，这个服务方法将会为调用者返回解析出来的arrangement实体信息<br>
	 * @param jsonObject 传入的一个符合arrangement-json格式的能够正确解析的jsonObject对象
	 * @return 
	 * @throws BizException
	 */
	public ArrangementEntity translationEntity(JSONObject jsonObject) throws BizException;
	
	/**
	 * 传入一个符合arrangement-xml规范的字符串信息，如果解析成功，这个服务方法将会为调用者返回解析出来的arrangement实体信息<br>
	 * @param xmlText
	 * @return 
	 * @throws BizException
	 */
	public ArrangementEntity translationEntity(String xmlText) throws BizException;
	
	/**
	 * 传入一个符合arrangement-xml规范的xml输入流，如果解析成功，这个服务方法将会为调用者返回解析出来的arrangement实体信息<br>
	 * 注意：它并不会主动关闭xmlStream这个输入流
	 * @param xmlStream 
	 * @return 
	 * @throws BizException
	 */
	public ArrangementEntity translationEntity(InputStream xmlStream) throws BizException;
	
	/**
	 * @param xmlText
	 * @return
	 * @throws BizException
	 * TODO 暂缓实现
	 */
	public JSONObject translationJSON(String xmlText) throws BizException;
	
	/**
	 * @param xmlStream
	 * @return
	 * @throws BizException
	 * TODO 暂缓实现
	 */
	public JSONObject translationJSON(InputStream xmlStream) throws BizException;
}
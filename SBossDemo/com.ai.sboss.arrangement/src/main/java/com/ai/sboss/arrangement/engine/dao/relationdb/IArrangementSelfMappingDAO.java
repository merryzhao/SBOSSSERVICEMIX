package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;
import java.util.Set;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.orm.ArrangementSelfMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * @author chaos
 *
 */
public interface IArrangementSelfMappingDAO extends SystemDAO<ArrangementSelfMappingEntity> {
	/**
	 * 获取一个指定的编排流程的子流程集合信息。<br>
	 * @param arrangementuid 子流程对应的父编排流程唯一编号信息
	 * @return 如果有符合编号的ArrangementSelfMappingEntity，将以集合的形式进行返回；其他情况下返回null
	 * @throws BizException
	 */
	public List<ArrangementSelfMappingEntity> getArrangementSelfMappingSet(String arrangementuid) throws BizException;
	
	/**
	 * 重新绑定指定的流程编排信息所绑定的子流程集合。<br>
	 * 既然是重新绑定，那么之前设定的子流程集合信息都将不再起作用。
	 * @param arrangementuid 指定的需要重新绑定子流程信息的编排流程uid
	 * @param childArrangements 新的子流程信息集合
	 * @throws BizException 
	 */
	public void bindArrangementChildArrangements(String arrangementuid , Set<ArrangementSelfMappingEntity> childArrangements) throws BizException;
	
	/**
	 * 释放绑定的子流程集合<br>
	 * @param arrangementuid 需要释放子流程的父流程uid
	 * @return null
	 * @throws BizException
	 */
	public void releaseAllArrangementChildArrangements(String arrangementuid) throws BizException;

}
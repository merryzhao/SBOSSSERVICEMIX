package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;

import com.ai.sboss.arrangement.engine.dao.SystemDAO;
import com.ai.sboss.arrangement.entity.orm.InstanceContextParamEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * @author yinwenjie
 */
public interface IInstanceContextParamDAO extends SystemDAO<InstanceContextParamEntity> {
	/**
	 * 查看流程实例上下文的参数信息。按照流程实例编号查询
	 * @param arrangementInstanceId 流程实例编号查询
	 * @return 
	 */
	public List<InstanceContextParamEntity> queryContextParamByArrangementInstanceId(String arrangementInstanceId) throws BizException;
	
	/**
	 * 按照指定的上下文实例参数的uid，更新最新的value的值
	 * @param uid 指定的编号
	 * @param newestValue 最新的值
	 * @throws BizException
	 */
	public void updateNewestContextParamValue(String uid , String newestValue) throws BizException;
}
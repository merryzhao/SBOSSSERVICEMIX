package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.orm.InstanceContextParamEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author yinwenjie
 */
@Component("InstanceContextParamDAOImpl")
public class InstanceContextParamDAOImpl extends AbstractRelationalDBDAO<InstanceContextParamEntity> implements IInstanceContextParamDAO {

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO#getEntityClass()
	 */
	@Override
	protected Class<InstanceContextParamEntity> getEntityClass() {
		return InstanceContextParamEntity.class;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IInstanceContextParamDAO#queryContextParamByArrangementInstanceId(java.lang.String)
	 */
	@Override
	public List<InstanceContextParamEntity> queryContextParamByArrangementInstanceId(String arrangementInstanceId)  throws BizException {
		if(StringUtils.isEmpty(arrangementInstanceId)) {
			throw new BizException("错误的流程实例编号，请检查。", ResponseCode._404);
		}
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("arrangementInstanceId", arrangementInstanceId);
		List<InstanceContextParamEntity> result = null;
		result = this.queryByHqlFile("IInstanceContextParamDAO.queryContextParamByArrangementInstanceId", condition);
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IInstanceContextParamDAO#updateNewestContextParamValue(java.lang.String, java.lang.String)
	 */
	@Override
	public void updateNewestContextParamValue(String uid, String newestValue) throws BizException {
		if(StringUtils.isEmpty(uid)) {
			throw new BizException("错误的流程实例编号，请检查。", ResponseCode._404);
		}
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("uid", uid);
		condition.put("nowValue", newestValue);
		
		this.executeSQLFile("IInstanceContextParamDAO.updateNewestContextParamValue", condition);
	}
}
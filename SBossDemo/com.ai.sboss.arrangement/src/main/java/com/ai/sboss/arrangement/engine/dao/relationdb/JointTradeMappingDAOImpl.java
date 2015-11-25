package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointTradeMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author yinwenjie
 *
 */
@Component("jointTradeMappingDAOImpl")
public class JointTradeMappingDAOImpl extends AbstractRelationalDBDAO<JointTradeMappingEntity> implements IJointTradeMappingDAO {
	@Autowired
	private IJointDAO jointDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO#getEntityClass
	 * ()
	 */
	@Override
	protected Class<JointTradeMappingEntity> getEntityClass() {
		return JointTradeMappingEntity.class;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointTradeMappingDAO#queryJointTradeMappingSet(java.lang.String)
	 */
	@Override
	public List<JointTradeMappingEntity> queryJointTradeMappingSet(String jointuid) throws BizException {
		if (StringUtils.isEmpty(jointuid)) {
			throw new BizException("错误的任务编号jointuid参数，请检查", ResponseCode._402);
		}

		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("jointuid", jointuid);

		// 获取记录
		List<JointTradeMappingEntity> result = null;
		result = this.queryByHqlFile("IJointTradeMappingDAO.getJointTradeMappingSet", conditionMap);
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointTradeMappingDAO#queryJointTradeByTradeid(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public JointTradeMappingEntity queryJointTradeByTradeid(String scope, String tradeid, String jointid) throws BizException {
		if (StringUtils.isEmpty(scope)) {
			throw new BizException("错误的scope参数，请检查", ResponseCode._402);
		}
		if (StringUtils.isEmpty(tradeid)) {
			throw new BizException("错误的tradeid参数，请检查", ResponseCode._402);
		}
		if (StringUtils.isEmpty(jointid)) {
			throw new BizException("错误的任务模板编号，请检查", ResponseCode._402);
		}
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("scope", scope);
		conditionMap.put("tradeid", tradeid);
		conditionMap.put("jointid", jointid);

		// 获取记录
		List<JointTradeMappingEntity> result = null;
		result = this.queryByHqlFile("IJointTradeMappingDAO.queryJointTradeByTradeid", conditionMap);
		
		if(result == null || result.isEmpty()) {
			return null;
		}
		
		return result.get(0);
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointTradeMappingDAO#releaseAllJointTradeMapping(java.lang.String)
	 */
	@Override
	public void releaseAllJointTradeMapping(String jointuid) throws BizException {
		if (StringUtils.isEmpty(jointuid)) {
			throw new BizException("错误的任务编号jointuid参数，请检查", ResponseCode._402);
		}

		// 删除记录
		List<JointTradeMappingEntity> tradeMappingEntities = queryJointTradeMappingSet(jointuid);
		if (tradeMappingEntities == null) {
			return;
		}
		for (JointTradeMappingEntity trade: tradeMappingEntities) {
			this.delete(trade.getUid());
		}
	}

	@Override
	public void bindJointTrade(JointTradeMappingEntity trademapping) throws BizException {
		// 合法性检查
		if (!checkTradeMappingValid(trademapping)) {
			throw new BizException("trademapping参数不合法", ResponseCode._402);
		}
		// 若没有填写uid，系统将为这个joint任务生成一个全系统唯一的
		if (StringUtils.isEmpty(trademapping.getUid())) {
			trademapping.setUid(java.util.UUID.randomUUID().toString());
		}

		this.insert(trademapping);
	}

	/**
	 * 检查一个joint信息，检查joint非空必要属性是否全部都已经配置完毕<br>
	 * 对应的inputparams集合、outputparams集合、trade集合信息都必须设置<br>
	 * 其他参数根据业务的实际情况来
	 * 
	 * @param joint
	 * @return 若JointEntity格式合法则返回True， 否则返回False
	 * @throws BizException
	 */
	private boolean checkTradeMappingValid(JointTradeMappingEntity trademapping) throws BizException {
		if (trademapping == null) {
			return false;
		}

		boolean ret = true;
		ret &= !StringUtils.isEmpty(trademapping.getTradeid());
		ret &= !StringUtils.isEmpty(trademapping.getScope());
		if (!trademapping.getScope().equals("industry") && !trademapping.getScope().equals("producer") && !trademapping.getScope().equals("consumer")) {
			ret = false;
		}
		ret &= (trademapping.getJoint() != null);
		return ret;
	}

	@Override
	public void bindJointTrade(String jointuid, Entry<String, String> tradeInfo) throws BizException {
		if (StringUtils.isEmpty(jointuid) || tradeInfo == null) {
			throw new BizException("错误的参数，请检查", ResponseCode._402);
		}

		// 建立新行业信息对象
		JointTradeMappingEntity trademapping = new JointTradeMappingEntity();
		trademapping.setUid(java.util.UUID.randomUUID().toString());
		trademapping.setTradeid(tradeInfo.getKey());
		trademapping.setScope(tradeInfo.getValue());
		JointEntity jointEntity = jointDAO.getJointWithoutParams(jointuid);
		trademapping.setJoint(jointEntity);

		this.bindJointTrade(trademapping);
	}
}
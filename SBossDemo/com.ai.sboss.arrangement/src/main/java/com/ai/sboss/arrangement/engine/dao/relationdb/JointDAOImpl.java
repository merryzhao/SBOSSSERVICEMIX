package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointTradeMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author yinwenjie
 */
@Component("jointDAOImpl")
public class JointDAOImpl extends AbstractRelationalDBDAO<JointEntity> implements IJointDAO {
	@Autowired
	private IJointOutputParamsDAO jointOutputParamsDAO;
	@Autowired
	private IJointInputParamsDAO jointInputParamsDAO;
	@Autowired
	private IJointTradeMappingDAO jointTradeMappingDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO#getEntityClass
	 * ()
	 */
	@Override
	protected Class<JointEntity> getEntityClass() {
		return JointEntity.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO#queryJointByTradeid
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public List<JointEntity> queryJointByTradeid(String tradeid, String scope) throws BizException {
		if (StringUtils.isEmpty(tradeid)) {
			throw new BizException("错误的行业tradeid参数，请检查", ResponseCode._403);
		} 
		
		// 组装条件
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("tradeid", tradeid);
		if (!StringUtils.isEmpty(scope)) {
			condition.put("scope", scope);
		}
		// 获取记录(由于使用的hql)
		List<JointEntity> result = null;
		result = this.queryByHqlFile("IJointDAO.queryJointByTradeid", condition);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO#
	 * queryJointByTradeidPage(java.lang.String, java.lang.String,
	 * java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageEntity queryJointByTradeidPage(String tradeid, String scope, Integer pageNumber, Integer maxPerNumber) throws BizException {
		if (StringUtils.isEmpty(tradeid)) {
			throw new BizException("错误的行业tradeid参数，请检查", ResponseCode._403);
		}
		if (pageNumber == null || pageNumber < 0) {
			pageNumber = 0;
		}
		if (maxPerNumber == null || maxPerNumber < 0) {
			maxPerNumber = 20;
		}

		// 组装条件
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("tradeid", tradeid);
		if (!StringUtils.isEmpty(scope)) {
			condition.put("scope", scope);
		}

		// 获取记录(由于使用的hql)
		PageEntity pageEntity = null;
		pageEntity = this.queryByPageHQLFile("IJointDAO.queryJointByTradeid", condition, pageNumber, maxPerNumber);
		return pageEntity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO#
	 * queryJointByDisplayName(java.lang.String)
	 */
	@Override
	public JointEntity queryJointByDisplayName(String displayName) throws BizException {
		if (StringUtils.isEmpty(displayName)) {
			throw new BizException("错误的显示名称displayName参数，请检查", ResponseCode._403);
		}

		// 组装查询条件
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("displayName", displayName);

		// 获取查询结果
		List<JointEntity> result = null;
		result = this.queryByHqlFile("IJointDAO.queryJointByDisplayName", condition);

		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO#
	 * queryJointByDisplayNameWithParams(java.lang.String)
	 */
	@Override
	public JointEntity queryJointByDisplayNameWithParams(String displayName) throws BizException {
		if (StringUtils.isEmpty(displayName)) {
			throw new BizException("错误的显示名称displayName参数，请检查", ResponseCode._403);
		}

		// 组装查询条件
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("displayName", displayName);

		// 获取查询结果
		List<JointEntity> result = null;
		result = this.queryByHqlFile("IJointDAO.queryJointByDisplayNameWithParams", condition);
		
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO#queryJointByArrangementID(java.lang.String)
	 */
	@Override
	public List<JointEntity> queryJointByArrangementID(String arrangementid) throws BizException {
		if (StringUtils.isEmpty(arrangementid)) {
			throw new BizException("错误的任务编号arrangementid参数，请检查", ResponseCode._402);
		}
		
		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("arrangementID", arrangementid);
		
		// 获取记录
		List<JointEntity> result = null;
		result = this.queryByHqlFile("IJointDAO.queryJointByArrangementID", conditionMap);
	
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO#queryMappingJointByTradeid(java.lang.String)
	 */
	@Override
	public List<JointEntity> queryMappingJointByTradeid(String tradeid) throws BizException {
		if (StringUtils.isEmpty(tradeid)) {
			throw new BizException("错误的tradeid参数，请检查", ResponseCode._402);
		}
		
		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("tradeid", tradeid);
		
		//获取记录
		List<JointEntity> result = null;
		result = this.queryByHqlFile("IJointDAO.queryMappingJointByTradeid", conditionMap);
	
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO#getJointWithoutParams
	 * (java.lang.String)
	 */
	@Override
	public JointEntity getJointWithoutParams(String jointuid) throws BizException {
		if (StringUtils.isEmpty(jointuid)) {
			throw new BizException("错误的任务编号jointuid参数，请检查", ResponseCode._402);
		}

		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("jointuid", jointuid);

		// 获取记录
		List<JointEntity> result = null;
		result = this.queryByHqlFile("IJointDAO.getJointWithoutParams", conditionMap);

		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO#getJointWithParams
	 * (java.lang.String)
	 */
	@Override
	public JointEntity getJointWithParams(String jointuid) throws BizException {
		if (StringUtils.isEmpty(jointuid)) {
			throw new BizException("错误的任务编号jointuid参数，请检查", ResponseCode._402);
		}

		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("jointuid", jointuid);

		// 获取记录
		List<JointEntity> result = null;
		result = this.queryByHqlFile("IJointDAO.getJointWithParams", conditionMap);

		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO#createJoint(
	 * com.ai.sboss.arrangement.entity.orm.JointEntity)
	 */
	@Override
	public void createJoint(JointEntity joint) throws BizException {
		// 合法性检查
		if (!checkJointValid(joint)) {
			throw new BizException("任务节点定义错误", ResponseCode._401);
		}
		// 若没有填写uid，系统将为这个joint任务生成一个全系统唯一的
		if (StringUtils.isEmpty(joint.getUid())) {
			joint.setUid(java.util.UUID.randomUUID().toString());
		}
		// 新增
		this.insert(joint);
		
		// 绑定参数
		Set<JointInputParamsEntity> inputParamsEntities = joint.getInputParams();
		Set<JointOutputParamsEntity> outputParamsEntities = joint.getOutputParams();
		String jointuid = joint.getUid();

		if (outputParamsEntities != null) {
			jointOutputParamsDAO.bindJointOutputParams(jointuid, outputParamsEntities);
		}

		if (inputParamsEntities != null) {
			jointInputParamsDAO.bindJointInputParams(jointuid, inputParamsEntities);
		}

		Set<JointTradeMappingEntity> tradeMappingEntities = joint.getTrades();

		// 绑定trade
		if (tradeMappingEntities != null) {
			for (JointTradeMappingEntity tradeMapping : tradeMappingEntities) {
				// updateJointTradeMapping(jointuid, tradeMapping);
				jointTradeMappingDAO.bindJointTrade(tradeMapping);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO#updateJoint(
	 * com.ai.sboss.arrangement.entity.orm.JointEntity)
	 */
	@Override
	public void updateJoint(JointEntity joint) throws BizException {
		// 更新
		this.update(joint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ai.sboss.arrangement.engine.dao.relationdb.IJointDAO#updateJointTrades
	 * (java.lang.String, java.util.Map)
	 */
	@Override
	public void updateJointTrades(String jointuid, Map<String, String> trades) throws BizException {
		if (StringUtils.isEmpty(jointuid) || trades == null) {
			throw new BizException("错误的参数，请检查", ResponseCode._402);
		}
		// 解除之前的行业信息绑定
		jointTradeMappingDAO.releaseAllJointTradeMapping(jointuid);
		// 绑定行业信息
		for (Map.Entry<String, String> iteratorEntry : trades.entrySet()) {
			jointTradeMappingDAO.bindJointTrade(jointuid, iteratorEntry);
		}
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
	private boolean checkJointValid(JointEntity joint) throws BizException {
		if (joint == null) {
			return false;
		}

		boolean ret = true;
		// 由于没有什么可检查的所以直接返回
		return ret;
	}

	@Override
	public void deleteJoint(String jointuid) throws BizException {
		if (StringUtils.isEmpty(jointuid)) {
			throw new BizException("错误的参数，请检查", ResponseCode._402);
		}
		jointInputParamsDAO.releaseAllJointInputParams(jointuid);
		jointOutputParamsDAO.releaseAllJointOutputParams(jointuid);
		jointTradeMappingDAO.releaseAllJointTradeMapping(jointuid);
		
		this.delete(jointuid);
	}
}
package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author yinwenjie
 *
 */
@Component("ArrangementInstanceDAOImpl")
public class ArrangementInstanceDAOImpl extends AbstractRelationalDBDAO<ArrangementInstanceEntity> implements IArrangementInstanceDAO {

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO#getEntityClass()
	 */
	@Override
	protected Class<ArrangementInstanceEntity> getEntityClass() {
		return ArrangementInstanceEntity.class;
	}

	@Override
	@Transactional("transactionManager")
	public PageEntity queryArrangementInstancesByUserid(String userid, String statu,
			Integer pageNumber, Integer perNumber) throws BizException {
		if (StringUtils.isEmpty(userid)) {
			throw new BizException("userid 必须作为查询条件传入", ResponseCode._403);
		}
		if (pageNumber == null) {
			pageNumber = 0;
		}
		if (perNumber == null) {
			perNumber = 20;
		}
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userid", userid);
		if (statu != null) {
			condition.put("statu", statu);
		}
		PageEntity pageEntity = null;
		pageEntity = this.queryByPageHQLFile("IArrangementInstanceDAO.queryArrangementInstancesByUserid", condition, pageNumber,perNumber);

		return pageEntity;

	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementInstanceDAO#queryArrangementInstancesByBusinessID(java.lang.String)
	 */
	@Override
	public ArrangementInstanceEntity queryArrangementInstancesByBusinessID(String businessid) throws BizException {
		if (StringUtils.isEmpty(businessid)) {
			throw new BizException("businessID 必须作为查询条件传入", ResponseCode._403);
		}

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("businessid", businessid);
		
		List<ArrangementInstanceEntity> resultList = null;
		resultList =  this.queryByHqlFile("IArrangementInstanceDAO.queryArrangementInstancesByBusinessID", condition);
		if(resultList == null || resultList.isEmpty()) {
			return null;
		}
		
		return resultList.get(0);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementInstanceDAO#queryArrangementInstancesByID(java.lang.String)
	 */
	@Override
	public ArrangementInstanceEntity queryArrangementInstancesByID(String arrangementInstancesid) throws BizException {
		if (StringUtils.isEmpty(arrangementInstancesid)) {
			throw new BizException("arrangementInstancesid 必须作为查询条件传入", ResponseCode._403);
		}

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("arrangementInstancesid", arrangementInstancesid);
		
		List<ArrangementInstanceEntity> resultList = null;
		resultList =  this.queryByHqlFile("IArrangementInstanceDAO.queryArrangementInstancesByID", condition);
		if(resultList == null || resultList.isEmpty()) {
			return null;
		}
		
		return resultList.get(0);
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementInstanceDAO#createArrangementInstance(com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity)
	 */
	@Override
	public ArrangementInstanceEntity createArrangementInstance(ArrangementInstanceEntity arrangementInstance) throws BizException {
		if (!checkValid(arrangementInstance)) {
			throw new BizException("arrangementInstance参数传入非法。", ResponseCode._402);
		}
		
		if (StringUtils.isEmpty(arrangementInstance.getUid())) {
			arrangementInstance.setUid(UUID.randomUUID().toString());
		}

		if (checkDuplication(arrangementInstance)) {
			throw new BizException("arrangementInstanceId及businessId不能重复创建，请检查", ResponseCode._405);
		}
		
		this.insert(arrangementInstance);
		this.getSessionFactory().getCurrentSession().flush();
		return arrangementInstance;
	}

	@Deprecated
	@Override
	public void updateArrangementInstance(ArrangementInstanceEntity arrangementInstance)
			throws BizException {
		if (arrangementInstance == null) {
			throw new BizException("传入流程实例不能为空", ResponseCode._401);
		}

		if (!checkValid(arrangementInstance) || StringUtils.isEmpty(arrangementInstance.getUid())) {
			throw new BizException("arrangementInstance参数传入非法。", ResponseCode._402);
		}
		this.update(arrangementInstance);	
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementInstanceDAO#updateArrangementInstanceCompletedStatu(java.lang.String)
	 */
	@Override
	public void updateArrangementInstanceCompletedStatu(String arrangementInstanceuid , Long endTime) throws BizException {
		if (StringUtils.isEmpty(arrangementInstanceuid)) {
			throw new BizException("流程实例ID不能为空", ResponseCode._401);
		}
		
		// 组合条件
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("arrangementInstanceId", arrangementInstanceuid);
		condition.put("endTime", endTime);

		this.executeSQLFile("IArrangementInstanceDAO.updateArrangementInstanceCompletedStatu", condition);
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementInstanceDAO#updateArrangementInstanceStatu(java.lang.String, java.lang.String)
	 */
	@Override
	public void updateArrangementInstanceStatu(String arrangementInstanceuid, String statu) throws BizException {
		if (StringUtils.isEmpty(arrangementInstanceuid)) {
			throw new BizException("流程实例ID不能为空", ResponseCode._401);
		}
		
		if (StringUtils.isEmpty(statu) || !StringUtils.equals(statu, "waiting")
				&& !StringUtils.equals(statu, "executing") 
				&& !StringUtils.equals(statu, "revoked") 
				&& !StringUtils.equals(statu, "completed")
				&& !StringUtils.equals(statu, "terminated") ) {
			throw new BizException("statu的值不能为空，且只能是(waiting|executing|revoked|completed|terminated)，请检查。", ResponseCode._403);
		}
		
		// 组合条件
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("arrangementInstanceId", arrangementInstanceuid);
		condition.put("statu", statu);

		this.executeSQLFile("IArrangementInstanceDAO.updateArrangementInstanceStatu", condition);
	}

	@Override
	public void deleteArrangementInstance(String arrangementInstanceuid) throws BizException {
		if (StringUtils.isEmpty(arrangementInstanceuid)) {
			throw new BizException("传入流程实例ID不能为空", ResponseCode._401);
		}
		// 删除记录
		this.delete(arrangementInstanceuid);
	}
	
	/**
	 * 检查一个ArrangementInstance信息，检查BusinessId是否已经被存储了避免重复<br>
	 * 
	 * @param arrangementInstance 待检查的流程实例
	 * @return 若ArrangementInstanceEntity已经存在则返回True， 否则返回False
	 * @throws BizException
	 */
	private boolean checkDuplication(ArrangementInstanceEntity arrangementInstance) throws BizException {
		if (arrangementInstance == null) {
			return false;
		}
		
		boolean ret = false;
		if (queryArrangementInstancesByBusinessID(arrangementInstance.getBusinessID()) != null) {
			ret = true;
		}
		return ret;
	}
	
	private boolean checkValid(ArrangementInstanceEntity arrangementInstance) {
		if (arrangementInstance == null) {
			return false;
		}

		boolean ret = true;
		ret = ret && !StringUtils.isEmpty(arrangementInstance.getCreator());
		ret = ret && (arrangementInstance.getCreateTime() != null);
		ret = ret && !StringUtils.isEmpty(arrangementInstance.getStatu());
		ret = ret && !StringUtils.isEmpty(arrangementInstance.getDisplayName());
		ret = ret && !StringUtils.isEmpty(arrangementInstance.getCreatorScope());
		ret = ret && (StringUtils.equals(arrangementInstance.getCreatorScope(), "industry") 
				|| StringUtils.equals(arrangementInstance.getCreatorScope(), "producer")
				|| StringUtils.equals(arrangementInstance.getCreatorScope(), "consumer"));
		ret = ret && (arrangementInstance.getArrangement() != null);

		return ret;
	}	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementInstanceDAO#queryArrangementInstanceBybusinessID(java.lang.String)
	 */
	@Override
	public ArrangementInstanceEntity queryArrangementInstanceBybusinessID(String businessid) throws BizException {
		if (StringUtils.isEmpty(businessid)) {
			throw new BizException("错误的任务编号businessid参数，请检查", ResponseCode._402);
		}

		// 组装查询条件
		HashMap<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("businessID", businessid);

		// 获取记录
		List<ArrangementInstanceEntity> result = null;
		try {
			result = this.queryByHqlFile("IArrangementInstanceDAO.queryArrangementInstanceBybusinessID", conditionMap);
		} catch (Exception e) {
			throw new BizException(e.getMessage(), ResponseCode._501);
		} 

		if (result == null || result.size() == 0) {
			return null;
		}
		return result.get(0);
	}

}
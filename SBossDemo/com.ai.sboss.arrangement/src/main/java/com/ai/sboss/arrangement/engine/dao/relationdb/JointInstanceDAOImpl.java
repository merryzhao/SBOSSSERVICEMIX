package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.PageEntity;

import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author yinwenjie
 */
@Component("jointInstanceDAOImpl")
public class JointInstanceDAOImpl extends AbstractRelationalDBDAO<JointInstanceEntity>implements IJointInstanceDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(JointInstanceDAOImpl.class);

	@Autowired
	private IJointInputParamsInstanceDAO jointInputParamsInstanceDAO;

	@Autowired
	private IJointOutputParamsInstanceDAO jointOutputParamsInstanceDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO#
	 * getEntityClass()
	 */
	@Override
	protected Class<JointInstanceEntity> getEntityClass() {
		return JointInstanceEntity.class;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceDAO#queryJointInstancesByID(java.lang.String)
	 */
	@Override
	public JointInstanceEntity queryJointInstancesByID(String jointInstanceid) throws BizException {
		if (StringUtils.isEmpty(jointInstanceid)) {
			throw new BizException("jointInstanceid 必须作为查询条件传入", ResponseCode._403);
		}
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("jointInstanceid", jointInstanceid);
		
		List<JointInstanceEntity> resultList = null;
		resultList =  this.queryByHqlFile("IJointInstanceDAO.queryJointInstancesByID", condition);
		if(resultList == null || resultList.isEmpty()) {
			return null;
		}
		
		return resultList.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceDAO#
	 * queryJointInstancesByUserid(java.lang.String, java.lang.Integer,
	 * java.lang.Integer)
	 */
	@Override
	@Transactional("transactionManager")
	public PageEntity queryJointInstancesByUserid(String userid, Integer pageNumber, Integer perNumber) throws BizException {
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
		PageEntity pageEntity = null;
		try {
			pageEntity = this.queryByPageSQLFile("IJointInstanceDAO.queryJointInstancesByUserid", condition, pageNumber, perNumber);
		} catch (BizException e) {
			JointInstanceDAOImpl.LOGGER.error(e.getMessage(), e);
			throw new BizException(e.getMessage(), ResponseCode._501);
		}

		return pageEntity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceDAO#
	 * queryJointInstancesByBusinessID(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public List<JointInstanceEntity> queryJointInstancesByBusinessID(String businessid) throws BizException {
		if (StringUtils.isEmpty(businessid)) {
			throw new BizException("businessID 必须作为查询条件传入", ResponseCode._403);
		}

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("businessid", businessid);

		return this.queryByHqlFile("IJointInstanceDAO.queryJointInstancesByBusinessid", condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceDAO#
	 * queryJointInstanceEntityByInstanceID(java.lang.String, java.lang.String)
	 */
	@Override
	public List<JointInstanceEntity> queryJointInstanceEntityByInstanceID(String arrangementInstanceuid, String jointStatu) throws BizException {
		if (StringUtils.isEmpty(arrangementInstanceuid)) {
			throw new BizException("arrangementInstanceuid条件必须传入，请检查。", ResponseCode._403);
		}

		if (jointStatu != null && !StringUtils.equals(jointStatu, "waiting")
				&& !StringUtils.equals(jointStatu, "executing") && !StringUtils.equals(jointStatu, "followed")
				&& !StringUtils.equals(jointStatu, "revoked") && !StringUtils.equals(jointStatu, "completed")
				&& !StringUtils.equals(jointStatu, "terminated")) {
			throw new BizException("jointStatu的值只能是(waiting|executing|followed|revoked|completed|terminated)，请检查。" , ResponseCode._403);
		}

		// 组合条件
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("arrangementInstanceuid", arrangementInstanceuid);
		if (jointStatu != null) {
			condition.put("jointStatu", jointStatu);
		}

		List<JointInstanceEntity> result = null;
		result = this.queryByHqlFile("IJointInstanceDAO.queryJointInstanceEntityByInstanceID", condition);

		return result;
	}

	@Override
	public JointInstanceEntity createJointInstance(JointInstanceEntity jointInstance) throws BizException {
		if (!checkJointInstanceValid(jointInstance)) {
			throw new BizException("jointInstance参数传入非法。", ResponseCode._402);
		}

		if (StringUtils.isEmpty(jointInstance.getUid())) {
			jointInstance.setUid(UUID.randomUUID().toString());
		}

		this.insert(jointInstance);
		this.getSessionFactory().getCurrentSession().flush();
		return jointInstance;
	}

	private boolean checkJointInstanceValid(JointInstanceEntity jointInstance) {
		if (jointInstance == null) {
			return false;
		}

		boolean ret = true;
		ret = ret && (jointInstance.getArrangementInstance() != null);
		ret = ret && (jointInstance.getJoint() != null);
		ret = ret && !StringUtils.isEmpty(jointInstance.getCreator());
		ret = ret && !StringUtils.isEmpty(jointInstance.getStatu());
		ret = ret && (StringUtils.equals(jointInstance.getStatu(), "waiting")
				|| StringUtils.equals(jointInstance.getStatu(), "executing")
				|| StringUtils.equals(jointInstance.getStatu(), "followed")
				|| StringUtils.equals(jointInstance.getStatu(), "revoked")
				|| StringUtils.equals(jointInstance.getStatu(), "completed")
				|| StringUtils.equals(jointInstance.getStatu(), "terminated"));

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceDAO#
	 * updateJointInstanceStatu(java.lang.String, java.lang.String)
	 */
	@Override
	public void updateJointInstanceStatu(String jointInstanceuid, String jointStatu) throws BizException {
		if (StringUtils.isEmpty(jointInstanceuid)) {
			throw new BizException("任务实例ID不能为空", ResponseCode._401);
		}

		if (StringUtils.isEmpty(jointStatu) || !StringUtils.equals(jointStatu, "waiting")
				&& !StringUtils.equals(jointStatu, "executing") && !StringUtils.equals(jointStatu, "followed")
				&& !StringUtils.equals(jointStatu, "revoked") && !StringUtils.equals(jointStatu, "completed")
				&& !StringUtils.equals(jointStatu, "terminated")) {
			throw new BizException(
					"jointStatu的值不能为空，且只能是(waiting|executing|followed|revoked|completed|terminated)，请检查。",
					ResponseCode._403);
		}
		// 组合条件
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("jointInstanceId", jointInstanceuid);
		condition.put("statu", jointStatu);

		this.executeSQLFile("IJointInstanceDAO.updateJointInstanceStatu", condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceDAO#
	 * updateCompletedJointInstanceStatuByArrangementInstanceId(java.lang.
	 * String)
	 */
	@Override
	public void updateCompletedJointInstanceStatuByArrangementInstanceId(String arrangementInstanceId) throws BizException {
		if (StringUtils.isEmpty(arrangementInstanceId)) {
			throw new BizException("流程实例ID不能为空", ResponseCode._401);
		}

		// 组合条件
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("arrangementInstanceId", arrangementInstanceId);

		this.executeSQLFile("IJointInstanceDAO.updateCompletedJointInstanceStatuByArrangementInstanceId", condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceDAO#
	 * updateFollowedJointInstanceStatuByJointInstanceId(java.lang.String,
	 * java.lang.Long, java.lang.String)
	 */
	@Override
	public void updateFollowedJointInstanceStatuByJointInstanceId(String jointInstanceuid, Long exeTime, String executor) throws BizException {
		if (StringUtils.isEmpty(jointInstanceuid)) {
			throw new BizException("任务实例ID不能为空", ResponseCode._401);
		}
		if (exeTime == null) {
			exeTime = new Date().getTime();
		}

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("exeTime", exeTime);
		condition.put("executor", executor == null?"":executor);
		condition.put("jointInstanceuid", jointInstanceuid);

		this.executeSQLFile("IJointInstanceDAO.updateFollowedJointInstanceStatuByJointInstanceId", condition);
	}

	@Override
	public void deleteJointInstance(String jointInstanceuid) throws BizException {
		if (StringUtils.isEmpty(jointInstanceuid)) {
			throw new BizException("传入任务实例ID不能为空", ResponseCode._401);
		}
		// 删除记录
		this.delete(jointInstanceuid);
	}

	@Override
	public void deleteJointInstancesByArrangementInstanceID(String arrangementInstanceuid) throws BizException {
		if (StringUtils.isEmpty(arrangementInstanceuid)) {
			throw new BizException("传入流程实例ID不能为空", ResponseCode._401);
		}

		List<JointInstanceEntity> jointInstances = queryJointInstanceEntityByInstanceID(arrangementInstanceuid, null);

		for (JointInstanceEntity entity : jointInstances) {
			this.jointInputParamsInstanceDAO.deleteInputParamsInstancesByJointInstanceID(entity.getUid());
			this.jointOutputParamsInstanceDAO.deleteOutputParamsInstancesByJointInstanceID(entity.getUid());
			deleteJointInstance(entity.getUid());
		}
	}
}

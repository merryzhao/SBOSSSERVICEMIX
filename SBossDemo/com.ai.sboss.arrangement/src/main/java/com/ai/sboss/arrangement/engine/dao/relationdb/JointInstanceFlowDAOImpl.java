package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceFlowEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author yinwenjie
 *
 */
@Component("JointInstanceFlowDAOImpl")
public class JointInstanceFlowDAOImpl extends AbstractRelationalDBDAO<JointInstanceFlowEntity> implements IJointInstanceFlowDAO {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(JointInstanceFlowDAOImpl.class);
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO#getEntityClass()
	 */
	@Override
	protected Class<JointInstanceFlowEntity> getEntityClass() {
		return JointInstanceFlowEntity.class;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceFlowDAO#queryExecutingJointInstanceByArrangementInstanceId(java.lang.String)
	 */
	@Override
	public JointInstanceFlowEntity queryExecutingJointInstanceByArrangementInstanceId(String arrangementInstanceId) throws BizException {
		if(StringUtils.isEmpty(arrangementInstanceId)) {
			throw new BizException("错误的arrangementInstanceId信息", ResponseCode._403);
		}
		
		Map<String, Object> condition = new HashMap<String , Object>();
		condition.put("arrangementInstanceId", arrangementInstanceId);
		
		List<JointInstanceFlowEntity> result = null;
		result = this.queryByHqlFile("IJointInstanceFlowDAO.queryExecutingJointInstanceByArrangementInstanceId", condition);
		
		if(result == null || result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceFlowDAO#queryJointFlowByJointInstanceId(java.lang.String)
	 */
	@Override
	public JointInstanceFlowEntity queryJointFlowByJointInstanceId(String jointInstanceId) throws BizException {
		if(StringUtils.isEmpty(jointInstanceId)) {
			throw new BizException("错误的jointInstanceId信息", ResponseCode._403);
		}
		
		Map<String, Object> condition = new HashMap<String , Object>();
		condition.put("jointInstanceId", jointInstanceId);
		
		List<JointInstanceFlowEntity> result = null;
		result = this.queryByHqlFile("IJointInstanceFlowDAO.queryJointFlowByJointInstanceId", condition);
		
		if(result == null || result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceFlowDAO#queryJointInstanceByArrangementInstanceId(java.lang.String, java.lang.String)
	 */
	@Override
	public List<JointInstanceFlowEntity> queryJointInstanceByArrangementInstanceId(String arrangementInstanceId, String statu) throws BizException {
		if(StringUtils.isEmpty(arrangementInstanceId)) {
			throw new BizException("错误的arrangementInstanceId信息", ResponseCode._403);
		}
		
		Map<String, Object> condition = new HashMap<String , Object>();
		condition.put("arrangementInstanceId", arrangementInstanceId);
		if(!StringUtils.isEmpty(statu)) {
			condition.put("statu", statu);
		}
		
		List<JointInstanceFlowEntity> result = null;
		result = this.queryByHqlFile("IJointInstanceFlowDAO.queryJointInstanceByArrangementInstanceId", condition);
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceFlowDAO#queryNextJointInstanceByJointInstanceId(java.lang.String)
	 */
	@Override
	public JointInstanceEntity queryNextJointInstanceByJointInstanceId(String jointInstanceId) throws BizException {
		if(StringUtils.isEmpty(jointInstanceId)) {
			throw new BizException("错误的任务实例编号信息", ResponseCode._403);
		}
		JointInstanceFlowEntity flow = this.queryJointFlowByJointInstanceId(jointInstanceId);
		if(flow == null) {
			return null;
		}
		
		//取得下一个任务实例的信息
		JointInstanceEntity nextJointInstance = flow.getNextJointInstance();
		return nextJointInstance;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceFlowDAO#updateJointFlowStatuByFlowId(java.lang.String, java.lang.String)
	 */
	@Override
	public void updateJointFlowStatuByFlowId(String flowid, String jointStatu) throws BizException {
		//进行可用性判断
		if (StringUtils.isEmpty(flowid)) {
			throw new BizException("任务实例流转ID不能为空", ResponseCode._401);
		}
		if (StringUtils.isEmpty(jointStatu)) {
			throw new BizException("任务实例状态不能为空", ResponseCode._401);
		}
		if (!StringUtils.equals(jointStatu, "waiting") && !StringUtils.equals(jointStatu, "executing") 
				&& !StringUtils.equals(jointStatu, "followed") && !StringUtils.equals(jointStatu, "revoked") 
				&& !StringUtils.equals(jointStatu, "completed") && !StringUtils.equals(jointStatu, "terminated")) {
			throw new BizException("jointStatu的值只能是(waiting|executing|followed|revoked|completed|terminated)，请检查。", ResponseCode._403);
		}
		
		//赋值，进行执行
		Map<String, Object> condition = new HashMap<String , Object>();
		condition.put("uid", flowid);
		condition.put("jointStatu", jointStatu);
		
		this.executeSQLFile("IJointInstanceFlowDAO.updateJointFlowStatuByFlowId", condition);
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceFlowDAO#updateCompletedJointInstanceStatuByArrangementInstanceId(java.lang.String)
	 */
	@Override
	public void updateCompletedJointInstanceStatuByArrangementInstanceId(String arrangementInstanceId) throws BizException {
		if (StringUtils.isEmpty(arrangementInstanceId)) {
			throw new BizException("流程实例ID不能为空", ResponseCode._401);
		}
		
		//组合条件
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("arrangementInstanceId", arrangementInstanceId);
		
		this.executeSQLFile("IJointInstanceFlowDAO.updateCompletedJointInstanceStatuByArrangementInstanceId", condition);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IJointInstanceFlowDAO#updateFollowedJointInstanceStatuByJointInstanceId(java.lang.String, java.lang.Long, java.lang.String)
	 */
	@Override
	public void updateFollowedJointInstanceStatuByFlowId(String flowid, Long exeTime, String executor) throws BizException {
		if (StringUtils.isEmpty(flowid)) {
			throw new BizException("任务实例流转ID不能为空", ResponseCode._401);
		}
		if (exeTime == null) {
			exeTime = new Date().getTime();
		}
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("exeTime", exeTime);
		condition.put("executor", executor == null?"":executor);
		condition.put("uid", flowid);
		
		this.executeSQLFile("IJointInstanceFlowDAO.updateFollowedJointInstanceStatuByFlowId", condition);
	}

	@Override
	public JointInstanceFlowEntity createJointInstanceFlow(JointInstanceFlowEntity jointInstanceFlow)
			throws BizException {
		if (!checkValid(jointInstanceFlow)) {
			throw new BizException("jointInstanceFlow参数传入非法。", ResponseCode._402);
		}

		if (StringUtils.isEmpty(jointInstanceFlow.getUid())) {
			jointInstanceFlow.setUid(UUID.randomUUID().toString());
		}

		try {
			this.insert(jointInstanceFlow);
			this.getSessionFactory().getCurrentSession().flush();
		} catch (Exception e) {
			JointInstanceFlowDAOImpl.LOGGER.error(e.getMessage(), e);
			throw new BizException(e.getMessage(), ResponseCode._501);
		}
		
		return jointInstanceFlow;
	}

	private boolean checkValid(JointInstanceFlowEntity jointInstanceFlow) {
		if (jointInstanceFlow == null) {
			return false;
		}

		boolean ret = true;
		ret = ret && (jointInstanceFlow.getJointInstance() != null);
		ret = ret && (jointInstanceFlow.getJoint() != null);
		ret = ret && (jointInstanceFlow.getExpectedExeTime() != null);
		ret = ret && !StringUtils.isEmpty(jointInstanceFlow.getStatu());

		return ret;
	}

	@Override
	public List<JointInstanceFlowEntity> querySortedJointInstanceByArrangementInstanceId(String arrangementInstanceId)
			throws BizException {
		if(StringUtils.isEmpty(arrangementInstanceId)) {
			throw new BizException("错误的arrangementInstanceId信息", ResponseCode._403);
		}
		
		Map<String, Object> condition = new HashMap<String , Object>();
		condition.put("arrangementInstanceId", arrangementInstanceId);
		
		List<Object[]> queryresult = null;
		queryresult = this.queryBySqlFile("IJointInstanceFlowDAO.querySortedJointInstanceByArrangementInstanceId", condition);
		List<JointInstanceFlowEntity> returnResult = new ArrayList<JointInstanceFlowEntity>();
		Iterator<Object[]> objIter = queryresult.iterator();

		for (;objIter.hasNext();) {
			JointInstanceFlowEntity instanceFlowEntity = new JointInstanceFlowEntity();
			JointInstanceEntity instanceEntity = new JointInstanceEntity();
			Object[] rawObjs = objIter.next();
			instanceEntity.setUid(String.valueOf(rawObjs[0]));
			instanceEntity.setAbsOffsettime(rawObjs[1]!=null?Long.parseLong(rawObjs[1].toString()):0L);
			instanceEntity.setCamelUri(String.valueOf(rawObjs[2]));
			instanceEntity.setExecutor(String.valueOf(rawObjs[3]));
			instanceEntity.setPromptOffsettime(String.valueOf(rawObjs[4]));
			instanceEntity.setRelateOffsettime(rawObjs[5]!=null?Long.parseLong(rawObjs[5].toString()):0L);
			//skip arrangement6
			JointEntity joint = new JointEntity();
			joint.setUid(String.valueOf(rawObjs[7]));
			instanceEntity.setJoint(joint);
			instanceEntity.setCreator(String.valueOf(rawObjs[8]));
			instanceEntity.setExpandTypeId(rawObjs[9]!=null?Integer.parseInt(rawObjs[9].toString()):0);
			instanceEntity.setOffsetTitle(String.valueOf(rawObjs[10]));
			instanceEntity.setOffsetVisible(String.valueOf(rawObjs[11]));
			instanceEntity.setExeTime(rawObjs[12]!=null?Long.parseLong(rawObjs[12].toString()):0L);
			instanceEntity.setExpectedExeTime(rawObjs[13]!=null?Long.parseLong(rawObjs[13].toString()):0L);
			instanceEntity.setStatu(String.valueOf(rawObjs[14]));
			instanceEntity.setWeight(rawObjs[15]!=null?Long.parseLong(rawObjs[15].toString()):0L);
			LOGGER.info("Got properties===>"+rawObjs[16]);
			instanceEntity.setProperties(String.valueOf(rawObjs[16]));
			
			instanceFlowEntity.setUid(String.valueOf(rawObjs[17]));
			instanceFlowEntity.setExecutor(String.valueOf(rawObjs[18]));
			instanceFlowEntity.setStatu(String.valueOf(rawObjs[19]));
			instanceFlowEntity.setJoint(joint);
			instanceFlowEntity.setJointInstance(instanceEntity);
			instanceFlowEntity.setPreviouArrangementInstance(null);
			instanceFlowEntity.setPreviouJointInstance(null);
			instanceFlowEntity.setNextArrangementInstance(null);
			instanceFlowEntity.setNextJointInstance(null);
			instanceFlowEntity.setExeTime(rawObjs[27]!=null?Long.parseLong(rawObjs[27].toString()):0L);
			
			
			returnResult.add(instanceFlowEntity);
		}
		LOGGER.info("numbers ->" + returnResult.size());
		return returnResult;
		
		//return result;
	}
}

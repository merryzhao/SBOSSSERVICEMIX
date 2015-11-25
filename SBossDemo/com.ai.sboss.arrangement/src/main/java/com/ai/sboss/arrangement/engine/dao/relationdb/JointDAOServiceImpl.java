package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ai.sboss.arrangement.engine.dao.JointDAOService;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointTradeMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * 持久层JointDAOService的一个mysql关系型数据库门面的实现
 * @author yinwenjie
 *
 */
@Component("jointDAOServiceImpl")
@Transactional("transactionManager")
public class JointDAOServiceImpl implements JointDAOService {
	@Autowired
	private IJointDAO jointDAO;
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.JointDAOService#queryJointByTradeid(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public List<JointEntity> queryJointByTradeid(String tradeid, String scope) throws BizException {
		return jointDAO.queryJointByTradeid(tradeid, scope);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.JointDAOService#queryJointByTradeidPage(java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Transactional("transactionManager")
	public PageEntity queryJointByTradeidPage(String tradeid, String scope, Integer pageNumber, Integer maxPerNumber) throws BizException {
		return jointDAO.queryJointByTradeidPage(tradeid, scope, pageNumber, maxPerNumber);
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.JointDAOService#queryJointBybusinessID(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public List<JointEntity> queryJointBybusinessID(String businessid) throws BizException {
		/*
		 * 1、首先，根据businessid，查询到流程实例
		 * 2、然后根据这个流程实例查询对应的流程模板的id，查询归属其的任务模板
		 * （任务模板集合）按照偏移量进行返回
		 * */
		
		//1、=========
		//TODO 由于现在流程实例的功能没有完成，所以目前所有实例都是使用一个模板。所以先写死
//		ArrangementInstanceEntity arrangementInstance = this.arrangementInstanceDAO.queryArrangementInstanceBybusinessID(businessid);
//		if(arrangementInstance == null) {
//			return null;
//		}
//		String arrangementid = arrangementInstance.getArrangement().getUid();
		String arrangementid = "4eb9e271-c9fc-4153-b33e-ce631acda97d";
		
		//2、========
		List<JointEntity> joints = this.jointDAO.queryJointByArrangementID(arrangementid);
		return joints;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.JointDAOService#queryJointByArrangementID(java.lang.String)
	 */
	@Override
	public List<JointEntity> queryJointByArrangementID(String arrangementid) throws BizException {
		return this.jointDAO.queryJointByArrangementID(arrangementid);
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.JointDAOService#queryMappingJointByTradeid(java.lang.String)
	 */
	@Override
	public List<JointEntity> queryMappingJointByTradeid(String tradeid) throws BizException {
		return this.jointDAO.queryMappingJointByTradeid(tradeid);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.JointDAOService#getJointWitoutParams(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public JointEntity getJointWithoutParams(String jointuid) throws BizException {
		return jointDAO.getJointWithoutParams(jointuid);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.JointDAOService#getJointWithParams(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public JointEntity getJointWithParams(String jointuid) throws BizException {
		return jointDAO.getJointWithParams(jointuid);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.JointDAOService#createJoint(com.ai.sboss.arrangement.entity.orm.JointEntity)
	 */
	@Override
	@Transactional("transactionManager")
	public void createJoint(JointEntity joint) throws BizException {
		Set<JointInputParamsEntity> inputParamsEntities = joint.getInputParams();
		Set<JointTradeMappingEntity> tradeMappingEntities = joint.getTrades();
		if (inputParamsEntities == null || tradeMappingEntities == null) {
			throw new BizException("joint对应的inputparams集合、trade集合信息都必须设置", ResponseCode._401);
		}
		jointDAO.createJoint(joint);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.JointDAOService#updateJoint(com.ai.sboss.arrangement.entity.orm.JointEntity)
	 */
	@Override
	@Transactional("transactionManager")
	public void updateJoint(JointEntity joint) throws BizException {
		//检查相应的行业信息是否已经设置
		Set<JointTradeMappingEntity> tradeMappingEntities = joint.getTrades();
		if (tradeMappingEntities == null) {
			throw new BizException("行业信息不能为空", ResponseCode._401);
		}
		jointDAO.updateJoint(joint);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.JointDAOService#updateJointTrades(java.lang.String, java.util.Map)
	 */
	@Override
	@Transactional("transactionManager")
	public void updateJointTrades(String jointuid, Map<String, String> trades) throws BizException {
		jointDAO.updateJointTrades(jointuid, trades);
	}

	@Override
	@Transactional("transactionManager")
	public void deleteJoint(String jointuid) throws BizException {
		jointDAO.deleteJoint(jointuid);
	}
}
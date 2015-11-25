package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ai.sboss.arrangement.engine.dao.ArrangementDAOService;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementSelfMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * 持久层ArrangementDAOService的一个mysql关系型数据库门面的实现
 * @author yinwenjie
 */
@Component("arrangementDAOServiceImpl")
@Transactional("transactionManager")
public class ArrangementDAOServiceImpl implements ArrangementDAOService {
	@Autowired
	private IArrangementDAO arrangementDAO;
	@Autowired
	private IArrangementJointMappingDAO arrangementJointMappingDAO;
	@Autowired
	private IArrangementSelfMappingDAO arrangementSelfMappingDAO;

	@Override
	@Transactional("transactionManager")
	public List<ArrangementEntity> queryArrangementByTradeidWithoutSet(String tradeid, String scope) throws BizException {
		List<ArrangementEntity> result = arrangementDAO.queryArrangementByTradeidWithoutSet(tradeid, scope);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.ArrangementDAOService#
	 * queryArrangementByTradeidPageWithoutSet(java.lang.String,
	 * java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Transactional("transactionManager")
	public PageEntity queryArrangementByTradeidPageWithoutSet(String tradeid, String scope, Integer pageNumber, Integer maxPerNumber) throws BizException {
		PageEntity resultEntity = arrangementDAO.queryArrangementByTradeidPageWithoutSet(tradeid, scope, pageNumber, maxPerNumber);
		return resultEntity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.ArrangementDAOService#
	 * getArrangementWithoutSet(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public ArrangementEntity getArrangementWithoutSet(String arrangementuid) throws BizException {
		ArrangementEntity resultEntity = arrangementDAO.getArrangementWithoutSet(arrangementuid);
		return resultEntity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.ArrangementDAOService#
	 * getArrangementWithSet(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public ArrangementEntity getArrangementWithSet(String arrangementuid) throws BizException {
		return this.arrangementDAO.getArrangementWithSet(arrangementuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ai.sboss.arrangement.engine.dao.ArrangementDAOService#createArrangement
	 * (com.ai.sboss.arrangement.entity.orm.ArrangementEntity)
	 */
	@Override
	@Transactional("transactionManager")
	public void createArrangement(ArrangementEntity arrangement) throws BizException {
		this.arrangementDAO.createArrangement(arrangement);
		Set<ArrangementJointMappingEntity> jointMappingEntities = arrangement.getJointmapping();
		if (jointMappingEntities != null) { 
			this.arrangementJointMappingDAO.bindArrangementJointmapping(arrangement.getUid(), jointMappingEntities);
		}
		Set<ArrangementSelfMappingEntity> childArrangements = arrangement.getChildArrangements();
		if (childArrangements != null) {
			this.arrangementSelfMappingDAO.bindArrangementChildArrangements(arrangement.getUid(), childArrangements);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ai.sboss.arrangement.engine.dao.ArrangementDAOService#updateArrangement
	 * (com.ai.sboss.arrangement.entity.orm.ArrangementEntity)
	 */
	@Override
	@Transactional("transactionManager")
	public void updateArrangement(ArrangementEntity arrangement) throws BizException {
		arrangementDAO.updateArrangement(arrangement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.ArrangementDAOService#
	 * bindArrangementJointmapping(java.lang.String, java.util.Set)
	 */
	@Override
	@Transactional("transactionManager")
	public void updateArrangementJointmapping(String arrangementuid, Set<ArrangementJointMappingEntity> jointmapping) throws BizException {
		arrangementJointMappingDAO.releaseAllArrangementJointmapping(arrangementuid);
		arrangementJointMappingDAO.bindArrangementJointmapping(arrangementuid, jointmapping);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ai.sboss.arrangement.engine.dao.ArrangementDAOService#
	 * bindArrangementChildArrangements(java.lang.String, java.util.Set)
	 */
	@Override
	@Transactional("transactionManager")
	public void updateArrangementChildArrangements(String arrangementuid, Set<ArrangementSelfMappingEntity> childArrangements) throws BizException {
		arrangementSelfMappingDAO.releaseAllArrangementChildArrangements(arrangementuid);
		arrangementSelfMappingDAO.bindArrangementChildArrangements(arrangementuid, childArrangements);
	}

	@Override
	@Transactional("transactionManager")
	public void deleteArrangement(String arrangementuid) throws BizException {
		//TODO:这里的业务动作，需要进行讨论并确认.如果说Instance不能被删除，这里的模版也不应该能删除。同时就需要一个标志位来表明这个模版无效
		arrangementDAO.deleteArrangement(arrangementuid);
	}
}

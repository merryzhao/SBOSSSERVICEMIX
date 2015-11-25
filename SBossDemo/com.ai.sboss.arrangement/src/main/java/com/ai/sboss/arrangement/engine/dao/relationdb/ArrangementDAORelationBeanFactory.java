package com.ai.sboss.arrangement.engine.dao.relationdb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory;
import com.ai.sboss.arrangement.engine.dao.ArrangementDAOService;
import com.ai.sboss.arrangement.engine.dao.InstanceDAOService;
import com.ai.sboss.arrangement.engine.dao.JointDAOService;
import com.ai.sboss.arrangement.engine.dao.LogDAOService;
import com.ai.sboss.arrangement.engine.dao.ParamsDAOService;

/**
 * 编排系统数据持久层的关系型数据库形态的抽象工厂的具体实现。
 * @author yinwenjie
 */
@Component("arrangementDAORelationBeanFactory")
public class ArrangementDAORelationBeanFactory extends ArrangementDAOAbstractFactory {
	/**
	 * 
	 */
	@Autowired
	private ArrangementDAOService arrangementDAOService;
	
	/**
	 * 
	 */
	@Autowired
	private InstanceDAOService instanceDAOService;
	
	/**
	 * 
	 */
	@Autowired
	private JointDAOService jointDAOService;
	
	/**
	 * 
	 */
	@Autowired
	private ParamsDAOService paramsDAOService;
	
	/**
	 * 
	 */
	@Autowired
	private LogDAOService logDAOService;
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory#getArrangementDAOService()
	 */
	@Override
	public ArrangementDAOService getArrangementDAOService() {
		return this.arrangementDAOService;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory#getInstanceDAOService()
	 */
	@Override
	public InstanceDAOService getInstanceDAOService() {
		return this.instanceDAOService;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory#getJointDAOService()
	 */
	@Override
	public JointDAOService getJointDAOService() {
		return this.jointDAOService;
	}
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory#getLogDAOService()
	 */
	@Override
	public LogDAOService getLogDAOService() {
		return this.logDAOService;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory#getParamsDAOService()
	 */
	@Override
	@Transactional("transactionManager")
	public ParamsDAOService getParamsDAOService() {
		return this.paramsDAOService;
	}

	public void setArrangementDAOService(ArrangementDAOService arrangementDAOService) {
		this.arrangementDAOService = arrangementDAOService;
	}

	public void setInstanceDAOService(InstanceDAOService instanceDAOService) {
		this.instanceDAOService = instanceDAOService;
	}

	public void setJointDAOService(JointDAOService jointDAOService) {
		this.jointDAOService = jointDAOService;
	}

	public void setParamsDAOService(ParamsDAOService paramsDAOService) {
		this.paramsDAOService = paramsDAOService;
	}
}

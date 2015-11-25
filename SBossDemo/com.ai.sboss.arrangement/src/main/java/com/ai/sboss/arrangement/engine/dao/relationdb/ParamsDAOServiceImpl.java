package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ai.sboss.arrangement.engine.dao.ParamsDAOService;
import com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity;
import com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * 持久层ParamsDAOService的一个mysql关系型数据库门面的实现
 * @author yinwenjie
 *
 */
@Component("paramsDAOServiceImpl")
public class ParamsDAOServiceImpl implements ParamsDAOService {
	@Autowired
	private IJointInputParamsDAO jointInputParamsDAO;
	
	@Autowired
	private IJointOutputParamsDAO jointOutputParamsDAO;
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.ParamsDAOService#queryInputParamsByjointuid(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public List<JointInputParamsEntity> queryInputParamsByjointuid(String jointuid) throws BizException {
		return jointInputParamsDAO.queryInputParamsByjointuid(jointuid);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.ParamsDAOService#queryOutputParamsByjointuid(java.lang.String)
	 */
	@Override
	@Transactional("transactionManager")
	public List<JointOutputParamsEntity> queryOutputParamsByjointuid(String jointuid) throws BizException {
		return jointOutputParamsDAO.queryOutputParamsByjointuid(jointuid);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.ParamsDAOService#bindJointInputParams(java.lang.String, java.util.Set)
	 */
	@Override
	@Transactional("transactionManager")
	public void updateJointInputParams(String jointuid, Set<JointInputParamsEntity> inputParams) throws BizException {
		jointInputParamsDAO.releaseAllJointInputParams(jointuid);
		jointInputParamsDAO.bindJointInputParams(jointuid, inputParams);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.ParamsDAOService#bindJointOutputParams(java.lang.String, java.util.Set)
	 */
	@Override
	@Transactional("transactionManager")
	public void updateJointOutputParams(String jointuid, Set<JointOutputParamsEntity> outputParams) throws BizException {
		jointOutputParamsDAO.releaseAllJointOutputParams(jointuid);
		jointOutputParamsDAO.bindJointOutputParams(jointuid, outputParams);
	}
}
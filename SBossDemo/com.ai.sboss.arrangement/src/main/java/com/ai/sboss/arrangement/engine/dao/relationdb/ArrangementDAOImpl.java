package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity;
import com.ai.sboss.arrangement.entity.orm.JointEntity;
import com.ai.sboss.arrangement.entity.orm.JointTradeMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author yinwenjie
 */
@Component("arrangementDAOImpl")
public class ArrangementDAOImpl extends AbstractRelationalDBDAO<ArrangementEntity> implements IArrangementDAO {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(ArrangementDAOImpl.class);
	
	/**
	 * 
	 */
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
	protected Class<ArrangementEntity> getEntityClass() {
		return ArrangementEntity.class;
	}

	@Override
	public List<ArrangementEntity> queryArrangementByTradeidWithoutSet(String tradeid, String scope) throws BizException {
		if (StringUtils.isEmpty(tradeid)) {
			throw new BizException("错误的行业tradeid参数，请检查", ResponseCode._402);
		}

		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("tradeid", tradeid);
		if (!StringUtils.isEmpty(scope)) {
			conditionMap.put("scope", scope);
		}

		// 获取记录
		List<ArrangementEntity> result = null;
		result = this.queryByHqlFile("IArrangementDAO.queryArrangementByTradeidWithoutSet", conditionMap);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementDAO#queryArrangementByTradeidPageWithoutSet(java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageEntity queryArrangementByTradeidPageWithoutSet(String tradeid, String scope, Integer pageNumber, Integer maxPerNumber) throws BizException {
		PageEntity retPageEntity = new PageEntity();
		if (pageNumber == null || pageNumber < 0) {
			pageNumber = 0;
		}
		if (maxPerNumber == null || maxPerNumber < 0) {
			maxPerNumber = 20;
		}

		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("tradeid", tradeid);
		
		if (StringUtils.isEmpty(scope)) {
			conditionMap.put("scope", scope);
		}
		
		//调用分页hql查询
		retPageEntity = this.queryByPageHQLFile("IArrangementDAO.queryArrangementByTradeidPageWithoutSet", conditionMap, pageNumber, maxPerNumber);
		return retPageEntity;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.dao.relationdb.IArrangementDAO#getArrangementWithoutSet(java.lang.String)
	 */
	@Override
	public ArrangementEntity getArrangementWithoutSet(String arrangementuid) throws BizException {
		if (StringUtils.isEmpty(arrangementuid)) {
			throw new BizException("错误的流程编号arrangementuid参数，请检查", ResponseCode._402);
		}

		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("arrangementuid", arrangementuid);

		// 获取记录
		List<ArrangementEntity> result = null;
		result = this.queryByHqlFile("IArrangementDAO.getArrangementWithoutSet", conditionMap);

		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	@Override
	public void createArrangement(ArrangementEntity arrangement) throws BizException {	
		if (arrangement == null) {
			throw new BizException("错误的流程实体ArrangementEntity参数，请检查", ResponseCode._402);
		}
		// reade正确性检查
		if(!this.checkArrangementTradeid(arrangement)) {
			throw new BizException("流程实体ArrangementEntity定义的trade非法，也可能是和joint的tradeid不一致，请检查", ResponseCode._402);
		}
		
		// 合法性检查
		if (!this.checkArrangementValid(arrangement)) {
			throw new BizException("流程实体ArrangementEntity定义非法，请检查", ResponseCode._402);
		}
		
		// 若没有填写uid，系统将为这个joint任务生成一个全系统唯一的
		if (StringUtils.isEmpty(arrangement.getUid())) {
			arrangement.setUid(java.util.UUID.randomUUID().toString());
		}

		// 插入记录
		this.insert(arrangement);
	}

	@Deprecated
	@Override
	public void updateArrangement(ArrangementEntity arrangement) throws BizException {
		if (arrangement == null) {
			throw new BizException("错误的流程实体ArrangementEntity参数，请检查", ResponseCode._402);
		}

		// 更新记录
		this.update(arrangement);
	}

	@Override
	public void deleteArrangement(String arrangementuid) throws BizException {
		if (StringUtils.isEmpty(arrangementuid)) {
			throw new BizException("错误的流程编号arrangementuid参数，请检查", ResponseCode._402);
		}

		this.delete(arrangementuid);
	}
	
	/**
	 * 一个arrangement中，包含了若干个joint。arrangement中还有一个关键的tradeid。检查的原则是：
	 * 这些joint必须属于都属于这个tradeid，并且关联的scope范围必须和arrangement设置的范围一致
	 * @param arrangement 
	 * @return
	 */
	private boolean checkArrangementTradeid(ArrangementEntity arrangement) throws BizException {
		/*
		 * 检查过程是：
		 * 1、按照arrangement中的joint信息进行循环，每循环一次，做一下操作：
		 * 2、查询A_JOINTTRADEMAPPING数据表，查询条件是 SCOPE = arrangement.tradescope AND TRADEID = arrangement.tradeid AND JOINT=joint.uid
		 * 3、如果查询成功，说明trade信息是正确的，如果查询不成功，说明trade不正确
		 * */
		//1、=======================
		Set<ArrangementJointMappingEntity> jointMappings = arrangement.getJointmapping();
		//如果这个流程模板没有设置任何的任务模板，那么就不需要再继续检查了
		if(jointMappings == null || jointMappings.isEmpty() ) {
			return true;
		}
		
		//2、=======================
		for (ArrangementJointMappingEntity jointMapping : jointMappings) {
			JointEntity joint = jointMapping.getJoint();
			String scope = arrangement.getTradeScope();
			String tradeid = arrangement.getTradeid();
			
			JointTradeMappingEntity jointTradeMapping = null;
			try {
				jointTradeMapping = this.jointTradeMappingDAO.queryJointTradeByTradeid(scope, tradeid, joint.getUid());
			} catch (BizException e) {
				ArrangementDAOImpl.LOGGER.error(e.getMessage(), e);
				throw e;
			}
			
			//如果没有查询到相关数据，说明trede信息指定错误了
			if(jointTradeMapping == null) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 检查一个arrangement基本信息，检查arrangement非空必要属性是否全部都已经配置完毕<br>
	 * 1、这个方法本身只做必填信息的格式、值的验证，并不会进行流程数据流正误的验证。后者的验证在rule模块中实现<br>
	 * 2、传入的arrangement对象，处了包括必须填写的属性信息外，jointmapping、
	 * childArrangements这样的集合信息也必须被传入。<br>
	 * 3、arrangement的uid信息没有必要指定，如果未指定，系统会自己生成一个全系统唯一的uid信息
	 * 
	 * @param arrangement
	 * @return 若ArrangementEntity格式合法则返回True， 否则返回False
	 * @throws BizException
	 */
	private boolean checkArrangementValid(ArrangementEntity arrangement) throws BizException {
		if (arrangement == null) {
			return false;
		}

		/**
		 * 根据业务一个trade行业只允许有一个scope=industry的默认流程
		 */
		boolean ret = true;
		ret = ret && !StringUtils.isEmpty(arrangement.getCreator());
		ret = ret && !StringUtils.isEmpty(arrangement.getCreatorScope());
		ret = ret && !StringUtils.isEmpty(arrangement.getDisplayName());
		ret = ret && !StringUtils.isEmpty(arrangement.getTradeid());
		ret = ret && !StringUtils.isEmpty(arrangement.getFlows());
		ret = ret && !StringUtils.isEmpty(arrangement.getTradeScope());
		
		return ret;
	}

	@Override
	public ArrangementEntity getArrangementWithSet(String arrangementuid) throws BizException {
		if (StringUtils.isEmpty(arrangementuid)) {
			throw new BizException("错误的流程编号arrangementuid参数，请检查", ResponseCode._402);
		}

		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("arrangementuid", arrangementuid);

		// 获取记录
		List<ArrangementEntity> result = null;
		result = this.queryByHqlFile("IArrangementDAO.getArrangementWithSet", conditionMap);

		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
}

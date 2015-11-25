package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.orm.ArrangementSelfMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

@Component("arrangementSelfMappingDAOImpl")
public class ArrangementSelfMappingDAOImpl extends AbstractRelationalDBDAO<ArrangementSelfMappingEntity> implements IArrangementSelfMappingDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO#getEntityClass
	 * ()
	 */
	@Override
	protected Class<ArrangementSelfMappingEntity> getEntityClass() {
		return ArrangementSelfMappingEntity.class;
	}

	@Override
	public List<ArrangementSelfMappingEntity> getArrangementSelfMappingSet(String arrangementuid) throws BizException {
		if (StringUtils.isEmpty(arrangementuid)) {
			throw new BizException("错误的流程编号arrangementuid参数，请检查", ResponseCode._402);
		}

		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("arrangementuid", arrangementuid);

		// 获取记录
		List<ArrangementSelfMappingEntity> result = null;
		result = this.queryByHqlFile("IArrangementSelfMappingDAO.getArrangementSelfMappingSet", conditionMap);
		
		return result;
	}

	@Override
	public void bindArrangementChildArrangements(String arrangementuid, Set<ArrangementSelfMappingEntity> childArrangements) throws BizException {
		if (childArrangements == null) {
			throw new BizException("错误的空Set<ArrangementSelfMappingEntity>参数，请检查", ResponseCode._402);
		}
		for (ArrangementSelfMappingEntity newArrangementSelfMappingEntity : childArrangements) {
			// 插入新节点
			// 合法性检查
			if (!checkArrangementSelfMappingValid(newArrangementSelfMappingEntity)) {
				throw new BizException("子流程映射ArrangementSelfMappingEntity定义非法，请检查", ResponseCode._402);
			}
			this.insert(newArrangementSelfMappingEntity);
		}
	}

	@Override
	public void releaseAllArrangementChildArrangements(String arrangementuid) throws BizException {
		if (StringUtils.isEmpty(arrangementuid)) {
			throw new BizException("错误的流程编号arrangementuid参数，请检查", ResponseCode._402);
		}

		List<ArrangementSelfMappingEntity> childArrangement = getArrangementSelfMappingSet(arrangementuid);
		
		if (childArrangement == null) {
			return;
		}
		// 删除记录
		for (ArrangementSelfMappingEntity childentry : childArrangement) {
			this.delete(childentry.getUid());
		}
	}
	
	/**
	 * 检查一个joint信息，检查joint非空必要属性是否全部都已经配置完毕<br>
	 * 对应的inputparams集合、outputparams集合、trade集合信息都必须设置<br>
	 * 其他参数根据业务的实际情况来
	 * @param childmapping
	 * @return 若ArrangementSelfMappingEntity格式合法则返回True， 否则返回False
	 * @throws BizException
	 */
	private boolean checkArrangementSelfMappingValid(ArrangementSelfMappingEntity childmapping) throws BizException {
		if (childmapping == null) {
			return false;
		}
		// 若没有填写uid，系统将为这个joint任务生成一个全系统唯一的
		if (StringUtils.isEmpty(childmapping.getUid())) {
			childmapping.setUid(java.util.UUID.randomUUID().toString());
		}

		boolean ret = true;
		ret &= (childmapping.getArrangement() != null);
		ret &= (childmapping.getParentArrangement() != null);
		return ret;
	}
}
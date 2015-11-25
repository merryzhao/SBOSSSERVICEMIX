package com.ai.sboss.arrangement.engine.dao.relationdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO;
import com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * @author yinwenjie
 *
 */
@Component("arrangementJointMappingDAOImpl")
public class ArrangementJointMappingDAOImpl extends AbstractRelationalDBDAO<ArrangementJointMappingEntity> implements IArrangementJointMappingDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ai.sboss.arrangement.engine.dao.AbstractRelationalDBDAO#getEntityClass
	 * ()
	 */
	@Override
	protected Class<ArrangementJointMappingEntity> getEntityClass() {
		return ArrangementJointMappingEntity.class;
	}

	@Override
	public List<ArrangementJointMappingEntity> getArrangementJointmappingSet(String arrangementuid) throws BizException {
		if (StringUtils.isEmpty(arrangementuid)) {
			throw new BizException("错误的流程编号arrangementuid参数，请检查", ResponseCode._402);
		}

		// 组装查询条件
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("arrangementuid", arrangementuid);

		// 获取记录
		List<ArrangementJointMappingEntity> result = null;
		result = this.queryByHqlFile("IArrangementJointMappingDAO.getArrangementJointmappingSet", conditionMap);
		return result;
	}

	@Override
	public void bindArrangementJointmapping(String arrangementuid, Set<ArrangementJointMappingEntity> jointmapping) throws BizException {
		if (jointmapping == null) {
			throw new BizException("错误的空Set<ArrangementJointMappingEntity>参数，请检查", ResponseCode._402);
		}
		
		for (ArrangementJointMappingEntity newArrangementJointMappingEntity : jointmapping) {
			// 插入新节点
			// 合法性检查
			if (!checkArrangementJointMappingValid(newArrangementJointMappingEntity)) {
				throw new BizException("流程任务映射ArrangementJointMappingEntity定义非法，请检查", ResponseCode._402);
			}
			this.insert(newArrangementJointMappingEntity);
		}
	}

	@Override
	public void releaseAllArrangementJointmapping(String arrangementuid) throws BizException {
		if (StringUtils.isEmpty(arrangementuid)) {
			throw new BizException("错误的流程编号arrangementuid参数，请检查", ResponseCode._402);
		}

		List<ArrangementJointMappingEntity> arrangementJointMapping = getArrangementJointmappingSet(arrangementuid);
		if (arrangementJointMapping == null) {
			return;
		}
		// 删除记录
		for (ArrangementJointMappingEntity jointMapping : arrangementJointMapping) {
			this.delete(jointMapping.getUid());
		}
	}

	/**
	 * 检查一个ArrangementJointMappingEntity信息，
	 * 检查ArrangementJointMappingEntity非空必要属性是否全部都已经配置完毕<br>
	 * 
	 * @param arrangementjointmapping
	 * @return 若ArrangementJointMappingEntity格式合法则返回True， 否则返回False
	 * @throws BizException
	 */
	private boolean checkArrangementJointMappingValid(ArrangementJointMappingEntity arrangementjointmapping) throws BizException {
		if (arrangementjointmapping == null) {
			return false;
		}
		// 若没有填写uid，系统将为这个joint任务生成一个全系统唯一的
		if (StringUtils.isEmpty(arrangementjointmapping.getUid())) {
			arrangementjointmapping.setUid(java.util.UUID.randomUUID().toString());
		}

		boolean ret = true;
		ret &= (arrangementjointmapping.getJoint() != null);
		ret &= (arrangementjointmapping.getParentArrangement() != null);
		return ret;
	}

}

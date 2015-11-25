package com.ai.sboss.arrangement.engine.local;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.IInstanceCommand;
import com.ai.sboss.arrangement.engine.dao.ArrangementDAOAbstractFactory;
import com.ai.sboss.arrangement.engine.dao.InstanceDAOService;
import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceFlowEntity;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.utils.JSONUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author yinwenjie
 */
@Component("_processorEngine_instanceCommand")
public class InstanceCommandLocalImpl implements IInstanceCommand {
	
	@Autowired
	private ArrangementDAOAbstractFactory arrangementDAOAbstractFactory;
	
	private ArrangementInstanceEntity arrangementInstance;
	
	private List<JointInstanceFlowEntity> jointInstanceFlowEntities;

	@Override
	public void init() throws BizException {

	}

	@Override
	public void init(ArrangementInstanceEntity arrangementInstance,
			List<JointInstanceFlowEntity> jointInstanceFlowEntities) throws BizException {
		this.arrangementInstance = arrangementInstance;
		this.jointInstanceFlowEntities = jointInstanceFlowEntities;
		this.init();
	}

	@Override
	public void execute() throws BizException {
		// 创建实例
		// TODO: 暂时未考虑复合流程 不应该在这里创建实例，应该在确定任务执行顺序之后
		JSONObject instanceDataJSON = JSONUtils.toJSONObject(this.arrangementInstance,
									new String[] { "parentInstance", 
											"childArrangementInstances", 
											"outputParamInstance", 
											"inputParamInstances", 
											"arrangementInstance", 
											"jointmapping", 
											"childArrangements", 
											"arrangementInstances", 
											});
		InstanceDAOService instanceDAOService = this.arrangementDAOAbstractFactory.getInstanceDAOService();
		String arrangementInstanceID = instanceDAOService.createArrangementInstance(instanceDataJSON);
		if (StringUtils.isEmpty(arrangementInstanceID)) {
			
		}
		// 创建JointInstanceFlowEntity
		JSONArray createdjoinInstanceFlowArray = new JSONArray();
		for (JointInstanceFlowEntity joinInstanceFlow : jointInstanceFlowEntities) {
			JSONObject jointInstanceFlowJSON = JSONUtils.toJSONObject(joinInstanceFlow, 
												new String[] {"arrangementInstance", 
																"outputParamInstance", 
																"inputParamInstances", 
																"outputParams", 
																"inputParams", 
																"trades", 
																"jointinstances", 
																"parentInstance", 
																"jointInstances", 
																"childArrangementInstances"
												});
			//将创建的JointInstanceFlow记录
			createdjoinInstanceFlowArray.add(instanceDAOService.createJointInstanceFlow(jointInstanceFlowJSON));
		}
		String firstJointInstanceFlowId = createdjoinInstanceFlowArray.getJSONObject(0).getString("uid");
		String firstJointInstanceId = jointInstanceFlowEntities.get(0).getJointInstance().getUid();
		instanceDAOService.updateJointFlowStatuByFlowId(firstJointInstanceFlowId, "executing");
		instanceDAOService.updateJointInstanceStatu(firstJointInstanceId, "executing");
	}

	@Override
	public void undo() throws BizException {
		//TODO:这里需要对创建的实例做标记
		//TODO:这里需要稍后对于流程实例做日志
	}

}

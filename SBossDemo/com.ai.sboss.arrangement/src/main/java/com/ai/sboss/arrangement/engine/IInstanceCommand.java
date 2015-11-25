package com.ai.sboss.arrangement.engine;

import java.util.List;

import com.ai.sboss.arrangement.entity.orm.ArrangementInstanceEntity;
import com.ai.sboss.arrangement.entity.orm.JointInstanceFlowEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * @author yinwenjie
 */
public interface IInstanceCommand extends ICommand {
	/**
	 * 初始化实例命令
	 * @param arrangementInstance 流程实例的信息 
	 * @param jointInstanceFlowEntities 顺序流程实例列表信息 
	 */
	public void init(ArrangementInstanceEntity arrangementInstance , List<JointInstanceFlowEntity> jointInstanceFlowEntities) throws BizException;
}
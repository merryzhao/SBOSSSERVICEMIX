package com.ai.sboss.arrangement.engine;

import com.ai.sboss.arrangement.exception.BizException;

import net.sf.json.JSONObject;

/**
 * “实例启动业务”，命令控制器需要实现的接口<br>
 * 在整个编排系统中，“实例启动业务”只可能有一个控制器的具体实现。
 * 这里安排IStartupControlService接口的原因，完全是为了准守spring中定义与实现分离的Ioc容器规范
 * @author yinwenjie
 */
public interface IStartupControlService {
	/**
	 * 依据json格式中，指定的业务流程模板，创建/启动一个业务流程实例。<br>
	 * 并且在启动成功后，想调用者返回相关的处理结果（以流程实例对象的方式）
	 * @param arrangementInstanceJSON 符合业务描述规范的启动信息描述
	 * @return 返回ArrangementInstanceEntity
	 * TODO: 这里的返回值待商榷
	 */
	public JSONObject startArrangementInstance(JSONObject arrangementInstanceJSON) throws BizException;
}

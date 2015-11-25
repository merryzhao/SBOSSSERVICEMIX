package com.ai.sboss.arrangement.engine;

/**
 * “终止业务”，命令控制器需要实现的接口<br>
 * 在整个编排系统中，“终止业务”只可能有一个控制器。
 * 这里安排ITerminateControlService接口的原因，完全是为了准守spring中定义与实现分离的Ioc容器规范
 * @author yinwenjie
 *
 */
public interface ITerminateControlService {

}

package com.ai.sboss.arrangement.engine;

/**
 *  “实例逆向（回退）流转业务”，命令控制器需要实现的接口<br>
 * 在整个编排系统中，“实例逆向（回退）流转业务”只可能有一个控制器的具体实现。
 * 这里安排IFallbackControlService接口的原因，完全是为了准守spring中定义与实现分离的Ioc容器规范
 * @author yinwenjie
 *
 */
public interface IFallbackControlService {

}

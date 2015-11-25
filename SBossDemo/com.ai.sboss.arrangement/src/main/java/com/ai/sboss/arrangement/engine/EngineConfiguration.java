/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package com.ai.sboss.arrangement.engine;


/**
 * 配置接口，为AbstractEngineFactory工厂定义了如何进行<br>
 * IStartupControlService、IStreamingControlService、ITerminateControlService、IFallbackControlService<br>
 * 这四个engine服务接口的生成。为了保证实现的透明性，具体的流转引擎的实现EngineConfiguration的描述
 * @author Chaos
 * @author yinwenjie
 */
public interface EngineConfiguration {
	/**
	 * @return the fallbackControlService
	 */
	public IFallbackControlService getFallbackControlService();

	/**
	 * @return the startupControlService
	 */
	public IStartupControlService getStartupControlService();

	/**
	 * @return the terminateControlService
	 */
	public ITerminateControlService getTerminateControlService();

	/**
	 * @return the streamingControlService
	 */
	public IStreamingControlService getStreamingControlService();
}
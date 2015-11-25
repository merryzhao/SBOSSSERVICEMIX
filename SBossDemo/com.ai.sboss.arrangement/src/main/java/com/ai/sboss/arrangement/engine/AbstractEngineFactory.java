package com.ai.sboss.arrangement.engine;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 流程引擎抽象工厂类
 */
public abstract class AbstractEngineFactory {
	
	@Autowired
	private EngineConfiguration configuration;
	
	/**
	 * @return the configuration
	 */
	public EngineConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(EngineConfiguration configuration) {
		this.configuration = configuration;
	}
	
	public abstract IStartupControlService getStartupControlService();
	
	public abstract IStreamingControlService getStreamingControlService();
	
	public abstract ITerminateControlService getTerminateControlService();
	
	public abstract IFallbackControlService getFallbackControlService();
}
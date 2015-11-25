package com.ai.sboss.arrangement.engine.local;

import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.AbstractEngineFactory;
import com.ai.sboss.arrangement.engine.EngineConfiguration;
import com.ai.sboss.arrangement.engine.IFallbackControlService;
import com.ai.sboss.arrangement.engine.IStartupControlService;
import com.ai.sboss.arrangement.engine.IStreamingControlService;
import com.ai.sboss.arrangement.engine.ITerminateControlService;

/**
 * @author yinwenjie
 *
 */
@Component("_processorEngine_localEngineFactoryBean")
public class LocalEngineFactoryBean extends AbstractEngineFactory {
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.AbstractEngineFactory#getStartupControlService()
	 */
	@Override
	public IStartupControlService getStartupControlService() {
		EngineConfiguration engineConfiguration = this.getConfiguration();
		return engineConfiguration.getStartupControlService();
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.AbstractEngineFactory#getStreamingControlService()
	 */
	@Override
	public IStreamingControlService getStreamingControlService() {
		EngineConfiguration engineConfiguration = this.getConfiguration();
		return engineConfiguration.getStreamingControlService();
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.AbstractEngineFactory#getTerminateControlService()
	 */
	@Override
	public ITerminateControlService getTerminateControlService() {
		EngineConfiguration engineConfiguration = this.getConfiguration();
		return engineConfiguration.getTerminateControlService();
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.AbstractEngineFactory#getFallbackControlService()
	 */
	@Override
	public IFallbackControlService getFallbackControlService() {
		EngineConfiguration engineConfiguration = this.getConfiguration();
		return engineConfiguration.getFallbackControlService();
	}

}

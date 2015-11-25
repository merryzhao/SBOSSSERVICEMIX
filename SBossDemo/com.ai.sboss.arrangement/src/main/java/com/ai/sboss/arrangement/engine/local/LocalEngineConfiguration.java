package com.ai.sboss.arrangement.engine.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.engine.EngineConfiguration;
import com.ai.sboss.arrangement.engine.IFallbackControlService;
import com.ai.sboss.arrangement.engine.IStartupControlService;
import com.ai.sboss.arrangement.engine.IStreamingControlService;
import com.ai.sboss.arrangement.engine.ITerminateControlService;

/**
 * @author yinwenjie
 */
@Component("_processorEngine_localEngineConfiguration")
public class LocalEngineConfiguration implements EngineConfiguration {
	
	@Autowired
	private IFallbackControlService fallbackControlService;
	
	@Autowired
	private IStartupControlService startupControlService;
	
	@Autowired
	private ITerminateControlService terminateControlService;
	
	@Autowired
	private IStreamingControlService streamingControlService;
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineConfiguration#getFallbackControlService()
	 */
	@Override
	public IFallbackControlService getFallbackControlService() {
		return this.fallbackControlService;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineConfiguration#getStartupControlService()
	 */
	@Override
	public IStartupControlService getStartupControlService() {
		return this.startupControlService;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineConfiguration#getTerminateControlService()
	 */
	@Override
	public ITerminateControlService getTerminateControlService() {
		return this.terminateControlService;
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineConfiguration#getStreamingControlService()
	 */
	@Override
	public IStreamingControlService getStreamingControlService() {
		return this.streamingControlService;
	}
}
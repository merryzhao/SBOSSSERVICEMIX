package com.ai.sboss.arrangement.event.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.event.ArrangementBeginListener;
import com.ai.sboss.arrangement.event.BeginEvent;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * 模拟一个begin事件的监听者
 * @author yinwenjie
 */
@Component("someBeginEventListener")
public class SomeBeginEventListener implements ArrangementBeginListener {

	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(SomeBeginEventListener.class);
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.event.ArrangementBeginListener#onArrangementBegin(com.ai.sboss.arrangement.event.BeginEvent)
	 */
	@Override
	public void onArrangementBegin(BeginEvent event) throws BizException {
		SomeBeginEventListener.LOGGER.info("SomeBeginEventListener do ===================");
	}
}

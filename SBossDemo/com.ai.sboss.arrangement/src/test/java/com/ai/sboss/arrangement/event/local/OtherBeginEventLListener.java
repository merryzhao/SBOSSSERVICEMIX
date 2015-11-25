package com.ai.sboss.arrangement.event.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ai.sboss.arrangement.event.ArrangementBeginListener;
import com.ai.sboss.arrangement.event.BeginEvent;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * 模拟第二个开始事件监听者
 * @author yinwenjie
 *
 */
@Component("otherBeginEventLListener")
public class OtherBeginEventLListener implements ArrangementBeginListener {

	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(OtherBeginEventLListener.class);
	
	@Override
	public void onArrangementBegin(BeginEvent event) throws BizException {
		OtherBeginEventLListener.LOGGER.info("OtherBeginEventLListener do ===================");
	}
}
package com.ai.sboss.arrangement.translation;

/**
 * 流程、任务、实例的各种描述状态（entity、json、xml）是需要进行互相转换的。<br>
 * 这个转换工作通过TranslationAbstractFactory工程所管理的“翻译服务”进行处理。
 * TODO 从目前的情况来看，翻译服务的粒度不需要这么细，看在以后的版本中是否进行合并。
 * @author yinwenjie
 */
public abstract class TranslationAbstractFactory {
	/**
	 * @return
	 */
	public abstract ArrangementTranslationService getArrangementTranslationService();

	/**
	 * @return
	 */
	public abstract JointTranslationService getJointTranslationService();
	
	/**
	 * @return
	 */
	public abstract ArrangementInstanceTranslationService getArrangementInstanceTranslationService();
}

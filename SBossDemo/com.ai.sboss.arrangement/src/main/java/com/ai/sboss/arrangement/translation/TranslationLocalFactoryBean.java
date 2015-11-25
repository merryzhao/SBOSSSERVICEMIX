package com.ai.sboss.arrangement.translation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 这个工厂为上层服务提供了 实体-XML 、 XML-实体 的解析服务。
 * @author yinwenjie
 * TODO 按照业务层面提供的要求，这个工厂的实现最好采用的是“享元模式”作为基础。
 * 并且有一个“blockqueen”限制整个享元的最大长度。但是由于项目时间的限制，这个升级在后续进行
 */
@Component("translationLocalFactoryBean")
public class TranslationLocalFactoryBean extends TranslationAbstractFactory {
	/**
	 * 
	 */
	@Autowired
	private ArrangementTranslationService arrangementTranslationService;
	
	/**
	 * 
	 */
	@Autowired
	private JointTranslationService jointTranslationService;
	
	/**
	 * 
	 */
	@Autowired
	private ArrangementInstanceTranslationService arrangementInstanceTranslationService;

	/**
	 * @return the arrangementTranslationService
	 */
	public ArrangementTranslationService getArrangementTranslationService() {
		return arrangementTranslationService;
	}

	/**
	 * @param arrangementTranslationService the arrangementTranslationService to set
	 */
	public void setArrangementTranslationService(
			ArrangementTranslationService arrangementTranslationService) {
		this.arrangementTranslationService = arrangementTranslationService;
	}

	/**
	 * @return the jointTranslationService
	 */
	public JointTranslationService getJointTranslationService() {
		return jointTranslationService;
	}

	/**
	 * @param jointTranslationService the jointTranslationService to set
	 */
	public void setJointTranslationService(
			JointTranslationService jointTranslationService) {
		this.jointTranslationService = jointTranslationService;
	}

	/**
	 * @return the arrangementInstanceTranslationService
	 */
	public ArrangementInstanceTranslationService getArrangementInstanceTranslationService() {
		return arrangementInstanceTranslationService;
	}

	/**
	 * @param arrangementInstanceTranslationService the arrangementInstanceTranslationService to set
	 */
	public void setArrangementInstanceTranslationService(
			ArrangementInstanceTranslationService arrangementInstanceTranslationService) {
		this.arrangementInstanceTranslationService = arrangementInstanceTranslationService;
	}
}

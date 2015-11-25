/**
 * 
 */
package com.ai.sboss.datanalysis.core;

import java.util.HashMap;

/**
 * @author idot
 *
 */
public interface DataAnalysisCoreInterface {
	
	/**
	 * 数据分析的核心之一：内容解析
	 * 对传入的内容，包括文本、图片、音频和视频进行解析，最终输出文本形式的关键字列表
	 * 由于平台用户可能收藏相同的内容，因此对销售品以及待分析内容的解析结果需要考虑保存在数据库，如此当内容相同时，解析无需重复进行
	 */
	public HashMap<String, Integer> dataAnalysisCore(Object object);
	
	/**
	 * 内容解析过程会产生诸多不同的中间结果，考虑保存中间结果的必要性与技术实现
	 * @param object
	 */
	public void saveIntermediateResult(Object object);
}

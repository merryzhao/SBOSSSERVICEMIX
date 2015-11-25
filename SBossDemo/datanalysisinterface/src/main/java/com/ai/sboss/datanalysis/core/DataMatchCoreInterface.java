/**
 * 
 */
package com.ai.sboss.datanalysis.core;

/**
 * @author idot
 *
 */
public interface DataMatchCoreInterface {

	/**
	 * 内容解析完成后，得到关键字列表
	 * 需要将对收藏内容的解析结果与对销售品文本内容的解析结果进行匹配
	 * 最终结果返回按照相似度由大到小排列的销售品列表
	 */
	public float matchContentOffering(Object contentAnalysisResult, Object offeringAnalysisResult);
}

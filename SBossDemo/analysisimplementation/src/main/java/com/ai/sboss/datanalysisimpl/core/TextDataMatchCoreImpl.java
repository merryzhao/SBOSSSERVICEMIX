/**
 * 
 */
package com.ai.sboss.datanalysisimpl.core;

import java.util.HashMap;
import java.util.Map;

import com.ai.sboss.datanalysis.core.DataMatchCoreInterface;

/**
 * @author idot
 * jaccard系数：A和B交集/A和B并集 
 * 这里对jeccard系数做了适当变形，考虑了文本中词频对相似度的影响：交集词频总量/(内容词频总量+offering词频总量)
 *
 */
public class TextDataMatchCoreImpl implements DataMatchCoreInterface{
	// TODO Auto-generated method stub
	@SuppressWarnings("unchecked")
	public float matchContentOffering(Object contentAnalysisResult,
			Object offeringAnalysisResult) {
		HashMap<String, Integer> contentData = new HashMap<String, Integer>();
		HashMap<String, Integer> offeringData = new HashMap<String, Integer>();
		if(contentAnalysisResult instanceof HashMap && offeringAnalysisResult instanceof HashMap){
			contentData = (HashMap<String, Integer>) contentAnalysisResult;
			offeringData = (HashMap<String, Integer>) offeringAnalysisResult;
/*			System.out.println("contentData-->"+contentData);
			System.out.println("offeringData-->"+offeringData);*/
		}
		int valueContent = 0;//内容词频总量
		int valueOffering = 0;//offering词频总量
		int interSectionValue = 0;//交集词频总量
		//计算文本集合交集，并统计词频
		for(Map.Entry<String, Integer> entryContent:contentData.entrySet()){
			valueContent = valueContent + entryContent.getValue();  
			for(Map.Entry<String, Integer> entryOffering:offeringData.entrySet()){
				valueOffering = valueOffering + entryOffering.getValue();
				
				if(entryContent.getKey().equals(entryOffering.getKey())){
					interSectionValue = interSectionValue + entryContent.getValue() + entryOffering.getValue();
				}
			}
		}
		
		//如果传入的content_id在文件服务器没有取到内容，即contentsJson（见TextRelOfferingRecommendorImpl.java中的方法contentRelOfferingAnalysis()参数为空
		if(contentData.size() != 0){
			valueOffering = valueOffering/contentData.size();
			return (float)interSectionValue/(valueContent + valueOffering);			
		}
		else{
			return (float) 0.0;
		}
	}

}

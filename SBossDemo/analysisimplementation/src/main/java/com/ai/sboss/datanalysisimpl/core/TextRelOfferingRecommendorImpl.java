package com.ai.sboss.datanalysisimpl.core;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ai.sboss.datanalysis.core.DataAnalysisCoreInterface;
import com.ai.sboss.datanalysis.core.DataMatchCoreInterface;
import com.ai.sboss.datanalysis.core.OfferingRecommendorInterface;
import com.ai.sboss.datanalysis.util.MixAllTextInfoInterface;
import com.ai.sboss.datanalysisimpl.util.ConfigureParamRead;
import com.ai.sboss.datanalysisimpl.util.MixAllTextInfoImpl;

public class TextRelOfferingRecommendorImpl  extends UnicastRemoteObject implements OfferingRecommendorInterface{

	protected TextRelOfferingRecommendorImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public String contentRelOfferingAnalysis(JSONArray contentsJson)
			throws Exception, RemoteException {
		String contentAll = null;
		ArrayList<Long> contentIds = new ArrayList();
		for(int contentIdx = 0; contentIdx < contentsJson.size(); contentIdx ++){
			String contentJson = contentsJson.getString(contentIdx);
			if(!contentJson.equals("{}")){
				JSONObject content = JSONObject.fromObject(contentJson);
				Long contentId = content.getLong("contentId");
				String title = content.getString("title");
				String textContent = content.getString("content");
				String contentStr = title + " " + textContent;
				contentAll = contentAll + " " + contentStr;
				contentIds.add(contentId);
			}
		}
		contentIds.add(1002L);
		DataAnalysisCoreInterface tst = new TextDataAnalysisCoreImpl();
		MixAllTextInfoInterface textProcessor = new MixAllTextInfoImpl();
		//testContent = textProcessor.readFileByLines("content.txt");
		
		List<JSONObject> allCatalogTrees = textProcessor.getAllCatalogs("industryList.txt");
		List<JSONObject> totalOfferings = textProcessor.getAllOfferings(allCatalogTrees);
		List<JSONObject> totalOfferingInfo = textProcessor.getTotalOfferingInfo(totalOfferings);
		HashMap<Long, Float> matchRes = new HashMap<Long, Float>();
		JSONArray relOfferingList = new JSONArray();
		//提取内容文本关键信息
		HashMap<String, Integer> parseContentRes = tst.dataAnalysisCore(contentAll);
		for(int i = 0; i< totalOfferingInfo.size(); i++){
			String offerStr = totalOfferingInfo.get(i).toString();
			//根据查询的offer详情获取文本信息
			JSONObject offeringTextInfo = textProcessor.mixAllOfferingTextInfo(offerStr);


			//提取服务文本关键信息
			HashMap<String, Integer> parseOfferingRes = tst.dataAnalysisCore(textProcessor.offeringTextInfoExtract(offeringTextInfo.toString(), "offeringName") + textProcessor.offeringTextInfoExtract(offeringTextInfo.toString(), "comments")+textProcessor.offeringTextInfoExtract(offeringTextInfo.toString(), "charSpecList"));
			//服务匹配
			DataMatchCoreInterface matchSV = new TextDataMatchCoreImpl();
			float matchValue = matchSV.matchContentOffering(parseContentRes, parseOfferingRes);
			
			JSONObject offerJson = JSONObject.fromObject(offerStr);
			if(matchValue > 0){
				matchRes.put(offerJson.getJSONObject("data").getLong("offerId"), matchValue);
				offerJson.put("matchVaule", matchValue);
				//relOfferingList.add(offerJson.toString());
			}
		}
		
		//按匹配分值从大到小获取指定数量的offering数据
		String matchNumber = ConfigureParamRead.getValue("matchNumber");
		HashMap<Long, Float> sortMaptchRes = textProcessor.sortMap(matchRes);
		int idx = 0;
		for(Map.Entry<Long, Float> entry: sortMaptchRes.entrySet()){
			if(idx < Integer.parseInt(matchNumber)){
				for(int i = 0; i< totalOfferingInfo.size(); i++){
					String offerStr = totalOfferingInfo.get(i).toString();
					JSONObject offerJson = JSONObject.fromObject(offerStr);				
					if(offerJson.getJSONObject("data").getLong("offerId") == entry.getKey()){
						offerJson.put("matchValue", entry.getValue());
						relOfferingList.add(offerJson.toString());
					}				
				}		
			}
			idx ++;
		}
		JSONObject contentRelOffering = new JSONObject();
		contentRelOffering.put("contentId", contentIds);
		contentRelOffering.put("offeringList", relOfferingList);
		System.out.println(contentRelOffering.toString());
		System.out.println("done!!");
		System.out.println("done!!");
		System.out.println("done!!");
		return contentRelOffering.toString();
	}
}

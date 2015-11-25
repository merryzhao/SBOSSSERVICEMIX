/**
 * 
 */
package com.ai.sboss.datanalysisimpl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ai.sboss.datanalysis.support.FileOperationInterface;
import com.ai.sboss.datanalysis.util.CrmHttpRequestInterface;
import com.ai.sboss.datanalysis.util.MixAllTextInfoInterface;
import com.ai.sboss.datanalysisimpl.support.FileOperationImpl;


/**
 * @author idot
 *
 */
public class MixAllTextInfoImpl implements MixAllTextInfoInterface{


	public List<String> readFileByLines(String fileName) {
		// TODO Auto-generated method stub
		List<String> fileLines = new ArrayList<String>();
		File file = new File(fileName);
        BufferedReader reader = null;
        try {
            //System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	fileLines.add(tempString);
                //System.out.println("line " + line + ": " + tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return fileLines;
	}
	
	/**
	 * 判断一个词是否为一个停用词
	 */
	public boolean isStopword(List<String> stopWordList, String keyWord){
		if(stopWordList.contains(keyWord)){
			return true;
		}else{
			return false;
		}
	}

	public String offeringTextInfoExtract(String offerTextJson, String key) {
		// TODO Auto-generated method stub
		JSONObject offerJson = JSONObject.fromObject(offerTextJson);
		String commentText = null;
		String specListText = null;
		if("offeringId".equals(key)||"offeringCode".equals(key)||"description".equals(key)||"offeringName".equals(key)||"catalogId".equals(key)){
			if("offeringName".equals(key)){
				//System.out.println("offeringName---->"+offerJson.getString(key));
			}
			
			return offerJson.getString(key);
		}else if("comments".equals(key)){
			JSONArray commentArray = JSONArray.fromObject(offerJson.getString(key));
			System.out.println("commentArray--->" + commentArray.toString());
			for(int i = 0; i < commentArray.size(); i++){
				
				JSONObject commentJsonObject = JSONObject.fromObject(commentArray.getString(i));
				commentText = commentText + " " + (i + 1) + ":" +commentJsonObject.getString("contextComment");
			}
			return commentText;
		}else if("charSpecList".equals(key)){
			JSONArray charSpecArray = JSONArray.fromObject(offerJson.getString(key));
			for(int i = 0; i < charSpecArray.size(); i++){
				JSONObject charSpecJsonObject = JSONObject.fromObject(charSpecArray.getString(i));
				specListText = specListText + " " + (i + 1) + ":" + charSpecJsonObject.getString("charSpecName") + "\t" + charSpecJsonObject.getString("charSpecVal");
			}
			//System.out.println(specListText);
			return specListText;
		}else{
			return null;
		}
	}

	public Long findParentCatalogNode(String catalogTree, Long catalogId) {
		JSONArray catalogArray = JSONArray.fromObject(catalogTree);
		Long parentNode = null;
		for(int i = 0; i< catalogArray.size(); i++){
			JSONObject tempNode = JSONObject.fromObject(catalogArray.get(i));
			if(tempNode.getLong("catalogId") == catalogId){
				if(tempNode.getString("parentCatalog") != "null"){
					parentNode = JSONObject.fromObject(tempNode.getString("parentCatalog")).getLong("catalogId");
					break;
				}
			}
		}
		return parentNode;
	}
	
	public boolean isCatalogLeaf(String catalogTree, Long catalogId) {
		JSONArray catalogArray = JSONArray.fromObject(catalogTree);
		for(int i = 0; i< catalogArray.size(); i++){
			if(findParentCatalogNode(catalogTree, JSONObject.fromObject(catalogArray.get(i)).getLong("catalogId")) == catalogId){
				return false;
			}
		}
		return true;
	}

	/**
	 * 从fake文件中读取所有的顶层catalog编码，循环遍历该列表，获得所有的catalog(包含顶层、中间层以及叶子节点)
	 * 返回值为所有的catalog树，每棵树作为JSONObject保存起来
	 */
	public List<JSONObject> getAllCatalogs(String fileName) {
		// TODO Auto-generated method stub
		FileOperationInterface fileOperationInterface = new FileOperationImpl();
		CrmHttpRequestInterface crmHttpRequestInterface = new CrmHttpRequestImpl();
		List<JSONObject> resultJsonList = new ArrayList<JSONObject>();
		List<Long> catalogIdList = fileOperationInterface.getAllCatalogId(fileName);     //从指定的文件中读出所有的顶层catalog
		System.out.println("catalogIdList--->" + catalogIdList);
		/**
		 * 循环遍历所有的顶层catalog，调用CRM的服务查找出该catalog对应的目录树
		 */
		for(Long catalogId : catalogIdList){ 
			String queryCatalogTreeUrl = ConfigureParamRead.getValue("queryCatalogTreeUrl");
			//System.out.print(queryCatalogTreeUrl);
//			String queryCatalogTreeUrl = "http://10.5.1.247:5095/HubCrmServlet?servicecode=queryCatalogTree&WEB_HUB_PARAMS={\"data\":{\"catalogId\":" + 
//						 catalogId + ",\"isGetElderOrChildren\":2},\"header\":{\"content-type\":\"application/json\"}}";
			queryCatalogTreeUrl = queryCatalogTreeUrl.replace("+catalogId+", catalogId.toString());
			String urlResult = crmHttpRequestInterface.sendPostRequest(queryCatalogTreeUrl, "");
			JSONObject interJson = JSONObject.fromObject(urlResult);
			resultJsonList.add(interJson);
		}
		return resultJsonList;
	}
	
	
	/**
	 * 将getAllCatalogs方法得到的输出作为输入，获得每棵树所有的叶子节点
	 * 由于所有的销售品都是挂接到叶子节点的，因此遍历所有的叶子节点可以得到所有的销售品列表
	 */
	public List<JSONObject> getAllOfferings(List<JSONObject> allCatalogTrees) {
		// TODO Auto-generated method stub	
		
		CrmHttpRequestInterface crmHttpRequestInterface = new CrmHttpRequestImpl();
		List<JSONObject> allOfferingsList = new ArrayList<JSONObject>();
		
		for(JSONObject singleCatalogTree : allCatalogTrees){
			JSONArray tempData = singleCatalogTree.getJSONArray("data");
			for(int i = 0 ; i < tempData.size(); i++){
				JSONObject singleCatalog = tempData.getJSONObject(i);
				
				/**
				 * 因为crm部署存在问题，传过来的叶子节点isLeaf没有正确的数值，因此，这里摒弃直接取isLeaf字段判断是否是叶子节点的问题
				 * 如果后期改了crm的部署，则可以调整过来   singleCatalog.getInt("isLeaf") == 1
				 */
				Long tempId = singleCatalog.getLong("catalogId");
				//Long parentId = findParentCatalogNode(tempData.toString(), tempId);
				
//				String tempUrl = "http://10.5.1.247:5095/HubCrmServlet?servicecode=queryCatalogTree&WEB_HUB_PARAMS={\"data\":{\"catalogId\":" + 
//						tempId + ",\"isGetElderOrChildren\":2},\"header\":{\"content-type\":\"application/json\"}}";
//				String tempUrlResult = crmHttpRequestInterface.sendPostRequest(tempUrl, "");
//				JSONObject interJson = JSONObject.fromObject(tempUrlResult);
//				JSONArray interArray = interJson.getJSONArray("data");
//				
				//if(interArray.size() == 1){
				if(isCatalogLeaf(tempData.toString(), tempId)){
					Long singleCatalogId = singleCatalog.getLong("catalogId");
					//System.out.println(singleCatalogId);
					String queryProductOfferingUrl = ConfigureParamRead.getValue("queryProductOfferingUrl");
					queryProductOfferingUrl = queryProductOfferingUrl.replace("+singleCatalogId+", singleCatalogId.toString());
//					String queryProductOfferingUrl = "http://10.5.1.247:5091/HubCrmServlet?servicecode=queryProdOffering&WEB_HUB_PARAMS={\"data\":{\"condition\":{\"conditionType\":[{\"condType\":\"catalogId\",\"condValue\":[\""
//							 + singleCatalogId + "\"]}],\"pageSize\":10,\"pageNumber\":1}},\"header\":{\"content-type\":\"application/json\"}}";
					
					String urlResult = crmHttpRequestInterface.sendPostRequest(queryProductOfferingUrl, "");
					JSONObject tempOfferingsJson = JSONObject.fromObject(urlResult);
					//System.out.print("tempoffer->"+tempOfferingsJson);
					JSONObject tempOfferingData = tempOfferingsJson.getJSONObject("data");
					JSONArray tempOfferingsInfo = tempOfferingData.getJSONArray("offerings");
					for(int j = 0; j < tempOfferingsInfo.size(); j++){
						JSONObject singleOfferingInfo = new JSONObject();
						Long tempOfferingId = tempOfferingsInfo.getJSONObject(j).getLong("offerId");
						String tempOfferingCode = tempOfferingsInfo.getJSONObject(j).getString("offeringCode");
						singleOfferingInfo.put("offeringId", tempOfferingId);
						singleOfferingInfo.put("offeringCode", tempOfferingCode);
						singleOfferingInfo.put("catalogId", singleCatalogId);
						singleOfferingInfo.put("catalogTree", tempData);
						allOfferingsList.add(singleOfferingInfo);
					}
				}
			}
		}
		return allOfferingsList;
	}
	
	/**
	 * 循环遍历所有的offering列表，获得每个销售品的可用文本信息，将其保存为一个JSONObject，并且返回
	 */
	
	public JSONObject mixAllOfferingTextInfo(String offeringDetailJson) {
		// TODO Auto-generated method stub
		CrmHttpRequestInterface crmHttpRequestInterface = new CrmHttpRequestImpl();	
		JSONObject resultTextInfo = new JSONObject();
		
		JSONObject data = JSONObject.fromObject(offeringDetailJson).getJSONObject("data");
		Long offeringId = data.getLong("offerId");
		String offeringCode = data.getString("offeringCode");
		String description = data.getString("description");
		String offeringName = data.getString("offeringName");
		List<JSONObject> charSpecList = new ArrayList<JSONObject>();
		
		//获取销售品的评论文本信息
		List<JSONObject> commentsList = new ArrayList<JSONObject>();
		String queryComentUrl = ConfigureParamRead.getValue("queryComentUrl");
		queryComentUrl = queryComentUrl.replace("+offeringId+", offeringId.toString());
//		String queryComentUrl = "http://10.5.1.247:5095/HubCrmServlet?servicecode=queryCommentsForServiceDetail&WEB_HUB_PARAMS={\"data\":{\"objectId\":" + offeringId + 
//				            ",\"objectType\":\"Offering\"},\"header\":{\"Content-Type\":\"application/json\"}}";
		String commentUrlResult = crmHttpRequestInterface.sendPostRequest(queryComentUrl,"");
		System.out.println(commentUrlResult);
		
		JSONObject temp = JSONObject.fromObject(commentUrlResult);

		if(!temp.getString("data").equals("")){
			JSONArray commentData = JSONObject.fromObject(commentUrlResult).getJSONArray("data");
			for(int i = 0; i< commentData.size(); i++){
					JSONObject tempContextComment = new JSONObject();
				    JSONObject singleContextCommentJson = commentData.getJSONObject(i);
				    String singleContextComment = "";
				    if(singleContextCommentJson.getJSONObject("contextComment").containsKey("context")){
					    singleContextComment = singleContextCommentJson.getJSONObject("contextComment").getString("context");   
				    }
				    tempContextComment.put("contextComment", singleContextComment);
				    Long partyRoleId = singleContextCommentJson.getLong("partyRoleId");      //目前只有文本评论，没有其它的评论形式（标签评论等）
				    tempContextComment.put("partyRoleId",partyRoleId);
				    commentsList.add(tempContextComment);
			}
		}

		JSONArray offeringChars = data.getJSONArray("productOfferingSpecCharList");
		for(int i = 0; i < offeringChars.size(); i++){
			JSONObject singleChar = offeringChars.getJSONObject(i);
			JSONObject tempCharSpec = new JSONObject();
			String tempCharSpecName = singleChar.getJSONObject("specCharId").getString("charSpecName");
			List<String> tempCharSpecVal = new ArrayList<String>();
			String singleCharSpecVal = null;
			
			JSONArray tempSpecValList = singleChar.getJSONArray("productOfferingSpecCharValList");
			if(tempSpecValList.size() != 0 || !tempSpecValList.isEmpty()){
				for(int j = 0; j < tempSpecValList.size(); j++){
					singleCharSpecVal = tempSpecValList.getJSONObject(j).getJSONObject("specCharValId").getString("value");
					tempCharSpecVal.add(singleCharSpecVal);
				}
			}
			else{
				singleCharSpecVal = singleChar.getString("value");
				tempCharSpecVal.add(singleCharSpecVal);
			}
			tempCharSpec.put("charSpecName", tempCharSpecName);
			tempCharSpec.put("charSpecVal", tempCharSpecVal);
			charSpecList.add(tempCharSpec);
		}
		
		resultTextInfo.put("offeringId", offeringId);
		resultTextInfo.put("offeringCode", offeringCode);
		resultTextInfo.put("description", description);
		resultTextInfo.put("offeringName", offeringName);
		
		resultTextInfo.put("comments", commentsList);
		resultTextInfo.put("charSpecList", charSpecList);			
		
		return resultTextInfo;
	}

	/**
	 * 获得所有销售品的可利用文本信息构成的JSONObject，将其作为一个列表返回
	 */
	

	public List<JSONObject> getTotalOfferingInfo(List<JSONObject> totalOfferings) {
		// TODO Auto-generated method stub
		
		List<JSONObject> totalOfferingInfo = new ArrayList<JSONObject>();
		
		for(JSONObject singleOffering : totalOfferings){
			Long singleOfferingId = singleOffering.getLong("offeringId");
			//System.out.println("singleOfferId -- >" + singleOfferingId);
			String singleOfferingCode = singleOffering.getString("offeringCode");
			Long singleOfferingCatalogId = singleOffering.getLong("catalogId");
			JSONArray singleCatalogTree = singleOffering.getJSONArray("catalogTree");
			String offerDetailJson = acquireOfferingDetail(singleOfferingId, singleOfferingCode);
			JSONObject tempOfferingInfo = JSONObject.fromObject(offerDetailJson);
			//System.out.println(offerDetailJson);
			//JSONObject tempOfferingInfo = mixAllOfferingTextInfo(offerDetailJson);
			//tempOfferingInfo.put("offeringId", singleOfferingId);
			//tempOfferingInfo.put("offeringCode", singleOfferingCode);
			tempOfferingInfo.put("catalogId", singleOfferingCatalogId);
			tempOfferingInfo.put("catalogTree", singleCatalogTree);
			totalOfferingInfo.add(tempOfferingInfo);
		}
		return totalOfferingInfo;
	}
	
	/**
	 * 爬虫相关的处理需等亮哥回来确认
	 */
	public String decomposeContentInfo(Object content) {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<Long, Float> sortMap(HashMap<Long, Float> oldMap) {
		ArrayList<Map.Entry<Long, Float>> list = new ArrayList<Map.Entry<Long, Float>>(oldMap.entrySet());  
        Collections.sort(list, new Comparator<Map.Entry<Long, Float>>() {  
			public int compare(Entry<Long, Float> o1, Entry<Long, Float> o2) {
				if(o2.getValue()!=null&&o1.getValue()!=null&&o2.getValue().compareTo(o1.getValue())>0){  
		            return 1;  
		           }else{  
		            return -1;  
		           }  
			}  
        });  
        Map newMap = new LinkedHashMap();  
        for (int i = 0; i < list.size(); i++) {  
            newMap.put(list.get(i).getKey(), list.get(i).getValue());  
        }  
        return (HashMap<Long, Float>) newMap;  
	}

	public String acquireOfferingDetail(Long offeringId, String offeringCode) {
		CrmHttpRequestInterface crmHttpRequestInterface = new CrmHttpRequestImpl();	
		JSONObject resultTextInfo = new JSONObject();
		String acquireProductOfferingUrl = ConfigureParamRead.getValue("acquireProductOfferingUrl");
		acquireProductOfferingUrl = acquireProductOfferingUrl.replace("+offeringId+", offeringId.toString()).replace("+offeringCode+", "");    //offeringCode.toString()
/*		String acquireProductOfferingUrl = "http://10.5.1.247:5095/HubCrmServlet?bridgeEndpoint=true&servicecode=acquireProductoffering&WEB_HUB_PARAMS={\"data\":{\"productofferingID\":\"" + offeringId + 
			     "\",\"productofferingCode\":\"\"},\"header\":{\"Content-Type\":\"application/json\"}}";*/
//		System.out.println("acquireProductOfferingUrl--->" + acquireProductOfferingUrl);
		String urlResult = null;
		urlResult = crmHttpRequestInterface.sendPostRequest(acquireProductOfferingUrl, "");

		//System.out.println(urlResult);
		return urlResult;
	}
}

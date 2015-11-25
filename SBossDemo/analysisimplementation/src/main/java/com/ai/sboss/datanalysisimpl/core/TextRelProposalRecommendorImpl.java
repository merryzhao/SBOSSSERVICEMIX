package com.ai.sboss.datanalysisimpl.core;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ai.sboss.datanalysis.core.ProposalRecommendorInterface;
import com.ai.sboss.datanalysis.util.CrmHttpRequestInterface;
import com.ai.sboss.datanalysisimpl.util.CrmHttpRequestImpl;

public class TextRelProposalRecommendorImpl extends UnicastRemoteObject implements ProposalRecommendorInterface{

	protected TextRelProposalRecommendorImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public String contentRelProposalAnalysis(String contentRelOfferingJson) throws Exception, RemoteException{
		JSONArray relOfferings = JSONArray.fromObject(JSONObject.fromObject(contentRelOfferingJson).getString("offeringList"));
		JSONArray proposalItemList = new JSONArray();
		String parten = "###.####";  
	    
		DecimalFormat decimal = new DecimalFormat(parten);  
		    
		for(int idx = 0; idx < relOfferings.size(); idx ++){
			Long offerId = relOfferings.getJSONObject(idx).getJSONObject("data").getLong("offerId");
			Long catalogId = relOfferings.getJSONObject(idx).getLong("catalogId");
			JSONObject proposalItemTemp = new JSONObject();
			proposalItemTemp.put("elementId", offerId);
			JSONObject parentProposalItemID = new JSONObject();
			proposalItemTemp.put("parentElementId", catalogId);
			proposalItemTemp.put("parentElementType", "catalog");
			proposalItemTemp.put("isMain", 0);
			proposalItemTemp.put("selectFlagOfVirtualService", 1);

			String piStr = decimal.format(relOfferings.getJSONObject(idx).getDouble("matchValue"));  
			proposalItemTemp.put("relevant_level", piStr);
			proposalItemTemp.put("proposalItemType", "offering");
			proposalItemTemp.put("state", 1);
			proposalItemList.add(proposalItemTemp);
			proposalItemTemp.clear();
			//判断catalog类proposalItem中是否已经包含此catalog
			int flag = 0;
			for(int itemIdx = 0; itemIdx < proposalItemList.size(); itemIdx ++){
				if(proposalItemList.getJSONObject(itemIdx).getLong("elementId") == catalogId && proposalItemList.getJSONObject(itemIdx).getString("proposalItemType").equals("catalog")){
					flag = 1;
				}
			}
			//如果proposalItem中部包含此catalog，则添加
			if(0 == flag){
				proposalItemTemp.put("elementId", catalogId);
				JSONObject parentProposalID = new JSONObject();
/*				proposalItemTemp.put("parentElementType", "");
				proposalItemTemp.put("parentElementID", "");*/
				//proposalItemTemp.put("parentItemId", "");
				proposalItemTemp.put("isMain", 0);
				proposalItemTemp.put("selectFlagOfVirtualService", 1);
				//proposalItemTemp.put("relevant_level", 0);
				proposalItemTemp.put("proposalItemType", "catalog");
				proposalItemTemp.put("state", 1);
				proposalItemList.add(proposalItemTemp);
				proposalItemTemp.clear();
			}	
		}
		Double catalogRelevantVal = 0.0;
		for(int itemIdx = 0; itemIdx < proposalItemList.size(); itemIdx ++){
			if(proposalItemList.getJSONObject(itemIdx).getString("proposalItemType").equals("catalog")){
				Long catalogElementId = proposalItemList.getJSONObject(itemIdx).getLong("elementId");
				for(int offerIdx = 0; offerIdx < proposalItemList.size(); offerIdx ++){
					if(proposalItemList.getJSONObject(offerIdx).getString("proposalItemType").equals("offering") && 
							proposalItemList.getJSONObject(offerIdx).getLong("parentElementId") == catalogElementId){
						catalogRelevantVal = catalogRelevantVal + proposalItemList.getJSONObject(offerIdx).getDouble("relevant_level");
					}
				}
				JSONObject temp = proposalItemList.getJSONObject(itemIdx);
				temp.put("relevant_level", decimal.format(catalogRelevantVal));
				proposalItemList.set(itemIdx, temp);
				catalogRelevantVal = 0.0;
			}
		}
		System.out.println(proposalItemList.toString());
		JSONArray proposalRoles = new JSONArray();
		JSONObject roleTemp = new JSONObject();
		roleTemp.put("partyAssociationType", "Customer");
		roleTemp.put("partyRoleId", 100000002014L);
		roleTemp.put("state", 1);
		proposalRoles.add(roleTemp);
		JSONObject proposalValue = new JSONObject();
		proposalValue.put("proposalDesc", "panzy_test");
		proposalValue.put("state", 1);
		proposalValue.put("proposalItem", proposalItemList);
		proposalValue.put("proposalRoles", proposalRoles);
		JSONObject header = new JSONObject();
		header.put("content-type", "application/json");
		JSONObject proposalParam = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("proposalValue", proposalValue);
		proposalParam.put("data", data);
		proposalParam.put("header", header);
		
		CrmHttpRequestInterface crmHttpRequestInterface = new CrmHttpRequestImpl();
		String resStr = crmHttpRequestInterface.sendPostRequest("http://10.5.1.247:5095/HubCrmServlet?servicecode=addProposal&WEB_HUB_PARAMS=" + proposalParam.toString(), "");
		
		Long proposalId = JSONObject.fromObject(resStr).getJSONObject("data").getLong("responseResultId");
		JSONObject proposalData = new JSONObject();
		proposalData.put("proposalId", proposalId);
		JSONObject acquireParam = new JSONObject();
		acquireParam.put("data", proposalData);
		acquireParam.put("header", header);
		//http://10.5.1.247:5095/HubCrmServlet?servicecode=acquireProposalById&WEB_HUB_PARAMS={"data":{"proposalId":624},"header":{"content-type":"application/json"}}
		String proposalReturnData = crmHttpRequestInterface.sendPostRequest("http://10.5.1.247:5095/HubCrmServlet?servicecode=acquireProposalById&WEB_HUB_PARAMS=" + acquireParam, "");
		
		System.out.println(proposalReturnData);
		return proposalReturnData;
	}
}

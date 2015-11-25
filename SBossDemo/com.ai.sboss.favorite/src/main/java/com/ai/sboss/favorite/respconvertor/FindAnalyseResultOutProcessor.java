/**
 * 
 */
package com.ai.sboss.favorite.respconvertor;

import java.rmi.Naming;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;
import com.ai.sboss.datanalysis.core.OfferingRecommendorInterface;
import com.ai.sboss.datanalysis.core.ProposalRecommendorInterface;

/**
 * @author monica
 * 根据传入的content_id获取到对应内容之后，将其组装成一个jsonArray返回，进行解析
 */
public class FindAnalyseResultOutProcessor implements IBasicOutProcessor{

	protected Logger logger = Logger.getLogger(this.getClass());
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Message inMessage = exchange.getIn();
		String ret = convert2requst(inMessage.getBody(String.class));
		inMessage.setBody(ret);
	}
	
	@Override
	public String convert2requst(String exchange) throws Exception {
		
		JSONArray tempArrayObj = JSONArray.fromObject(exchange);		
		if(tempArrayObj.size() == 0 || tempArrayObj.isEmpty()){
			return RETFMT.replace("<DATA>", "[]").replace("<CODE>", "0").replace("<MSG>", StringUtils.EMPTY); 
		}
		else{
			return RETFMT.replace("<DATA>", getConvertedResult(tempArrayObj.toString())).replace("<CODE>", "1").replace("<MSG>", StringUtils.EMPTY);
		}
	}
	
	private String getConvertedResult(String data) throws Exception {
		// TODO Auto-generated method stub
		//定义返回的json字符串
		logger.info("FindAnalyseResultOutProcessor data--->" + data);
		
		JSONObject retJsonObj = new JSONObject();
		/**
		 * 此处传入的data是将多个content_id对应的jsonObject组装成一个jsonArray返回
		 */
		JSONArray contentsJson = JSONArray.fromObject(data);
		logger.info("contentsJson-->" + contentsJson.toString());
		
		/**
		 * 将组装成一个json的收藏内容作为原始数据传入到文本分析核心算法接口
		 * 核心算法经过文本解析之后，将得到的服务方案进行保存
		 */
		
		OfferingRecommendorInterface offerRecommendor = (OfferingRecommendorInterface) Naming
				.lookup("rmi://0.0.0.0:8093/AnalysisService");
		String contentRelServices = offerRecommendor.contentRelOfferingAnalysis(contentsJson);
		logger.info("FindAnalyseResultOutProcessor contentRelServices-->" + contentRelServices);
		
		/**
		 * 调用文本分析的核心算法获得与content_id列表相关的offering列表
		 */
		ProposalRecommendorInterface proposalRecommendor = (ProposalRecommendorInterface) Naming.lookup("rmi://0.0.0.0:8094/proposalRecommendor");
		String proposalReturnData = proposalRecommendor.contentRelProposalAnalysis(contentRelServices);
		logger.info("FindAnalyseResultOutProcessor proposalReturnData--->" + proposalReturnData);
		
//		/**
//		 * 对返回的proposalJson进行解析，拼成前端需要的返回参数格式
//		 */
//		JSONObject proposalJsonObj = JSONObject.fromObject(proposalReturnData).getJSONObject("data");
//		logger.info("FindAnalyseResultOutProcessor proposalJsonObj--->" + proposalJsonObj.toString());
//		
//		// Set scheme_id
//		Long scheme_id = proposalJsonObj.getLong("proposalId");     //服务方案id
//		retJsonObj.put("scheme_id", scheme_id);
//		
//		JSONArray proposalItemList = proposalJsonObj.getJSONArray("proposalItem");
//
//		// Set param_list
//		JSONArray paramListArray = new JSONArray();
//		retJsonObj.put("param_list", paramListArray);
//		
//		// Set service_list  service_list是指那些parentElementId为空的offering
//		JSONArray serviceListArray = new JSONArray();
//		if(null != proposalItemList && proposalItemList.size() > 0){
//			for(int i = 0; i < proposalItemList.size(); i++){
//				JSONObject singleProposalItem = proposalItemList.getJSONObject(i);
//				if(null != singleProposalItem && singleProposalItem.getString("proposalItemType").equalsIgnoreCase("Offering")){
//					Long parentElementId = singleProposalItem.getLong("parentElementId");
//					if(parentElementId == null){
//						Long serviceIdTemp = singleProposalItem.getLong("elementId");
//						JSONObject temp = new JSONObject();
//						temp.put("service_id", serviceIdTemp);
//						serviceListArray.add(temp);
//					}
//				}
//			}
//		}
//		retJsonObj.put("service_list", serviceListArray);
//
//		// Set catalog_list
//		JSONArray catalogListArray = new JSONArray();
//		JSONArray offeringListArray = new JSONArray();
//		if (null != proposalItemList && proposalItemList.size() > 0) {
//			for (int nIndex = 0; nIndex < proposalItemList.size(); ++nIndex) {
//
//				JSONObject itemObject = proposalItemList.getJSONObject(nIndex);
//				
//				if (null != itemObject&& "Catalog".equalsIgnoreCase(itemObject.getString("proposalItemType"))) {
//					JSONObject retItemObj = new JSONObject();
//					retItemObj.put("catalog_id",itemObject.getLong("elementId"));
//					retItemObj.put("catalog_name",itemObject.getString("catalogName"));
//					retItemObj.put("select_flag", itemObject.getInt("selectFlagOfVirtualService"));
//					retItemObj.put("binding_flag",itemObject.getInt("isMain"));
//					retItemObj.put("relevant_level",itemObject.getString("relevant_level"));
//
//					final long nProposalItemId = itemObject.getLong("elementId");
//					final String itemType = itemObject.getString("proposalItemType");
//					if (StringUtils.isEmpty(itemType)) {
//						continue;
//					}
//
//					// Add Offerings
//					JSONArray offering1ListArray = new JSONArray();
//					for (int nIndex1 = 0; nIndex1 < proposalItemList.size(); ++nIndex1) {
//						JSONObject itemObject1 = proposalItemList.getJSONObject(nIndex1);
//						if (null != itemObject1&& "Offering".equalsIgnoreCase(itemObject1.getString("proposalItemType"))) {
//
//							long parentElemId = itemObject1.getLong("parentElementId");
//							String parentElemType = itemObject1.getString("parentElementType");
//							if (nProposalItemId == parentElemId&& itemType.equalsIgnoreCase(parentElemType)) {
//								JSONObject retOfferingItemObj = new JSONObject();
//								retOfferingItemObj.put("service_id",itemObject1.getLong("elementId"));
//								offering1ListArray.add(retOfferingItemObj);
//							}
//						}
//					}
//					retItemObj.put("service_list", offering1ListArray);
//					catalogListArray.add(retItemObj);
//				}
//
//				if (null != itemObject&& "Offering".equalsIgnoreCase(itemObject.getString("proposalItemType"))) {
//
//					if (itemObject.getLong("parentElementId") < 1&& StringUtils.isEmpty(itemObject.getString("parentElementType"))) {
//						JSONObject retOfferingItemObj = new JSONObject();
//						retOfferingItemObj.put("service_id",itemObject.getLong("elementId"));
//						offeringListArray.add(retOfferingItemObj);
//					}
//				}
//			}
//
//		}
//		retJsonObj.put("catalog_list", catalogListArray);
//		if (offeringListArray.size() > 0) {
//			retJsonObj.put("service_list", offeringListArray);
//		}
//		String retValue = retJsonObj.toString();
//		return retValue;
		return proposalReturnData;
	}
}


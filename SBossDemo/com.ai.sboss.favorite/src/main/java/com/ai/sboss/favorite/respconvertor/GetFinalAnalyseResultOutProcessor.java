package com.ai.sboss.favorite.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class GetFinalAnalyseResultOutProcessor implements IBasicOutProcessor {

	private static final Logger LOGGER = Logger
			.getLogger(GetFinalAnalyseResultOutProcessor.class);

	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"1\",\"digest\":\"\"}}";

	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		final String retResult = convert2requst(inMessage.getBody(String.class));

		LOGGER.info(retResult);

		inMessage.setBody(retResult);
	}

	@Override
	public String convert2requst(String exchange) throws Exception {
		JSONObject input = JSONObject.fromObject(exchange);
		LOGGER.info("GetFinalAnalyseResultOutProcessor first input --->" + input.toString());

		Object dataValue = input.get("data");
		if (null != dataValue) {
			if (dataValue.toString().isEmpty()) {
				return RETFMT.replace("<DATA>", "[]")
						.replace("<CODE>", "0")
						.replace("<MSG>", StringUtils.EMPTY);
			} else {
				
				LOGGER.info("GetFinalAnalyseResultOutProcessor temp --->" +  getConvertedResult(input.getJSONObject("data")));
				return RETFMT.replace("<DATA>", getConvertedResult(input.getJSONObject("data")))
						.replace("<CODE>", "1")
						.replace("<MSG>", "success");
			}
		} else {
			return RETFMT.replace("<DATA>", "[]")
					.replace("<CODE>", "0")
					.replace("<MSG>", StringUtils.EMPTY);
		}
	}

	private String getConvertedResult(JSONObject input) {
		
		LOGGER.info("GetFinalAnalyseResultOutProcess second input --->" + input.toString());
		String retValue = null;
		if (null != input) {
			JSONObject dataJsonObj = input.getJSONObject("data");
			JSONArray proposalItemList = dataJsonObj.getJSONArray("proposalItem");
			JSONObject retJsonObj = new JSONObject();

			// Set scheme_id
			Long scheme_id = dataJsonObj.getLong("proposalId");     //服务方案id
			retJsonObj.put("scheme_id", scheme_id);
			
			// Set param_list
			JSONArray paramListArray = new JSONArray();
			retJsonObj.put("param_list", paramListArray);
			
			// Set service_list  service_list是指那些parentElementId为空的offering
			JSONArray serviceListArray = new JSONArray();
			if(null != proposalItemList && proposalItemList.size() > 0){
				for(int i = 0; i < proposalItemList.size(); i++){
					JSONObject singleProposalItem = proposalItemList.getJSONObject(i);
					if(null != singleProposalItem && singleProposalItem.getString("proposalItemType").equalsIgnoreCase("Offering")){
						Long parentElementId = singleProposalItem.getLong("parentElementId");
						if(parentElementId == null){
							Long serviceIdTemp = singleProposalItem.getLong("elementId");
							JSONObject temp = new JSONObject();
							temp.put("service_id", serviceIdTemp);
							serviceListArray.add(temp);
						}
					}
				}
			}
			retJsonObj.put("service_list", serviceListArray);

			// Set catalog_list
			JSONArray catalogListArray = new JSONArray();
			JSONArray offeringListArray = new JSONArray();
			if (null != proposalItemList && proposalItemList.size() > 0) {
				for (int nIndex = 0; nIndex < proposalItemList.size(); ++nIndex) {

					JSONObject itemObject = proposalItemList
							.getJSONObject(nIndex);
					if (null != itemObject
							&& "Catalog".equalsIgnoreCase(itemObject
									.getString("proposalItemType"))) {
						JSONObject retItemObj = new JSONObject();
						retItemObj.put("catalog_id",
								itemObject.getLong("elementId"));
						retItemObj.put("catalog_name",
								itemObject.getString("catalogName"));
						retItemObj
								.put("select_flag", itemObject
										.getInt("selectFlagOfVirtualService"));
						retItemObj.put("binding_flag",
								itemObject.getInt("isMain"));
						retItemObj.put("relevant_level",
								itemObject.getString("relevant_level"));

						final long nProposalItemId = itemObject
								.getLong("elementId");
						final String itemType = itemObject
								.getString("proposalItemType");
						if (StringUtils.isEmpty(itemType)) {
							continue;
						}

						// Add Offerings
						JSONArray offering1ListArray = new JSONArray();
						for (int nIndex1 = 0; nIndex1 < proposalItemList.size(); ++nIndex1) {
							JSONObject itemObject1 = proposalItemList
									.getJSONObject(nIndex1);
							if (null != itemObject1
									&& "Offering".equalsIgnoreCase(itemObject1
											.getString("proposalItemType"))) {

								long parentElemId = itemObject1
										.getLong("parentElementId");
								String parentElemType = itemObject1
										.getString("parentElementType");
								if (nProposalItemId == parentElemId
										&& itemType
												.equalsIgnoreCase(parentElemType)) {
									JSONObject retOfferingItemObj = new JSONObject();
									retOfferingItemObj.put("service_id",
											itemObject1.getLong("elementId"));
									offering1ListArray.add(retOfferingItemObj);
								}
							}
						}
						retItemObj.put("service_list", offering1ListArray);

						catalogListArray.add(retItemObj);
					}

					if (null != itemObject
							&& "Offering".equalsIgnoreCase(itemObject
									.getString("proposalItemType"))) {

						if (itemObject.getLong("parentElementId") < 1
								&& StringUtils.isEmpty(itemObject
										.getString("parentElementType"))) {
							JSONObject retOfferingItemObj = new JSONObject();
							retOfferingItemObj.put("service_id",
									itemObject.getLong("elementId"));
							offeringListArray.add(retOfferingItemObj);
						}

					}
				}

			}
			retJsonObj.put("catalog_list", catalogListArray);
			if (offeringListArray.size() > 0) {
				retJsonObj.put("service_list", offeringListArray);
			}

			retValue = retJsonObj.toString();
		}

		return retValue;
	}
}

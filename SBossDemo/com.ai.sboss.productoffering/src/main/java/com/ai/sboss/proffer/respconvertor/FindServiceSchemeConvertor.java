package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class FindServiceSchemeConvertor implements IBasicOutProcessor {

	private static final Logger LOGGER = Logger
			.getLogger(FindServiceSchemeConvertor.class);

	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":1,\"result_msg\":\"\",\"data_mode\":\"1\",\"digest\":\"\"}}";

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
		LOGGER.info(input.toString());

		Object dataValue = input.get("data");
		if (null != dataValue) {
			if (dataValue.toString().isEmpty()) {
				return RETFMT.replace("<DATA>", "[]")
						.replace("<CODE>", input.getString("hub_code"))
						.replace("<MSG>", StringUtils.EMPTY);
			} else {
				return RETFMT.replace("<DATA>", getConvertedResult(input))
						.replace("<CODE>", input.getString("hub_code"))
						.replace("<MSG>", StringUtils.EMPTY);
			}
		} else {
			return RETFMT.replace("<DATA>", "[]")
					.replace("<CODE>", input.getString("0"))
					.replace("<MSG>", StringUtils.EMPTY);
		}
	}

	private String getConvertedResult(JSONObject input) {
		String retValue = null;
		if (null != input) {
			JSONObject dataJsonObj = input.getJSONObject("data");
			JSONArray proposalItemList = dataJsonObj
					.getJSONArray("proposalItem");
			JSONObject retJsonObj = new JSONObject();

			// Set param_list
			JSONArray paramListArray = new JSONArray();
			retJsonObj.put("param_list", paramListArray);

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

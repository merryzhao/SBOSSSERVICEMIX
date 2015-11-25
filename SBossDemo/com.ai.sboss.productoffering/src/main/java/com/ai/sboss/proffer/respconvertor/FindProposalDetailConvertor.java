package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class FindProposalDetailConvertor implements IBasicOutProcessor {

	private static final Logger LOGGER = Logger
			.getLogger(FindProposalDetailConvertor.class);

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
		String retValueString = null;
		if (null != input) {
			JSONObject proposalObject = input.getJSONObject("data");
			if (null != proposalObject) {

				JSONObject retJSONObj = new JSONObject();
				retJSONObj.put("proposal_id",
						proposalObject.getLong("proposalId"));
				retJSONObj.put("proposal_name",
						proposalObject.getString("proposalName"));
				retJSONObj.put("order_times",
						proposalObject.getLong("orderedTimes"));

				JSONArray proposalItemsArray = proposalObject
						.getJSONArray("proposalItem");
				if (null != proposalItemsArray && proposalItemsArray.size() > 0) {
					JSONArray retOfferingList = new JSONArray();
					for (int nIndex = 0; nIndex < proposalItemsArray.size(); ++nIndex) {
						JSONObject itemObj = proposalItemsArray
								.getJSONObject(nIndex);
						if (null != itemObj) {
							JSONObject jsonObj = new JSONObject();
							jsonObj.put("service_id",
									itemObj.getLong("elementId"));
							retOfferingList.add(jsonObj);
						}
					}
					retJSONObj.put("service_list", retOfferingList);
				}

				retValueString = retJSONObj.toString();
			}
		}

		return retValueString;
	}
}

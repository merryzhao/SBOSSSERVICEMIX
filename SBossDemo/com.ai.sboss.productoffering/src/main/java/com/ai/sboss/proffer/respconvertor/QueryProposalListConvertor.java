package com.ai.sboss.proffer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryProposalListConvertor implements IBasicOutProcessor {

	private static final Logger LOGGER = Logger
			.getLogger(QueryProposalListConvertor.class);

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
			JSONArray dataJsonArray = input.getJSONArray("data");
			if (null != dataJsonArray && dataJsonArray.size() > 0) {
				JSONArray listOfProposalId = new JSONArray();
				for (int nIndex = 0; nIndex < dataJsonArray.size(); ++nIndex) {
					JSONObject object = dataJsonArray.getJSONObject(nIndex);
					if (null != object) {
						JSONObject jsonObj = new JSONObject();
						jsonObj.put("proposal_id",
								Long.toString(object.getLong("proposalId")));
						listOfProposalId.add(jsonObj);
					}
				}

				JSONObject retJSONObj = new JSONObject();
				retJSONObj.put("proposal_list", listOfProposalId);
				retValueString = retJSONObj.toString();
			}
		}

		return retValueString;
	}
}

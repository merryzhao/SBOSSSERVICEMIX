package com.ai.sboss.proffer.respconvertor;

import java.sql.Timestamp;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QueryCompServiceInfoConvertor implements IBasicOutProcessor {
	Logger logger = Logger.getLogger(QueryCompServiceInfoConvertor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String retdata = convert2requst(in.getBody(String.class));
		Message retMessage = exchange.getOut();
		retMessage.setBody(retdata);
		exchange.setIn(retMessage);
	}

	@Override
	public String convert2requst(String data) throws Exception {

		JSONObject crmofferdetails = JSONObject.fromObject(data);
		JSONObject crmofferdetail = new JSONObject() ;
		if (crmofferdetails.getLong("hub_code") == 0L) {
			return null;
		}
		crmofferdetail = crmofferdetails.getJSONObject("data");

		JSONArray bundleProdOfferList = crmofferdetail.getJSONArray("bundleProdOfferOptionList") ;

	// 取服务主数据
		JSONObject compServDetail  = AnalysisOfferValue(crmofferdetail);
	// 取 角色LIST数据
        JSONArray relOfferRoleList  = AnalysisRoleList(bundleProdOfferList) ;


        compServDetail.element("relServRoleList", relOfferRoleList) ;
		// logger.info("current retdetail is++>"+retdetail);
		return compServDetail.toString();
	}


	public  JSONObject AnalysisOfferValue(JSONObject tempOfferValue) {

		 JSONObject   OfferValue = new JSONObject();

		OfferValue.put("service_id", tempOfferValue.get("offerId"));
		OfferValue.put("service_name", tempOfferValue.get("offeringName"));

		return OfferValue ;
	}

	 private  JSONArray AnalysisRoleList(JSONArray tempOfferRoleList) {

		  JSONArray relOfferRoleList = new JSONArray();

		for (int index = 0; index < tempOfferRoleList.size(); index++) {

			JSONObject bundleProdOfferObject = JSONObject
					.fromObject(tempOfferRoleList.get(index));
			JSONObject relOffering = JSONObject
					.fromObject(bundleProdOfferObject.get("relOffering"));
			JSONObject role = JSONObject.fromObject(bundleProdOfferObject
					.get("role"));

			JSONObject relOfferRole = new JSONObject();
			relOfferRole.put("relService_id", relOffering.get("offerId"));

			relOfferRole.put("relService_name",
					relOffering.get("offeringName"));

			relOfferRole.put("relService_roleId", role.get("roleId"));
			relOfferRole.put("relService_roleName", role.get("roleName"));

			relOfferRoleList.add(relOfferRole);
		}

		return relOfferRoleList ;
	}

}

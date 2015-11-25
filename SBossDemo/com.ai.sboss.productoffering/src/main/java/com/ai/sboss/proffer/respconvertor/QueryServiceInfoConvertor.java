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

public class QueryServiceInfoConvertor implements IBasicOutProcessor {
	Logger logger = Logger.getLogger(QueryServiceInfoConvertor.class);

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
		if (crmofferdetails.getLong("hub_code") == 0L) {
			return null;
		}
		crmofferdetails = crmofferdetails.getJSONObject("data");
		JSONObject retdetail = parserCrmOfferingDetail(crmofferdetails);
		Long provider_id = 0L;
		if (retdetail.containsKey("service_provider")) {
			provider_id = retdetail.getLong("service_provider");
			retdetail.remove("service_provider");
		}
		retdetail.put("provider_id", provider_id);
		// logger.info("current retdetail is++>"+retdetail);
		return retdetail.toString();
	}

	private JSONObject parserCrmOfferingDetail(JSONObject crmjson) throws Exception {
		if (crmjson == null) {
			return null;
		}

		JSONObject service_info = new JSONObject();
		JSONArray optionArray = new JSONArray();

		service_info.put("service_id", crmjson.getLong("offerId"));
		service_info.put("service_name", crmjson.getString("offeringName"));

		JSONArray valList = crmjson.getJSONArray("productOfferingSpecCharList");
		for (int j = 0; j < valList.size(); j++) {
			JSONObject productOfferingSpecCharObject = valList.getJSONObject(j);
			JSONObject specCharObject = productOfferingSpecCharObject.getJSONObject("specCharId");

			if (specCharObject.isNullObject() || specCharObject.isEmpty()) {
				continue;
			}
			if (specCharObject.getLong("isCustomized") == 0) { // 固定参数
				service_info = parserMandatoryElement(service_info, productOfferingSpecCharObject);
			} else { // 客户可选参数
				optionArray = parserOptionElement(optionArray, productOfferingSpecCharObject);
			}
		}

		logger.info("QueryServiceInfoConvertor--->service_info" + service_info);
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		//将价格相关的参数包装为一个价格对象
		JSONObject service_price = new JSONObject();
		String priceTemp = "0.0";
		if(service_info.containsKey("price")){
			priceTemp = service_info.getString("price");
		}
		Double priceFinal = 0.0;
		logger.info("priceTemp=====>" + priceTemp);
		if(priceTemp != ""){
			priceFinal = Double.parseDouble(priceTemp);
		}
		logger.info("pricefinal=====>" + priceFinal);
		service_price.put("price", priceFinal);
		String billing_mode = "";
		if(service_info.containsKey("billing_mode")){	
			billing_mode = service_info.getString("billing_mode");
		}
		//将unit默认为美元
		String unit = "$";
		if(service_info.containsKey("unit")){
			unit = service_info.getString("unit");
		}
		service_price.put("billing_mode", billing_mode);
		service_price.put("unit", unit);
		service_info.remove("price");
		service_info.remove("billing_mode");
		service_info.remove("unit");
		service_info.put("service_price", service_price);
		
		// 服务保障，需求中没提，填写空值
		String service_guarantee = "";
		service_info.put("service_guarantee", service_guarantee);
		// 服务约束
		String service_restraint = "";
		service_info.put("service_restraint", service_restraint);
		// 服务流程 TODO: 需要和流程系统对接
		String service_process = "";
		service_info.put("service_process", service_process);
		service_info.put("service_exectime", ts.getTime());
		service_info.put("service_param_list", optionArray.toString());
		
		//添加评分数据
		if(!service_info.containsKey("service_rating")){
			service_info.put("service_rating", 0.0);
		}
		logger.info("queryServiceInfoConvertor_service_info======>" + service_info);
		return service_info;
	}

	private JSONArray parserOptionElement(JSONArray optionArray, JSONObject productOfferingSpecCharObject) {
		JSONObject specCharObject = productOfferingSpecCharObject.getJSONObject("specCharId");
		JSONArray productOfferingSpecCharValList = productOfferingSpecCharObject.getJSONArray("productOfferingSpecCharValList");
		if (specCharObject.isNullObject()) {
			return optionArray;
		}
		if (specCharObject.isEmpty()) {
			return optionArray;
		}
		// 可选参数名称
		String paramName = specCharObject.getString("charSpecName");
		// 解析可选参数
		JSONObject optionelement = new JSONObject();
		optionelement.put("param_type", StringUtils.isEmpty(specCharObject.getString("charSpecType"))?0L:Long.getLong(specCharObject.getString("charSpecType")));
		optionelement.put("param_key", paramName);
		Long param_key_id = specCharObject.getLong("specCharId");
		optionelement.put("param_key_id", param_key_id);

		// 可选参数值列表
		JSONArray optionvalueList = new JSONArray();
		for (int i = 0; i < productOfferingSpecCharValList.size(); ++i) {
			JSONObject optionValue = new JSONObject();
			JSONObject currentOfferingSpecCharVal = productOfferingSpecCharValList.getJSONObject(i);
			optionValue.put("id", currentOfferingSpecCharVal.getJSONObject("specCharValId").getLong("specCharValId"));
			optionValue.put("title", currentOfferingSpecCharVal.getJSONObject("specCharValId").getString("displayVal"));
			optionValue.put("content", currentOfferingSpecCharVal.getJSONObject("specCharValId").getString("displayVal"));
			optionValue.put("memo", currentOfferingSpecCharVal.getJSONObject("specCharValId").getString("displayVal"));
			optionValue.put("value", (currentOfferingSpecCharVal.getJSONObject("specCharValId").getLong("isDefault") != 0L)?0L:1L);
			optionvalueList.add(optionValue);
		}
		if (optionvalueList.isEmpty() && !productOfferingSpecCharObject.getString("value").isEmpty()) {
			optionelement.put("param_value", "["+productOfferingSpecCharObject.getString("value")+"]");	
		} else {
			optionelement.put("param_value", optionvalueList.toString());
		}
		optionArray.add(optionelement);
		return optionArray;
	}

	private JSONObject parserMandatoryElement(JSONObject service_info, JSONObject productOfferingSpecCharObject) {
		if (productOfferingSpecCharObject.isNullObject()) {
			return service_info;
		}
		if (productOfferingSpecCharObject.isEmpty()) {
			return service_info;
		}
		JSONObject specCharObject = productOfferingSpecCharObject.getJSONObject("specCharId");
		Long specCharId = specCharObject.getLong("specCharId");
		
		// 解析必选，不可定制参数
		if (specCharId == 25042641L) {// 服务介绍图片URL
			String service_intro_url = "";
			service_intro_url = productOfferingSpecCharObject.getString("value").isEmpty() ? "" : productOfferingSpecCharObject.getString("value");
			service_info.put("service_intro_url", service_intro_url);		
			//将service_thumbnail_url也加进去
			String service_thumbnail_url = "";
			service_thumbnail_url = productOfferingSpecCharObject.getString("value").isEmpty() ? "" : productOfferingSpecCharObject.getString("value");
			service_info.put("service_thumbnail_url", service_thumbnail_url);
//		} else if (specCharId == 25042641L) {// 服务介绍缩略图
//			String service_thumbnail_url = "";
//			service_thumbnail_url = productOfferingSpecCharObject.getString("value").isEmpty() ? "" : productOfferingSpecCharObject.getString("value");
//			service_info.put("service_thumbnail_url", service_thumbnail_url);
		} else if (specCharId == 25042640L) {// 服务简介
			String service_intro = "";
			service_intro = productOfferingSpecCharObject.getString("value").isEmpty() ? "" : productOfferingSpecCharObject.getString("value");
			service_info.put("service_intro", service_intro);
		} else if (specCharId == 25042645L) {//价格数值
			String price = productOfferingSpecCharObject.getString("value").isEmpty() ? "" : productOfferingSpecCharObject.getString("value");
			service_info.put("price", price);
		} else if (specCharId == 25042664L) { // 服务提供者（策划者）ID
			Long service_provider_id = -1L;
			service_provider_id = productOfferingSpecCharObject.getString("value").isEmpty() ? 0L: Long.parseLong(productOfferingSpecCharObject.getString("value"));
			service_info.put("service_provider", service_provider_id);
		} else if (specCharId == 25042662L) { //服务提供商头像图片URL
			String service_provider_url = productOfferingSpecCharObject.getString("value").isEmpty() ? "" : productOfferingSpecCharObject.getString("value");
			service_info.put("provider_portrait_url", service_provider_url);
		} else if (specCharId == 25042711L) { //服务流程
			String service_process = productOfferingSpecCharObject.getString("value").isEmpty() ? "" : productOfferingSpecCharObject.getString("value");
			service_info.put("service_process", service_process);
		} else if (specCharId == 25042738L) { //服务约束
			String service_restraint = productOfferingSpecCharObject.getString("value").isEmpty() ? "" : productOfferingSpecCharObject.getString("value");
			service_info.put("service_restraint", service_restraint);
		} else if (specCharId == 25042736L) { //服务保障
			String service_guarantee = productOfferingSpecCharObject.getString("value").isEmpty() ? "" : productOfferingSpecCharObject.getString("value");
			service_info.put("service_guarantee", service_guarantee);
		} else if (specCharId == 25043015L) { //销售品模板
			String offering_templateid = productOfferingSpecCharObject.getString("value").isEmpty() ? "" : productOfferingSpecCharObject.getString("value");
			service_info.put("offering_templateid", offering_templateid);
		} else if (specCharId == 25043196L) { //计费方式
			String billing_mode = productOfferingSpecCharObject.getString("value").isEmpty() ? "" : productOfferingSpecCharObject.getString("value");
			service_info.put("billing_mode", billing_mode);
		} else if (specCharId == 25043195L) { //货币单位
			String unit = productOfferingSpecCharObject.getString("value").isEmpty() ? "" : productOfferingSpecCharObject.getString("value");
			service_info.put("unit", unit);
		}
		else {
			logger.warn("找到没有确认的必要参数"+specCharObject.getString("charSpecName"));
			if (StringUtils.isEmpty(specCharObject.getString("charSpecName"))) {
				return service_info;
			}
			String valueString = productOfferingSpecCharObject.getString("value");
			service_info.put(specCharObject.getString("charSpecName"), valueString);
		}
		return service_info;
	}
}

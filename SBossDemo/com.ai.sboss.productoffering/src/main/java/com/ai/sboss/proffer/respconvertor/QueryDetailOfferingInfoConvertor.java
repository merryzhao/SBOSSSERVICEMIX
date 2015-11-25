/**
 * 
 */
package com.ai.sboss.proffer.respconvertor;

/**
 * 
 */
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author idot
 *
 */
public class QueryDetailOfferingInfoConvertor implements IBasicOutProcessor{

	Logger logger = Logger.getLogger(QueryDetailOfferingInfoConvertor.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		String data = exchange.getIn().getBody(String.class);
		String returnJson = convert2requst(data);
//		logger.info("returnJson-----------------------" + returnJson);
		exchange.getIn().setBody(returnJson);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		// TODO Auto-generated method stub
		JSONObject crmret = JSONObject.fromObject(data);
//		logger.info("crm returned==>"+crmret.toString());
		JSONObject outputJson = new JSONObject();
		
		
		JSONObject jsonData = crmret.getJSONObject("data");
		JSONArray params  = jsonData.getJSONArray("productOfferingSpecCharList");
		
		
		String service_intro = "";
		String offeringPrice = "";
		String service_thumbnail_url = "";
		
		String offeringId = jsonData.getString("offerId");
		String offeringName = jsonData.getString("offeringName");
		JSONObject obj = new JSONObject();
				
		for(int i = 0; i< params.size(); i++) {	
			JSONArray valList = params.getJSONObject(i).getJSONArray("productOfferingSpecCharValList");
			
			for (int j = 0; j < valList.size(); j++) {
				JSONObject specCharObject = valList.getJSONObject(j).getJSONObject("specCharValId");
//				logger.info("specCharObject****************" + specCharObject);
				if (specCharObject.getJSONObject("specChar").getString("charSpecName").equals("intro_url")) {
					service_thumbnail_url = specCharObject.getString("value");
					logger.info("service_intro_url........." + service_thumbnail_url);
				}else if (specCharObject.getJSONObject("specChar").getString("charSpecName").equals("tag_name")) {
					String tempTagName = specCharObject.getString("value");
//					logger.info("tempTagName-----------------" + tempTagName);
					obj.put("tag_name", tempTagName);
				} else if (specCharObject.getJSONObject("specChar").getString("charSpecName").equals("description")) {
					service_intro = specCharObject.getString("displayVal");
				}else if(specCharObject.getJSONObject("specChar").getString("charSpecName").equals("price")){
					offeringPrice = specCharObject.getString("displayVal");
				}
			}
		}
		outputJson.put("service_id", offeringId);
		outputJson.put("service_name", offeringName);
		outputJson.put("service_intro", service_intro);
		outputJson.put("service_price",offeringPrice);
		outputJson.put("service_thumbnail_url", service_thumbnail_url);
		outputJson.put("service_tags", obj);
//		logger.info("==>"+outputJson.toString());
		return outputJson.toString();
	}

}

package com.ai.sboss.proffer.respconvertor;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

public class QueryServiceCommentConvertor implements IBasicOutProcessor {

	protected Logger logger = Logger.getLogger(this.getClass());
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		String data = exchange.getIn().getBody(String.class);
		String returnJson = convert2requst(data);
		exchange.getIn().setBody(returnJson);
	}

	@Override
	public String convert2requst(String data) throws Exception {

		logger.info("queryServiceCommentConvertor_data====>" + data);
		//定义返回的comment
		JSONObject offeringGrades = new JSONObject();
		
		Double service_rating = 0.0;
		JSONObject resultCommentsJson = JSONObject.fromObject(data);
		JSONArray commentArray = resultCommentsJson.getJSONArray("data");
		if(commentArray.size() != 0){
	        for(int i = 0; i < commentArray.size(); i++){
	            JSONObject commentElement = commentArray.getJSONObject(i);
	            JSONArray gradesArray = commentElement.getJSONArray("grades");
	            logger.info("gradesArray====>" + gradesArray);
	            if(gradesArray.size() == 0){
	                  continue;
	            }
	            for(int j = 0; j < gradesArray.size(); j++){
	                 JSONObject gradeJson = gradesArray.getJSONObject(j);
	                 if(gradeJson.getJSONObject("gradeSpecification").getLong("gradeSpecificationId") == 2001L){
	                	   String ratingTemp = gradeJson.getString("gradeValue");
	                	   service_rating = Double.parseDouble(ratingTemp);
	                	   logger.info("service_rating=====>" + service_rating);
	                       break;
	                 }
	            }
	            if(service_rating !=null){
	            	break;
	            }
	        }
		}
		
        offeringGrades.put("service_rating", service_rating);
        return offeringGrades.toString();
	}
}

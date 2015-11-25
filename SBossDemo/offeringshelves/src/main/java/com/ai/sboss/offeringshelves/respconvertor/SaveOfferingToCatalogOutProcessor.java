/**
 * 
 */
package com.ai.sboss.offeringshelves.respconvertor;

import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;
import com.ai.sboss.upc.httpRequestInterface.UpcHttpRequestInterface;
/**
 * @author idot
 *
 */
public class SaveOfferingToCatalogOutProcessor implements IBasicOutProcessor{

	protected Logger logger = Logger.getLogger(this.getClass());
//	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":1,\"result_msg\":\"success\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	private final static String RETFMT = "<DATA>";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Message inMessage = exchange.getIn();
		String ret = convert2requst(inMessage.getBody(String.class));
		inMessage.setBody(ret);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		// TODO Auto-generated method stub
		//获取前端传入的参数
		JSONObject param = JSONObject.fromObject(data);
		JSONArray offeringList = param.getJSONArray("offeringList");
		//采用RMI的协议方式，调用upc接口
		UpcHttpRequestInterface upcHttpRequestInterface = (UpcHttpRequestInterface) Naming.lookup("rmi://10.5.1.249:8091/upcHttpRequestService");
		
		upcHttpRequestInterface.loginIn();
		
		//设置参数
		Long nodeId = param.getLong("nodeId");
//		String paras = "nodeId=" + nodeId + "&_=1440128733501";     //设置查询catalognodeid对应的销售品列表的参数
		String paras = "nodeId=" + nodeId;
		List<Object> tempOfferIdList = new ArrayList<Object>();
		//获取到指定catalognodeid对应的销售品列表
		String catalogRelOfferings = upcHttpRequestInterface.sendGetRequest("http://10.1.228.153:8090/ALUPC-jc2/?service=ajax&page=upc.product.RelatedProductOfferingNode&listener=fetchRelOffering&", paras);
		
		JSONObject relOfferings = JSONObject.fromObject(catalogRelOfferings);
		logger.info("relOfferings--------------" + relOfferings);
		JSONObject tempObject = relOfferings.getJSONObject("data");
		logger.info("tempObject-----------------" + tempObject);
		if(tempObject != null || !tempObject.isEmpty() || tempObject.equals("{}")){
			JSONArray relOfferingsArray = tempObject.getJSONArray("data");
			logger.info("relOfferingsArray----------" + relOfferingsArray);
			if(relOfferingsArray != null){
				for(int i=0; i < relOfferingsArray.size(); i++){
					JSONObject temp = JSONObject.fromObject(relOfferingsArray.get(i).toString());
					tempOfferIdList.add(temp.get("offerId"));
				}
			}
		}
		if(offeringList != null){
			for(int i=0; i < offeringList.size(); i++){
				tempOfferIdList.add(JSONObject.fromObject(offeringList.get(i).toString()).get("offeringId"));
			}
		}
		logger.info("tempOfferIdList--------------" + tempOfferIdList);
		String offerings = "";
		for (Object tempOfferId : tempOfferIdList){
			offerings = offerings + tempOfferId + "%2C"; 
		}
		logger.info("offerings-------------------" + offerings);
		String parameters = "offerIds=" + offerings.substring(0, offerings.length() - 3) + "&nodeId=" + nodeId;
		logger.info("parameters-------------------" + parameters);
		String resultJson = upcHttpRequestInterface.sendGetRequest("http://10.1.228.153:8090/ALUPC-jc2/?service=ajax&page=upc.product.RelatedProductOfferingNode&listener=saveRelOffering&", parameters);
		logger.info("request url------------------" + ("http://10.1.228.153:8090/ALUPC-jc2/?service=ajax&page=upc.product.RelatedProductOfferingNode&listener=saveRelOffering&"+parameters));
		return RETFMT.replace("<DATA>", resultJson.toString());
	}

}

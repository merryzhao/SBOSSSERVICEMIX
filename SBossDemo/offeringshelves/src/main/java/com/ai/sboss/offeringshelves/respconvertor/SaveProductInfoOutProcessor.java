package com.ai.sboss.offeringshelves.respconvertor;

import java.rmi.Naming;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;
import com.ai.sboss.upc.httpRequestInterface.UpcHttpRequestInterface;

public class SaveProductInfoOutProcessor implements IBasicOutProcessor {
	private final static String FAKEFILE = "E:/temp/offeringInfo.properties";
	private final static String JSON_KEY = "contents";
	private final static int UI_TEMPLATE_ID = 287;
	protected Logger logger = Logger.getLogger(this.getClass());
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":1,\"result_msg\":\"success\",\"data_mode\":\"0\",\"digest\":\"\"}}";

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
		//规整前台传入的参数
		JSONObject inputParam  = JSONObject.fromObject(data);
		String tmpData = inputParam.getString(JSON_KEY);
		String headFix = "taskId=0&opType=new&objInstId=0&";
		String parameters =  new String();
		int uiTemplateId = UI_TEMPLATE_ID; //这里先写死吧，不知道是否会有影响。
		
		//汇总成Http参数
		parameters = headFix + "UITemplateId=" + uiTemplateId + "&" + "objData=" + tmpData + "&" + "dataTemplateFlag=&objVersion=V1";			
		
		logger.info("param------------>" + parameters);
		

		//调用Http服务
		UpcHttpRequestInterface upcHttpRequestInterface = (UpcHttpRequestInterface) Naming.lookup("rmi://10.5.1.249:8091/upcHttpRequestService");
		
		upcHttpRequestInterface.loginIn();
		
		String resultJson = upcHttpRequestInterface.sendPostRequest("http://10.1.228.153:8090/ALUPC-jc/?service=ajax&page=upc.product.OfferEditUI&listener=saveObjData&m=81000010&p=upc.product.OfferEditUI&" + parameters, "");
		return RETFMT.replace("<DATA>", resultJson.toString());
	}
}
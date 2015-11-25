/**
 * 
 */
package com.ai.sboss.offeringshelves.respconvertor;

import java.rmi.Naming;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;
import com.ai.sboss.upc.httpRequestInterface.UpcHttpRequestInterface;
/**
 * @author pzy
 *
 */
public class SaveOfferingInfoOutProcessor implements IBasicOutProcessor{
	private final static String FAKEFILE = "data/fakedata/offeringInfo.properties";
	private final static String JSON_KEY = "contents";
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
		//获取前端传入的参数
//		LineNumberReader linereader = new LineNumberReader(new FileReader(FAKEFILE));
//		String parameters = linereader.readLine();
		JSONObject tempJson = JSONObject.fromObject(data);
		String tempParam = tempJson.getString(JSON_KEY);
//		parameters = URLEncoder.encode(parameters, "utf-8");

		String parameters = "taskId=0&opType=new&objInstId=0&UITemplateId=45&objData=" + tempParam + "&" +"dataTemplateFlag=&objVersion=V1";
		logger.info("encode parameters------>" + parameters);
		//采用RMI的协议方式，调用upc接口，其中，charSpecType的类型写死为ACCESS（解决版本问题）
		UpcHttpRequestInterface upcHttpRequestInterface = (UpcHttpRequestInterface) Naming.lookup("rmi://10.5.1.249:8091/upcHttpRequestService");
		
		upcHttpRequestInterface.loginIn();
		
		String resultJson = upcHttpRequestInterface.sendPostRequest("http://10.1.228.153:8090/ALUPC-jc/?service=ajax&page=upc.product.OfferEditUI&listener=saveObjData&m=81000010&p=upc.product.OfferEditUI&" + parameters, "");
		
		JSONObject relJson = JSONObject.fromObject(resultJson);

		return RETFMT.replace("<DATA>", resultJson.toString());
	}
}

package com.ai.sboss.offeringshelves.respconvertor;

import java.rmi.Naming;

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

public class QueryCatalogRelOfferingOutProcessor implements IBasicOutProcessor{
	
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
		JSONObject param = JSONObject.fromObject(data);		
		Long nodeId = param.getLong("node_id");
//		String parameters = "nodeId=" + nodeId + "&_=1440132888106";
		String parameters = "nodeId=" + nodeId;
		logger.info("parameters------------" + parameters);
		
		//采用RMI的协议方式，调用upc接口，其中，charSpecType的类型写死为ACCESS（解决版本问题）
		UpcHttpRequestInterface upcHttpRequestInterface = (UpcHttpRequestInterface) Naming.lookup("rmi://10.5.1.249:8091/upcHttpRequestService");
		
		upcHttpRequestInterface.loginIn();		
		logger.info("upcHttpRequestInterface------------" + upcHttpRequestInterface);
		String resultJson = upcHttpRequestInterface.sendGetRequest("http://10.1.228.153:8090/ALUPC-jc2/?service=ajax&page=upc.product.RelatedProductOfferingNode&listener=fetchRelOffering&", parameters);
		logger.info("resultjson------------" + resultJson);
		return RETFMT.replace("<DATA>", resultJson.toString());
	}

}

package com.ai.sboss.offeringshelves.respconvertor;
import java.rmi.Naming;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;
import com.ai.sboss.upc.httpRequestInterface.IUpcToCrm;

public class ServiceLaunchOutProcessor implements IBasicOutProcessor {
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
		logger.info("launchData : " + data);
		
		JSONObject tempData = JSONObject.fromObject(data);
		Long objectId = tempData.getLong("objectId");
		
		logger.info("objectId----------------" + objectId);
		
		IUpcToCrm upcToCrm = (IUpcToCrm) Naming.lookup("rmi://10.5.1.245:8091/upcLanuchToCrm");
		String resultJson = upcToCrm.upcToCrm(""+objectId);
		
		return RETFMT.replace("<DATA>", resultJson.toString());
	}

}
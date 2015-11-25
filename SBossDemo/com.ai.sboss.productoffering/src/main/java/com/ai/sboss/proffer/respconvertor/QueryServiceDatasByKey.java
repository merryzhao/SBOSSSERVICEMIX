/**
 * 
 */
package com.ai.sboss.proffer.respconvertor;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.ServiceCalendarMgrInterface.IServiceCalendarMgr;
import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author Chaos
 *
 */
public class QueryServiceDatasByKey implements IBasicOutProcessor {
	private final Logger logger = Logger.getLogger(QueryServiceDatasByKey.class);
	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("=====QueryServiceDatasByKey======");
		Message in = exchange.getIn();
		Message retMessage = exchange.getOut();
		String retString = convert2requst(in.getBody(String.class));
		retMessage.setBody(retString);
		exchange.setIn(retMessage);
		
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.common.interfaces.IBasicOutProcessor#convert2requst(java.lang.String)
	 */
	@Override
	public String convert2requst(String data) throws MalformedURLException, NotBoundException, RemoteException {
		String keywordString = JSONObject.fromObject(data).getString("keyword");
		IServiceCalendarMgr calendarService = (IServiceCalendarMgr)Naming.lookup("rmi://127.0.0.1:8004/ServiceCalendarMgr");
		JSONArray serviceList = calendarService.getServiceDatasAdvByServiceName(keywordString);
		if (serviceList.size() == 0) {
			logger.info("got 0 services");
		}
		JSONArray catalogIdListArray = new JSONArray();
		for (int i = 0; i < serviceList.size(); ++i) {
			logger.info("serviceList->"+i+"->"+serviceList.getJSONObject(i).toString());
			catalogIdListArray.add(serviceList.getJSONObject(i).getLong("catalogId"));
		}
		JSONObject retJsonObject = new JSONObject();
		retJsonObject.put("catalogIdList", catalogIdListArray);
		return retJsonObject.toString();
	}

}

package com.ai.sboss.order.processor;

import java.rmi.Naming;
import java.sql.Timestamp;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

import com.ai.sboss.ServiceCalendarMgrInterface.IServiceCalendarMgr;
import com.ai.sboss.common.interfaces.IBasicOutProcessor;

@Component("saveOrderInstInProcessor")
public class SaveOrderInstInProcessor implements IBasicOutProcessor {
	//private final Logger logger = Logger.getLogger(SaveOrderInstInProcessor.class);
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String retJson = convert2requst(in.getBody(String.class));
		Message retMessage = exchange.getOut();
		if (retJson == null) {
			retMessage.setBody(RETFMT.replace("<DATA>", "{}").replace("<CODE>", "0").replace("<MSG>", "failed"));
		} else {
			retMessage.setBody(RETFMT.replace("<DATA>", retJson).replace("<CODE>", "1").replace("<MSG>", "sucess"));
		}
		exchange.setIn(retMessage);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		JSONObject bodyJsonObject = JSONObject.fromObject(data);
		if (bodyJsonObject.getString("hub_code").equals("0")) {
			return null;
		}
		Long customerOrderId = bodyJsonObject.getLong("data");
		Long productOfferingId = bodyJsonObject.getLong("service_id");
		Timestamp serviceExecTime = new Timestamp(bodyJsonObject.getLong("service_exectime"));
		Long customerId = bodyJsonObject.getLong("userId");
		IServiceCalendarMgr calendarService = (IServiceCalendarMgr)Naming.lookup("rmi://127.0.0.1:8004/ServiceCalendarMgr");
		boolean retboolean = calendarService.saveServiceProcessInst(customerId, customerOrderId, productOfferingId, serviceExecTime);
		JSONObject retJsonObject = new JSONObject();
		retJsonObject.put("order_id", customerOrderId);
		if (!retboolean) {
			retJsonObject.put("result_code", 0);
		}
		retJsonObject.put("result_code", 1);
		return retJsonObject.toString();
	}

}

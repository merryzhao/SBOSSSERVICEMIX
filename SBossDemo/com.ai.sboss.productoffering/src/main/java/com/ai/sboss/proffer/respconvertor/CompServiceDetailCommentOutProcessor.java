package com.ai.sboss.proffer.respconvertor;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;
import net.sf.json.JSONObject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

public class CompServiceDetailCommentOutProcessor
  implements IBasicOutProcessor
{
  protected Logger logger = Logger.getLogger(getClass());
  private static final String RESULT_FMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":\"<RET_MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";

  public void process(Exchange exchange)
    throws Exception
  {
    String data = (String)exchange.getIn().getBody(String.class);
    String returnJson = convert2requst(data);
    exchange.getIn().setBody(null);
    exchange.getIn().setBody(returnJson);
  }

  public String convert2requst(String data)
    throws Exception
  {
    this.logger.info("CompServiceDetailCommentOutProcessor====>" + data);
    JSONObject tempData = new JSONObject();
    Double service_rating = Double.valueOf(0.0D);
    JSONObject resultJsonObj = JSONObject.fromObject(data);
    if (resultJsonObj.containsKey("service_rating")) {
      String service_rating_temp = resultJsonObj.getString("service_rating");
      tempData = resultJsonObj.getJSONObject("data");
      service_rating = Double.valueOf(Double.parseDouble(service_rating_temp));
    }
    tempData.put("service_rating", service_rating);
    resultJsonObj.remove("service_rating");

    return "{\"data\":<DATA>,\"desc\":{\"result_code\":<RET_CODE>,\"result_msg\":\"<RET_MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}".replace("<DATA>", resultJsonObj.toString()).replace("<RET_CODE>", Integer.toString(1)).replace("<RET_MSG>", "success");
  }
}
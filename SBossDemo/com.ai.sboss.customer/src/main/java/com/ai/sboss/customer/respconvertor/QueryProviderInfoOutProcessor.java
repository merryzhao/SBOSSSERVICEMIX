/**
 * 
 */
package com.ai.sboss.customer.respconvertor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author monica
 *
 */
public class QueryProviderInfoOutProcessor implements IBasicOutProcessor{

	private final static String CRMFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	protected Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Message inMessage = exchange.getIn();
		logger.info("inMessage====>" + inMessage);
		String ret = convert2requst(inMessage.getBody(String.class));
		inMessage.setBody(ret);
		logger.info("inMessage====>" + inMessage);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		// TODO Auto-generated method stub
		if (StringUtils.isEmpty(data)) {
			return CRMFMT.replace("<DATA>", "").replace("<CODE>", "0").replace("<MSG>", "failed");
		}
		JSONObject providerJson = JSONObject.fromObject(data).getJSONObject("data");
		JSONObject provider = new JSONObject();
		int user_sex = providerJson.getInt("userSex");
		logger.info("queryProviderInfoOutProcessor.user_sex=====>" + user_sex);
		String user_birthday = providerJson.getString("userBirthday");
		String user_portrait_icon = providerJson.getString("portraitUrl");
		String user_name = providerJson.getString("providerName");
		String user_phone = providerJson.getString("userPhone");
		
		//现在provider信息不要求包含地址信息，后期需要考虑
		JSONArray user_address = new JSONArray();
		provider.put("user_sex", user_sex);
		provider.put("user_birthday", user_birthday);
		provider.put("user_portrait_icon", user_portrait_icon);
		provider.put("user_name", user_name);
		provider.put("user_phone", user_phone);
		provider.put("user_address", user_address);
		logger.info("user_address=====>" + user_address);
		return CRMFMT.replace("<DATA>", provider.toString()).replace("<CODE>", "1").replace("<MSG>", "success");
	}

}

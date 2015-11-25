/**
 * 查询客户信息
 */
package com.ai.sboss.customer.respconvertor;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author monica
 *
 */
public class AcquireCustomerOutProcessor implements IBasicOutProcessor{

	private final static String CRMFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":<CODE>,\"result_msg\":\"<MSG>\",\"data_mode\":\"0\",\"digest\":\"\"}}";
	protected Logger logger = Logger.getLogger(this.getClass());
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
		logger.info("data->" + data);
		JSONObject finalJson = new JSONObject();
		JSONObject resultData = JSONObject.fromObject(data).getJSONObject("data");
		
		JSONObject partyJson = resultData.getJSONObject("partyRole").getJSONObject("party");
		JSONObject individualJson = partyJson.getJSONObject("individual");
		//获取性别和生日数据
		String temp = individualJson.getString("gender");
		int user_sex;
		String user_birthday = "";
		if(temp.equals("M")){
			user_sex = 1;
		} else if(temp.equals("F")){
			user_sex = 2;
		} else{
			user_sex = 0;
		}
		finalJson.put("user_sex", user_sex);
		user_birthday = individualJson.getString("birthDate");
		finalJson.put("user_birthday", user_birthday);
		
		//获取用户头像链接地址
		String user_portrait_icon = resultData.getString("portraitUrl");
		finalJson.put("user_portrait_icon", user_portrait_icon);
		
		//获取用户账户名称
		String user_name = partyJson.getString("partyName");
		finalJson.put("user_name", user_name);
		
		//获取用户联系媒介信息
		JSONArray partyRoleContactMediumAssocArray = resultData.getJSONObject("partyRole").getJSONArray("partyRoleContactMediumAssocList");
		String user_phone = "";
		
		List<Long> contactAddressList = new ArrayList<Long>();
		JSONArray contactAddress = new JSONArray();
		JSONObject addressJsonObj = new JSONObject();
		
		for(int i = 0; i < partyRoleContactMediumAssocArray.size(); i++){
			JSONObject singleContactMediumAssoc = partyRoleContactMediumAssocArray.getJSONObject(i);
			JSONObject singleContactMedium = singleContactMediumAssoc.getJSONObject("contactMedium");
			if(singleContactMedium.getString("contMedType").equals("1003")){
				//获取到用户的电话号码
				user_phone = singleContactMedium.getString("contNumber");
			}
			if(singleContactMedium.getString("contMedType").equals("1000")){
				//获取用户的地址信息列表，返回的是place_id的列表
				Long tempAddress = singleContactMedium.getLong("contNumber");
				contactAddressList.add(tempAddress);
			}
		}
		
		addressJsonObj.put("placeIds", contactAddressList);
		contactAddress.add(addressJsonObj);
		
		
		finalJson.put("user_phone", user_phone);
		finalJson.put("contact_address", contactAddress);
		
//		return finalJson.toString();
		return CRMFMT.replace("<DATA>", finalJson.toString()).replace("<CODE>", "1").replace("<MSG>", "success");
	}
}

package com.ai.sboss.customer.fake;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Properties;

import net.minidev.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.StreamCache;
import org.apache.log4j.Logger;


public class QueryCustomerDataFake {
	Logger logger = Logger.getLogger(QueryCustomerDataFake.class);
	private final String CUSTOMER_DETAIL = "data/fakedata/accountlist.properties";
	private final String CRMFMT="{\"data\":<DATA>,\"hub_code\":<CODE>,\"hub_msg\":<MSG>}";
  
	//private final String CUSTOMER_DETAIL = "E:/user/20150525_Activiti/customerinfo.properties";
	public String getResponse(StreamCache isc) throws Exception {

		return "";
	}
	
	public String readValue(String tokenid) throws Exception {

		Properties prop = new Properties();
		//InputStream in = Object.class.getResourceAsStream(CUSTOMER_DETAIL);	
		InputStream in = new BufferedInputStream(new FileInputStream(CUSTOMER_DETAIL));
		String retCustomerId = null;
		try {	 
			prop.load(in);			
			retCustomerId = prop.getProperty(tokenid).trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("catched customer id:" + retCustomerId);
		if (retCustomerId == null) {
			logger.error("not found customer id ");
			return null;
		}

		return retCustomerId;
	}
	
	/**
	 * 获取用户账户列表。<br>
	 * @param null
	 * @return 账户列表JSONArray
	 * @throws null
	 */
	private JSONArray queryUserAccountList() {
		JSONArray accountList = new JSONArray();
		try {
		LineNumberReader linereader = new LineNumberReader(new InputStreamReader(new FileInputStream(CUSTOMER_DETAIL), "UTF-8"));
		String content = "";
		while ((content = linereader.readLine()) != null) {
			if (content.startsWith("#")) {
				continue;
			}
			JSONObject accountinfo = JSONObject.fromObject(content);
			accountList.add(accountinfo);
		}
		linereader.close();
		} catch (IOException e) {
			logger.error(CUSTOMER_DETAIL+e.toString());
		}
		return accountList;
	}
	
	/**
	 * 根据公众号ID查询用户账户信息。<br>
	 * @param isc 原始String
	 * @return 账户信息
	 * @throws Exception
	 */
	public String queryUserInfoByOpenId(String isc) throws Exception {
		JSONObject jsonInput = JSONObject.fromObject(isc);
		String wx_openid = jsonInput.getString("wx_openid");
		
		JSONArray userlist = queryUserAccountList();
		for (int i = 0; i < userlist.size(); ++i) {
			JSONObject accountinfObject = (JSONObject) userlist.get(i);
			if (accountinfObject.getString("openid").equals(wx_openid)) {
				return CRMFMT.replace("<DATA>", accountinfObject.toString()).replace("<CODE>", "1").replace("<MSG>", "\"\"");
			}
		}
		return CRMFMT.replace("<DATA>", new JSONObject().toString()).replace("<CODE>", "0").replace("<MSG>", "not found");
	}
	
	/**
	 * 根据公众号ID查询用户账户信息。<br>
	 * @param isc 原始String
	 * @return 账户信息
	 * @throws Exception
	 */
	public String queryUserInfoByUserId(String isc) throws Exception {
		JSONObject jsonInput = JSONObject.fromObject(isc);
		String userId = jsonInput.getString("user_id");
		
		JSONArray userlist = queryUserAccountList();
		for (int i = 0; i < userlist.size(); ++i) {
			JSONObject accountinfObject = (JSONObject) userlist.get(i);
			if (accountinfObject.getString("id").equals(userId)) {
				return CRMFMT.replace("<DATA>", accountinfObject.toString()).replace("<CODE>", "1").replace("<MSG>", "\"\"");
			}
		}
		return CRMFMT.replace("<DATA>", new JSONObject().toString()).replace("<CODE>", "0").replace("<MSG>", "not found");
	}
	
	/**
	 * 根据用户手机号查询用户账户信息。<br>
	 * @param isc 原始String
	 * @return 账户信息
	 * @throws Exception
	 */
	public String queryUserInfoByMobileNumber(String isc) throws Exception {
		JSONObject jsonInput = JSONObject.fromObject(isc);
		String mobile = jsonInput.getString("phone");
		
		JSONArray userlist = queryUserAccountList();
		for (int i = 0; i < userlist.size(); ++i) {
			JSONObject accountinfObject = (JSONObject) userlist.get(i);
			if (accountinfObject.getString("mobilenumber").equals(mobile)) {
				return CRMFMT.replace("<DATA>", accountinfObject.toString()).replace("<CODE>", "1").replace("<MSG>", "\"\"");
			}
		}
		return CRMFMT.replace("<DATA>", new JSONObject().toString()).replace("<CODE>", "0").replace("<MSG>", "not found");
	}


}

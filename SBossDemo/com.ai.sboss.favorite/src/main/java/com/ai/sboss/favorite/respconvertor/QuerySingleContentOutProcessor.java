/**
 * 
 */
package com.ai.sboss.favorite.respconvertor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author monica
 *
 */
public class QuerySingleContentOutProcessor implements IBasicOutProcessor{

	Logger logger = Logger.getLogger(QuerySingleContentOutProcessor.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Message in = exchange.getIn();
		String ret = convert2requst(in.getBody(String.class));
		in.setBody(ret);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		// TODO Auto-generated method stub
		
		logger.info("QuerySingleContentOutProcessor data--->" + data);
		//定义返回结果
		JSONObject singleContentResult = new JSONObject();
		
		JSONObject dataJson = JSONObject.fromObject(data);
		JSONObject tempJson = dataJson.getJSONObject("data");
		/**
		 * 获取content_id在文件服务器对应的url地址信息
		 */
		String contentUrl = tempJson.getString("filePosition");
		/**
		 * 根据contentUrl从文件服务器读取文件
		 */
	      try {
	    	  URL url = new URL(contentUrl);
	          HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    	  connection.connect();
	    	  BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
	    	  String lines = null;
	    	  while((lines = reader.readLine()) != null){
//	    		  logger.info("QuerySingleContentOutProcessor lines--->" + lines);
	    		  JSONObject lineJson = JSONObject.fromObject("{" + lines + "}");
	    		  Long contentId = lineJson.getLong("contentId");
	    		  String title = lineJson.getString("title");
	    		  String content = lineJson.getString("content");
	    		  
	    		  singleContentResult.put("contentId", contentId);
	    		  singleContentResult.put("title", title);
	    		  singleContentResult.put("content", content);
	    	  }
	    	  reader.close();
	    	  connection.disconnect();
	      } catch (Exception e) {
	          e.printStackTrace();
	      }
	      
//	      logger.info("QuerySingleContentOutProcessor singleContentResult--->" + singleContentResult.toString());
	      return singleContentResult.toString();
	}

}

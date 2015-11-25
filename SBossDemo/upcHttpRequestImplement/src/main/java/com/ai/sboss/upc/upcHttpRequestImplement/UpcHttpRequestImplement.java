/**
 * 
 */
package com.ai.sboss.upc.upcHttpRequestImplement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;





import com.ai.sboss.upc.httpRequestInterface.UpcHttpRequestInterface;

/**
 * @author idot
 *
 */
@SuppressWarnings("serial")
public class UpcHttpRequestImplement extends UnicastRemoteObject implements UpcHttpRequestInterface {
	
//	protected Logger logger = Logger.getLogger(this.getClass());
	//定义一个全局的sessionid
	public static String sessionId = "";

	
	public UpcHttpRequestImplement() throws RemoteException {
		
		// TODO Auto-generated constructor stub
	}

	public String sendGetRequest(String url, String param) throws Exception, RemoteException {
		// TODO Auto-generated method stub
		
//    		if("".equals(sessionId)){
    		sessionId = getSessionId();
//    		}
		
	        String result = "";
	        BufferedReader in = null;
	        try {
	            String urlNameString = url + param;
	            URL realUrl = new URL(urlNameString);
	            // 打开和URL之间的连接
	            URLConnection connection = realUrl.openConnection();
	            // 设置通用的请求属性
	            connection.setRequestProperty("accept", "*/*");
	            connection.setRequestProperty("connection", "Keep-Alive");
	            connection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; Trident/7.0; rv:11.0) like Gecko");
	            connection.setRequestProperty("X-Requested-With","XMLHttpRequest");
	            connection.setRequestProperty("Authorization","Basic MDF1cGM6MTIzNDU2");
	            connection.setRequestProperty("Cookie","base.login.name=01upc; JSEEIONID=" + sessionId);

	            // 建立实际的连接
	            connection.connect();
	            // 获取所有响应头字段
	            Map<String, List<String>> map = connection.getHeaderFields();
	            // 遍历所有的响应头字段
	            for (String key : map.keySet()) {
	                System.out.println(key + "--->" + map.get(key));
	            }
	            // 定义 BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            System.out.println("发送GET请求出现异常！" + e);
	            e.printStackTrace();
	        }
	        // 使用finally块来关闭输入流
	        finally {
	            try {
	                if (in != null) {
	                    in.close();
	                }
	            } catch (Exception e2) {
	                e2.printStackTrace();
	            }
	        }
	        System.out.println("result---------" + result);
	        return result;
	    }

	public String sendPostRequest(String url, String param) throws Exception, RemoteException{
		// TODO Auto-generated method stub
		 	PrintWriter out = null;
	        BufferedReader in = null;
	        String result = "";
	        
	        if("".equals(sessionId)){
	        	sessionId = getSessionId();
	        }
	        
	        try {
	            URL realUrl = new URL(url);
	            // 打开和URL之间的连接
	            URLConnection conn = realUrl.openConnection();
	            // 设置通用的请求属性
	            conn.setRequestProperty("accept", "text/plain, */*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("user-agent",
	                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; Trident/7.0; rv:11.0) like Gecko");
	            conn.setRequestProperty("X-Requested-With","XMLHttpRequest");
	            conn.setRequestProperty("Authorization","Basic MDF1cGM6MTIzNDU2");
	            conn.setRequestProperty("Cookie","base.login.name=01upc; JSEEIONID=" + sessionId);
	            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
	            conn.setRequestProperty("Accept-Language", "zh-Hans-CN, zh-Hans; q=0.8, en-US; q=0.5, en; q=0.3");
	            conn.setRequestProperty("Cache-Control", "no-cache");
	            conn.setRequestProperty("Content-Type", ":application/x-www-form-urlencoded; charset=UTF-8");
	          
	            // 发送POST请求必须设置如下两行
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            //logger.info("cookie---------------"+conn.getRequestProperty("Cookie"));
	            // 获取URLConnection对象对应的输出流
	            out = new PrintWriter(conn.getOutputStream());
	            // 发送请求参数
	            out.print(param);
	            // flush输出流的缓冲
	            out.flush();
	            // 定义BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(
	                    new InputStreamReader(conn.getInputStream()));
	            //logger.info("in---------------"+in);
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            System.out.println("发送 POST 请求出现异常！"+e);
	            e.printStackTrace();
	        }
	        //使用finally块来关闭输出流、输入流
	        finally{
	            try{
	                if(out!=null){
	                    out.close();
	                }
	                if(in!=null){
	                    in.close();
	                }
	            }
	            catch(IOException ex){
	                ex.printStackTrace();
	            }
	        }
	        return result;
	}

	public String getSessionId() throws Exception {
		// TODO Auto-generated method stub
		 URL geturl = new URL("http://10.1.228.153:8090/ALUPC-jc2/image?width=70&height=21&length=4&t=0.02837374759837985"); 
	   	 HttpURLConnection conn = (HttpURLConnection)geturl.openConnection(); 
	   	 conn.setConnectTimeout(5000); 
	   	 conn.setRequestMethod("GET");

	   	 String cookie = conn.getHeaderField("set-cookie");
	   	 System.out.println(cookie);
	   	 sessionId=cookie.split(";")[0].split("=")[1];
	   	 return sessionId;
	}

	public String loginIn() throws Exception {
		// TODO Auto-generated method stub
		String param="&PASSWORD=123456&STAFF_ID=01UPC&VERIFY_CODE=0";
    	String loginIn = sendPostRequest("http://10.1.228.153:8090/ALUPC-jc2/?service=ajax&page=Home&listener=loginHome&MENU_ID=&p=Home"+param, "");
    	return loginIn;
	}
	
}

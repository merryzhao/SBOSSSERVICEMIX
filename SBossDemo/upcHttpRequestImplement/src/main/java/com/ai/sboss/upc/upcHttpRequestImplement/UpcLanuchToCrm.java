package com.ai.sboss.upc.upcHttpRequestImplement;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

import com.ai.sboss.upc.httpRequestInterface.IUpcToCrm;

@SuppressWarnings("serial")
public class UpcLanuchToCrm extends UnicastRemoteObject implements IUpcToCrm {
	 public UpcLanuchToCrm() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	String sessionId="";
    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     * @throws Exception 
     */
    public String sendGet(String url, String param) throws Exception {
    	
    	String cookies=getSessionId();
    	String cookie=cookies.split(";")[0].split("=")[1];
    	System.out.println(cookie);
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; Trident/7.0; rv:11.0) like Gecko");
            connection.setRequestProperty("X-Requested-With","XMLHttpRequest");
            connection.setRequestProperty("Authorization","Basic MDF1cGM6MTIzNDU2");
            connection.setRequestProperty("Cookie","base.login.name=01upc; JSESSIONID="+cookie);
            
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
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     * @throws Exception 
     */
    public String sendPost(String url, String param) throws Exception {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
//        if("".equals(sessionId)){
        	sessionId=getSessionId();
        	System.out.println("sesessionId==========================>"+sessionId);
//        }
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
//            conn.setRequestProperty("Cookie","base.login.name=01upc; JSESSIONID="+sessionId);//base.login.name=01upc; JSESSIONID=9EC8E493136A996CEA3F53C7B065DE30
            conn.setRequestProperty("Cookie","base.login.name=01upc; JSESSIONID="+sessionId);
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Accept-Language", "zh-Hans-CN, zh-Hans; q=0.8, en-US; q=0.5, en; q=0.3");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", ":application/x-www-form-urlencoded; charset=UTF-8");
//            conn.setRequestProperty("Referer", "http://10.1.228.153:8090/ALUPC-jc/?service=page/upc.product.ProductEditUI&UITemplateId=26&opType=new&itemType=ProductSpecification&dataFlag=&returnType=&m=81000011&p=upc.product.ProductEditUI#nogo");
            //Cookie: base.login.name=01upc; JSESSIONID=2371A115AC64661C55BBF1CE8648319E
            //Cookie: base.login.name=01upc; JSESSIONID=2371A115AC64661C55BBF1CE8648319E
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
//            out.write("objData = %22__OP_FLAG__%22%3a%22new%22%2c%22productSpecRelationships%22%3a%5b%5d%2c%22relateCharacteristic%22%3a%5b%7b%22__OP_FLAG__%22%3a%22new%22%2c%22CharacteristicId%22%3a%222041%22%2c%22value%22%3a%2212345678%22%7d%5d%2c%22productSpecificationType%22%3a%22ACCESS%22%2c%22prodTypeName%22%3a%22Access%22%2c%22productSpecificationName%22%3a%22123%22%2c%22productSpecificationCode%22%3a%22456%22%2c%22description%22%3a%22789%22");
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
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
    
    public  String upcToCrm (String objectId) throws Exception, RemoteException {
    	//登陆
    	String param="&PASSWORD=123456&STAFF_ID=01UPC&VERIFY_CODE=0";
    	String loginIn = sendPost("http://10.1.228.153:8090/ALUPC-jc2/?service=ajax&page=Home&listener=loginHome&MENU_ID=&p=Home"+param, "");
    	System.out.println("loginIn================================>"+loginIn);
    	
    	String parameters="&targetEnvs={\"items\":{\"itemEnvs\":[{\"envId\":\"2002\"}],\"flag\":true}}&launchToKonwledge=0&objectId="+objectId+"&typeId=101&batch=&objectIds=&releaseType=0&releaseDate=";
    	String resultJson = sendPost("http://10.1.228.153:8090/ALUPC-jc2/?service=ajax&page=upc.product.ProdOfferingManm&listener=addToLaunchQueue&m=81000010&p=upc.product.ProdOfferingManm"+parameters, "");
       
    	System.out.println("resultJson========================>"+resultJson);
    
    	return resultJson;
    }
    
    public String getSessionId() throws Exception{
    	URL geturl = new URL("http://10.1.228.153:8090/ALUPC-jc2/image?width=70&height=21&length=4&t=0.02837374759837985"); 
   	 HttpURLConnection conn = (HttpURLConnection)geturl.openConnection(); 
   	 conn.setConnectTimeout(5000); 
   	 conn.setRequestMethod("GET");

   	 String cookie = conn.getHeaderField("set-cookie");  
   	 System.out.println(cookie);
   	 sessionId=cookie.split(";")[0].split("=")[1];
   	 return sessionId;
    }
}
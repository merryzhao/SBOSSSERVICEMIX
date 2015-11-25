package com.ai.sboss.favorite.respconvertor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;
import com.ai.sboss.datanalysis.core.OfferingRecommendorInterface;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

//convert function not finished

public class QueryServicesByContentOutProcessor implements IBasicOutProcessor {
	protected Logger logger = Logger.getLogger(this.getClass());
	private final static String RETFMT = "{\"data\":<DATA>,\"desc\":{\"result_code\":1,\"result_msg\":\"success\",\"data_mode\":\"0\",\"digest\":\"\"}}";


	@Override
	public void process(Exchange exchange) throws Exception {
		Message inMessage = exchange.getIn();
		String ret = convert2requst(inMessage.getBody(String.class));
		inMessage.setBody(ret);
	}

	@Override
	public String convert2requst(String data) throws Exception {
		//获取前端传入的参数，这里写死数据
		JSONObject param = JSONObject.fromObject(data);		
		Long favoriteId = param.getLong("favorite_id");
		/*Long favoriteId = 10001L;*/
		

		//将存放在hdfs中的分析结果文件读取到内存中，采用RMI的协议方式（解决版本问题）		
//		HadoopOperations operations = (HadoopOperations) Naming.lookup("rmi://localhost:8092/hadoopservice");
//		List<String> resultJsonAll = operations.readHDFSListAll("hdfs://10.1.228.151:9000","/user/aihadoop/data/crm/offering/2015-06-10");				
		List<JSONObject> resultJsons = new ArrayList<JSONObject>();
		JSONObject finalJson = new JSONObject();
		
		//将取出的数据以及查询的favorite_id传给分析器进行分析
		String fakeContent = favoriteId.toString()+"\t"+"fakecontentvalues";
		
		
		//String fileName = "E:\\work\\Git\\SBossDemo\\fileProcessorImpl\\SpiderFiles\\sbssGet\\"+favoriteId+".spider";
		String fileName = "E:\\temp\\analysisimplementation\\contentJson.txt";
		List<String> fileLines = new ArrayList<String>();
		File file = new File(fileName);
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(file));
        JSONArray contentJson = JSONArray.fromObject(reader.readLine());
//		LineNumberReader linereader = new LineNumberReader(new FileReader(
//				fileName));
//		String contentJson= linereader.readLine();
		System.out.println("before--->"+contentJson.toString());
//		IdataAnalysisPkgSV analysisPkg = (IdataAnalysisPkgSV) Naming.lookup("rmi://localhost:8090/dataAnalysis");
//		String contentRelServices = analysisPkg.contentRelServiceAnalysis(fakeContent, resultJsonAll);
		
		OfferingRecommendorInterface offerRecommendor = (OfferingRecommendorInterface) Naming
				.lookup("rmi://0.0.0.0:8093/AnalysisService");
		String contentRelServices = offerRecommendor.contentRelOfferingAnalysis(contentJson);
		//logger.info("after analysised==>"+contentRelServices);
		
/*		//遍历内存中的分析结果文件，如果发现分析结果json文件的favorite_id与前端传入的一致，则取出后面的offering_list
		JSONObject offerList = new JSONObject();
		String offeringList = null;
		for(String resultJson : resultJsonAll){
			String[] favoriteStr = resultJson.split("\t");
			String contentId = favoriteStr[0].trim();
			logger.info("contentId************>"+contentId);
			if(contentId.equals(favoriteId.toString())){
				offeringList = favoriteStr[1];
				break;
			}
		}*/
		//offerList = JSONObject.fromObject(offeringList);
//		String[] favoriteStr = contentRelServices.split("\t");
//		JSONObject offerList = JSONObject.fromObject(favoriteStr[1]);
		JSONObject relRes = JSONObject.fromObject(contentRelServices);
		JSONArray offerDetailList = new JSONArray();
		offerDetailList = relRes.getJSONArray("offeringList");
				
		//循环遍历对应favorite_id的所有offering，取出相关数据，构造成前端需要返回的数据格式
		for(int index = 0; index < offerDetailList.size(); index ++){
			JSONObject offer = JSONObject.fromObject(offerDetailList.getJSONObject(index).getString("data"));
			String service_id = offer.getString("offerId");
			String service_name = offer.getString("offeringName");
			String service_intro = null;
			String service_price = null;
			String service_thumbnail_url = null;
			String service_tag_temp = null;
			JSONArray charList = offer.getJSONArray("productOfferingSpecCharList");
			for(int i = 0; i < charList.size(); i++){
				JSONArray valList = charList.getJSONObject(i).getJSONArray("productOfferingSpecCharValList");
				for (int j = 0; j < valList.size(); j++) {
					if(valList.getJSONObject(j).getJSONObject("specCharValId").getJSONObject("specChar").getString("charSpecName").equals("description")){
						service_intro = valList.getJSONObject(j).getJSONObject("specCharValId").getString("value");
						continue;
					}else if(valList.getJSONObject(j).getJSONObject("specCharValId").getJSONObject("specChar").getString("charSpecName").equals("price")){
						service_price = valList.getJSONObject(j).getJSONObject("specCharValId").getString("displayVal");
						continue;
					}else if(valList.getJSONObject(j).getJSONObject("specCharValId").getJSONObject("specChar").getString("charSpecName").equals("intro_url")){
						service_thumbnail_url = valList.getJSONObject(j).getJSONObject("specCharValId").getString("value");
						continue;
					}else if(valList.getJSONObject(j).getJSONObject("specCharValId").getJSONObject("specChar").getString("charSpecName").equals("tag_name")){
						service_tag_temp = valList.getJSONObject(j).getJSONObject("specCharValId").getString("value");
						continue;
					}
				}
			}
			String collected_flag = "0";
			JSONArray service_tag = new JSONArray();
			Map<String,String> tag_temp = new HashMap<String,String>();
			tag_temp.put("tag_name", service_tag_temp);
			service_tag = JSONArray.fromObject(tag_temp);
					
			//取出前端要求返回的字段后，拼装成json格式
			JSONObject returnjson = new JSONObject();
			returnjson.put("service_id", service_id);
			returnjson.put("service_name", service_name);
			returnjson.put("service_intro", service_intro);
			returnjson.put("service_price", service_price);
			returnjson.put("service_thumbnail_url", service_thumbnail_url);
			returnjson.put("service_tag", service_tag);
			returnjson.put("collected_flag", collected_flag);
			resultJsons.add(returnjson);
		}
		finalJson.put("content_services", resultJsons);
		return RETFMT.replace("<DATA>", finalJson.toString());
	}
}

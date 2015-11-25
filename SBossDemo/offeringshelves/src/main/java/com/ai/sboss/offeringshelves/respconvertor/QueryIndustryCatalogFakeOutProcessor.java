/**
 * 
 */
package com.ai.sboss.offeringshelves.respconvertor;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.ai.sboss.common.interfaces.IBasicOutProcessor;

/**
 * @author idot
 *
 */
public class QueryIndustryCatalogFakeOutProcessor implements IBasicOutProcessor{

	private final static String RET_FMT = "{\"data\":<DATA>, \"desc\":{\"result_code\":1,\"result_msg\":\"success\",\"data_mode\":0,\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryIndustryCatalogFakeOutProcessor.class);
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
		//获取前端输入的参数，如果catalogId为100000000027,则读取personal_care文件；若为100000000024,则读取vocational_training文件；若为100000000030，则读取care_training文件
		String FAKEFILE = "";
		String IndustryCatalog = "";
		JSONObject param = JSONObject.fromObject(data);		
		Long catalogId = param.getLong("catalogId");
		if(catalogId.equals(100000000027L)){
//			FAKEFILE = "E:/files/personal_care.txt";
			FAKEFILE = "data/fakedata/offeringshelves/personal_care.txt";
		}
		else if(catalogId.equals(100000000024L)){
//			FAKEFILE = "E:/files/vocational_training.txt";
			FAKEFILE = "data/fakedata/offeringshelves/vocational_training.txt";
		}
		else if(catalogId.equals(100000000030L)){
			FAKEFILE = "data/fakedata/offeringshelves/care_training.txt";
		}
		try {
			LineNumberReader linereader = new LineNumberReader(new FileReader(
					FAKEFILE));
			IndustryCatalog = linereader.readLine();
			linereader.close();
		} catch (IOException e) {
			logger.error(FAKEFILE+e.toString());
		}
		return RET_FMT.replace("<DATA>", IndustryCatalog.toString());
	}
	
}

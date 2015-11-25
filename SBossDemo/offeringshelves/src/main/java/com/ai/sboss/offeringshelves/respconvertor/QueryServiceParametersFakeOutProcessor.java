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
public class QueryServiceParametersFakeOutProcessor implements IBasicOutProcessor{

	private final static String RET_FMT = "{\"data\":<DATA>, \"desc\":{\"result_code\":1,\"result_msg\":\"success\",\"data_mode\":0,\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryServiceParametersFakeOutProcessor.class);
	
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
		//获取前端输入的参数，如果catalogNodeId为1965,则读取Manicure文件；若为1997,则读取Education文件；若为2015,则读取Education_Manicure
		String FAKEFILE = "";
		String serviceParameters = "";
		JSONObject param = JSONObject.fromObject(data);		
		Long catalogNodeId = param.getLong("catalogNodeId");
		if(catalogNodeId == 1965){
//			FAKEFILE = "E:/files/manicure.txt";
			FAKEFILE = "data/fakedata/offeringshelves/manicure.txt";
		}
		else if(catalogNodeId == 1997){
//			FAKEFILE = "E:/files/education.txt";
			FAKEFILE = "data/fakedata/offeringshelves/education.txt";
		}
		else if(catalogNodeId == 2015){
//			FAKEFILE = "E:/files/education_manicure.txt";
			FAKEFILE = "data/fakedata/offeringshelves/education_manicure.txt";
		}
		try {
			LineNumberReader linereader = new LineNumberReader(new FileReader(
					FAKEFILE));
			serviceParameters = linereader.readLine();
			linereader.close();
		} catch (IOException e) {
			logger.error(FAKEFILE+e.toString());
		}
		return RET_FMT.replace("<DATA>", serviceParameters.toString());
	}

}

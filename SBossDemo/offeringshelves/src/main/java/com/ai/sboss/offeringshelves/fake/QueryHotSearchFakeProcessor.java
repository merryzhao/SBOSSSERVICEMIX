/**
 * 
 */
package com.ai.sboss.offeringshelves.fake;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

/**
 * @author idot
 *
 */
public class QueryHotSearchFakeProcessor implements Processor{

	private final static String FAKEFILE = "data/fakedata/offeringshelves/hotsearchList.txt";
//	private final static String FAKEFILE = "E:/files/hotsearchList.txt";
	private final static String RET_FMT = "{<DATA>, \"desc\":{\"result_code\":1,\"result_msg\":\"success\",\"data_mode\":0,\"digest\":\"\"}}";
	Logger logger = Logger.getLogger(QueryHotSearchFakeProcessor.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		
		String hotSearchList = "";
		try {
			LineNumberReader linereader = new LineNumberReader(new FileReader(
					FAKEFILE));
			hotSearchList = linereader.readLine();
			linereader.close();
		} catch (IOException e) {
			logger.error(FAKEFILE+e.toString());
		}
		
		exchange.getIn().setBody(RET_FMT.replace("<DATA>", hotSearchList));
	}

}

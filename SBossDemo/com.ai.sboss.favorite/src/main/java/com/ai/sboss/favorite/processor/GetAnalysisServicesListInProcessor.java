package com.ai.sboss.favorite.processor;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;

import com.ai.sboss.common.interfaces.IBasicInProcessor;

public class GetAnalysisServicesListInProcessor implements IBasicInProcessor {

	@Override
	public void process(Exchange exchange) throws Exception {
		String args = "";
		String ret = queryServiceList(args);
		exchange.getIn().setBody(ret);
	}

	private String queryServiceList(String args) throws Exception {
		//IdataAnalysisPkgSV analysis = (IdataAnalysisPkgSV)Naming.lookup("rmi://localhost:8090/dataAnalysis");
		/*analysis.contentRelServiceAnalysis(content, serviceList);*/
		return null;
	}

	@Override
	public JSONObject getInputParam(Exchange exchange) throws Exception {
		return null;
	}

}

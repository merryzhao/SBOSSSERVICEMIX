package com.ai.sboss.datanalysis.core;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ProposalRecommendorInterface extends Remote{
	/**
	 * 将根据内容分析出的服务列表构造成服务方案
	 * @param contentRelOfferingJson
	 * @return
	 */
	public String contentRelProposalAnalysis(String contentRelOfferingJson)throws Exception, RemoteException;
}

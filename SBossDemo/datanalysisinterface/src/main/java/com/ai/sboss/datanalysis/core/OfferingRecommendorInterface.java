package com.ai.sboss.datanalysis.core;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.sf.json.JSONArray;

public interface OfferingRecommendorInterface extends Remote{
	
	public String contentRelOfferingAnalysis(JSONArray contentsJson)throws Exception, RemoteException;
}

package com.ai.sboss.datanalysisimpl.core;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import com.ai.sboss.datanalysis.core.OfferingRecommendorInterface;
import com.ai.sboss.datanalysis.core.ProposalRecommendorInterface;

public class AnalysisServStart {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
			OfferingRecommendorInterface dataAnalysis = new TextRelOfferingRecommendorImpl();
			LocateRegistry.createRegistry(8093);
			Naming.bind("//0.0.0.0:8093/AnalysisService", dataAnalysis);
			
			ProposalRecommendorInterface proposalRecommendor = new TextRelProposalRecommendorImpl();
			LocateRegistry.createRegistry(8094);
			Naming.bind("//0.0.0.0:8094/proposalRecommendor", proposalRecommendor);
			
			System.out.println("connect sucess!");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}

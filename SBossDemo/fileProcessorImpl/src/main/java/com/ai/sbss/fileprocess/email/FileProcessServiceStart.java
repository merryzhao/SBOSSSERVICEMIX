package com.ai.sbss.fileprocess.email;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import fileProcessorInterface.fileProcessorInterface.IArticleProcessor;
import fileProcessorInterface.fileProcessorInterface.IProcessMail;
import fileProcessorInterface.fileProcessorInterface.ISendMail;



public class FileProcessServiceStart {
	public static void main(String[] arg){
		try{
			IArticleProcessor arProcessor = new ArticleSpider();
			LocateRegistry.createRegistry(8001);
			Naming.bind("//0.0.0.0:8001/fileprocess", arProcessor);	
			
			IProcessMail mailProcessor = new ProcessMail(null);
			LocateRegistry.createRegistry(8002);
			Naming.bind("//0.0.0.0:8002/mailprocess", mailProcessor);
			
			ISendMail sendMail = new SendMail();
			LocateRegistry.createRegistry(8003);
			Naming.bind("//0.0.0.0:8003/sendmail", sendMail);
			
			System.out.println("all services are started!");
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}

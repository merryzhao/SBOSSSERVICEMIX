/**
 * 
 */
package com.ai.sboss.upc.upcHttpRequestServer;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import com.ai.sboss.upc.httpRequestInterface.UpcHttpRequestInterface;
import com.ai.sboss.upc.upcHttpRequestImplement.UpcHttpRequestImplement;
import com.ai.sboss.upc.upcHttpRequestImplement.UpcLanuchToCrm;

/**
 * @author idot
 *
 */
public class UpcHttpRequestServ {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
			UpcHttpRequestInterface upcHttpRequestServ = new UpcHttpRequestImplement(); 
			
			UpcLanuchToCrm upcLanuchToCrm = new UpcLanuchToCrm();
			LocateRegistry.createRegistry(8091);
			Naming.bind("//0.0.0.0:8091/upcHttpRequestService", upcHttpRequestServ);			
			Naming.bind("//0.0.0.0:8091/upcLanuchToCrm", upcLanuchToCrm);
			System.out.println("connect sucess!");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}

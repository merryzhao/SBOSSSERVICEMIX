/**
 * 
 */
package com.ai.hadoop.operationserver;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import com.ai.hadoop.operationimplements.HadoopOperationsImpl;
import com.ai.hadoop.operationinterfaces.HadoopOperations;

/**
 * @author idot
 *
 */
public class HadoopOperationsServ {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try{
			HadoopOperations hadoopOperationsServ = new HadoopOperationsImpl();
			LocateRegistry.createRegistry(8092);
			Naming.bind("//0.0.0.0:8092/hadoopservice", hadoopOperationsServ);
			System.out.println("connect sucess!");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}

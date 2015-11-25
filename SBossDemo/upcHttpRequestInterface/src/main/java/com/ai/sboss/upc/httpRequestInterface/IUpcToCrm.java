package com.ai.sboss.upc.httpRequestInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IUpcToCrm extends Remote{
	
	public String upcToCrm (String objectId) throws Exception, RemoteException;

}

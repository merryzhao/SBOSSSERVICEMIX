package com.ai.sboss.ServiceCalendarMgrInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;

import net.sf.json.JSONArray;

public interface IServiceCalendarMgr extends Remote {

	public List<String> getAllServiceProcess() throws RemoteException;

	public String getServiceProcess(long productOfferingId, long customerId)
			throws RemoteException;

	public boolean saveServiceProcessInst(long customerId,
			long customerOrderId, long productOfferingId,
			Timestamp serviceExecTime) throws RemoteException;

	public boolean saveServiceProcessInst(long customerId,
			long productOfferingId, Timestamp serviceExecTime)
			throws RemoteException;

	public String getServiceProcessInst(long customerId) throws RemoteException;

	public List<? extends IServiceInformation> getServiceDatasByServiceName(
			String serviceName) throws RemoteException;

	public JSONArray getServiceDatasAdvByServiceName(String serviceName)
			throws RemoteException;
}

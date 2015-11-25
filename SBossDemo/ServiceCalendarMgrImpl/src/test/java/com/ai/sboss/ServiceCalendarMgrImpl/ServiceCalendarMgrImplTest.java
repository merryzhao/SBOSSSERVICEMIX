package com.ai.sboss.ServiceCalendarMgrImpl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;

import net.sf.json.JSONArray;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ai.sboss.ServiceCalendarMgrInterface.IServiceCalendarMgr;
import com.ai.sboss.ServiceCalendarMgrInterface.IServiceInformation;

public class ServiceCalendarMgrImplTest {

	private static IServiceCalendarMgr m_serviceCalendarMgr = null;

	@BeforeClass
	public static void onInitServiceCalendarMgrObj()
			throws MalformedURLException, RemoteException, NotBoundException {
		/*m_serviceCalendarMgr = (IServiceCalendarMgr) Naming
				.lookup("rmi://127.0.0.1:8004/ServiceCalendarMgr");
		Assert.assertTrue(null != m_serviceCalendarMgr);*/
	}

	@AfterClass
	public static void onReleaseServiceCalendarMgrObj() {
		m_serviceCalendarMgr = null;
	}

	@Test
	public void testGetServiceProcess() throws MalformedURLException,
			RemoteException, NotBoundException {
		/*String offeringContent = m_serviceCalendarMgr.getServiceProcess(
				201507201404L, 1);
		Assert.assertTrue(null != offeringContent);
		Assert.assertTrue(!offeringContent.isEmpty());*/
	}

	@Test
	public void testGetSvProcess() throws RemoteException, IOException,
			NotBoundException {
		/*List<String> listOfServices = m_serviceCalendarMgr
				.getAllServiceProcess();
		Assert.assertTrue(null != listOfServices);
		Assert.assertTrue(listOfServices.size() > 0);*/
	}

	@Test
	public void testSaveServiceProcessInst() throws RemoteException {

		/*Timestamp testTime = new Timestamp(System.currentTimeMillis());
		boolean bIsOk = m_serviceCalendarMgr.saveServiceProcessInst(888, 999,
				201507201404L, testTime);
		Assert.assertTrue(bIsOk);*/

	}

	@Test
	public void testGetServiceDatasByServiceName() throws RemoteException {

		/*List<? extends IServiceInformation> listOfInfo = m_serviceCalendarMgr
				.getServiceDatasByServiceName("Pick");
		Assert.assertTrue(listOfInfo.size() > 0);

		JSONArray array = m_serviceCalendarMgr
				.getServiceDatasAdvByServiceName("Pick");
		Assert.assertTrue(array.size() > 0);*/

	}

	@Test
	public void testGetServiceProcessInst() throws RemoteException {
		/*String fileContent = m_serviceCalendarMgr.getServiceProcessInst(888);
		Assert.assertTrue(null != fileContent);
		Assert.assertTrue(!fileContent.isEmpty());*/
	}

	@Test
	public void testGetServiceDatasAdvByServiceName() throws RemoteException,
			MalformedURLException, NotBoundException {
		
		/*JSONArray serviceList = m_serviceCalendarMgr
				.getServiceDatasAdvByServiceName("Car");
		Assert.assertTrue(serviceList != null);*/
	}
}

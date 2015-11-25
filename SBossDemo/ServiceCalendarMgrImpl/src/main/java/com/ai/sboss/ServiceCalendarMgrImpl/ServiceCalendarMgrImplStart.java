package com.ai.sboss.ServiceCalendarMgrImpl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ai.sboss.ServiceCalendarMgrInterface.IServiceCalendarMgr;


public class ServiceCalendarMgrImplStart {

	private static Logger LOGGER = Logger
			.getLogger(ServiceCalendarMgrImplStart.class.getName());

	public static void main(String[] args) {

		LOGGER.log(Level.INFO, "Start Service Calendar Management ...");

		try {

			LOGGER.log(Level.INFO,
					"Registry service oif ServiceCalendarMgr ...");

			IServiceCalendarMgr serviceCalenderMgr = new ServiceCalendarMgrImpl();
			LocateRegistry.createRegistry(8004);
			Naming.bind("//0.0.0.0:8004/ServiceCalendarMgr", serviceCalenderMgr);
			
			LOGGER.log(Level.INFO, "The Service Calendar Management has been enabled ...");

		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.log(Level.FINER, ex.getMessage());
		}
	}
}

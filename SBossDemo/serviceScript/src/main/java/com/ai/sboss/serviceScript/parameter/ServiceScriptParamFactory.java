package com.ai.sboss.serviceScript.parameter;

public class ServiceScriptParamFactory {

	private static volatile ServiceScriptParamFactory scriptParamFactory = null;

	private ServiceScriptParamFactory() {
	}

	public static ServiceScriptParamFactory getInstance() {
		if (null == scriptParamFactory) {
			synchronized (ServiceScriptParamFactory.class) {
				if (null == scriptParamFactory) {
					scriptParamFactory = new ServiceScriptParamFactory();
				}
			}
		}
		return scriptParamFactory;
	}
}

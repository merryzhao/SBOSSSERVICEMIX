package com.ai.sboss.arrangement.engine.startup;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class InitClassLoader implements BundleActivator {
	
	/**
	 * 日志
	 */
	private static Log LOGGER = LogFactory.getLog(InitClassLoader.class);
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		/*
		 * 这些orm相关的entity，将被加载到osgi框架的全局classloader中，以便hibernate-core bundle能够找到这些class
		 * */
		Bundle bundle = context.getBundle();
		ClassLoader classLoader = bundle.getClass().getClassLoader();
//		ClassLoader nowClassLoader = classLoader;
//		InitClassLoader.LOGGER.info("看到的bundle.getBundleId：" + bundle.getBundleId());
//		InitClassLoader.LOGGER.info("看到的classloader：" + classLoader.getClass().getName());
		
		ClassLoader parentLoader = null;
		while((parentLoader = classLoader.getParent()) != null) {
			InitClassLoader.LOGGER.info("看到的parent：" + parentLoader.getClass().getName());
			classLoader = parentLoader; 
		}
		
		//下面，老子将这个类加载到最顶层的classloader中
//		InitClassLoader.LOGGER.info("看到的nowClassLoader：" + nowClassLoader.getClass().getName());
//		InitClassLoader.LOGGER.info("看到的classLoader 423345：" + classLoader.getClass().getName());
		
		//============================
//		MyClassLoader myClassLoader = new MyClassLoader(bundle , classLoader);
//		myClassLoader.loadClass("com.ai.sboss.arrangement.entity.AbstractEntity");
//		myClassLoader.loadClass("com.ai.sboss.arrangement.entity.UUIDEntity");
//		myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.JointEntity");
//		myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.ArrangementEntity");
//		myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity");
//		myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.ArrangementSelfMappingEntity");
//		myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.JointEntity");
//		myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity");
//		myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity");
//		myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.JointTradeMappingEntity");
//		Object instance = myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.JointEntity").newInstance();
//		
//		DoTest.LOGGER.info("看到的nowClassLoader 423345：" + instance);
		
		//===========================接下来，我们将这个classloader加载到org.hibernate.core
		Bundle[] bundles = context.getBundles();
		for (Bundle bundleitem : bundles) {
			InitClassLoader.LOGGER.info(bundleitem.getSymbolicName() + " | " + bundleitem.getBundleId() + " | " + bundleitem.getLocation());
			if(StringUtils.equals("org.hibernate.core", bundleitem.getSymbolicName())) {
				ClassLoader coreClassLoader = bundleitem.getClass().getClassLoader();
				MyClassLoader myClassLoader = new MyClassLoader(bundle , coreClassLoader);
				myClassLoader.loadClass("com.ai.sboss.arrangement.entity.AbstractEntity");
				myClassLoader.loadClass("com.ai.sboss.arrangement.entity.UUIDEntity");
				myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.JointEntity");
				myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.ArrangementEntity");
				myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity");
				myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.ArrangementSelfMappingEntity");
				myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.JointEntity");
				myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.JointInputParamsEntity");
				myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.JointOutputParamsEntity");
				myClassLoader.loadClass("com.ai.sboss.arrangement.entity.orm.JointTradeMappingEntity");
				
				InitClassLoader.LOGGER.info("org.hibernate.core：==============================");
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		
	}
	
}

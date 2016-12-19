package com.liferay.cdi.test.cases;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import org.osgi.framework.wiring.BundleWiring;

public class CdiContainerTests extends AbstractTestCase {

	@Override
	protected void setUp() throws Exception {
		cdiBundle = bundleContext.installBundle(null , getBundle("basic-beans.jar"));
		cdiBundle.start();
		cdiContainer = waitForCdiContainer(cdiBundle.getBundleId());
	}

	@Override
	protected void tearDown() throws Exception {
		cdiBundle.uninstall();
	}

	public void testGetBeanFromCdiContainerService() throws Exception {
		BeanManager beanManager = cdiContainer.getBeanManager();

		assertNotNull(beanManager);
		assertPojoExists(beanManager);
	}

	public void testGetBeanManagerFromCDI() throws Exception {
		Thread currentThread = Thread.currentThread();
		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		try {
			BundleWiring bundleWiring = cdiBundle.adapt(BundleWiring.class);

			currentThread.setContextClassLoader(bundleWiring.getClassLoader());

			BeanManager beanManager = CDI.current().getBeanManager();
	
			assertNotNull(beanManager);
			assertPojoExists(beanManager);
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

}
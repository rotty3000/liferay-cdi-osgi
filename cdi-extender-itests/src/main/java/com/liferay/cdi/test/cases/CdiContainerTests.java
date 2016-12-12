package com.liferay.cdi.test.cases;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

public class CdiContainerTests extends AbstractTestCase {

	@Override
	protected void setUp() throws Exception {
		cdiBundle = bundleContext.installBundle(null , getBundle("basic-beans.jar"));
		cdiBundle.start();
		cdiContainer = waitForCdiContainer(cdiBundle.getBundleId());
	}

	@Override
	protected void tearDown() throws Exception {
		cdiBundle.stop();
	}

	public void testGetBeanFromCdiContainerService() throws Exception {
		BeanManager beanManager = cdiContainer.getBeanManager();

		assertNotNull(beanManager);
		assertPojoExists(beanManager);
	}

	public void testGetBeanManagerThroughJNDI() throws Exception {
		InitialContext context = new InitialContext();

		BeanManager beanManager = (BeanManager)context.lookup("java:comp/BeanManager");

		assertNotNull(beanManager);
		assertPojoExists(beanManager);
	}

}
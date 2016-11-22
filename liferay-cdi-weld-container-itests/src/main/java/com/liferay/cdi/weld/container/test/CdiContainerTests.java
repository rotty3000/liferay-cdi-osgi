package com.liferay.cdi.weld.container.test;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

public class CdiContainerTests extends AbstractTestCase {

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
package com.liferay.cdi.weld.container.test;

import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.naming.InitialContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.CdiContainer;

import com.liferay.cdi.weld.container.test.bean.SimpleBean;

import junit.framework.TestCase;

public class ContainerTests extends TestCase {
	
	static final Bundle bundle = FrameworkUtil.getBundle(ContainerTests.class);
	
	public void testSimpleBeanFromContainer() throws Exception {
		BundleContext bundleContext = bundle.getBundleContext();

		ServiceReference<CdiContainer> reference = bundleContext.getServiceReference(CdiContainer.class);
		
		CdiContainer container = bundleContext.getService(reference);
		
		BeanManager beanManager = container.getBeanManager();

		assertSimpleBeanExists(beanManager);
	}

	public void testBeanManagerFromJNDI() throws Exception {
		InitialContext context = new InitialContext();

		BeanManager beanManager = (BeanManager)context.lookup("java:comp/BeanManager");

		assertNotNull(beanManager);
		assertSimpleBeanExists(beanManager);
	}

	private void assertSimpleBeanExists(BeanManager beanManager) {
		Set<Bean<?>> beans = beanManager.getBeans(SimpleBean.class, any);

		assertFalse(beans.isEmpty());
		assertTrue(beans.iterator().next() instanceof SimpleBean);
		assertFalse(beans.iterator().hasNext());
	}

	private static final AnnotationLiteral<Any> any = new AnnotationLiteral<Any>() {
		private static final long serialVersionUID = 1L;
	};

}

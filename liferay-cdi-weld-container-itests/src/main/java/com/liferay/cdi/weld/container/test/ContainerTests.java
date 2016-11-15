package com.liferay.cdi.weld.container.test;

import java.util.Iterator;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.naming.InitialContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cdi.CdiContainer;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.cdi.weld.container.test.bean.SimpleBean;

import junit.framework.TestCase;

public class ContainerTests extends TestCase {

	static final Bundle bundle = FrameworkUtil.getBundle(ContainerTests.class);

	public void testSimpleBeanFromContainer() throws Exception {
		ServiceTracker<CdiContainer,CdiContainer> st = getST();

		CdiContainer container = st.waitForService(20000);

		BeanManager beanManager = container.getBeanManager();

		assertSimpleBeanExists(beanManager);
	}

	public void testBeanManagerFromJNDI() throws Exception {
		ServiceTracker<CdiContainer,CdiContainer> st = getST();

		st.waitForService(20000);

		InitialContext context = new InitialContext();

		BeanManager beanManager = (BeanManager)context.lookup("java:comp/BeanManager");

		assertNotNull(beanManager);
		assertSimpleBeanExists(beanManager);
	}

	private void assertSimpleBeanExists(BeanManager beanManager) {
		Set<Bean<?>> beans = beanManager.getBeans(SimpleBean.class, any);

		assertFalse(beans.isEmpty());
		Iterator<Bean<?>> iterator = beans.iterator();
		Bean<?> bean = iterator.next();
		assertTrue(bean.getBeanClass().isAssignableFrom(SimpleBean.class));
		assertFalse(iterator.hasNext());
	}

	private ServiceTracker<CdiContainer, CdiContainer> getST() throws InvalidSyntaxException {
		BundleContext bundleContext = bundle.getBundleContext();
		Filter filter = bundleContext.createFilter(
			"(&(objectClass=" + CdiContainer.class.getName() + ")(service.bundleid=" + bundle.getBundleId() + "))");
		ServiceTracker<CdiContainer, CdiContainer> serviceTracker = new ServiceTracker<>(
			bundle.getBundleContext(), filter, null);
		serviceTracker.open();
		return serviceTracker;
	}

	private static final AnnotationLiteral<Any> any = new AnnotationLiteral<Any>() {
		private static final long serialVersionUID = 1L;
	};

}

package com.liferay.cdi.test.cases;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cdi.CdiContainer;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.cdi.test.interfaces.Pojo;

import junit.framework.TestCase;

public class AbstractTestCase extends TestCase {

	@Override
	protected void setUp() throws Exception {
		cdiContainer = waitForCdiContainer();
	}

	void assertPojoExists(BeanManager beanManager) {
		Set<Bean<?>> beans = beanManager.getBeans(Pojo.class, any);

		assertFalse(beans.isEmpty());
		Iterator<Bean<?>> iterator = beans.iterator();
		Bean<?> bean = iterator.next();
		assertTrue(bean.getBeanClass().isAssignableFrom(Pojo.class));
		assertFalse(iterator.hasNext());

		bean = beanManager.resolve(beans);
		CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
		Pojo pojo = (Pojo)beanManager.getReference(bean, Pojo.class, ctx);
		assertNotNull(pojo);
	}

	InputStream getBundle(String name) {
		Class<?> clazz = this.getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(name);
	}

	ServiceTracker<CdiContainer, CdiContainer> getServiceTracker(long bundleId) throws InvalidSyntaxException {
		Filter filter = bundleContext.createFilter(
			"(&(objectClass=" + CdiContainer.class.getName() + ")(service.bundleid=" + bundleId + "))");

		ServiceTracker<CdiContainer, CdiContainer> serviceTracker = new ServiceTracker<>(bundleContext, filter, null);

		serviceTracker.open();

		return serviceTracker;
	}

	CdiContainer waitForCdiContainer() throws Exception {
		return waitForCdiContainer(bundle.getBundleId());
	}

	CdiContainer waitForCdiContainer(long bundleId) throws Exception {
		return getServiceTracker(bundleId).waitForService(timeout);
	}

	static final AnnotationLiteral<Any> any = new AnnotationLiteral<Any>() {
		private static final long serialVersionUID = 1L;
	};

	static final Bundle bundle = FrameworkUtil.getBundle(CdiBeanTests.class);
	static final BundleContext bundleContext = bundle.getBundleContext();
	static final long timeout = 5000;

	Bundle cdiBundle;
	CdiContainer cdiContainer;

}
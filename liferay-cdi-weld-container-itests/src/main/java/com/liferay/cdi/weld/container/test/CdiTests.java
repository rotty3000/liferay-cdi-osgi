package com.liferay.cdi.weld.container.test;

import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
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
import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.CdiContainer;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.cdi.weld.container.test.beans.ConstructorInjectedService;
import com.liferay.cdi.weld.container.test.beans.FieldInjectedReference;
import com.liferay.cdi.weld.container.test.beans.FieldInjectedService;
import com.liferay.cdi.weld.container.test.beans.MethodInjectedService;
import com.liferay.cdi.weld.container.test.beans.Pojo;
import com.liferay.cdi.weld.container.test.beans.ServiceWithProperties;

import junit.framework.TestCase;

public class CdiTests extends TestCase {

	static final Bundle bundle = FrameworkUtil.getBundle(CdiTests.class);
	static final BundleContext bundleContext = bundle.getBundleContext();

	static final long timeout = 200000;

	public void testConstructorInjectedService() throws Exception {
		ServiceTracker<CdiContainer, CdiContainer> st = getCdiContainerTracker();

		st.waitForService(timeout);

		ServiceReference<ConstructorInjectedService> serviceReference = bundleContext.getServiceReference(
			ConstructorInjectedService.class);

		assertNotNull(serviceReference);

		ConstructorInjectedService beanService = bundleContext.getService(serviceReference);

		assertNotNull(beanService);
		assertEquals("PREFIXCONSTRUCTOR", beanService.doSomething());
	}

	public void testFieldInjectedReference() throws Exception {
		ServiceTracker<CdiContainer, CdiContainer> st = getCdiContainerTracker();

		st.waitForService(timeout);

		ServiceReference<FieldInjectedReference> serviceReference = bundleContext.getServiceReference(
			FieldInjectedReference.class);

		assertNotNull(serviceReference);

		FieldInjectedReference fieldInjectedReference = bundleContext.getService(serviceReference);

		assertNotNull(fieldInjectedReference);
	}

	public void testFieldInjectedService() throws Exception {
		ServiceTracker<CdiContainer, CdiContainer> st = getCdiContainerTracker();

		st.waitForService(timeout);

		ServiceReference<FieldInjectedService> serviceReference = bundleContext.getServiceReference(
				FieldInjectedService.class);

		assertNotNull(serviceReference);

		FieldInjectedService beanService = bundleContext.getService(serviceReference);

		assertNotNull(beanService);
		assertEquals("PREFIXFIELD", beanService.doSomething());
	}

	public void testMethodInjectedService() throws Exception {
		ServiceTracker<CdiContainer, CdiContainer> st = getCdiContainerTracker();

		st.waitForService(timeout);

		ServiceReference<MethodInjectedService> serviceReference = bundleContext.getServiceReference(
				MethodInjectedService.class);

		assertNotNull(serviceReference);

		MethodInjectedService beanService = bundleContext.getService(serviceReference);

		assertNotNull(beanService);
		assertEquals("PREFIXMETHOD", beanService.doSomething());
	}

	public void testBeanAsServiceWithProperties() throws Exception {
		ServiceTracker<CdiContainer, CdiContainer> st = getCdiContainerTracker();

		st.waitForService(timeout);

		ServiceReference<ServiceWithProperties> serviceReference = bundleContext.getServiceReference(
			ServiceWithProperties.class);

		assertNotNull(serviceReference);

		ServiceWithProperties serviceWithProperties = bundleContext.getService(serviceReference);

		assertNotNull(serviceWithProperties);

		assertEquals("test.value.b2", serviceReference.getProperty("test.key.b2"));
	}

	public void testSimpleBeanFromContainer() throws Exception {
		ServiceTracker<CdiContainer, CdiContainer> st = getCdiContainerTracker();

		CdiContainer container = st.waitForService(timeout);

		BeanManager beanManager = container.getBeanManager();

		assertPojoExists(beanManager);
	}

	public void testBeanManagerFromJNDI() throws Exception {
		ServiceTracker<CdiContainer, CdiContainer> st = getCdiContainerTracker();

		st.waitForService(timeout);

		InitialContext context = new InitialContext();

		BeanManager beanManager = (BeanManager)context.lookup("java:comp/BeanManager");

		assertNotNull(beanManager);
		assertPojoExists(beanManager);
	}

	private void assertPojoExists(BeanManager beanManager) {
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

	private ServiceTracker<CdiContainer, CdiContainer> getCdiContainerTracker() throws InvalidSyntaxException {
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

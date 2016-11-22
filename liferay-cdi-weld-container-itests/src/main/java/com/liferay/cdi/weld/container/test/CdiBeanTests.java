package com.liferay.cdi.weld.container.test;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.osgi.framework.ServiceReference;

import com.liferay.cdi.weld.container.test.beans.BundleContextBean;
import com.liferay.cdi.weld.container.test.beans.ConstructorInjectedService;
import com.liferay.cdi.weld.container.test.beans.FieldInjectedReference;
import com.liferay.cdi.weld.container.test.beans.FieldInjectedService;
import com.liferay.cdi.weld.container.test.beans.MethodInjectedService;
import com.liferay.cdi.weld.container.test.beans.ServiceWithProperties;

public class CdiBeanTests extends AbstractTestCase {

	public void testConstructorInjectedService() throws Exception {
		ServiceReference<ConstructorInjectedService> serviceReference = bundleContext.getServiceReference(
			ConstructorInjectedService.class);

		assertNotNull(serviceReference);

		ConstructorInjectedService beanService = bundleContext.getService(serviceReference);

		assertNotNull(beanService);
		assertEquals("PREFIXCONSTRUCTOR", beanService.doSomething());
	}

	public void testFieldInjectedReference() throws Exception {
		ServiceReference<FieldInjectedReference> serviceReference = bundleContext.getServiceReference(
			FieldInjectedReference.class);

		assertNotNull(serviceReference);

		FieldInjectedReference fieldInjectedReference = bundleContext.getService(serviceReference);

		assertNotNull(fieldInjectedReference);
		assertNotNull(fieldInjectedReference.getProperties());
		assertNotNull(fieldInjectedReference.getReference1());
		assertNotNull(fieldInjectedReference.getReference2());
		assertNotNull(fieldInjectedReference.getService());
		assertEquals("value", fieldInjectedReference.getProperties().get("key"));
		assertEquals("value", fieldInjectedReference.getReference1().getProperty("key"));
		assertEquals("value", fieldInjectedReference.getReference2().getProperty("key"));
	}

	public void testFieldInjectedService() throws Exception {
		ServiceReference<FieldInjectedService> serviceReference = bundleContext.getServiceReference(
				FieldInjectedService.class);

		assertNotNull(serviceReference);

		FieldInjectedService beanService = bundleContext.getService(serviceReference);

		assertNotNull(beanService);
		assertEquals("PREFIXFIELD", beanService.doSomething());
	}

	public void testMethodInjectedService() throws Exception {
		ServiceReference<MethodInjectedService> serviceReference = bundleContext.getServiceReference(
			MethodInjectedService.class);

		assertNotNull(serviceReference);

		MethodInjectedService beanService = bundleContext.getService(serviceReference);

		assertNotNull(beanService);
		assertEquals("PREFIXMETHOD", beanService.doSomething());
	}

	public void testBeanAsServiceWithProperties() throws Exception {
		ServiceReference<ServiceWithProperties> serviceReference = bundleContext.getServiceReference(
			ServiceWithProperties.class);

		assertNotNull(serviceReference);

		ServiceWithProperties serviceWithProperties = bundleContext.getService(serviceReference);

		assertNotNull(serviceWithProperties);

		assertEquals("test.value.b2", serviceReference.getProperty("test.key.b2"));
	}

	public void testBundleContextInjection() throws Exception {
		BeanManager beanManager = cdiContainer.getBeanManager();

		assertNotNull(beanManager);

		Set<Bean<?>> beans = beanManager.getBeans(BundleContextBean.class, any);
		Bean<?> bean = beanManager.resolve(beans);
		CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
		BundleContextBean bcb = (BundleContextBean)beanManager.getReference(bean, BundleContextBean.class, ctx);
		assertNotNull(bcb);
		assertNotNull(bcb.getBundleContext());
	}

}
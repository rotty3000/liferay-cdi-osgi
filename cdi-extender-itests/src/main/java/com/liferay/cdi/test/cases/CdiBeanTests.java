package com.liferay.cdi.test.cases;

import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.liferay.cdi.test.interfaces.BeanThingy;
import com.liferay.cdi.test.interfaces.BundleContextBeanQualifier;
import com.liferay.cdi.test.interfaces.FieldInjectedReference;

@SuppressWarnings("rawtypes")
public class CdiBeanTests extends AbstractTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cdiContainer = waitForCdiContainer(cdiBundle.getBundleId());
	}

	public void testConstructorInjectedService() throws Exception {
		Iterator<ServiceReference<BeanThingy>> iterator = bundleContext.getServiceReferences(
			BeanThingy.class, String.format("(objectClass=*.%s)","ConstructorInjectedService")).iterator();

		assertTrue(iterator.hasNext());

		ServiceReference<BeanThingy> serviceReference = iterator.next();

		assertNotNull(serviceReference);

		BeanThingy bean = bundleContext.getService(serviceReference);

		assertNotNull(bean);
		assertEquals("PREFIXCONSTRUCTOR", bean.doSomething());
	}

	public void testFieldInjectedReference_BundleScoped() throws Exception {
		Iterator<ServiceReference<FieldInjectedReference>> iterator = bundleContext.getServiceReferences(
			FieldInjectedReference.class, String.format("(objectClass=*.%s)","FieldInjectedBundleScopedImpl")).iterator();

		assertTrue(iterator.hasNext());

		ServiceReference<FieldInjectedReference> serviceReference = iterator.next();

		assertNotNull(serviceReference);

		FieldInjectedReference fieldInjectedReference = bundleContext.getService(serviceReference);

		assertNotNull(fieldInjectedReference);
		assertNotNull(fieldInjectedReference.getProperties());
		assertNotNull(fieldInjectedReference.getGenericReference());
		assertNotNull(fieldInjectedReference.getRawReference());
		assertNotNull(fieldInjectedReference.getService());
		assertEquals("value", fieldInjectedReference.getProperties().get("key"));
		assertEquals("value", fieldInjectedReference.getGenericReference().getProperty("key"));
		assertEquals("value", fieldInjectedReference.getRawReference().getProperty("key"));
	}

	public void testFieldInjectedReference_PrototypeScoped() throws Exception {
		Iterator<ServiceReference<FieldInjectedReference>> iterator = bundleContext.getServiceReferences(
			FieldInjectedReference.class, String.format("(objectClass=*.%s)","FieldInjectedPrototypeScopedImpl")).iterator();

		assertTrue(iterator.hasNext());

		ServiceReference<FieldInjectedReference> serviceReference = iterator.next();

		assertNotNull(serviceReference);

		FieldInjectedReference fieldInjectedReference = bundleContext.getService(serviceReference);

		assertNotNull(fieldInjectedReference);
		assertNotNull(fieldInjectedReference.getProperties());
		assertNotNull(fieldInjectedReference.getGenericReference());
		assertNotNull(fieldInjectedReference.getRawReference());
		assertNotNull(fieldInjectedReference.getService());
		assertEquals("value", fieldInjectedReference.getProperties().get("key"));
		assertEquals("value", fieldInjectedReference.getGenericReference().getProperty("key"));
		assertEquals("value", fieldInjectedReference.getRawReference().getProperty("key"));
	}

	public void testFieldInjectedService() throws Exception {
		Iterator<ServiceReference<BeanThingy>> iterator = bundleContext.getServiceReferences(
			BeanThingy.class, String.format("(objectClass=*.%s)","FieldInjectedService")).iterator();

		assertTrue(iterator.hasNext());

		ServiceReference<BeanThingy> serviceReference = iterator.next();

		assertNotNull(serviceReference);

		BeanThingy bean = bundleContext.getService(serviceReference);

		assertNotNull(bean);
		assertEquals("PREFIXFIELD", bean.doSomething());
	}

	public void testMethodInjectedService() throws Exception {
		Iterator<ServiceReference<BeanThingy>> iterator = bundleContext.getServiceReferences(
			BeanThingy.class, String.format("(objectClass=*.%s)","MethodInjectedService")).iterator();

		assertTrue(iterator.hasNext());

		ServiceReference<BeanThingy> serviceReference = iterator.next();

		assertNotNull(serviceReference);

		BeanThingy bean = bundleContext.getService(serviceReference);

		assertNotNull(bean);
		assertEquals("PREFIXMETHOD", bean.doSomething());
	}

	public void testBeanAsServiceWithProperties() throws Exception {
		Iterator<ServiceReference<BeanThingy>> iterator = bundleContext.getServiceReferences(
			BeanThingy.class, String.format("(objectClass=*.%s)","ServiceWithProperties")).iterator();

		assertTrue(iterator.hasNext());

		ServiceReference<BeanThingy> serviceReference = iterator.next();

		assertNotNull(serviceReference);

		BeanThingy bean = bundleContext.getService(serviceReference);

		assertNotNull(bean);

		assertEquals("test.value.b2", serviceReference.getProperty("test.key.b2"));
	}

	public void testBundleContextInjection() throws Exception {
		BeanManager beanManager = cdiContainer.getBeanManager();

		assertNotNull(beanManager);

		@SuppressWarnings("serial")
		Set<Bean<?>> beans = beanManager.getBeans(Object.class, new AnnotationLiteral<BundleContextBeanQualifier>() {});
		Bean<?> bean = beanManager.resolve(beans);
		CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
		Object bcb = beanManager.getReference(bean, Object.class, ctx);
		assertNotNull(bcb);
		@SuppressWarnings("unchecked")
		BeanThingy<BundleContext> bti = (BeanThingy<BundleContext>)bcb;
		assertNotNull(bti.getThingy());
		assertTrue(bti.getThingy() instanceof BundleContext);
	}

}
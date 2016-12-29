package com.liferay.cdi.test.cases;

import java.util.Iterator;
import java.util.List;
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

		assertTrue(serviceReference.getProperty("p.Boolean") instanceof Boolean);
		assertTrue(serviceReference.getProperty("p.Boolean.array") instanceof Boolean[]);
		assertEquals(2, ((Boolean[])serviceReference.getProperty("p.Boolean.array")).length);
		assertTrue(serviceReference.getProperty("p.Boolean.list") instanceof List);
		assertEquals(2, ((List<Boolean>)serviceReference.getProperty("p.Boolean.list")).size());
		assertTrue(serviceReference.getProperty("p.Boolean.set") instanceof Set);
		assertEquals(2, ((Set<Boolean>)serviceReference.getProperty("p.Boolean.set")).size());

		assertTrue(serviceReference.getProperty("p.Byte") instanceof Byte);
		assertTrue(serviceReference.getProperty("p.Byte.array") instanceof Byte[]);
		assertEquals(2, ((Byte[])serviceReference.getProperty("p.Byte.array")).length);
		assertTrue(serviceReference.getProperty("p.Byte.list") instanceof List);
		assertEquals(2, ((List<Byte>)serviceReference.getProperty("p.Byte.list")).size());
		assertTrue(serviceReference.getProperty("p.Byte.set") instanceof Set);
		assertEquals(2, ((Set<Byte>)serviceReference.getProperty("p.Byte.set")).size());

		assertTrue(serviceReference.getProperty("p.Character") instanceof Character);
		assertTrue(serviceReference.getProperty("p.Character.array") instanceof Character[]);
		assertEquals(2, ((Character[])serviceReference.getProperty("p.Character.array")).length);
		assertTrue(serviceReference.getProperty("p.Character.list") instanceof List);
		assertEquals(2, ((List<Character>)serviceReference.getProperty("p.Character.list")).size());
		assertTrue(serviceReference.getProperty("p.Character.set") instanceof Set);
		assertEquals(2, ((Set<Character>)serviceReference.getProperty("p.Character.set")).size());

		assertTrue(serviceReference.getProperty("p.Double") instanceof Double);
		assertTrue(serviceReference.getProperty("p.Double.array") instanceof Double[]);
		assertEquals(2, ((Double[])serviceReference.getProperty("p.Double.array")).length);
		assertTrue(serviceReference.getProperty("p.Double.list") instanceof List);
		assertEquals(2, ((List<Double>)serviceReference.getProperty("p.Double.list")).size());
		assertTrue(serviceReference.getProperty("p.Double.set") instanceof Set);
		assertEquals(2, ((Set<Double>)serviceReference.getProperty("p.Double.set")).size());

		assertTrue(serviceReference.getProperty("p.Float") instanceof Float);
		assertTrue(serviceReference.getProperty("p.Float.array") instanceof Float[]);
		assertEquals(2, ((Float[])serviceReference.getProperty("p.Float.array")).length);
		assertTrue(serviceReference.getProperty("p.Float.list") instanceof List);
		assertEquals(2, ((List<Float>)serviceReference.getProperty("p.Float.list")).size());
		assertTrue(serviceReference.getProperty("p.Float.set") instanceof Set);
		assertEquals(2, ((Set<Float>)serviceReference.getProperty("p.Float.set")).size());

		assertTrue(serviceReference.getProperty("p.Integer") instanceof Integer);
		assertTrue(serviceReference.getProperty("p.Integer.array") instanceof Integer[]);
		assertEquals(2, ((Integer[])serviceReference.getProperty("p.Integer.array")).length);
		assertTrue(serviceReference.getProperty("p.Integer.list") instanceof List);
		assertEquals(2, ((List<Integer>)serviceReference.getProperty("p.Integer.list")).size());
		assertTrue(serviceReference.getProperty("p.Integer.set") instanceof Set);
		assertEquals(2, ((Set<Integer>)serviceReference.getProperty("p.Integer.set")).size());

		assertTrue(serviceReference.getProperty("p.Long") instanceof Long);
		assertTrue(serviceReference.getProperty("p.Long.array") instanceof Long[]);
		assertEquals(2, ((Long[])serviceReference.getProperty("p.Long.array")).length);
		assertTrue(serviceReference.getProperty("p.Long.list") instanceof List);
		assertEquals(2, ((List<Long>)serviceReference.getProperty("p.Long.list")).size());
		assertTrue(serviceReference.getProperty("p.Long.set") instanceof Set);
		assertEquals(2, ((Set<Long>)serviceReference.getProperty("p.Long.set")).size());

		assertTrue(serviceReference.getProperty("p.Short") instanceof Short);
		assertTrue(serviceReference.getProperty("p.Short.array") instanceof Short[]);
		assertEquals(2, ((Short[])serviceReference.getProperty("p.Short.array")).length);
		assertTrue(serviceReference.getProperty("p.Short.list") instanceof List);
		assertEquals(2, ((List<Short>)serviceReference.getProperty("p.Short.list")).size());
		assertTrue(serviceReference.getProperty("p.Short.set") instanceof Set);
		assertEquals(2, ((Set<Short>)serviceReference.getProperty("p.Short.set")).size());

		assertTrue(serviceReference.getProperty("p.String") instanceof String);
		assertTrue(serviceReference.getProperty("p.String.array") instanceof String[]);
		assertEquals(2, ((String[])serviceReference.getProperty("p.String.array")).length);
		assertTrue(serviceReference.getProperty("p.String.list") instanceof List);
		assertEquals(2, ((List<String>)serviceReference.getProperty("p.String.list")).size());
		assertTrue(serviceReference.getProperty("p.String.set") instanceof Set);
		assertEquals(2, ((Set<String>)serviceReference.getProperty("p.String.set")).size());
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
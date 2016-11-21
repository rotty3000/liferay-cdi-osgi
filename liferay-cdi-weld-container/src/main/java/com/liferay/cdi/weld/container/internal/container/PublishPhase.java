package com.liferay.cdi.weld.container.internal.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Singleton;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cdi.CdiContainer;
import org.osgi.service.cdi.CdiEvent;
import org.osgi.service.cdi.annotations.BundleScoped;
import org.osgi.service.cdi.annotations.PrototypeScoped;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.service.cdi.annotations.ServiceProperty;
import org.osgi.service.jndi.JNDIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.cdi.weld.container.internal.CdiHelper;
import com.liferay.cdi.weld.container.internal.ctx.JNDIContext;

public class PublishPhase {

	public PublishPhase(
		Bundle bundle, CdiHelper cdiHelper, BeanDeploymentArchive beanDeploymentArchive) {

		_bundle = bundle;
		_cdiHelper = cdiHelper;
		_beanDeploymentArchive = beanDeploymentArchive;
		_bundleContext = _bundle.getBundleContext();
	}

	public void close() {
		Iterator<ServiceRegistration<?>> iterator = _registrations.iterator();

		while (iterator.hasNext()) {
			ServiceRegistration<?> registration = iterator.next();
			iterator.remove();
			registration.unregister();
		}

		if (_cdiContainerRegistration != null) {
			_cdiContainerRegistration.unregister();
		}

		if (_beanManagerRegistration != null) {
			_beanManagerRegistration.unregister();
		}

		if (_objectFactoryRegistration != null) {
			_objectFactoryRegistration.unregister();
		}
	}

	public void open(WeldBootstrap bootstrap) {
		_cdiHelper.fireCdiEvent(new CdiEvent(CdiEvent.Type.SATISFIED, _bundle, _cdiHelper.getExtenderBundle()));

		bootstrap.validateBeans();
		bootstrap.endInitialization();

		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put("osgi.cdi.container.symbolicname", _bundle.getSymbolicName());
		properties.put("osgi.cdi.container.version", _bundle.getVersion());

		BeanManager beanManager = bootstrap.getManager(_beanDeploymentArchive);

		_cdiContainerRegistration = _bundle.getBundleContext().registerService(
			CdiContainer.class, new InternalCdiContainer(beanManager), properties);

		properties = new Hashtable<>();
		properties.put("osgi.cdi.container.symbolicname", _bundle.getSymbolicName());
		properties.put("osgi.cdi.container.version", _bundle.getVersion());

		_beanManagerRegistration = _bundle.getBundleContext().registerService(
			BeanManager.class, beanManager, properties);

		properties = new Hashtable<>();
		properties.put("osgi.cdi.container.symbolicname", _bundle.getSymbolicName());
		properties.put("osgi.cdi.container.version", _bundle.getVersion());
		properties.put(JNDIConstants.JNDI_URLSCHEME, "java");

		_objectFactoryRegistration = _bundle.getBundleContext().registerService(
			ObjectFactory.class, new JndiObjectFactory(new JNDIContext(beanManager)), properties);

		Set<Bean<?>> allBeans = beanManager.getBeans(Object.class, CdiHelper.ANY);

		if (!allBeans.isEmpty()) {
			processBeans(beanManager, allBeans);
		}

		_cdiHelper.fireCdiEvent(new CdiEvent(CdiEvent.Type.CREATED, _bundle, _cdiHelper.getExtenderBundle()));
	}

	private String[] getClassNames(Service service, Bean<?> bean) {
		List<String> classNames = new ArrayList<>();

		Class<?>[] types = service == null ? new Class<?>[0] : service.types();

		if (types.length > 0) {
			for (Type type : types) {
				classNames.add(type.getTypeName());
			}
		}
		else {
			types = bean.getBeanClass().getInterfaces();

			if (types.length > 0) {
				for (Type type : types) {
					classNames.add(type.getTypeName());
				}
			}
			else {
				classNames.add(bean.getBeanClass().getName());
			}
		}

		return classNames.toArray(new String[0]);
	}

	private void processBeans(BeanManager beanManager, Set<Bean<?>> allBeans) {
		for (Bean<?> bean : allBeans) {
			processBean(beanManager, bean);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void processBean(BeanManager beanManager, Bean<?> bean) {
		Service service = bean.getBeanClass().getDeclaredAnnotation(Service.class);

		if (service == null) {
			// TODO make sure to check the osgi.extender requirement to see if the "services" attribute is set
			// listing beans that should be published as bare services.

			return;
		}

		Class<? extends Annotation> scope = bean.getScope();

		if (!ApplicationScoped.class.isAssignableFrom(scope) &&
			!Dependent.class.isAssignableFrom(scope) &&
			!Singleton.class.isAssignableFrom(scope) &&
			!BundleScoped.class.isAssignableFrom(scope) &&
			!PrototypeScoped.class.isAssignableFrom(scope)) {

			if (_log.isErrorEnabled()) {
				_log.error(
					"Bean '{}' cannot use the @Service annotation because it has an unsupported scope '{}'",
					bean, scope);
			}

			return;
		}

		Dictionary<String, Object> properties = new Hashtable<>();

		if (service != null) {
			for (ServiceProperty serviceProperty :service.properties()) {
				// TODO convert value to typed
				properties.put(serviceProperty.key(), serviceProperty.value());
			}
		}

		String[] classNames = getClassNames(service, bean);

		Context context = beanManager.getContext(scope);
		CreationalContext creationalContext = beanManager.createCreationalContext(bean);

		if (_log.isDebugEnabled()) {
			_log.debug("Publishing bean '{}' as service with scope '{}'.", bean.getBeanClass(), scope);
		}

		if (PrototypeScoped.class.isAssignableFrom(scope)) {
			_registrations.add(
				_bundleContext.registerService(
					classNames, new PrototypeScopeWrapper(bean, context, creationalContext), properties));
		}
		else if (BundleScoped.class.isAssignableFrom(scope)) {
			_registrations.add(
				_bundleContext.registerService(
					classNames, new BundleScopeWrapper(bean, context, creationalContext), properties));
		}
		else {
			_registrations.add(
				_bundleContext.registerService(classNames, context.get(bean, creationalContext), properties));
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(PublishPhase.class);

	private final BeanDeploymentArchive _beanDeploymentArchive;
	private final Bundle _bundle;
	private final BundleContext _bundleContext;
	private final CdiHelper _cdiHelper;
	private final List<ServiceRegistration<?>> _registrations = new CopyOnWriteArrayList<>();

	private ServiceRegistration<BeanManager> _beanManagerRegistration;
	private ServiceRegistration<CdiContainer> _cdiContainerRegistration;
	private ServiceRegistration<ObjectFactory> _objectFactoryRegistration;

	@SuppressWarnings({"rawtypes", "unchecked", "unused"})
	private class BundleScopeWrapper implements ServiceFactory {

		public BundleScopeWrapper(Bean bean, Context context, CreationalContext creationalContext) {
			_bean = bean;
			_context = context;
			_creationalContext = creationalContext;
		}

		@Override
		public Object getService(Bundle bundle, ServiceRegistration registration) {
			return _context.get(_bean, _creationalContext);
		}

		@Override
		public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
			_bean.destroy(service, _creationalContext);
		}

		private Bean _bean;
		private BeanManager _beanManager;
		private Context _context;
		private CreationalContext _creationalContext;

	}

	@SuppressWarnings({"rawtypes", "unchecked", "unused"})
	private class PrototypeScopeWrapper implements PrototypeServiceFactory {

		public PrototypeScopeWrapper(Bean bean, Context context, CreationalContext creationalContext) {
			_bean = bean;
			_context = context;
			_creationalContext = creationalContext;
		}

		@Override
		public Object getService(Bundle bundle, ServiceRegistration registration) {
			return _context.get(_bean, _creationalContext);
		}

		@Override
		public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
			_bean.destroy(service, _creationalContext);
		}

		private Bean _bean;
		private BeanManager _beanManager;
		private Context _context;
		private CreationalContext _creationalContext;

	}

	private class InternalCdiContainer implements CdiContainer {

		public InternalCdiContainer(BeanManager beanManager) {
			_beanManager = beanManager;
		}

		@Override
		public BeanManager getBeanManager() {
			return _beanManager;
		}

		private final BeanManager _beanManager;

	}

	private class JndiObjectFactory implements ObjectFactory {

		public JndiObjectFactory(JNDIContext jndiContext) {
			_jndiContext = jndiContext;
		}

		@Override
		public Object getObjectInstance(
				Object obj, Name name, javax.naming.Context context, Hashtable<?, ?> environment)
			throws Exception {

			if (obj == null) {
				return _jndiContext;
			}

			return null;
		}

		private final JNDIContext _jndiContext;

	}

}
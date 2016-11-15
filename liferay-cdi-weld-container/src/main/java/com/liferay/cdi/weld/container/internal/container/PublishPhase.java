package com.liferay.cdi.weld.container.internal.container;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cdi.CdiContainer;
import org.osgi.service.cdi.CdiEvent;
import org.osgi.service.cdi.Service;
import org.osgi.service.jndi.JNDIConstants;

import com.liferay.cdi.weld.container.internal.CdiHelper;
import com.liferay.cdi.weld.container.internal.ctx.JNDIContext;

public class PublishPhase {

	public PublishPhase(
		Bundle bundle, Bootstrap bootstrap, CdiHelper cdiHelper, BeanDeploymentArchive beanDeploymentArchive) {

		_bundle = bundle;
		_bootstrap = bootstrap;
		_cdiHelper = cdiHelper;
		_beanDeploymentArchive = beanDeploymentArchive;
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

	public void open() {
		_cdiHelper.fireCdiEvent(new CdiEvent(CdiEvent.SATISFIED, _bundle, _cdiHelper.getExtenderBundle()));

		_bootstrap.deployBeans();
		_bootstrap.validateBeans();
		_bootstrap.endInitialization();

		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put("osgi.cdi.container.symbolicname", _bundle.getSymbolicName());
		properties.put("osgi.cdi.container.version", _bundle.getVersion());

		BeanManager beanManager = _bootstrap.getManager(_beanDeploymentArchive);

		_cdiContainerRegistration = _bundle.getBundleContext().registerService(
			CdiContainer.class, new LocalCdiContainer(beanManager), properties);

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

		Set<Bean<?>> beansAsServices = beanManager.getBeans(Object.class, Service);

		if (!beansAsServices.isEmpty()) {
			// TODO publish all beans annotated with @Service into the service registry.
			// TODO make sure to check the osgi.extender requirement to see if the "services" attribute is set listing
			// beans that should be published as bare services.

			// _registrations.add(...)
		}

		_cdiHelper.fireCdiEvent(new CdiEvent(CdiEvent.CREATED, _bundle, _cdiHelper.getExtenderBundle()));
	}

	private static final AnnotationLiteral<Service> Service = new AnnotationLiteral<Service>() {
		private static final long serialVersionUID = 1L;
	};

	private BeanDeploymentArchive _beanDeploymentArchive;
	private ServiceRegistration<BeanManager> _beanManagerRegistration;
	private Bundle _bundle;
	private Bootstrap _bootstrap;
	private CdiHelper _cdiHelper;
	private ServiceRegistration<CdiContainer> _cdiContainerRegistration;
	private ServiceRegistration<ObjectFactory> _objectFactoryRegistration;
	private final List<ServiceRegistration<?>> _registrations = new CopyOnWriteArrayList<>();

	private class LocalCdiContainer implements CdiContainer {

		public LocalCdiContainer(BeanManager beanManager) {
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
		public Object getObjectInstance(Object obj, Name name, Context context, Hashtable<?, ?> environment)
			throws Exception {

			if (obj == null) {
				return _jndiContext;
			}

			return null;
		}

		private final JNDIContext _jndiContext;

	}

}
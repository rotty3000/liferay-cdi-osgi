package com.liferay.cdi.weld.container.internal.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.cdi.CdiEvent;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.cdi.weld.container.internal.BundleDeployment;
import com.liferay.cdi.weld.container.internal.CdiHelper;
import com.liferay.cdi.weld.container.internal.DebugExtension;
import com.liferay.cdi.weld.container.internal.ExtensionMetadata;
import com.liferay.cdi.weld.container.internal.FilterBuilder;
import com.liferay.cdi.weld.container.internal.ReferenceDependency;
import com.liferay.cdi.weld.container.internal.ReferenceExtension;
import com.liferay.cdi.weld.container.internal.SimpleEnvironment;

public class ReferencePhase {

	public ReferencePhase(
		Bundle bundle, CdiHelper cdiHelper, BeanDeploymentArchive beanDeploymentArchive,
		Map<ServiceReference<Extension>, Metadata<Extension>> extensions) {

		_bundle = bundle;
		_cdiHelper = cdiHelper;
		_beanDeploymentArchive = beanDeploymentArchive;
		_extensions = extensions;
		_bundleContext = _bundle.getBundleContext();
		_bundleWiring = _bundle.adapt(BundleWiring.class);

		_publishPhase = new PublishPhase(_bundle, _cdiHelper, _beanDeploymentArchive);
	}

	public void close() {
		_publishPhase.close();

		if (_serviceTracker != null) {
			_serviceTracker.close();
			_serviceTracker = null;
		}
	}

	public void open() {
		WeldBootstrap bootstrap = new WeldBootstrap();

		List<Metadata<Extension>> extensions = new ArrayList<>();

		// Add the internal debug extension
		extensions.add(new ExtensionMetadata(new DebugExtension(), _bundle.toString()));
		extensions.add(
			new ExtensionMetadata(new ReferenceExtension(_referenceDependencies, _bundleContext), _bundle.toString()));

		// Add extensions found from the bundle's classloader, such as those in the Bundle-ClassPath
		for (Metadata<Extension> meta : bootstrap.loadExtensions(_bundleWiring.getClassLoader())) {
			extensions.add(meta);
		}

		// Add external extensions
		for (Metadata<Extension> meta : _extensions.values()) {
			extensions.add(meta);
		}

		Deployment deployment = new BundleDeployment(extensions, _beanDeploymentArchive);

		bootstrap.startContainer(new SimpleEnvironment(), deployment);
		bootstrap.startInitialization();
		bootstrap.deployBeans();

		if (!_referenceDependencies.isEmpty()) {
			_cdiHelper.fireCdiEvent(
				new CdiEvent(CdiEvent.Type.WAITING_FOR_SERVICES, _bundle, _cdiHelper.getExtenderBundle()));

			if (_log.isDebugEnabled()) {
				_log.debug("Waiting for services {}", _referenceDependencies);
			}

			Filter filter = FilterBuilder.createReferenceFilter(_referenceDependencies);

			_serviceTracker = new ServiceTracker<>(_bundleContext, filter, new ReferencePhaseCustomizer(bootstrap));

			_serviceTracker.open();
		}
		else {
			_publishPhase.open(bootstrap);
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(ReferencePhase.class);

	private final BeanDeploymentArchive _beanDeploymentArchive;
	private final Bundle _bundle;
	private final BundleContext _bundleContext;
	private final BundleWiring _bundleWiring;
	private final CdiHelper _cdiHelper;
	private final Map<ServiceReference<Extension>, Metadata<Extension>> _extensions;
	private final PublishPhase _publishPhase;
	private final List<ReferenceDependency> _referenceDependencies = new CopyOnWriteArrayList<>();

	private ServiceTracker<?, ?> _serviceTracker;

	private class ReferencePhaseCustomizer implements ServiceTrackerCustomizer<Object, ReferenceDependency> {

		public ReferencePhaseCustomizer(WeldBootstrap bootstrap) {
			_bootstrap = bootstrap;
		}

		@Override
		public ReferenceDependency addingService(ServiceReference<Object> reference) {
			ReferenceDependency trackedDependency = null;

			for (ReferenceDependency referenceDependency : _referenceDependencies) {
				if (referenceDependency.matches(reference)) {
					_referenceDependencies.remove(referenceDependency);

					trackedDependency = referenceDependency;

					trackedDependency.resolve(reference);
				}
			}

			if ((trackedDependency != null) && _referenceDependencies.isEmpty()) {
				_publishPhase.open(_bootstrap);
			}

			return trackedDependency;
		}

		@Override
		public void modifiedService(ServiceReference<Object> reference, ReferenceDependency referenceDependency) {
		}

		@Override
		public void removedService(ServiceReference<Object> reference, ReferenceDependency referenceDependency) {
			if (_referenceDependencies.isEmpty()) {
				_publishPhase.close();
			}
			_referenceDependencies.add(referenceDependency);
		}

		private WeldBootstrap _bootstrap;

	}

}

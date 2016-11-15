package com.liferay.cdi.weld.container.internal.container;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.cdi.CdiEvent;
import org.osgi.service.cdi.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.liferay.cdi.weld.container.internal.BundleDeployment;
import com.liferay.cdi.weld.container.internal.CdiHelper;
import com.liferay.cdi.weld.container.internal.FilterBuilder;
import com.liferay.cdi.weld.container.internal.ReferenceDependency;
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

		_weldBootstrap = new WeldBootstrap();

		_publishPhase = new PublishPhase(_bundle, _weldBootstrap, _cdiHelper, _beanDeploymentArchive);
	}

	public void close() {
		_publishPhase.close();

		if (_serviceTracker != null) {
			_serviceTracker.close();
		}

		_weldBootstrap.shutdown();
	}

	public void open() {
		List<Metadata<Extension>> extensions = new ArrayList<>();

		// Add internal extensions
		for (Metadata<Extension> meta : _weldBootstrap.loadExtensions(_bundleWiring.getClassLoader())) {
			extensions.add(meta);
		}

		// Add external extensions
		for (Metadata<Extension> meta : _extensions.values()) {
			extensions.add(meta);
		}

		Deployment deployment = new BundleDeployment(extensions, _beanDeploymentArchive);

		_weldBootstrap.startContainer(new SimpleEnvironment(), deployment);
		_weldBootstrap.startInitialization();

		_beanManager = _weldBootstrap.getManager(_beanDeploymentArchive);

		List<ReferenceDependency> referenceDependencies = getReferenceDependencies();

		if (!referenceDependencies.isEmpty()) {
			_cdiHelper.fireCdiEvent(new CdiEvent(CdiEvent.WAITING_FOR_SERVICES, _bundle, _cdiHelper.getExtenderBundle()));

			Filter filter = FilterBuilder.createReferenceFilter(referenceDependencies);

			_serviceTracker = new ServiceTracker<>(_bundleContext, filter, new ReferencePhaseCustomizer());

			_serviceTracker.open();
		}
		else {
			_publishPhase.open();
		}
	}

	private List<ReferenceDependency> getReferenceDependencies() {
		List<ReferenceDependency> referenceDependencies = new ArrayList<>();

		Set<Bean<?>> beansWithReferences = _beanManager.getBeans(Object.class, Reference);

		if (!beansWithReferences.isEmpty()) {
			for (Bean<?> bean : beansWithReferences) {
				for (InjectionPoint injectionPoint : bean.getInjectionPoints()) {
					for (Annotation annotation : injectionPoint.getQualifiers()) {
						if (annotation instanceof Reference) {
							referenceDependencies.add(new ReferenceDependency(bean, injectionPoint));
						}
					}
				}
			}
		}

		return referenceDependencies;
	}

	private static final AnnotationLiteral<Reference> Reference = new AnnotationLiteral<Reference>(){
		private static final long serialVersionUID = 1L;
	};

	private BeanDeploymentArchive _beanDeploymentArchive;
	private BeanManager _beanManager;
	private Bundle _bundle;
	private BundleContext _bundleContext;
	private BundleWiring _bundleWiring;
	private CdiHelper _cdiHelper;
	private final Map<ServiceReference<Extension>, Metadata<Extension>> _extensions;
	private PublishPhase _publishPhase;
	private final List<ReferenceDependency> _referenceDependencies = new CopyOnWriteArrayList<>();
	private ServiceTracker<?, ?> _serviceTracker;
	private Bootstrap _weldBootstrap;

	private class ReferencePhaseCustomizer implements ServiceTrackerCustomizer<Object, ReferenceDependency> {

		@Override
		public ReferenceDependency addingService(ServiceReference<Object> reference) {
			Iterator<ReferenceDependency> iterator = _referenceDependencies.iterator();

			ReferenceDependency trackedDependency = null;

			while (iterator.hasNext()) {
				ReferenceDependency extentionDependency = iterator.next();

				if (extentionDependency.matches(reference)) {
					iterator.remove();
					trackedDependency = extentionDependency;

					Object service = _bundleContext.getService(reference);

					trackedDependency.bind(reference, service);

					break;
				}
			}

			if (_referenceDependencies.isEmpty()) {
				_publishPhase.open();
			}

			return trackedDependency;
		}

		@Override
		public void modifiedService(ServiceReference<Object> reference, ReferenceDependency referenceDependency) {
		}

		@Override
		public void removedService(ServiceReference<Object> reference, ReferenceDependency referenceDependency) {
			_publishPhase.close();
			_bundleContext.ungetService(reference);
			_referenceDependencies.add(referenceDependency);
		}

	}

}

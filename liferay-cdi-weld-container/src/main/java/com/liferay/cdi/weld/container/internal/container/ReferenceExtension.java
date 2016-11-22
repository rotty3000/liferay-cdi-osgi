package com.liferay.cdi.weld.container.internal.container;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionPoint;

import org.osgi.framework.BundleContext;
import org.osgi.service.cdi.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@SuppressWarnings("rawtypes")
public class ReferenceExtension implements Extension {

	public ReferenceExtension(List<ReferenceDependency> referenceDependencies, BundleContext bundleContext) {
		_referenceDependencies = referenceDependencies;
		_bundleContext = bundleContext;
	}

	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
		for (ReferenceDependency referenceDependency : _referenceDependencies) {
			referenceDependency.addBean(abd);
		}

		// Add the BundleContext as a bean

		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - Bean discovery complete");
		}
	}

	void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - Bean discovery started");
		}
	}

	void processInjectionTarget(@Observes ProcessInjectionPoint pip) {
		InjectionPoint injectionPoint = pip.getInjectionPoint();
		Annotated annotated = injectionPoint.getAnnotated();
		Reference reference = annotated.getAnnotation(Reference.class);

		if (reference == null) {
			return;
		}

		try {
			ReferenceDependency referenceDependency = new ReferenceDependency(
				reference, injectionPoint, _bundleContext);

			_referenceDependencies.add(referenceDependency);

			if (_log.isDebugEnabled()) {
				_log.debug("CDIe - Found OSGi service reference {}", referenceDependency);
			}
		}
		catch (Exception e) {
			if (_log.isErrorEnabled()) {
				_log.error("CDIe - Error on reference", e);
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(ReferenceExtension.class);

	private final BundleContext _bundleContext;
	private final List<ReferenceDependency> _referenceDependencies;

}
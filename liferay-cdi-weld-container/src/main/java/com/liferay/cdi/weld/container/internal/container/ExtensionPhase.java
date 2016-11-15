package com.liferay.cdi.weld.container.internal.container;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.cdi.CdiEvent;
import org.osgi.service.cdi.Constants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.liferay.cdi.weld.container.internal.CdiHelper;
import com.liferay.cdi.weld.container.internal.ExtensionDependency;
import com.liferay.cdi.weld.container.internal.ExtensionMetadata;
import com.liferay.cdi.weld.container.internal.FilterBuilder;

public class ExtensionPhase {

	public ExtensionPhase(
		BundleContext bundleContext, CdiHelper cdiHelper, BeanDeploymentArchive beanDeploymentArchive) {

		_bundleContext = bundleContext;
		_cdiHelper = cdiHelper;
		_beanDeploymentArchive = beanDeploymentArchive;

		_bundle = bundleContext.getBundle();
		_bundleWiring = _bundle.adapt(BundleWiring.class);

		_referencePhase = new ReferencePhase(_bundleContext, _cdiHelper, _beanDeploymentArchive, _extensions);
	}

	public void close() {
		_cdiHelper.fireCdiEvent(
			new CdiEvent(CdiEvent.DESTROYING, _bundleContext.getBundle(), _cdiHelper.getExtenderBundle()));

		if (_extensionTracker != null) {
			_extensionTracker.close();
		}
		else {
			_referencePhase.close();
		}

		_cdiHelper.fireCdiEvent(
			new CdiEvent(CdiEvent.DESTROYED, _bundleContext.getBundle(), _cdiHelper.getExtenderBundle()));
	}

	public void open() {
		_cdiHelper.fireCdiEvent(new CdiEvent(CdiEvent.CREATING, _bundle, _cdiHelper.getExtenderBundle()));

		_extensionDependencies.addAll(getExtensionDependencies());

		if (!_extensionDependencies.isEmpty()) {
			_cdiHelper.fireCdiEvent(
				new CdiEvent(CdiEvent.WAITING_FOR_EXTENSIONS, _bundle, _cdiHelper.getExtenderBundle()));

			Filter filter = FilterBuilder.createExtensionFilter(_extensionDependencies);

			_extensionTracker = new ServiceTracker<>(
				_bundleContext, filter, new ExtensionPhaseCustomizer());

			_extensionTracker.open();
		}
		else {
			_referencePhase.open();
		}
	}

	private List<ExtensionDependency> getExtensionDependencies() {
		List<ExtensionDependency> extensionDependencies = new ArrayList<>();

		List<BundleWire> requiredWires = _bundleWiring.getRequiredWires(Constants.CDI_EXTENSION_CAPABILITY);

		for (BundleWire wire : requiredWires) {
			Map<String, Object> attributes = wire.getCapability().getAttributes();
			if (attributes.containsKey(Constants.EXTENSION_ATTRIBUTE)) {
				ExtensionDependency extensionDependency = new ExtensionDependency(
					_bundleContext, wire.getProvider().getBundle().getBundleId(),
					(String)attributes.get(Constants.EXTENSION_ATTRIBUTE));

				extensionDependencies.add(extensionDependency);
			}
		}

		return extensionDependencies;
	}

	private final BeanDeploymentArchive _beanDeploymentArchive;
	private final BundleContext _bundleContext;
	private final Bundle _bundle;
	private final BundleWiring _bundleWiring;
	private final CdiHelper _cdiHelper;
	private final Map<ServiceReference<Extension>, Metadata<Extension>> _extensions =
			new ConcurrentSkipListMap<>(Comparator.reverseOrder());
	private final List<ExtensionDependency> _extensionDependencies = new CopyOnWriteArrayList<>();
	private ServiceTracker<Extension, ExtensionDependency> _extensionTracker;
	private ReferencePhase _referencePhase;

	private class ExtensionPhaseCustomizer implements ServiceTrackerCustomizer<Extension, ExtensionDependency> {

		@Override
		public ExtensionDependency addingService(ServiceReference<Extension> reference) {
			Iterator<ExtensionDependency> iterator = _extensionDependencies.iterator();

			ExtensionDependency trackedDependency = null;

			while (iterator.hasNext()) {
				ExtensionDependency extentionDependency = iterator.next();

				if (extentionDependency.matches(reference)) {
					iterator.remove();
					trackedDependency = extentionDependency;

					Extension extension = _bundleContext.getService(reference);

					_extensions.put(reference, new ExtensionMetadata(extension, reference.getBundle().toString()));

					break;
				}
			}

			if (_extensionDependencies.isEmpty()) {
				_referencePhase.open();
			}

			return trackedDependency;
		}

		@Override
		public void modifiedService(ServiceReference<Extension> reference, ExtensionDependency extentionDependency) {
		}

		@Override
		public void removedService(ServiceReference<Extension> reference, ExtensionDependency extentionDependency) {
			_referencePhase.close();
			_extensions.remove(reference);
			_bundleContext.ungetService(reference);
			_extensionDependencies.add(extentionDependency);
		}

	}

}

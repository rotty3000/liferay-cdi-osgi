/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.cdi.weld.container.internal.container;

import java.util.Comparator;
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
import org.osgi.service.cdi.CdiExtenderConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.liferay.cdi.weld.container.internal.CdiContainerState;

public class Phase_2_Extension {

	public Phase_2_Extension(
		Bundle bundle, CdiContainerState cdiContainerState, BeanDeploymentArchive beanDeploymentArchive) {

		_bundle = bundle;
		_cdiContainerState = cdiContainerState;
		_beanDeploymentArchive = beanDeploymentArchive;
		_bundleContext = _bundle.getBundleContext();
		_bundleWiring = _bundle.adapt(BundleWiring.class);
		_extensions = new ConcurrentSkipListMap<>(Comparator.reverseOrder());

		_phase3 = new Phase_3_Reference(_bundle, _cdiContainerState, _beanDeploymentArchive, _extensions);
	}

	public void close() {
		_cdiContainerState.fire(CdiEvent.State.DESTROYING);

		if (_extensionTracker != null) {
			_extensionTracker.close();
		}
		else {
			_phase3.close();
		}

		_cdiContainerState.fire(CdiEvent.State.DESTROYED);

		_cdiContainerState.close();
	}

	public void open() {
		_cdiContainerState.fire(CdiEvent.State.CREATING);

		findExtensionDependencies();

		if (!_extensionDependencies.isEmpty()) {
			_cdiContainerState.fire(CdiEvent.State.WAITING_FOR_EXTENSIONS);

			Filter filter = FilterBuilder.createExtensionFilter(_extensionDependencies);

			_extensionTracker = new ServiceTracker<>(_bundleContext, filter, new ExtensionPhaseCustomizer());

			_extensionTracker.open();
		}
		else {
			_phase3.open();
		}
	}

	private void findExtensionDependencies() {
		List<BundleWire> requiredWires = _bundleWiring.getRequiredWires(CdiExtenderConstants.CDI_EXTENSION);

		for (BundleWire wire : requiredWires) {
			Map<String, Object> attributes = wire.getCapability().getAttributes();

			String extension = (String)attributes.get(CdiExtenderConstants.CDI_EXTENSION);

			if (extension != null) {
				ExtensionDependency extensionDependency = new ExtensionDependency(
					_bundleContext, wire.getProvider().getBundle().getBundleId(), extension);

				_extensionDependencies.add(extensionDependency);
			}
		}
	}

	private final BeanDeploymentArchive _beanDeploymentArchive;
	private final BundleContext _bundleContext;
	private final Bundle _bundle;
	private final BundleWiring _bundleWiring;
	private final CdiContainerState _cdiContainerState;
	private final Map<ServiceReference<Extension>, Metadata<Extension>> _extensions;
	private final List<ExtensionDependency> _extensionDependencies = new CopyOnWriteArrayList<>();
	private final Phase_3_Reference _phase3;

	private ServiceTracker<Extension, ExtensionDependency> _extensionTracker;

	private class ExtensionPhaseCustomizer implements ServiceTrackerCustomizer<Extension, ExtensionDependency> {

		@Override
		public ExtensionDependency addingService(ServiceReference<Extension> reference) {
			ExtensionDependency trackedDependency = null;

			for (ExtensionDependency extensionDependency : _extensionDependencies) {
				if (extensionDependency.matches(reference)) {
					_extensionDependencies.remove(extensionDependency);
					trackedDependency = extensionDependency;

					Extension extension = _bundleContext.getService(reference);

					_extensions.put(reference, new ExtensionMetadata(extension, reference.getBundle().toString()));

					break;
				}
			}

			if ((trackedDependency != null) && _extensionDependencies.isEmpty()) {
				_phase3.open();
			}

			return trackedDependency;
		}

		@Override
		public void modifiedService(ServiceReference<Extension> reference, ExtensionDependency extentionDependency) {
		}

		@Override
		public void removedService(ServiceReference<Extension> reference, ExtensionDependency extentionDependency) {
			if (_extensionDependencies.isEmpty()) {
				_phase3.close();

				_cdiContainerState.fire(CdiEvent.State.WAITING_FOR_EXTENSIONS);
			}
			_extensions.remove(reference);
			_bundleContext.ungetService(reference);
			_extensionDependencies.add(extentionDependency);
		}

	}

}

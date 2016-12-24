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

package com.liferay.cdi.container.internal.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeansXml;
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

public class Phase_3_Reference {

	public Phase_3_Reference(
		Bundle bundle, CdiContainerState cdiContainerState, Map<ServiceReference<Extension>,
		Metadata<Extension>> extensions, Collection<String> beanClassNames, BeansXml beansXml) {

		_bundle = bundle;
		_cdiContainerState = cdiContainerState;
		_extensions = extensions;
		_bundleContext = _bundle.getBundleContext();
		_bundleWiring = _bundle.adapt(BundleWiring.class);
		_beanClassNames = beanClassNames;
		_beansXml = beansXml;
	}

	public void close() {
		if (_serviceTracker != null) {
			_serviceTracker.close();

			_serviceTracker = null;
		}
		else {
			_publishPhase.close();

			_publishPhase = null;
		}
	}

	public void open() {
		BeanDeploymentArchive beanDeploymentArchive = new BundleDeploymentArchive(
			_bundleWiring, _cdiContainerState.getId(), _beanClassNames, _beansXml,
			_cdiContainerState.getExtenderBundle());

		WeldBootstrap bootstrap = new WeldBootstrap();

		List<Metadata<Extension>> extensions = new ArrayList<>();

		// Add the internal extensions
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

		Deployment deployment = new BundleDeployment(extensions, beanDeploymentArchive);

		bootstrap.startContainer(new SimpleEnvironment(), deployment);

		BeanManager beanManager = bootstrap.getManager(beanDeploymentArchive);

		_cdiContainerState.setBeanManager(beanManager);

		bootstrap.startInitialization();
		bootstrap.deployBeans();

		if (!_referenceDependencies.isEmpty()) {
			Filter filter = FilterBuilder.createReferenceFilter(_referenceDependencies);

			_cdiContainerState.fire(CdiEvent.State.WAITING_FOR_SERVICES, filter.toString());

			_serviceTracker = new ServiceTracker<>(_bundleContext, filter, new ReferencePhaseCustomizer(bootstrap));

			_serviceTracker.open();
		}
		else {
			_publishPhase = new Phase_4_Publish(_bundle, _cdiContainerState);

			_publishPhase.open(bootstrap);
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(Phase_3_Reference.class);

	private final Collection<String> _beanClassNames;
	private final BeansXml _beansXml;
	private final Bundle _bundle;
	private final BundleContext _bundleContext;
	private final BundleWiring _bundleWiring;
	private final CdiContainerState _cdiContainerState;
	private final Map<ServiceReference<Extension>, Metadata<Extension>> _extensions;
	private Phase_4_Publish _publishPhase;
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
				_publishPhase = new Phase_4_Publish(_bundle, _cdiContainerState);

				_publishPhase.open(_bootstrap);
			}
			else if (_log.isDebugEnabled()) {
				_log.debug("CDIe - Still waiting for serivces {}", _referenceDependencies);
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

				_publishPhase = null;

				_cdiContainerState.fire(CdiEvent.State.WAITING_FOR_SERVICES);
			}
			_referenceDependencies.add(referenceDependency);
		}

		private final WeldBootstrap _bootstrap;

	}

}
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
			_lock.lock();

			try {
				if (_publishPhase != null) {
					_publishPhase.close();

					_publishPhase = null;
				}
			}
			finally {
				_lock.unlock();
			}
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
		extensions.add(new ExtensionMetadata(new ServiceExtension(_services), _bundle.toString()));

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
			_lock.lock();

			try {
				_publishPhase = new Phase_4_Publish(this, bootstrap);

				_publishPhase.open();
			}
			finally {
				_lock.unlock();
			}
		}
	}

	private final Collection<String> _beanClassNames;
	private final BeansXml _beansXml;
	final Bundle _bundle;
	private final BundleContext _bundleContext;
	private final BundleWiring _bundleWiring;
	final CdiContainerState _cdiContainerState;
	private final Map<ServiceReference<Extension>, Metadata<Extension>> _extensions;
	private final Lock _lock = new ReentrantLock(true);
	private Phase_4_Publish _publishPhase;
	final List<ReferenceDependency> _referenceDependencies = new CopyOnWriteArrayList<>();
	final List<ServiceDeclaration> _services = new CopyOnWriteArrayList<>();

	ServiceTracker<?, ?> _serviceTracker;

	private class ReferencePhaseCustomizer implements ServiceTrackerCustomizer<Object, Object> {

		public ReferencePhaseCustomizer(WeldBootstrap bootstrap) {
			_bootstrap = bootstrap;
		}

		@Override
		public Object addingService(ServiceReference<Object> reference) {
			_lock.lock();

			try {
				if (_publishPhase != null) {
					return null;
				}

				boolean matches = false;
				boolean resolved = true;

				for (ReferenceDependency referenceDependency : _referenceDependencies) {
					if (referenceDependency.matches(reference)) {
						referenceDependency.resolve(reference);
						matches = true;
					}
					if (!referenceDependency.isResolved()) {
						resolved = false;
					}
				}

				if (!matches) {
					return null;
				}

				if (resolved) {
					_publishPhase = new Phase_4_Publish(Phase_3_Reference.this, _bootstrap);

					_publishPhase.open();
				}

				return new Object();
			}
			finally {
				_lock.unlock();
			}
		}

		@Override
		public void modifiedService(ServiceReference<Object> reference, Object object) {
		}

		@Override
		public void removedService(ServiceReference<Object> reference, Object object) {
			_lock.lock();

			try {
				if (_publishPhase != null) {
					_publishPhase.close();

					_publishPhase = null;

					_cdiContainerState.fire(CdiEvent.State.WAITING_FOR_SERVICES);
				}

				for (ReferenceDependency referenceDependency : _referenceDependencies) {
					if (referenceDependency.matches(reference)) {
						referenceDependency.unresolve(reference);
					}
				}
			}
			finally {
				_lock.unlock();
			}
		}

		private final WeldBootstrap _bootstrap;

	}

}
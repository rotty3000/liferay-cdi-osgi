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

import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.spi.ObjectFactory;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cdi.CdiEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Phase_4_Publish {

	public Phase_4_Publish(Bundle bundle, CdiContainerState cdiContainerState, List<ServiceDeclaration> services) {
		_bundle = bundle;
		_cdiContainerState = cdiContainerState;
		_services = services;
		_bundleContext = _bundle.getBundleContext();
	}

	public void close() {
		for (ServiceRegistration<?> registration : _registrations) {
			try {
				registration.unregister();
			}
			catch (IllegalStateException ise) {
				if (_log.isTraceEnabled()) {
					_log.trace("Service already unregistered {}", registration);
				}
			}
		}

		_registrations.clear();

		if (_beanManagerRegistration != null) {
			try {
				_beanManagerRegistration.unregister();
			}
			catch (Exception e) {
				if (_log.isTraceEnabled()) {
					_log.trace("Service already unregistered {}", _beanManagerRegistration);
				}
			}
		}

		if (_objectFactoryRegistration != null) {
			try {
				_objectFactoryRegistration.unregister();
			}
			catch (Exception e) {
				if (_log.isTraceEnabled()) {
					_log.trace("Service already unregistered {}", _objectFactoryRegistration);
				}
			}
		}

		_bootstrap.shutdown();
	}

	public void open(Bootstrap bootstrap) {
		_cdiContainerState.fire(CdiEvent.State.SATISFIED);

		_bootstrap = bootstrap;

		_bootstrap.validateBeans();
		_bootstrap.endInitialization();

		processServiceDeclarations();

		BeanManager beanManager = _cdiContainerState.getBeanManager();

		_beanManagerRegistration = _bundle.getBundleContext().registerService(
			BeanManager.class, beanManager, null);

		_cdiContainerState.fire(CdiEvent.State.CREATED);
	}

	private void processServiceDeclarations() {
		// TODO check for beans to be published as services explicitly defined through the requirement

		for (ServiceDeclaration serviceDeclaration : _services) {
			processServiceDeclaration(serviceDeclaration);
		}
	}

	private void processServiceDeclaration(ServiceDeclaration serviceDeclaration) {
		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - Publishing bean {} as service.", serviceDeclaration.getBean());
		}

		String[] classNames = serviceDeclaration.getClassNames();
		Object serviceInstance = serviceDeclaration.getServiceInstance();
		Dictionary<String,Object> properties = serviceDeclaration.getProperties();

		_registrations.add(
			_bundleContext.registerService(classNames, serviceInstance, properties));
	}

	private static final Logger _log = LoggerFactory.getLogger(Phase_4_Publish.class);

	private Bootstrap _bootstrap;
	private final Bundle _bundle;
	private final BundleContext _bundleContext;
	private final CdiContainerState _cdiContainerState;
	private final List<ServiceDeclaration> _services;
	private final List<ServiceRegistration<?>> _registrations = new CopyOnWriteArrayList<>();

	private ServiceRegistration<BeanManager> _beanManagerRegistration;
	private ServiceRegistration<ObjectFactory> _objectFactoryRegistration;

}
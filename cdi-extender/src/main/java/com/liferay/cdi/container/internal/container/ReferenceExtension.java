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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionPoint;

import org.osgi.framework.BundleContext;
import org.osgi.service.cdi.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.cdi.container.internal.bean.BundleContextBean;

@ApplicationScoped
public class ReferenceExtension implements Extension {

	public ReferenceExtension(List<ReferenceDependency> referenceDependencies, BundleContext bundleContext) {
		_referenceDependencies = referenceDependencies;
		_bundleContext = bundleContext;
	}

	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager manager) {
		for (ReferenceDependency referenceDependency : _referenceDependencies) {
			referenceDependency.addBean(abd);
		}

		abd.addBean(new BundleContextBean(_bundleContext));

		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - Bean discovery complete");
		}
	}

	void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - Bean discovery started");
		}
	}

	void processInjectionTarget(
		@Observes @SuppressWarnings("rawtypes") ProcessInjectionPoint pip, BeanManager manager) {

		InjectionPoint injectionPoint = pip.getInjectionPoint();
		Annotated annotated = injectionPoint.getAnnotated();
		Reference reference = annotated.getAnnotation(Reference.class);

		if (reference == null) {
			return;
		}

		try {
			ReferenceDependency referenceDependency = new ReferenceDependency(
				reference, manager, injectionPoint, _bundleContext);

			_referenceDependencies.add(referenceDependency);

			if (_log.isDebugEnabled()) {
				_log.debug("CDIe - Found OSGi service reference {}", referenceDependency);
			}
		}
		catch (Exception e) {
			if (_log.isErrorEnabled()) {
				_log.error("CDIe - Error on reference {}", reference, e);
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(ReferenceExtension.class);

	private final BundleContext _bundleContext;
	private final List<ReferenceDependency> _referenceDependencies;

}
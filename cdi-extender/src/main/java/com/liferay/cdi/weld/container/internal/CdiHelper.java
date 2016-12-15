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

package com.liferay.cdi.weld.container.internal;

import java.util.Hashtable;
import java.util.Map;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cdi.CdiContainer;
import org.osgi.service.cdi.CdiEvent;
import org.osgi.service.cdi.CdiExtenderConstants;
import org.osgi.service.cdi.CdiListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.cdi.weld.container.internal.container.CdiContainerService;

public class CdiHelper {

	public static final AnnotationLiteral<Any> ANY = new AnnotationLiteral<Any>() {
		private static final long serialVersionUID = 1L;
	};

	public CdiHelper(
		Bundle extenderBundle, Map<ServiceReference<CdiListener>, CdiListener> listeners, BundleContext bundleContext) {

		_extenderBundle = extenderBundle;
		_listeners = listeners;

		_cdiContainerService = new CdiContainerService();

		Hashtable<String, Object> properties = new Hashtable<>();

		properties.put(CdiExtenderConstants.CDI_EXTENDER_CONTAINER_STATE, CdiEvent.State.CREATING);

		_cdiContainerRegistration = bundleContext.registerService(
			CdiContainer.class, _cdiContainerService, properties);
	}

	public void close() {
		try {
			_cdiContainerRegistration.unregister();
		}
		catch (Exception e) {
			if (_log.isTraceEnabled()) {
				_log.trace("Service already unregistered {}", _cdiContainerRegistration);
			}
		}
	}

	public Bundle getExtenderBundle() {
		return _extenderBundle;
	}

	public void fireCdiEvent(CdiEvent event) {
		fireCdiEvent(event, null);
	}

	public void fireCdiEvent(CdiEvent event, BeanManager beanManager) {
		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - Event {}", event);
		}

		if (beanManager != null) {
			_cdiContainerService.setBeanManager(beanManager);
		}

		updateState(event);

		for (CdiListener listener : _listeners.values()) {
			try {
				listener.cdiEvent(event);
			}
			catch (Throwable t) {
				if (_log.isErrorEnabled()) {
					_log.error("CDIe - CdiListener failed", t);
				}
			}
		}
	}

	private void updateState(CdiEvent event) {
		ServiceReference<CdiContainer> reference = _cdiContainerRegistration.getReference();

		if (event.getType() == reference.getProperty(CdiExtenderConstants.CDI_EXTENDER_CONTAINER_STATE)) {
			return;
		}

		Hashtable<String, Object> properties = new Hashtable<>();

		for (String key : reference.getPropertyKeys()) {
			properties.put(key, reference.getProperty(key));
		}

		properties.put(CdiExtenderConstants.CDI_EXTENDER_CONTAINER_STATE, event.getType());

		_cdiContainerRegistration.setProperties(properties);
	}

	private static final Logger _log = LoggerFactory.getLogger(CdiHelper.class);

	private final ServiceRegistration<CdiContainer> _cdiContainerRegistration;
	private final CdiContainerService _cdiContainerService;
	private final Bundle _extenderBundle;
	private final Map<ServiceReference<CdiListener>, CdiListener> _listeners;

}
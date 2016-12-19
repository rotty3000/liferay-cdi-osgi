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

package com.liferay.cdi.container.internal;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.CdiListener;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.cdi.container.internal.container.Phase_1_Init;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - starting {}", bundleContext.getBundle());
		}

		_bundleContext = bundleContext;

		_listenerTracker = new ServiceTracker<>(_bundleContext, CdiListener.class, new CdiListenerCustomizer());

		_bundleTracker = new BundleTracker<Phase_1_Init>(
			_bundleContext, Bundle.ACTIVE | Bundle.STARTING,
			new CdiBundleCustomizer(_bundleContext.getBundle(), _listeners));

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				_listenerTracker.open();
				_bundleTracker.open();

				if (_log.isDebugEnabled()) {
					_log.debug("CDIe - started {}", bundleContext.getBundle());
				}
			}

		});

		thread.start();
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - stoping {}", bundleContext.getBundle());
		}

		_bundleTracker.close();
		_listenerTracker.close();

		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - stoped {}", bundleContext.getBundle());
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(Activator.class);

	private BundleContext _bundleContext;
	private BundleTracker<Phase_1_Init> _bundleTracker;
	private Map<ServiceReference<CdiListener>, CdiListener> _listeners =
		new ConcurrentSkipListMap<>(Comparator.reverseOrder());
	private ServiceTracker<CdiListener, CdiListener> _listenerTracker;

	private class CdiListenerCustomizer implements ServiceTrackerCustomizer<CdiListener, CdiListener> {

		@Override
		public CdiListener addingService(ServiceReference<CdiListener> reference) {
			CdiListener cdiListener = _bundleContext.getService(reference);
			_listeners.put(reference, cdiListener);
			return cdiListener;
		}

		@Override
		public void modifiedService(ServiceReference<CdiListener> reference, CdiListener service) {
		}

		@Override
		public void removedService(ServiceReference<CdiListener> reference, CdiListener service) {
			_listeners.remove(reference);
			_bundleContext.ungetService(reference);
		}

	}

}
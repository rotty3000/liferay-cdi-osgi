/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.cdi.weld.container.internal;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.CdiListener;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.cdi.weld.container.internal.container.WeldCdiContainer;

public class CdiBundleCustomizer implements BundleTrackerCustomizer<WeldCdiContainer> {

	public CdiBundleCustomizer(Bundle extenderBundle, Map<ServiceReference<CdiListener>, CdiListener> listeners) {
		_extenderBundle = extenderBundle;
		_listeners = listeners;
	}

	@Override
	public WeldCdiContainer addingBundle(Bundle bundle, BundleEvent bundleEvent) {
		if (!CapabilityUtil.requiresCdiExtender(bundle)) {
			return null;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("CDI bundle found {}", bundle);
		}

		CdiHelper cdiHelper = new CdiHelper(_extenderBundle, _listeners);

		try {
			WeldCdiContainer weldCdiContainer = new WeldCdiContainer(bundle, cdiHelper);

			weldCdiContainer.open();

			return weldCdiContainer;
		}
		catch (Throwable t) {
			if (_log.isErrorEnabled()) {
				_log.error("Exception during init", t);
			}
		}

		return null;
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent bundleEvent, WeldCdiContainer weldCdiContainer) {
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent bundleEvent, WeldCdiContainer weldCdiContainer) {
		if (_log.isDebugEnabled()) {
			_log.debug("CDI bundle removed {}", bundle);
		}

		weldCdiContainer.close();
	}

	private static final Logger _log = LoggerFactory.getLogger(CdiBundleCustomizer.class);

	private final Bundle _extenderBundle;
	private final Map<ServiceReference<CdiListener>, CdiListener> _listeners;

}

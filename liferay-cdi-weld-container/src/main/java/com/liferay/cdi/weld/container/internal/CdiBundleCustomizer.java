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

		CdiHelper cdiHelper = new CdiHelper(_extenderBundle, _listeners);

		WeldCdiContainer weldCdiContainer = new WeldCdiContainer(bundle, cdiHelper);

		weldCdiContainer.open();

		return weldCdiContainer;
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent bundleEvent, WeldCdiContainer weldCdiContainer) {
		removedBundle(bundle, bundleEvent, weldCdiContainer);
		addingBundle(bundle, bundleEvent);
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent bundleEvent, WeldCdiContainer weldCdiContainer) {
		weldCdiContainer.close();
	}

	private final Bundle _extenderBundle;
	private final Map<ServiceReference<CdiListener>, CdiListener> _listeners;

}

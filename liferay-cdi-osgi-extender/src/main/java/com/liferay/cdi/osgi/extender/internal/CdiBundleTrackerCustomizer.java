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
package com.liferay.cdi.osgi.extender.internal;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;

import org.osgi.util.tracker.BundleTrackerCustomizer;

import com.liferay.cdi.osgi.spi.WabCdiContainer;
import com.liferay.cdi.osgi.spi.WabCdiContainerService;


/**
 * @author  Neil Griffin
 */
public class CdiBundleTrackerCustomizer implements BundleTrackerCustomizer<WabCdiContainer> {

	private BundleContext bundleContext;
	private Map<Bundle, WabCdiContainer> startedWabCdiContainers;
	private WabCdiContainerService wabCdiContainerService;

	public CdiBundleTrackerCustomizer(BundleContext bundleContext, Map<Bundle, WabCdiContainer> startedWabCdiContainers,
		WabCdiContainerService wabCdiContainerService) {
		this.bundleContext = bundleContext;
		this.startedWabCdiContainers = startedWabCdiContainers;
		this.wabCdiContainerService = wabCdiContainerService;
	}

	@Override
	public WabCdiContainer addingBundle(Bundle bundle, BundleEvent bundleEvent) {

		WabCdiContainer wabCdiContainer = null;

		// If the specified bundle requires the "osgi.cdi" extender capability, then
		if (CapabilityUtil.requiresCdiExtender(bundleContext, bundle)) {

			// If the Liferay WabExtender sent the onStartup event prior to the BundleTracker being made aware of the
			// bundle, then remove it from the list of started containers and return it. Otherwise, get an non-started
			// container from the service and return it. Startup will take place when the Liferay WabExtender sends the
			// onStartup event.
			wabCdiContainer = startedWabCdiContainers.remove(bundle);

			if (wabCdiContainer == null) {
				wabCdiContainer = wabCdiContainerService.getWabCdiContainer(bundle);
			}
		}

		return wabCdiContainer;
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent bundleEvent, WabCdiContainer wabCdiContainer) {
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent bundleEvent, WabCdiContainer wabCdiContainer) {
	}
}

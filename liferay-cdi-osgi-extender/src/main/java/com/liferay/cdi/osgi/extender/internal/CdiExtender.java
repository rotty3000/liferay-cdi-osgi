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
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import org.osgi.util.tracker.BundleTracker;

import com.liferay.cdi.osgi.spi.WabCdiContainer;
import com.liferay.cdi.osgi.spi.WabCdiContainerService;

import com.liferay.portal.osgi.web.wab.extender.WabInitializer;
import com.liferay.portal.osgi.web.wab.extender.WabInitializerRegistry;


/**
 * @author  Neil Griffin
 */
@Component(immediate = true)
public class CdiExtender {

	@Reference
	private WabCdiContainerService wabCdiContainerService;

	@Reference
	private WabInitializerRegistry wabInitializerRegistry;

	private BundleTracker<WabCdiContainer> bundleTracker;
	private Map<Bundle, WabCdiContainer> startedWabCdiContainers;
	private WabInitializer wabInitializer;

	@Activate
	protected void activate(BundleContext bundleContext) {

		startedWabCdiContainers = new ConcurrentHashMap<Bundle, WabCdiContainer>();

		CdiBundleTrackerCustomizer cdiBundleTrackerCustomizer = new CdiBundleTrackerCustomizer(bundleContext,
				startedWabCdiContainers, wabCdiContainerService);
		bundleTracker = new BundleTracker<WabCdiContainer>(bundleContext, Bundle.ACTIVE, cdiBundleTrackerCustomizer);
		bundleTracker.open();
		wabInitializer = new CdiWabInitializer(bundleContext, bundleTracker, startedWabCdiContainers,
				wabCdiContainerService);
		wabInitializerRegistry.addWabInitializer(wabInitializer);
		System.err.println("*** CdiExtender.activate() ADDED wabInitializer=" + wabInitializer);
	}

	@Deactivate
	protected void deactivate() {
		bundleTracker.close();
		bundleTracker = null;
		wabInitializerRegistry.removeWabInitializer(wabInitializer);
		System.err.println("*** CdiExtender.activate() REMOVED wabInitializer=" + wabInitializer);
	}
}

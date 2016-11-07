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
import java.util.Set;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.osgi.util.tracker.BundleTracker;

import com.liferay.cdi.osgi.spi.WabCdiContainer;
import com.liferay.cdi.osgi.spi.WabCdiContainerService;

import com.liferay.portal.osgi.web.wab.extender.WabInitializer;


/**
 * @author  Neil Griffin
 */
public class CdiWabInitializer implements WabInitializer {

	private BundleContext bundleContext;
	private BundleTracker bundleTracker;
	private Map<Bundle, WabCdiContainer> startedWabCdiContainers;
	private WabCdiContainerService wabCdiContainerService;

	public CdiWabInitializer(BundleContext bundleContext, BundleTracker<WabCdiContainer> bundleTracker,
		Map<Bundle, WabCdiContainer> startedWabCdiContainers, WabCdiContainerService wabCdiContainerService) {

		this.bundleContext = bundleContext;
		this.bundleTracker = bundleTracker;
		this.startedWabCdiContainers = startedWabCdiContainers;
		this.wabCdiContainerService = wabCdiContainerService;
	}

	@Override
	public void onStartup(Bundle bundle, Set<Class<?>> classes, ServletContext servletContext) {

		// If the specified bundle is being tracked as one that requires the "osgi.cdi" extender capability, then
		// startup the CDI container.
		WabCdiContainer wabCdiContainer = (WabCdiContainer) bundleTracker.getObject(bundle);

		if (wabCdiContainer != null) {
			wabCdiContainer.startup(servletContext);
			servletContext.setAttribute("beanManagerHack", wabCdiContainer.getBeanManager());
		}

		// Otherwise, if the bundle requires the "osgi.cdi" extender capability, then remember this "onStartup" event
		// so that the CDI container can be bootstrapped later when the tracker starts tracking the bundle.
		else if (CapabilityUtil.requiresCdiExtender(bundleContext, bundle)) {
			wabCdiContainer = wabCdiContainerService.getWabCdiContainer(bundle);
			wabCdiContainer.startup(servletContext);
			startedWabCdiContainers.put(bundle, wabCdiContainer);
		}
	}
}

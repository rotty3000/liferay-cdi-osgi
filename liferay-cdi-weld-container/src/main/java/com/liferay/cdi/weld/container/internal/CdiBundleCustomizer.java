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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.CdiContainer;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author  Neil Griffin
 */
public class CdiBundleCustomizer implements BundleTrackerCustomizer<CdiContainer> {

	@Override
	public CdiContainer addingBundle(Bundle bundle, BundleEvent bundleEvent) {
		if (!CapabilityUtil.requiresCdiExtender(bundle)) {
			return null;
		}

		BundleContext currentBundleContext = bundle.getBundleContext();

		ServiceReference<CdiContainer> reference = currentBundleContext.getServiceReference(CdiContainer.class);

		return currentBundleContext.getService(reference);
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent bundleEvent, CdiContainer cdiContainer) {
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent bundleEvent, CdiContainer cdiContainer) {
	}

}

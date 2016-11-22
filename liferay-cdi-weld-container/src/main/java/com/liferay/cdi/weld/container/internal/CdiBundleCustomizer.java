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

import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CdiListener;
import org.osgi.service.cdi.Constants;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.cdi.weld.container.internal.container.Phase_1_Init;

public class CdiBundleCustomizer implements BundleTrackerCustomizer<Phase_1_Init> {

	public CdiBundleCustomizer(Bundle extenderBundle, Map<ServiceReference<CdiListener>, CdiListener> listeners) {
		_extenderBundle = extenderBundle;
		_listeners = listeners;
	}

	@Override
	public Phase_1_Init addingBundle(Bundle bundle, BundleEvent bundleEvent) {
		if (!requiresCdiExtender(bundle)) {
			return null;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - bundle detected {}", bundle);
		}

		CdiHelper cdiHelper = new CdiHelper(_extenderBundle, _listeners);

		try {
			Phase_1_Init phase1 = new Phase_1_Init(bundle, cdiHelper);

			phase1.open();

			return phase1;
		}
		catch (Throwable t) {
			if (_log.isErrorEnabled()) {
				_log.error("CDIe - Exception during init", t);
			}
		}

		return null;
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent bundleEvent, Phase_1_Init phase1) {
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent bundleEvent, Phase_1_Init phase1) {
		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - bundle removed {}", bundle);
		}

		phase1.close();
	}

	private final boolean requiresCdiExtender(Bundle bundle) {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		List<BundleWire> requiredBundleWires = bundleWiring.getRequiredWires(ExtenderNamespace.EXTENDER_NAMESPACE);

		for (BundleWire bundleWire : requiredBundleWires) {
			Map<String, Object> attributes = bundleWire.getCapability().getAttributes();

			if (attributes.containsKey(ExtenderNamespace.EXTENDER_NAMESPACE) &&
				attributes.get(ExtenderNamespace.EXTENDER_NAMESPACE).equals(Constants.CDI_EXTENDER)) {

				Bundle providerWiringBundle = bundleWire.getProviderWiring().getBundle();

				if (providerWiringBundle.equals(_extenderBundle)) {
					return true;
				}
			}
		}

		return false;
	}

	private static final Logger _log = LoggerFactory.getLogger(CdiBundleCustomizer.class);

	private final Bundle _extenderBundle;
	private final Map<ServiceReference<CdiListener>, CdiListener> _listeners;

}

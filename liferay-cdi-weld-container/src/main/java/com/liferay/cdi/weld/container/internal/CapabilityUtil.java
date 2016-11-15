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
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.cdi.Constants;

/**
 * @author  Neil Griffin
 */
public class CapabilityUtil {

	public static boolean requiresCdiExtender(Bundle bundle) {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		List<BundleWire> requiredBundleWires = bundleWiring.getRequiredWires("osgi.extender");

		for (BundleWire bundleWire : requiredBundleWires) {
			Map<String, Object> attributes = bundleWire.getCapability().getAttributes();

			if (attributes.containsKey("osgi.extender") &&
				attributes.get("osgi.extender").equals(Constants.CDI_EXTENDER)) {

				Bundle providerWiringBundle = bundleWire.getProviderWiring().getBundle();

				if (providerWiringBundle.equals(extenderBundle)) {
					return true;
				}
			}
		}

		return false;
	}

	private static final Bundle extenderBundle = FrameworkUtil.getBundle(CapabilityUtil.class);

}

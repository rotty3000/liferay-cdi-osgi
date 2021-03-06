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

import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CdiEvent;
import org.osgi.service.cdi.CdiExtenderConstants;
import org.osgi.service.cdi.CdiListener;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.cdi.container.internal.container.CdiContainerState;
import com.liferay.cdi.container.internal.container.Phase_1_Init;

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

		CdiContainerState cdiHelper = new CdiContainerState(bundle, _extenderBundle, _listeners);

		Phase_1_Init initPhase = null;

		try {
			initPhase = new Phase_1_Init(bundle, cdiHelper);

			initPhase.open();

			return initPhase;
		}
		catch (Throwable t) {
			cdiHelper.fire(CdiEvent.State.FAILURE, t);

			if (initPhase != null) {
				initPhase.close();
			}

			cdiHelper.close();
		}

		return null;
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent bundleEvent, Phase_1_Init phase1) {
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent bundleEvent, Phase_1_Init phase1) {
		phase1.close();

		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - bundle removed {}", bundle);
		}
	}

	private final boolean requiresCdiExtender(Bundle bundle) {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		List<BundleWire> requiredBundleWires = bundleWiring.getRequiredWires(ExtenderNamespace.EXTENDER_NAMESPACE);

		for (BundleWire bundleWire : requiredBundleWires) {
			Map<String, Object> attributes = bundleWire.getCapability().getAttributes();

			if (attributes.containsKey(ExtenderNamespace.EXTENDER_NAMESPACE) &&
				attributes.get(ExtenderNamespace.EXTENDER_NAMESPACE).equals(CdiExtenderConstants.CDI_EXTENDER)) {

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

package com.liferay.cdi.test.cases;

import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CdiContainer;
import org.osgi.service.cdi.Constants;
import org.osgi.util.tracker.ServiceTracker;

public class CdiExtenderTests extends AbstractTestCase {

	@Override
	protected void setUp() throws Exception {
		cdiBundle = bundleContext.installBundle(null , getBundle("basic-beans.jar"));
		cdiBundle.start();
		cdiContainer = waitForCdiContainer(cdiBundle.getBundleId());
	}

	@Override
	protected void tearDown() throws Exception {
		cdiBundle.stop();
	}

	public void testStopExtender() throws Exception {
		Bundle cdiExtenderBundle = getCdiExtenderBundle();

		ServiceTracker<CdiContainer,CdiContainer> serviceTracker = getServiceTracker(bundle.getBundleId());

		assertNotNull(serviceTracker.waitForService(timeout));

		cdiExtenderBundle.stop();

		assertTrue(serviceTracker.isEmpty());

		cdiExtenderBundle.start();

		assertNotNull(serviceTracker.waitForService(timeout));

		serviceTracker.close();
	}

	public Bundle getCdiExtenderBundle() {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		List<BundleWire> requiredWires = bundleWiring.getRequiredWires(ExtenderNamespace.EXTENDER_NAMESPACE);

		for (BundleWire wire : requiredWires) {
			Map<String, Object> attributes = wire.getCapability().getAttributes();
			String extender = (String)attributes.get(ExtenderNamespace.EXTENDER_NAMESPACE);

			if (Constants.CDI_EXTENDER.equals(extender)) {
				return wire.getProvider().getBundle();
			}
		}

		return null;
	}

}
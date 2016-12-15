package com.liferay.cdi.test.cases;

import org.osgi.framework.Bundle;
import org.osgi.service.cdi.CdiContainer;
import org.osgi.util.tracker.ServiceTracker;

public class CdiExtenderTests extends AbstractTestCase {

	@Override
	protected void setUp() throws Exception {
		cdiBundle = bundleContext.installBundle(null , getBundle("basic-beans.jar"));
		cdiBundle.start();
	}

	@Override
	protected void tearDown() throws Exception {
		cdiBundle.uninstall();
	}

	public void testStopExtender() throws Exception {
		Bundle cdiExtenderBundle = getCdiExtenderBundle();

		ServiceTracker<CdiContainer,CdiContainer> serviceTracker = getServiceTracker(cdiBundle.getBundleId());

		try {
			assertNotNull(serviceTracker.waitForService(timeout));

			cdiExtenderBundle.stop();

			assertTrue(serviceTracker.isEmpty());

			cdiExtenderBundle.start();

			assertNotNull(serviceTracker.waitForService(timeout));
		}
		finally {
			serviceTracker.close();
		}
	}

}
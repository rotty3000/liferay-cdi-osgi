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

import java.util.Dictionary;
import java.util.Hashtable;

import javax.naming.spi.ObjectFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cdi.CdiContainer;
import org.osgi.service.jndi.JNDIConstants;
import org.osgi.util.tracker.BundleTracker;

/**
 * @author Neil Griffin
 * @author Raymond Augé
 */
public class Activator implements BundleActivator {

	@Override
	@SuppressWarnings("rawtypes")
	public void start(BundleContext bundleContext) throws Exception {
		Dictionary<String, Object> properties = new Hashtable<>();

		// TODO For now!
		properties.put(Constants.SERVICE_VENDOR, "Liferay, Inc.");
		properties.put(JNDIConstants.JNDI_URLSCHEME, "java");

		_serviceRegistration = bundleContext.registerService(
			new String[] {CdiContainer.class.getName(), ObjectFactory.class.getName()}, 
			new CdiContainerServiceFactory(), properties);

		_bundleTracker = new BundleTracker<CdiContainer>(bundleContext, Bundle.ACTIVE, new CdiBundleCustomizer());

		_bundleTracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_bundleTracker.close();

		_serviceRegistration.unregister();
	}

	private BundleTracker<CdiContainer> _bundleTracker;

	@SuppressWarnings("rawtypes")
	private ServiceRegistration _serviceRegistration;
	
}

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

import com.liferay.cdi.osgi.spi.WabCdiContainer;
import org.apache.felix.utils.log.Logger;
import org.osgi.framework.Bundle;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.liferay.cdi.osgi.spi.WabCdiContainerService;


/**
 * @author  Neil Griffin
 */
@Component(immediate = true, service = WabCdiContainerService.class)
public class WeldWabCdiContainerService implements WabCdiContainerService {

	private Logger logger;

	@Activate
	public void activate(BundleContext bundleContext) {
		logger = new Logger(bundleContext);
	}

	@Override
	public WabCdiContainer getWabCdiContainer(Bundle bundle) {
		return new WeldWabCdiContainer(bundle, logger);
	}
}

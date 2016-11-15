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
package com.liferay.cdi.portable.extension.internal;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.osgi.service.component.annotations.Component;

/**
 * @author  Neil Griffin
 */
@ApplicationScoped
@Component
public class CDIExtension implements Extension {

	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
		System.err.println("finished the scanning process");
	}

	void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
		System.err.println("beginning the scanning process");
	}

	<T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
		System.err.println("scanning type: " + pat.getAnnotatedType().getJavaClass().getName());
	}
}

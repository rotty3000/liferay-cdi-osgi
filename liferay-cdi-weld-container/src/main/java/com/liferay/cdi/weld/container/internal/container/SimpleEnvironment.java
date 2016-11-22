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
package com.liferay.cdi.weld.container.internal.container;

import java.util.HashSet;
import java.util.Set;

import org.jboss.weld.bootstrap.api.Environment;
import org.jboss.weld.bootstrap.api.Service;

/**
 * @author  Neil Griffin
 */
public class SimpleEnvironment implements Environment {

	private Set<Class<? extends Service>> requiredBeanDeploymentArchiveServices;
	private Set<Class<? extends Service>> requiredDeploymentServices;

	public SimpleEnvironment() {
		this.requiredBeanDeploymentArchiveServices = new HashSet<Class<? extends Service>>();
		this.requiredDeploymentServices = new HashSet<Class<? extends Service>>();
	}

	@Override
	public Set<Class<? extends Service>> getRequiredBeanDeploymentArchiveServices() {
		return requiredBeanDeploymentArchiveServices;
	}

	@Override
	public Set<Class<? extends Service>> getRequiredDeploymentServices() {
		return requiredDeploymentServices;
	}
}
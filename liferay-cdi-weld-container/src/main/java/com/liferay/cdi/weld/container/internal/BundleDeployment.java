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

import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;


/**
 * @author  Neil Griffin
 */
public class BundleDeployment implements Deployment {

	private BeanDeploymentArchive beanDeploymentArchive;
	private Collection<BeanDeploymentArchive> beanDeploymentArchives;
	private Iterable<Metadata<Extension>> extensions;

	public BundleDeployment(Iterable<Metadata<Extension>> extensions, BeanDeploymentArchive beanDeploymentArchive) {
		this.beanDeploymentArchives = new ArrayList<BeanDeploymentArchive>();
		this.beanDeploymentArchives.add(beanDeploymentArchive);
		this.extensions = extensions;
		this.beanDeploymentArchive = beanDeploymentArchive;
	}

	@Override
	public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
		return beanDeploymentArchives;
	}

	@Override
	public Iterable<Metadata<Extension>> getExtensions() {
		return extensions;
	}

	@Override
	public ServiceRegistry getServices() {
		return beanDeploymentArchive.getServices();
	}

	@Override
	public BeanDeploymentArchive loadBeanDeploymentArchive(Class<?> aClass) {
		return beanDeploymentArchive;
	}

}

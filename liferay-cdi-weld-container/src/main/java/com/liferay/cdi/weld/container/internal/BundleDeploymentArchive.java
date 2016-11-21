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

import java.util.Collection;
import java.util.Collections;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.serialization.spi.ProxyServices;
import org.osgi.framework.wiring.BundleWiring;


/**
 * @author  Neil Griffin
 */
public class BundleDeploymentArchive implements BeanDeploymentArchive {

	private Collection<String> beanClasses;
	private Collection<BeanDeploymentArchive> beanDeploymentArchives;
	private BeansXml beansXml;
	private Collection<EjbDescriptor<?>> ejbs;
	private String id;
	private ServiceRegistry services;

	public BundleDeploymentArchive(
		BundleWiring bundleWiring, String id, Collection<String> beanClasses, BeansXml beansXml, CdiHelper cdiHelper) {

		this.id = id;
		this.beanClasses = beanClasses;
		this.beanDeploymentArchives = Collections.emptyList();
		this.beansXml = beansXml;
		this.ejbs = Collections.emptyList();
		this.services = new SimpleServiceRegistry();

		BundleResourcesLoader loader = new BundleResourcesLoader(bundleWiring, cdiHelper.getExtenderBundle());

		services.add(ResourceLoader.class, loader);
		services.add(ProxyServices.class, loader);
	}

	@Override
	public Collection<String> getBeanClasses() {
		return beanClasses;
	}

	@Override
	public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
		return beanDeploymentArchives;
	}

	@Override
	public BeansXml getBeansXml() {
		return beansXml;
	}

	@Override
	public Collection<EjbDescriptor<?>> getEjbs() {
		return ejbs;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ServiceRegistry getServices() {
		return services;
	}
}

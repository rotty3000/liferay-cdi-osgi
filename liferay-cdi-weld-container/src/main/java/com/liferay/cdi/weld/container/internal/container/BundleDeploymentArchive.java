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

package com.liferay.cdi.weld.container.internal.container;

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

import com.liferay.cdi.weld.container.internal.CdiHelper;
import com.liferay.cdi.weld.container.internal.loader.BundleResourcesLoader;

public class BundleDeploymentArchive implements BeanDeploymentArchive {

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

	private Collection<String> beanClasses;
	private Collection<BeanDeploymentArchive> beanDeploymentArchives;
	private BeansXml beansXml;
	private Collection<EjbDescriptor<?>> ejbs;
	private String id;
	private ServiceRegistry services;

}
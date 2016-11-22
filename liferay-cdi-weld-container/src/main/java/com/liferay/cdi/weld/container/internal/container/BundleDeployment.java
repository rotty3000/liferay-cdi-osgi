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

import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;

public class BundleDeployment implements Deployment {

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

	private BeanDeploymentArchive beanDeploymentArchive;
	private Collection<BeanDeploymentArchive> beanDeploymentArchives;
	private Iterable<Metadata<Extension>> extensions;

}
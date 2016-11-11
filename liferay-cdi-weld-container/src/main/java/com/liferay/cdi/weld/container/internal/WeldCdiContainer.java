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
import java.util.Hashtable;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.Environment;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.cdi.CdiContainer;

import com.liferay.cdi.weld.container.internal.ctx.JNDIContext;

/**
 * @author  Neil Griffin
 */
public class WeldCdiContainer implements CdiContainer, ObjectFactory {

	private BeanManager beanManager;
	private Bootstrap bootstrap;
	private Context context;

	public WeldCdiContainer(Bundle bundle) {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		String deploymentArchiveId = bundle.getSymbolicName() + ":" + bundle.getBundleId();

		bootstrap = new WeldBootstrap();
		Iterable<Metadata<Extension>> extensions = bootstrap.loadExtensions(bundleWiring.getClassLoader());
		ScanResults scanResults = ScannerUtil.scan(bundleWiring);
		Collection<String> beanClasses = scanResults.getBeanClasses();
		BeansXml beansXml = bootstrap.parse(scanResults.getBeanDescriptorURLs());
		BeanDeploymentArchive beanDeploymentArchive = new BundleDeploymentArchive(
			bundleWiring, deploymentArchiveId, beanClasses, beansXml);
		Environment environment = new SimpleEnvironment();
		Deployment deployment = new BundleDeployment(extensions, beanDeploymentArchive);
		bootstrap.startContainer(environment, deployment);
		bootstrap.startInitialization();
		bootstrap.deployBeans();
		bootstrap.validateBeans();
		bootstrap.endInitialization();

		beanManager = bootstrap.getManager(beanDeploymentArchive);
		
		context = new JNDIContext(this);
	}

	@Override
	public BeanManager getBeanManager() {
		return beanManager;
	}

	@Override
	public Object getObjectInstance(Object obj, Name name, Context context, Hashtable<?, ?> environment)
		throws Exception {
		
		if (obj == null) {
			return this.context;
		}

		System.out.println("[JNDI] obj: " + obj);
		System.out.println("[JNDI] name: " + name);
		System.out.println("[JNDI] context: " + context);
		System.out.println("[JNDI] env: " + environment);

		return beanManager;
	}

	public void shutdown() {
		bootstrap.shutdown();
	}

}
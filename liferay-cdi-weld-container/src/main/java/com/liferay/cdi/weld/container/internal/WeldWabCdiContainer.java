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
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.AnnotationLiteral;
import javax.servlet.ServletContext;

import org.apache.felix.utils.log.Logger;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.Environment;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.literal.DestroyedLiteral;
import org.jboss.weld.manager.api.WeldManager;

import org.osgi.framework.Bundle;

import com.liferay.cdi.osgi.spi.WabCdiContainer;


/**
 * @author  Neil Griffin
 */
public class WeldWabCdiContainer implements WabCdiContainer {

	private BeanManager beanManager;
	private Bootstrap bootstrap;
	private Bundle bundle;
	private Logger logger;

	public WeldWabCdiContainer(Bundle bundle, Logger logger) {
		this.bundle = bundle;
		this.logger = logger;
	}

	@Override
	public BeanManager getBeanManager() {
		return beanManager;
	}

	@Override
	public void shutdown() {

		if (bootstrap != null) {
			bootstrap.shutdown();
		}
		else {
			logger.log(Logger.LOG_ERROR, "Unable to shutdown since Weld is not yet started");
		}
	}

	@Override
	public void startup(ServletContext servletContext) {

		if (bootstrap == null) {

			try {
				bootstrap = new WeldBootstrap();

				Iterable<Metadata<Extension>> extensions = bootstrap.loadExtensions(servletContext.getClassLoader());
				ScanResults scanResults = ScannerUtil.scan(bundle, logger);
				String deploymentArchiveId = bundle.getSymbolicName() + ":" + bundle.getBundleId();
				Collection<String> beanClasses = scanResults.getBeanClasses();
				BeansXml beansXml = bootstrap.parse(scanResults.getBeanDescriptorURLs());
				BeanDeploymentArchive beanDeploymentArchive = new BundleDeploymentArchive(deploymentArchiveId,
						beanClasses, beansXml);
				Environment environment = new SimpleEnvironment();
				Deployment deployment = new BundleDeployment(extensions, beanDeploymentArchive);
				bootstrap.startContainer(environment, deployment);
				bootstrap.startInitialization();
				bootstrap.deployBeans();
				bootstrap.validateBeans();
				bootstrap.endInitialization();

				beanManager = bootstrap.getManager(beanDeploymentArchive);
				printBeans(beanManager);
			}
			catch (Throwable t) {
				logger.log(Logger.LOG_ERROR, "Error starting Weld", t);
			}
		}
		else {
			logger.log(Logger.LOG_ERROR, "Unable to startup since Weld is already started");
		}
	}

	private void printBeans(BeanManager beanManager) {

		System.err.println("**** beanManager=" + beanManager);

		AnnotationLiteral<Any> annotationLiteral = new AnnotationLiteral<Any>() {
			};

		Set<Bean<?>> beans = beanManager.getBeans(Object.class, annotationLiteral);

		for (Bean<?> bean : beans) {
			System.err.println("     " + bean.getBeanClass().getName());
		}
	}
}

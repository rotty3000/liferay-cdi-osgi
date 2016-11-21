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

import java.util.Collection;

import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.xml.BeansXmlParser;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.liferay.cdi.weld.container.internal.BundleDeploymentArchive;
import com.liferay.cdi.weld.container.internal.CdiHelper;
import com.liferay.cdi.weld.container.internal.ScanResults;
import com.liferay.cdi.weld.container.internal.ScannerUtil;

public class WeldCdiContainer {

	public WeldCdiContainer(Bundle bundle, CdiHelper cdiHelper) {
		String deploymentArchiveId = bundle.getSymbolicName() + ":" + bundle.getBundleId();
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		ScanResults scanResults = ScannerUtil.scan(bundleWiring);
		Collection<String> beanClasses = scanResults.getBeanClasses();
		BeansXmlParser beansXmlParser = new BeansXmlParser();
		BeansXml beansXml = beansXmlParser.parse(scanResults.getBeanDescriptorURLs());

		BeanDeploymentArchive beanDeploymentArchive = new BundleDeploymentArchive(
			bundleWiring, deploymentArchiveId, beanClasses, beansXml, cdiHelper);

		_extensionPhase = new ExtensionPhase(bundle, cdiHelper, beanDeploymentArchive);
	}

	public void close() {
		_extensionPhase.close();
	}

	public void open() {
		_extensionPhase.open();
	}

	private ExtensionPhase _extensionPhase;

}
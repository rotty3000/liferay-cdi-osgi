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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author  Neil Griffin
 */
public class ScannerUtil {

	private static final String[] BEAN_DESCRIPTOR_PATHS = new String[] {"META-INF/beans.xml", "WEB-INF/beans.xml"};

	public static ScanResults scan(BundleWiring bundleWiring) {
		List<URL> beanDescriptorURLs = new ArrayList<URL>();

		for (String descriptorPath : BEAN_DESCRIPTOR_PATHS) {
			URL beanDescriptorURL = bundleWiring.getBundle().getResource(descriptorPath);

			if (beanDescriptorURL != null) {
				beanDescriptorURLs.add(beanDescriptorURL);
			}
		}

		List<String> beanClasses = new ArrayList<String>();

		Collection<String> resources = bundleWiring.listResources(
			"/", "*.class", BundleWiring.LISTRESOURCES_LOCAL | BundleWiring.LISTRESOURCES_RECURSE);

		if (resources != null) {
			for (String resource : resources) {
				resource = resource.replace('/', '.');
				resource = resource.replace(".class", "");
	
				beanClasses.add(resource);
			}
		}

		return new ScanResults(beanClasses, beanDescriptorURLs);
	}

}

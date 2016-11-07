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
import java.util.Enumeration;
import java.util.List;

import org.apache.felix.utils.log.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;


/**
 * @author  Neil Griffin
 */
public class ScannerUtil {

	private static final String[] BEAN_DESCRIPTOR_PATHS = new String[] { "META-INF/beans.xml", "WEB-INF/beans.xml" };

	public static ScanResults scan(Bundle bundle, Logger logger) {

		List<URL> beanDescriptorURLs = new ArrayList<URL>();

		for (String descriptorPath : BEAN_DESCRIPTOR_PATHS) {

			URL beanDescriptorURL = bundle.getEntry(descriptorPath);

			if (beanDescriptorURL != null) {
				beanDescriptorURLs.add(beanDescriptorURL);
				logger.log(Logger.LOG_DEBUG, "Found beanDescriptorURL=" + beanDescriptorURL);
			}
		}

		List<String> beanClasses = new ArrayList<String>();
		String[] classPaths = new String[] { "/" };
		String bundleClassPath = bundle.getHeaders().get(Constants.BUNDLE_CLASSPATH);

		if (bundleClassPath != null) {
			classPaths = bundleClassPath.split(",");
		}

		for (String classPath : classPaths) {

			if (classPath.endsWith(".jar")) {
				logger.log(Logger.LOG_DEBUG, "Skipping scanning classpath of jar=" + classPath);
			}
			else {
				Enumeration<URL> classPathURLs = bundle.findEntries(classPath, "*.class", true);

				while ((classPathURLs != null) && classPathURLs.hasMoreElements()) {
					URL classPathURL = classPathURLs.nextElement();
					String file = classPathURL.getFile();
					int pos = file.indexOf(classPath);
					String fqcn = file.substring(pos + classPath.length());

					if (fqcn.startsWith("/")) {
						fqcn = fqcn.substring(1);
					}

					fqcn = fqcn.replace('/', '.');
					fqcn = fqcn.replace(".class", "");
					logger.log(Logger.LOG_DEBUG, "Found fqcn=" + fqcn);
					beanClasses.add(fqcn);
				}
			}
		}

		return new ScanResults(beanClasses, beanDescriptorURLs);
	}
}

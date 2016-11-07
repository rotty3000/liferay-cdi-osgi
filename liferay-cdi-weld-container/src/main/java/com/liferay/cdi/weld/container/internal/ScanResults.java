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
import java.util.Collection;


/**
 * @author  Neil Griffin
 */
public class ScanResults {

	private Collection<String> beanClasses;
	private Collection<URL> beanDescriptorURLs;

	public ScanResults(Collection<String> beanClasses, Collection<URL> beanDescriptorURLs) {
		this.beanClasses = beanClasses;
		this.beanDescriptorURLs = beanDescriptorURLs;
	}

	public Collection<String> getBeanClasses() {
		return beanClasses;
	}

	public Collection<URL> getBeanDescriptorURLs() {
		return beanDescriptorURLs;
	}
}

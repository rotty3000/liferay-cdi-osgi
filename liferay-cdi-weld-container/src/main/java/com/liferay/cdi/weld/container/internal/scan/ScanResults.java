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

package com.liferay.cdi.weld.container.internal.scan;

import java.net.URL;
import java.util.Collection;

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

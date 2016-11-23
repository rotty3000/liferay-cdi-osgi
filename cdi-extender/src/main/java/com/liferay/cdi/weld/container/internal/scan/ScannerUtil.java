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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.cdi.Constants;

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

		List<BundleRequirement> requirements = bundleWiring.getRequirements(Constants.CDI_EXTENDER);

		for (BundleRequirement requirement : requirements) {
			Map<String, Object> attributes = requirement.getAttributes();
			if (attributes.containsKey("beans")) {
				Object object = attributes.get("beans");
				if (object instanceof List) {
					beanClasses.addAll((List<String>)attributes.get("beans"));
				}
			}
		}

		if (beanClasses.isEmpty()) {
			Collection<String> resources = bundleWiring.listResources(
				"/", "*.class", BundleWiring.LISTRESOURCES_LOCAL | BundleWiring.LISTRESOURCES_RECURSE);

			if (resources != null) {
				for (String resource : resources) {
					resource = resource.replace('/', '.');
					resource = resource.replace(".class", "");

					beanClasses.add(resource);
				}
			}
		}

		return new ScanResults(beanClasses, beanDescriptorURLs);
	}

}

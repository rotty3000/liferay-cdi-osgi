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

import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.xml.BeansXmlParser;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.liferay.cdi.weld.container.internal.CdiContainerState;
import com.liferay.cdi.weld.container.internal.scan.ScanResults;
import com.liferay.cdi.weld.container.internal.scan.ScannerUtil;

public class Phase_1_Init {

	public Phase_1_Init(Bundle bundle, CdiContainerState cdiContainerState) {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		ScanResults scanResults = ScannerUtil.scan(bundleWiring);
		Collection<String> beanClasses = scanResults.getBeanClasses();
		BeansXmlParser beansXmlParser = new BeansXmlParser();
		BeansXml beansXml = beansXmlParser.parse(scanResults.getBeanDescriptorURLs());

		_phase2 = new Phase_2_Extension(bundle, cdiContainerState, beanClasses, beansXml);
	}

	public void close() {
		_phase2.close();
	}

	public void open() {
		_phase2.open();
	}

	private final Phase_2_Extension _phase2;

}